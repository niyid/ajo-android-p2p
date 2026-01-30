package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.TransactionEntity

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)
    
    @Update
    suspend fun update(transaction: TransactionEntity)
    
    @Query("SELECT * FROM transactions WHERE status = 'pending'")
    suspend fun getPendingTransactions(): List<TransactionEntity>
    
    @Query("SELECT * FROM transactions WHERE txHash = :txHash")
    suspend fun getByTxHash(txHash: String): TransactionEntity?
    
    @Query("DELETE FROM transactions WHERE status = 'confirmed' AND confirmedAt < :cutoffTime")
    suspend fun deleteOldCompletedTransactions(cutoffTime: Long): Int
}
