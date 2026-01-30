package com.techducat.ajo.service.fee

import android.content.Context
import com.techducat.ajo.R
import com.techducat.ajo.dlt.MoneroBlockchainProvider
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.ServiceFeeEntity
import com.techducat.ajo.util.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID
import org.json.JSONObject
import org.json.JSONArray
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class ServiceFeeManager(
    private val context: Context,
    private val db: AjoDatabase,
    private val monero: MoneroBlockchainProvider
) {
    
    companion object {
        private const val TAG = "com.techducat.ajo.service.fee.ServiceFeeManager"
        
        // Fee Configuration
        const val DEFAULT_FEE_PERCENTAGE = 0.015 // 1.5%
        const val MIN_FEE_PERCENTAGE = 0.01 // 1%
        const val MAX_FEE_PERCENTAGE = 0.05 // 5%
        const val MIN_FEE_AMOUNT_ATOMIC = 1000000L // 0.000001 XMR minimum
        
        // Backend API Configuration
        private val BACKEND_API_URL = com.techducat.ajo.BuildConfig.SERVICE_BACKEND_URL
        private val API_KEY = com.techducat.ajo.BuildConfig.SERVICE_API_KEY
        
        // Cache duration for service wallet address (5 minutes)
        const val ADDRESS_CACHE_DURATION_MS = 300000L
        
        // Maximum number of retries
        private const val MAX_FETCH_RETRIES = 3
    }
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val prefs = context.getSharedPreferences("service_fee_prefs", Context.MODE_PRIVATE)
    
    // Fee events flow
    private val _feeEvents = MutableSharedFlow<FeeEvent>(replay = 10)
    val feeEvents: SharedFlow<FeeEvent> = _feeEvents.asSharedFlow()
    
    // Cached service wallet with expiration
    private var cachedServiceWallet: CachedWallet? = null
    private val cacheLock = Any()
    
    // Public key loaded from raw resource
    private lateinit var publicKeyPem: String
    
    // OkHttp client with API key authentication
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(ApiKeyInterceptor())
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-App-Version", getAppVersion())
                    .addHeader("X-Device-Id", getDeviceId())
                    .build()
                chain.proceed(request)
            }
            .build()
    }
    
    // Fee statistics
    private var totalFeesCollected = 0L
    private var totalTransactionsProcessed = 0
    private var failedFeeTransactions = 0
    
    init {
        // Load public key from raw resource
        publicKeyPem = loadPublicKeyFromResource()
        Logger.i("$TAG: Public key loaded from res/raw/service_wallet_public_key.pem")
        
        loadStatistics()
        Logger.i("$TAG: Initialized with secure service wallet management")
        Logger.i("$TAG: Backend URL: $BACKEND_API_URL")
    }
    
    // ============================================================
    // API KEY AUTHENTICATION
    // ============================================================
    
    /**
     * OkHttp interceptor to add API key to all requests
     */
    inner class ApiKeyInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            
            val newRequest = originalRequest.newBuilder()
                .header("X-API-Key", API_KEY)
                .build()
            
            return chain.proceed(newRequest)
        }
    }
    
    // ============================================================
    // Load Public Key from Resource
    // ============================================================
    
    private fun loadPublicKeyFromResource(): String {
        return try {
            context.resources.openRawResource(R.raw.service_wallet_public_key)
                .bufferedReader()
                .use { it.readText() }
        } catch (e: Exception) {
            Logger.e("$TAG: Failed to load public key from resource", e)
            throw SecurityException("Failed to load service wallet public key", e)
        }
    }
    
    // ============================================================
    // Secure Service Wallet Management
    // ============================================================
    
    /**
     * Get verified service wallet address from backend
     * Uses cryptographic signature verification to ensure authenticity
     */
    suspend fun getVerifiedServiceWallet(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Check cache first
            synchronized(cacheLock) {
                cachedServiceWallet?.let { cached ->
                    if (!cached.isExpired()) {
                        Logger.d("$TAG: Using cached service wallet (${cached.remainingMs()}ms remaining)")
                        return@withContext Result.success(cached.address)
                    }
                }
            }
            
            // Fetch fresh address from backend
            Logger.i("$TAG: Fetching service wallet from backend")
            val response = fetchServiceWalletFromBackend()
            
            if (response.isFailure) {
                // Try to use cached address as fallback if not too old (up to 1 hour)
                synchronized(cacheLock) {
                    cachedServiceWallet?.let { cached ->
                        if (cached.age() < 3600000) { // 1 hour
                            Logger.w("$TAG: Backend fetch failed, using stale cache as fallback")
                            return@withContext Result.success(cached.address)
                        }
                    }
                }
                return@withContext Result.failure(
                    response.exceptionOrNull() ?: Exception("Failed to fetch service wallet")
                )
            }
            
            val walletResponse = response.getOrThrow()
            
            // CRITICAL: Verify signature before accepting address
            val isValid = verifyAddressSignature(
                address = walletResponse.address,
                signature = walletResponse.signature,
                timestamp = walletResponse.timestamp
            )
            
            if (!isValid) {
                Logger.e("$TAG: CRITICAL SECURITY ALERT - Address signature verification FAILED!")
                Logger.e("$TAG: Address: ${maskAddress(walletResponse.address)}")
                Logger.e("$TAG: This could indicate a MITM attack or compromised backend")
                
                reportSecurityEvent(
                    eventType = "invalid_signature",
                    details = mapOf(
                        "address" to walletResponse.address,
                        "timestamp" to walletResponse.timestamp.toString()
                    )
                )
                
                _feeEvents.emit(
                    FeeEvent.SecurityViolation(
                        message = "Service wallet signature verification failed",
                        address = walletResponse.address
                    )
                )
                
                return@withContext Result.failure(
                    SecurityException("Service wallet signature verification failed")
                )
            }
            
            Logger.i("$TAG: Service wallet signature verified successfully ✓")
            
            // Additional validation: Check address format
            if (!isValidMoneroAddress(walletResponse.address)) {
                Logger.e("$TAG: Invalid Monero address format received from backend")
                return@withContext Result.failure(
                    Exception("Invalid address format")
                )
            }
            
            // Cache the verified address
            synchronized(cacheLock) {
                cachedServiceWallet = CachedWallet(
                    address = walletResponse.address,
                    timestamp = System.currentTimeMillis(),
                    signature = walletResponse.signature
                )
            }
            
            // Store in preferences as backup
            prefs.edit()
                .putString("last_verified_wallet", walletResponse.address)
                .putLong("last_verified_timestamp", System.currentTimeMillis())
                .apply()
            
            Logger.i("$TAG: Service wallet cached: ${maskAddress(walletResponse.address)}")
            
            Result.success(walletResponse.address)
            
        } catch (e: Exception) {
            Logger.e("$TAG: Failed to get verified service wallet", e)
            Result.failure(e)
        }
    }
    
    /**
     * Fetch service wallet from backend with retry logic
     */
    private suspend fun fetchServiceWalletFromBackend(): Result<ServiceWalletResponse> {
        var lastException: Exception? = null
        
        repeat(MAX_FETCH_RETRIES) { attempt ->
            try {
                val url = "$BACKEND_API_URL/api/v1/service-wallet?appVersion=${getAppVersion()}&deviceId=${getDeviceId()}"
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()
                
                val response = httpClient.newCall(request).execute()
                
                if (!response.isSuccessful) {
                    throw Exception("Backend returned ${response.code}: ${response.message}")
                }
                
                val body = response.body?.string() ?: throw Exception("Empty response body")
                val json = JSONObject(body)
                
                val walletResponse = ServiceWalletResponse(
                    address = json.getString("walletAddress"),
                    signature = json.getString("signature"),
                    timestamp = json.getLong("timestamp"),
                    expiresIn = json.optLong("expiresIn", ADDRESS_CACHE_DURATION_MS)
                )
                
                Logger.d("$TAG: Successfully fetched service wallet from backend (attempt ${attempt + 1})")
                return Result.success(walletResponse)
                
            } catch (e: Exception) {
                lastException = e
                Logger.w("$TAG: Attempt ${attempt + 1}/$MAX_FETCH_RETRIES failed: ${e.message}")
                
                if (attempt < MAX_FETCH_RETRIES - 1) {
                    delay(1000L * (attempt + 1))
                }
            }
        }
        
        return Result.failure(lastException ?: Exception("Failed to fetch service wallet"))
    }
    
    /**
     * Verify address signature using Ed25519
     */
    private fun verifyAddressSignature(
        address: String,
        signature: String,
        timestamp: Long
    ): Boolean {
        return try {
            val message = "$address|$timestamp"
            
            // Check timestamp freshness (prevent replay attacks)
            val currentTime = System.currentTimeMillis()
            val timeDiff = Math.abs(currentTime - timestamp)
            
            if (timeDiff > 300000) { // 5 minutes
                Logger.e("$TAG: Timestamp too old or in future: ${timeDiff}ms difference")
                return false
            }
            
            // Parse public key from loaded PEM
            val publicKeyBytes = parsePublicKey(publicKeyPem)
            val keySpec = X509EncodedKeySpec(publicKeyBytes)
            val keyFactory = KeyFactory.getInstance("Ed25519")
            val publicKey = keyFactory.generatePublic(keySpec)
            
            // Verify signature
            val signatureBytes = Base64.getDecoder().decode(signature)
            val sig = Signature.getInstance("Ed25519")
            sig.initVerify(publicKey)
            sig.update(message.toByteArray(Charsets.UTF_8))
            
            val isValid = sig.verify(signatureBytes)
            
            if (isValid) {
                Logger.d("$TAG: ✓ Signature verification PASSED")
            } else {
                Logger.e("$TAG: ✗ Signature verification FAILED")
            }
            
            isValid
            
        } catch (e: Exception) {
            Logger.e("$TAG: Signature verification error", e)
            false
        }
    }
    
    /**
     * Parse PEM-formatted public key
     */
    private fun parsePublicKey(pemKey: String): ByteArray {
        val key = pemKey
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")
        
        return Base64.getDecoder().decode(key)
    }
    
    /**
     * Report security events to backend for monitoring
     */
    private fun reportSecurityEvent(eventType: String, details: Map<String, String>) {
        scope.launch {
            try {
                val json = JSONObject().apply {
                    put("eventType", eventType)
                    put("timestamp", System.currentTimeMillis())
                    put("appVersion", getAppVersion())
                    put("deviceId", getDeviceId())
                    
                    val detailsJson = JSONObject()
                    details.forEach { (k, v) -> detailsJson.put(k, v) }
                    put("details", detailsJson)
                }
                
                val mediaType = "application/json".toMediaType()
                val requestBody = json.toString().toRequestBody(mediaType)
                
                val request = Request.Builder()
                    .url("$BACKEND_API_URL/api/v1/security-events")
                    .post(requestBody)
                    .build()
                
                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        Logger.d("$TAG: Security event reported: $eventType")
                    } else {
                        Logger.w("$TAG: Failed to report security event: ${response.code}")
                    }
                }
                
            } catch (e: Exception) {
                Logger.e("$TAG: Failed to report security event", e)
            }
        }
    }
    
    // ============================================================
    // Fee Calculation
    // ============================================================
    
    fun calculateFee(
        grossAmount: BigDecimal,
        feePercentage: Double = DEFAULT_FEE_PERCENTAGE,
        customMinFee: Long? = null
    ): FeeCalculation {
        val validatedPercentage = when {
            feePercentage < MIN_FEE_PERCENTAGE -> MIN_FEE_PERCENTAGE
            feePercentage > MAX_FEE_PERCENTAGE -> MAX_FEE_PERCENTAGE
            else -> feePercentage
        }
        
        var fee = grossAmount.multiply(BigDecimal.valueOf(validatedPercentage))
            .setScale(12, RoundingMode.DOWN)
        
        val minFee = BigDecimal.valueOf((customMinFee ?: MIN_FEE_AMOUNT_ATOMIC).toDouble() / 1e12)
        if (fee < minFee) {
            fee = minFee
        }
        
        val netAmount = grossAmount.subtract(fee)
        
        Logger.d("$TAG: Fee calculated - Gross: $grossAmount XMR, Fee: $fee XMR (${validatedPercentage * 100}%), Net: $netAmount XMR")
        
        return FeeCalculation(
            grossAmount = grossAmount,
            feeAmount = fee,
            netAmount = netAmount,
            feePercentage = validatedPercentage,
            minFeeApplied = fee == minFee
        )
    }
    
    fun calculateFeeAtomic(
        grossAmountAtomic: Long,
        feePercentage: Double = DEFAULT_FEE_PERCENTAGE
    ): FeeCalculationAtomic {
        val grossAmount = BigDecimal.valueOf(grossAmountAtomic.toDouble() / 1e12)
        val feeCalc = calculateFee(grossAmount, feePercentage)
        
        return FeeCalculationAtomic(
            grossAmountAtomic = grossAmountAtomic,
            feeAmountAtomic = (feeCalc.feeAmount.toDouble() * 1e12).toLong(),
            netAmountAtomic = (feeCalc.netAmount.toDouble() * 1e12).toLong(),
            feePercentage = feeCalc.feePercentage
        )
    }
    
    // ============================================================
    // Process Distribution with Fee
    // ============================================================
    
    suspend fun processDistributionWithFee(
        roscaId: String,
        recipientAddress: String,
        amount: BigDecimal,
        distributionId: String = UUID.randomUUID().toString()
    ): Result<DistributionResult> = withContext(Dispatchers.IO) {
        try {
            Logger.i("$TAG: Processing distribution $distributionId for $amount XMR")
            
            // Get verified service wallet
            val serviceWalletResult = getVerifiedServiceWallet()
            if (serviceWalletResult.isFailure) {
                Logger.e("$TAG: Failed to get service wallet")
                return@withContext Result.failure(
                    serviceWalletResult.exceptionOrNull() ?: Exception("Failed to get service wallet")
                )
            }
            val serviceWallet = serviceWalletResult.getOrThrow()
            
            // Calculate fee
            val calculation = calculateFee(amount)
            Logger.i("$TAG: Distribution breakdown - Net: ${calculation.netAmount} XMR, Fee: ${calculation.feeAmount} XMR")
            
            // Create fee record
            val feeRecord = createFeeRecord(
                distributionId = distributionId,
                roscaId = roscaId,
                calculation = calculation,
                serviceWallet = serviceWallet
            )
            
            // Send to recipient (net amount)
            Logger.d("$TAG: Sending ${calculation.netAmount} XMR to recipient $recipientAddress")
            val recipientTxResult = monero.sendTransaction(
                recipientAddress = recipientAddress,
                amount = calculation.netAmount.toDouble()
            )

            if (recipientTxResult.isFailure) {
                Logger.e("$TAG: Failed to send to recipient: ${recipientTxResult.exceptionOrNull()?.message}")
                updateFeeRecordStatus(feeRecord.id, "failed")
                return@withContext Result.failure(
                    recipientTxResult.exceptionOrNull() ?: Exception("Failed to send to recipient")
                )
            }

            val recipientTxHash = recipientTxResult.getOrThrow()
            
            Logger.i("$TAG: ✓ Sent to recipient: $recipientTxHash")
            
            // Send fee to service wallet
            Logger.d("$TAG: Sending ${calculation.feeAmount} XMR fee to service wallet")
            val feeTxHash = try {
                val feeTxResult = monero.sendTransaction(
                    recipientAddress = serviceWallet,
                    amount = calculation.feeAmount.toDouble()
                )
                
                if (feeTxResult.isSuccess) {
                    feeTxResult.getOrNull()
                } else {
                    Logger.e("$TAG: Fee transaction failed: ${feeTxResult.exceptionOrNull()?.message}")
                    failedFeeTransactions++
                    saveStatistics()
                    null
                }
            } catch (e: Exception) {
                Logger.e("$TAG: Fee transaction failed", e)
                failedFeeTransactions++
                saveStatistics()
                null
            }
            
            if (feeTxHash != null) {
                Logger.i("$TAG: ✓ Fee sent to service: $feeTxHash")
                totalFeesCollected += (calculation.feeAmount.toDouble() * 1e12).toLong()
                
                _feeEvents.emit(
                    FeeEvent.FeeCollected(
                        distributionId = distributionId,
                        feeAmount = calculation.feeAmount,
                        serviceWallet = serviceWallet,
                        txHash = feeTxHash
                    )
                )
            } else {
                Logger.w("$TAG: ⚠ Fee transaction failed, but recipient payment succeeded")
                _feeEvents.emit(
                    FeeEvent.FeeCollectionFailed(
                        distributionId = distributionId,
                        errorMessage = "Fee transaction failed"
                    )
                )
            }
            
            // Update fee record
            updateFeeRecord(feeRecord.id, recipientTxHash, feeTxHash)
            
            totalTransactionsProcessed++
            saveStatistics()
            
            val result = DistributionResult(
                distributionId = distributionId,
                recipientTxHash = recipientTxHash,
                feeTxHash = feeTxHash,
                calculation = calculation,
                serviceWallet = serviceWallet,
                feeRecordId = feeRecord.id,
                status = if (feeTxHash != null) "completed" else "partial"
            )
            
            Logger.i("$TAG: ✓ Distribution completed successfully")
            Result.success(result)
            
        } catch (e: Exception) {
            Logger.e("$TAG: Distribution failed", e)
            Result.failure(e)
        }
    }
    
    // ============================================================
    // Fee Record Management
    // ============================================================
    private suspend fun createFeeRecord(
        distributionId: String,
        roscaId: String,
        calculation: FeeCalculation,
        serviceWallet: String
    ): ServiceFeeEntity {
        val feeEntity = ServiceFeeEntity(
            id = UUID.randomUUID().toString(),
            distributionId = distributionId,
            roscaId = roscaId,
            grossAmount = (calculation.grossAmount.toDouble() * 1e12).toLong(),  // Convert to atomic units
            feeAmount = (calculation.feeAmount.toDouble() * 1e12).toLong(),      // Convert to atomic units
            netAmount = (calculation.netAmount.toDouble() * 1e12).toLong(),      // Convert to atomic units
            feePercentage = calculation.feePercentage,
            serviceWallet = serviceWallet,
            recipientTxHash = null,
            feeTxHash = null,
            status = "pending",
            errorMessage = null,  // Add this field
            createdAt = System.currentTimeMillis(),
            completedAt = null
        )
        
        db.serviceFeeDao().insert(feeEntity)
        Logger.d("$TAG: Fee record created: ${feeEntity.id}")
        
        return feeEntity
    }
    
    private suspend fun updateFeeRecord(
        feeRecordId: String,
        recipientTxHash: String,
        feeTxHash: String?
    ) {
        val status = if (feeTxHash != null) "completed" else "partial"
        val feeEntity = db.serviceFeeDao().getById(feeRecordId)
        
        if (feeEntity != null) {
            feeEntity.recipientTxHash = recipientTxHash
            feeEntity.feeTxHash = feeTxHash
            feeEntity.status = status
            feeEntity.completedAt = System.currentTimeMillis()
            
            db.serviceFeeDao().update(feeEntity)
            Logger.d("$TAG: Fee record updated: $feeRecordId - $status")
        }
    }
    
    private suspend fun updateFeeRecordStatus(feeRecordId: String, status: String) {
        val feeEntity = db.serviceFeeDao().getById(feeRecordId)
        
        if (feeEntity != null) {
            feeEntity.status = status
            db.serviceFeeDao().update(feeEntity)
        }
    }
    
    // ============================================================
    // Statistics and Monitoring
    // ============================================================
    
    fun getStatistics(): FeeStatistics {
        return FeeStatistics(
            totalFeesCollected = totalFeesCollected,
            totalTransactionsProcessed = totalTransactionsProcessed,
            failedTransactions = failedFeeTransactions,
            pendingTransactions = 0, // TODO: Query from database
            averageFeeAmount = if (totalTransactionsProcessed > 0) {
                totalFeesCollected / totalTransactionsProcessed
            } else 0L
        )
    }
    
    suspend fun getFeeHistory(roscaId: String): List<ServiceFeeEntity> {
        return db.serviceFeeDao().getFeeRecordsByRosca(roscaId)
    }
    
    suspend fun getAllFeeRecords(): List<ServiceFeeEntity> {
        return db.serviceFeeDao().getAllFeeRecords()
    }
    
    // ============================================================
    // Utility Functions
    // ============================================================
    
    private fun isValidMoneroAddress(address: String): Boolean {
        return address.length in 95..106 && (address.startsWith("4") || address.startsWith("8"))
    }
    
    private fun maskAddress(address: String): String {
        return if (address.length > 10) {
            "${address.substring(0, 6)}...${address.substring(address.length - 4)}"
        } else {
            address
        }
    }
    
    private fun getAppVersion(): String {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }
    
    private fun getDeviceId(): String {
        return prefs.getString("device_id", null) ?: run {
            val id = UUID.randomUUID().toString()
            prefs.edit().putString("device_id", id).apply()
            id
        }
    }
    
    private fun loadStatistics() {
        totalFeesCollected = prefs.getLong("total_fees_collected", 0L)
        totalTransactionsProcessed = prefs.getInt("total_transactions_processed", 0)
        failedFeeTransactions = prefs.getInt("failed_fee_transactions", 0)
    }
    
    private fun saveStatistics() {
        prefs.edit().apply {
            putLong("total_fees_collected", totalFeesCollected)
            putInt("total_transactions_processed", totalTransactionsProcessed)
            putInt("failed_fee_transactions", failedFeeTransactions)
            apply()
        }
    }
    
    fun cleanup() {
        scope.cancel()
    }
}

// ============================================================
// Data Classes
// ============================================================

data class CachedWallet(
    val address: String,
    val timestamp: Long,
    val signature: String
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() - timestamp > ServiceFeeManager.ADDRESS_CACHE_DURATION_MS
    }
    
    fun age(): Long = System.currentTimeMillis() - timestamp
    fun remainingMs(): Long = ServiceFeeManager.ADDRESS_CACHE_DURATION_MS - age()
}

data class ServiceWalletResponse(
    val address: String,
    val signature: String,
    val timestamp: Long,
    val expiresIn: Long
)

data class FeeCalculation(
    val grossAmount: BigDecimal,
    val feeAmount: BigDecimal,
    val netAmount: BigDecimal,
    val feePercentage: Double,
    val minFeeApplied: Boolean = false
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("gross_amount", grossAmount.toDouble())
            put("fee_amount", feeAmount.toDouble())
            put("net_amount", netAmount.toDouble())
            put("fee_percentage", feePercentage)
            put("min_fee_applied", minFeeApplied)
        }
    }
}

data class FeeCalculationAtomic(
    val grossAmountAtomic: Long,
    val feeAmountAtomic: Long,
    val netAmountAtomic: Long,
    val feePercentage: Double
)

data class DistributionResult(
    val distributionId: String,
    val recipientTxHash: String,
    val feeTxHash: String?,
    val calculation: FeeCalculation,
    val serviceWallet: String,
    val feeRecordId: String,
    val status: String
)

data class FeeStatistics(
    val totalFeesCollected: Long,
    val totalTransactionsProcessed: Int,
    val failedTransactions: Int,
    val pendingTransactions: Int,
    val averageFeeAmount: Long
)

sealed class FeeEvent {
    data class FeeCollected(
        val distributionId: String,
        val feeAmount: BigDecimal,
        val serviceWallet: String,
        val txHash: String
    ) : FeeEvent()
    
    data class FeeCollectionFailed(
        val distributionId: String,
        val errorMessage: String
    ) : FeeEvent()
    
    data class SecurityViolation(
        val message: String,
        val address: String
    ) : FeeEvent()
}

fun ServiceFeeEntity.toJson(): JSONObject {
    return JSONObject().apply {
        put("id", id)
        put("distribution_id", distributionId)
        put("rosca_id", roscaId)
        put("gross_amount", grossAmount)
        put("fee_amount", feeAmount)
        put("net_amount", netAmount)
        put("fee_percentage", feePercentage)
        put("service_wallet", serviceWallet)
        put("recipient_tx_hash", recipientTxHash)
        put("fee_tx_hash", feeTxHash)
        put("status", status)
        put("created_at", createdAt)
        put("completed_at", completedAt)
    }
}
