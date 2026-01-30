package com.techducat.ajo.core.crypto

import com.techducat.ajo.sync.protocol.SyncMessage
import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64

/**
 * Message signing and verification with Ed25519
 * Uses simplified implementation for now
 */
object MessageSigner {
    
    /**
     * Sign a sync message
     * Returns Base64-encoded signature
     */
    fun sign(message: SyncMessage, privateKeyHex: String): String {
        val payload = buildSigningPayload(message)
        
        // TODO: Replace with actual Ed25519 signing
        // For now, use HMAC-SHA256 as placeholder
        val signature = simpleSign(payload, privateKeyHex)
        
        return Base64.encodeToString(signature, Base64.NO_WRAP)
    }
    
    /**
     * Verify message signature
     */
    fun verify(message: SyncMessage, signatureB64: String, publicKeyHex: String): Boolean {
        val payload = buildSigningPayload(message)
        val signature = Base64.decode(signatureB64, Base64.NO_WRAP)
        
        // TODO: Replace with actual Ed25519 verification
        val expected = simpleSign(payload, publicKeyHex)
        
        return signature.contentEquals(expected)
    }
    
    private fun buildSigningPayload(message: SyncMessage): ByteArray {
        val data = "${message.protocolVersion}|${message.messageId}|${message.timestamp}|${message.payload}"
        return data.toByteArray()
    }
    
    // Placeholder signing (replace with Ed25519)
    private fun simpleSign(data: ByteArray, key: String): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(key.toByteArray())
        digest.update(data)
        return digest.digest()
    }
    
    /**
     * Generate random message ID
     */
    fun generateMessageId(): String {
        val random = SecureRandom()
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP)
    }
}
