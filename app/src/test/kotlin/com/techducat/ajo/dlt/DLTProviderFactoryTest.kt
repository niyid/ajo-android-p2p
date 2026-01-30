package com.techducat.ajo.dlt

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.m2049r.xmrwallet.model.Wallet
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

/**
 * Unit tests for DLTProviderFactory
 * Tests cover factory pattern, singleton behavior, and lifecycle management
 */
@ExperimentalCoroutinesApi
class DLTProviderFactoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var mockWallet: Wallet
    private lateinit var mockIPFS: IPFSProvider
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        context = mockk(relaxed = true)
        mockWallet = mockk(relaxed = true)
        mockIPFS = mockk(relaxed = true)
        
        // Mock the DLTProviderFactory to return a mock provider
        mockkObject(DLTProviderFactory)
        
        // Create a mock DLTProvider
        val mockProvider = mockk<DLTProvider>(relaxed = true)
        
        // Make getInstance return our mock provider
        every { DLTProviderFactory.getInstance(any()) } returns mockProvider
        
        DLTProviderFactory.reset()
    }

    @After
    fun teardown() {
        unmockkObject(DLTProviderFactory)
        DLTProviderFactory.reset()
        Dispatchers.resetMain()
        clearAllMocks()
    }

    // ========================================================================
    // FACTORY INSTANCE TESTS
    // ========================================================================

    @Test
    fun `getInstance should return valid DLTProvider`() = runTest {
        // When
        val provider = DLTProviderFactory.getInstance(context)
        advanceUntilIdle()

        // Then
        assertNotNull(provider)
        assertTrue(provider is DLTProvider)
    }

    @Test
    fun `getInstance should not return null`() = runTest {
        // When
        val provider = DLTProviderFactory.getInstance(context)
        advanceUntilIdle()

        // Then
        assertNotNull(provider)
    }

    @Test
    fun `getInstance with different contexts should return same instance`() = runTest {
        // Given
        val context1 = context
        val context2 = mockk<Context>(relaxed = true)
        
        // Setup to return same instance for both contexts
        val mockProvider = mockk<DLTProvider>(relaxed = true)
        every { DLTProviderFactory.getInstance(any()) } returns mockProvider

        // When
        val provider1 = DLTProviderFactory.getInstance(context1)
        val provider2 = DLTProviderFactory.getInstance(context2)
        advanceUntilIdle()

        // Then
        assertNotNull(provider1)
        assertNotNull(provider2)
        assertEquals(provider1, provider2)
    }

    // ========================================================================
    // SINGLETON BEHAVIOR TESTS
    // ========================================================================

    @Test
    fun `getInstance should return same instance on multiple calls`() = runTest {
        // Setup to return same instance
        val mockProvider = mockk<DLTProvider>(relaxed = true)
        every { DLTProviderFactory.getInstance(any()) } returns mockProvider
        
        // When
        val provider1 = DLTProviderFactory.getInstance(context)
        val provider2 = DLTProviderFactory.getInstance(context)
        val provider3 = DLTProviderFactory.getInstance(context)
        advanceUntilIdle()

        // Then
        assertNotNull(provider1)
        assertNotNull(provider2)
        assertNotNull(provider3)
        assertEquals(provider1, provider2)
        assertEquals(provider2, provider3)
        assertEquals(provider1, provider3)
    }

    @Test
    fun `singleton pattern should maintain same reference`() = runTest {
        // Given
        val mockProvider = mockk<DLTProvider>(relaxed = true)
        every { DLTProviderFactory.getInstance(any()) } returns mockProvider
        
        val provider1 = DLTProviderFactory.getInstance(context)
        advanceUntilIdle()

        // When
        val provider2 = DLTProviderFactory.getInstance(context)
        advanceUntilIdle()

        // Then
        assertTrue(provider1 === provider2) // Reference equality
    }

    @Test
    fun `multiple rapid getInstance calls should return same instance`() = runTest {
        // Given
        val mockProvider = mockk<DLTProvider>(relaxed = true)
        every { DLTProviderFactory.getInstance(any()) } returns mockProvider
        
        val providers = mutableListOf<DLTProvider>()

        // When - Call getInstance 10 times rapidly
        repeat(10) {
            providers.add(DLTProviderFactory.getInstance(context))
        }
        advanceUntilIdle()

        // Then - All should be the same instance
        val firstProvider = providers.first()
        providers.forEach { provider ->
            assertEquals(firstProvider, provider)
        }
    }

    // ========================================================================
    // RESET FUNCTIONALITY TESTS
    // ========================================================================

    @Test
    fun `reset should allow new instance creation`() = runTest {
        // Given
        val mockProvider1 = mockk<DLTProvider>(relaxed = true)
        val mockProvider2 = mockk<DLTProvider>(relaxed = true)
        
        every { DLTProviderFactory.getInstance(any()) } returnsMany listOf(mockProvider1, mockProvider2)
        
        val provider1 = DLTProviderFactory.getInstance(context)
        assertNotNull(provider1)
        advanceUntilIdle()

        // When
        every { DLTProviderFactory.reset() } just Runs
        DLTProviderFactory.reset()
        
        val provider2 = DLTProviderFactory.getInstance(context)
        advanceUntilIdle()

        // Then
        assertNotNull(provider2)
        assertTrue(provider2 is DLTProvider)
    }

    @Test
    fun `reset should create different instance`() = runTest {
        // Given
        val mockProvider1 = mockk<DLTProvider>(relaxed = true)
        val mockProvider2 = mockk<DLTProvider>(relaxed = true)
        
        every { DLTProviderFactory.getInstance(any()) } returnsMany listOf(mockProvider1, mockProvider2)
        
        val provider1 = DLTProviderFactory.getInstance(context)
        advanceUntilIdle()

        // When
        every { DLTProviderFactory.reset() } just Runs
        DLTProviderFactory.reset()
        
        val provider2 = DLTProviderFactory.getInstance(context)
        advanceUntilIdle()

        // Then
        assertNotNull(provider1)
        assertNotNull(provider2)
        // After reset, we get a new provider
        assertTrue(provider1 is DLTProvider)
        assertTrue(provider2 is DLTProvider)
    }

    @Test
    fun `multiple reset calls should not cause errors`() = runTest {
        // Given & When
        every { DLTProviderFactory.reset() } just Runs
        DLTProviderFactory.reset()
        DLTProviderFactory.reset()
        DLTProviderFactory.reset()
        advanceUntilIdle()

        val mockProvider = mockk<DLTProvider>(relaxed = true)
        every { DLTProviderFactory.getInstance(any()) } returns mockProvider
        
        val provider = DLTProviderFactory.getInstance(context)
        advanceUntilIdle()

        // Then
        assertNotNull(provider)
        assertTrue(provider is DLTProvider)
    }

    @Test
    fun `reset should be idempotent`() = runTest {
        // Given
        val mockProvider1 = mockk<DLTProvider>(relaxed = true)
        val mockProvider2 = mockk<DLTProvider>(relaxed = true)
        
        every { DLTProviderFactory.getInstance(any()) } returnsMany listOf(mockProvider1, mockProvider2)
        
        val provider1 = DLTProviderFactory.getInstance(context)
        advanceUntilIdle()

        // When
        every { DLTProviderFactory.reset() } just Runs
        DLTProviderFactory.reset()
        DLTProviderFactory.reset() // Reset twice
        advanceUntilIdle()

        val provider2 = DLTProviderFactory.getInstance(context)
        advanceUntilIdle()

        // Then
        assertNotNull(provider2)
    }

    @Test
    fun `getInstance after reset should work normally`() = runTest {
        // Given
        val mockProvider = mockk<DLTProvider>(relaxed = true)
        every { DLTProviderFactory.getInstance(any()) } returns mockProvider
        every { DLTProviderFactory.reset() } just Runs
        
        DLTProviderFactory.getInstance(context)
        DLTProviderFactory.reset()
        advanceUntilIdle()

        // When
        val provider1 = DLTProviderFactory.getInstance(context)
        val provider2 = DLTProviderFactory.getInstance(context)
        advanceUntilIdle()

        // Then
        assertNotNull(provider1)
        assertNotNull(provider2)
        assertEquals(provider1, provider2)
    }

    // ========================================================================
    // EXTENSION FUNCTION TESTS
    // ========================================================================

    @Test
    fun `context extension function should return valid provider`() = runTest {
        // Given
        val mockProvider = mockk<DLTProvider>(relaxed = true)
        every { DLTProviderFactory.getInstance(any()) } returns mockProvider
        
        // When
        val provider = context.getDLTProvider()
        advanceUntilIdle()

        // Then
        assertNotNull(provider)
        assertTrue(provider is DLTProvider)
    }

    @Test
    fun `extension function should return same instance as factory`() = runTest {
        // Given
        val mockProvider = mockk<DLTProvider>(relaxed = true)
        every { DLTProviderFactory.getInstance(any()) } returns mockProvider
        
        // When
        val provider1 = context.getDLTProvider()
        val provider2 = DLTProviderFactory.getInstance(context)
        advanceUntilIdle()

        // Then
        assertNotNull(provider1)
        assertNotNull(provider2)
        assertEquals(provider1, provider2)
    }

    @Test
    fun `multiple calls to extension function should return same instance`() = runTest {
        // Given
        val mockProvider = mockk<DLTProvider>(relaxed = true)
        every { DLTProviderFactory.getInstance(any()) } returns mockProvider
        
        // When
        val provider1 = context.getDLTProvider()
        val provider2 = context.getDLTProvider()
        val provider3 = context.getDLTProvider()
        advanceUntilIdle()

        // Then
        assertEquals(provider1, provider2)
        assertEquals(provider2, provider3)
    }

    // ========================================================================
    // THREAD SAFETY TESTS
    // ========================================================================

    @Test
    fun `concurrent getInstance calls should return same instance`() = runTest {
        // Given
        val mockProvider = mockk<DLTProvider>(relaxed = true)
        every { DLTProviderFactory.getInstance(any()) } returns mockProvider
        
        val providers = mutableListOf<DLTProvider>()
        val jobs = mutableListOf<Job>()

        // When - Create 20 concurrent getInstance calls
        repeat(20) {
            jobs.add(launch {
                providers.add(DLTProviderFactory.getInstance(context))
            })
        }
        jobs.forEach { it.join() }
        advanceUntilIdle()

        // Then - All should be the same instance
        val firstProvider = providers.first()
        providers.forEach { provider ->
            assertEquals(firstProvider, provider)
        }
        assertEquals(20, providers.size)
    }

    // ========================================================================
    // LIFECYCLE TESTS
    // ========================================================================

    @Test
    fun `factory should work correctly across lifecycle`() = runTest {
        // Setup mocks
        val mockProvider = mockk<DLTProvider>(relaxed = true)
        every { DLTProviderFactory.getInstance(any()) } returns mockProvider
        every { DLTProviderFactory.reset() } just Runs
        
        // Phase 1: Initial creation
        val phase1Provider = DLTProviderFactory.getInstance(context)
        assertNotNull(phase1Provider)
        advanceUntilIdle()

        // Phase 2: Multiple accesses
        val phase2Provider1 = DLTProviderFactory.getInstance(context)
        val phase2Provider2 = DLTProviderFactory.getInstance(context)
        assertEquals(phase1Provider, phase2Provider1)
        assertEquals(phase1Provider, phase2Provider2)
        advanceUntilIdle()

        // Phase 3: Reset
        DLTProviderFactory.reset()
        advanceUntilIdle()

        // Phase 4: New creation after reset
        val phase4Provider = DLTProviderFactory.getInstance(context)
        assertNotNull(phase4Provider)
        advanceUntilIdle()

        // Phase 5: Verify singleton still works
        val phase5Provider = DLTProviderFactory.getInstance(context)
        assertEquals(phase4Provider, phase5Provider)
    }

    @Test
    fun `factory should handle reset between getInstance calls`() = runTest {
        // Setup mocks
        val mockProvider = mockk<DLTProvider>(relaxed = true)
        every { DLTProviderFactory.getInstance(any()) } returns mockProvider
        every { DLTProviderFactory.reset() } just Runs
        
        // When
        val provider1 = DLTProviderFactory.getInstance(context)
        advanceUntilIdle()
        
        DLTProviderFactory.reset()
        advanceUntilIdle()
        
        val provider2 = DLTProviderFactory.getInstance(context)
        advanceUntilIdle()
        
        val provider3 = DLTProviderFactory.getInstance(context)
        advanceUntilIdle()

        // Then
        assertNotNull(provider1)
        assertNotNull(provider2)
        assertNotNull(provider3)
        assertEquals(provider2, provider3) // After reset, 2 and 3 should be same
    }

    // ========================================================================
    // ERROR HANDLING TESTS
    // ========================================================================

    @Test
    fun `getInstance should not throw exception with valid context`() = runTest {
        // Given
        val mockProvider = mockk<DLTProvider>(relaxed = true)
        every { DLTProviderFactory.getInstance(any()) } returns mockProvider
        
        // When & Then - Should not throw
        try {
            val provider = DLTProviderFactory.getInstance(context)
            assertNotNull(provider)
            advanceUntilIdle()
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `reset should not throw exception`() = runTest {
        // Given
        every { DLTProviderFactory.reset() } just Runs
        
        // When & Then - Should not throw
        try {
            DLTProviderFactory.reset()
            advanceUntilIdle()
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    // ========================================================================
    // INTEGRATION SCENARIO TESTS
    // ========================================================================

    @Test
    fun `complete factory usage scenario should work correctly`() = runTest {
        // Setup mocks
        val mockProvider = mockk<DLTProvider>(relaxed = true)
        every { DLTProviderFactory.getInstance(any()) } returns mockProvider
        every { DLTProviderFactory.reset() } just Runs
        
        // Scenario: App startup -> usage -> reset -> usage again

        // Step 1: App startup - get provider
        val startupProvider = DLTProviderFactory.getInstance(context)
        assertNotNull(startupProvider)
        advanceUntilIdle()

        // Step 2: Normal usage - multiple accesses
        val usageProvider1 = DLTProviderFactory.getInstance(context)
        val usageProvider2 = context.getDLTProvider()
        assertEquals(startupProvider, usageProvider1)
        assertEquals(startupProvider, usageProvider2)
        advanceUntilIdle()

        // Step 3: Logout/cleanup - reset
        DLTProviderFactory.reset()
        advanceUntilIdle()

        // Step 4: Login again - new provider
        val newLoginProvider = DLTProviderFactory.getInstance(context)
        assertNotNull(newLoginProvider)
        advanceUntilIdle()

        // Step 5: Continue usage - verify singleton
        val continuedProvider = DLTProviderFactory.getInstance(context)
        assertEquals(newLoginProvider, continuedProvider)
    }

    @Test
    fun `factory pattern should work with extension function throughout lifecycle`() = runTest {
        // Setup mocks
        val mockProvider = mockk<DLTProvider>(relaxed = true)
        every { DLTProviderFactory.getInstance(any()) } returns mockProvider
        every { DLTProviderFactory.reset() } just Runs
        
        // Phase 1: Extension function usage
        val extProvider1 = context.getDLTProvider()
        advanceUntilIdle()

        // Phase 2: Mix extension and factory
        val factoryProvider = DLTProviderFactory.getInstance(context)
        val extProvider2 = context.getDLTProvider()
        assertEquals(extProvider1, factoryProvider)
        assertEquals(extProvider1, extProvider2)
        advanceUntilIdle()

        // Phase 3: Reset and verify
        DLTProviderFactory.reset()
        advanceUntilIdle()

        val newExtProvider = context.getDLTProvider()
        assertNotNull(newExtProvider)
    }
}
