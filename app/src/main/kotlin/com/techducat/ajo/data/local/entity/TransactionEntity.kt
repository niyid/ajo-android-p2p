package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    
    // ========== EXISTING FIELDS (PRESERVED) ==========
    var txHash: String?,
    var status: String,
    var confirmations: Int = 0,
    var confirmedAt: Long? = null,
    val createdAt: Long,
    
    // ========== BASIC TRANSACTION FIELDS ==========
    val roscaId: String = "",
    val type: String = "DISTRIBUTION",
    val amount: Long = 0,
    val fromAddress: String? = null,
    val toAddress: String? = null,
    val blockHeight: Long? = null,
    val timestamp: Long = System.currentTimeMillis(),
    
    // ========== MULTISIG FIELDS ==========
    val requiredSignatures: Int = 3,
    val currentSignatureCount: Int = 0,
    
    // ========== P2P SYNC FIELDS ==========
    val syncVersion: Long = 1,
    val lastModifiedBy: String? = null,
    val lastModifiedAt: Long = System.currentTimeMillis()
) {
    companion object {
        // Status constants
        const val STATUS_PENDING_SIGNATURES = "PENDING_SIGNATURES"
        const val STATUS_PENDING_CONFIRMATION = "PENDING_CONFIRMATION"
        const val STATUS_BROADCAST = "BROADCAST"
        const val STATUS_CONFIRMED = "confirmed"
        const val STATUS_FAILED = "failed"
        
        // Transaction type constants
        const val TYPE_CONTRIBUTION = "CONTRIBUTION"
        const val TYPE_DISTRIBUTION = "DISTRIBUTION"
        const val TYPE_PENALTY = "PENALTY"
        const val TYPE_FEE = "FEE"
    }
    
    /**
     * Checks if transaction has enough signatures to broadcast
     */
    fun isReadyToBroadcast(): Boolean = 
        currentSignatureCount >= requiredSignatures
    
    /**
     * Checks if transaction is confirmed on blockchain
     */
    fun isComplete(): Boolean = 
        status == STATUS_CONFIRMED
    
    /**
     * Checks if more signatures are needed
     */
    fun needsMoreSignatures(): Boolean = 
        currentSignatureCount < requiredSignatures
    
    /**
     * Gets human-readable status description
     */
    fun getStatusDescription(): String = when {
        needsMoreSignatures() -> "Collecting signatures ($currentSignatureCount/$requiredSignatures)"
        status == STATUS_PENDING_CONFIRMATION -> "Waiting for blockchain confirmation"
        status == STATUS_BROADCAST -> "Broadcast to network"
        status == STATUS_CONFIRMED -> "Confirmed ($confirmations confirmations)"
        status == STATUS_FAILED -> "Transaction failed"
        else -> status
    }
}
