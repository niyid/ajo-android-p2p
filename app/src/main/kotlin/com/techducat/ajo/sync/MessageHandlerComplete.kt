package com.techducat.ajo.sync

import android.content.Context
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.*
import com.techducat.ajo.sync.protocol.*
import com.techducat.ajo.core.crypto.MessageSigner
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Complete message handler with full entity deserialization
 */
class MessageHandlerComplete(private val context: Context) {
    
    private val db = AjoDatabase.getInstance(context)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    suspend fun handleMessage(messageBytes: ByteArray) = withContext(Dispatchers.IO) {
        try {
            val message = json.decodeFromString(
                SyncMessage.serializer(),
                messageBytes.decodeToString()
            )
            
            // Verify signature
            val peer = db.peerDao().getPeerByNodeId(message.senderNodeId)
            if (peer == null || !MessageSigner.verify(message, message.signature, peer.publicKey)) {
                return@withContext
            }
            
            // Process
            when (message.messageType) {
                MessageType.MEMBERSHIP_REQUEST -> handleMembershipRequest(message)
                MessageType.MEMBERSHIP_RESPONSE -> handleMembershipResponse(message)
                MessageType.ENTITY_UPDATE -> handleEntityUpdate(message)
                MessageType.STATE_SYNC -> handleStateSync(message)
                MessageType.ACK -> handleAck(message)
                MessageType.PING -> handlePing(message)
                MessageType.PONG -> handlePong(message)
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private suspend fun handleMembershipRequest(message: SyncMessage) {
        val payload = json.decodeFromString(
            MembershipRequestPayload.serializer(),
            message.payload
        )
        
        // Accept member
        val member = MemberEntity(
            id = payload.nodeId,
            roscaId = payload.roscaId,
            userId = payload.nodeId,
            name = payload.displayName ?: "Unknown",
            moneroAddress = payload.publicWalletAddress,
            joinedAt = payload.joinedAt,
            position = 0,
            leftAt = 0,
            leftReason = "",
            isActive = true,
            status = "active"
        )
        
        db.memberDao().insert(member)
    }
    
    private suspend fun handleMembershipResponse(message: SyncMessage) {
        val payload = json.decodeFromString(
            MembershipResponsePayload.serializer(),
            message.payload
        )
        
        if (payload.accepted && payload.roscaState != null) {
            applySnapshot(payload.roscaState)
        }
    }
    
    private suspend fun handleEntityUpdate(message: SyncMessage) {
        val payload = json.decodeFromString(
            EntityUpdatePayload.serializer(),
            message.payload
        )
        
        payload.updates.forEach { update ->
            when (update.entityType) {
                "roscas" -> applyRoscaUpdate(update)
                "members" -> applyMemberUpdate(update)
                "contributions" -> applyContributionUpdate(update)
                "rounds" -> applyRoundUpdate(update)
                "distributions" -> applyDistributionUpdate(update)
                "bids" -> applyBidUpdate(update)
                "transactions" -> applyTransactionUpdate(update)
                "multisig_signatures" -> applyMultisigSignatureUpdate(update)
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
        // Message delivered successfully
    }
    
    private suspend fun handlePing(message: SyncMessage) {
        // TODO: Send pong
    }
    
    private suspend fun handlePong(message: SyncMessage) {
        // Peer is alive
    }
    
    // ========== Entity Update Handlers ==========
    
    private suspend fun applyRoscaUpdate(update: EntityUpdate) {
        if (update.data != null) {
            val rosca = EntitySerializers.deserializeRosca(update.data)
            db.roscaDao().insert(rosca)
        }
    }
    
    private suspend fun applyMemberUpdate(update: EntityUpdate) {
        if (update.data != null) {
            val member = EntitySerializers.deserializeMember(update.data)
            db.memberDao().insert(member)
        }
    }
    
    private suspend fun applyContributionUpdate(update: EntityUpdate) {
        if (update.data != null) {
            val contribution = EntitySerializers.deserializeContribution(update.data)
            db.contributionDao().insert(contribution)
        }
    }
    
    private suspend fun applyRoundUpdate(update: EntityUpdate) {
        if (update.data != null) {
            val round = EntitySerializers.deserializeRound(update.data)
            db.roundDao().insert(round)
        }
    }
    
    private suspend fun applyDistributionUpdate(update: EntityUpdate) {
        if (update.data != null) {
            val distribution = EntitySerializers.deserializeDistribution(update.data)
            db.distributionDao().insert(distribution)
        }
    }
    
    private suspend fun applyBidUpdate(update: EntityUpdate) {
        if (update.data != null) {
            val bid = EntitySerializers.deserializeBid(update.data)
            db.bidDao().insert(bid)
        }
    }
    
    private suspend fun applyTransactionUpdate(update: EntityUpdate) {
        if (update.data != null) {
            val transaction = EntitySerializers.deserializeTransaction(update.data)
            db.transactionDao().insert(transaction)
        }
    }
    
    private suspend fun applyMultisigSignatureUpdate(update: EntityUpdate) {
        if (update.data != null) {
            val sig = EntitySerializers.deserializeMultisigSignature(update.data)
            db.multisigSignatureDao().insert(sig)
        }
    }
    
    private suspend fun applySnapshot(snapshot: RoscaSnapshot) {
        // Apply full snapshot
        val rosca = EntitySerializers.deserializeRosca(snapshot.rosca)
        db.roscaDao().insert(rosca)
        
        snapshot.members.forEach { memberJson ->
            val member = EntitySerializers.deserializeMember(memberJson)
            db.memberDao().insert(member)
        }
        
        snapshot.rounds.forEach { roundJson ->
            val round = EntitySerializers.deserializeRound(roundJson)
            db.roundDao().insert(round)
        }
        
        snapshot.contributions.forEach { contribJson ->
            val contrib = EntitySerializers.deserializeContribution(contribJson)
            db.contributionDao().insert(contrib)
        }
    }
}
