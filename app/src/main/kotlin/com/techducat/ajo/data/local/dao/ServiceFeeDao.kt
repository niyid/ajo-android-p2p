package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.ServiceFeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceFeeDao {
    @Query("SELECT * FROM service_fees WHERE status = 'PENDING'")
    suspend fun getPendingFees(): List<ServiceFeeEntity>
    
    @Query("SELECT SUM(CAST(feeAmount AS REAL)) FROM service_fees WHERE status = 'PAID'")
    suspend fun getTotalFeesCollected(): Double?
    
    @Query("SELECT * FROM service_fees WHERE id = :id")
    suspend fun getById(id: String): ServiceFeeEntity?
    
    @Query("SELECT * FROM service_fees ORDER BY createdAt DESC")
    suspend fun getAllFeeRecords(): List<ServiceFeeEntity>
    
    @Query("SELECT * FROM service_fees ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getFeeHistory(limit: Int, offset: Int): List<ServiceFeeEntity>
    
    @Query("SELECT * FROM service_fees WHERE roscaId = :roscaId")
    suspend fun getFeeRecordsByRosca(roscaId: String): List<ServiceFeeEntity>
    
    @Query("SELECT * FROM service_fees WHERE serviceWallet = :walletAddress ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentTransactionsForWallet(walletAddress: String, limit: Int): List<ServiceFeeEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fee: ServiceFeeEntity)
    
    @Update
    suspend fun update(fee: ServiceFeeEntity)
    
    // Delete Operations
    @Delete
    suspend fun delete(penalty: ServiceFeeEntity)
    
}
