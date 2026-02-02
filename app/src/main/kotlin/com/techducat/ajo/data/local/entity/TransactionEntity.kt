package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.techducat.ajo.model.Transaction

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val roscaId: String,
    val roundNumber: Int,
    var txHash: String?,
    val amount: Long,
    val toAddress: String?,
    val fromAddress: String?,
    var status: String,
    val requiredSignatures: Int,
    val currentSignatureCount: Int,
    var confirmations: Int = 0,
    val createdAt: Long,
    val broadcastAt: Long? = null,
    var confirmedAt: Long? = null,
    val syncVersion: Int,
    val lastModifiedBy: String,
    val lastModifiedAt: Long
) {
    companion object {
        const val STATUS_PENDING_SIGNATURES = "pending_signatures"
        const val STATUS_BROADCAST = "broadcast"
        const val STATUS_CONFIRMED = "confirmed"
        const val STATUS_FAILED = "failed"
    }
}

// Extension functions for converting between Entity and Domain models
fun TransactionEntity.toDomain() = Transaction(
    id = id,
    roscaId = roscaId,
    roundNumber = roundNumber,
    txHash = txHash,
    amount = amount,
    toAddress = toAddress,
    fromAddress = fromAddress,
    status = when (status) {
        TransactionEntity.STATUS_PENDING_SIGNATURES -> Transaction.TransactionStatus.PENDING_SIGNATURES
        TransactionEntity.STATUS_BROADCAST -> Transaction.TransactionStatus.BROADCAST
        TransactionEntity.STATUS_CONFIRMED -> Transaction.TransactionStatus.CONFIRMED
        else -> Transaction.TransactionStatus.FAILED
    },
    requiredSignatures = requiredSignatures,
    currentSignatureCount = currentSignatureCount,
    confirmations = confirmations,
    createdAt = createdAt,
    broadcastAt = broadcastAt,
    confirmedAt = confirmedAt
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    roscaId = roscaId,
    roundNumber = roundNumber,
    txHash = txHash,
    amount = amount,
    toAddress = toAddress,
    fromAddress = fromAddress,
    status = status.name,
    requiredSignatures = requiredSignatures,
    currentSignatureCount = currentSignatureCount,
    confirmations = confirmations,
    createdAt = createdAt,
    broadcastAt = broadcastAt,
    confirmedAt = confirmedAt,
    syncVersion = 1,
    lastModifiedBy = "",
    lastModifiedAt = System.currentTimeMillis()
)
