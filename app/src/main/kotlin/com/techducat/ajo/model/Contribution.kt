package com.techducat.ajo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Contribution(
    val id: String = UUID.randomUUID().toString(),
    val contributionId: String = id,  // Alias for compatibility
    var roscaId: String = "",
    var roundNumber: Int = 0,  // Add roundNumber field
    var roundId: String = "",
    var memberId: String = "",
    var amount: Long = 0L,
    var txHash: String? = null,  // Make nullable and rename from transactionHash
    var status: ContributionStatus = ContributionStatus.PENDING,
    var createdAt: Long = System.currentTimeMillis(),
    var confirmedAt: Long? = null,
    var confirmations: Int = 0,
    var blockHeight: Long = 0L,
    var notes: String? = null
) : Parcelable {
    
    enum class ContributionStatus {
        PENDING,
        CONFIRMED,
        FAILED,
        REFUNDED
    }
    
    // Backward compatibility property
    val transactionHash: String?
        get() = txHash
    
    /**
     * Check if contribution is confirmed
     */
    fun isConfirmed(): Boolean {
        return status == ContributionStatus.CONFIRMED
    }
}
