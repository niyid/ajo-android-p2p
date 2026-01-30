package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.PayoutEntity
import kotlinx.coroutines.flow.Flow

/**
 * PayoutDao - Manages payout records for ROSCA rounds
 * 
 * Note: Payouts are linked to Rounds. Each Round has one Payout when distributed.
 * The Round tracks the overall contribution cycle, while Payout tracks the distribution details.
 */
@Dao
interface PayoutDao {
    
    // ========== Insert Operations ==========
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payout: PayoutEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(payouts: List<PayoutEntity>)
    
    // ========== Update Operations ==========
    
    @Update
    suspend fun update(payout: PayoutEntity)
    
    @Query("""
        UPDATE payouts SET 
            status = :status, 
            updated_at = :updatedAt 
        WHERE id = :payoutId
    """)
    suspend fun updateStatus(
        payoutId: String, 
        status: String, 
        updatedAt: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE payouts SET 
            tx_hash = :txHash, 
            tx_id = :txId, 
            status = :status, 
            completed_at = :completedAt, 
            updated_at = :updatedAt 
        WHERE id = :payoutId
    """)
    suspend fun completeTransaction(
        payoutId: String,
        txHash: String,
        txId: String?,
        status: String,
        completedAt: Long,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE payouts SET 
            confirmations = :confirmations, 
            verified_at = :verifiedAt, 
            updated_at = :updatedAt 
        WHERE id = :payoutId
    """)
    suspend fun updateConfirmations(
        payoutId: String,
        confirmations: Int,
        verifiedAt: Long?,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE payouts SET 
            status = :status, 
            failed_at = :failedAt, 
            error_message = :errorMessage, 
            updated_at = :updatedAt 
        WHERE id = :payoutId
    """)
    suspend fun markAsFailed(
        payoutId: String,
        status: String,
        failedAt: Long,
        errorMessage: String?,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE payouts SET 
            ipfs_hash = :ipfsHash, 
            last_synced_at = :lastSyncedAt, 
            is_dirty = :isDirty,
            updated_at = :updatedAt
        WHERE id = :payoutId
    """)
    suspend fun updateSyncStatus(
        payoutId: String,
        ipfsHash: String?,
        lastSyncedAt: Long,
        isDirty: Boolean,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    // ========== Delete Operations ==========
    
    @Delete
    suspend fun delete(payout: PayoutEntity)
    
    @Query("DELETE FROM payouts WHERE id = :payoutId")
    suspend fun deleteById(payoutId: String)
    
    @Query("DELETE FROM payouts WHERE rosca_id = :roscaId")
    suspend fun deleteByRoscaId(roscaId: String)
    
    @Query("DELETE FROM payouts WHERE round_id = :roundId")
    suspend fun deleteByRoundId(roundId: String)
    
    // ========== Query Operations ==========
    
    @Query("SELECT * FROM payouts WHERE id = :payoutId")
    suspend fun getById(payoutId: String): PayoutEntity?
    
    @Query("SELECT * FROM payouts WHERE id = :payoutId")
    fun observeById(payoutId: String): Flow<PayoutEntity?>
    
    // Get payout for a specific round (should be unique)
    @Query("SELECT * FROM payouts WHERE round_id = :roundId LIMIT 1")
    suspend fun getByRoundId(roundId: String): PayoutEntity?
    
    @Query("SELECT * FROM payouts WHERE round_id = :roundId LIMIT 1")
    fun observeByRoundId(roundId: String): Flow<PayoutEntity?>
    
    // All payouts for a ROSCA
    @Query("SELECT * FROM payouts WHERE rosca_id = :roscaId ORDER BY initiated_at DESC")
    suspend fun getByRoscaId(roscaId: String): List<PayoutEntity>
    
    @Query("SELECT * FROM payouts WHERE rosca_id = :roscaId ORDER BY initiated_at DESC")
    fun observeByRoscaId(roscaId: String): Flow<List<PayoutEntity>>
    
    // Payouts to a specific recipient
    @Query("SELECT * FROM payouts WHERE recipient_id = :recipientId ORDER BY initiated_at DESC")
    suspend fun getByRecipientId(recipientId: String): List<PayoutEntity>
    
    @Query("SELECT * FROM payouts WHERE recipient_id = :recipientId ORDER BY initiated_at DESC")
    fun observeByRecipientId(recipientId: String): Flow<List<PayoutEntity>>
    
    // Payouts by status
    @Query("SELECT * FROM payouts WHERE status = :status ORDER BY initiated_at DESC")
    suspend fun getByStatus(status: String): List<PayoutEntity>
    
    @Query("SELECT * FROM payouts WHERE status = :status ORDER BY initiated_at DESC")
    fun observeByStatus(status: String): Flow<List<PayoutEntity>>
    
    // Combined queries
    @Query("SELECT * FROM payouts WHERE rosca_id = :roscaId AND status = :status ORDER BY initiated_at DESC")
    suspend fun getByRoscaAndStatus(roscaId: String, status: String): List<PayoutEntity>
    
    @Query("SELECT * FROM payouts WHERE payout_type = :payoutType ORDER BY initiated_at DESC")
    suspend fun getByPayoutType(payoutType: String): List<PayoutEntity>
    
    // ========== Transaction Queries ==========
    
    @Query("SELECT * FROM payouts WHERE tx_hash = :txHash LIMIT 1")
    suspend fun getByTxHash(txHash: String): PayoutEntity?
    
    @Query("SELECT * FROM payouts WHERE tx_id = :txId LIMIT 1")
    suspend fun getByTxId(txId: String): PayoutEntity?
    
    // Pending verification (transactions that need confirmation tracking)
    @Query("""
        SELECT * FROM payouts 
        WHERE status = :status 
        AND confirmations < :requiredConfirmations
        AND tx_hash IS NOT NULL
        ORDER BY initiated_at ASC
    """)
    suspend fun getPendingVerification(
        status: String = PayoutEntity.STATUS_PROCESSING,
        requiredConfirmations: Int = 10
    ): List<PayoutEntity>
    
    @Query("""
        SELECT * FROM payouts 
        WHERE tx_hash IS NOT NULL 
        AND verified_at IS NULL
        ORDER BY initiated_at ASC
    """)
    suspend fun getUnverifiedTransactions(): List<PayoutEntity>
    
    // ========== Aggregate Queries ==========
    
    @Query("""
        SELECT SUM(net_amount) 
        FROM payouts 
        WHERE rosca_id = :roscaId 
        AND status = :status
    """)
    suspend fun getTotalPayoutsByRosca(
        roscaId: String, 
        status: String = PayoutEntity.STATUS_COMPLETED
    ): Long?
    
    @Query("SELECT SUM(penalty_amount) FROM payouts WHERE rosca_id = :roscaId")
    suspend fun getTotalPenaltiesByRosca(roscaId: String): Long?
    
    @Query("""
        SELECT SUM(service_fee) 
        FROM payouts 
        WHERE rosca_id = :roscaId 
        AND status = :status
    """)
    suspend fun getTotalServiceFeesByRosca(
        roscaId: String, 
        status: String = PayoutEntity.STATUS_COMPLETED
    ): Long?
    
    @Query("""
        SELECT SUM(net_amount) 
        FROM payouts 
        WHERE recipient_id = :recipientId 
        AND status = :status
    """)
    suspend fun getTotalPayoutsByRecipient(
        recipientId: String, 
        status: String = PayoutEntity.STATUS_COMPLETED
    ): Long?
    
    @Query("SELECT COUNT(*) FROM payouts WHERE rosca_id = :roscaId")
    suspend fun getPayoutCountByRosca(roscaId: String): Int
    
    @Query("SELECT COUNT(*) FROM payouts WHERE rosca_id = :roscaId AND status = :status")
    suspend fun getPayoutCountByRoscaAndStatus(roscaId: String, status: String): Int
    
    // ========== Failed Payout Queries ==========
    
    @Query("""
        SELECT * FROM payouts 
        WHERE status = :status 
        AND failed_at IS NOT NULL 
        ORDER BY failed_at DESC
    """)
    suspend fun getFailedPayouts(status: String = PayoutEntity.STATUS_FAILED): List<PayoutEntity>
    
    @Query("""
        SELECT * FROM payouts 
        WHERE rosca_id = :roscaId 
        AND status = :status
        ORDER BY failed_at DESC
    """)
    suspend fun getFailedPayoutsByRosca(
        roscaId: String, 
        status: String = PayoutEntity.STATUS_FAILED
    ): List<PayoutEntity>
    
    // ========== Sync Operations ==========
    
    @Query("SELECT * FROM payouts WHERE is_dirty = 1 ORDER BY updated_at ASC")
    suspend fun getDirtyPayouts(): List<PayoutEntity>
    
    @Query("""
        SELECT * FROM payouts 
        WHERE last_synced_at IS NULL 
        OR last_synced_at < :timestamp
        ORDER BY updated_at ASC
    """)
    suspend fun getUnsyncedPayouts(timestamp: Long): List<PayoutEntity>
    
    @Query("""
        UPDATE payouts 
        SET is_dirty = 0, last_synced_at = :timestamp, updated_at = :timestamp
        WHERE id IN (:payoutIds)
    """)
    suspend fun markAsSynced(payoutIds: List<String>, timestamp: Long)
    
    // ========== Date Range Queries ==========
    
    @Query("""
        SELECT * FROM payouts 
        WHERE rosca_id = :roscaId 
        AND initiated_at BETWEEN :startTime AND :endTime 
        ORDER BY initiated_at DESC
    """)
    suspend fun getPayoutsByDateRange(
        roscaId: String, 
        startTime: Long, 
        endTime: Long
    ): List<PayoutEntity>
    
    @Query("""
        SELECT * FROM payouts 
        WHERE initiated_at BETWEEN :startTime AND :endTime 
        ORDER BY initiated_at DESC
    """)
    fun observePayoutsByDateRange(startTime: Long, endTime: Long): Flow<List<PayoutEntity>>
    
    // ========== Cleanup Operations ==========
    
    @Query("""
        DELETE FROM payouts 
        WHERE status = :status 
        AND failed_at < :timestamp
    """)
    suspend fun deleteOldFailedPayouts(
        status: String = PayoutEntity.STATUS_FAILED, 
        timestamp: Long
    )
    
    // ========== Utility Queries ==========
    
    // Get the most recent payout for a ROSCA
    @Query("""
        SELECT * FROM payouts 
        WHERE rosca_id = :roscaId 
        ORDER BY initiated_at DESC 
        LIMIT 1
    """)
    suspend fun getLatestPayout(roscaId: String): PayoutEntity?
    
    // Check if a round already has a payout
    @Query("SELECT COUNT(*) FROM payouts WHERE round_id = :roundId")
    suspend fun hasPayoutForRound(roundId: String): Int
    
    // Get successful payouts count for statistics
    @Query("""
        SELECT COUNT(*) FROM payouts 
        WHERE rosca_id = :roscaId 
        AND status = :status
    """)
    suspend fun getSuccessfulPayoutCount(
        roscaId: String,
        status: String = PayoutEntity.STATUS_COMPLETED
    ): Int
}
