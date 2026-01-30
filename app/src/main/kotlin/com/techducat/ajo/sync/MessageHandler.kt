package com.techducat.ajo.sync

import android.content.Context
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.sync.protocol.*
import com.techducat.ajo.core.crypto.MessageSigner
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Handles inbound sync messages
 */
class MessageHandler(private val context: Context) {
    
    private val db = AjoDatabase.getInstance(context)
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    /**
     * Process incoming message
     */
    suspend fun handleMessage(messageBytes: ByteArray) = withContext(Dispatchers.IO) {
        try {
            // Deserialize
            val message = json.decodeFromString(
                SyncMessage.serializer(),
                messageBytes.decodeToString()
            )
            
            // Verify signature
            val peer = db.peerDao().getPeerByNodeId(message.senderNodeId)
            
            if (peer == null || !MessageSigner.verify(message, message.signature, peer.publicKey)) {
                logMessage(message, "FAILED", "Invalid signature")
                return@withContext
            }
            
            // Process based on type
            when (message.messageType) {
                MessageType.MEMBERSHIP_REQUEST -> handleMembershipRequest(message)
                MessageType.MEMBERSHIP_RESPONSE -> handleMembershipResponse(message)
                MessageType.ENTITY_UPDATE -> handleEntityUpdate(message)
                MessageType.STATE_SYNC -> handleStateSync(message)
                MessageType.ACK -> handleAck(message)
                MessageType.PING -> handlePing(message)
                MessageType.PONG -> handlePong(message)
            }
            
            logMessage(message, "SUCCESS", null)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private suspend fun handleMembershipRequest(message: SyncMessage) {
        val payload = json.decodeFromString(
            MembershipRequestPayload.serializer(),
            message.payload
        )
        
        // TODO: Validate request
        // TODO: Create member entity
        // TODO: Send acceptance response
    }
    
    private suspend fun handleMembershipResponse(message: SyncMessage) {
        val payload = json.decodeFromString(
            MembershipResponsePayload.serializer(),
            message.payload
        )
        
        if (payload.accepted && payload.roscaState != null) {
            // Apply state snapshot
            applySnapshot(payload.roscaState)
        }
    }
    
    private suspend fun handleEntityUpdate(message: SyncMessage) {
        val payload = json.decodeFromString(
            EntityUpdatePayload.serializer(),
            message.payload
        )
        
        payload.updates.forEach { update ->
            // Apply update based on entity type
            when (update.entityType) {
                "contributions" -> applyContributionUpdate(update)
                "members" -> applyMemberUpdate(update)
                // ... handle other entity types
            }
        }
    }
    
    private suspend fun handleStateSync(message: SyncMessage) {
        val payload = json.decodeFromString(
            StateSyncPayload.serializer(),
            message.payload
        )
        
        applySnapshot(payload.snapshot)
    }
    
    private suspend fun handleAck(message: SyncMessage) {
        // ACK received - original message was delivered
    }
    
    private suspend fun handlePing(message: SyncMessage) {
        // TODO: Send PONG response
    }
    
    private suspend fun handlePong(message: SyncMessage) {
        // PONG received - peer is alive
    }
    
    private suspend fun applySnapshot(snapshot: RoscaSnapshot) {
        // Parse and apply ROSCA state
        // This is a simplified version - full implementation would:
        // 1. Parse each JSON entity
        // 2. Check versions
        // 3. Apply updates
        // 4. Resolve conflicts (creator wins)
    }
    
    private suspend fun applyContributionUpdate(update: EntityUpdate) {
        // TODO: Parse contribution JSON and insert/update
    }
    
    private suspend fun applyMemberUpdate(update: EntityUpdate) {
        // TODO: Parse member JSON and insert/update
    }
    
    private suspend fun logMessage(message: SyncMessage, status: String, error: String?) {
        val log = com.techducat.ajo.data.local.entity.SyncLogEntity(
            roscaId = "",  // Extract from payload
            direction = "INBOUND",
            peerNodeId = message.senderNodeId,
            entityType = message.messageType.name,
            entityId = message.messageId,
            operation = "RECEIVE",
            status = status,
            timestamp = System.currentTimeMillis(),
            errorMessage = error
        )
        
        db.syncLogDao().insert(log)
    }
}
