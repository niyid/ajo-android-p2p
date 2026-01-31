package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)
    
    @Update
    suspend fun update(transaction: TransactionEntity)
    
    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun get(id: String): TransactionEntity?
    
    @Query("SELECT * FROM transactions WHERE txHash = :txHash LIMIT 1")
    suspend fun getByTxHash(txHash: String): TransactionEntity?
    
    @Query("SELECT * FROM transactions WHERE roscaId = :roscaId ORDER BY createdAt DESC")
    suspend fun getByRosca(roscaId: String): List<TransactionEntity>
    
    @Query("SELECT * FROM transactions WHERE roscaId = :roscaId ORDER BY createdAt DESC")
    fun getByRoscaFlow(roscaId: String): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE status = 'pending'")
    suspend fun getPendingTransactions(): List<TransactionEntity>
    
    @Query("SELECT * FROM transactions WHERE status = 'PENDING_CONFIRMATION' OR status = 'BROADCAST'")
    suspend fun getPendingConfirmation(): List<TransactionEntity>
    
    @Query("SELECT * FROM transactions WHERE status = 'PENDING_SIGNATURES' ORDER BY createdAt ASC")
    suspend fun getPendingSignatures(): List<TransactionEntity>
    
    @Query("DELETE FROM transactions WHERE status = 'confirmed' AND confirmedAt < :cutoffTime")
    suspend fun deleteOldCompletedTransactions(cutoffTime: Long): Int
    
    @Delete
    suspend fun delete(transaction: TransactionEntity)
}
