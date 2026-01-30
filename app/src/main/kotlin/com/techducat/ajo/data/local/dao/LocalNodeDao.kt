package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.LocalNodeEntity

@Dao
interface LocalNodeDao {
    @Query("SELECT * FROM local_node LIMIT 1")
    suspend fun getLocalNode(): LocalNodeEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(node: LocalNodeEntity)
    
    @Update
    suspend fun update(node: LocalNodeEntity)
    
    @Query("UPDATE local_node SET lastSyncAt = :timestamp WHERE nodeId = :nodeId")
    suspend fun updateLastSyncTime(nodeId: String, timestamp: Long)
    
    @Query("DELETE FROM local_node")
    suspend fun deleteAll()
}
