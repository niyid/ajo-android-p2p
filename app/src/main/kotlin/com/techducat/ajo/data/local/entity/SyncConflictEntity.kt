package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Conflict resolution state
 * SYNC: NEVER
 * 
 * When version conflicts occur, store them here.
 * In practice, creator's version always wins.
 */
@Entity(tableName = "sync_conflicts")
data class SyncConflictEntity(
    @PrimaryKey 
    val id: String,
    
    val roscaId: String,
    val entityType: String,
    val entityId: String,
    val localVersion: Long,
    val remoteVersion: Long,
    val localPayload: String,         // JSON
    val remotePayload: String,        // JSON
    val detectedAt: Long,
    val resolvedAt: Long? = null,
    val resolution: String? = null    // KEEP_LOCAL, KEEP_REMOTE, CREATOR_WINS
) {
    companion object {
        const val RESOLUTION_KEEP_LOCAL = "KEEP_LOCAL"
        const val RESOLUTION_KEEP_REMOTE = "KEEP_REMOTE"
        const val RESOLUTION_CREATOR_WINS = "CREATOR_WINS"
    }
}
