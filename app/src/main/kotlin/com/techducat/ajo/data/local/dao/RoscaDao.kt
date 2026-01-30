package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.RoscaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoscaDao {
    // ============ Insert Operations ============
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rosca: RoscaEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(roscas: List<RoscaEntity>)
    
    // ============ Update Operations ============
    @Update
    suspend fun update(rosca: RoscaEntity)
    
    @Query("UPDATE roscas SET status = :status, updatedAt = :updatedAt WHERE id = :roscaId")
    suspend fun updateStatus(roscaId: String, status: String, updatedAt: Long)
    
    @Query("UPDATE roscas SET cycleNumber = :cycle, currentRound = :cycle, updatedAt = :updatedAt WHERE id = :roscaId")
    suspend fun updateCycle(roscaId: String, cycle: Int, updatedAt: Long)
    
    @Query("UPDATE roscas SET ipfsHash = :ipfsHash, ipfsCid = :ipfsHash, lastSyncedAt = :timestamp, isDirty = 0 WHERE id = :roscaId")
    suspend fun updateSyncStatus(roscaId: String, ipfsHash: String, timestamp: Long)
    
    // ============ Delete Operations ============
    @Delete
    suspend fun delete(rosca: RoscaEntity)
    
    // ============ Query Operations ============
    
    // Primary queries with multiple method names for compatibility
    @Query("SELECT * FROM roscas WHERE id = :id")
    suspend fun getById(id: String): RoscaEntity?
    
    @Query("SELECT * FROM roscas WHERE id = :id")
    suspend fun getGroupById(id: String): RoscaEntity?
    
    @Query("SELECT * FROM roscas WHERE id = :roscaId")
    suspend fun getRoscaById(roscaId: String): RoscaEntity?
    
    // Get all roscas
    @Query("SELECT * FROM roscas ORDER BY createdAt DESC")
    suspend fun getAllGroups(): List<RoscaEntity>
    
    @Query("SELECT * FROM roscas ORDER BY createdAt DESC")
    suspend fun getAllRoscas(): List<RoscaEntity>
    
    @Query("SELECT * FROM roscas ORDER BY createdAt DESC")
    fun observeAllRoscas(): Flow<List<RoscaEntity>>
    
    // Status-based queries
    @Query("SELECT * FROM roscas WHERE status = :status ORDER BY createdAt DESC")
    suspend fun getRoscasByStatus(status: String): List<RoscaEntity>
    
    @Query("SELECT * FROM roscas WHERE status = :status ORDER BY createdAt DESC")
    fun observeRoscasByStatus(status: String): Flow<List<RoscaEntity>>
    
    @Query("SELECT * FROM roscas WHERE status = 'ACTIVE' ORDER BY createdAt DESC")
    suspend fun getActiveRoscas(): List<RoscaEntity>
    
    @Query("SELECT * FROM roscas WHERE status = 'ACTIVE' ORDER BY createdAt DESC")
    fun observeActiveRoscas(): Flow<List<RoscaEntity>>
    
    @Query("SELECT * FROM roscas WHERE status = 'COMPLETED' ORDER BY completedAt DESC")
    suspend fun getCompletedRoscas(): List<RoscaEntity>
    
    // Creator-based queries
    @Query("SELECT * FROM roscas WHERE creatorId = :creatorId ORDER BY createdAt DESC")
    suspend fun getRoscasByCreator(creatorId: String): List<RoscaEntity>
    
    @Query("SELECT * FROM roscas WHERE creatorId = :creatorId ORDER BY createdAt DESC")
    fun observeRoscasByCreator(creatorId: String): Flow<List<RoscaEntity>>
    
    // Sync queries
    @Query("SELECT * FROM roscas WHERE isDirty = 1")
    suspend fun getDirtyGroups(): List<RoscaEntity>
    
    @Query("SELECT * FROM roscas WHERE isDirty = 1")
    suspend fun getDirtyRoscas(): List<RoscaEntity>
    
    @Query("SELECT * FROM roscas WHERE lastSyncedAt IS NULL OR lastSyncedAt < :timestamp")
    suspend fun getUnsyncedRoscas(timestamp: Long): List<RoscaEntity>
    
    // Count queries
    @Query("SELECT COUNT(*) FROM roscas")
    suspend fun getRoscaCount(): Int
    
    @Query("SELECT COUNT(*) FROM roscas WHERE status = :status")
    suspend fun getRoscaCountByStatus(status: String): Int
    
    @Query("SELECT COUNT(*) FROM roscas WHERE creatorId = :creatorId")
    suspend fun getRoscaCountByCreator(creatorId: String): Int
    
    @Query("DELETE FROM roscas WHERE id = :roscaId")
    suspend fun deleteById(roscaId: String)    
    
    // Search queries
    @Query("SELECT * FROM roscas WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    suspend fun searchRoscas(query: String): List<RoscaEntity>
    
    @Query("SELECT * FROM roscas WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun observeSearchRoscas(query: String): Flow<List<RoscaEntity>>
    
    // Date range queries
    @Query("SELECT * FROM roscas WHERE createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    suspend fun getRoscasByDateRange(startDate: Long, endDate: Long): List<RoscaEntity>
    
    @Query("SELECT * FROM roscas WHERE startDate BETWEEN :startDate AND :endDate ORDER BY startDate ASC")
    suspend fun getRoscasStartingInRange(startDate: Long, endDate: Long): List<RoscaEntity>

    // Add this method to RoscaDao interface
    @Query("""
        SELECT DISTINCT r.* FROM roscas r
        INNER JOIN members m ON r.id = m.roscaId
        WHERE m.userId = :userId AND m.isActive = 1
        ORDER BY r.createdAt DESC
    """)
    suspend fun getRoscasByUser(userId: String): List<RoscaEntity>    
    
}
