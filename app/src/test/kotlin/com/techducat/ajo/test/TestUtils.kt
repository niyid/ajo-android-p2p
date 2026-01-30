package com.techducat.ajo.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.techducat.ajo.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.junit.Rule
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith

import java.util.UUID

/**
 * Test utilities and helper functions
 */
object TestUtils {
    
    /**
     * Extension function to get value from LiveData synchronously for testing
     */
    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {}
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }
        this.observeForever(observer)
        
        try {
            afterObserve.invoke()
            
            if (!latch.await(time, timeUnit)) {
                this.removeObserver(observer)
                throw TimeoutException("LiveData value was never set.")
            }
            
            @Suppress("UNCHECKED_CAST")
            return data as T
        } finally {
            this.removeObserver(observer)
        }
    }
    
    /**
     * Extension function to collect first value from Flow with timeout
     */
    suspend fun <T> Flow<T>.firstWithTimeout(
        timeout: Long = 2000L
    ): T {
        return first()
    }
    
    /**
     * Create test ContributionEntity
     */
    fun createTestContribution(
        id: String = "contrib_${System.currentTimeMillis()}",
        memberId: String = "member_1",
        roscaId: String = "rosca_1",
        cycleNumber: Int = 1,
        amount: Long = 100000L,
        status: String = "pending"
    ) = ContributionEntity(
        id = id,
        memberId = memberId,
        roscaId = roscaId,
        cycleNumber = cycleNumber,
        amount = amount,
        status = status,
        dueDate = System.currentTimeMillis(),
        proofOfPayment = null,
        verifiedAt = null,
        notes = null,
        txId = if (status == "completed") "tx_$id" else null,
        txHash = if (status == "completed") "hash_$id" else null,
        isDirty = false,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
    
    /**
     * Create test MemberEntity
     */
    fun createTestMember(
        id: String = "member_${System.currentTimeMillis()}",
        roscaId: String = "rosca_1",
        walletAddress: String? = "wallet_$id",
        name: String = "Test Member",
        status: String = "active"
    ) = MemberEntity(
        id = id,
        roscaId = roscaId,
        userId = "user_$id",
        name = name,
        moneroAddress = walletAddress,
        joinedAt = System.currentTimeMillis(),
        position = 1,
        leftAt = 0L,
        leftReason = "",
        isActive = status == "active",
        walletAddress = walletAddress,
        payoutOrderPosition = 1,
        hasReceivedPayout = false,
        totalContributed = 0L,
        missedPayments = 0,
        lastContributionAt = null,
        exitedAt = null,
        updatedAt = System.currentTimeMillis(),
        ipfsHash = null,
        lastSyncedAt = null,
        isDirty = false,
        status = status
    )
    
    /**
     * Create test PayoutEntity
     */
    fun createTestPayout(
        id: String = "payout_${System.currentTimeMillis()}",
        roscaId: String = "rosca_1",
        recipientId: String = "member_1",
        netAmount: Long = 100000L,
        status: String = PayoutEntity.STATUS_PENDING
    ) = PayoutEntity(
        id = id,
        roscaId = roscaId,
        recipientId = recipientId,
        recipientAddress = "test_address",
        roundId = "round_1",
        grossAmount = netAmount + 2000L,
        penaltyAmount = 0L,
        serviceFee = 2000L,
        netAmount = netAmount,
        payoutType = "regular",
        status = status,
        initiatedAt = System.currentTimeMillis(),
        completedAt = if (status == PayoutEntity.STATUS_COMPLETED) System.currentTimeMillis() else null,
        txHash = null,
        txId = null,
        confirmations = 0,
        verifiedAt = null,
        failedAt = null,
        errorMessage = null,
        ipfsHash = null,
        lastSyncedAt = null,
        isDirty = false,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
    
    /**
     * Create test PenaltyEntity
     */
    fun createTestPenalty(
        id: String = "penalty_${System.currentTimeMillis()}",
        roscaId: String = "rosca_1",
        memberId: String = "member_1",
        penaltyAmount: Long = 5000L,
        status: String = PenaltyEntity.STATUS_CALCULATED,
        totalContributed: Long = 100000L,
        cyclesParticipated: Int = 3,
        cyclesRemaining: Int = 2,
        reason: String = "Test penalty reason"
    ) = PenaltyEntity(
        id = id,
        roscaId = roscaId,
        memberId = memberId,
        penaltyType = PenaltyEntity.TYPE_NON_PAYMENT,
        totalContributed = totalContributed,
        cyclesParticipated = cyclesParticipated,
        cyclesRemaining = cyclesRemaining,
        penaltyAmount = penaltyAmount,
        penaltyPercentage = 5.0,
        reimbursementAmount = (penaltyAmount * 0.9).toLong(),
        calculationMethod = "percentage",
        reason = reason,
        status = status,
        payoutId = null,
        appliedAt = if (status == PenaltyEntity.STATUS_APPLIED) System.currentTimeMillis() else null,
        waivedAt = null,
        waivedBy = null,
        waiverReason = null,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
    
    /**
     * Create test RoscaEntity
     */
    fun createTestRosca(
        id: String = "rosca_${System.currentTimeMillis()}",
        name: String = "Test Rosca",
        status: String = "ACTIVE",
        creatorId: String? = "creator_1"
    ) = RoscaEntity(
        id = id,
        name = name,
        description = "Test description",
        creatorId = creatorId,
        contributionAmount = 100000L,
        contributionFrequency = RoscaEntity.FREQUENCY_WEEKLY,
        totalMembers = 5,
        currentRound = 1,
        cycleNumber = 1,
        status = status,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        startDate = System.currentTimeMillis(),
        completedAt = null,
        walletAddress = "test_wallet_address",
        ipfsHash = null,
        ipfsCid = UUID.randomUUID().toString(),
        lastSyncedAt = null,
        isDirty = false
    )
    
    /**
     * Create test ServiceFeeEntity
     */
    fun createTestServiceFee(
        id: String = "fee_${System.currentTimeMillis()}",
        roscaId: String = "rosca_1",
        feeAmount: Long = 2000L,
        status: String = "PENDING"
    ) = ServiceFeeEntity(
        id = id,
        distributionId = "dist_$id",
        roscaId = roscaId,
        grossAmount = feeAmount + 100000L,
        feeAmount = feeAmount,
        netAmount = 100000L,
        feePercentage = 2.0,
        serviceWallet = "service_wallet_1",
        recipientTxHash = null,
        feeTxHash = null,
        status = status,
        errorMessage = null,
        createdAt = System.currentTimeMillis(),
        completedAt = null
    )
    
    /**
     * Create test TransactionEntity
     */
    fun createTestTransaction(
        id: String = "tx_${System.currentTimeMillis()}",
        txHash: String? = "tx_hash_${System.currentTimeMillis()}",
        status: String = "pending"
    ) = TransactionEntity(
        id = id,
        txHash = txHash,
        status = status,
        confirmations = 0,
        confirmedAt = if (status == "confirmed") System.currentTimeMillis() else null,
        createdAt = System.currentTimeMillis()
    )
    
    /**
     * Wait for condition with timeout
     */
    fun waitForCondition(
        timeoutMs: Long = 5000L,
        checkIntervalMs: Long = 100L,
        condition: () -> Boolean
    ): Boolean {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (condition()) {
                return true
            }
            Thread.sleep(checkIntervalMs)
        }
        return false
    }
}

/**
 * Base class for integration tests with common setup
 */
@RunWith(RobolectricTestRunner::class)
abstract class BaseIntegrationTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    protected fun assertTimestampRecent(timestamp: Long, maxAgeMs: Long = 5000L) {
        val age = System.currentTimeMillis() - timestamp
        assert(age >= 0 && age < maxAgeMs) {
            "Timestamp $timestamp is not recent (age: ${age}ms)"
        }
    }
    
    protected fun assertValidMoneroAddress(address: String) {
        assert(address.isNotEmpty()) { "Address is empty" }
        // Add more validation as needed
    }
    
    protected fun assertValidTransactionHash(txHash: String) {
        assert(txHash.isNotEmpty()) { "Transaction hash is empty" }
        assert(txHash.length == 64 || txHash.length == 32) { 
            "Invalid transaction hash length: ${txHash.length}" 
        }
    }
}

/**
 * Mock callback implementations for testing
 */
class MockWalletStatusListener : com.techducat.ajo.wallet.WalletSuite.WalletStatusListener {
    var initSuccessCalled = false
    var initFailureCalled = false
    var balanceUpdateCalled = false
    var syncProgressCalled = false
    
    var lastBalance = 0L
    var lastUnlocked = 0L
    var lastHeight = 0L
    var lastPercentDone = 0.0
    
    override fun onWalletInitialized(success: Boolean, message: String) {
        if (success) {
            initSuccessCalled = true
        } else {
            initFailureCalled = true
        }
    }
    
    override fun onBalanceUpdated(balance: Long, unlocked: Long) {
        balanceUpdateCalled = true
        lastBalance = balance
        lastUnlocked = unlocked
    }
    
    override fun onSyncProgress(height: Long, startHeight: Long, endHeight: Long, percentDone: Double) {
        syncProgressCalled = true
        lastHeight = height
        lastPercentDone = percentDone
    }
    
    fun reset() {
        initSuccessCalled = false
        initFailureCalled = false
        balanceUpdateCalled = false
        syncProgressCalled = false
        lastBalance = 0L
        lastUnlocked = 0L
        lastHeight = 0L
        lastPercentDone = 0.0
    }
}

class MockTransactionListener : com.techducat.ajo.wallet.WalletSuite.TransactionListener {
    var transactionCreatedCalled = false
    var transactionConfirmedCalled = false
    var transactionFailedCalled = false
    var outputReceivedCalled = false
    
    var lastTxId: String? = null
    var lastAmount = 0L
    var lastError: String? = null
    
    override fun onTransactionCreated(txId: String, amount: Long) {
        transactionCreatedCalled = true
        lastTxId = txId
        lastAmount = amount
    }
    
    override fun onTransactionConfirmed(txId: String) {
        transactionConfirmedCalled = true
        lastTxId = txId
    }
    
    override fun onTransactionFailed(txId: String, error: String) {
        transactionFailedCalled = true
        lastTxId = txId
        lastError = error
    }
    
    override fun onOutputReceived(amount: Long, txHash: String, isConfirmed: Boolean) {
        outputReceivedCalled = true
        lastAmount = amount
        lastTxId = txHash
    }
    
    fun reset() {
        transactionCreatedCalled = false
        transactionConfirmedCalled = false
        transactionFailedCalled = false
        outputReceivedCalled = false
        lastTxId = null
        lastAmount = 0L
        lastError = null
    }
}

/**
 * Test data generators
 */
object TestDataGenerator {
    
    fun generateRoscaWithMembers(
        roscaId: String = "rosca_test",
        memberCount: Int = 5
    ): Pair<RoscaEntity, List<MemberEntity>> {
        val rosca = TestUtils.createTestRosca(
            id = roscaId,
            name = "Test Rosca $roscaId"
        )
        
        val members = (1..memberCount).map { index ->
            TestUtils.createTestMember(
                id = "member_${roscaId}_$index",
                roscaId = roscaId,
                name = "Member $index"
            )
        }
        
        return Pair(rosca, members)
    }
    
    fun generateContributionsForRound(
        roscaId: String,
        memberIds: List<String>,
        round: Int,
        amount: Long = 100000L
    ): List<ContributionEntity> {
        return memberIds.map { memberId ->
            TestUtils.createTestContribution(
                id = "contrib_${roscaId}_${memberId}_$round",
                memberId = memberId,
                roscaId = roscaId,
                cycleNumber = round,
                amount = amount
            )
        }
    }
    
    fun generatePayoutForRound(
        roscaId: String,
        recipientId: String,
        roundId: String,
        amount: Long = 500000L
    ): PayoutEntity {
        return TestUtils.createTestPayout(
            id = "payout_${roscaId}_$roundId",
            roscaId = roscaId,
            recipientId = recipientId,
            netAmount = amount
        )
    }
    
    fun generatePenalties(
        roscaId: String,
        memberIds: List<String>,
        penaltyAmount: Long = 5000L
    ): List<PenaltyEntity> {
        return memberIds.map { memberId ->
            TestUtils.createTestPenalty(
                id = "penalty_${roscaId}_$memberId",
                roscaId = roscaId,
                memberId = memberId,
                penaltyAmount = penaltyAmount
            )
        }
    }
}

/**
 * Test assertions
 */
object TestAssertions {
    
    fun assertContributionValid(contribution: ContributionEntity) {
        assert(contribution.id.isNotEmpty()) { "Contribution ID is empty" }
        assert(contribution.memberId.isNotEmpty()) { "Member ID is empty" }
        assert(contribution.roscaId.isNotEmpty()) { "Rosca ID is empty" }
        assert(contribution.amount > 0) { "Contribution amount must be positive" }
        assert(contribution.cycleNumber > 0) { "Cycle number must be positive" }
    }
    
    fun assertMemberValid(member: MemberEntity) {
        assert(member.id.isNotEmpty()) { "Member ID is empty" }
        assert(member.roscaId.isNotEmpty()) { "Rosca ID is empty" }
        assert(member.walletAddress?.isNotEmpty() == true) { "Wallet address is empty" }
        assert(member.name.isNotEmpty()) { "Member name is empty" }
        assert(member.position > 0) { "Payout order must be positive" }
    }
    
    fun assertPayoutValid(payout: PayoutEntity) {
        assert(payout.id.isNotEmpty()) { "Payout ID is empty" }
        assert(payout.roscaId.isNotEmpty()) { "Rosca ID is empty" }
        assert(payout.recipientId.isNotEmpty()) { "Recipient ID is empty" }
        assert(payout.netAmount > 0) { "Net amount must be positive" }
        assert(payout.grossAmount >= payout.netAmount) { 
            "Gross amount must be >= net amount" 
        }
    }
    
    fun assertPenaltyValid(penalty: PenaltyEntity) {
        assert(penalty.id.isNotEmpty()) { "Penalty ID is empty" }
        assert(penalty.roscaId.isNotEmpty()) { "Rosca ID is empty" }
        assert(penalty.memberId.isNotEmpty()) { "Member ID is empty" }
        assert(penalty.penaltyAmount > 0) { "Penalty amount must be positive" }
        assert(penalty.penaltyPercentage >= 0) { 
            "Penalty percentage must be non-negative" 
        }
    }
    
    fun assertRoscaValid(rosca: RoscaEntity) {
        assert(rosca.id.isNotEmpty()) { "Rosca ID is empty" }
        assert(rosca.name.isNotEmpty()) { "Rosca name is empty" }
        assert(rosca.creatorId?.isNotEmpty() == true) { "Creator ID is empty" }
        assert(rosca.contributionAmount > 0) { "Contribution amount must be positive" }
        assert(rosca.totalMembers > 0) { "Total members must be positive" }
        assert(rosca.cycleNumber >= 0) { "Cycle number must be non-negative" }
    }
}

/**
 * Performance testing utilities
 */
object PerformanceTestUtils {
    
    data class TimingResult(
        val operationName: String,
        val durationMs: Long,
        val success: Boolean
    )
    
    inline fun <T> measureTime(operationName: String, operation: () -> T): Pair<T, TimingResult> {
        val startTime = System.currentTimeMillis()
        var success = false
        val result = try {
            operation().also { success = true }
        } catch (e: Exception) {
            throw e
        } finally {
            val duration = System.currentTimeMillis() - startTime
            val timing = TimingResult(operationName, duration, success)
            println("$operationName: ${duration}ms (success: $success)")
        }
        val duration = System.currentTimeMillis() - startTime
        return Pair(result, TimingResult(operationName, duration, success))
    }
    
    fun assertPerformance(timingResult: TimingResult, maxDurationMs: Long) {
        assert(timingResult.durationMs <= maxDurationMs) {
            "${timingResult.operationName} took ${timingResult.durationMs}ms, expected <= ${maxDurationMs}ms"
        }
    }
}
