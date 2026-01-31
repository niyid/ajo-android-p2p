package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    
    // ========== YOUR EXISTING FIELDS ==========
    var txHash: String?,
    var status: String,
    var confirmations: Int = 0,
    var confirmedAt: Long? = null,
    val createdAt: Long,
    
    // ========== MISSING BASIC FIELDS (ADD THESE) ==========
    val roscaId: String = "",                      // Which ROSCA
    val type: String = "DISTRIBUTION",             // CONTRIBUTION, DISTRIBUTION, etc
    val amount: Long = 0,                          // Amount in atomic units
    val fromAddress: String? = null,               // Sender address
    val toAddress: String? = null,                 // Recipient address
    val blockHeight: Long? = null,                 // Block number
    val timestamp: Long = System.currentTimeMillis(),
    
    // ========== P2P MULTISIG FIELDS (ADD THESE) ==========
    val requiredSignatures: Int = 3,               // How many sigs needed (2-of-3, 3-of-3, etc)
    val syncVersion: Long = 1,                     // For conflict resolution
    val lastModifiedBy: String? = null,            // Which node made last change
    val lastModifiedAt: Long = 0                   // When last modified
)
