package com.techducat.ajo.sync

import com.techducat.ajo.sync.protocol.*
import com.techducat.ajo.core.crypto.MessageSigner
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import android.util.Base64

/**
 * Complete referral code generation and consumption
 */
object ReferralCodec {
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    /**
     * Create referral code (creator side)
     */
    fun create(
        roscaId: String,
        roscaName: String,
        creatorNodeId: String,
        creatorPublicKey: String,
        creatorEndpoint: String,
        contributionAmount: Double,
        currency: String,
        frequency: String,
        maxMembers: Int,
        currentMembers: Int,
        maxUses: Int = 1,
        validityHours: Long = 24,
        privateKey: String
    ): String {
        val now = System.currentTimeMillis()
        
        val payload = ReferralPayload(
            version = 1,
            roscaId = roscaId,
            roscaName = roscaName,
            creatorNodeId = creatorNodeId,
            creatorPublicKey = creatorPublicKey,
            creatorEndpoint = creatorEndpoint,
            contributionAmount = contributionAmount,
            currency = currency,
            frequency = frequency,
            maxMembers = maxMembers,
            currentMembers = currentMembers,
            maxUses = maxUses,
            expiresAt = now + (validityHours * 60 * 60 * 1000),
            createdAt = now
        )
        
        val payloadJson = json.encodeToString(ReferralPayload.serializer(), payload)
        
        // Sign payload
        val tempMessage = com.techducat.ajo.sync.protocol.SyncMessage(
            protocolVersion = 1,
            messageId = "referral",
            senderNodeId = creatorNodeId,
            recipientNodeId = "",
            timestamp = now,
            messageType = com.techducat.ajo.sync.protocol.MessageType.MEMBERSHIP_REQUEST,
            payload = payloadJson,
            signature = ""
        )
        
        val signature = MessageSigner.sign(tempMessage, privateKey)
        
        val code = ReferralCode(
            payload = payload,
            signature = signature
        )
        
        val codeJson = json.encodeToString(ReferralCode.serializer(), code)
        return Base64.encodeToString(codeJson.toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
    }
    
    /**
     * Parse referral code
     */
    fun parse(code: String): ReferralCode? {
        return try {
            val decoded = Base64.decode(code, Base64.URL_SAFE or Base64.NO_WRAP)
            json.decodeFromString(ReferralCode.serializer(), decoded.decodeToString())
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Verify referral signature
     */
    fun verify(code: ReferralCode): Boolean {
        val payloadJson = json.encodeToString(ReferralPayload.serializer(), code.payload)
        
        val tempMessage = com.techducat.ajo.sync.protocol.SyncMessage(
            protocolVersion = 1,
            messageId = "referral",
            senderNodeId = code.payload.creatorNodeId,
            recipientNodeId = "",
            timestamp = code.payload.createdAt,
            messageType = com.techducat.ajo.sync.protocol.MessageType.MEMBERSHIP_REQUEST,
            payload = payloadJson,
            signature = code.signature
        )
        
        return MessageSigner.verify(tempMessage, code.signature, code.payload.creatorPublicKey)
    }
    
    /**
     * Check if valid
     */
    fun isValid(code: ReferralCode): Boolean {
        return code.payload.expiresAt > System.currentTimeMillis()
    }
}

@Serializable
data class ReferralPayload(
    val version: Int,
    val roscaId: String,
    val roscaName: String,
    val creatorNodeId: String,
    val creatorPublicKey: String,
    val creatorEndpoint: String,
    val contributionAmount: Double,
    val currency: String,
    val frequency: String,
    val maxMembers: Int,
    val currentMembers: Int,
    val maxUses: Int,
    val expiresAt: Long,
    val createdAt: Long
)

@Serializable
data class ReferralCode(
    val payload: ReferralPayload,
    val signature: String
)

sealed class ValidationResult {
    data class Valid(val code: ReferralCode) : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}
