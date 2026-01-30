package com.techducat.ajo.ui.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techducat.ajo.repository.UserRepository
import com.techducat.ajo.service.ReferralHandler
import com.techducat.ajo.service.ReferralResult
import com.techducat.ajo.util.AuthStateManager
import com.techducat.ajo.util.Logger
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository,
    private val referralHandler: ReferralHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    private val _signInIntent = MutableSharedFlow<Intent?>()
    val signInIntent: SharedFlow<Intent?> = _signInIntent.asSharedFlow()
    
    // Expose referral result to UI
    private val _referralResult = MutableSharedFlow<ReferralResult>()
    val referralResult: SharedFlow<ReferralResult> = _referralResult.asSharedFlow()
    
    fun startGoogleSignIn() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val intent = userRepository.getGoogleSignInIntent()
                _signInIntent.emit(intent)
            } catch (e: Exception) {
                Logger.e("Error starting Google sign in", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to start sign in: ${e.message}"
                )
            }
        }
    }
    
    suspend fun getCurrentUserDisplayName(): String? {
        return try {
            val result = userRepository.silentSignIn()
            result.getOrNull()?.displayName
        } catch (e: Exception) {
            Logger.e("Error getting current user display name", e)
            null
        }
    }
        
    fun handleSignInResult(data: Intent?) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val result = userRepository.handleGoogleSignIn(data)
                
                result.fold(
                    onSuccess = { userProfile ->
                        Logger.d("Sign in successful: ${userProfile.email}")
                        
                        // ✨ Update AuthStateManager with login
                        AuthStateManager.onLogin(userRepository.context, userProfile.id)
                        
                        // Process any pending referral after successful sign in
                        processReferralAfterLogin(userProfile.id, userProfile.email)
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSignedIn = true
                        )
                    },
                    onFailure = { exception ->
                        Logger.e("Sign in failed", exception)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Sign in failed: ${exception.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                Logger.e("Error handling sign in result", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "An error occurred: ${e.message}"
                )
            }
        }
    }
    
    fun checkExistingSignIn() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val result = userRepository.silentSignIn()
                
                result.fold(
                    onSuccess = { userProfile ->
                        Logger.d("Existing sign in found: ${userProfile.email}")
                        
                        // ✨ Update AuthStateManager with existing login
                        AuthStateManager.onLogin(userRepository.context, userProfile.id)
                        
                        // Also process referral for existing sign-in (in case app was killed)
                        processReferralAfterLogin(userProfile.id, userProfile.email)
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSignedIn = true
                        )
                    },
                    onFailure = {
                        Logger.d("No existing sign in found")
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                )
            } catch (e: Exception) {
                Logger.e("Error checking existing sign in", e)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
    
    // ✨ NEW: Sign out function
    fun signOut() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // Sign out from repository (handles Google sign out + SharedPreferences)
                val result = userRepository.signOut()
                
                result.fold(
                    onSuccess = {
                        // ✨ Update AuthStateManager
                        AuthStateManager.onLogout(userRepository.context)
                        
                        _uiState.value = LoginUiState() // Reset to initial state
                        
                        Logger.d("User signed out successfully")
                    },
                    onFailure = { exception ->
                        Logger.e("Sign out failed", exception)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to sign out: ${exception.message}"
                        )
                    }
                )
                
            } catch (e: Exception) {
                Logger.e("Error during sign out", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to sign out: ${e.message}"
                )
            }
        }
    }
    
    private suspend fun processReferralAfterLogin(userId: String, userEmail: String?) {
        try {
            // Check if there's a pending referral
            if (!referralHandler.hasPendingReferral()) {
                Logger.d("No pending referral to process")
                return
            }
            
            Logger.d("Processing pending referral for user: $userId")
            
            val result = referralHandler.processReferralAfterLogin(userId, userEmail)
            
            // Emit result to UI for handling
            _referralResult.emit(result)
            
            // Log the result
            when (result) {
                is ReferralResult.Success -> {
                    Logger.d("✓ Referral processed successfully: Joined ${result.roscaName}")
                }
                is ReferralResult.AlreadyMember -> {
                    Logger.d("User already member of ${result.roscaName}")
                }
                is ReferralResult.Error -> {
                    Logger.e("Referral processing error: ${result.message}")
                }
                is ReferralResult.Expired -> {
                    Logger.w("Referral code expired")
                }
                is ReferralResult.InvalidCode -> {
                    Logger.w("Invalid referral code")
                }
                is ReferralResult.RoscaFull -> {
                    Logger.w("ROSCA is full")
                }
                is ReferralResult.EmailMismatch -> {
                    Logger.w("Email mismatch: expected ${result.expectedEmail}")
                }
                else -> {
                    Logger.d("Referral result: $result")
                }
            }
            
        } catch (e: Exception) {
            Logger.e("Exception processing referral", e)
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val error: String? = null
)
