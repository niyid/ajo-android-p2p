package com.techducat.ajo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Distribution(
    val id: String = UUID.randomUUID().toString(),
    val roscaId: String,
    val roundId: String,
    val roundNumber: Int,
    val recipientId: String,
    val recipientAddress: String,
    val amount: Long,
    val txHash: String? = null,
    val status: DistributionStatus = DistributionStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val confirmedAt: Long? = null
) : Parcelable {
    
    enum class DistributionStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}
