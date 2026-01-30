package com.techducat.ajo.data.local.entity

import androidx.room.*
import org.json.JSONObject

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "payouts")
data class PayoutEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "rosca_id")
    val roscaId: String,
    
    @ColumnInfo(name = "recipient_id")
    val recipientId: String,
    
    @ColumnInfo(name = "round_id")
    val roundId: String? = null,
    
    @ColumnInfo(name = "payout_type")
    val payoutType: String,  // regular, early_exit, final, penalty_adjusted
    
    // Amount Breakdown
    @ColumnInfo(name = "gross_amount")
    val grossAmount: Long,  // Total pool amount
    
    @ColumnInfo(name = "service_fee")
    val serviceFee: Long,  // 5% service fee
    
    @ColumnInfo(name = "penalty_amount")
    val penaltyAmount: Long = 0L,  // Penalty deducted (if any)
    
    @ColumnInfo(name = "net_amount")
    val netAmount: Long,  // Actual amount paid out
    
    // Transaction Details
    @ColumnInfo(name = "tx_hash")
    var txHash: String? = null,  // Monero transaction hash
    
    @ColumnInfo(name = "tx_id")
    var txId: String? = null,  // Monero Payment ID
    
    @ColumnInfo(name = "recipient_address")
    val recipientAddress: String,
    
    // Status Tracking
    @ColumnInfo(name = "status")
    var status: String,
    
    @ColumnInfo(name = "initiated_at")
    val initiatedAt: Long,
    
    @ColumnInfo(name = "completed_at")
    var completedAt: Long? = null,
    
    @ColumnInfo(name = "failed_at")
    var failedAt: Long? = null,
    
    @ColumnInfo(name = "error_message")
    var errorMessage: String? = null,
    
    // Verification
    @ColumnInfo(name = "confirmations")
    var confirmations: Int = 0,
    
    @ColumnInfo(name = "verified_at")
    var verifiedAt: Long? = null,
    
    // Metadata
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    
    @ColumnInfo(name = "updated_at")
    var updatedAt: Long? = null,
    
    // IPFS Integration
    @ColumnInfo(name = "ipfs_hash")
    var ipfsHash: String? = null,
    
    @ColumnInfo(name = "last_synced_at")
    var lastSyncedAt: Long? = null,
    
    @ColumnInfo(name = "is_dirty")
    var isDirty: Boolean = false
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("rosca_id", roscaId)
            put("recipient_id", recipientId)
            put("payout_type", payoutType)
            put("gross_amount", grossAmount)
            put("service_fee", serviceFee)
            put("penalty_amount", penaltyAmount)
            put("net_amount", netAmount)
            put("tx_hash", txHash)
            put("status", status)
            put("recipient_address", recipientAddress)
        }
    }
    
    fun calculateNetAmount(): Long {
        return grossAmount - serviceFee - penaltyAmount
    }
    
    companion object {
        // Payout Types
        const val TYPE_REGULAR = "regular"
        const val TYPE_EARLY_EXIT = "early_exit"
        const val TYPE_FINAL = "final"
        const val TYPE_PENALTY_ADJUSTED = "penalty_adjusted"
        
        // Status
        const val STATUS_PENDING = "pending"
        const val STATUS_PROCESSING = "processing"
        const val STATUS_COMPLETED = "completed"
        const val STATUS_FAILED = "failed"
        const val STATUS_CANCELLED = "cancelled"
    }
}

