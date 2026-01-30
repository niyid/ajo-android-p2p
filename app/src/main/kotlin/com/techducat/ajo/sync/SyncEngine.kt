package com.techducat.ajo.sync

import android.content.Context
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.SyncQueueEntity
import com.techducat.ajo.sync.protocol.*
import com.techducat.ajo.core.crypto.MessageSigner
import com.techducat.ajo.core.crypto.KeyManagerImpl
import com.techducat.ajo.core.network.NetworkTransport
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Core sync engine - processes outbound sync queue
 */
class SyncEngine(
    private val context: Context,
    private val transport: NetworkTransport
) {
    private val db = AjoDatabase.getInstance(context)
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    /**
     * Process pending sync queue
     */
    suspend fun processSyncQueue() = withContext(Dispatchers.IO) {
        val localNode = KeyManagerImpl.getOrCreateLocalNode(context)
        val privateKey = KeyManagerImpl.getPrivateKey(context) ?: return@withContext
        
        val pending = db.syncQueueDao().getPendingSyncs()
        
        pending.forEach { item ->
            try {
                sendSyncMessage(item, localNode.nodeId, privateKey)
                
                // Delete on success
                db.syncQueueDao().delete(item)
                
                // Log success
                logSync(item, "SUCCESS", null)
                
            } catch (e: Exception) {
                // Increment attempts
                val updated = item.copy(
                    attempts = item.attempts + 1,
                    lastAttemptAt = System.currentTimeMillis()
                )
                
                if (updated.attempts >= updated.maxAttempts) {
                    // Max attempts reached - log and delete
                    logSync(item, "FAILED", e.message)
                    db.syncQueueDao().delete(item)
                } else {
                    // Retry later
                    db.syncQueueDao().insert(updated)
                }
            }
        }
    }
    
    /**
     * Send single sync message
     */
    private suspend fun sendSyncMessage(
        item: SyncQueueEntity,
        senderNodeId: String,
        privateKey: String
    ) {
        // Get sync target for this ROSCA
        val targets = db.syncTargetDao().getEnabledTargets(item.roscaId)
        
        if (targets.isEmpty()) {
            throw IllegalStateException("No sync targets for ROSCA ${item.roscaId}")
        }
        
        targets.forEach { target ->
            val peer = db.peerDao().getPeerByNodeId(target.targetPeerId.removePrefix("peer_"))
                ?: throw IllegalStateException("Peer not found: ${target.targetPeerId}")
            
            // Build sync message
            val message = SyncMessage(
                protocolVersion = 1,
                messageId = MessageSigner.generateMessageId(),
                senderNodeId = senderNodeId,
                recipientNodeId = peer.nodeId,
                timestamp = System.currentTimeMillis(),
                messageType = MessageType.ENTITY_UPDATE,
                payload = item.payload,
                signature = ""
            )
            
            // Sign message
            val signedMessage = message.copy(
                signature = MessageSigner.sign(message, privateKey)
            )
            
            // Serialize to JSON
            val messageJson = json.encodeToString(SyncMessage.serializer(), signedMessage)
            
            // Send over network
            transport.send(peer.endpoint ?: "", messageJson.toByteArray())
            
            // Update sync target
            db.syncTargetDao().updateLastSuccess(target.id, System.currentTimeMillis())
        }
    }
    
    /**
     * Queue entity update for sync
     */
    suspend fun queueUpdate(
        entityType: String,
        entityId: String,
        roscaId: String,
        operation: String,
        payload: String
    ) = withContext(Dispatchers.IO) {
        val item = SyncQueueEntity(
            entityType = entityType,
            entityId = entityId,
            operation = operation,
            payload = payload,
            createdAt = System.currentTimeMillis(),
            lastAttemptAt = null
        )
        
        db.syncQueueDao().insert(item)
    }
    
    private suspend fun logSync(item: SyncQueueEntity, status: String, error: String?) {
        val log = com.techducat.ajo.data.local.entity.SyncLogEntity(
            roscaId = item.roscaId,
            direction = "OUTBOUND",
            peerNodeId = "",  // Would need to track which peer
            entityType = item.entityType,
            entityId = item.entityId,
            operation = item.operation,
            status = status,
            timestamp = System.currentTimeMillis(),
            errorMessage = error
        )
        
        db.syncLogDao().insert(log)
    }
}
