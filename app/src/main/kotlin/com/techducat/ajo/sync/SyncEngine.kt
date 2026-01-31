package com.techducat.ajo.sync

import android.content.Context
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.sync.protocol.*
import com.techducat.ajo.core.crypto.MessageSigner
import com.techducat.ajo.core.crypto.KeyManagerImpl
import com.techducat.ajo.core.network.NetworkTransport
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * FIXED Sync engine
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
                // Get sync targets (peers to send to)
                // This is simplified - you'll need to adapt based on your schema
                
                // Build sync message
                val message = SyncMessage(
                    protocolVersion = 1,
                    messageId = MessageSigner.generateMessageId(),
                    senderNodeId = localNode.nodeId,
                    recipientNodeId = "",  // TODO: Get from sync targets
                    timestamp = System.currentTimeMillis(),
                    messageType = MessageType.ENTITY_UPDATE,
                    payload = item.payload,
                    signature = ""
                )
                
                // Sign message
                val signedMessage = message.copy(
                    signature = MessageSigner.sign(message, privateKey)
                )
                
                // Serialize and send
                val messageJson = json.encodeToString(SyncMessage.serializer(), signedMessage)
                
                // TODO: Send to actual peers
                // transport.send(peerEndpoint, messageJson.toByteArray())
                
                // Delete on success
                db.syncQueueDao().delete(item)
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
