package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.DistributionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DistributionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(distribution: DistributionEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(distributions: List<DistributionEntity>)
    
    @Update
    suspend fun update(distribution: DistributionEntity)
    
    @Query("UPDATE distributions SET status = :status, updatedAt = :updatedAt WHERE id = :distributionId")
    suspend fun updateStatus(distributionId: String, status: String, updatedAt: Long = System.currentTimeMillis())
    
    @Query("""
        UPDATE distributions 
        SET txHash = :txHash, txId = :txId, status = :status, confirmedAt = :confirmedAt, updatedAt = :updatedAt
        WHERE id = :distributionId
    """)
    suspend fun completeDistribution(
        distributionId: String,
        txHash: String,
        txId: String?,
        status: String,
        confirmedAt: Long,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    @Delete
    suspend fun delete(distribution: DistributionEntity)
    
    @Query("DELETE FROM distributions WHERE id = :distributionId")
    suspend fun deleteById(distributionId: String)
    
    @Query("DELETE FROM distributions WHERE roscaId = :roscaId")
    suspend fun deleteByRoscaId(roscaId: String)
    
    @Query("SELECT * FROM distributions WHERE id = :id")
    suspend fun getById(id: String): DistributionEntity?
    
    @Query("SELECT * FROM distributions WHERE id = :id")
    fun observeById(id: String): Flow<DistributionEntity?>
    
    @Query("SELECT * FROM distributions WHERE roscaId = :roscaId ORDER BY roundNumber DESC")
    suspend fun getByRoscaId(roscaId: String): List<DistributionEntity>
    
    @Query("SELECT * FROM distributions WHERE roscaId = :roscaId ORDER BY roundNumber DESC")
    fun observeByRoscaId(roscaId: String): Flow<List<DistributionEntity>>
    
    @Query("SELECT * FROM distributions WHERE roundId = :roundId LIMIT 1")
    suspend fun getByRoundId(roundId: String): DistributionEntity?
    
    @Query("SELECT * FROM distributions WHERE roscaId = :roscaId AND roundNumber = :roundNumber LIMIT 1")
    suspend fun getByRound(roscaId: String, roundNumber: Int): DistributionEntity?
    
    @Query("SELECT * FROM distributions WHERE recipientId = :recipientId ORDER BY createdAt DESC")
    suspend fun getByRecipient(recipientId: String): List<DistributionEntity>
    
    @Query("SELECT * FROM distributions WHERE status = :status ORDER BY createdAt ASC")
    suspend fun getByStatus(status: String): List<DistributionEntity>
    
    @Query("SELECT * FROM distributions WHERE txHash = :txHash LIMIT 1")
    suspend fun getByTxHash(txHash: String): DistributionEntity?
    
    @Query("SELECT SUM(amount) FROM distributions WHERE roscaId = :roscaId AND status = 'completed'")
    suspend fun getTotalDistributed(roscaId: String): Long?
    
    @Query("SELECT COUNT(*) FROM distributions WHERE roscaId = :roscaId")
    suspend fun getDistributionCount(roscaId: String): Int
}
