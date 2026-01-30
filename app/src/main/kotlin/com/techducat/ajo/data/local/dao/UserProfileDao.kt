package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    
    @Query("SELECT * FROM user_profiles WHERE id = :userId LIMIT 1")
    suspend fun getUserProfile(userId: String): UserProfileEntity?
    
    @Query("SELECT * FROM user_profiles WHERE id = :userId LIMIT 1")
    fun getUserProfileFlow(userId: String): Flow<UserProfileEntity?>
    
    @Query("SELECT * FROM user_profiles WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveUserProfile(): UserProfileEntity?
    
    @Query("SELECT * FROM user_profiles WHERE isActive = 1 LIMIT 1")
    fun getActiveUserProfileFlow(): Flow<UserProfileEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfileEntity)
    
    @Update
    suspend fun updateUserProfile(userProfile: UserProfileEntity)
    
    @Query("UPDATE user_profiles SET lastLoginAt = :timestamp WHERE id = :userId")
    suspend fun updateLastLogin(userId: String, timestamp: Long)
    
    @Query("UPDATE user_profiles SET isActive = 0")
    suspend fun deactivateAllProfiles()
    
    @Query("UPDATE user_profiles SET isActive = 1 WHERE id = :userId")
    suspend fun setActiveProfile(userId: String)
    
    @Delete
    suspend fun deleteUserProfile(userProfile: UserProfileEntity)
    
    @Query("DELETE FROM user_profiles WHERE id = :userId")
    suspend fun deleteUserProfileById(userId: String)
    
    @Query("DELETE FROM user_profiles")
    suspend fun deleteAllUserProfiles()
}

