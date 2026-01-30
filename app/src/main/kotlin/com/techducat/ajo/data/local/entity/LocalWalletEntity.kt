package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ForeignKey

/**
 * Local wallet files - NEVER SYNCS
 * SYNC: NEVER (contains paths to secrets)
 * 
 * This stores metadata about wallet files on THIS device only.
 * The wallet files themselves contain private keys and MUST NOT sync.
 * 
 * Other members see:
 * - Public wallet address (in MemberEntity.publicWalletAddress)
 * - Multisig info (if coordinating)
 * 
 * Other members DO NOT see:
 * - File paths, private keys, passwords, view keys
 */
@Entity(
    tableName = "local_wallets",
    foreignKeys = [
        ForeignKey(
            entity = RoscaEntity::class,
            parentColumns = ["id"],
            childColumns = ["roscaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("roscaId"), Index("nodeId")]
)
data class LocalWalletEntity(
    @PrimaryKey 
    val id: String,
    
    val roscaId: String,
    val nodeId: String,               // Owner (usually local node)
    val walletPath: String,           // Absolute path to .keys file
    val cacheFilePath: String? = null,
    val passwordEncrypted: String,    // Encrypted with Keystore
    val createdAt: Long,
    val lastAccessedAt: Long? = null,
    val isMultisig: Boolean = false,
    val multisigInfo: String? = null, // JSON with multisig setup state
    
    // Optional metadata
    val label: String? = null
) {
    companion object {
        const val TAG = "LocalWalletEntity"
    }
}
