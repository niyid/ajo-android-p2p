package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.SyncLogEntity

@Dao
interface SyncLogDao {
    @Query("SELECT * FROM sync_log WHERE roscaId = :roscaId ORDER BY timestamp DESC LIMIT 100")
    suspend fun getRecentLogs(roscaId: String): List<SyncLogEntity>
    
    @Query("SELECT * FROM sync_log WHERE status = 'FAILED' ORDER BY timestamp DESC LIMIT 50")
    suspend fun getFailedSyncs(): List<SyncLogEntity>
    
    @Query("SELECT * FROM sync_log WHERE roscaId = :roscaId AND status = :status ORDER BY timestamp DESC")
    suspend fun getLogsByStatus(roscaId: String, status: String): List<SyncLogEntity>
    
    @Insert
    suspend fun insert(log: SyncLogEntity)
    
    @Query("DELETE FROM sync_log WHERE timestamp < :cutoffTime")
    suspend fun deleteOldLogs(cutoffTime: Long)
    
    @Query("DELETE FROM sync_log WHERE roscaId = :roscaId")
    suspend fun deleteByRosca(roscaId: String)
}
