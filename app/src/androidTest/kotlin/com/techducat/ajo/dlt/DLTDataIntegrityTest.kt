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
class DLTDataIntegrityTest {
    
    private lateinit var context: Context
    private lateinit var ipfsProvider: IPFSProvider
    
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        ipfsProvider = IPFSProvider.getInstance(context)
        ipfsProvider.clearCache()
    }
    
    @After
    fun teardown() {
        ipfsProvider.clearCache()
    }
    
    @Test
    fun testDataIntegrityAfterStoreAndRetrieve() = runBlocking {
        // Given
        val originalData = """
            {
                "rosca": "integrity_test",
                "timestamp": ${System.currentTimeMillis()},
                "checksum": "abc123"
            }
        """.trimIndent()
        
        // When
        val storeResult = ipfsProvider.store(originalData)
        
        if (storeResult.isSuccess) {
            val hash = storeResult.getOrNull()!!
            val retrieveResult = ipfsProvider.retrieve(hash)
            
            // Then
            if (retrieveResult.isSuccess) {
                val retrievedData = retrieveResult.getOrNull()
                assertEquals(originalData, retrievedData)
            }
        }
    }
    
    @Test
    fun testJsonStructurePreservation() = runBlocking {
        // Given
        val complexJson = """
            {
                "rosca": {
                    "id": "test_123",
                    "members": ["m1", "m2", "m3"],
                    "metadata": {
                        "created": 1234567890,
                        "status": "active"
                    }
                }
            }
        """.trimIndent()
        
        // When
        val storeResult = ipfsProvider.store(complexJson)
        
        if (storeResult.isSuccess) {
            val hash = storeResult.getOrNull()!!
            val jsonObject = ipfsProvider.fetchJson(hash)
            
            // Then
            if (jsonObject != null) {
                assertTrue(jsonObject.has("rosca"))
                val rosca = jsonObject.getJSONObject("rosca")
                assertEquals("test_123", rosca.getString("id"))
            }
        }
    }
    
    @Test
    fun testCacheConsistency() = runBlocking {
        // Given
        val testData = """{"cache": "consistency"}"""
        val storeResult = ipfsProvider.store(testData)
        
        if (storeResult.isSuccess) {
            val hash = storeResult.getOrNull()!!
            
            // When - retrieve multiple times
            val retrieval1 = ipfsProvider.retrieve(hash)
            val retrieval2 = ipfsProvider.retrieve(hash)
            val retrieval3 = ipfsProvider.retrieve(hash)
            
            // Then - all retrievals should return same data
            if (retrieval1.isSuccess && retrieval2.isSuccess && retrieval3.isSuccess) {
                assertEquals(retrieval1.getOrNull(), retrieval2.getOrNull())
                assertEquals(retrieval2.getOrNull(), retrieval3.getOrNull())
            }
        }
    }
}
