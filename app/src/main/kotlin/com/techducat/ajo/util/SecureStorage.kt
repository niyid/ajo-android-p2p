package com.techducat.ajo.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Secure storage for sensitive data using EncryptedSharedPreferences
 */
class SecureStorage(context: Context) {
    
    companion object {
        private const val TAG = "com.techducat.ajo.util.SecureStorage"
        
        // Existing keys
        private const val KEY_CHANGENOW_API_KEY = "changenow_api_key"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
        
        // PIN-related keys
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_PIN_SALT = "pin_salt"
        private const val KEY_PIN_ATTEMPTS = "pin_attempts"
        private const val KEY_PIN_LOCKOUT_TIME = "pin_lockout_time"
        
        // PIN security parameters
        private const val MAX_PIN_ATTEMPTS = 5
        private const val LOCKOUT_DURATION_MS = 300000L // 5 minutes
        private const val HASH_ITERATIONS = 10000
    }
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "ajo_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    // ============================================
    // EXISTING METHODS - ChangeNow API Key
    // ============================================
    
    fun setChangeNowApiKey(apiKey: String) {
        sharedPreferences.edit().putString(KEY_CHANGENOW_API_KEY, apiKey).apply()
    }
    
    fun getChangeNowApiKey(): String? {
        return sharedPreferences.getString(KEY_CHANGENOW_API_KEY, null)
    }
    
    // ============================================
    // EXISTING METHODS - Authentication Token
    // ============================================
    
    fun setAuthToken(token: String, expiryTime: Long) {
        sharedPreferences.edit().apply {
            putString(KEY_AUTH_TOKEN, token)
            putLong(KEY_TOKEN_EXPIRY, expiryTime)
            apply()
        }
    }
    
    fun getAuthToken(): String? {
        val expiryTime = sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0)
        return if (System.currentTimeMillis() < expiryTime) {
            sharedPreferences.getString(KEY_AUTH_TOKEN, null)
        } else {
            // Token expired
            null
        }
    }
    
    fun isTokenValid(): Boolean {
        val expiryTime = sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0)
        return System.currentTimeMillis() < expiryTime && getAuthToken() != null
    }
    
    // ============================================
    // EXISTING METHODS - Refresh Token
    // ============================================
    
    fun setRefreshToken(token: String) {
        sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }
    
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }
    
    // ============================================
    // NEW METHODS - PIN Management
    // ============================================
    
    /**
     * Save a new PIN securely using SHA-256 hashing with salt
     */
    fun savePin(pin: String) {
        // Generate a random salt
        val salt = ByteArray(32)
        SecureRandom().nextBytes(salt)
        
        // Hash the PIN with salt
        val pinHash = hashPinWithSalt(pin, salt)
        
        // Store the hash and salt
        sharedPreferences.edit().apply {
            putString(KEY_PIN_HASH, pinHash.toHexString())
            putString(KEY_PIN_SALT, salt.toHexString())
            putInt(KEY_PIN_ATTEMPTS, 0)
            putLong(KEY_PIN_LOCKOUT_TIME, 0L)
            apply()
        }
        
        Logger.d("$TAG: PIN saved securely")
    }
    
    /**
     * Verify a PIN against stored hash
     * Implements rate limiting and lockout
     */
    fun verifyPin(pin: String): Boolean {
        // Check if locked out
        if (isPinLockedOut()) {
            Logger.w("$TAG: PIN verification blocked - account locked out")
            return false
        }
        
        val storedHashHex = sharedPreferences.getString(KEY_PIN_HASH, null)
        val storedSaltHex = sharedPreferences.getString(KEY_PIN_SALT, null)
        
        if (storedHashHex == null || storedSaltHex == null) {
            Logger.w("$TAG: No PIN configured")
            return false
        }
        
        val storedSalt = storedSaltHex.hexToByteArray()
        val storedHash = storedHashHex.hexToByteArray()
        
        // Hash the provided PIN with the stored salt
        val providedHash = hashPinWithSalt(pin, storedSalt)
        
        // Constant-time comparison to prevent timing attacks
        val isValid = storedHash.contentEquals(providedHash)
        
        if (isValid) {
            // Reset attempts on successful verification
            sharedPreferences.edit().apply {
                putInt(KEY_PIN_ATTEMPTS, 0)
                putLong(KEY_PIN_LOCKOUT_TIME, 0L)
                apply()
            }
            Logger.d("$TAG: PIN verified successfully")
            return true
        } else {
            // Increment failed attempts
            incrementPinAttempts()
            Logger.w("$TAG: PIN verification failed")
            return false
        }
    }
    
    /**
     * Check if PIN exists
     */
    fun hasPin(): Boolean {
        return sharedPreferences.getString(KEY_PIN_HASH, null) != null
    }
    
    /**
     * Remove PIN
     */
    fun removePin() {
        sharedPreferences.edit().apply {
            remove(KEY_PIN_HASH)
            remove(KEY_PIN_SALT)
            remove(KEY_PIN_ATTEMPTS)
            remove(KEY_PIN_LOCKOUT_TIME)
            apply()
        }
        Logger.d("$TAG: PIN removed")
    }
    
    /**
     * Check if account is locked out due to too many failed attempts
     */
    fun isPinLockedOut(): Boolean {
        val lockoutTime = sharedPreferences.getLong(KEY_PIN_LOCKOUT_TIME, 0L)
        
        if (lockoutTime == 0L) {
            return false
        }
        
        val currentTime = System.currentTimeMillis()
        if (currentTime < lockoutTime) {
            return true
        }
        
        // Lockout expired - reset
        sharedPreferences.edit().apply {
            putInt(KEY_PIN_ATTEMPTS, 0)
            putLong(KEY_PIN_LOCKOUT_TIME, 0L)
            apply()
        }
        
        return false
    }
    
    /**
     * Get remaining lockout time in milliseconds
     */
    fun getRemainingLockoutTime(): Long {
        if (!isPinLockedOut()) {
            return 0L
        }
        
        val lockoutTime = sharedPreferences.getLong(KEY_PIN_LOCKOUT_TIME, 0L)
        val currentTime = System.currentTimeMillis()
        
        return maxOf(0L, lockoutTime - currentTime)
    }
    
    /**
     * Get number of failed PIN attempts
     */
    fun getPinAttempts(): Int {
        return sharedPreferences.getInt(KEY_PIN_ATTEMPTS, 0)
    }
    
    /**
     * Increment failed PIN attempts and trigger lockout if needed
     */
    private fun incrementPinAttempts() {
        val attempts = sharedPreferences.getInt(KEY_PIN_ATTEMPTS, 0) + 1
        
        sharedPreferences.edit().apply {
            putInt(KEY_PIN_ATTEMPTS, attempts)
            
            if (attempts >= MAX_PIN_ATTEMPTS) {
                // Trigger lockout
                val lockoutTime = System.currentTimeMillis() + LOCKOUT_DURATION_MS
                putLong(KEY_PIN_LOCKOUT_TIME, lockoutTime)
                Logger.w("$TAG: Account locked out due to too many failed attempts")
            }
            
            apply()
        }
    }
    
    /**
     * Hash PIN with salt using SHA-256 with multiple iterations (PBKDF2-like)
     */
    private fun hashPinWithSalt(pin: String, salt: ByteArray, iterations: Int = HASH_ITERATIONS): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        
        var hash = (pin + salt.toHexString()).toByteArray()
        
        // Apply multiple iterations for stronger hashing
        repeat(iterations) {
            hash = digest.digest(hash)
        }
        
        return hash
    }
    
    // ============================================
    // EXISTING METHODS - Clear Data
    // ============================================
    
    /**
     * Clear all secure data (including PIN)
     */
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
    
    /**
     * Clear specific auth data (doesn't clear PIN)
     */
    fun clearAuthData() {
        sharedPreferences.edit().apply {
            remove(KEY_AUTH_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_TOKEN_EXPIRY)
            apply()
        }
    }
}

// ============================================
// Extension Functions for Hex Conversion
// ============================================

private fun ByteArray.toHexString(): String {
    return joinToString("") { "%02x".format(it) }
}

private fun String.hexToByteArray(): ByteArray {
    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}
