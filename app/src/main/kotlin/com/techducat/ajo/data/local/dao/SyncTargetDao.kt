package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.SyncTargetEntity

@Dao
interface SyncTargetDao {
    @Query("SELECT * FROM sync_targets WHERE roscaId = :roscaId AND syncEnabled = 1")
    suspend fun getEnabledTargets(roscaId: String): List<SyncTargetEntity>
    
    @Query("SELECT * FROM sync_targets WHERE roscaId = :roscaId")
    suspend fun getTargetsByRosca(roscaId: String): List<SyncTargetEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(target: SyncTargetEntity)
    
    @Update
    suspend fun update(target: SyncTargetEntity)
    
    @Query("UPDATE sync_targets SET lastSyncAttempt = :timestamp WHERE id = :id")
    suspend fun updateLastAttempt(id: String, timestamp: Long)
    
    @Query("UPDATE sync_targets SET lastSyncSuccess = :timestamp, consecutiveFailures = 0 WHERE id = :id")
    suspend fun updateLastSuccess(id: String, timestamp: Long)
    
    @Query("UPDATE sync_targets SET consecutiveFailures = consecutiveFailures + 1 WHERE id = :id")
    suspend fun incrementFailures(id: String)
    
    @Query("DELETE FROM sync_targets WHERE roscaId = :roscaId")
    suspend fun deleteByRosca(roscaId: String)
}
