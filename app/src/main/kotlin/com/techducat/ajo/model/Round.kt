package com.techducat.ajo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Round(
    val id: String = UUID.randomUUID().toString(),
    val roscaId: String,
    val roundNumber: Int,
    val recipientId: String? = null,
    val recipientAddress: String? = null,
    val targetAmount: Long,
    val collectedAmount: Long = 0L,
    val distributedAmount: Long = 0L,  // ADD THIS - used in line 911, 913
    val bidAmount: Long? = null,
    val status: RoundStatus,
    val biddingDeadline: Long? = null,
    val startedAt: Long,
    val contributionDeadline: Long? = null,
    val payoutTransactionHash: String? = null,
    val completedAt: Long? = null
) : Parcelable {
    
    enum class RoundStatus {
        BIDDING,      // For bidding phase
        CONTRIBUTION, // For contribution collection (keep this, used in code)
        PAYOUT,       // Payout initiated
        COMPLETED,    // Payout completed
        FAILED        // Round failed
    }
    
    // Backward compatibility properties for RoscaManager
    val expectedAmount: Long
        get() = targetAmount
    
    val dueDate: Long
        get() = contributionDeadline ?: (startedAt + 7 * 24 * 60 * 60 * 1000L)
}
