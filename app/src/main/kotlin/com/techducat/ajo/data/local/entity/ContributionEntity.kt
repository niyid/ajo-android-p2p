package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

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
    var paidAt: Long? = null,  // Missing field
    var confirmations: Int = 0,  // Missing field
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
        const val STATUS_PAID = "paid"  // Added - used in DAO
        const val STATUS_CONFIRMED = "confirmed"
        const val STATUS_FAILED = "failed"
        const val STATUS_VERIFYING = "verifying"
        const val STATUS_DISPUTED = "disputed"
        const val STATUS_CANCELLED = "cancelled"  // Added - used in DAO
    }
}
