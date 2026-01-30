package com.techducat.ajo.ui.exchange

import android.content.Context
import com.techducat.ajo.util.SecureStorage
import com.techducat.ajo.wallet.WalletSuite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.OutputStreamWriter
import java.math.BigDecimal
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * ChangeNow API Client - Production Ready with Secure Storage
 * 
 * Provides cryptocurrency exchange functionality through ChangeNow API v2
 * Integrates with WalletSuite for seamless XMR transactions
 * 
 * Features:
 * - Secure API key storage using EncryptedSharedPreferences
 * - Currency listings and exchange rates
 * - Exchange transaction creation and monitoring
 * - Minimum/maximum amount validation
 * - Transaction status tracking
 * - Automatic wallet integration
 */
class ChangeNowApi private constructor(
    private val context: Context,
    private val walletSuite: WalletSuite,
    private val secureStorage: SecureStorage
) {
    
    companion object {
        private const val TAG = "com.techducat.ajo.ui.exchange.ChangeNowApi"
        
        // ChangeNow API Configuration
        private const val BASE_URL = "https://api.changenow.io/v2"
        
        // Timeouts
        private const val CONNECT_TIMEOUT_MS = 30000L
        private const val READ_TIMEOUT_MS = 30000L
        
        // Rate limiting
        private const val MIN_REQUEST_INTERVAL_MS = 100L
        private var lastRequestTime = 0L
        
        @Volatile
        private var instance: ChangeNowApi? = null
        
        fun getInstance(context: Context): ChangeNowApi {
            return instance ?: synchronized(this) {
                instance ?: ChangeNowApi(
                    context.applicationContext,
                    WalletSuite.getInstance(context),
                    SecureStorage(context.applicationContext)
                ).also { instance = it }
            }
        }
        
        fun resetInstance() {
            synchronized(this) {
                instance = null
            }
        }
    }
    
    /**
     * Get API key from secure storage
     * FIXED: No longer hardcoded in source
     */
    private fun getApiKey(): String {
        return secureStorage.getChangeNowApiKey()
            ?: throw IllegalStateException(
                "ChangeNow API key not configured. Please set it in app settings."
            )
    }
    
    /**
     * Check if API key is configured
     */
    fun isApiKeyConfigured(): Boolean {
        return secureStorage.getChangeNowApiKey() != null
    }
    
    /**
     * Set API key (should be called during initial setup)
     * This should only be called from settings or setup flow
     */
    fun setApiKey(apiKey: String) {
        if (apiKey.isBlank()) {
            throw IllegalArgumentException("API key cannot be empty")
        }
        secureStorage.setChangeNowApiKey(apiKey)
        Timber.tag(TAG).i("API key configured successfully")
    }
    
    // ============================================================================
    // DATA CLASSES
    // ============================================================================
    
    /**
     * Available currency for exchange
     */
    data class Currency(
        val ticker: String,
        val name: String,
        val image: String?,
        val hasExternalId: Boolean,
        val isFiat: Boolean,
        val featured: Boolean,
        val isStable: Boolean,
        val supportsFixedRate: Boolean,
        val network: String?
    )
    
    /**
     * Exchange rate information
     */
    data class ExchangeRate(
        val fromCurrency: String,
        val toCurrency: String,
        val estimatedAmount: BigDecimal,
        val transactionSpeedForecast: String?,
        val warningMessage: String?,
        val rateId: String?
    )
    
    /**
     * Exchange amount limits
     */
    data class ExchangeRange(
        val minAmount: BigDecimal,
        val maxAmount: BigDecimal?
    )
    
    /**
     * Created exchange transaction
     */
    data class ExchangeTransaction(
        val id: String,
        val fromCurrency: String,
        val toCurrency: String,
        val fromAmount: BigDecimal,
        val toAmount: BigDecimal,
        val payinAddress: String,
        val payoutAddress: String,
        val payinExtraId: String?,
        val payoutExtraId: String?,
        val refundAddress: String?,
        val status: String,
        val createdAt: Long
    )
    
    /**
     * Exchange transaction status
     */
    data class TransactionStatus(
        val id: String,
        val status: String,
        val fromAmount: BigDecimal?,
        val toAmount: BigDecimal?,
        val payinHash: String?,
        val payoutHash: String?,
        val expectedReceiveAmount: BigDecimal?,
        val expectedSendAmount: BigDecimal?,
        val updatedAt: Long
    )
    
    // ============================================================================
    // PUBLIC API METHODS
    // ============================================================================
    
    /**
     * Get list of available currencies
     */
    suspend fun getAvailableCurrencies(
        active: Boolean = true,
        fixedRate: Boolean = false
    ): Result<List<Currency>> = withContext(Dispatchers.IO) {
        try {
            // Check API key is configured
            if (!isApiKeyConfigured()) {
                return@withContext Result.failure(
                    IllegalStateException("ChangeNow API key not configured")
                )
            }
            
            Timber.tag(TAG).d("Fetching available currencies")
            
            val params = buildString {
                append("?active=$active")
                if (fixedRate) append("&fixedRate=true")
            }
            
            val response = executeRequest(
                endpoint = "/exchange/currencies$params",
                method = "GET"
            )
            
            val currencies = parseCurrenciesResponse(response)
            Timber.tag(TAG).i("Retrieved ${currencies.size} currencies")
            
            Result.success(currencies)
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error fetching currencies")
            Result.failure(e)
        }
    }
    
    /**
     * Get estimated exchange amount
     */
    suspend fun getEstimatedExchangeAmount(
        fromCurrency: String,
        toCurrency: String,
        fromAmount: BigDecimal,
        fixedRate: Boolean = false
    ): Result<ExchangeRate> = withContext(Dispatchers.IO) {
        try {
            if (!isApiKeyConfigured()) {
                return@withContext Result.failure(
                    IllegalStateException("ChangeNow API key not configured")
                )
            }
            
            Timber.tag(TAG).d("Getting exchange estimate: $fromAmount $fromCurrency -> $toCurrency")
            
            val type = if (fixedRate) "fixed-rate" else "standard"
            val params = "?fromCurrency=$fromCurrency&toCurrency=$toCurrency" +
                        "&fromAmount=$fromAmount&type=$type"
            
            val response = executeRequest(
                endpoint = "/exchange/estimated-amount$params",
                method = "GET"
            )
            
            val rate = parseEstimateResponse(response, fromCurrency, toCurrency)
            Timber.tag(TAG).i("Exchange rate: ${rate.estimatedAmount} $toCurrency")
            
            Result.success(rate)
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error getting estimate")
            Result.failure(e)
        }
    }
    
    /**
     * Get minimum and maximum exchange amounts
     */
    suspend fun getExchangeRange(
        fromCurrency: String,
        toCurrency: String,
        fixedRate: Boolean = false
    ): Result<ExchangeRange> = withContext(Dispatchers.IO) {
        try {
            if (!isApiKeyConfigured()) {
                return@withContext Result.failure(
                    IllegalStateException("ChangeNow API key not configured")
                )
            }
            
            Timber.tag(TAG).d("Getting exchange range: $fromCurrency -> $toCurrency")
            
            val type = if (fixedRate) "fixed-rate" else "standard"
            val params = "?fromCurrency=$fromCurrency&toCurrency=$toCurrency&type=$type"
            
            val response = executeRequest(
                endpoint = "/exchange/range$params",
                method = "GET"
            )
            
            val range = parseRangeResponse(response)
            Timber.tag(TAG).i("Exchange range: ${range.minAmount} - ${range.maxAmount}")
            
            Result.success(range)
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error getting range")
            Result.failure(e)
        }
    }
    
    /**
     * Create exchange transaction
     * Automatically integrates with WalletSuite for XMR transactions
     */
    suspend fun createExchange(
        fromCurrency: String,
        toCurrency: String,
        fromAmount: BigDecimal,
        toAddress: String,
        refundAddress: String? = null,
        extraId: String? = null,
        fixedRate: Boolean = false,
        rateId: String? = null
    ): Result<ExchangeTransaction> = withContext(Dispatchers.IO) {
        try {
            if (!isApiKeyConfigured()) {
                return@withContext Result.failure(
                    IllegalStateException("ChangeNow API key not configured")
                )
            }
            
            Timber.tag(TAG).i("Creating exchange: $fromAmount $fromCurrency -> $toCurrency")
            
            // Validate amount is within range
            val rangeResult = getExchangeRange(fromCurrency, toCurrency, fixedRate)
            if (rangeResult.isFailure) {
                return@withContext Result.failure(
                    Exception("Failed to validate exchange range")
                )
            }
            
            val range = rangeResult.getOrThrow()
            if (fromAmount < range.minAmount) {
                return@withContext Result.failure(
                    Exception("Amount below minimum: ${range.minAmount} $fromCurrency")
                )
            }
            
            if (range.maxAmount != null && fromAmount > range.maxAmount) {
                return@withContext Result.failure(
                    Exception("Amount above maximum: ${range.maxAmount} $fromCurrency")
                )
            }
            
            // Build request body
            val requestBody = JSONObject().apply {
                put("fromCurrency", fromCurrency.lowercase())
                put("toCurrency", toCurrency.lowercase())
                put("fromAmount", fromAmount.toString())
                put("address", toAddress)
                put("flow", if (fixedRate) "fixed-rate" else "standard")
                
                refundAddress?.let { put("refundAddress", it) }
                extraId?.let { put("extraId", it) }
                rateId?.let { put("rateId", it) }
            }
            
            val response = executeRequest(
                endpoint = "/exchange",
                method = "POST",
                body = requestBody.toString()
            )
            
            val transaction = parseExchangeResponse(response)
            
            Timber.tag(TAG).i("Exchange created: ${transaction.id}")
            Timber.tag(TAG).d("Payin address: ${transaction.payinAddress}")
            
            // If exchanging FROM XMR, automatically send the transaction
            if (fromCurrency.equals("xmr", ignoreCase = true)) {
                sendXmrToExchange(transaction)
            }
            
            Result.success(transaction)
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error creating exchange")
            Result.failure(e)
        }
    }
    
    /**
     * Get exchange transaction status
     */
    suspend fun getTransactionStatus(
        transactionId: String
    ): Result<TransactionStatus> = withContext(Dispatchers.IO) {
        try {
            if (!isApiKeyConfigured()) {
                return@withContext Result.failure(
                    IllegalStateException("ChangeNow API key not configured")
                )
            }
            
            Timber.tag(TAG).d("Getting status for transaction: $transactionId")
            
            val response = executeRequest(
                endpoint = "/exchange/by-id?id=$transactionId",
                method = "GET"
            )
            
            val status = parseStatusResponse(response)
            Timber.tag(TAG).d("Transaction status: ${status.status}")
            
            Result.success(status)
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error getting transaction status")
            Result.failure(e)
        }
    }
    
    /**
     * Get exchange transaction history
     */
    suspend fun getTransactionHistory(
        limit: Int = 10,
        offset: Int = 0
    ): Result<List<TransactionStatus>> = withContext(Dispatchers.IO) {
        try {
            if (!isApiKeyConfigured()) {
                return@withContext Result.failure(
                    IllegalStateException("ChangeNow API key not configured")
                )
            }
            
            Timber.tag(TAG).d("Fetching transaction history")
            
            val response = executeRequest(
                endpoint = "/exchange/txs?limit=$limit&offset=$offset",
                method = "GET"
            )
            
            val transactions = parseHistoryResponse(response)
            Timber.tag(TAG).i("Retrieved ${transactions.size} transactions")
            
            Result.success(transactions)
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error fetching history")
            Result.failure(e)
        }
    }
    
    /**
     * Validate address format
     */
    suspend fun validateAddress(
        currency: String,
        address: String,
        extraId: String? = null
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            if (!isApiKeyConfigured()) {
                return@withContext Result.failure(
                    IllegalStateException("ChangeNow API key not configured")
                )
            }
            
            Timber.tag(TAG).d("Validating address for $currency")
            
            val params = buildString {
                append("?currency=${currency.lowercase()}")
                append("&address=$address")
                extraId?.let { append("&extraId=$it") }
            }
            
            val response = executeRequest(
                endpoint = "/validate/address$params",
                method = "GET"
            )
            
            val json = JSONObject(response)
            val isValid = json.optBoolean("result", false)
            
            Timber.tag(TAG).d("Address validation: $isValid")
            Result.success(isValid)
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error validating address")
            Result.failure(e)
        }
    }
    
    // ============================================================================
    // WALLET SUITE INTEGRATION
    // ============================================================================
    
    /**
     * Send XMR to exchange address using WalletSuite
     */
    private suspend fun sendXmrToExchange(
        transaction: ExchangeTransaction
    ) = suspendCancellableCoroutine<Unit> { continuation ->
        try {
            Timber.tag(TAG).i("Sending ${transaction.fromAmount} XMR to exchange")
            
            walletSuite.sendTransaction(
                transaction.payinAddress,
                transaction.fromAmount.toDouble(),
                object : WalletSuite.TransactionCallback {
                    override fun onSuccess(txId: String, amount: Long) {
                        Timber.tag(TAG).i("XMR sent successfully: $txId")
                        if (continuation.isActive) {
                            continuation.resume(Unit)
                        }
                    }
                    
                    override fun onError(error: String) {
                        Timber.tag(TAG).e("Failed to send XMR: $error")
                        if (continuation.isActive) {
                            continuation.resumeWithException(
                                Exception("Failed to send XMR: $error")
                            )
                        }
                    }
                }
            )
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Exception sending XMR")
            if (continuation.isActive) {
                continuation.resumeWithException(e)
            }
        }
    }
    
    /**
     * Get user's XMR address from WalletSuite
     */
    suspend fun getXmrRefundAddress(): Result<String> = suspendCancellableCoroutine { continuation ->
        try {
            walletSuite.getAddress(object : WalletSuite.AddressCallback {
                override fun onSuccess(address: String) {
                    if (continuation.isActive) {
                        continuation.resume(Result.success(address))
                    }
                }
                
                override fun onError(error: String) {
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(Exception(error)))
                    }
                }
            })
        } catch (e: Exception) {
            if (continuation.isActive) {
                continuation.resume(Result.failure(e))
            }
        }
    }
    
    /**
     * Check if user has sufficient XMR balance
     */
    suspend fun hasSufficientXmrBalance(
        requiredAmount: BigDecimal
    ): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        try {
            walletSuite.getBalance(object : WalletSuite.BalanceCallback {
                override fun onSuccess(balance: Long, unlocked: Long) {
                    val unlockedXmr = BigDecimal(unlocked).divide(
                        BigDecimal("1000000000000"),
                        12,
                        BigDecimal.ROUND_DOWN
                    )
                    val sufficient = unlockedXmr >= requiredAmount
                    
                    if (continuation.isActive) {
                        continuation.resume(Result.success(sufficient))
                    }
                }
                
                override fun onError(error: String) {
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(Exception(error)))
                    }
                }
            })
        } catch (e: Exception) {
            if (continuation.isActive) {
                continuation.resume(Result.failure(e))
            }
        }
    }
    
    // ============================================================================
    // HTTP REQUEST HANDLING
    // ============================================================================
    
    /**
     * Execute HTTP request with rate limiting and error handling
     * FIXED: API key retrieved from secure storage
     */
    private suspend fun executeRequest(
        endpoint: String,
        method: String,
        body: String? = null
    ): String = withContext(Dispatchers.IO) {
        // Rate limiting
        val now = System.currentTimeMillis()
        val timeSinceLastRequest = now - lastRequestTime
        if (timeSinceLastRequest < MIN_REQUEST_INTERVAL_MS) {
            kotlinx.coroutines.delay(MIN_REQUEST_INTERVAL_MS - timeSinceLastRequest)
        }
        lastRequestTime = System.currentTimeMillis()
        
        var connection: HttpURLConnection? = null
        
        try {
            // Get API key from secure storage
            val apiKey = getApiKey()
            
            val url = URL("$BASE_URL$endpoint")
            connection = url.openConnection() as HttpURLConnection
            
            connection.apply {
                requestMethod = method
                connectTimeout = CONNECT_TIMEOUT_MS.toInt()
                readTimeout = READ_TIMEOUT_MS.toInt()
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("x-changenow-api-key", apiKey) // FIXED: From secure storage
                doInput = true
                
                if (method == "POST" && body != null) {
                    doOutput = true
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(body)
                        writer.flush()
                    }
                }
            }
            
            val responseCode = connection.responseCode
            
            if (responseCode !in 200..299) {
                val errorStream = connection.errorStream ?: connection.inputStream
                val errorBody = errorStream.bufferedReader().use { it.readText() }
                
                Timber.tag(TAG).e("API error $responseCode: $errorBody")
                throw Exception("API error: $responseCode - ${parseErrorMessage(errorBody)}")
            }
            
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            
            Timber.tag(TAG).d("API response: ${response.take(200)}")
            
            response
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Request failed: $endpoint")
            throw e
        } finally {
            connection?.disconnect()
        }
    }
    
    // ============================================================================
    // RESPONSE PARSERS
    // ============================================================================
    
    private fun parseCurrenciesResponse(json: String): List<Currency> {
        val currencies = mutableListOf<Currency>()
        val jsonArray = JSONArray(json)
        
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            currencies.add(
                Currency(
                    ticker = item.getString("ticker"),
                    name = item.getString("name"),
                    image = item.optString("image", null),
                    hasExternalId = item.optBoolean("hasExternalId", false),
                    isFiat = item.optBoolean("isFiat", false),
                    featured = item.optBoolean("featured", false),
                    isStable = item.optBoolean("isStable", false),
                    supportsFixedRate = item.optBoolean("supportsFixedRate", false),
                    network = item.optString("network", null)
                )
            )
        }
        
        return currencies
    }
    
    private fun parseEstimateResponse(
        json: String,
        fromCurrency: String,
        toCurrency: String
    ): ExchangeRate {
        val obj = JSONObject(json)
        
        return ExchangeRate(
            fromCurrency = fromCurrency,
            toCurrency = toCurrency,
            estimatedAmount = BigDecimal(obj.getString("toAmount")),
            transactionSpeedForecast = obj.optString("transactionSpeedForecast", null),
            warningMessage = obj.optString("warningMessage", null),
            rateId = obj.optString("rateId", null)
        )
    }
    
    private fun parseRangeResponse(json: String): ExchangeRange {
        val obj = JSONObject(json)
        
        return ExchangeRange(
            minAmount = BigDecimal(obj.getString("minAmount")),
            maxAmount = if (obj.has("maxAmount")) {
                BigDecimal(obj.getString("maxAmount"))
            } else null
        )
    }
    
    private fun parseExchangeResponse(json: String): ExchangeTransaction {
        val obj = JSONObject(json)
        
        return ExchangeTransaction(
            id = obj.getString("id"),
            fromCurrency = obj.getString("fromCurrency"),
            toCurrency = obj.getString("toCurrency"),
            fromAmount = BigDecimal(obj.optString("fromAmount", "0")),
            toAmount = BigDecimal(obj.optString("expectedAmount", "0")),
            payinAddress = obj.getString("payinAddress"),
            payoutAddress = obj.getString("payoutAddress"),
            payinExtraId = obj.optString("payinExtraId", null),
            payoutExtraId = obj.optString("payoutExtraId", null),
            refundAddress = obj.optString("refundAddress", null),
            status = obj.optString("status", "waiting"),
            createdAt = System.currentTimeMillis()
        )
    }
    
    private fun parseStatusResponse(json: String): TransactionStatus {
        val obj = JSONObject(json)
        
        return TransactionStatus(
            id = obj.getString("id"),
            status = obj.getString("status"),
            fromAmount = if (obj.has("amountSend")) {
                BigDecimal(obj.getString("amountSend"))
            } else null,
            toAmount = if (obj.has("amountReceive")) {
                BigDecimal(obj.getString("amountReceive"))
            } else null,
            payinHash = obj.optString("payinHash", null),
            payoutHash = obj.optString("payoutHash", null),
            expectedReceiveAmount = if (obj.has("expectedReceiveAmount")) {
                BigDecimal(obj.getString("expectedReceiveAmount"))
            } else null,
            expectedSendAmount = if (obj.has("expectedSendAmount")) {
                BigDecimal(obj.getString("expectedSendAmount"))
            } else null,
            updatedAt = System.currentTimeMillis()
        )
    }
    
    private fun parseHistoryResponse(json: String): List<TransactionStatus> {
        val transactions = mutableListOf<TransactionStatus>()
        val jsonArray = JSONArray(json)
        
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            transactions.add(parseStatusResponse(item.toString()))
        }
        
        return transactions
    }
    
    private fun parseErrorMessage(errorBody: String): String {
        return try {
            val json = JSONObject(errorBody)
            json.optString("message", "Unknown error")
        } catch (e: Exception) {
            errorBody
        }
    }
}
