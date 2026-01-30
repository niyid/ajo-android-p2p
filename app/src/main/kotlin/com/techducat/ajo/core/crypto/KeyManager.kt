package com.techducat.ajo.core.crypto

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class KeyManager(private val context: Context) {
    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val MASTER_KEY_ALIAS = "ajo_master_key"
    }
    
    private val passwordCache = mutableMapOf<String, String>()
    
    fun generateSecurePassword(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
    
    fun storeWalletPassword(roscaId: String, userId: String, password: String) {
        val key = "${roscaId}_$userId"
        context.getSharedPreferences("wallet_passwords", Context.MODE_PRIVATE)
            .edit()
            .putString(key, password)
            .apply()
        passwordCache[key] = password
    }
    
    fun getWalletPassword(roscaId: String, userId: String): String? {
        val key = "${roscaId}_$userId"
        return passwordCache[key] ?: context.getSharedPreferences("wallet_passwords", Context.MODE_PRIVATE)
            .getString(key, null)?.also { passwordCache[key] = it }
    }
    
    fun deleteWalletPassword(roscaId: String, userId: String) {
        val key = "${roscaId}_$userId"
        passwordCache.remove(key)
        context.getSharedPreferences("wallet_passwords", Context.MODE_PRIVATE)
            .edit()
            .remove(key)
            .apply()
    }
}
