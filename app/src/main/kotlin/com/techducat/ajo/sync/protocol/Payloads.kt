package com.techducat.ajo.sync.protocol

import kotlinx.serialization.Serializable

/**
 * MEMBERSHIP_REQUEST payload
 * Sent by invitee after consuming referral
 */
@Serializable
data class MembershipRequestPayload(
    val roscaId: String,
    val referralCode: String,
    val nodeId: String,
    val publicKey: String,
    val displayName: String? = null,
    val publicWalletAddress: String? = null,
    val joinedAt: Long
)

/**
 * MEMBERSHIP_RESPONSE payload
 * Sent by creator accepting/rejecting member
 */
@Serializable
data class MembershipResponsePayload(
    val roscaId: String,
    val accepted: Boolean,
    val memberId: String? = null,
    val reason: String? = null,
    val roscaState: RoscaSnapshot? = null
)

/**
 * Complete ROSCA state snapshot
 */
@Serializable
data class RoscaSnapshot(
    val rosca: String,              // JSON of RoscaEntity
    val members: List<String>,      // JSON array
    val rounds: List<String> = emptyList(),
    val contributions: List<String> = emptyList(),
    val distributions: List<String> = emptyList(),
    val transactions: List<String> = emptyList(),
    val multisigSignatures: List<String> = emptyList(),
    val peers: List<String> = emptyList(),
    val version: Long
)

/**
 * ENTITY_UPDATE payload
 */
@Serializable
data class EntityUpdatePayload(
    val roscaId: String,
    val updates: List<EntityUpdate>
)

@Serializable
data class EntityUpdate(
    val entityType: String,
    val entityId: String,
    val operation: String,         // INSERT, UPDATE, DELETE
    val version: Long,
    val data: String? = null,      // JSON (null for DELETE)
    val lastModifiedBy: String,
    val lastModifiedAt: Long
)

/**
 * STATE_SYNC payload
 */
@Serializable
data class StateSyncPayload(
    val roscaId: String,
    val syncType: String,          // FULL, INCREMENTAL
    val snapshot: RoscaSnapshot
)

/**
 * ACK payload
 */
@Serializable
data class AckPayload(
    val ackedMessageId: String,
    val status: String,            // SUCCESS, CONFLICT, ERROR
    val conflicts: List<EntityConflict>? = null,
    val errorMessage: String? = null
)

@Serializable
data class EntityConflict(
    val entityType: String,
    val entityId: String,
    val localVersion: Long,
    val remoteVersion: Long,
    val resolution: String
)

/**
 * PING/PONG payloads
 */
@Serializable
data class PingPayload(
    val timestamp: Long
)

@Serializable
data class PongPayload(
    val pingTimestamp: Long,
    val pongTimestamp: Long
)
