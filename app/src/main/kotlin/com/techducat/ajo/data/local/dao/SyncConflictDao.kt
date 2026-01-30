package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.SyncConflictEntity

@Dao
interface SyncConflictDao {
    @Query("SELECT * FROM sync_conflicts WHERE resolvedAt IS NULL")
    suspend fun getUnresolvedConflicts(): List<SyncConflictEntity>
    
    @Query("SELECT * FROM sync_conflicts WHERE roscaId = :roscaId AND resolvedAt IS NULL")
    suspend fun getUnresolvedForRosca(roscaId: String): List<SyncConflictEntity>
    
    @Insert
    suspend fun insert(conflict: SyncConflictEntity)
    
    @Query("UPDATE sync_conflicts SET resolvedAt = :timestamp, resolution = :resolution WHERE id = :id")
    suspend fun markResolved(id: String, timestamp: Long, resolution: String)
    
    @Query("DELETE FROM sync_conflicts WHERE resolvedAt IS NOT NULL AND resolvedAt < :cutoffTime")
    suspend fun deleteResolvedOlderThan(cutoffTime: Long)
}
