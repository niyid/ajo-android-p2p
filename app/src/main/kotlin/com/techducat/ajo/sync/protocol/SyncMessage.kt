package com.techducat.ajo.sync.protocol

import kotlinx.serialization.Serializable

/**
 * Top-level sync message envelope
 * All P2P messages use this wrapper
 */
@Serializable
data class SyncMessage(
    val protocolVersion: Int = 1,
    val messageId: String,              // UUID for deduplication
    val senderNodeId: String,
    val recipientNodeId: String,
    val timestamp: Long,
    val messageType: MessageType,
    val payload: String,                // JSON-encoded specific payload
    val signature: String               // Ed25519 signature
)

@Serializable
enum class MessageType {
    MEMBERSHIP_REQUEST,
    MEMBERSHIP_RESPONSE,
    INVITE_REQUEST,
    INVITE_RESPONSE,
    ENTITY_UPDATE,
    STATE_SYNC,
    ACK,
    PING,
    PONG
}
