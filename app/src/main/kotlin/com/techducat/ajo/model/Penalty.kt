package com.techducat.ajo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Penalty(
    val id: String = UUID.randomUUID().toString(),
    val roscaId: String,
    val memberId: String,
    val payoutId: String? = null,
    val penaltyType: PenaltyType,
    val totalContributed: Long,
    val cyclesParticipated: Int,
    val cyclesRemaining: Int,
    val penaltyPercentage: Double,
    val penaltyAmount: Long,
    val reimbursementAmount: Long,
    val calculationMethod: CalculationMethod,
    val reason: String,
    val exitReason: String? = null,
    val status: PenaltyStatus,
    val appliedAt: Long? = null,
    val waivedAt: Long? = null,
    val waivedBy: String? = null,
    val waiverReason: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable {
    
    enum class PenaltyType {
        EARLY_EXIT,
        NON_PAYMENT,
        VIOLATION,
        ADMINISTRATIVE
    }
    
    enum class CalculationMethod {
        PERCENTAGE,
        PRORATED,
        FIXED,
        CUSTOM
    }
    
    enum class PenaltyStatus {
        CALCULATED,
        APPLIED,
        WAIVED,
        DISPUTED
    }
    
    fun isWaived(): Boolean = status == PenaltyStatus.WAIVED && waivedAt != null
    fun isDisputed(): Boolean = status == PenaltyStatus.DISPUTED
    fun calculateReimbursement(): Long = totalContributed - penaltyAmount
}
