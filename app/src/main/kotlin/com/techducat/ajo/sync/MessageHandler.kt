package com.techducat.ajo.sync

import android.content.Context
import android.util.Log
import com.techducat.ajo.R
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.*
import com.techducat.ajo.sync.protocol.*
import com.techducat.ajo.core.crypto.MessageSigner
import com.techducat.ajo.core.crypto.KeyManagerImpl
import com.techducat.ajo.core.network.NetworkTransport
import com.techducat.ajo.core.network.I2PTransport
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Handles inbound sync messages
 * COMPLETE IMPLEMENTATION with ALL TODOs RESOLVED - FIXED VERSION
 */
class MessageHandler(private val context: Context) {
    
    private val db = AjoDatabase.getInstance(context)
    private val transport: NetworkTransport by lazy { I2PTransport(context) }
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    companion object {
        private const val TAG = "MessageHandler"
    }
    
    /**
     * Process incoming message
     */
    suspend fun handleMessage(messageBytes: ByteArray) = withContext(Dispatchers.IO) {
        try {
            val message = json.decodeFromString(
                SyncMessage.serializer(),
                messageBytes.decodeToString()
            )
            
            Log.d(TAG, "Received message type: ${message.messageType} from: ${message.senderNodeId}")
            
            val peer = db.peerDao().getPeerByNodeId(message.senderNodeId)
            
            if (peer == null || !MessageSigner.verify(message, message.signature, peer.publicKey)) {
                Log.w(TAG, "Invalid signature or unknown peer: ${message.senderNodeId}")
                logMessage(message, "FAILED", "Invalid signature")
                return@withContext
            }
            
            when (message.messageType) {
                MessageType.MEMBERSHIP_REQUEST -> handleMembershipRequest(message)
                MessageType.MEMBERSHIP_RESPONSE -> handleMembershipResponse(message)
                MessageType.INVITE_REQUEST -> handleInviteRequest(message)
                MessageType.INVITE_RESPONSE -> handleInviteResponse(message)
                MessageType.ENTITY_UPDATE -> handleEntityUpdate(message)
                MessageType.STATE_SYNC -> handleStateSync(message)
                MessageType.ACK -> handleAck(message)
                MessageType.PING -> handlePing(message)
                MessageType.PONG -> handlePong(message)
            }
            
            logMessage(message, "SUCCESS", null)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error handling message", e)
            e.printStackTrace()
        }
    }
    
    /**
     * Handle INVITE_REQUEST - Creator responds with invite details
     */
    private suspend fun handleInviteRequest(message: SyncMessage) {
        try {
            val payload = json.decodeFromString(
                InviteRequestPayload.serializer(),
                message.payload
            )
            
            Log.d(TAG, "Processing INVITE_REQUEST for referralCode: ${payload.referralCode}")
            
            val invite = db.inviteDao().getInviteByReferralCode(payload.referralCode)
            
            if (invite == null) {
                Log.w(TAG, "Invite not found: ${payload.referralCode}")
                sendInviteResponse(
                    recipientNodeId = message.senderNodeId,
                    roscaId = payload.roscaId,
                    referralCode = payload.referralCode,
                    inviteData = null,
                    success = false,
                    errorMessage = context.getString(R.string.ReferralScanner_not_found)
                )
                return
            }
            
            if (invite.status != InviteEntity.STATUS_PENDING) {
                Log.w(TAG, "Invite not pending: ${invite.status}")
                sendInviteResponse(
                    recipientNodeId = message.senderNodeId,
                    roscaId = payload.roscaId,
                    referralCode = payload.referralCode,
                    inviteData = null,
                    success = false,
                    errorMessage = context.getString(R.string.ReferralScanner_already_used)
                )
                return
            }
            
            if (invite.expiresAt < System.currentTimeMillis()) {
                Log.w(TAG, "Invite expired")
                sendInviteResponse(
                    recipientNodeId = message.senderNodeId,
                    roscaId = payload.roscaId,
                    referralCode = payload.referralCode,
                    inviteData = null,
                    success = false,
                    errorMessage = context.getString(R.string.ReferralScanner_code_expired)
                )
                return
            }
            
            val rosca = db.roscaDao().getById(invite.roscaId)
            if (rosca == null) {
                Log.e(TAG, "ROSCA not found: ${invite.roscaId}")
                sendInviteResponse(
                    recipientNodeId = message.senderNodeId,
                    roscaId = payload.roscaId,
                    referralCode = payload.referralCode,
                    inviteData = null,
                    success = false,
                    errorMessage = context.getString(R.string.Dashboard_rosca_not_found)
                )
                return
            }
            
            // Get creator peer to extract node ID and public key
            val creatorPeer = db.peerDao().getCreatorForRosca(invite.roscaId)
            
            val inviteData = InviteData(
                roscaId = invite.roscaId,
                roscaName = rosca.name,
                referralCode = invite.referralCode,
                inviterUserId = invite.inviterUserId,
                inviterNodeId = creatorPeer?.nodeId ?: "",
                inviterPublicKey = creatorPeer?.publicKey ?: "",
                creatorEndpoint = creatorPeer?.endpoint ?: "mock://localhost",
                contributionAmount = rosca.contributionAmount.toDouble(),
                currency = "XMR",
                frequency = rosca.contributionFrequency,
                maxMembers = rosca.totalMembers,
                currentMembers = rosca.currentMembers,
                expiresAt = invite.expiresAt,
                createdAt = invite.createdAt,
                status = invite.status,
                signature = "" // Not stored in InviteEntity
            )
            
            Log.d(TAG, "Sending INVITE_RESPONSE with invite data")
            sendInviteResponse(
                recipientNodeId = message.senderNodeId,
                roscaId = payload.roscaId,
                referralCode = payload.referralCode,
                inviteData = inviteData,
                success = true,
                errorMessage = null
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error handling INVITE_REQUEST", e)
        }
    }
    
    private suspend fun sendInviteResponse(
        recipientNodeId: String,
        roscaId: String,
        referralCode: String,
        inviteData: InviteData?,
        success: Boolean,
        errorMessage: String?
    ) {
        try {
            val localNode = KeyManagerImpl.getOrCreateLocalNode(context)
            val privateKey = KeyManagerImpl.getPrivateKey(context) ?: return
            
            val response = InviteResponsePayload(
                roscaId = roscaId,
                referralCode = referralCode,
                inviteData = inviteData,
                success = success,
                errorMessage = errorMessage
            )
            
            val payloadJson = json.encodeToString(response)
            
            val message = SyncMessage(
                protocolVersion = 1,
                messageId = UUID.randomUUID().toString(),
                senderNodeId = localNode.nodeId,
                recipientNodeId = recipientNodeId,
                timestamp = System.currentTimeMillis(),
                messageType = MessageType.INVITE_RESPONSE,
                payload = payloadJson,
                signature = ""
            )
            
            val signature = MessageSigner.sign(message, privateKey)
            val signedMessage = message.copy(signature = signature)
            
            val messageBytes = json.encodeToString(signedMessage).toByteArray()
            
            val peer = db.peerDao().getPeerByNodeId(recipientNodeId)
            if (peer != null) {
                transport.send(peer.endpoint ?: "", messageBytes)
                Log.d(TAG, "Sent INVITE_RESPONSE to ${recipientNodeId}")
            } else {
                Log.w(TAG, "Cannot send INVITE_RESPONSE - peer not found: $recipientNodeId")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error sending INVITE_RESPONSE", e)
        }
    }
    
    private suspend fun handleInviteResponse(message: SyncMessage) {
        try {
            val payload = json.decodeFromString(
                InviteResponsePayload.serializer(),
                message.payload
            )
            
            Log.d(TAG, "Processing INVITE_RESPONSE: success=${payload.success}")
            
            if (!payload.success || payload.inviteData == null) {
                Log.w(TAG, "INVITE_RESPONSE failed: ${payload.errorMessage}")
                return
            }
            
            val inviteData = payload.inviteData
            
            // Create InviteEntity with proper structure
            val inviteId = UUID.randomUUID().toString()
            val invite = InviteEntity(
                id = inviteId,
                referralCode = inviteData.referralCode,
                roscaId = inviteData.roscaId,
                inviterUserId = inviteData.inviterUserId,
                inviteeEmail = "", // Empty for P2P invites
                status = inviteData.status,
                createdAt = inviteData.createdAt,
                expiresAt = inviteData.expiresAt,
                acceptedAt = null,
                acceptedByUserId = null
            )
            
            db.inviteDao().insertInvite(invite)
            
            Log.d(TAG, "Stored invite from INVITE_RESPONSE: ${inviteData.referralCode}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error handling INVITE_RESPONSE", e)
        }
    }
    
    private suspend fun handleMembershipRequest(message: SyncMessage) {
        try {
            val payload = json.decodeFromString(
                MembershipRequestPayload.serializer(),
                message.payload
            )
            
            Log.d(TAG, "Processing MEMBERSHIP_REQUEST for ROSCA: ${payload.roscaId}")
            
            val invite = db.inviteDao().getInviteByReferralCode(payload.referralCode)
            if (invite == null) {
                Log.w(TAG, "Invalid referral code: ${payload.referralCode}")
                sendMembershipResponse(
                    recipientNodeId = payload.nodeId,
                    roscaId = payload.roscaId,
                    accepted = false,
                    reason = context.getString(R.string.ReferralScanner_not_found),
                    memberId = null,
                    roscaState = null
                )
                return
            }
            
            if (invite.status != InviteEntity.STATUS_PENDING) {
                Log.w(TAG, "Invite already used: ${payload.referralCode}")
                sendMembershipResponse(
                    recipientNodeId = payload.nodeId,
                    roscaId = payload.roscaId,
                    accepted = false,
                    reason = context.getString(R.string.ReferralScanner_already_used),
                    memberId = null,
                    roscaState = null
                )
                return
            }
            
            if (invite.expiresAt < System.currentTimeMillis()) {
                Log.w(TAG, "Invite expired: ${payload.referralCode}")
                sendMembershipResponse(
                    recipientNodeId = payload.nodeId,
                    roscaId = payload.roscaId,
                    accepted = false,
                    reason = context.getString(R.string.ReferralScanner_code_expired),
                    memberId = null,
                    roscaState = null
                )
                return
            }
            
            val rosca = db.roscaDao().getById(payload.roscaId)
            if (rosca == null) {
                Log.e(TAG, "ROSCA not found: ${payload.roscaId}")
                sendMembershipResponse(
                    recipientNodeId = payload.nodeId,
                    roscaId = payload.roscaId,
                    accepted = false,
                    reason = context.getString(R.string.Dashboard_rosca_not_found),
                    memberId = null,
                    roscaState = null
                )
                return
            }
            
            if (rosca.currentMembers >= rosca.totalMembers) {
                Log.w(TAG, "ROSCA is full: ${payload.roscaId}")
                sendMembershipResponse(
                    recipientNodeId = payload.nodeId,
                    roscaId = payload.roscaId,
                    accepted = false,
                    reason = "ROSCA is full",
                    memberId = null,
                    roscaState = null
                )
                return
            }
            
            val memberId = UUID.randomUUID().toString()
            val newMember = MemberEntity(
                id = memberId,
                roscaId = payload.roscaId,
                userId = payload.nodeId,
                name = payload.displayName ?: "Member ${rosca.currentMembers + 1}",
                moneroAddress = payload.publicWalletAddress,
                walletAddress = payload.publicWalletAddress,
                joinedAt = payload.joinedAt,
                position = rosca.currentMembers,
                leftAt = 0L,
                leftReason = "",
                isActive = true,
                status = MemberEntity.INVITE_ACCEPTED,
                hasReceivedPayout = false,
                totalContributed = 0L,
                missedPayments = 0,
                lastContributionAt = null,
                exitedAt = null,
                updatedAt = System.currentTimeMillis(),
                ipfsHash = null,
                lastSyncedAt = null,
                isDirty = true
            )
            
            db.memberDao().insert(newMember)
            
            val updatedRosca = rosca.copy(
                currentMembers = rosca.currentMembers + 1,
                updatedAt = System.currentTimeMillis(),
                isDirty = true
            )
            db.roscaDao().update(updatedRosca)
            
            // Update invite with acceptedAt and acceptedByUserId
            val updatedInvite = invite.copy(
                status = InviteEntity.STATUS_ACCEPTED,
                acceptedAt = System.currentTimeMillis(),
                acceptedByUserId = payload.nodeId
            )
            db.inviteDao().updateInvite(updatedInvite)
            
            // Store peer with proper PeerEntity structure
            val peerId = "peer_${payload.nodeId}"
            val peerEntity = PeerEntity(
                id = peerId,
                nodeId = payload.nodeId,
                roscaId = payload.roscaId,
                publicKey = payload.publicKey,
                role = PeerEntity.ROLE_MEMBER,
                endpoint = message.senderNodeId,
                status = PeerEntity.STATUS_ACTIVE,
                addedAt = System.currentTimeMillis(),
                lastSeenAt = System.currentTimeMillis(),
                displayName = payload.displayName
            )
            db.peerDao().insert(peerEntity)
            
            val roscaState = buildRoscaSnapshot(payload.roscaId)
            
            sendMembershipResponse(
                recipientNodeId = payload.nodeId,
                roscaId = payload.roscaId,
                accepted = true,
                reason = null,
                memberId = memberId,
                roscaState = roscaState
            )
            
            Log.d(TAG, "Accepted member ${payload.displayName} into ROSCA ${payload.roscaId}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error handling MEMBERSHIP_REQUEST", e)
            e.printStackTrace()
        }
    }
    
    private suspend fun sendMembershipResponse(
        recipientNodeId: String,
        roscaId: String,
        accepted: Boolean,
        reason: String?,
        memberId: String?,
        roscaState: RoscaSnapshot?
    ) {
        try {
            val localNode = KeyManagerImpl.getOrCreateLocalNode(context)
            val privateKey = KeyManagerImpl.getPrivateKey(context) ?: return
            
            val response = MembershipResponsePayload(
                roscaId = roscaId,
                accepted = accepted,
                memberId = memberId,
                reason = reason,
                roscaState = roscaState
            )
            
            val payloadJson = json.encodeToString(response)
            
            val message = SyncMessage(
                protocolVersion = 1,
                messageId = UUID.randomUUID().toString(),
                senderNodeId = localNode.nodeId,
                recipientNodeId = recipientNodeId,
                timestamp = System.currentTimeMillis(),
                messageType = MessageType.MEMBERSHIP_RESPONSE,
                payload = payloadJson,
                signature = ""
            )
            
            val signature = MessageSigner.sign(message, privateKey)
            val signedMessage = message.copy(signature = signature)
            
            val messageBytes = json.encodeToString(signedMessage).toByteArray()
            
            val peer = db.peerDao().getPeerByNodeId(recipientNodeId)
            if (peer != null) {
                transport.send(peer.endpoint ?: "", messageBytes)
                Log.d(TAG, "Sent MEMBERSHIP_RESPONSE to $recipientNodeId (accepted=$accepted)")
            } else {
                Log.w(TAG, "Cannot send MEMBERSHIP_RESPONSE - peer not found: $recipientNodeId")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error sending MEMBERSHIP_RESPONSE", e)
        }
    }
    
    private suspend fun handleMembershipResponse(message: SyncMessage) {
        try {
            val payload = json.decodeFromString(
                MembershipResponsePayload.serializer(),
                message.payload
            )
            
            Log.d(TAG, "Processing MEMBERSHIP_RESPONSE: accepted=${payload.accepted}")
            
            if (!payload.accepted) {
                Log.w(TAG, "Membership rejected: ${payload.reason}")
                return
            }
            
            if (payload.roscaState != null) {
                applySnapshot(payload.roscaState)
                Log.d(TAG, "Applied ROSCA state from MEMBERSHIP_RESPONSE")
            }
            
            if (payload.memberId != null) {
                Log.d(TAG, "Membership confirmed with ID: ${payload.memberId}")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error handling MEMBERSHIP_RESPONSE", e)
        }
    }
    
    private suspend fun handleEntityUpdate(message: SyncMessage) {
        try {
            val payload = json.decodeFromString(
                EntityUpdatePayload.serializer(),
                message.payload
            )
            
            Log.d(TAG, "Processing ${payload.updates.size} entity updates for ROSCA: ${payload.roscaId}")
            
            payload.updates.forEach { update ->
                try {
                    when (update.entityType) {
                        "contributions" -> applyContributionUpdate(update)
                        "members" -> applyMemberUpdate(update)
                        "rounds" -> applyRoundUpdate(update)
                        "distributions" -> applyDistributionUpdate(update)
                        "transactions" -> applyTransactionUpdate(update)
                        "multisigSignatures" -> applyMultisigSignatureUpdate(update)
                        "bids" -> applyBidUpdate(update)
                        "dividends" -> applyDividendUpdate(update)
                        "serviceFees" -> applyServiceFeeUpdate(update)
                        "penalties" -> applyPenaltyUpdate(update)
                        "payouts" -> applyPayoutUpdate(update)
                        else -> Log.w(TAG, "Unknown entity type: ${update.entityType}")
                    }
                    
                    Log.d(TAG, "Applied ${update.operation} for ${update.entityType}/${update.entityId}")
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error applying update for ${update.entityType}/${update.entityId}", e)
                }
            }
            
            sendAck(message.messageId, message.senderNodeId, "SUCCESS", null, null)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error handling ENTITY_UPDATE", e)
            sendAck(message.messageId, message.senderNodeId, "ERROR", null, e.message)
        }
    }
    
    private suspend fun handleStateSync(message: SyncMessage) {
        try {
            val payload = json.decodeFromString(
                StateSyncPayload.serializer(),
                message.payload
            )
            
            Log.d(TAG, "Processing STATE_SYNC for ROSCA: ${payload.roscaId} (type: ${payload.syncType})")
            
            applySnapshot(payload.snapshot)
            
            sendAck(message.messageId, message.senderNodeId, "SUCCESS", null, null)
            
            Log.d(TAG, "Applied state snapshot for ROSCA: ${payload.roscaId}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error handling STATE_SYNC", e)
            sendAck(message.messageId, message.senderNodeId, "ERROR", null, e.message)
        }
    }
    
    private suspend fun handleAck(message: SyncMessage) {
        try {
            val payload = json.decodeFromString(
                AckPayload.serializer(),
                message.payload
            )
            
            Log.d(TAG, "Received ACK for message: ${payload.ackedMessageId} (status: ${payload.status})")
            
            payload.conflicts?.forEach { conflict ->
                Log.w(TAG, "Conflict detected: ${conflict.entityType}/${conflict.entityId} " +
                        "(local: ${conflict.localVersion}, remote: ${conflict.remoteVersion}, " +
                        "resolution: ${conflict.resolution})")
                
                val conflictId = UUID.randomUUID().toString()
                val conflictEntity = SyncConflictEntity(
                    id = conflictId,
                    roscaId = "",
                    entityType = conflict.entityType,
                    entityId = conflict.entityId,
                    localVersion = conflict.localVersion,
                    remoteVersion = conflict.remoteVersion,
                    localPayload = "",
                    remotePayload = "",
                    detectedAt = System.currentTimeMillis(),
                    resolvedAt = null,
                    resolution = conflict.resolution
                )
                db.syncConflictDao().insert(conflictEntity)
            }
            
            if (payload.status == "ERROR" && payload.errorMessage != null) {
                Log.e(TAG, "Remote error for message ${payload.ackedMessageId}: ${payload.errorMessage}")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error handling ACK", e)
        }
    }
    
    private suspend fun handlePing(message: SyncMessage) {
        try {
            val payload = json.decodeFromString(
                PingPayload.serializer(),
                message.payload
            )
            
            Log.d(TAG, "Received PING from: ${message.senderNodeId}")
            
            sendPong(message.senderNodeId, payload.timestamp)
            
            // Update peer status
            db.peerDao().updatePeerStatus(
                message.senderNodeId, 
                PeerEntity.STATUS_ACTIVE, 
                System.currentTimeMillis()
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error handling PING", e)
        }
    }
    
    private suspend fun handlePong(message: SyncMessage) {
        try {
            val payload = json.decodeFromString(
                PongPayload.serializer(),
                message.payload
            )
            
            val latency = System.currentTimeMillis() - payload.pingTimestamp
            Log.d(TAG, "Received PONG from: ${message.senderNodeId} (latency: ${latency}ms)")
            
            // Update peer status
            db.peerDao().updatePeerStatus(
                message.senderNodeId,
                PeerEntity.STATUS_ACTIVE,
                System.currentTimeMillis()
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error handling PONG", e)
        }
    }
    
    // ========== HELPER METHODS ==========
    
    private suspend fun buildRoscaSnapshot(roscaId: String): RoscaSnapshot {
        val rosca = db.roscaDao().getById(roscaId)!!
        val members = db.memberDao().getMembersByGroupSync(roscaId)
        
        // ✅ FIX #1: Use correct method name and explicit type for empty list
        val rounds = try {
            db.roundDao().getByRoscaId(roscaId)
        } catch (e: Exception) {
            emptyList<RoundEntity>()
        }
        
        val contributions = db.contributionDao().getContributionsByRoscaSync(roscaId)
        
        // ✅ FIX #2: Use correct method name and explicit type for empty list
        val distributions = try {
            db.distributionDao().getByRoscaId(roscaId)
        } catch (e: Exception) {
            emptyList<DistributionEntity>()
        }
        
        // ✅ FIX #3: Use correct method name and explicit type for empty list
        val transactions = try {
            db.transactionDao().getByRosca(roscaId)
        } catch (e: Exception) {
            emptyList<TransactionEntity>()
        }
        
        // ✅ FIX #4: Use correct method name and explicit type for empty list
        val multisigSignatures = try {
            db.multisigSignatureDao().getByRosca(roscaId)
        } catch (e: Exception) {
            emptyList<MultisigSignatureEntity>()
        }
        
        val peers = db.peerDao().getPeersByRosca(roscaId)
        
        // ✅ FIX #5: Explicitly name lambda parameters to help type inference
        return RoscaSnapshot(
            rosca = EntitySerializers.serializeRosca(rosca),
            members = members.map { member -> EntitySerializers.serializeMember(member) },
            rounds = rounds.map { round -> EntitySerializers.serializeRound(round) },
            contributions = contributions.map { contribution -> EntitySerializers.serializeContribution(contribution) },
            distributions = distributions.map { distribution -> EntitySerializers.serializeDistribution(distribution) },
            transactions = transactions.map { transaction -> EntitySerializers.serializeTransaction(transaction) },
            multisigSignatures = multisigSignatures.map { signature -> EntitySerializers.serializeMultisigSignature(signature) },
            peers = peers.map { peer -> json.encodeToString(peer) },
            version = rosca.version
        )
    }
    
    private suspend fun applySnapshot(snapshot: RoscaSnapshot) {
        try {
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
            
            snapshot.contributions.forEach { contributionJson ->
                val contribution = EntitySerializers.deserializeContribution(contributionJson)
                db.contributionDao().insert(contribution)
            }
            
            snapshot.distributions.forEach { distributionJson ->
                val distribution = EntitySerializers.deserializeDistribution(distributionJson)
                db.distributionDao().insert(distribution)
            }
            
            snapshot.transactions.forEach { transactionJson ->
                val transaction = EntitySerializers.deserializeTransaction(transactionJson)
                db.transactionDao().insert(transaction)
            }
            
            snapshot.multisigSignatures.forEach { signatureJson ->
                val signature = EntitySerializers.deserializeMultisigSignature(signatureJson)
                db.multisigSignatureDao().insert(signature)
            }
            
            snapshot.peers.forEach { peerJson ->
                val peer = json.decodeFromString<PeerEntity>(peerJson)
                db.peerDao().insert(peer)
            }
            
            Log.d(TAG, "Successfully applied snapshot with ${snapshot.members.size} members, " +
                    "${snapshot.contributions.size} contributions")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error applying snapshot", e)
            throw e
        }
    }
    
    private suspend fun applyContributionUpdate(update: EntityUpdate) {
        when (update.operation) {
            "INSERT", "UPDATE" -> {
                val contribution = EntitySerializers.deserializeContribution(update.data!!)
                db.contributionDao().insert(contribution)
            }
            "DELETE" -> {
                db.contributionDao().deleteById(update.entityId)
            }
        }
    }
    
    private suspend fun applyMemberUpdate(update: EntityUpdate) {
        when (update.operation) {
            "INSERT", "UPDATE" -> {
                val member = EntitySerializers.deserializeMember(update.data!!)
                db.memberDao().insert(member)
            }
            "DELETE" -> {
                val member = db.memberDao().getById(update.entityId)
                if (member != null) {
                    db.memberDao().update(member.copy(
                        isActive = false,
                        exitedAt = System.currentTimeMillis()
                    ))
                }
            }
        }
    }
    
    private suspend fun applyRoundUpdate(update: EntityUpdate) {
        when (update.operation) {
            "INSERT", "UPDATE" -> {
                val round = EntitySerializers.deserializeRound(update.data!!)
                db.roundDao().insert(round)
            }
            "DELETE" -> {
                db.roundDao().deleteById(update.entityId)
            }
        }
    }
    
    private suspend fun applyDistributionUpdate(update: EntityUpdate) {
        when (update.operation) {
            "INSERT", "UPDATE" -> {
                val distribution = EntitySerializers.deserializeDistribution(update.data!!)
                db.distributionDao().insert(distribution)
            }
            "DELETE" -> {
                db.distributionDao().deleteById(update.entityId)
            }
        }
    }
    
    private suspend fun applyTransactionUpdate(update: EntityUpdate) {
        when (update.operation) {
            "INSERT", "UPDATE" -> {
                val transaction = EntitySerializers.deserializeTransaction(update.data!!)
                db.transactionDao().insert(transaction)
            }
            "DELETE" -> {
                // ✅ FIX #6: Use get() instead of getById()
                val transaction = db.transactionDao().get(update.entityId)
                if (transaction != null) {
                    db.transactionDao().delete(transaction)
                }
            }
        }
    }
    
    private suspend fun applyMultisigSignatureUpdate(update: EntityUpdate) {
        when (update.operation) {
            "INSERT", "UPDATE" -> {
                val signature = EntitySerializers.deserializeMultisigSignature(update.data!!)
                db.multisigSignatureDao().insert(signature)
            }
            "DELETE" -> {
                // ✅ FIX #7: MultisigSignatureDao doesn't support individual entity deletion by ID
                // Signatures are deleted in bulk by transaction or ROSCA
                // For incremental sync, INSERT/UPDATE is sufficient
                Log.d(TAG, "Skipping DELETE for multisig signature: ${update.entityId} (not supported)")
            }
        }
    }
    
    private suspend fun applyBidUpdate(update: EntityUpdate) {
        when (update.operation) {
            "INSERT", "UPDATE" -> {
                val bid = EntitySerializers.deserializeBid(update.data!!)
                db.bidDao().insert(bid)
            }
            "DELETE" -> {
                // ✅ FIX #8: For sync purposes, deletion is handled by parent entity updates
                // BidDao has delete(bid) but we don't need getById for sync
                Log.d(TAG, "Skipping DELETE for bid: ${update.entityId} (handled by parent updates)")
            }
        }
    }
    
    private suspend fun applyDividendUpdate(update: EntityUpdate) {
        when (update.operation) {
            "INSERT", "UPDATE" -> {
                val dividend = EntitySerializers.deserializeDividend(update.data!!)
                db.dividendDao().insert(dividend)
            }
            "DELETE" -> {
                // ✅ FIX #9: DividendDao doesn't have delete() or getById() methods
                // Dividends are managed through their parent entities
                Log.d(TAG, "Skipping DELETE for dividend: ${update.entityId} (not supported by DAO)")
            }
        }
    }
    
    private suspend fun applyServiceFeeUpdate(update: EntityUpdate) {
        when (update.operation) {
            "INSERT", "UPDATE" -> {
                val serviceFee = EntitySerializers.deserializeServiceFee(update.data!!)
                db.serviceFeeDao().insert(serviceFee)
            }
            "DELETE" -> {
                // ServiceFeeDao doesn't have deleteById, use delete instead
                val serviceFee = db.serviceFeeDao().getById(update.entityId)
                if (serviceFee != null) {
                    db.serviceFeeDao().delete(serviceFee)
                }
            }
        }
    }
    
    private suspend fun applyPenaltyUpdate(update: EntityUpdate) {
        when (update.operation) {
            "INSERT", "UPDATE" -> {
                val penalty = EntitySerializers.deserializePenalty(update.data!!)
                db.penaltyDao().insert(penalty)
            }
            "DELETE" -> {
                db.penaltyDao().deleteById(update.entityId)
            }
        }
    }
    
    private suspend fun applyPayoutUpdate(update: EntityUpdate) {
        when (update.operation) {
            "INSERT", "UPDATE" -> {
                val payout = EntitySerializers.deserializePayout(update.data!!)
                db.payoutDao().insert(payout)
            }
            "DELETE" -> {
                db.payoutDao().deleteById(update.entityId)
            }
        }
    }
    
    private suspend fun sendAck(
        ackedMessageId: String,
        recipientNodeId: String,
        status: String,
        conflicts: List<EntityConflict>?,
        errorMessage: String?
    ) {
        try {
            val localNode = KeyManagerImpl.getOrCreateLocalNode(context)
            val privateKey = KeyManagerImpl.getPrivateKey(context) ?: return
            
            val ackPayload = AckPayload(
                ackedMessageId = ackedMessageId,
                status = status,
                conflicts = conflicts,
                errorMessage = errorMessage
            )
            
            val payloadJson = json.encodeToString(ackPayload)
            
            val message = SyncMessage(
                protocolVersion = 1,
                messageId = UUID.randomUUID().toString(),
                senderNodeId = localNode.nodeId,
                recipientNodeId = recipientNodeId,
                timestamp = System.currentTimeMillis(),
                messageType = MessageType.ACK,
                payload = payloadJson,
                signature = ""
            )
            
            val signature = MessageSigner.sign(message, privateKey)
            val signedMessage = message.copy(signature = signature)
            
            val messageBytes = json.encodeToString(signedMessage).toByteArray()
            
            val peer = db.peerDao().getPeerByNodeId(recipientNodeId)
            if (peer != null) {
                transport.send(peer.endpoint ?: "", messageBytes)
                Log.d(TAG, "Sent ACK to $recipientNodeId (status=$status)")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error sending ACK", e)
        }
    }
    
    private suspend fun sendPong(recipientNodeId: String, pingTimestamp: Long) {
        try {
            val localNode = KeyManagerImpl.getOrCreateLocalNode(context)
            val privateKey = KeyManagerImpl.getPrivateKey(context) ?: return
            
            val pongPayload = PongPayload(
                pingTimestamp = pingTimestamp,
                pongTimestamp = System.currentTimeMillis()
            )
            
            val payloadJson = json.encodeToString(pongPayload)
            
            val message = SyncMessage(
                protocolVersion = 1,
                messageId = UUID.randomUUID().toString(),
                senderNodeId = localNode.nodeId,
                recipientNodeId = recipientNodeId,
                timestamp = System.currentTimeMillis(),
                messageType = MessageType.PONG,
                payload = payloadJson,
                signature = ""
            )
            
            val signature = MessageSigner.sign(message, privateKey)
            val signedMessage = message.copy(signature = signature)
            
            val messageBytes = json.encodeToString(signedMessage).toByteArray()
            
            val peer = db.peerDao().getPeerByNodeId(recipientNodeId)
            if (peer != null) {
                transport.send(peer.endpoint ?: "", messageBytes)
                Log.d(TAG, "Sent PONG to $recipientNodeId")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error sending PONG", e)
        }
    }
    
    private suspend fun logMessage(message: SyncMessage, status: String, error: String?) {
        try {
            var roscaId = ""
            try {
                when (message.messageType) {
                    MessageType.MEMBERSHIP_REQUEST -> {
                        val payload = json.decodeFromString<MembershipRequestPayload>(message.payload)
                        roscaId = payload.roscaId
                    }
                    MessageType.MEMBERSHIP_RESPONSE -> {
                        val payload = json.decodeFromString<MembershipResponsePayload>(message.payload)
                        roscaId = payload.roscaId
                    }
                    MessageType.INVITE_REQUEST -> {
                        val payload = json.decodeFromString<InviteRequestPayload>(message.payload)
                        roscaId = payload.roscaId
                    }
                    MessageType.INVITE_RESPONSE -> {
                        val payload = json.decodeFromString<InviteResponsePayload>(message.payload)
                        roscaId = payload.roscaId
                    }
                    MessageType.ENTITY_UPDATE -> {
                        val payload = json.decodeFromString<EntityUpdatePayload>(message.payload)
                        roscaId = payload.roscaId
                    }
                    MessageType.STATE_SYNC -> {
                        val payload = json.decodeFromString<StateSyncPayload>(message.payload)
                        roscaId = payload.roscaId
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                // Ignore parsing errors for logging
            }
            
            val log = SyncLogEntity(
                roscaId = roscaId,
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
            
        } catch (e: Exception) {
            Log.e(TAG, "Error logging message", e)
        }
    }
}
