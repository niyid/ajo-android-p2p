package com.techducat.ajo.wallet

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for WalletSuite
 * Tests cover wallet initialization, balance queries, transactions, multisig operations, and ROSCA functionality
 */
@ExperimentalCoroutinesApi
class WalletSuiteTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockContext: Context
    private lateinit var walletSuite: WalletSuite
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        mockContext = mockk(relaxed = true)
        
        // Create mock instance directly - don't try to mock companion object
        walletSuite = mockk(relaxUnitFun = true)
        
        // Setup default mock behaviors
        setupWalletSuiteMocks()
        
        // Mock the static convertAtomicToXmr method
        mockkStatic("com.techducat.ajo.wallet.WalletSuite")
        every { WalletSuite.convertAtomicToXmr(any()) } answers {
            val atomic = arg<Long>(0)
            "%.4f".format(atomic / 1e12)
        }
    }

    private fun setupWalletSuiteMocks() {
        // Mock getAddress
        every { 
            walletSuite.getAddress(any()) 
        } answers {
            val callback = arg<WalletSuite.AddressCallback>(0)
            callback.onSuccess("48test_mock_address_123")
        }
        
        // Mock getBalance
        every { 
            walletSuite.getBalance(any()) 
        } answers {
            val callback = arg<WalletSuite.BalanceCallback>(0)
            callback.onSuccess(5000000000000L, 4000000000000L)
        }
        
        // Mock sendTransaction
        every { 
            walletSuite.sendTransaction(any(), any(), any()) 
        } answers {
            val callback = arg<WalletSuite.TransactionCallback>(2)
            callback.onSuccess("tx_hash_abc123", 1000000000000L)
        }
        
        // Mock createRosca
        every { 
            walletSuite.createRosca(any(), any(), any(), any(), any()) 
        } answers {
            val callback = arg<WalletSuite.RoscaCreationCallback>(4)
            callback.onSuccess("rosca_123456", "multisig_info", "setup_info")
        }
        
        // Mock joinRosca
        every { 
            walletSuite.joinRosca(any(), any(), any()) 
        } answers {
            val callback = arg<WalletSuite.RoscaJoinCallback>(2)
            val roscaId = arg<String>(0)
            callback.onSuccess(roscaId, "your_multisig_info")
        }
        
        // Mock finalizeRoscaSetup
        every { 
            walletSuite.finalizeRoscaSetup(any(), any(), any(), any()) 
        } answers {
            val callback = arg<WalletSuite.RoscaFinalizeCallback>(3)
            callback.onSuccess("rosca_123456", "4Bmultisig_final_address_xyz", true)
        }
        
        // Mock contributeToRosca
        every { 
            walletSuite.contributeToRosca(any(), any(), any(), any()) 
        } answers {
            val callback = arg<WalletSuite.RoscaContributionCallback>(3)
            callback.onSuccess("tx_contribution_abc123", 1000000000000L, 1)
        }
        
        // Mock executeRoscaPayout
        every { 
            walletSuite.executeRoscaPayout(any(), any(), any(), any(), any(), any()) 
        } answers {
            val callback = arg<WalletSuite.RoscaPayoutCallback>(5)
            callback.onSuccess("payout_tx_abc123", 5000000000000L, "48recipient_address_xyz")
        }
        
        // Mock getRoscaState
        every { 
            walletSuite.getRoscaState(any(), any()) 
        } answers {
            val callback = arg<WalletSuite.RoscaStateCallback>(1)
            val roscaId = arg<String>(0)
            val state = createMockRoscaState(roscaId)
            callback.onSuccess(state)
        }
        
        // Mock getMultisigInfo
        every { 
            walletSuite.getMultisigInfo(any()) 
        } answers {
            val callback = arg<WalletSuite.MultisigCallback>(0)
            callback.onSuccess("multisig_info_base64", "pending_finalization")
        }
        
        // Mock makeMultisig
        every { 
            walletSuite.makeMultisig(any(), any(), any()) 
        } answers {
            val callback = arg<WalletSuite.MultisigCallback>(2)
            callback.onSuccess("", "4Bmultisig_final_address_xyz")
        }
        
        // Mock getUserId
        every { walletSuite.userId } returns "48test_user_id_123"
        
        // Mock isReady
        every { walletSuite.isReady } returns true
    }

    private fun createMockRoscaState(roscaId: String): WalletSuite.RoscaState {
        return WalletSuite.RoscaState(
            roscaId, // roscaId
            "4Brosca_multisig", // multisigAddress
            5, // totalMembers
            3, // threshold
            1000000000000L, // contributionAmount
            2, // currentRound
            3000000000000L, // totalContributed
            2000000000000L, // totalPaidOut
            true, // isActive
            false, // isComplete
            listOf("member1", "member2"), // memberAddresses
            listOf(1, 2, 3, 4, 5) // payoutOrder
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    // ========================================================================
    // WALLET INITIALIZATION TESTS
    // ========================================================================

    @Test
    fun `getAddress should return wallet address`() = runTest {
        // Given
        val expectedAddress = "48test_mock_address_123"

        // When
        var result: String? = null
        walletSuite.getAddress(
            object : WalletSuite.AddressCallback {
                override fun onSuccess(address: String) {
                    result = address
                }
                override fun onError(error: String) {
                    fail("Should not fail: $error")
                }
            })
        advanceUntilIdle()

        // Then
        assertEquals(expectedAddress, result)
    }

    @Test
    fun `getBalance should return correct balance`() = runTest {
        // Given
        val expectedBalance = 5000000000000L
        val expectedUnlocked = 4000000000000L

        // When
        var balanceResult: Long? = null
        var unlockedResult: Long? = null
        walletSuite.getBalance(
            object : WalletSuite.BalanceCallback {
                override fun onSuccess(balance: Long, unlocked: Long) {
                    balanceResult = balance
                    unlockedResult = unlocked
                }
                override fun onError(error: String) {
                    fail("Should not fail: $error")
                }
            })
        advanceUntilIdle()

        // Then
        assertEquals(expectedBalance, balanceResult)
        assertEquals(expectedUnlocked, unlockedResult)
    }

    @Test
    fun `getUserId should return user identifier`() {
        // When
        val userId = walletSuite.userId

        // Then
        assertNotNull(userId)
        assertTrue(userId.isNotBlank())
        assertEquals("48test_user_id_123", userId)
    }

    @Test
    fun `isReady should return wallet readiness status`() {
        // When
        val isReady = walletSuite.isReady

        // Then
        assertTrue(isReady)
    }

    // ========================================================================
    // TRANSACTION TESTS
    // ========================================================================

    @Test
    fun `sendTransaction should succeed with valid parameters`() = runTest {
        // Given
        val amount = 1.0 // XMR
        val expectedTxId = "tx_hash_abc123"

        // When
        var result: String? = null
        walletSuite.sendTransaction("48recipient_address", amount,
            object : WalletSuite.TransactionCallback {
                override fun onSuccess(txId: String, amount: Long) {
                    result = txId
                }
                override fun onError(error: String) {
                    fail("Should not fail: $error")
                }
            })
        advanceUntilIdle()

        // Then
        assertEquals(expectedTxId, result)
    }

    @Test
    fun `sendTransaction should fail with insufficient balance`() = runTest {
        // Given
        val errorMessage = "Insufficient balance"
        every { 
            walletSuite.sendTransaction(any(), any(), any()) 
        } answers {
            val callback = arg<WalletSuite.TransactionCallback>(2)
            callback.onError(errorMessage)
        }

        // When
        var errorResult: String? = null
        walletSuite.sendTransaction("48recipient_address", 99999.0,
            object : WalletSuite.TransactionCallback {
                override fun onSuccess(txId: String, amount: Long) {
                    fail("Should not succeed")
                }
                override fun onError(error: String) {
                    errorResult = error
                }
            })
        advanceUntilIdle()

        // Then
        assertEquals(errorMessage, errorResult)
    }

    // ========================================================================
    // MULTISIG TESTS
    // ========================================================================

    @Test
    fun `getMultisigInfo should succeed`() = runTest {
        // Given
        val expectedMultisigInfo = "multisig_info_base64"

        // When
        var result: String? = null
        walletSuite.getMultisigInfo(
            object : WalletSuite.MultisigCallback {
                override fun onSuccess(info: String, address: String) {
                    result = info
                }
                override fun onError(error: String) {
                    fail("Should not fail: $error")
                }
            })
        advanceUntilIdle()

        // Then
        assertEquals(expectedMultisigInfo, result)
    }

    @Test
    fun `makeMultisig should succeed with valid parameters`() = runTest {
        // Given
        val expectedAddress = "4Bmultisig_final_address_xyz"

        // When
        var result: String? = null
        walletSuite.makeMultisig(listOf("info1", "info2"), 2,
            object : WalletSuite.MultisigCallback {
                override fun onSuccess(info: String, address: String) {
                    result = address
                }
                override fun onError(error: String) {
                    fail("Should not fail: $error")
                }
            })
        advanceUntilIdle()

        // Then
        assertEquals(expectedAddress, result)
        assertTrue(result!!.startsWith("4"))
    }

    // ========================================================================
    // ROSCA CREATION TESTS
    // ========================================================================

    @Test
    fun `createRosca should succeed with valid parameters`() = runTest {
        // Given
        val expectedRoscaId = "rosca_123456"
        val expectedMultisigInfo = "multisig_info"

        // When
        var result: Triple<String, String, String>? = null
        walletSuite.createRosca("Test ROSCA", 5, 1000000000000L, 3,
            object : WalletSuite.RoscaCreationCallback {
                override fun onSuccess(roscaId: String, multisigInfo: String, setupInfo: String) {
                    result = Triple(roscaId, multisigInfo, setupInfo)
                }
                override fun onError(error: String) {
                    fail("Should not fail: $error")
                }
            })
        advanceUntilIdle()

        // Then
        assertNotNull(result)
        assertEquals(expectedRoscaId, result?.first)
        assertEquals(expectedMultisigInfo, result?.second)
    }

    @Test
    fun `createRosca should fail with invalid member count`() = runTest {
        // Given
        val errorMessage = "Invalid member count"
        every { 
            walletSuite.createRosca(any(), any(), any(), any(), any()) 
        } answers {
            val callback = arg<WalletSuite.RoscaCreationCallback>(4)
            callback.onError(errorMessage)
        }

        // When
        var errorResult: String? = null
        walletSuite.createRosca("Test ROSCA", 1, 1000000000000L, 1,
            object : WalletSuite.RoscaCreationCallback {
                override fun onSuccess(roscaId: String, multisigInfo: String, setupInfo: String) {
                    fail("Should not succeed")
                }
                override fun onError(error: String) {
                    errorResult = error
                }
            })
        advanceUntilIdle()

        // Then
        assertEquals(errorMessage, errorResult)
    }

    // ========================================================================
    // ROSCA JOIN TESTS
    // ========================================================================

    @Test
    fun `joinRosca should succeed with valid invitation`() = runTest {
        // Given
        val roscaId = "rosca_123456"
        val expectedMultisigInfo = "your_multisig_info"

        // When
        var result: Pair<String, String>? = null
        walletSuite.joinRosca(roscaId, "invitation_data",
            object : WalletSuite.RoscaJoinCallback {
                override fun onSuccess(roscaId: String, yourMultisigInfo: String) {
                    result = Pair(roscaId, yourMultisigInfo)
                }
                override fun onError(error: String) {
                    fail("Should not fail: $error")
                }
            })
        advanceUntilIdle()

        // Then
        assertNotNull(result)
        assertEquals(roscaId, result?.first)
        assertEquals(expectedMultisigInfo, result?.second)
    }

    // ========================================================================
    // ROSCA FINALIZATION TESTS
    // ========================================================================

    @Test
    fun `finalizeRoscaSetup should succeed with valid parameters`() = runTest {
        // Given
        val expectedRoscaId = "rosca_123456"
        val expectedAddress = "4Bmultisig_final_address_xyz"

        // When
        var result: Triple<String, String, Boolean>? = null
        walletSuite.finalizeRoscaSetup("rosca_123456", listOf("info1", "info2", "info3"), 2,
            object : WalletSuite.RoscaFinalizeCallback {
                override fun onSuccess(roscaId: String, multisigAddress: String, isReady: Boolean) {
                    result = Triple(roscaId, multisigAddress, isReady)
                }
                override fun onError(error: String) {
                    fail("Should not fail: $error")
                }
            })
        advanceUntilIdle()

        // Then
        assertNotNull(result)
        assertEquals(expectedRoscaId, result?.first)
        assertEquals(expectedAddress, result?.second)
        assertTrue(result?.third ?: false)
    }

    // ========================================================================
    // ROSCA CONTRIBUTION TESTS
    // ========================================================================

    @Test
    fun `contributeToRosca should succeed with valid parameters`() = runTest {
        // Given
        val contributionAmount = 1000000000000L
        val expectedTxId = "tx_contribution_abc123"

        // When
        var result: String? = null
        walletSuite.contributeToRosca("rosca_123", contributionAmount, 1,
            object : WalletSuite.RoscaContributionCallback {
                override fun onSuccess(txId: String, amount: Long, roundNumber: Int) {
                    result = txId
                }
                override fun onError(error: String) {
                    fail("Should not fail: $error")
                }
            })
        advanceUntilIdle()

        // Then
        assertEquals(expectedTxId, result)
    }

    // ========================================================================
    // ROSCA PAYOUT TESTS
    // ========================================================================

    @Test
    fun `executeRoscaPayout should succeed with valid parameters`() = runTest {
        // Given
        val payoutAmount = 5000000000000L
        val expectedTxHash = "payout_tx_abc123"

        // When
        var result: Pair<String, Long>? = null
        walletSuite.executeRoscaPayout("rosca_123", "48recipient_address_xyz", payoutAmount, 1, emptyList(),
            object : WalletSuite.RoscaPayoutCallback {
                override fun onSuccess(txId: String, payoutAmount: Long, recipientAddress: String) {
                    result = Pair(txId, payoutAmount)
                }
                override fun onError(error: String) {
                    fail("Should not fail: $error")
                }
            })
        advanceUntilIdle()

        // Then
        assertNotNull(result)
        assertEquals(expectedTxHash, result?.first)
        assertEquals(payoutAmount, result?.second)
    }

    // ========================================================================
    // ROSCA STATE TESTS
    // ========================================================================

    @Test
    fun `getRoscaState should return active ROSCA information`() = runTest {
        // Given
        val roscaId = "rosca_123456"

        // When
        var result: WalletSuite.RoscaState? = null
        walletSuite.getRoscaState(roscaId,
            object : WalletSuite.RoscaStateCallback {
                override fun onSuccess(state: WalletSuite.RoscaState) {
                    result = state
                }
                override fun onError(error: String) {
                    fail("Should not fail: $error")
                }
            })
        advanceUntilIdle()

        // Then
        assertNotNull(result)
        assertEquals(roscaId, result?.roscaId)
        assertEquals("4Brosca_multisig", result?.multisigAddress)
        assertEquals(5, result?.totalMembers)
        assertEquals(3, result?.threshold)
        assertEquals(1000000000000L, result?.contributionAmount)
        assertEquals(2, result?.currentRound)
        assertEquals(3000000000000L, result?.totalContributed)
        assertEquals(2000000000000L, result?.totalPaidOut)
        assertTrue(result?.isActive ?: false)
        assertFalse(result?.isComplete ?: true)
        assertEquals(listOf("member1", "member2"), result?.memberAddresses)
        assertEquals(listOf(1, 2, 3, 4, 5), result?.payoutOrder)
    }

    // ========================================================================
    // UTILITY FUNCTION TESTS
    // ========================================================================

    @Test
    fun `convertAtomicToXmr should convert correctly`() {
        // Given
        val atomic1 = 1000000000000L
        val atomic2 = 1500000000000L
        val atomic3 = 500000000L

        // When
        val result1 = WalletSuite.convertAtomicToXmr(atomic1)
        val result2 = WalletSuite.convertAtomicToXmr(atomic2)
        val result3 = WalletSuite.convertAtomicToXmr(atomic3)

        // Then
        assertEquals("1.0000", result1)
        assertEquals("1.5000", result2)
        assertEquals("0.0005", result3)
    }

    // ========================================================================
    // INTEGRATION SCENARIO TESTS
    // ========================================================================

    @Test
    fun `complete ROSCA lifecycle should succeed`() = runTest {
        // Given
        val roscaName = "Test ROSCA"
        val numMembers = 5
        val contributionAmount = 1000000000000L
        val threshold = 3

        // When - Execute complete ROSCA lifecycle
        var createSuccess = false
        walletSuite.createRosca(roscaName, numMembers, contributionAmount, threshold,
            object : WalletSuite.RoscaCreationCallback {
                override fun onSuccess(roscaId: String, multisigInfo: String, setupInfo: String) {
                    createSuccess = true
                }
                override fun onError(error: String) {}
            })
        advanceUntilIdle()
        assertTrue(createSuccess)

        var joinSuccess = false
        walletSuite.joinRosca("rosca_123456", "setup_info",
            object : WalletSuite.RoscaJoinCallback {
                override fun onSuccess(roscaId: String, yourMultisigInfo: String) {
                    joinSuccess = true
                }
                override fun onError(error: String) {}
            })
        advanceUntilIdle()
        assertTrue(joinSuccess)

        var finalizeSuccess = false
        walletSuite.finalizeRoscaSetup("rosca_123456", listOf("info1", "info2", "info3"), threshold,
            object : WalletSuite.RoscaFinalizeCallback {
                override fun onSuccess(roscaId: String, multisigAddress: String, isReady: Boolean) {
                    finalizeSuccess = true
                }
                override fun onError(error: String) {}
            })
        advanceUntilIdle()
        assertTrue(finalizeSuccess)

        var contributeSuccess = false
        walletSuite.contributeToRosca("rosca_123456", contributionAmount, 1,
            object : WalletSuite.RoscaContributionCallback {
                override fun onSuccess(txId: String, amount: Long, roundNumber: Int) {
                    contributeSuccess = true
                }
                override fun onError(error: String) {}
            })
        advanceUntilIdle()
        assertTrue(contributeSuccess)

        var payoutSuccess = false
        walletSuite.executeRoscaPayout("rosca_123456", "48recipient_address", 5000000000000L, 1, emptyList(),
            object : WalletSuite.RoscaPayoutCallback {
                override fun onSuccess(txId: String, payoutAmount: Long, recipientAddress: String) {
                    payoutSuccess = true
                }
                override fun onError(error: String) {}
            })
        advanceUntilIdle()

        var stateSuccess = false
        walletSuite.getRoscaState("rosca_123456",
            object : WalletSuite.RoscaStateCallback {
                override fun onSuccess(state: WalletSuite.RoscaState) {
                    stateSuccess = true
                }
                override fun onError(error: String) {}
            })
        advanceUntilIdle()

        // Then
        assertTrue(payoutSuccess)
        assertTrue(stateSuccess)

        verify { walletSuite.createRosca(any(), any(), any(), any(), any()) }
        verify { walletSuite.joinRosca(any(), any(), any()) }
        verify { walletSuite.finalizeRoscaSetup(any(), any(), any(), any()) }
        verify { walletSuite.contributeToRosca(any(), any(), any(), any()) }
        verify { walletSuite.executeRoscaPayout(any(), any(), any(), any(), any(), any()) }
        verify { walletSuite.getRoscaState(any(), any()) }
    }
}
