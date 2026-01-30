package com.techducat.ajo.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Centralized authentication state manager
 * Provides a single source of truth for login state across the app
 */
object AuthStateManager {
    private const val TAG = "com.techducat.ajo.util.AuthStateManager"
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_USER_ID = "user_id"
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()
    
    private var sharedPrefs: SharedPreferences? = null
    
    /**
     * Initialize the AuthStateManager with application context
     * Should be called from Application.onCreate()
     */
    fun initialize(context: Context) {
        sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        checkAuthState(context)
    }
    
    /**
     * Check current authentication state
     */
    fun checkAuthState(context: Context) {
        val prefs = sharedPrefs ?: context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedUserId = prefs.getString(KEY_USER_ID, null)
        val googleAccount = GoogleSignIn.getLastSignedInAccount(context)
        
        val isAuthenticated = storedUserId != null && googleAccount != null
        
        Log.d(TAG, "Auth state check - UserId: $storedUserId, Google: ${googleAccount?.email}, Authenticated: $isAuthenticated")
        
        _userId.value = if (isAuthenticated) storedUserId else null
        _isLoggedIn.value = isAuthenticated
    }
    
    /**
     * Update authentication state when user logs in
     */
    fun onLogin(context: Context, userId: String) {
        val prefs = sharedPrefs ?: context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USER_ID, userId).apply()
        
        _userId.value = userId
        _isLoggedIn.value = true
        
        Log.d(TAG, "User logged in: $userId")
    }
    
    /**
     * Clear authentication state when user logs out
     */
    fun onLogout(context: Context) {
        val prefs = sharedPrefs ?: context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        
        _userId.value = null
        _isLoggedIn.value = false
        
        Log.d(TAG, "User logged out")
    }
    
    /**
     * Get current user ID (synchronous)
     */
    fun getCurrentUserId(context: Context): String? {
        // First check in-memory state
        val cachedUserId = _userId.value
        if (cachedUserId != null) {
            return cachedUserId
        }
        
        // Fallback to SharedPreferences
        val prefs = sharedPrefs ?: context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedUserId = prefs.getString(KEY_USER_ID, null)
        val googleAccount = GoogleSignIn.getLastSignedInAccount(context)
        
        // Verify Google account is still signed in
        if (storedUserId != null && googleAccount != null) {
            _userId.value = storedUserId
            _isLoggedIn.value = true
            return storedUserId
        }
        
        // Inconsistent state - clear everything
        if (storedUserId != null || googleAccount != null) {
            onLogout(context)
        }
        
        return null
    }
    
    /**
     * Check if user is currently logged in (synchronous)
     */
    fun isUserLoggedIn(context: Context): Boolean {
        return getCurrentUserId(context) != null
    }
}
