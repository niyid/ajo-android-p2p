package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.LocalWalletEntity

@Dao
interface LocalWalletDao {
    @Query("SELECT * FROM local_wallets WHERE roscaId = :roscaId")
    suspend fun getWalletsByRosca(roscaId: String): List<LocalWalletEntity>
    
    @Query("SELECT * FROM local_wallets WHERE id = :id LIMIT 1")
    suspend fun getWalletById(id: String): LocalWalletEntity?
    
    @Query("SELECT * FROM local_wallets WHERE roscaId = :roscaId AND nodeId = :nodeId LIMIT 1")
    suspend fun getWalletForNode(roscaId: String, nodeId: String): LocalWalletEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wallet: LocalWalletEntity)
    
    @Update
    suspend fun update(wallet: LocalWalletEntity)
    
    @Query("UPDATE local_wallets SET lastAccessedAt = :timestamp WHERE id = :id")
    suspend fun updateLastAccessed(id: String, timestamp: Long)
    
    @Delete
    suspend fun delete(wallet: LocalWalletEntity)
    
    @Query("DELETE FROM local_wallets WHERE roscaId = :roscaId")
    suspend fun deleteByRosca(roscaId: String)
}
