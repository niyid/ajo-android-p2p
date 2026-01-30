package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ForeignKey

/**
 * Peer registry - other nodes we know about in ROSCAs
 * SYNC: Partial (public keys sync, endpoints don't)
 * 
 * Written when:
 * - Creator creates ROSCA (self-entry)
 * - Member consumes referral (creator entry)
 * - Creator accepts member (member entry synced to others)
 */
@Entity(
    tableName = "peers",
    indices = [
        Index("nodeId", unique = true),
        Index("roscaId")
    ]
)
data class PeerEntity(
    @PrimaryKey 
    val id: String,                  // peer_<nodeId>
    
    val nodeId: String,               // Unique node identifier
    val roscaId: String,              // Which ROSCA this peer belongs to
    val publicKey: String,            // Ed25519 public key for verification
    val role: String,                 // CREATOR, MEMBER
    val endpoint: String? = null,     // I2P/Tor address (local config, not synced)
    val status: String = "ACTIVE",    // ACTIVE, OFFLINE, EXITED
    val addedAt: Long,
    val lastSeenAt: Long? = null,
    
    // Optional metadata
    val displayName: String? = null
) {
    companion object {
        const val ROLE_CREATOR = "CREATOR"
        const val ROLE_MEMBER = "MEMBER"
        
        const val STATUS_ACTIVE = "ACTIVE"
        const val STATUS_OFFLINE = "OFFLINE"
        const val STATUS_EXITED = "EXITED"
    }
}
