package com.techducat.ajo.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.techducat.ajo.model.Round
import com.techducat.ajo.model.Round.RoundStatus

@Entity(
    tableName = "rounds",
    foreignKeys = [
        ForeignKey(
            entity = RoscaEntity::class,
            parentColumns = ["id"],
            childColumns = ["rosca_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["rosca_id"]),
        Index(value = ["rosca_id", "round_number"], unique = true),
        Index(value = ["status"]),
        Index(value = ["recipient_member_id"])
    ]
)
data class RoundEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "rosca_id")
    val roscaId: String,
    
    @ColumnInfo(name = "round_number")
    val roundNumber: Int,
    
    @ColumnInfo(name = "recipient_member_id")
    val recipientMemberId: String,
    
    @ColumnInfo(name = "recipient_address")
    val recipientAddress: String,
    
    @ColumnInfo(name = "status")
    val status: String, // ACTIVE, PAYOUT, COMPLETED, FAILED
    
    @ColumnInfo(name = "target_amount")
    val targetAmount: Long, // Expected total contributions
    
    @ColumnInfo(name = "collected_amount")
    val collectedAmount: Long = 0L, // Actual contributions received
    
    @ColumnInfo(name = "expected_contributors")
    val expectedContributors: Int,
    
    @ColumnInfo(name = "actual_contributors")
    val actualContributors: Int = 0,
    
    @ColumnInfo(name = "payout_amount")
    val payoutAmount: Long? = null, // Final amount paid out (may differ from collected due to fees/penalties)
    
    @ColumnInfo(name = "service_fee")
    val serviceFee: Long = 0L,
    
    @ColumnInfo(name = "penalty_amount")
    val penaltyAmount: Long = 0L,
    
    @ColumnInfo(name = "started_at")
    val startedAt: Long,
    
    @ColumnInfo(name = "due_date")
    val dueDate: Long,
    
    @ColumnInfo(name = "payout_initiated_at")
    val payoutInitiatedAt: Long? = null,
    
    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null,
    
    @ColumnInfo(name = "payout_tx_hash")
    val payoutTxHash: String? = null,
    
    @ColumnInfo(name = "payout_tx_id")
    val payoutTxId: String? = null,
    
    @ColumnInfo(name = "payout_confirmations")
    val payoutConfirmations: Int = 0,
    
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "ipfs_hash")
    val ipfsHash: String? = null,
    
    @ColumnInfo(name = "is_dirty")
    val isDirty: Boolean = true,
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        // Round Status Constants
        const val STATUS_ACTIVE = "ACTIVE"           // Contributions being collected
        const val STATUS_PAYOUT = "PAYOUT"           // All contributions in, awaiting payout
        const val STATUS_COMPLETED = "COMPLETED"     // Payout successful
        const val STATUS_FAILED = "FAILED"           // Round failed (not enough contributions, payout failed, etc.)
        const val STATUS_CANCELLED = "CANCELLED"     // Manually cancelled
    }
    
    // Helper functions
    fun isComplete(): Boolean = actualContributors >= expectedContributors
    
    fun isFullyCollected(): Boolean = collectedAmount >= targetAmount
    
    fun canPayout(): Boolean = status == STATUS_PAYOUT && isComplete() && isFullyCollected()
    
    fun isOverdue(): Boolean = System.currentTimeMillis() > dueDate && status == STATUS_ACTIVE
    
    fun getContributionProgress(): Float {
        return if (expectedContributors > 0) {
            (actualContributors.toFloat() / expectedContributors.toFloat()) * 100
        } else 0f
    }
    
    fun getAmountProgress(): Float {
        return if (targetAmount > 0) {
            (collectedAmount.toFloat() / targetAmount.toFloat()) * 100
        } else 0f
    }
}

// Extension functions for converting between Entity and Domain models
fun RoundEntity.toDomain() = Round(
    id = id,
    roscaId = roscaId,
    roundNumber = roundNumber,
    recipientId = recipientMemberId,
    recipientAddress = recipientAddress,
    targetAmount = targetAmount,
    collectedAmount = collectedAmount,
    bidAmount = null,
    status = when (status.uppercase()) {
        RoundEntity.STATUS_ACTIVE -> RoundStatus.CONTRIBUTION
        RoundEntity.STATUS_PAYOUT -> RoundStatus.PAYOUT
        RoundEntity.STATUS_COMPLETED -> RoundStatus.COMPLETED
        RoundEntity.STATUS_FAILED -> RoundStatus.FAILED
        RoundEntity.STATUS_CANCELLED -> RoundStatus.FAILED
        else -> RoundStatus.CONTRIBUTION
    },
    biddingDeadline = null,
    startedAt = startedAt,
    contributionDeadline = dueDate,
    payoutTransactionHash = payoutTxHash ?: payoutTxId,
    completedAt = completedAt
)

fun Round.toEntity() = RoundEntity(
    id = id,
    roscaId = roscaId,
    roundNumber = roundNumber,
    recipientMemberId = recipientId ?: "",
    recipientAddress = recipientAddress ?: "",
    targetAmount = targetAmount,
    collectedAmount = collectedAmount,
    status = when (status) {
        RoundStatus.BIDDING -> RoundEntity.STATUS_ACTIVE
        RoundStatus.CONTRIBUTION -> RoundEntity.STATUS_ACTIVE
        RoundStatus.PAYOUT -> RoundEntity.STATUS_PAYOUT
        RoundStatus.COMPLETED -> RoundEntity.STATUS_COMPLETED
        RoundStatus.FAILED -> RoundEntity.STATUS_FAILED
    },
    expectedContributors = 0,
    actualContributors = 0,
    payoutAmount = if (status == RoundStatus.COMPLETED) collectedAmount else null,
    serviceFee = 0L,
    penaltyAmount = 0L,
    startedAt = startedAt,
    dueDate = contributionDeadline ?: System.currentTimeMillis(),
    payoutInitiatedAt = if (status == RoundStatus.PAYOUT) System.currentTimeMillis() else null,
    completedAt = completedAt,
    payoutTxHash = payoutTransactionHash,
    payoutTxId = payoutTransactionHash,
    payoutConfirmations = if (payoutTransactionHash != null) 1 else 0,
    notes = null,
    ipfsHash = null,
    isDirty = true,
    lastSyncedAt = null,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)
