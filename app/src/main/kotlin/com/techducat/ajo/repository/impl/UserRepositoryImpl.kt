package com.techducat.ajo.repository.impl

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.techducat.ajo.data.local.dao.UserProfileDao
import com.techducat.ajo.data.local.entity.UserProfileEntity
import com.techducat.ajo.repository.UserRepository
import com.techducat.ajo.service.auth.GoogleAuthService
import com.techducat.ajo.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Implementation of UserRepository for user authentication and profile management
 */
class UserRepositoryImpl(
    private val googleAuthService: GoogleAuthService,
    private val userProfileDao: UserProfileDao,
    override val context: Context // ✨ Expose context for AuthStateManager
) : UserRepository {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    private val _currentUserProfile = MutableStateFlow<UserProfileEntity?>(null)
    
    // ============ Authentication Operations ============
    
    override fun getGoogleSignInIntent(): Intent {
        return googleAuthService.getSignInIntent()
    }
    
    override suspend fun handleGoogleSignIn(data: Intent?): Result<UserProfileEntity> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            
            if (account == null) {
                return Result.failure(Exception("Sign in failed: No account returned"))
            }
            
            val userId = account.id ?: return Result.failure(Exception("No user ID"))
            val email = account.email ?: return Result.failure(Exception("No email"))
            val displayName = account.displayName ?: email.substringBefore("@")
            val photoUrl = account.photoUrl?.toString()
            val idToken = account.idToken
            
            val currentTime = System.currentTimeMillis()
            
            // Deactivate all existing profiles before creating/activating new one
            userProfileDao.deactivateAllProfiles()
            
            val userProfile = UserProfileEntity(
                id = userId,
                email = email,
                displayName = displayName,
                photoUrl = photoUrl,
                idToken = idToken,
                isActive = true,
                createdAt = currentTime,
                lastLoginAt = currentTime
            )
            
            // Save to database
            userProfileDao.insertUserProfile(userProfile)
            
            // Activate this profile
            userProfileDao.setActiveProfile(userId)
            
            // Save user ID to SharedPreferences
            saveUserIdToPrefs(userId)
            
            // Update in-memory profile
            _currentUserProfile.value = userProfile
            
            Logger.d("User signed in successfully: $email (ID: $userId)")
            
            Result.success(userProfile)
        } catch (e: ApiException) {
            Logger.e("Google Sign-In failed", e)
            Result.failure(Exception("Sign in failed: ${e.message}"))
        } catch (e: Exception) {
            Logger.e("Error handling sign in result", e)
            Result.failure(e)
        }
    }
    
    override suspend fun silentSignIn(): Result<UserProfileEntity> {
        return try {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            
            if (account != null) {
                val userId = account.id ?: return Result.failure(Exception("No user ID"))
                val email = account.email ?: return Result.failure(Exception("No email"))
                val displayName = account.displayName ?: email.substringBefore("@")
                val photoUrl = account.photoUrl?.toString()
                val idToken = account.idToken
                
                // Check if user exists in database
                var userProfile = userProfileDao.getUserProfile(userId)
                
                if (userProfile == null) {
                    // Create new profile if doesn't exist
                    val currentTime = System.currentTimeMillis()
                    userProfile = UserProfileEntity(
                        id = userId,
                        email = email,
                        displayName = displayName,
                        photoUrl = photoUrl,
                        idToken = idToken,
                        isActive = true,
                        createdAt = currentTime,
                        lastLoginAt = currentTime
                    )
                    userProfileDao.insertUserProfile(userProfile)
                } else {
                    // Update last login time and token
                    userProfileDao.updateLastLogin(userId, System.currentTimeMillis())
                    
                    val updatedProfile = userProfile.copy(
                        lastLoginAt = System.currentTimeMillis(),
                        idToken = idToken,
                        isActive = true
                    )
                    userProfileDao.updateUserProfile(updatedProfile)
                    userProfile = updatedProfile
                }
                
                // Deactivate all profiles and activate current one
                userProfileDao.deactivateAllProfiles()
                userProfileDao.setActiveProfile(userId)
                
                // Save user ID to SharedPreferences
                saveUserIdToPrefs(userId)
                
                // Update in-memory profile
                _currentUserProfile.value = userProfile
                
                Logger.d("Silent sign-in successful: $email (ID: $userId)")
                
                Result.success(userProfile)
            } else {
                Logger.d("No existing sign-in found")
                Result.failure(Exception("No signed in account"))
            }
        } catch (e: Exception) {
            Logger.e("Silent sign-in failed", e)
            Result.failure(e)
        }
    }
    
    override fun isSignedIn(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account != null
    }
    
    override suspend fun signOut(): Result<Unit> {
        return try {
            googleAuthService.signOut()
            
            // Deactivate user in database
            val userId = getCurrentUserId()
            if (userId != null) {
                userProfileDao.deactivateAllProfiles()
            }
            
            // Clear SharedPreferences
            clearUserData()
            
            // Clear in-memory profile
            _currentUserProfile.value = null
            
            Logger.d("User signed out successfully")
            
            Result.success(Unit)
        } catch (e: Exception) {
            Logger.e("Sign out failed", e)
            Result.failure(e)
        }
    }
    
    override suspend fun revokeAccess(): Result<Unit> {
        return try {
            googleAuthService.revokeAccess()
            
            // Delete user from database
            val userId = getCurrentUserId()
            if (userId != null) {
                userProfileDao.deleteUserProfileById(userId)
            }
            
            // Clear SharedPreferences
            clearUserData()
            
            // Clear in-memory profile
            _currentUserProfile.value = null
            
            Logger.d("Access revoked successfully")
            
            Result.success(Unit)
        } catch (e: Exception) {
            Logger.e("Revoke access failed", e)
            Result.failure(e)
        }
    }
    
    override suspend fun deleteUserAccount(): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
            
            if (userId != null) {
                // TODO: Delete user data from Firestore/backend
                
                // Delete from local database
                userProfileDao.deleteUserProfileById(userId)
                
                // Revoke Google access
                googleAuthService.revokeAccess()
                
                // Clear SharedPreferences
                clearUserData()
                
                // Clear in-memory profile
                _currentUserProfile.value = null
                
                Logger.d("User account deleted: $userId")
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Logger.e("Delete account failed", e)
            Result.failure(e)
        }
    }
    
    // ============ User Profile Operations ============
    
    override suspend fun saveUserProfile(
        userId: String,
        name: String,
        walletAddress: String,
        phoneNumber: String?,
        email: String?
    ) {
        try {
            var userProfile = userProfileDao.getUserProfile(userId)
            
            if (userProfile != null) {
                // Update existing profile
                val updatedProfile = userProfile.copy(
                    displayName = name,
                    email = email ?: userProfile.email
                )
                userProfileDao.updateUserProfile(updatedProfile)
                _currentUserProfile.value = updatedProfile
            } else {
                // Create new profile
                val currentTime = System.currentTimeMillis()
                val newProfile = UserProfileEntity(
                    id = userId,
                    email = email ?: "",
                    displayName = name,
                    photoUrl = null,
                    idToken = null,
                    isActive = true,
                    createdAt = currentTime,
                    lastLoginAt = currentTime
                )
                userProfileDao.insertUserProfile(newProfile)
                _currentUserProfile.value = newProfile
            }
            
            // Save wallet address and phone separately in SharedPreferences
            sharedPreferences.edit().apply {
                putString("wallet_address", walletAddress)
                putString("phone_number", phoneNumber)
                apply()
            }
            
            Logger.d("User profile saved: $userId")
        } catch (e: Exception) {
            Logger.e("Error saving user profile", e)
            throw e
        }
    }
    
    override suspend fun getUserProfile(userId: String): Map<String, Any?>? {
        return try {
            val profile = userProfileDao.getUserProfile(userId)
            
            if (profile != null) {
                mapOf(
                    "id" to profile.id,
                    "email" to profile.email,
                    "displayName" to profile.displayName,
                    "photoUrl" to profile.photoUrl,
                    "walletAddress" to sharedPreferences.getString("wallet_address", null),
                    "phoneNumber" to sharedPreferences.getString("phone_number", null),
                    "createdAt" to profile.createdAt,
                    "lastLoginAt" to profile.lastLoginAt,
                    "isActive" to profile.isActive
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Logger.e("Error getting user profile", e)
            null
        }
    }
    
    override suspend fun updateUserName(userId: String, newName: String) {
        try {
            val userProfile = userProfileDao.getUserProfile(userId)
            
            if (userProfile != null) {
                val updatedProfile = userProfile.copy(displayName = newName)
                userProfileDao.updateUserProfile(updatedProfile)
                _currentUserProfile.value = updatedProfile
                
                Logger.d("User name updated: $userId -> $newName")
            }
        } catch (e: Exception) {
            Logger.e("Error updating user name", e)
            throw e
        }
    }
    
    override suspend fun getCurrentUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }
    
    override suspend fun getCurrentWalletAddress(): String? {
        return sharedPreferences.getString("wallet_address", null)
    }
    
    override fun observeUser(userId: String): Flow<Map<String, Any?>?> {
        // Convert UserProfileEntity flow to Map flow
        return userProfileDao.getUserProfileFlow(userId).map { profile ->
            if (profile != null) {
                mapOf(
                    "id" to profile.id,
                    "email" to profile.email,
                    "displayName" to profile.displayName,
                    "photoUrl" to profile.photoUrl,
                    "walletAddress" to sharedPreferences.getString("wallet_address", null),
                    "phoneNumber" to sharedPreferences.getString("phone_number", null),
                    "createdAt" to profile.createdAt,
                    "lastLoginAt" to profile.lastLoginAt,
                    "isActive" to profile.isActive
                )
            } else {
                null
            }
        }
    }
    
    // ============ Additional Helper Methods ============
    
    override suspend fun getCurrentUserProfile(): UserProfileEntity? {
        // Try to get from memory first
        _currentUserProfile.value?.let { return it }
        
        // Try to load from database
        val userId = getCurrentUserId() ?: return null
        val profile = userProfileDao.getUserProfile(userId)
        
        // Update in-memory cache
        _currentUserProfile.value = profile
        
        return profile
    }
    
    override fun getCurrentUserProfileFlow(): Flow<UserProfileEntity?> {
        // Combine in-memory flow with database flow
        val userId = sharedPreferences.getString("user_id", null)
        return if (userId != null) {
            userProfileDao.getUserProfileFlow(userId)
        } else {
            _currentUserProfile.asStateFlow()
        }
    }
    
    override suspend fun updateUserProfile(userProfile: UserProfileEntity): Result<Unit> {
        return try {
            userProfileDao.updateUserProfile(userProfile)
            _currentUserProfile.value = userProfile
            
            Logger.d("User profile updated: ${userProfile.id}")
            
            Result.success(Unit)
        } catch (e: Exception) {
            Logger.e("Error updating user profile", e)
            Result.failure(e)
        }
    }
    
    // ============ Private Helper Methods ============
    
    private fun saveUserIdToPrefs(userId: String) {
        sharedPreferences.edit().apply {
            putString("user_id", userId)
            apply()
        }
        Logger.d("Saved user ID to SharedPreferences: $userId")
    }
    
    private fun clearUserData() {
        sharedPreferences.edit().apply {
            remove("user_id")
            remove("wallet_address")
            remove("phone_number")
            apply()
        }
        Logger.d("Cleared user data from SharedPreferences")
    }
}
