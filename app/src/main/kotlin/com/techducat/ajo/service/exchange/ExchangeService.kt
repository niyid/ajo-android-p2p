package com.techducat.ajo.service.exchange

import com.techducat.ajo.util.Logger
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.math.BigDecimal
import java.io.IOException

/**
 * Exchange Service - Buy/Sell Monero using ChangeNow API
 */
class ExchangeService {
    
    companion object {
        private const val API_BASE_URL = "https://api.changenow.io/v2"
        private const val API_KEY = "YOUR_CHANGENOW_API_KEY" // Get from changenow.io
    }
    
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    
    /**
     * Get exchange rate estimate
     */
    suspend fun getEstimate(
        fromCurrency: String,
        toCurrency: String,
        amount: BigDecimal
    ): Result<ExchangeEstimate> {
        return try {
            val url = "$API_BASE_URL/exchange/estimated-amount" +
                    "?fromCurrency=$fromCurrency" +
                    "&toCurrency=$toCurrency" +
                    "&fromAmount=$amount" +
                    "&type=direct"
            
            val request = Request.Builder()
                .url(url)
                .header("x-changenow-api-key", API_KEY)
                .build()
            
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: throw IOException("Empty response")
            
            if (!response.isSuccessful) {
                throw IOException("API error: ${response.code}")
            }
            
            val estimate = json.decodeFromString<ExchangeEstimateResponse>(body)
            
            Result.success(
                ExchangeEstimate(
                    fromAmount = amount,
                    fromCurrency = fromCurrency,
                    toAmount = BigDecimal(estimate.toAmount),
                    toCurrency = toCurrency,
                    estimatedFee = BigDecimal(estimate.networkFee ?: "0"),
                    validUntil = System.currentTimeMillis() + 30000
                )
            )
        } catch (e: Exception) {
            Logger.e("Exchange: Estimate failed - ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Buy Monero with fiat
     */
    suspend fun buyMonero(
        amount: BigDecimal,
        currency: String,
        moneroAddress: String
    ): Result<ExchangeTransaction> {
        return try {
            val requestBody = mapOf(
                "fromCurrency" to currency.lowercase(),
                "toCurrency" to "xmr",
                "fromAmount" to amount.toString(),
                "address" to moneroAddress,
                "flow" to "standard",
                "type" to "direct"
            )
            
            val jsonBody = json.encodeToString(requestBody)
            val body = jsonBody.toRequestBody("application/json".toMediaType())
            
            val request = Request.Builder()
                .url("$API_BASE_URL/exchange")
                .header("x-changenow-api-key", API_KEY)
                .post(body)
                .build()
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw IOException("Empty response")
            
            if (!response.isSuccessful) {
                throw IOException("API error: $responseBody")
            }
            
            val exchangeResponse = json.decodeFromString<ExchangeCreateResponse>(responseBody)
            
            Logger.i("Exchange: Buy order created - ${exchangeResponse.id}")
            
            Result.success(
                ExchangeTransaction(
                    id = exchangeResponse.id,
                    payinAddress = exchangeResponse.payinAddress,
                    payinAmount = BigDecimal(exchangeResponse.fromAmount),
                    payoutAddress = moneroAddress,
                    payoutAmount = BigDecimal(exchangeResponse.toAmount),
                    status = exchangeResponse.status,
                    createdAt = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            Logger.e("Exchange: Buy failed - ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Check transaction status
     */
    suspend fun getTransactionStatus(transactionId: String): Result<TransactionStatus> {
        return try {
            val request = Request.Builder()
                .url("$API_BASE_URL/exchange/by-id?id=$transactionId")
                .header("x-changenow-api-key", API_KEY)
                .build()
            
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: throw IOException("Empty response")
            
            if (!response.isSuccessful) {
                throw IOException("API error: ${response.code}")
            }
            
            val statusResponse = json.decodeFromString<ExchangeStatusResponse>(body)
            
            Result.success(
                TransactionStatus(
                    id = statusResponse.id,
                    status = statusResponse.status,
                    payinHash = statusResponse.payinHash,
                    payoutHash = statusResponse.payoutHash,
                    amountFrom = BigDecimal(statusResponse.fromAmount ?: "0"),
                    amountTo = BigDecimal(statusResponse.toAmount ?: "0")
                )
            )
        } catch (e: Exception) {
            Logger.e("Exchange: Status check failed - ${e.message}")
            Result.failure(e)
        }
    }
}

// Data classes
data class ExchangeEstimate(
    val fromAmount: BigDecimal,
    val fromCurrency: String,
    val toAmount: BigDecimal,
    val toCurrency: String,
    val estimatedFee: BigDecimal,
    val validUntil: Long
)

data class ExchangeTransaction(
    val id: String,
    val payinAddress: String,
    val payinAmount: BigDecimal,
    val payoutAddress: String,
    val payoutAmount: BigDecimal,
    val status: String,
    val createdAt: Long
)

data class TransactionStatus(
    val id: String,
    val status: String,
    val payinHash: String?,
    val payoutHash: String?,
    val amountFrom: BigDecimal,
    val amountTo: BigDecimal
)

@Serializable
data class ExchangeEstimateResponse(
    val toAmount: String,
    val networkFee: String?
)

@Serializable
data class ExchangeCreateResponse(
    val id: String,
    val payinAddress: String,
    val fromAmount: String,
    val toAmount: String,
    val status: String
)

@Serializable
data class ExchangeStatusResponse(
    val id: String,
    val status: String,
    val payinHash: String?,
    val payoutHash: String?,
    val fromAmount: String?,
    val toAmount: String?
)
