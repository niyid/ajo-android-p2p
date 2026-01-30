package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.PeerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PeerDao {
    @Query("SELECT * FROM peers WHERE roscaId = :roscaId")
    suspend fun getPeersByRosca(roscaId: String): List<PeerEntity>
    
    @Query("SELECT * FROM peers WHERE roscaId = :roscaId")
    fun getPeersByRoscaFlow(roscaId: String): Flow<List<PeerEntity>>
    
    @Query("SELECT * FROM peers WHERE nodeId = :nodeId LIMIT 1")
    suspend fun getPeerByNodeId(nodeId: String): PeerEntity?
    
    @Query("SELECT * FROM peers WHERE roscaId = :roscaId AND role = 'CREATOR' LIMIT 1")
    suspend fun getCreatorForRosca(roscaId: String): PeerEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(peer: PeerEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(peers: List<PeerEntity>)
    
    @Update
    suspend fun update(peer: PeerEntity)
    
    @Query("UPDATE peers SET status = :status, lastSeenAt = :timestamp WHERE nodeId = :nodeId")
    suspend fun updatePeerStatus(nodeId: String, status: String, timestamp: Long)
    
    @Delete
    suspend fun delete(peer: PeerEntity)
    
    @Query("DELETE FROM peers WHERE roscaId = :roscaId")
    suspend fun deleteByRosca(roscaId: String)
}
