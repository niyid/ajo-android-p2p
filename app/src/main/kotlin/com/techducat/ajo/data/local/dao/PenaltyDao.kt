package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.PenaltyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PenaltyDao {
    
    // Insert Operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(penalty: PenaltyEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(penalties: List<PenaltyEntity>)
    
    // Update Operations
    @Update
    suspend fun update(penalty: PenaltyEntity)
    
    @Query("UPDATE penalties SET status = :status, updated_at = :updatedAt WHERE id = :penaltyId")
    suspend fun updateStatus(penaltyId: String, status: String, updatedAt: Long)
    
    @Query("UPDATE penalties SET status = :status, applied_at = :appliedAt, updated_at = :updatedAt WHERE id = :penaltyId")
    suspend fun markAsApplied(
        penaltyId: String,
        status: String = PenaltyEntity.STATUS_APPLIED,
        appliedAt: Long,
        updatedAt: Long
    )
    
    @Query("UPDATE penalties SET status = :status, waived_at = :waivedAt, waived_by = :waivedBy, waiver_reason = :waiverReason, updated_at = :updatedAt WHERE id = :penaltyId")
    suspend fun markAsWaived(
        penaltyId: String,
        status: String = PenaltyEntity.STATUS_WAIVED,
        waivedAt: Long,
        waivedBy: String,
        waiverReason: String?,
        updatedAt: Long
    )
    
    @Query("UPDATE penalties SET payout_id = :payoutId, updated_at = :updatedAt WHERE id = :penaltyId")
    suspend fun linkToPayout(penaltyId: String, payoutId: String, updatedAt: Long)
    
    // Delete Operations
    @Delete
    suspend fun delete(penalty: PenaltyEntity)
    
    @Query("DELETE FROM penalties WHERE id = :penaltyId")
    suspend fun deleteById(penaltyId: String)
    
    // Query Operations
    @Query("SELECT * FROM penalties WHERE id = :penaltyId")
    suspend fun getById(penaltyId: String): PenaltyEntity?
    
    @Query("SELECT * FROM penalties WHERE id = :penaltyId")
    fun observeById(penaltyId: String): Flow<PenaltyEntity?>
    
    @Query("SELECT * FROM penalties WHERE rosca_id = :roscaId ORDER BY created_at DESC")
    fun observeByRoscaId(roscaId: String): Flow<List<PenaltyEntity>>
    
    @Query("SELECT * FROM penalties WHERE rosca_id = :roscaId ORDER BY created_at DESC")
    suspend fun getByRoscaId(roscaId: String): List<PenaltyEntity>
    
    @Query("SELECT * FROM penalties WHERE member_id = :memberId ORDER BY created_at DESC")
    fun observeByMemberId(memberId: String): Flow<List<PenaltyEntity>>
    
    @Query("SELECT * FROM penalties WHERE member_id = :memberId ORDER BY created_at DESC")
    suspend fun getByMemberId(memberId: String): List<PenaltyEntity>
    
    @Query("SELECT * FROM penalties WHERE rosca_id = :roscaId AND member_id = :memberId ORDER BY created_at DESC")
    suspend fun getByRoscaAndMember(roscaId: String, memberId: String): List<PenaltyEntity>
    
    @Query("SELECT * FROM penalties WHERE rosca_id = :roscaId AND member_id = :memberId ORDER BY created_at DESC")
    fun observeByRoscaAndMember(roscaId: String, memberId: String): Flow<List<PenaltyEntity>>
    
    @Query("SELECT * FROM penalties WHERE payout_id = :payoutId")
    suspend fun getByPayoutId(payoutId: String): PenaltyEntity?
    
    @Query("SELECT * FROM penalties WHERE status = :status ORDER BY created_at DESC")
    fun observeByStatus(status: String): Flow<List<PenaltyEntity>>
    
    @Query("SELECT * FROM penalties WHERE status = :status ORDER BY created_at DESC")
    suspend fun getByStatus(status: String): List<PenaltyEntity>
    
    @Query("SELECT * FROM penalties WHERE penalty_type = :penaltyType ORDER BY created_at DESC")
    suspend fun getByPenaltyType(penaltyType: String): List<PenaltyEntity>
    
    @Query("SELECT * FROM penalties WHERE rosca_id = :roscaId AND status = :status")
    suspend fun getByRoscaAndStatus(roscaId: String, status: String): List<PenaltyEntity>
    
    @Query("SELECT * FROM penalties WHERE member_id = :memberId AND status = :status")
    suspend fun getByMemberAndStatus(memberId: String, status: String): List<PenaltyEntity>
    
    // Aggregate Queries
    @Query("SELECT SUM(penalty_amount) FROM penalties WHERE rosca_id = :roscaId AND status = :status")
    suspend fun getTotalPenaltiesByRosca(
        roscaId: String,
        status: String = PenaltyEntity.STATUS_APPLIED
    ): Long?
    
    @Query("SELECT SUM(penalty_amount) FROM penalties WHERE member_id = :memberId AND status = :status")
    suspend fun getTotalPenaltiesByMember(
        memberId: String,
        status: String = PenaltyEntity.STATUS_APPLIED
    ): Long?
    
    @Query("SELECT SUM(reimbursement_amount) FROM penalties WHERE rosca_id = :roscaId AND status = :status")
    suspend fun getTotalReimbursementsByRosca(
        roscaId: String,
        status: String = PenaltyEntity.STATUS_APPLIED
    ): Long?
    
    @Query("SELECT COUNT(*) FROM penalties WHERE rosca_id = :roscaId")
    suspend fun getPenaltyCountByRosca(roscaId: String): Int
    
    @Query("SELECT COUNT(*) FROM penalties WHERE member_id = :memberId")
    suspend fun getPenaltyCountByMember(memberId: String): Int
    
    @Query("SELECT COUNT(*) FROM penalties WHERE rosca_id = :roscaId AND status = :status")
    suspend fun getPenaltyCountByRoscaAndStatus(roscaId: String, status: String): Int
    
    @Query("SELECT AVG(penalty_percentage) FROM penalties WHERE rosca_id = :roscaId AND penalty_type = :penaltyType")
    suspend fun getAveragePenaltyPercentage(roscaId: String, penaltyType: String): Double?
    
    // Status Specific Queries
    @Query("SELECT * FROM penalties WHERE status = :status ORDER BY created_at DESC")
    suspend fun getCalculatedPenalties(status: String = PenaltyEntity.STATUS_CALCULATED): List<PenaltyEntity>
    
    @Query("SELECT * FROM penalties WHERE status = :status AND applied_at IS NOT NULL ORDER BY applied_at DESC")
    suspend fun getAppliedPenalties(status: String = PenaltyEntity.STATUS_APPLIED): List<PenaltyEntity>
    
    @Query("SELECT * FROM penalties WHERE status = :status AND waived_at IS NOT NULL ORDER BY waived_at DESC")
    suspend fun getWaivedPenalties(status: String = PenaltyEntity.STATUS_WAIVED): List<PenaltyEntity>
    
    @Query("SELECT * FROM penalties WHERE status = :status ORDER BY created_at DESC")
    suspend fun getDisputedPenalties(status: String = PenaltyEntity.STATUS_DISPUTED): List<PenaltyEntity>
    
    @Query("SELECT * FROM penalties WHERE rosca_id = :roscaId AND status = :status")
    suspend fun getWaivedPenaltiesByRosca(
        roscaId: String,
        status: String = PenaltyEntity.STATUS_WAIVED
    ): List<PenaltyEntity>
    
    // Type Specific Queries
    @Query("SELECT * FROM penalties WHERE rosca_id = :roscaId AND penalty_type = :penaltyType")
    suspend fun getEarlyExitPenalties(
        roscaId: String,
        penaltyType: String = PenaltyEntity.TYPE_EARLY_EXIT
    ): List<PenaltyEntity>
    
    @Query("SELECT * FROM penalties WHERE member_id = :memberId AND penalty_type = :penaltyType")
    suspend fun getNonPaymentPenalties(
        memberId: String,
        penaltyType: String = PenaltyEntity.TYPE_NON_PAYMENT
    ): List<PenaltyEntity>
    
    @Query("SELECT * FROM penalties WHERE penalty_type = :penaltyType AND status != :excludeStatus ORDER BY penalty_amount DESC")
    suspend fun getViolationPenalties(
        penaltyType: String = PenaltyEntity.TYPE_VIOLATION,
        excludeStatus: String = PenaltyEntity.STATUS_WAIVED
    ): List<PenaltyEntity>
    
    // Date Range Queries
    @Query("SELECT * FROM penalties WHERE rosca_id = :roscaId AND created_at BETWEEN :startTime AND :endTime ORDER BY created_at DESC")
    suspend fun getPenaltiesByDateRange(roscaId: String, startTime: Long, endTime: Long): List<PenaltyEntity>
    
    @Query("SELECT * FROM penalties WHERE created_at BETWEEN :startTime AND :endTime ORDER BY created_at DESC")
    fun observePenaltiesByDateRange(startTime: Long, endTime: Long): Flow<List<PenaltyEntity>>
    
    @Query("SELECT * FROM penalties WHERE applied_at BETWEEN :startTime AND :endTime ORDER BY applied_at DESC")
    suspend fun getAppliedPenaltiesByDateRange(startTime: Long, endTime: Long): List<PenaltyEntity>
    
    // Waiver Queries
    @Query("SELECT * FROM penalties WHERE waived_by = :waivedBy ORDER BY waived_at DESC")
    suspend fun getPenaltiesWaivedBy(waivedBy: String): List<PenaltyEntity>
    
    @Query("SELECT COUNT(*) FROM penalties WHERE rosca_id = :roscaId AND status = :status")
    suspend fun getWaiverCountByRosca(
        roscaId: String,
        status: String = PenaltyEntity.STATUS_WAIVED
    ): Int
    
    // Calculation Method Queries
    @Query("SELECT * FROM penalties WHERE calculation_method = :method ORDER BY created_at DESC")
    suspend fun getByCalculationMethod(method: String): List<PenaltyEntity>
    
    @Query("SELECT * FROM penalties WHERE rosca_id = :roscaId AND calculation_method = :method")
    suspend fun getByRoscaAndCalculationMethod(roscaId: String, method: String): List<PenaltyEntity>
    
    // Pending Penalties (calculated but not yet applied)
    @Query("SELECT * FROM penalties WHERE status = :status AND payout_id IS NULL ORDER BY created_at DESC")
    suspend fun getPendingPenalties(status: String = PenaltyEntity.STATUS_CALCULATED): List<PenaltyEntity>
    
    @Query("SELECT * FROM penalties WHERE rosca_id = :roscaId AND status = :status AND payout_id IS NULL")
    suspend fun getPendingPenaltiesByRosca(
        roscaId: String,
        status: String = PenaltyEntity.STATUS_CALCULATED
    ): List<PenaltyEntity>
    
    // Analytics Queries
    @Query("SELECT penalty_type, COUNT(*) as count, SUM(penalty_amount) as total FROM penalties WHERE rosca_id = :roscaId GROUP BY penalty_type")
    suspend fun getPenaltyStatsByType(roscaId: String): List<PenaltyStatistic>
    
    @Query("SELECT member_id, COUNT(*) as count, SUM(penalty_amount) as total FROM penalties WHERE rosca_id = :roscaId GROUP BY member_id")
    suspend fun getPenaltyStatsByMember(roscaId: String): List<MemberPenaltyStatistic>
    
    // Cleanup Operations
    @Query("DELETE FROM penalties WHERE rosca_id = :roscaId")
    suspend fun deleteByRoscaId(roscaId: String)
    
    @Query("DELETE FROM penalties WHERE member_id = :memberId")
    suspend fun deleteByMemberId(memberId: String)
    
    @Query("DELETE FROM penalties WHERE status = :status AND created_at < :timestamp")
    suspend fun deleteOldCalculatedPenalties(
        status: String = PenaltyEntity.STATUS_CALCULATED,
        timestamp: Long
    )
}

// Data classes for aggregate queries
data class PenaltyStatistic(
    @ColumnInfo(name = "penalty_type") val penaltyType: String,
    @ColumnInfo(name = "count") val count: Int,
    @ColumnInfo(name = "total") val total: Long
)

data class MemberPenaltyStatistic(
    @ColumnInfo(name = "member_id") val memberId: String,
    @ColumnInfo(name = "count") val count: Int,
    @ColumnInfo(name = "total") val total: Long
)
