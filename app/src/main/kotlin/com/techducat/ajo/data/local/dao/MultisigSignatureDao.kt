package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.MultisigSignatureEntity

@Dao
interface MultisigSignatureDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(signature: MultisigSignatureEntity)
    
    @Upsert
    suspend fun upsert(signature: MultisigSignatureEntity)
    
    @Update
    suspend fun update(signature: MultisigSignatureEntity)
    
    @Query("SELECT * FROM multisig_signatures WHERE roscaId = :roscaId AND roundNumber = :roundNumber")
    suspend fun getMultisigSignatures(roscaId: String, roundNumber: Int): List<MultisigSignatureEntity>
    
    @Query("SELECT * FROM multisig_signatures WHERE roscaId = :roscaId AND roundNumber = :roundNumber AND memberId = :memberId")
    suspend fun getMultisigSignature(roscaId: String, roundNumber: Int, memberId: String): MultisigSignatureEntity?
    
    @Query("DELETE FROM multisig_signatures WHERE roscaId = :roscaId")
    suspend fun deleteByRoscaId(roscaId: String)
}
