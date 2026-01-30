package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.ContributionEntity
import kotlinx.coroutines.flow.Flow

/**
 * ContributionDao - Manages individual member contributions to ROSCA rounds
 * 
 * Architecture:
 * - Each contribution is tied to a specific Round (via roundId or cycleNumber)
 * - Multiple contributions make up a Round
 * - When all contributions for a round are complete, the round moves to PAYOUT status
 */
@Dao
interface ContributionDao {
    
    // ========== Insert Operations ==========
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contribution: ContributionEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contributions: List<ContributionEntity>)
    
    // ========== Update Operations ==========
    
    @Update
    suspend fun update(contribution: ContributionEntity)
    
    @Query("""
        UPDATE contributions 
        SET status = :status, updated_at = :updatedAt 
        WHERE id = :contributionId
    """)
    suspend fun updateStatus(
        contributionId: String, 
        status: String, 
        updatedAt: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE contributions 
        SET 
            txHash = :txHash,
            txId = :txId,
            status = :status,
            paidAt = :paidAt,
            updated_at = :updatedAt
        WHERE id = :contributionId
    """)
    suspend fun completeContribution(
        contributionId: String,
        txHash: String,
        txId: String?,
        status: String,
        paidAt: Long,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE contributions 
        SET 
            confirmations = :confirmations,
            verifiedAt = :verifiedAt,
            updated_at = :updatedAt
        WHERE id = :contributionId
    """)
    suspend fun updateConfirmations(
        contributionId: String,
        confirmations: Int,
        verifiedAt: Long?,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE contributions 
        SET 
            ipfsHash = :ipfsHash,
            lastSyncedAt = :lastSyncedAt,
            isDirty = :isDirty,
            updated_at = :updatedAt
        WHERE id = :contributionId
    """)
    suspend fun updateSyncStatus(
        contributionId: String,
        ipfsHash: String?,
        lastSyncedAt: Long,
        isDirty: Boolean,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    // ========== Delete Operations ==========
    
    @Delete
    suspend fun delete(contribution: ContributionEntity)
    
    @Query("DELETE FROM contributions WHERE id = :contributionId")
    suspend fun deleteById(contributionId: String)
    
    @Query("DELETE FROM contributions WHERE roscaId = :roscaId")
    suspend fun deleteByRoscaId(roscaId: String)
    
    // ========== Query Operations ==========
    
    @Query("SELECT * FROM contributions WHERE id = :id")
    suspend fun getContributionById(id: String): ContributionEntity?
    
    @Query("SELECT * FROM contributions WHERE id = :id")
    fun observeContributionById(id: String): Flow<ContributionEntity?>
    
    // Get contribution by member and round/cycle
    @Query("""
        SELECT * FROM contributions 
        WHERE memberId = :memberId 
        AND roscaId = :roscaId 
        AND cycleNumber = :cycleNumber
        LIMIT 1
    """)
    suspend fun getContributionByMemberAndCycle(
        memberId: String, 
        roscaId: String, 
        cycleNumber: Int
    ): ContributionEntity?
    
    // All contributions for a ROSCA
    @Query("SELECT * FROM contributions WHERE roscaId = :roscaId ORDER BY dueDate DESC")
    suspend fun getContributionsByRoscaSync(roscaId: String): List<ContributionEntity>
    
    @Query("SELECT * FROM contributions WHERE roscaId = :roscaId ORDER BY dueDate DESC")
    fun getContributionsByRosca(roscaId: String): Flow<List<ContributionEntity>>
    
    // All contributions for a specific round/cycle
    @Query("""
        SELECT * FROM contributions 
        WHERE roscaId = :roscaId 
        AND cycleNumber = :cycleNumber
        ORDER BY memberId ASC
    """)
    suspend fun getContributionsByRound(roscaId: String, cycleNumber: Int): List<ContributionEntity>
    
    @Query("""
        SELECT * FROM contributions 
        WHERE roscaId = :roscaId 
        AND cycleNumber = :cycleNumber
        ORDER BY memberId ASC
    """)
    fun observeContributionsByRound(roscaId: String, cycleNumber: Int): Flow<List<ContributionEntity>>
    
    // Contributions by member
    @Query("""
        SELECT * FROM contributions 
        WHERE memberId = :memberId 
        AND roscaId = :roscaId
        ORDER BY cycleNumber DESC
    """)
    suspend fun getByMemberAndRosca(memberId: String, roscaId: String): List<ContributionEntity>
    
    @Query("""
        SELECT * FROM contributions 
        WHERE memberId = :memberId 
        AND roscaId = :roscaId
        ORDER BY cycleNumber DESC
    """)
    fun observeByMemberAndRosca(memberId: String, roscaId: String): Flow<List<ContributionEntity>>
    
    // ========== Status-based Queries ==========
    
    @Query("SELECT * FROM contributions WHERE status = 'pending' ORDER BY dueDate ASC")
    suspend fun getPendingContributions(): List<ContributionEntity>
    
    @Query("SELECT * FROM contributions WHERE status = 'pending' ORDER BY dueDate ASC")
    fun observePendingContributions(): Flow<List<ContributionEntity>>
    
    @Query("""
        SELECT * FROM contributions 
        WHERE roscaId = :roscaId 
        AND status = :status
        ORDER BY dueDate ASC
    """)
    suspend fun getContributionsByStatus(roscaId: String, status: String): List<ContributionEntity>
    
    @Query("""
        SELECT * FROM contributions 
        WHERE roscaId = :roscaId 
        AND cycleNumber = :cycleNumber 
        AND status = :status
    """)
    suspend fun getContributionsByRoundAndStatus(
        roscaId: String, 
        cycleNumber: Int, 
        status: String
    ): List<ContributionEntity>
    
    // ========== Transaction Queries ==========
    
    @Query("SELECT * FROM contributions WHERE txId = :txId LIMIT 1")
    suspend fun getByTxId(txId: String): ContributionEntity?
    
    @Query("SELECT * FROM contributions WHERE txHash = :txHash LIMIT 1")
    suspend fun getByTxHash(txHash: String): ContributionEntity?
    
    @Query("""
        SELECT * FROM contributions 
        WHERE txHash IS NOT NULL 
        AND verifiedAt IS NULL
        AND status = 'paid'
        ORDER BY paidAt ASC
    """)
    suspend fun getUnverifiedContributions(): List<ContributionEntity>
    
    @Query("""
        SELECT * FROM contributions 
        WHERE status = 'paid' 
        AND confirmations < :requiredConfirmations
        AND txHash IS NOT NULL
        ORDER BY paidAt ASC
    """)
    suspend fun getPendingVerification(requiredConfirmations: Int = 10): List<ContributionEntity>
    
    // ========== Sync Operations ==========
    
    @Query("SELECT * FROM contributions WHERE isDirty = 1 ORDER BY updated_at ASC")
    suspend fun getDirtyContributions(): List<ContributionEntity>
    
    @Query("""
        SELECT * FROM contributions 
        WHERE lastSyncedAt IS NULL 
        OR lastSyncedAt < :timestamp
        ORDER BY updated_at ASC
    """)
    suspend fun getUnsyncedContributions(timestamp: Long): List<ContributionEntity>
    
    @Query("""
        UPDATE contributions 
        SET isDirty = 0, lastSyncedAt = :timestamp, updated_at = :timestamp
        WHERE id IN (:contributionIds)
    """)
    suspend fun markAsSynced(contributionIds: List<String>, timestamp: Long)
    
    // ========== Aggregate Queries ==========
    
    // Total contributions for a ROSCA
    @Query("""
        SELECT SUM(amount) 
        FROM contributions 
        WHERE roscaId = :roscaId 
        AND status = 'paid'
    """)
    suspend fun getTotalContributedAmount(roscaId: String): Long?
    
    // Total contributions for a specific round
    @Query("""
        SELECT SUM(amount) 
        FROM contributions 
        WHERE roscaId = :roscaId 
        AND cycleNumber = :cycleNumber 
        AND status = 'paid'
    """)
    suspend fun getTotalForRound(roscaId: String, cycleNumber: Int): Long?
    
    // Count of paid contributions for a round
    @Query("""
        SELECT COUNT(*) 
        FROM contributions 
        WHERE roscaId = :roscaId 
        AND cycleNumber = :cycleNumber 
        AND status = 'paid'
    """)
    suspend fun getPaidContributionCountForRound(roscaId: String, cycleNumber: Int): Int
    
    // Total expected contributions for a round
    @Query("""
        SELECT COUNT(*) 
        FROM contributions 
        WHERE roscaId = :roscaId 
        AND cycleNumber = :cycleNumber
    """)
    suspend fun getTotalContributionCountForRound(roscaId: String, cycleNumber: Int): Int
    
    // Check if round is complete (all contributions paid)
    @Query("""
        SELECT COUNT(*) = 0
        FROM contributions 
        WHERE roscaId = :roscaId 
        AND cycleNumber = :cycleNumber 
        AND status != 'paid'
    """)
    suspend fun isRoundComplete(roscaId: String, cycleNumber: Int): Boolean
    
    // Member contribution statistics
    @Query("""
        SELECT COUNT(*) 
        FROM contributions 
        WHERE memberId = :memberId 
        AND roscaId = :roscaId 
        AND status = 'paid'
    """)
    suspend fun getMemberPaidContributionCount(memberId: String, roscaId: String): Int
    
    @Query("""
        SELECT SUM(amount) 
        FROM contributions 
        WHERE memberId = :memberId 
        AND roscaId = :roscaId 
        AND status = 'paid'
    """)
    suspend fun getMemberTotalContributed(memberId: String, roscaId: String): Long?
    
    // ========== Overdue Queries ==========
    
    @Query("""
        SELECT * FROM contributions 
        WHERE status = 'pending' 
        AND dueDate < :currentTime
        ORDER BY dueDate ASC
    """)
    suspend fun getOverdueContributions(currentTime: Long = System.currentTimeMillis()): List<ContributionEntity>
    
    @Query("""
        SELECT * FROM contributions 
        WHERE roscaId = :roscaId 
        AND status = 'pending' 
        AND dueDate < :currentTime
        ORDER BY dueDate ASC
    """)
    suspend fun getOverdueContributionsForRosca(
        roscaId: String, 
        currentTime: Long = System.currentTimeMillis()
    ): List<ContributionEntity>
    
    @Query("""
        SELECT * FROM contributions 
        WHERE roscaId = :roscaId 
        AND cycleNumber = :cycleNumber 
        AND status = 'pending' 
        AND dueDate < :currentTime
    """)
    suspend fun getOverdueContributionsForRound(
        roscaId: String, 
        cycleNumber: Int,
        currentTime: Long = System.currentTimeMillis()
    ): List<ContributionEntity>
    
    // ========== Date Range Queries ==========
    
    @Query("""
        SELECT * FROM contributions 
        WHERE roscaId = :roscaId 
        AND dueDate BETWEEN :startTime AND :endTime
        ORDER BY dueDate ASC
    """)
    suspend fun getContributionsByDateRange(
        roscaId: String, 
        startTime: Long, 
        endTime: Long
    ): List<ContributionEntity>
    
    // ========== Cleanup Operations ==========
    
    @Query("""
        DELETE FROM contributions 
        WHERE status = 'cancelled' 
        AND updated_at < :timestamp
    """)
    suspend fun deleteOldCancelledContributions(timestamp: Long)
}
