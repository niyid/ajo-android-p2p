package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.InviteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InviteDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvite(invite: InviteEntity)
    
    @Update
    suspend fun updateInvite(invite: InviteEntity)
    
    @Delete
    suspend fun deleteInvite(invite: InviteEntity)
    
    @Query("SELECT * FROM invites WHERE referralCode = :referralCode LIMIT 1")
    suspend fun getInviteByReferralCode(referralCode: String): InviteEntity?
    
    @Query("SELECT * FROM invites WHERE roscaId = :roscaId")
    fun getInvitesByRosca(roscaId: String): Flow<List<InviteEntity>>
    
    @Query("SELECT * FROM invites WHERE inviterUserId = :userId")
    fun getInvitesByInviter(userId: String): Flow<List<InviteEntity>>
    
    @Query("SELECT * FROM invites WHERE inviteeEmail = :email AND status = :status")
    suspend fun getPendingInvitesByEmail(
        email: String, 
        status: String = InviteEntity.STATUS_PENDING
    ): List<InviteEntity>
    
    @Query("SELECT * FROM invites WHERE status = :status AND expiresAt > :currentTime")
    suspend fun getActiveInvites(
        status: String = InviteEntity.STATUS_PENDING, 
        currentTime: Long = System.currentTimeMillis()
    ): List<InviteEntity>
    
    @Query("UPDATE invites SET status = :newStatus WHERE expiresAt < :currentTime AND status = :oldStatus")
    suspend fun expireOldInvites(
        newStatus: String = InviteEntity.STATUS_EXPIRED,
        oldStatus: String = InviteEntity.STATUS_PENDING,
        currentTime: Long = System.currentTimeMillis()
    )
    
    @Query("SELECT COUNT(*) FROM invites WHERE roscaId = :roscaId AND status = :status")
    suspend fun getInviteCountByRosca(
        roscaId: String, 
        status: String = InviteEntity.STATUS_PENDING
    ): Int
}
