package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.RoundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoundDao {
    
    // Insert Operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(round: RoundEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rounds: List<RoundEntity>)
    
    // Update Operations
    @Update
    suspend fun update(round: RoundEntity)
    
    @Query("UPDATE rounds SET status = :status, updated_at = :updatedAt WHERE id = :roundId")
    suspend fun updateStatus(roundId: String, status: String, updatedAt: Long = System.currentTimeMillis())
    
    @Query("""
        UPDATE rounds SET 
            collected_amount = :collectedAmount,
            actual_contributors = :actualContributors,
            updated_at = :updatedAt
        WHERE id = :roundId
    """)
    suspend fun updateContributionProgress(
        roundId: String,
        collectedAmount: Long,
        actualContributors: Int,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE rounds SET 
            status = :status,
            payout_initiated_at = :payoutInitiatedAt,
            updated_at = :updatedAt
        WHERE id = :roundId
    """)
    suspend fun initiatePayout(
        roundId: String,
        status: String,
        payoutInitiatedAt: Long,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE rounds SET 
            status = :status,
            payout_tx_hash = :txHash,
            payout_tx_id = :txId,
            payout_amount = :payoutAmount,
            completed_at = :completedAt,
            updated_at = :updatedAt
        WHERE id = :roundId
    """)
    suspend fun completePayout(
        roundId: String,
        status: String,
        txHash: String,
        txId: String?,
        payoutAmount: Long,
        completedAt: Long,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE rounds SET 
            payout_confirmations = :confirmations,
            updated_at = :updatedAt
        WHERE id = :roundId
    """)
    suspend fun updatePayoutConfirmations(
        roundId: String,
        confirmations: Int,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE rounds SET 
            service_fee = :serviceFee,
            penalty_amount = :penaltyAmount,
            updated_at = :updatedAt
        WHERE id = :roundId
    """)
    suspend fun updateFees(
        roundId: String,
        serviceFee: Long,
        penaltyAmount: Long,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE rounds SET 
            ipfs_hash = :ipfsHash,
            last_synced_at = :lastSyncedAt,
            is_dirty = :isDirty,
            updated_at = :updatedAt
        WHERE id = :roundId
    """)
    suspend fun updateSyncStatus(
        roundId: String,
        ipfsHash: String?,
        lastSyncedAt: Long,
        isDirty: Boolean,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    // Delete Operations
    @Delete
    suspend fun delete(round: RoundEntity)
    
    @Query("DELETE FROM rounds WHERE id = :roundId")
    suspend fun deleteById(roundId: String)
    
    @Query("DELETE FROM rounds WHERE rosca_id = :roscaId")
    suspend fun deleteByRoscaId(roscaId: String)
    
    // Query Operations
    @Query("SELECT * FROM rounds WHERE id = :roundId")
    suspend fun getById(roundId: String): RoundEntity?
    
    @Query("SELECT * FROM rounds WHERE id = :roundId")
    fun observeById(roundId: String): Flow<RoundEntity?>
    
    @Query("SELECT * FROM rounds WHERE rosca_id = :roscaId ORDER BY round_number ASC")
    fun observeByRoscaId(roscaId: String): Flow<List<RoundEntity>>
    
    @Query("SELECT * FROM rounds WHERE rosca_id = :roscaId ORDER BY round_number ASC")
    suspend fun getByRoscaId(roscaId: String): List<RoundEntity>
    
    @Query("SELECT * FROM rounds WHERE rosca_id = :roscaId AND round_number = :roundNumber")
    suspend fun getRoundByNumber(roscaId: String, roundNumber: Int): RoundEntity?
    
    @Query("SELECT * FROM rounds WHERE rosca_id = :roscaId AND round_number = :roundNumber")
    fun observeRoundByNumber(roscaId: String, roundNumber: Int): Flow<RoundEntity?>
    
    @Query("SELECT * FROM rounds WHERE rosca_id = :roscaId AND status = :status ORDER BY round_number ASC")
    suspend fun getRoundsByStatus(roscaId: String, status: String): List<RoundEntity>
    
    @Query("SELECT * FROM rounds WHERE rosca_id = :roscaId AND status = :status ORDER BY round_number ASC")
    fun observeRoundsByStatus(roscaId: String, status: String): Flow<List<RoundEntity>>
    
    @Query("SELECT * FROM rounds WHERE recipient_member_id = :memberId ORDER BY round_number ASC")
    suspend fun getRoundsByRecipient(memberId: String): List<RoundEntity>
    
    @Query("SELECT * FROM rounds WHERE status = :status ORDER BY due_date ASC")
    suspend fun getAllByStatus(status: String): List<RoundEntity>
    
    // Current/Active Round Queries
    @Query("""
        SELECT * FROM rounds 
        WHERE rosca_id = :roscaId 
        AND status IN ('ACTIVE', 'PAYOUT')
        ORDER BY round_number DESC 
        LIMIT 1
    """)
    suspend fun getCurrentRound(roscaId: String): RoundEntity?
    
    @Query("""
        SELECT * FROM rounds 
        WHERE rosca_id = :roscaId 
        AND status IN ('ACTIVE', 'PAYOUT')
        ORDER BY round_number DESC 
        LIMIT 1
    """)
    fun observeCurrentRound(roscaId: String): Flow<RoundEntity?>
    
    @Query("""
        SELECT * FROM rounds 
        WHERE rosca_id = :roscaId 
        AND status = 'ACTIVE'
        ORDER BY round_number DESC 
        LIMIT 1
    """)
    suspend fun getActiveRound(roscaId: String): RoundEntity?
    
    // Aggregate Queries
    @Query("SELECT COUNT(*) FROM rounds WHERE rosca_id = :roscaId")
    suspend fun getRoundCount(roscaId: String): Int
    
    @Query("SELECT COUNT(*) FROM rounds WHERE rosca_id = :roscaId AND status = :status")
    suspend fun getRoundCountByStatus(roscaId: String, status: String): Int
    
    @Query("SELECT SUM(collected_amount) FROM rounds WHERE rosca_id = :roscaId")
    suspend fun getTotalCollected(roscaId: String): Long?
    
    @Query("SELECT SUM(payout_amount) FROM rounds WHERE rosca_id = :roscaId AND status = 'COMPLETED'")
    suspend fun getTotalPaidOut(roscaId: String): Long?
    
    @Query("SELECT SUM(service_fee) FROM rounds WHERE rosca_id = :roscaId")
    suspend fun getTotalServiceFees(roscaId: String): Long?
    
    @Query("SELECT SUM(penalty_amount) FROM rounds WHERE rosca_id = :roscaId")
    suspend fun getTotalPenalties(roscaId: String): Long?
    
    // Overdue Queries
    @Query("""
        SELECT * FROM rounds 
        WHERE status = 'ACTIVE' 
        AND due_date < :currentTime
        ORDER BY due_date ASC
    """)
    suspend fun getOverdueRounds(currentTime: Long = System.currentTimeMillis()): List<RoundEntity>
    
    @Query("""
        SELECT * FROM rounds 
        WHERE rosca_id = :roscaId 
        AND status = 'ACTIVE' 
        AND due_date < :currentTime
    """)
    suspend fun getOverdueRoundsForRosca(
        roscaId: String, 
        currentTime: Long = System.currentTimeMillis()
    ): List<RoundEntity>
    
    // Payout Ready Queries
    @Query("""
        SELECT * FROM rounds 
        WHERE status = 'PAYOUT'
        AND payout_initiated_at IS NULL
        ORDER BY due_date ASC
    """)
    suspend fun getPayoutReadyRounds(): List<RoundEntity>
    
    @Query("""
        SELECT * FROM rounds 
        WHERE rosca_id = :roscaId 
        AND status = 'PAYOUT'
        AND payout_initiated_at IS NULL
    """)
    suspend fun getPayoutReadyRoundsForRosca(roscaId: String): List<RoundEntity>
    
    // Sync Operations
    @Query("SELECT * FROM rounds WHERE is_dirty = 1")
    suspend fun getDirtyRounds(): List<RoundEntity>
    
    @Query("SELECT * FROM rounds WHERE last_synced_at IS NULL OR last_synced_at < :timestamp")
    suspend fun getUnsyncedRounds(timestamp: Long): List<RoundEntity>
    
    @Query("UPDATE rounds SET is_dirty = 0, last_synced_at = :timestamp WHERE id IN (:roundIds)")
    suspend fun markAsSynced(roundIds: List<String>, timestamp: Long)
    
    // Date Range Queries
    @Query("""
        SELECT * FROM rounds 
        WHERE rosca_id = :roscaId 
        AND started_at BETWEEN :startTime AND :endTime
        ORDER BY round_number ASC
    """)
    suspend fun getRoundsByDateRange(roscaId: String, startTime: Long, endTime: Long): List<RoundEntity>
    
    // Statistics Queries
    @Query("""
        SELECT AVG(completed_at - started_at) 
        FROM rounds 
        WHERE rosca_id = :roscaId 
        AND status = 'COMPLETED'
        AND completed_at IS NOT NULL
    """)
    suspend fun getAverageRoundDuration(roscaId: String): Long?
    
    @Query("""
        SELECT AVG(collected_amount) 
        FROM rounds 
        WHERE rosca_id = :roscaId 
        AND status = 'COMPLETED'
    """)
    suspend fun getAverageCollectedAmount(roscaId: String): Long?
}
