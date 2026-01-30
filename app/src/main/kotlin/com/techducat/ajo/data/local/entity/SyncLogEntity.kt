package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

/**
 * Sync log - history of sync operations (for debugging)
 * SYNC: NEVER
 */
@Entity(
    tableName = "sync_log",
    indices = [Index("roscaId"), Index("timestamp")]
)
data class SyncLogEntity(
    @PrimaryKey(autoGenerate = true) 
    val id: Long = 0,
    
    val roscaId: String,
    val direction: String,            // OUTBOUND, INBOUND
    val peerNodeId: String,
    val entityType: String,
    val entityId: String,
    val operation: String,            // INSERT, UPDATE, DELETE
    val status: String,               // SUCCESS, FAILED, CONFLICT
    val timestamp: Long,
    val errorMessage: String? = null,
    
    // Performance metrics
    val durationMs: Long? = null,
    val payloadSize: Int? = null
) {
    companion object {
        const val DIRECTION_OUTBOUND = "OUTBOUND"
        const val DIRECTION_INBOUND = "INBOUND"
        
        const val STATUS_SUCCESS = "SUCCESS"
        const val STATUS_FAILED = "FAILED"
        const val STATUS_CONFLICT = "CONFLICT"
    }
}
