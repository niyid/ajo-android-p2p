package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ForeignKey

/**
 * Sync targets - determines who this device syncs with
 * SYNC: NEVER (local routing policy)
 * 
 * Rules:
 * - If my role = MEMBER → target = CREATOR
 * - If my role = CREATOR → targets = ALL MEMBERS
 */
@Entity(
    tableName = "sync_targets",
    foreignKeys = [
        ForeignKey(
            entity = PeerEntity::class,
            parentColumns = ["id"],
            childColumns = ["targetPeerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("roscaId"), Index("targetPeerId")]
)
data class SyncTargetEntity(
    @PrimaryKey 
    val id: String,
    
    val roscaId: String,
    val targetPeerId: String,         // Which peer to sync with
    val syncEnabled: Boolean = true,
    val lastSyncAttempt: Long? = null,
    val lastSyncSuccess: Long? = null,
    
    // Retry tracking
    val consecutiveFailures: Int = 0
) {
    fun needsSync(): Boolean = syncEnabled && (lastSyncAttempt == null || 
        System.currentTimeMillis() - (lastSyncAttempt ?: 0) > 60000) // 1 min
}
