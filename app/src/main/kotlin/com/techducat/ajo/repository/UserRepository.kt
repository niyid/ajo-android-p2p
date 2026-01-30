package com.techducat.ajo.repository

import android.content.Context
import android.content.Intent
import com.techducat.ajo.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user authentication and profile management
 */
interface UserRepository {
    
    // ✨ Expose context for AuthStateManager
    val context: Context
    
    // ============ Authentication Operations ============
    
    fun getGoogleSignInIntent(): Intent
    
    suspend fun handleGoogleSignIn(data: Intent?): Result<UserProfileEntity>
    
    suspend fun silentSignIn(): Result<UserProfileEntity>
    
    fun isSignedIn(): Boolean
    
    suspend fun signOut(): Result<Unit>
    
    suspend fun revokeAccess(): Result<Unit>
    
    suspend fun deleteUserAccount(): Result<Unit>
    
    // ============ User Profile Operations ============
    
    suspend fun saveUserProfile(
        userId: String,
        name: String,
        walletAddress: String,
        phoneNumber: String?,
        email: String?
    )
    
    suspend fun getUserProfile(userId: String): Map<String, Any?>?
    
    suspend fun updateUserName(userId: String, newName: String)
    
    suspend fun getCurrentUserId(): String?
    
    suspend fun getCurrentWalletAddress(): String?
    
    fun observeUser(userId: String): Flow<Map<String, Any?>?>
    
    // ============ Additional Helper Methods ============
    
    suspend fun getCurrentUserProfile(): UserProfileEntity?
    
    fun getCurrentUserProfileFlow(): Flow<UserProfileEntity?>
    
    suspend fun updateUserProfile(userProfile: UserProfileEntity): Result<Unit>
}
