package com.techducat.ajo.dlt


import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.robolectric.RobolectricTestRunner
import com.m2049r.xmrwallet.model.Wallet
import com.m2049r.xmrwallet.model.PendingTransaction
import com.m2049r.xmrwallet.model.TransactionHistory
import com.m2049r.xmrwallet.model.TransactionInfo
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
class IPFSProviderTest {
    
    private lateinit var context: Context
    private lateinit var ipfsProvider: IPFSProvider
    private lateinit var mockWebServer: MockWebServer
    
    private var autoCloseable: AutoCloseable? = null
    
    @Before
    fun setup() {
        autoCloseable = MockitoAnnotations.openMocks(this)
        context = ApplicationProvider.getApplicationContext()
        
        // Setup mock web server for IPFS API
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        ipfsProvider = IPFSProvider.getInstance(context)
        ipfsProvider.setNodeUrl(mockWebServer.url("/").toString().removeSuffix("/"))
    }
    
    @After
    fun teardown() {
        autoCloseable?.close()
        mockWebServer.shutdown()
        ipfsProvider.clearCache()
    }
    
    @Test
    fun testSingletonInstance() {
        // Given & When
        val instance1 = IPFSProvider.getInstance(context)
        val instance2 = IPFSProvider.getInstance(context)
        
        // Then
        assertEquals(instance1, instance2)
    }
    
    @Test
    fun testStoreData() = runBlocking {
        // Given
        val testData = """{"test": "data"}"""
        val expectedHash = "QmTest123"
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"Hash": "$expectedHash"}""")
        )
        
        // When
        val result = ipfsProvider.store(testData)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedHash, result.getOrNull())
    }
    
    @Test
    fun testStoreDataFailure() = runBlocking {
        // Given
        val testData = """{"test": "data"}"""
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )
        
        // When
        val result = ipfsProvider.store(testData)
        
        // Then
        assertTrue(result.isFailure)
    }
    
    @Test
    fun testRetrieveData() = runBlocking {
        // Given
        val hash = "QmTest123"
        val expectedData = """{"test": "data"}"""
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(expectedData)
        )
        
        // When
        val result = ipfsProvider.retrieve(hash)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedData, result.getOrNull())
    }
    
    @Test
    fun testRetrieveDataFromCache() = runBlocking {
        // Given
        val testData = """{"test": "data"}"""
        val hash = "QmTest123"
        
        // Store data first
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"Hash": "$hash"}""")
        )
        ipfsProvider.store(testData)
        
        // When - retrieve without network call (from cache)
        val result = ipfsProvider.retrieve(hash)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(testData, result.getOrNull())
    }
    
    @Test
    fun testUploadJson() {
        // Given
        val json = """{"test": "data"}"""
        val expectedHash = "QmTest123"
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"Hash": "$expectedHash"}""")
        )
        
        // When
        val hash = ipfsProvider.uploadJson(json)
        
        // Then
        assertNotNull(hash)
        assertEquals(expectedHash, hash)
    }
    
    @Test
    fun testFetchJson() {
        // Given
        val hash = "QmTest123"
        val expectedJson = """{"test": "data"}"""
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(expectedJson)
        )
        
        // When
        val jsonObject = ipfsProvider.fetchJson(hash)
        
        // Then
        assertNotNull(jsonObject)
        assertEquals("data", jsonObject.getString("test"))
    }
    
    @Test
    fun testCheckNodeAvailability() = runBlocking {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"Version": "0.12.0"}""")
        )
        
        // When
        val result = ipfsProvider.checkNodeAvailability()
        
        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
    }
    
    @Test
    fun testCheckNodeAvailabilityFailure() = runBlocking {
        // Given - server is down
        mockWebServer.shutdown()
        
        // When
        val result = ipfsProvider.checkNodeAvailability()
        
        // Then
        assertTrue(result.isSuccess)
        assertFalse(result.getOrNull() == true)
    }
    
    @Test
    fun testPin() = runBlocking {
        // Given
        val hash = "QmTest123"
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"Pins": ["$hash"]}""")
        )
        
        // When
        val result = ipfsProvider.pin(hash)
        
        // Then
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun testUnpin() = runBlocking {
        // Given
        val hash = "QmTest123"
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"Pins": ["$hash"]}""")
        )
        
        // When
        val result = ipfsProvider.unpin(hash)
        
        // Then
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun testGetNodeStats() = runBlocking {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""
                    {
                        "RepoSize": 1000000,
                        "NumObjects": 50,
                        "RepoPath": "/data/ipfs",
                        "Version": "0.12.0"
                    }
                """.trimIndent())
        )
        
        // When
        val result = ipfsProvider.getNodeStats()
        
        // Then
        assertTrue(result.isSuccess)
        val stats = result.getOrNull()
        assertNotNull(stats)
        assertEquals(1000000L, stats.repoSize)
        assertEquals(50L, stats.numObjects)
    }
    
    @Test
    fun testClearCache() {
        // Given - add some data to cache
        val hash = "QmTest123"
        val data = """{"test": "data"}"""
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"Hash": "$hash"}""")
        )
        
        runBlocking {
            ipfsProvider.store(data)
        }
        
        val cacheSizeBefore = ipfsProvider.getCacheSize()
        
        // When
        ipfsProvider.clearCache()
        
        // Then
        val cacheSizeAfter = ipfsProvider.getCacheSize()
        assertTrue(cacheSizeBefore > 0)
        assertEquals(0L, cacheSizeAfter)
    }
    
    @Test
    fun testSetNodeUrl() {
        // Given
        val customUrl = "http://custom-ipfs-node:5001"
        
        // When
        ipfsProvider.setNodeUrl(customUrl)
        
        // Then - should use custom URL for next request
        // Verify by checking preferences
        val prefs = context.getSharedPreferences("ipfs_config", Context.MODE_PRIVATE)
        assertEquals(customUrl, prefs.getString("ipfs_node_url", null))
    }
    
    @Test
    fun testSetPinataCredentials() {
        // Given
        val apiKey = "test_api_key"
        val apiSecret = "test_api_secret"
        
        // When
        ipfsProvider.setPinataCredentials(apiKey, apiSecret)
        
        // Then
        val prefs = context.getSharedPreferences("ipfs_config", Context.MODE_PRIVATE)
        assertEquals(apiKey, prefs.getString("pinata_api_key", null))
        assertEquals(apiSecret, prefs.getString("pinata_api_secret", null))
    }
}
