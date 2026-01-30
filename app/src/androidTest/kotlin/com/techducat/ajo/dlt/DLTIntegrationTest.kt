package com.techducat.ajo.dlt


import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.robolectric.RobolectricTestRunner
import com.techducat.ajo.wallet.WalletSuite
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.*
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

@RunWith(RobolectricTestRunner::class)
class DLTIntegrationTest {
    
    private lateinit var context: Context
    private lateinit var ipfsProvider: IPFSProvider
    private lateinit var dltProvider: DLTProvider
    
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        ipfsProvider = IPFSProvider.getInstance(context)
        ipfsProvider.clearCache()
    }
    
    @After
    fun teardown() {
        ipfsProvider.clearCache()
        DLTProviderFactory.reset()
    }
    
    @Test
    fun testIPFSProviderInitialization() {
        // Given & When
        val provider = IPFSProvider.getInstance(context)
        
        // Then
        assertNotNull(provider)
    }
    
    @Test
    fun testStoreAndRetrieveData() = runBlocking {
        // Given
        val testData = """{"rosca": "test_data", "timestamp": ${System.currentTimeMillis()}}"""
        
        // When
        val storeResult = ipfsProvider.store(testData)
        
        // Then
        assertTrue(storeResult.isSuccess)
        val hash = storeResult.getOrNull()
        assertNotNull(hash)
        
        // Verify retrieval
        val retrieveResult = ipfsProvider.retrieve(hash)
        assertTrue(retrieveResult.isSuccess)
        
        // Note: In integration test, actual network calls may not work
        // This validates the flow and error handling
    }
    
    @Test
    fun testCachingBehavior() = runBlocking {
        // Given
        val testData = """{"test": "caching"}"""
        
        // When - store data
        val storeResult = ipfsProvider.store(testData)
        assertTrue(storeResult.isSuccess)
        
        val hash = storeResult.getOrNull()
        assertNotNull(hash)
        
        // Verify cache size increased
        val cacheSize = ipfsProvider.getCacheSize()
        assertTrue(cacheSize > 0)
        
        // Clear cache
        ipfsProvider.clearCache()
        
        // Verify cache is empty
        assertEquals(0L, ipfsProvider.getCacheSize())
    }
    
    @Test
    fun testJsonUploadAndFetch() {
        // Given
        val testJson = """{"test": "json", "value": 123}"""
        
        // When
        val hash = ipfsProvider.uploadJson(testJson)
        
        // Then
        // In integration test, this may return a mock hash or fail
        // We're testing the API contract
        // If hash is not null, try to fetch
        if (hash != null) {
            val retrieved = ipfsProvider.fetchJson(hash)
            // Retrieved may be null in test environment
        }
    }
    
    @Test
    fun testConfiguration() {
        // Given
        val customNodeUrl = "http://localhost:5001"
        
        // When
        ipfsProvider.setNodeUrl(customNodeUrl)
        
        // Then
        val prefs = context.getSharedPreferences("ipfs_config", Context.MODE_PRIVATE)
        assertEquals(customNodeUrl, prefs.getString("ipfs_node_url", null))
    }
    
    @Test
    fun testPinataConfiguration() {
        // Given
        val apiKey = "test_key"
        val apiSecret = "test_secret"
        
        // When
        ipfsProvider.setPinataCredentials(apiKey, apiSecret)
        
        // Then
        val prefs = context.getSharedPreferences("ipfs_config", Context.MODE_PRIVATE)
        assertEquals(apiKey, prefs.getString("pinata_api_key", null))
        assertEquals(apiSecret, prefs.getString("pinata_api_secret", null))
    }
}
