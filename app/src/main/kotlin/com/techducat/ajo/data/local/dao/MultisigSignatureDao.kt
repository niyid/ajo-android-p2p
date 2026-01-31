package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.MultisigSignatureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MultisigSignatureDao {
    
    @Query("SELECT * FROM multisig_signatures WHERE roscaId = :roscaId")
    fun getByRoscaFlow(roscaId: String): Flow<List<MultisigSignatureEntity>>
    
    @Query("SELECT * FROM multisig_signatures WHERE txHash = :txHash ORDER BY timestamp ASC")
    suspend fun getByTransaction(txHash: String): List<MultisigSignatureEntity>
    
    @Query("SELECT * FROM multisig_signatures WHERE txHash = :txHash ORDER BY timestamp ASC")
    fun getByTransactionFlow(txHash: String): Flow<List<MultisigSignatureEntity>>
    
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
    
    @Query("DELETE FROM multisig_signatures WHERE txHash = :txHash")
    suspend fun deleteByTransaction(txHash: String)
    
    @Query("DELETE FROM multisig_signatures WHERE roscaId = :roscaId")
    suspend fun deleteByRosca(roscaId: String)
    
    /**
     * Get all multisig signatures for a specific ROSCA and round number
     */
    @Query("SELECT * FROM multisig_signatures WHERE roscaId = :roscaId AND roundNumber = :roundNumber")
    suspend fun getMultisigSignatures(roscaId: String, roundNumber: Int): List<MultisigSignatureEntity>
    
    /**
     * Get a specific multisig signature for a member in a specific round
     */
    @Query("SELECT * FROM multisig_signatures WHERE roscaId = :roscaId AND roundNumber = :roundNumber AND memberId = :memberId LIMIT 1")
    suspend fun getMultisigSignature(roscaId: String, roundNumber: Int, memberId: String): MultisigSignatureEntity?
    
    /**
     * Insert or update (upsert) a multisig signature
     * Uses REPLACE strategy to update if already exists
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(signature: MultisigSignatureEntity)
}
