package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.MemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(member: MemberEntity)
    
    @Update
    suspend fun update(member: MemberEntity)
    
    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    suspend fun getMemberById(id: String): MemberEntity?
    
    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): MemberEntity?
    
    @Query("SELECT * FROM members WHERE roscaId = :roscaId")
    fun getMembersByGroup(roscaId: String): List<MemberEntity>
    
    @Query("SELECT * FROM members WHERE roscaId = :roscaId")
    fun getMembersByRosca(roscaId: String): Flow<List<MemberEntity>>
    
    @Query("SELECT * FROM members WHERE roscaId = :roscaId")
    fun getMembersByGroupSync(roscaId: String): List<MemberEntity>
    
    @Query("SELECT * FROM members")
    suspend fun getAllMembers(): List<MemberEntity>
    
    // ✅ FIX: Changed to use userId instead of non-existent nodeId
    @Query("SELECT * FROM members WHERE userId = :userId AND roscaId = :roscaId LIMIT 1")
    suspend fun getByUserId(userId: String, roscaId: String): MemberEntity?
    
    @Query("UPDATE members SET status = :status, updatedAt = :updatedAt WHERE id = :memberId")
    suspend fun updateStatus(memberId: String, status: String, updatedAt: Long)
}
