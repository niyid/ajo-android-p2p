package com.techducat.ajo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Bid(
    val id: String = UUID.randomUUID().toString(),
    val roscaId: String,  // Changed from roundId
    val roundNumber: Int,  // Added
    val roundId: String = "",  // Keep for backward compatibility
    val memberId: String,
    val bidAmount: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val status: BidStatus = BidStatus.PENDING
) : Parcelable

@Parcelize
enum class BidStatus : Parcelable {
    PENDING,
    WINNING,
    LOSING,
    CANCELLED
}
