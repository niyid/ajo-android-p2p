package com.techducat.ajo.dlt

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
import com.techducat.ajo.wallet.WalletSuite
import com.m2049r.xmrwallet.model.TransactionInfo

@ExperimentalCoroutinesApi
class MoneroBlockchainProviderTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockWalletSuite: WalletSuite
    private lateinit var mockIPFSProvider: IPFSProvider
    private lateinit var blockchainProvider: MoneroBlockchainProvider
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        mockWalletSuite = mockk(relaxed = true)
        mockIPFSProvider = mockk(relaxed = true)
        
        every { mockWalletSuite.getCachedAddress() } returns "test_address"
        
        // Add these mocks to prevent hanging
        every { mockWalletSuite.getBalance(any()) } answers {
            firstArg<WalletSuite.BalanceCallback>().onSuccess(1000000000000L, 1000000000000L)
        }
        
        // Fix: Use positional arguments for Java class StateOfSync
        // Constructor: StateOfSync(boolean syncing, long walletHeight, long daemonHeight, double percentDone)
        val mockSyncStatus = WalletSuite.StateOfSync(
            false,      // syncing
            1000L,      // walletHeight
            1000L,      // daemonHeight
            100.0       // percentDone
        )
        every { mockWalletSuite.getStateOfSync() } returns mockSyncStatus
        
        blockchainProvider = MoneroBlockchainProvider(mockWalletSuite, mockIPFSProvider)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `storeRoscaMetadata should succeed with valid parameters`() = runTest {
        val roscaMetadata = RoscaMetadata(
            roscaId = "rosca_123",
            name = "Test ROSCA",
            description = "A test rotating savings group",
            creatorId = "user_1",
            totalMembers = 5,
            contributionAmount = 1000000L,
            contributionFrequency = "monthly",
            payoutOrder = "random",
            startDate = System.currentTimeMillis(),
            memberIds = listOf("user_1", "user_2", "user_3", "user_4", "user_5"),
            multisigAddress = null,
            status = "active",
            createdAt = System.currentTimeMillis(),
            customMetadata = mapOf("purpose" to "emergency_fund")
        )
        val expectedTxHash = "tx_hash_123"

        every { mockWalletSuite.sendTransaction(any(), any(), any()) } answers {
            thirdArg<WalletSuite.TransactionCallback>().onSuccess(expectedTxHash, 0L)
        }

        val result = blockchainProvider.storeRoscaMetadata(roscaMetadata)

        assertTrue(result.isSuccess)
        assertEquals(expectedTxHash, result.getOrNull())
    }

    @Test
    fun `storeRoscaMetadata should fail when wallet transaction fails`() = runTest {
        val roscaMetadata = RoscaMetadata(
            roscaId = "rosca_123",
            name = "Test ROSCA",
            description = "A test rotating savings group",
            creatorId = "user_1",
            totalMembers = 5,
            contributionAmount = 1000000L,
            contributionFrequency = "monthly",
            payoutOrder = "random",
            startDate = System.currentTimeMillis(),
            memberIds = listOf("user_1", "user_2", "user_3", "user_4", "user_5"),
            multisigAddress = null,
            status = "active",
            createdAt = System.currentTimeMillis()
        )

        every { mockWalletSuite.sendTransaction(any(), any(), any()) } answers {
            thirdArg<WalletSuite.TransactionCallback>().onError("Transaction creation failed")
        }

        val result = blockchainProvider.storeRoscaMetadata(roscaMetadata)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Transaction creation failed") == true)
    }

    @Test
    fun `getRoscaMetadata should return metadata when found`() = runTest {
        val roscaId = "rosca_123"
        val expectedMetadata = RoscaMetadata(
            roscaId = roscaId,
            name = "Test ROSCA",
            description = "A test rotating savings group",
            creatorId = "user_1",
            totalMembers = 5,
            contributionAmount = 1000000L,
            contributionFrequency = "monthly",
            payoutOrder = "random",
            startDate = System.currentTimeMillis(),
            memberIds = listOf("user_1", "user_2", "user_3", "user_4", "user_5"),
            multisigAddress = null,
            status = "active",
            createdAt = System.currentTimeMillis()
        )

        val mockTx = TransactionInfo(
            "tx_hash_123",
            1000000L,
            System.currentTimeMillis(),
            10L
        )
        mockTx.notes = """AJO:{"roscaId":"$roscaId","name":"Test ROSCA","description":"A test rotating savings group","creatorId":"user_1","totalMembers":5,"contributionAmount":1000000,"contributionFrequency":"monthly","payoutOrder":"random","startDate":${expectedMetadata.startDate},"memberIds":["user_1","user_2","user_3","user_4","user_5"],"multisigAddress":null,"status":"active","createdAt":${expectedMetadata.createdAt}}"""

        every { mockWalletSuite.getTransactionHistory(any()) } answers {
            firstArg<WalletSuite.TransactionHistoryCallback>().onSuccess(listOf(mockTx))
        }

        val result = blockchainProvider.getRoscaMetadata(roscaId)

        assertTrue(result.isSuccess)
        assertEquals(roscaId, result.getOrNull()?.roscaId)
        assertEquals("Test ROSCA", result.getOrNull()?.name)
    }

    @Test
    fun `getRoscaMetadata should fail when metadata not found`() = runTest {
        val roscaId = "nonexistent_rosca"

        every { mockWalletSuite.getTransactionHistory(any()) } answers {
            firstArg<WalletSuite.TransactionHistoryCallback>().onSuccess(emptyList())
        }

        val result = blockchainProvider.getRoscaMetadata(roscaId)

        assertTrue(result.isFailure)
    }

    @Test
    fun `recordContribution should succeed with valid contribution`() = runTest {
        val contribution = ContributionRecord(
            id = "contrib_123",
            roscaId = "rosca_123",
            memberId = "user_1",
            amount = 1000000L,
            roundNumber = 1,
            dueDate = System.currentTimeMillis(),
            txHash = "previous_tx_hash"
        )
        val expectedTxHash = "contrib_tx_123"

        every { mockWalletSuite.sendTransaction(any(), any(), any()) } answers {
            thirdArg<WalletSuite.TransactionCallback>().onSuccess(expectedTxHash, 0L)
        }

        val result = blockchainProvider.recordContribution(contribution)

        assertTrue(result.isSuccess)
        assertEquals(expectedTxHash, result.getOrNull())
    }

    @Test
    fun `getContributions should return contributions for rosca`() = runTest {
        val roscaId = "rosca_123"
        val contribution = ContributionRecord(
            id = "contrib_123",
            roscaId = roscaId,
            memberId = "user_1",
            amount = 1000000L,
            roundNumber = 1,
            dueDate = System.currentTimeMillis(),
            txHash = "tx_hash_123"
        )

        val mockTx = TransactionInfo("tx_hash_123", 1000000L, System.currentTimeMillis(), 10L)
        mockTx.notes = """AJO:{"type":"contribution","data":"{\"id\":\"${contribution.id}\",\"roscaId\":\"${contribution.roscaId}\",\"memberId\":\"${contribution.memberId}\",\"amount\":${contribution.amount},\"roundNumber\":${contribution.roundNumber},\"dueDate\":${contribution.dueDate},\"txHash\":\"${contribution.txHash}\"}"}"""

        every { mockWalletSuite.getTransactionHistory(any()) } answers {
            firstArg<WalletSuite.TransactionHistoryCallback>().onSuccess(listOf(mockTx))
        }

        val result = blockchainProvider.getContributions(roscaId)

        assertTrue(result.isSuccess)
        val contributions = result.getOrNull()!!
        assertTrue(contributions.isNotEmpty())
        assertEquals(1, contributions.size)
    }

    @Test
    fun `getContributions should return empty list when no contributions found`() = runTest {
        val roscaId = "rosca_123"

        every { mockWalletSuite.getTransactionHistory(any()) } answers {
            firstArg<WalletSuite.TransactionHistoryCallback>().onSuccess(emptyList())
        }

        val result = blockchainProvider.getContributions(roscaId)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }

    @Test
    fun `recordDistribution should succeed with valid distribution`() = runTest {
        val distribution = DistributionRecord(
            id = "dist_123",
            roscaId = "rosca_123",
            recipientId = "user_2",
            recipientAddress = "4ABCD...MoneroAddress...XYZ",
            amount = 5000000L,
            roundNumber = 1,
            dueDate = System.currentTimeMillis(),
            txHash = "previous_tx_hash"
        )
        val expectedTxHash = "dist_tx_123"

        every { mockWalletSuite.sendTransaction(any(), any(), any()) } answers {
            thirdArg<WalletSuite.TransactionCallback>().onSuccess(expectedTxHash, 0L)
        }

        val result = blockchainProvider.recordDistribution(distribution)

        assertTrue(result.isSuccess)
        assertEquals(expectedTxHash, result.getOrNull())
    }

    @Test
    fun `sendTransaction should succeed with valid parameters`() = runTest {
        val recipientAddress = "4ABCD...Recipient...XYZ"
        val amount = 1.0
        val expectedTxHash = "send_tx_123"

        every { mockWalletSuite.sendTransaction(any(), any(), any()) } answers {
            thirdArg<WalletSuite.TransactionCallback>().onSuccess(expectedTxHash, 0L)
        }

        val result = blockchainProvider.sendTransaction(recipientAddress, amount)

        assertTrue(result.isSuccess)
        assertEquals(expectedTxHash, result.getOrNull())
    }

    @Test
    fun `sendTransaction should fail with invalid address`() = runTest {
        val invalidAddress = "invalid_address"
        val amount = 1.0

        every { mockWalletSuite.sendTransaction(any(), any(), any()) } answers {
            thirdArg<WalletSuite.TransactionCallback>().onError("Invalid address")
        }

        val result = blockchainProvider.sendTransaction(invalidAddress, amount)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Invalid address") == true)
    }

    @Test
    fun `getTransactionInfo should return transaction details`() = runTest {
        val txHash = "tx_hash_123"
        val mockTx = TransactionInfo(txHash, 1000000L, System.currentTimeMillis(), 10L)

        every { mockWalletSuite.getTransactionHistory(any()) } answers {
            firstArg<WalletSuite.TransactionHistoryCallback>().onSuccess(listOf(mockTx))
        }

        val result = blockchainProvider.getTransactionInfo(txHash)

        assertNotNull(result)
        assertEquals(txHash, result?.getString("hash"))
        assertEquals(10L, result?.getLong("confirmations"))
    }

    @Test
    fun `getTransactionInfo should return null for non-existent transaction`() = runTest {
        val txHash = "nonexistent_tx"

        every { mockWalletSuite.getTransactionHistory(any()) } answers {
            firstArg<WalletSuite.TransactionHistoryCallback>().onSuccess(emptyList())
        }

        val result = blockchainProvider.getTransactionInfo(txHash)

        assertNull(result)
    }

    @Test
    fun `verifyIntegrity should return true when all transactions are valid`() = runTest {
        every { mockWalletSuite.getTransactionHistory(any()) } answers {
            firstArg<WalletSuite.TransactionHistoryCallback>().onSuccess(emptyList())
        }

        val result = blockchainProvider.verifyIntegrity("rosca_123")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `storeMetadata should succeed with valid parameters`() = runTest {
        val roscaId = "rosca_123"
        val metadataType = "test_metadata"
        val data = "{\"test\": \"data\"}"
        val expectedTxHash = "metadata_tx_123"

        every { mockWalletSuite.sendTransaction(any(), any(), any()) } answers {
            thirdArg<WalletSuite.TransactionCallback>().onSuccess(expectedTxHash, 0L)
        }

        val result = blockchainProvider.storeMetadata(roscaId, metadataType, data)

        assertTrue(result.isSuccess)
        assertEquals(expectedTxHash, result.getOrNull())
    }

    @Test
    fun `getMetadata should return metadata when found`() = runTest {
        val roscaId = "rosca_123"
        val metadataType = "test_type"
        val expectedData = "test_data"

        val mockTx = TransactionInfo("tx_hash_123", 1000000L, System.currentTimeMillis(), 10L)
        mockTx.notes = """AJO:{"type":"$metadataType","roscaId":"$roscaId","data":"$expectedData"}"""

        every { mockWalletSuite.getTransactionHistory(any()) } answers {
            firstArg<WalletSuite.TransactionHistoryCallback>().onSuccess(listOf(mockTx))
        }

        val result = blockchainProvider.getMetadata(roscaId, metadataType)

        assertTrue(result.isSuccess)
        assertEquals(expectedData, result.getOrNull())
    }

    @Test
    fun `complete ROSCA lifecycle should succeed`() = runTest {
        val roscaMetadata = RoscaMetadata(
            roscaId = "test_rosca_123",
            name = "Test ROSCA",
            description = "Test Description",
            creatorId = "creator_1",
            totalMembers = 3,
            contributionAmount = 1000000L,
            contributionFrequency = "monthly",
            payoutOrder = "random",
            startDate = System.currentTimeMillis(),
            memberIds = listOf("user_1", "user_2", "user_3"),
            multisigAddress = null,
            status = "active",
            createdAt = System.currentTimeMillis()
        )

        every { mockWalletSuite.sendTransaction(any(), any(), any()) } answers {
            thirdArg<WalletSuite.TransactionCallback>().onSuccess("tx_hash_123", 0L)
        }

        val mockTx = TransactionInfo("tx_hash_123", 1000000L, System.currentTimeMillis(), 10L)
        mockTx.notes = """AJO:{"roscaId":"test_rosca_123","name":"Test ROSCA","description":"Test Description","creatorId":"creator_1","totalMembers":3,"contributionAmount":1000000,"contributionFrequency":"monthly","payoutOrder":"random","startDate":${roscaMetadata.startDate},"memberIds":["user_1","user_2","user_3"],"multisigAddress":null,"status":"active","createdAt":${roscaMetadata.createdAt}}"""

        every { mockWalletSuite.getTransactionHistory(any()) } answers {
            firstArg<WalletSuite.TransactionHistoryCallback>().onSuccess(listOf(mockTx))
        }

        val storeResult = blockchainProvider.storeRoscaMetadata(roscaMetadata)
        val contribResult = blockchainProvider.recordContribution(
            ContributionRecord("c1", "test_rosca_123", "user_1", 1000000L, 1, System.currentTimeMillis(), "tx1")
        )
        val distResult = blockchainProvider.recordDistribution(
            DistributionRecord("d1", "test_rosca_123", "user_2", "addr", 3000000L, 1, System.currentTimeMillis(), "tx2")
        )

        assertTrue(storeResult.isSuccess)
        assertTrue(contribResult.isSuccess)
        assertTrue(distResult.isSuccess)
    }

    @Test
    fun `createRosca should succeed with valid parameters`() = runTest {
        every { mockWalletSuite.createRosca(any(), any(), any(), any(), any()) } answers {
            lastArg<WalletSuite.RoscaCreationCallback>().onSuccess("rosca_123", "multisig_address", "setup_info")
        }

        val result = blockchainProvider.createRosca("Test ROSCA", 5, 1000000L, 3)

        assertTrue(result.isSuccess)
        assertEquals("rosca_123", result.getOrNull()?.roscaId)
    }

    @Test
    fun `joinRosca should succeed with valid parameters`() = runTest {
        every { mockWalletSuite.joinRosca(any(), any(), any()) } answers {
            lastArg<WalletSuite.RoscaJoinCallback>().onSuccess("rosca_123", "member_multisig_info")
        }

        val result = blockchainProvider.joinRosca("rosca_123", "setup_info_string")

        assertTrue(result.isSuccess)
        assertEquals("member_multisig_info", result.getOrNull())
    }

    @Test
    fun `finalizeRoscaSetup should succeed with valid parameters`() = runTest {
        every { mockWalletSuite.finalizeRoscaSetup(any(), any(), any(), any()) } answers {
            lastArg<WalletSuite.RoscaFinalizeCallback>().onSuccess("rosca_123", "multisig_address", true)
        }

        val result = blockchainProvider.finalizeRoscaSetup("rosca_123", listOf("info1", "info2", "info3"), 2)

        assertTrue(result.isSuccess)
        assertEquals("multisig_address", result.getOrNull()?.multisigAddress)
    }

    @Test
    fun `contributeToRosca should succeed with valid parameters`() = runTest {
        every { mockWalletSuite.contributeToRosca(any(), any(), any(), any()) } answers {
            lastArg<WalletSuite.RoscaContributionCallback>().onSuccess("tx_hash", 1000000L, 1)
        }

        val result = blockchainProvider.contributeToRosca("rosca_123", 1000000L, 1)

        assertTrue(result.isSuccess)
        assertEquals("tx_hash", result.getOrNull()?.txId)
    }

    @Test
    fun `getRoscaState should succeed with valid roscaId`() = runTest {
        val mockState = mockk<WalletSuite.RoscaState>(relaxed = true)
        
        every { mockWalletSuite.getRoscaState(any(), any()) } answers {
            lastArg<WalletSuite.RoscaStateCallback>().onSuccess(mockState)
        }

        val result = blockchainProvider.getRoscaState("rosca_123")

        assertTrue(result.isSuccess)
    }
}
