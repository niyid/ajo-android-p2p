package com.techducat.ajo.service.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.techducat.ajo.R
import com.techducat.ajo.data.local.entity.UserProfileEntity
import com.techducat.ajo.util.Logger
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Service for handling Google Sign-In authentication
 */
class GoogleAuthService(
    private val context: Context
) {
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        
        GoogleSignIn.getClient(context, gso)
    }
    
    fun getSignInIntent(): Intent = googleSignInClient.signInIntent
    
    suspend fun handleSignInResult(data: Intent?): Result<UserProfileEntity> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            
            val account = suspendCancellableCoroutine<GoogleSignInAccount> { continuation ->
                task.addOnSuccessListener { account ->
                    continuation.resume(account)
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }
            
            val userProfile = UserProfileEntity(
                id = account.id ?: throw IllegalStateException("Google account ID is null"),
                email = account.email ?: throw IllegalStateException("Email is null"),
                displayName = account.displayName ?: "",
                photoUrl = account.photoUrl?.toString(),
                idToken = account.idToken,
                createdAt = System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis()
            )
            
            Logger.d("Successfully signed in: ${userProfile.email}")
            Result.success(userProfile)
        } catch (e: ApiException) {
            Logger.e("Google sign in failed", e)
            Result.failure(e)
        } catch (e: Exception) {
            Logger.e("Unexpected error during sign in", e)
            Result.failure(e)
        }
    }
    
    fun getCurrentAccount(): GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(context)
    
    fun isSignedIn(): Boolean = getCurrentAccount() != null
    
    suspend fun signOut(): Result<Unit> {
        return try {
            suspendCancellableCoroutine { continuation ->
                googleSignInClient.signOut()
                    .addOnSuccessListener {
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
            Logger.d("User signed out successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Logger.e("Error signing out", e)
            Result.failure(e)
        }
    }
    
    suspend fun revokeAccess(): Result<Unit> {
        return try {
            suspendCancellableCoroutine { continuation ->
                googleSignInClient.revokeAccess()
                    .addOnSuccessListener {
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
            Logger.d("Access revoked successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Logger.e("Error revoking access", e)
            Result.failure(e)
        }
    }
    
    suspend fun silentSignIn(): Result<UserProfileEntity> {
        return try {
            val account = suspendCancellableCoroutine<GoogleSignInAccount> { continuation ->
                googleSignInClient.silentSignIn()
                    .addOnSuccessListener { account ->
                        continuation.resume(account)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
            
            val userProfile = UserProfileEntity(
                id = account.id ?: throw IllegalStateException("Google account ID is null"),
                email = account.email ?: throw IllegalStateException("Email is null"),
                displayName = account.displayName ?: "",
                photoUrl = account.photoUrl?.toString(),
                idToken = account.idToken,
                createdAt = System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis()
            )
            
            Logger.d("Silent sign in successful: ${userProfile.email}")
            Result.success(userProfile)
        } catch (e: Exception) {
            Logger.d("Silent sign in failed, user needs to sign in manually")
            Result.failure(e)
        }
    }
}
