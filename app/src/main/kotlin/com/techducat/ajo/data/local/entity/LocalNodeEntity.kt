package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local node identity - represents THIS device's P2P identity
 * SYNC: NEVER (device-local only, contains encrypted private key)
 * 
 * Created once on first app launch. The nodeId is derived from the public key,
 * making it tamper-proof.
 */
@Entity(tableName = "local_node")
data class LocalNodeEntity(
    @PrimaryKey 
    val nodeId: String,              // Derived from public key (Base58)
    
    val publicKey: String,            // Ed25519 public key (hex encoded)
    val privateKeyEncrypted: String,  // Encrypted with Android Keystore
    val createdAt: Long,
    val lastSyncAt: Long? = null,
    
    // Optional metadata
    val displayName: String? = null,
    val deviceInfo: String? = null
) {
    companion object {
        const val TAG = "LocalNodeEntity"
    }
}
