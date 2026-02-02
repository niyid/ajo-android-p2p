package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.techducat.ajo.model.Contribution

@Entity(tableName = "contributions")
data class ContributionEntity(
    @PrimaryKey val id: String,
    val roscaId: String,
    val memberId: String,
    val amount: Long,
    val cycleNumber: Int,
    var status: String,
    val dueDate: Long,
    var txHash: String?,
    var txId: String?,
    var proofOfPayment: String?,
    var paidAt: Long? = null,
    var confirmations: Int = 0,
    var verifiedAt: Long?,
    var notes: String?,
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    var updatedAt: Long? = null,
    var isDirty: Boolean = false,
    var lastSyncedAt: Long? = null,
    var ipfsHash: String? = null
) {
    companion object {
        const val STATUS_PENDING = "pending"
        const val STATUS_PAID = "paid"
        const val STATUS_CONFIRMED = "confirmed"
        const val STATUS_FAILED = "failed"
        const val STATUS_VERIFYING = "verifying"
        const val STATUS_DISPUTED = "disputed"
        const val STATUS_CANCELLED = "cancelled"
    }
}

// Extension functions for converting between Entity and Domain models
fun ContributionEntity.toDomain() = Contribution(
    id = id,
    roundId = roscaId,
    memberId = memberId,
    amount = amount,
    txHash = txHash ?: txId ?: "",
    status = try {
        when (status.uppercase()) {
            "PENDING" -> Contribution.ContributionStatus.PENDING
            "CONFIRMED" -> Contribution.ContributionStatus.CONFIRMED
            "FAILED" -> Contribution.ContributionStatus.FAILED
            else -> Contribution.ContributionStatus.PENDING
        }
    } catch (e: Exception) {
        Contribution.ContributionStatus.PENDING
    },
    createdAt = createdAt
)

fun Contribution.toEntity() = ContributionEntity(
    id = id,
    roscaId = roundId ?: "",
    memberId = memberId,
    amount = amount,
    cycleNumber = 0,
    status = status.name.lowercase(),
    dueDate = createdAt,
    txHash = txHash,
    txId = txHash ?: "",
    proofOfPayment = null,
    verifiedAt = if (status == Contribution.ContributionStatus.CONFIRMED) createdAt else null,
    notes = null,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis(),
    isDirty = false,
    lastSyncedAt = null,
    ipfsHash = null
)
