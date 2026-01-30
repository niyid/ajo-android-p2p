package com.techducat.ajo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Payout(
    val id: String = UUID.randomUUID().toString(),
    val roscaId: String,
    val recipientId: String,
    val roundId: String? = null,
    val payoutType: PayoutType,
    val grossAmount: Long,
    val serviceFee: Long,
    val penaltyAmount: Long = 0L,
    val netAmount: Long,
    val txHash: String? = null,
    val txId: String? = null,
    val recipientAddress: String,
    val status: PayoutStatus,
    val initiatedAt: Long,
    val completedAt: Long? = null,
    val failedAt: Long? = null,
    val errorMessage: String? = null,
    val confirmations: Int = 0,
    val verifiedAt: Long? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable {
    
    enum class PayoutType {
        REGULAR,
        EARLY_EXIT,
        FINAL,
        PENALTY_ADJUSTED
    }
    
    enum class PayoutStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED
    }
    
    fun isCompleted(): Boolean = status == PayoutStatus.COMPLETED
    fun hasPenalty(): Boolean = penaltyAmount > 0
    fun hasSufficientConfirmations(): Boolean = confirmations >= 10
}
