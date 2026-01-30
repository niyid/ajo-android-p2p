package com.techducat.ajo.data.local.entity

import androidx.room.*
import org.json.JSONObject

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "penalties")
data class PenaltyEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "rosca_id")
    val roscaId: String,
    
    @ColumnInfo(name = "member_id")
    val memberId: String,
    
    @ColumnInfo(name = "payout_id")
    val payoutId: String? = null,
    
    @ColumnInfo(name = "penalty_type")
    val penaltyType: String,
    
    // Penalty Calculation
    @ColumnInfo(name = "total_contributed")
    val totalContributed: Long,
    
    @ColumnInfo(name = "cycles_participated")
    val cyclesParticipated: Int,
    
    @ColumnInfo(name = "cycles_remaining")
    val cyclesRemaining: Int,
    
    @ColumnInfo(name = "penalty_percentage")
    val penaltyPercentage: Double,
    
    @ColumnInfo(name = "penalty_amount")
    val penaltyAmount: Long,
    
    @ColumnInfo(name = "reimbursement_amount")
    val reimbursementAmount: Long,
    
    @ColumnInfo(name = "calculation_method")
    val calculationMethod: String,
    
    // Penalty Reason
    @ColumnInfo(name = "reason")
    val reason: String,
    
    @ColumnInfo(name = "exit_reason")
    val exitReason: String? = null,
    
    // Status
    @ColumnInfo(name = "status")
    var status: String,
    
    @ColumnInfo(name = "applied_at")
    var appliedAt: Long? = null,
    
    @ColumnInfo(name = "waived_at")
    var waivedAt: Long? = null,
    
    @ColumnInfo(name = "waived_by")
    var waivedBy: String? = null,
    
    @ColumnInfo(name = "waiver_reason")
    var waiverReason: String? = null,
    
    // Metadata
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    
    @ColumnInfo(name = "updated_at")
    var updatedAt: Long? = null
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("rosca_id", roscaId)
            put("member_id", memberId)
            put("penalty_type", penaltyType)
            put("total_contributed", totalContributed)
            put("cycles_participated", cyclesParticipated)
            put("cycles_remaining", cyclesRemaining)
            put("penalty_percentage", penaltyPercentage)
            put("penalty_amount", penaltyAmount)
            put("reimbursement_amount", reimbursementAmount)
            put("reason", reason)
            put("status", status)
        }
    }
    
    fun calculateReimbursement(): Long {
        return totalContributed - penaltyAmount
    }
    
    fun isWaived(): Boolean {
        return status == STATUS_WAIVED && waivedAt != null
    }
    
    companion object {
        // Penalty Types
        const val TYPE_EARLY_EXIT = "early_exit"
        const val TYPE_NON_PAYMENT = "non_payment"
        const val TYPE_VIOLATION = "violation"
        const val TYPE_ADMINISTRATIVE = "administrative"
        
        // Calculation Methods
        const val METHOD_PERCENTAGE = "percentage"
        const val METHOD_PRORATED = "prorated"
        const val METHOD_FIXED = "fixed"
        const val METHOD_CUSTOM = "custom"
        
        // Status
        const val STATUS_CALCULATED = "calculated"
        const val STATUS_APPLIED = "applied"
        const val STATUS_WAIVED = "waived"
        const val STATUS_DISPUTED = "disputed"
    }
}
