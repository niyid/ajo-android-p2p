package com.techducat.ajo.dlt

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * IPFS Provider for decentralized storage
 * 
 * Supports both local IPFS node and public gateways:
 * - Local node: http://127.0.0.1:5001
 * - Infura: https://ipfs.infura.io:5001
 * - Pinata: https://api.pinata.cloud
 * - Public gateways for retrieval
 */
class IPFSProvider private constructor(private val context: Context) {
    
    companion object {
        private const val TAG = "com.techducat.ajo.dlt.IPFSProvider"
        
        @Volatile
        private var instance: IPFSProvider? = null
        
        fun getInstance(context: Context): IPFSProvider {
            return instance ?: synchronized(this) {
                instance ?: IPFSProvider(context.applicationContext).also { instance = it }
            }
        }
        
        // Configuration
        private const val LOCAL_NODE_URL = "http://127.0.0.1:5001"
        private const val INFURA_URL = "https://ipfs.infura.io:5001"
        private const val PINATA_API_URL = "https://api.pinata.cloud"
        
        // Gateway URLs for retrieval
        private val GATEWAY_URLS = listOf(
            "https://ipfs.io/ipfs/",
            "https://cloudflare-ipfs.com/ipfs/",
            "https://gateway.pinata.cloud/ipfs/",
            "https://dweb.link/ipfs/"
        )
        
        private const val TIMEOUT_SECONDS = 30L
        private const val MAX_RETRIES = 3
    }
    
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }
    
    private val prefs by lazy {
        context.getSharedPreferences("ipfs_config", Context.MODE_PRIVATE)
    }
    
    // Cache directory for IPFS content
    private val cacheDir by lazy {
        File(context.cacheDir, "ipfs_cache").apply {
            if (!exists()) mkdirs()
        }
    }
    
    /**
     * Store data on IPFS
     * @param data String data to store
     * @return Result containing IPFS hash (CID)
     */
    suspend fun store(data: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Storing data on IPFS (${data.length} bytes)")
            
            // Try local node first, then fallback to Infura
            val nodeUrl = getConfiguredNodeUrl()
            
            val result = when {
                nodeUrl.contains("pinata") -> storeToPinata(data)
                else -> storeToNode(nodeUrl, data)
            }
            
            result.onSuccess { hash ->
                Log.i(TAG, "✓ Data stored on IPFS: $hash")
                // Cache locally for quick retrieval
                cacheData(hash, data)
            }.onFailure { error ->
                Log.e(TAG, "✗ Failed to store on IPFS", error)
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Exception storing to IPFS", e)
            Result.failure(e)
        }
    }
    
    /**
     * Retrieve data from IPFS
     * @param hash IPFS hash (CID) to retrieve
     * @return Result containing retrieved data
     */
    suspend fun retrieve(hash: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Retrieving data from IPFS: $hash")
            
            // Check cache first
            val cached = getCachedData(hash)
            if (cached != null) {
                Log.d(TAG, "✓ Retrieved from cache")
                return@withContext Result.success(cached)
            }
            
            // Try multiple gateways with retry
            var lastError: Exception? = null
            
            for (gateway in GATEWAY_URLS) {
                for (attempt in 1..MAX_RETRIES) {
                    try {
                        val url = "$gateway$hash"
                        Log.d(TAG, "Attempting gateway: $gateway (attempt $attempt)")
                        
                        val request = Request.Builder()
                            .url(url)
                            .get()
                            .build()
                        
                        val response = okHttpClient.newCall(request).execute()
                        
                        if (response.isSuccessful) {
                            val data = response.body?.string() ?: ""
                            Log.i(TAG, "✓ Retrieved from IPFS via $gateway")
                            
                            // Cache for future use
                            cacheData(hash, data)
                            
                            return@withContext Result.success(data)
                        }
                        
                        Log.w(TAG, "Gateway $gateway returned: ${response.code}")
                        
                    } catch (e: Exception) {
                        Log.w(TAG, "Gateway $gateway failed (attempt $attempt)", e)
                        lastError = e
                        
                        if (attempt < MAX_RETRIES) {
                            Thread.sleep(1000L * attempt) // Exponential backoff
                        }
                    }
                }
            }
            
            Result.failure(lastError ?: Exception("Failed to retrieve from IPFS"))
            
        } catch (e: Exception) {
            Log.e(TAG, "Exception retrieving from IPFS", e)
            Result.failure(e)
        }
    }
    
    /**
     * Upload JSON data to IPFS
     * @param json JSON string to upload
     * @return IPFS hash or null on failure
     */
    fun uploadJson(json: String): String? {
        return try {
            // Use blocking call for synchronous API
            val result = kotlinx.coroutines.runBlocking {
                store(json)
            }
            result.getOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload JSON", e)
            null
        }
    }
    
    /**
     * Fetch JSON data from IPFS
     * @param hash IPFS hash to fetch
     * @return JSONObject or null on failure
     */
    fun fetchJson(hash: String): JSONObject? {
        return try {
            // Use blocking call for synchronous API
            val result = kotlinx.coroutines.runBlocking {
                retrieve(hash)
            }
            
            result.getOrNull()?.let { data ->
                JSONObject(data)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch JSON", e)
            null
        }
    }
    
    /**
     * Store file to IPFS
     * @param file File to upload
     * @return Result containing IPFS hash
     */
    suspend fun storeFile(file: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!file.exists()) {
                return@withContext Result.failure(IOException("File does not exist"))
            }
            
            Log.d(TAG, "Storing file: ${file.name} (${file.length()} bytes)")
            
            val nodeUrl = getConfiguredNodeUrl()
            
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    file.name,
                    file.readBytes().toRequestBody("application/octet-stream".toMediaType())
                )
                .build()
            
            val request = Request.Builder()
                .url("$nodeUrl/api/v0/add")
                .post(requestBody)
                .build()
            
            val response = okHttpClient.newCall(request).execute()
            
            if (response.isSuccessful) {
                val jsonResponse = JSONObject(response.body?.string() ?: "{}")
                val hash = jsonResponse.getString("Hash")
                Log.i(TAG, "✓ File stored on IPFS: $hash")
                Result.success(hash)
            } else {
                Result.failure(IOException("Failed to store file: ${response.code}"))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Exception storing file to IPFS", e)
            Result.failure(e)
        }
    }
    
    /**
     * Pin content to ensure persistence
     * @param hash IPFS hash to pin
     * @return Result indicating success
     */
    suspend fun pin(hash: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Pinning content: $hash")
            
            val nodeUrl = getConfiguredNodeUrl()
            
            val request = Request.Builder()
                .url("$nodeUrl/api/v0/pin/add?arg=$hash")
                .post("".toRequestBody())
                .build()
            
            val response = okHttpClient.newCall(request).execute()
            
            if (response.isSuccessful) {
                Log.i(TAG, "✓ Content pinned: $hash")
                Result.success(Unit)
            } else {
                Result.failure(IOException("Failed to pin: ${response.code}"))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Exception pinning content", e)
            Result.failure(e)
        }
    }
    
    /**
     * Unpin content
     * @param hash IPFS hash to unpin
     * @return Result indicating success
     */
    suspend fun unpin(hash: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val nodeUrl = getConfiguredNodeUrl()
            
            val request = Request.Builder()
                .url("$nodeUrl/api/v0/pin/rm?arg=$hash")
                .post("".toRequestBody())
                .build()
            
            val response = okHttpClient.newCall(request).execute()
            
            if (response.isSuccessful) {
                Log.i(TAG, "✓ Content unpinned: $hash")
                Result.success(Unit)
            } else {
                Result.failure(IOException("Failed to unpin: ${response.code}"))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Exception unpinning content", e)
            Result.failure(e)
        }
    }
    
    /**
     * Check if IPFS node is available
     */
    suspend fun checkNodeAvailability(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val nodeUrl = getConfiguredNodeUrl()
            
            val request = Request.Builder()
                .url("$nodeUrl/api/v0/version")
                .get()
                .build()
            
            val response = okHttpClient.newCall(request).execute()
            Result.success(response.isSuccessful)
            
        } catch (e: Exception) {
            Log.w(TAG, "IPFS node not available", e)
            Result.success(false)
        }
    }
    
    /**
     * Get IPFS node stats
     */
    suspend fun getNodeStats(): Result<IPFSStats> = withContext(Dispatchers.IO) {
        try {
            val nodeUrl = getConfiguredNodeUrl()
            
            val request = Request.Builder()
                .url("$nodeUrl/api/v0/stats/repo")
                .post("".toRequestBody())
                .build()
            
            val response = okHttpClient.newCall(request).execute()
            
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string() ?: "{}")
                val stats = IPFSStats(
                    repoSize = json.optLong("RepoSize", 0),
                    numObjects = json.optLong("NumObjects", 0),
                    repoPath = json.optString("RepoPath", ""),
                    version = json.optString("Version", "")
                )
                Result.success(stats)
            } else {
                Result.failure(IOException("Failed to get stats: ${response.code}"))
            }
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ============================================================================
    // PRIVATE HELPER METHODS
    // ============================================================================
    
    private fun storeToNode(nodeUrl: String, data: String): Result<String> {
        try {
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    "data.json",
                    data.toRequestBody("application/json".toMediaType())
                )
                .build()
            
            val request = Request.Builder()
                .url("$nodeUrl/api/v0/add")
                .post(requestBody)
                .build()
            
            val response = okHttpClient.newCall(request).execute()
            
            return if (response.isSuccessful) {
                val jsonResponse = JSONObject(response.body?.string() ?: "{}")
                val hash = jsonResponse.getString("Hash")
                Result.success(hash)
            } else {
                Result.failure(IOException("Node returned: ${response.code}"))
            }
            
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
    
    private fun storeToPinata(data: String): Result<String> {
        try {
            val apiKey = prefs.getString("pinata_api_key", null)
            val apiSecret = prefs.getString("pinata_api_secret", null)
            
            if (apiKey == null || apiSecret == null) {
                return Result.failure(Exception("Pinata credentials not configured"))
            }
            
            val json = JSONObject().apply {
                put("pinataContent", JSONObject(data))
            }
            
            val requestBody = json.toString()
                .toRequestBody("application/json".toMediaType())
            
            val request = Request.Builder()
                .url("$PINATA_API_URL/pinning/pinJSONToIPFS")
                .addHeader("pinata_api_key", apiKey)
                .addHeader("pinata_secret_api_key", apiSecret)
                .post(requestBody)
                .build()
            
            val response = okHttpClient.newCall(request).execute()
            
            return if (response.isSuccessful) {
                val jsonResponse = JSONObject(response.body?.string() ?: "{}")
                val hash = jsonResponse.getString("IpfsHash")
                Result.success(hash)
            } else {
                Result.failure(IOException("Pinata returned: ${response.code}"))
            }
            
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
    
    private fun getConfiguredNodeUrl(): String {
        return prefs.getString("ipfs_node_url", LOCAL_NODE_URL) ?: LOCAL_NODE_URL
    }
    
    private fun cacheData(hash: String, data: String) {
        try {
            val cacheFile = File(cacheDir, hash)
            cacheFile.writeText(data)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to cache data", e)
        }
    }
    
    private fun getCachedData(hash: String): String? {
        return try {
            val cacheFile = File(cacheDir, hash)
            if (cacheFile.exists()) {
                cacheFile.readText()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    // ============================================================================
    // CONFIGURATION METHODS
    // ============================================================================
    
    /**
     * Configure IPFS node URL
     */
    fun setNodeUrl(url: String) {
        prefs.edit().putString("ipfs_node_url", url).apply()
        Log.i(TAG, "IPFS node URL configured: $url")
    }
    
    /**
     * Configure Pinata credentials
     */
    fun setPinataCredentials(apiKey: String, apiSecret: String) {
        prefs.edit()
            .putString("pinata_api_key", apiKey)
            .putString("pinata_api_secret", apiSecret)
            .apply()
        Log.i(TAG, "Pinata credentials configured")
    }
    
    /**
     * Clear cache
     */
    fun clearCache() {
        try {
            cacheDir.listFiles()?.forEach { it.delete() }
            Log.i(TAG, "IPFS cache cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear cache", e)
        }
    }
    
    /**
     * Get cache size in bytes
     */
    fun getCacheSize(): Long {
        return try {
            cacheDir.listFiles()?.sumOf { it.length() } ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}

/**
 * IPFS node statistics
 */
data class IPFSStats(
    val repoSize: Long,
    val numObjects: Long,
    val repoPath: String,
    val version: String
)
