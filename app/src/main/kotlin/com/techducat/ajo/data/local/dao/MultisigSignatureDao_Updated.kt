package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.MultisigSignatureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MultisigSignatureDao {
    
    @Query("SELECT * FROM multisig_signatures WHERE roscaId = :roscaId")
    fun getByRoscaFlow(roscaId: String): Flow<List<MultisigSignatureEntity>>
    
    @Query("SELECT * FROM multisig_signatures WHERE transactionId = :transactionId ORDER BY timestamp ASC")
    suspend fun getByTransaction(transactionId: String): List<MultisigSignatureEntity>
    
    @Query("SELECT * FROM multisig_signatures WHERE transactionId = :transactionId ORDER BY timestamp ASC")
    fun getByTransactionFlow(transactionId: String): Flow<List<MultisigSignatureEntity>>
    
    @Query("SELECT * FROM multisig_signatures WHERE roscaId = :roscaId")
    suspend fun getByRosca(roscaId: String): List<MultisigSignatureEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(signature: MultisigSignatureEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(signatures: List<MultisigSignatureEntity>)
    
    @Update
    suspend fun update(signature: MultisigSignatureEntity)
    
    @Delete
    suspend fun delete(signature: MultisigSignatureEntity)
    
    @Query("DELETE FROM multisig_signatures WHERE transactionId = :transactionId")
    suspend fun deleteByTransaction(transactionId: String)
    
    @Query("DELETE FROM multisig_signatures WHERE roscaId = :roscaId")
    suspend fun deleteByRosca(roscaId: String)
}
