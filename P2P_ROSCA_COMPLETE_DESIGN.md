# Complete P2P ROSCA Design Specification
## Ajo Android App - Peer-to-Peer Sync Architecture

**Version**: 1.0  
**Date**: January 29, 2026  
**Status**: Production-Ready Specification

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [Core Design Principles](#core-design-principles)
3. [Architecture Overview](#architecture-overview)
4. [Database Schema](#database-schema)
5. [Sync Protocol](#sync-protocol)
6. [Referral System](#referral-system)
7. [State Machine](#state-machine)
8. [Multisig Coordination](#multisig-coordination)
9. [Security Model](#security-model)
10. [Implementation Roadmap](#implementation-roadmap)
11. [Installation Requirements](#installation-requirements)
12. [FAQ & Edge Cases](#faq--edge-cases)

---

## Executive Summary

### What This Design Achieves

Transform the Ajo Android app from a single-device simulation into a **fully peer-to-peer distributed system** where:

- Multiple users run the app on separate devices
- Each device maintains its own local Room database
- Databases automatically sync via I2P network
- Wallet files never leave devices (privacy-preserving)
- Monero multisig coordination works across devices
- No central server required

### The Key Insight

> **"The referral code writes enough metadata that the sync engine can no longer avoid doing the right thing."**

This is **configuration by data**, not code:
1. User consumes referral code
2. Code writes peer topology to local database
3. Sync targets are now configured
4. Background worker automatically syncs
5. No manual network setup needed

### What Users Experience

**Creator Side**:
```
1. Create ROSCA
2. Generate QR code
3. Share with friends
4. Watch members join automatically
```

**Member Side**:
```
1. Scan QR code
2. Confirm join
3. App syncs in background
4. See ROSCA details appear
```

**It feels automatic, but it's actually deterministic choreography.**

---

## Core Design Principles

### 1. Configuration by Data
Referral codes contain all bootstrap information:
- ROSCA ID
- Creator identity (node ID + public key)
- Creator's I2P endpoints
- Role assignment (MEMBER)
- ROSCA metadata (amount, frequency, etc.)

When consumed, this **programs the sync behavior**.

### 2. Explicit Sync Boundaries

Every entity is classified:

| Category | Examples | Syncs? |
|----------|----------|--------|
| **ROSCA State** | ROSCAs, Members, Contributions, Rounds | ✅ YES |
| **Coordination** | Multisig signatures, Transactions | ✅ YES |
| **Wallet Secrets** | Private keys, wallet files, passwords | ❌ NEVER |
| **Device Config** | I2P endpoints, local paths | ❌ NEVER |
| **Sync Metadata** | Queue, logs, conflicts | ❌ NEVER |

### 3. Creator Authority
- Creator is the **authoritative source** for ROSCA state
- Members push changes to creator
- Creator validates and broadcasts to all members
- In conflicts: **creator always wins**
- Simplifies conflict resolution dramatically

### 4. Local-First
- Each device has complete, independent database
- App works offline (queues changes)
- Sync happens in background
- No blocking operations
- Eventual consistency

### 5. Privacy by Structure
- Wallet files stored in device-local paths
- `LocalWalletEntity` separate from synced `MemberEntity`
- Only public addresses sync
- Private keys never serialized into sync messages
- Impossible to accidentally leak secrets

---

## Architecture Overview

### System Topology

```
┌─────────────────────────────────────────────────────────────┐
│                         ROSCA                                │
│                                                              │
│  ┌───────────────┐                                          │
│  │   CREATOR     │◄──────────────────────┐                  │
│  │   (Device A)  │                       │                  │
│  │               │                       │                  │
│  │ Room Database │                       │                  │
│  │ I2P Endpoint  │                       │                  │
│  └───────┬───────┘                       │                  │
│          │                               │                  │
│          │ Sync                          │ Sync             │
│          ▼                               │                  │
│  ┌───────────────┐               ┌──────┴──────┐           │
│  │   MEMBER 1    │               │  MEMBER 2   │           │
│  │   (Device B)  │               │ (Device C)  │           │
│  │               │               │             │           │
│  │ Room Database │               │ Room Database           │
│  │ I2P Endpoint  │               │ I2P Endpoint│           │
│  └───────────────┘               └─────────────┘           │
│                                                              │
│  Each device:                                               │
│  - Independent SQLite database                              │
│  - Own wallet files (local only)                            │
│  - Own I2P identity                                         │
│  - Syncs ONLY with creator (not peer-to-peer mesh)         │
└─────────────────────────────────────────────────────────────┘
```

### Sync Flow (Member → Creator)

```
Device B (Member)                     Device A (Creator)
┌─────────────────┐                  ┌─────────────────┐
│                 │                  │                 │
│ 1. User makes   │                  │                 │
│    contribution │                  │                 │
│                 │                  │                 │
│ 2. Write to     │                  │                 │
│    Room DB      │                  │                 │
│    ↓            │                  │                 │
│ 3. SQL trigger  │                  │                 │
│    adds to      │                  │                 │
│    sync_queue   │                  │                 │
│    ↓            │                  │                 │
│ 4. SyncWorker   │                  │                 │
│    reads queue  │                  │                 │
│    ↓            │                  │                 │
│ 5. Build JSON   │                  │                 │
│    message      │                  │                 │
│    ↓            │                  │                 │
│ 6. Sign with    │                  │                 │
│    Ed25519      │                  │                 │
│    ↓            │                  │                 │
│ 7. Send via I2P │─────────────────>│ 8. Receive msg  │
│                 │                  │    ↓            │
│                 │                  │ 9. Verify sig   │
│                 │                  │    ↓            │
│                 │                  │ 10. Validate    │
│                 │                  │     data        │
│                 │                  │     ↓           │
│                 │                  │ 11. Write to    │
│                 │                  │     Room DB     │
│                 │                  │     ↓           │
│ 13. Delete from │<─────────────────│ 12. Send ACK    │
│     sync_queue  │                  │                 │
│                 │                  │ 14. Broadcast   │
│                 │                  │     to other    │
│                 │                  │     members     │
└─────────────────┘                  └─────────────────┘
```

### Component Architecture

```
┌────────────────────────────────────────────────────────────┐
│                     Application Layer                       │
├────────────────────────────────────────────────────────────┤
│  UI Components                                              │
│  - Activities/Fragments                                     │
│  - ViewModels                                               │
│  - Composables                                              │
├────────────────────────────────────────────────────────────┤
│  Business Logic Layer                                       │
├────────────────────────────────────────────────────────────┤
│  Repositories                                               │
│  - RoscaRepository                                          │
│  - WalletRepository                                         │
│  - UserRepository                                           │
├────────────────────────────────────────────────────────────┤
│  Sync Layer (NEW)                                           │
├────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐  │
│  │ SyncWorker   │  │ SyncEngine   │  │ MessageHandler  │  │
│  │ (WorkManager)│  │              │  │                 │  │
│  └──────────────┘  └──────────────┘  └─────────────────┘  │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐  │
│  │ReferralCodec │  │MessageSigner │  │ConflictResolver │  │
│  └──────────────┘  └──────────────┘  └─────────────────┘  │
├────────────────────────────────────────────────────────────┤
│  Data Layer                                                 │
├────────────────────────────────────────────────────────────┤
│  Room Database                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐  │
│  │ Synced Data  │  │ Local Data   │  │ Sync Metadata   │  │
│  │ - Roscas     │  │ - LocalNode  │  │ - SyncQueue     │  │
│  │ - Members    │  │ - LocalWallet│  │ - SyncLog       │  │
│  │ - Rounds     │  │ - Invites    │  │ - Conflicts     │  │
│  └──────────────┘  └──────────────┘  └─────────────────┘  │
├────────────────────────────────────────────────────────────┤
│  Network Layer (NEW)                                        │
├────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐                        │
│  │ I2PManager   │  │ KeyManager   │                        │
│  │              │  │ (Ed25519)    │                        │
│  └──────────────┘  └──────────────┘                        │
├────────────────────────────────────────────────────────────┤
│  Blockchain Layer (Existing)                                │
├────────────────────────────────────────────────────────────┤
│  Monero Wallet                                              │
│  - Wallet files (device-local)                              │
│  - RPC client                                               │
│  - Multisig coordinator                                     │
└────────────────────────────────────────────────────────────┘
```

---

## Database Schema

### Layer 1: Peer Topology & Identity

#### LocalNodeEntity
**Purpose**: This device's P2P identity  
**Sync**: NEVER

```kotlin
@Entity(tableName = "local_node")
data class LocalNodeEntity(
    @PrimaryKey val nodeId: String,      // Derived from public key
    val publicKey: String,                // Ed25519 public key (hex)
    val privateKeyEncrypted: String,      // Encrypted with Android Keystore
    val createdAt: Long,
    val lastSyncAt: Long?
)
```

**Usage**:
```kotlin
// Created once on first app launch
val node = KeyManager.getOrCreateLocalNode(context)
```

#### PeerEntity
**Purpose**: Registry of other nodes in ROSCAs  
**Sync**: Partial (public keys yes, endpoints no)

```kotlin
@Entity(
    tableName = "peers",
    indices = [Index("nodeId", unique = true), Index("roscaId")]
)
data class PeerEntity(
    @PrimaryKey val id: String,
    val nodeId: String,                   // Unique node identifier
    val roscaId: String,                  // Which ROSCA this peer belongs to
    val publicKey: String,                // For signature verification
    val role: String,                     // CREATOR, MEMBER
    val endpoint: String?,                // I2P address (local config only)
    val status: String,                   // ACTIVE, OFFLINE, EXITED
    val addedAt: Long,
    val lastSeenAt: Long?
)
```

**Example**:
```kotlin
// Written when referral is consumed
PeerEntity(
    id = "peer_abc123",
    nodeId = "node_creator",
    roscaId = "rosca_xyz",
    publicKey = "ed25519_pubkey_hex",
    role = "CREATOR",
    endpoint = "i2p://abcdef.i2p:7656",
    status = "ACTIVE",
    addedAt = System.currentTimeMillis()
)
```

#### SyncTargetEntity
**Purpose**: Configures who this device syncs with  
**Sync**: NEVER (local routing policy)

```kotlin
@Entity(tableName = "sync_targets")
data class SyncTargetEntity(
    @PrimaryKey val id: String,
    val roscaId: String,
    val targetPeerId: String,             // Which peer to sync with
    val syncEnabled: Boolean = true,
    val lastSyncAttempt: Long?,
    val lastSyncSuccess: Long?
)
```

**Sync Rules**:
- If my role = MEMBER → target = CREATOR
- If my role = CREATOR → targets = ALL MEMBERS

---

### Layer 2: ROSCA State (Synced Data)

All entities in this layer include sync metadata:
```kotlin
val version: Long = 1,              // Optimistic locking
val lastModifiedBy: String?,        // Node ID that modified
val lastModifiedAt: Long
```

#### RoscaEntity
**Purpose**: ROSCA definition  
**Sync**: YES (creator authoritative)

```kotlin
@Entity(tableName = "roscas")
data class RoscaEntity(
    @PrimaryKey val id: String,
    val name: String,
    val contributionAmount: Double,
    val currency: String,
    val frequency: String,              // DAILY, WEEKLY, MONTHLY
    val maxMembers: Int,
    val currentMembers: Int,
    val startDate: Long,
    val endDate: Long?,
    val status: String,                 // FORMING, ACTIVE, COMPLETED, CANCELLED
    val creatorNodeId: String,          // For routing
    val createdAt: Long,
    val serviceFeePercentage: Double,
    val penaltyPercentage: Double,
    val multisigWalletAddress: String?, // Public address (safe to sync)
    
    // Sync metadata
    val version: Long = 1,
    val lastModifiedBy: String?,
    val lastModifiedAt: Long
)
```

#### MemberEntity
**Purpose**: ROSCA membership  
**Sync**: YES (creator authoritative)

```kotlin
@Entity(
    tableName = "members",
    foreignKeys = [
        ForeignKey(
            entity = RoscaEntity::class,
            parentColumns = ["id"],
            childColumns = ["roscaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("roscaId"), 
        Index("nodeId"),
        Index(value = ["roscaId", "nodeId"], unique = true)
    ]
)
data class MemberEntity(
    @PrimaryKey val id: String,
    val roscaId: String,
    val nodeId: String,                 // Changed from userId to nodeId
    val role: String,                   // CREATOR, MEMBER
    val joinedAt: Long,
    val status: String,                 // PENDING, ACTIVE, EXITED
    val publicWalletAddress: String?,   // Public Monero address (syncs)
    
    // Sync metadata
    val version: Long = 1,
    val lastModifiedBy: String?,
    val lastModifiedAt: Long
)
```

**Key Change**: `userId` → `nodeId` (links to P2P identity, not auth identity)

#### RoundEntity
**Purpose**: Round definitions  
**Sync**: YES

```kotlin
@Entity(
    tableName = "rounds",
    foreignKeys = [
        ForeignKey(
            entity = RoscaEntity::class,
            parentColumns = ["id"],
            childColumns = ["roscaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("roscaId"), Index("roundNumber")]
)
data class RoundEntity(
    @PrimaryKey val id: String,
    val roscaId: String,
    val roundNumber: Int,
    val startDate: Long,
    val endDate: Long,
    val status: String,                 // PENDING, ACTIVE, COMPLETED
    val recipientNodeId: String?,       // Winner of this round
    val totalCollected: Double = 0.0,
    val distributionMethod: String,     // FIXED_ORDER, BIDDING, RANDOM
    
    // Sync metadata
    val version: Long = 1,
    val lastModifiedBy: String?,
    val lastModifiedAt: Long
)
```

#### ContributionEntity
**Purpose**: Member contributions  
**Sync**: YES

```kotlin
@Entity(
    tableName = "contributions",
    foreignKeys = [
        ForeignKey(entity = RoscaEntity::class, ...),
        ForeignKey(entity = MemberEntity::class, ...),
        ForeignKey(entity = RoundEntity::class, ...)
    ],
    indices = [Index("roscaId"), Index("memberId"), Index("txHash")]
)
data class ContributionEntity(
    @PrimaryKey val id: String,
    val roscaId: String,
    val memberId: String,
    val roundId: String?,
    val amount: Double,
    val timestamp: Long,
    val status: String,                 // PENDING, CONFIRMED, FAILED
    val txHash: String?,                // Monero transaction hash
    
    // Sync metadata
    val version: Long = 1,
    val lastModifiedBy: String?,
    val lastModifiedAt: Long
)
```

#### MultisigSignatureEntity
**Purpose**: Multisig transaction signatures  
**Sync**: YES (critical for coordination)

```kotlin
@Entity(
    tableName = "multisig_signatures",
    foreignKeys = [
        ForeignKey(entity = RoscaEntity::class, ...),
        ForeignKey(entity = MemberEntity::class, ...)
    ],
    indices = [Index("roscaId"), Index("transactionId"), Index("signerId")]
)
data class MultisigSignatureEntity(
    @PrimaryKey val id: String,
    val roscaId: String,
    val transactionId: String,          // Links to transaction being signed
    val signerId: String,               // Member who signed
    val signature: String,              // Base64-encoded partial signature
    val timestamp: Long,
    val status: String,                 // PENDING, CONFIRMED
    
    // Sync metadata
    val version: Long = 1,
    val lastModifiedBy: String?,
    val lastModifiedAt: Long
)
```

**Other Synced Entities** (similar structure):
- `DistributionEntity` (payouts)
- `BidEntity` (for bidding rounds)
- `DividendEntity` (profit sharing)
- `ServiceFeeEntity` (platform fees)
- `PenaltyEntity` (late payment penalties)
- `TransactionEntity` (blockchain transactions)

---

### Layer 3: Local-Only Data (Never Syncs)

#### LocalWalletEntity
**Purpose**: Wallet file metadata  
**Sync**: NEVER (contains paths to secrets)

```kotlin
@Entity(
    tableName = "local_wallets",
    foreignKeys = [
        ForeignKey(entity = RoscaEntity::class, ...)
    ],
    indices = [Index("roscaId"), Index("nodeId")]
)
data class LocalWalletEntity(
    @PrimaryKey val id: String,
    val roscaId: String,
    val nodeId: String,                 // Owner (usually local node)
    val walletPath: String,             // e.g., "/data/data/.../wallet.keys"
    val cacheFilePath: String?,
    val passwordEncrypted: String,      // Encrypted with Keystore
    val createdAt: Long,
    val lastAccessedAt: Long?,
    val isMultisig: Boolean = false,
    val multisigInfo: String?           // JSON with setup state
)
```

**Critical**: These paths are device-specific and meaningless to other devices.

#### UserProfileEntity
**Purpose**: User identity and preferences  
**Sync**: NO (privacy)

```kotlin
@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val nodeId: String,                 // Link to LocalNodeEntity
    val email: String?,                 // Local only
    val displayName: String?,           // Syncs via MemberEntity
    val phoneNumber: String?,           // Local only
    val createdAt: Long,
    val lastLoginAt: Long,
    val notificationsEnabled: Boolean = true
)
```

#### InviteEntity
**Purpose**: Referral codes  
**Sync**: NO (creator manages locally)

```kotlin
@Entity(
    tableName = "invites",
    foreignKeys = [ForeignKey(entity = RoscaEntity::class, ...)],
    indices = [Index("roscaId"), Index("code", unique = true)]
)
data class InviteEntity(
    @PrimaryKey val id: String,
    val roscaId: String,
    val code: String,                   // Base64-encoded payload
    val createdByNodeId: String,
    val createdAt: Long,
    val expiresAt: Long,
    val maxUses: Int,
    val usedCount: Int = 0,
    val status: String,                 // ACTIVE, EXPIRED, REVOKED
    val signature: String               // Ed25519 signature
)
```

---

### Layer 4: Sync Coordination

#### SyncQueueEntity
**Purpose**: Outbound sync queue  
**Sync**: NEVER (local queue)

```kotlin
@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entityType: String,             // "members", "contributions", etc.
    val entityId: String,               // PK of changed entity
    val roscaId: String,
    val operation: String,              // INSERT, UPDATE, DELETE
    val payload: String,                // JSON of entity
    val targetNodeIds: String,          // Comma-separated
    val attempts: Int = 0,
    val maxAttempts: Int = 5,
    val createdAt: Long,
    val lastAttemptAt: Long?,
    val errorMessage: String?
)
```

**Populated by SQL triggers** (automatic):
```sql
CREATE TRIGGER members_insert_sync
AFTER INSERT ON members
FOR EACH ROW
BEGIN
    INSERT INTO sync_queue (...)
    VALUES ('members', NEW.id, ...);
END;
```

#### SyncLogEntity
**Purpose**: Sync history (debugging)  
**Sync**: NEVER

```kotlin
@Entity(tableName = "sync_log")
data class SyncLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roscaId: String,
    val direction: String,              // OUTBOUND, INBOUND
    val peerNodeId: String,
    val entityType: String,
    val entityId: String,
    val operation: String,
    val status: String,                 // SUCCESS, FAILED, CONFLICT
    val timestamp: Long,
    val errorMessage: String?
)
```

#### SyncConflictEntity
**Purpose**: Version conflicts awaiting resolution  
**Sync**: NEVER

```kotlin
@Entity(tableName = "sync_conflicts")
data class SyncConflictEntity(
    @PrimaryKey val id: String,
    val roscaId: String,
    val entityType: String,
    val entityId: String,
    val localVersion: Long,
    val remoteVersion: Long,
    val localPayload: String,           // JSON
    val remotePayload: String,          // JSON
    val detectedAt: Long,
    val resolvedAt: Long?,
    val resolution: String?             // KEEP_LOCAL, KEEP_REMOTE, CREATOR_WINS
)
```

---

### Schema Summary

| Table | Purpose | Syncs? | Sensitive? |
|-------|---------|--------|------------|
| `local_node` | This device's identity | ❌ | ✅ Private key |
| `peers` | Peer registry | Partial | ❌ |
| `sync_targets` | Routing config | ❌ | ❌ |
| `roscas` | ROSCA definitions | ✅ | ❌ |
| `members` | Membership | ✅ | ❌ |
| `rounds` | Round schedule | ✅ | ❌ |
| `contributions` | Payments | ✅ | ❌ |
| `distributions` | Payouts | ✅ | ❌ |
| `bids` | Bidding data | ✅ | ❌ |
| `dividends` | Profit sharing | ✅ | ❌ |
| `service_fees` | Platform fees | ✅ | ❌ |
| `penalties` | Late fees | ✅ | ❌ |
| `transactions` | Blockchain txs | ✅ | ❌ |
| `multisig_signatures` | Signatures | ✅ | ❌ |
| `local_wallets` | Wallet paths | ❌ | ✅ File paths |
| `user_profiles` | User info | ❌ | ⚠️ Email/phone |
| `invites` | Referral codes | ❌ | ❌ |
| `sync_queue` | Outbound queue | ❌ | ❌ |
| `sync_log` | Sync history | ❌ | ❌ |
| `sync_conflicts` | Conflicts | ❌ | ❌ |

---

## Sync Protocol

### Message Envelope

All messages use this wrapper:

```kotlin
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

enum class MessageType {
    MEMBERSHIP_REQUEST,     // Invitee → Creator
    MEMBERSHIP_RESPONSE,    // Creator → Invitee
    ENTITY_UPDATE,          // Bidirectional
    STATE_SYNC,             // Creator → Member (full sync)
    ACK,                    // Acknowledge
    PING,                   // Keep-alive
    PONG                    // Keep-alive response
}
```

### Message Signing

```kotlin
// Signing payload construction
val signingData = "${message.protocolVersion}|${message.messageId}|${message.timestamp}|${message.payload}"

// Sign with Ed25519
val signature = Ed25519.sign(signingData.toByteArray(), privateKey)

// Verification
val isValid = Ed25519.verify(signature, signingData.toByteArray(), publicKey)
```

### Payload Types

#### 1. MEMBERSHIP_REQUEST

Sent by invitee after consuming referral:

```kotlin
@Serializable
data class MembershipRequestPayload(
    val roscaId: String,
    val referralCode: String,           // For verification
    val nodeId: String,
    val publicKey: String,
    val displayName: String?,
    val publicWalletAddress: String?,
    val joinedAt: Long
)
```

#### 2. MEMBERSHIP_RESPONSE

Creator accepts/rejects membership:

```kotlin
@Serializable
data class MembershipResponsePayload(
    val roscaId: String,
    val accepted: Boolean,
    val memberId: String?,              // If accepted
    val reason: String?,                // If rejected
    val roscaState: RoscaSnapshot?      // If accepted
)

@Serializable
data class RoscaSnapshot(
    val rosca: String,                  // JSON of RoscaEntity
    val members: List<String>,          // JSON array of members
    val rounds: List<String>,
    val contributions: List<String>,
    val distributions: List<String>,
    // ... all synced entities
    val version: Long                   // Overall state version
)
```

#### 3. ENTITY_UPDATE

Any change to synced entities:

```kotlin
@Serializable
data class EntityUpdatePayload(
    val roscaId: String,
    val updates: List<EntityUpdate>     // Batched updates
)

@Serializable
data class EntityUpdate(
    val entityType: String,             // "contributions", "bids", etc.
    val entityId: String,
    val operation: String,              // INSERT, UPDATE, DELETE
    val version: Long,
    val data: String?,                  // JSON (null for DELETE)
    val lastModifiedBy: String,
    val lastModifiedAt: Long
)
```

#### 4. STATE_SYNC

Full or incremental state synchronization:

```kotlin
@Serializable
data class StateSyncPayload(
    val roscaId: String,
    val syncType: String,               // FULL, INCREMENTAL
    val snapshot: RoscaSnapshot
)
```

#### 5. ACK

Acknowledgment of received message:

```kotlin
@Serializable
data class AckPayload(
    val ackedMessageId: String,
    val status: String,                 // SUCCESS, CONFLICT, ERROR
    val conflicts: List<EntityConflict>? = null,
    val errorMessage: String? = null
)

@Serializable
data class EntityConflict(
    val entityType: String,
    val entityId: String,
    val localVersion: Long,
    val remoteVersion: Long,
    val resolution: String              // CREATOR_WINS, REJECTED
)
```

### Wire Format

Messages sent as JSON over I2P:

```json
{
  "protocolVersion": 1,
  "messageId": "550e8400-e29b-41d4-a716-446655440000",
  "senderNodeId": "node_abc123",
  "recipientNodeId": "node_xyz789",
  "timestamp": 1706500000000,
  "messageType": "ENTITY_UPDATE",
  "payload": "{\"roscaId\":\"rosca_001\",\"updates\":[...]}",
  "signature": "base64_encoded_ed25519_signature"
}
```

### Conflict Resolution

**Rule**: Creator always wins

```kotlin
fun resolveConflict(local: Entity, remote: Entity): Entity {
    if (remote.lastModifiedBy == CREATOR_NODE_ID) {
        // Creator's version wins
        logConflict(local, remote, resolution = "CREATOR_WINS")
        return remote
    }
    
    if (remote.version > local.version) {
        // Newer version wins
        return remote
    }
    
    if (remote.version == local.version) {
        // Same version, compare timestamp
        return if (remote.lastModifiedAt > local.lastModifiedAt) {
            remote
        } else {
            local
        }
    }
    
    // Local is newer
    return local
}
```

---

## Referral System

### Referral Code Structure

```kotlin
@Serializable
data class ReferralPayload(
    val version: Int = 1,
    val roscaId: String,
    val roscaName: String,
    val creatorNodeId: String,
    val creatorPublicKey: String,
    val creatorEndpoints: List<String>, // I2P addresses
    val role: String,                   // Always "MEMBER"
    val maxUses: Int,
    val expiresAt: Long,
    val createdAt: Long,
    val roscaMetadata: RoscaMetadata
)

@Serializable
data class RoscaMetadata(
    val contributionAmount: Double,
    val currency: String,
    val frequency: String,
    val maxMembers: Int,
    val currentMembers: Int,
    val startDate: Long
)

@Serializable
data class ReferralCode(
    val payload: ReferralPayload,
    val signature: String               // Ed25519 by creator
)
```

### Encoding

```kotlin
// Creator generates referral
val payload = ReferralPayload(...)
val payloadJson = Json.encodeToString(payload)
val signature = Ed25519.sign(payloadJson, creatorPrivateKey)

val code = ReferralCode(payload, signature)
val codeJson = Json.encodeToString(code)
val base64 = Base64.getUrlEncoder().encodeToString(codeJson.toByteArray())

// Result: "eyJwYXlsb2FkIjp7InZlcnNpb24iOjEsInJvc2NhSWQiOi..."
```

### Decoding & Validation

```kotlin
// Invitee scans code
val decoded = Base64.getUrlDecoder().decode(scannedCode)
val code = Json.decodeFromString<ReferralCode>(decoded.decodeToString())

// Validate
fun validate(code: ReferralCode): ValidationResult {
    // 1. Verify signature
    val payloadJson = Json.encodeToString(code.payload)
    if (!Ed25519.verify(code.signature, payloadJson, code.payload.creatorPublicKey)) {
        return Invalid("Invalid signature")
    }
    
    // 2. Check expiry
    if (code.payload.expiresAt < System.currentTimeMillis()) {
        return Invalid("Code expired")
    }
    
    // 3. Check capacity
    if (code.payload.roscaMetadata.currentMembers >= code.payload.roscaMetadata.maxMembers) {
        return Invalid("ROSCA is full")
    }
    
    return Valid(code)
}
```

### Consumption (The Key Moment)

This is where **configuration by data** happens:

```kotlin
suspend fun consumeReferral(
    code: ReferralCode,
    localNodeId: String,
    db: AjoDatabase
) {
    val payload = code.payload
    
    // 1. Write creator as peer (configures who to sync with)
    db.peerDao().insert(
        PeerEntity(
            id = "peer_${payload.creatorNodeId}",
            nodeId = payload.creatorNodeId,
            roscaId = payload.roscaId,
            publicKey = payload.creatorPublicKey,
            role = "CREATOR",
            endpoint = payload.creatorEndpoints.first(),
            status = "ACTIVE",
            addedAt = System.currentTimeMillis()
        )
    )
    
    // 2. Write ROSCA (placeholder, will be updated by creator)
    db.roscaDao().insert(
        RoscaEntity(
            id = payload.roscaId,
            name = payload.roscaName,
            creatorNodeId = payload.creatorNodeId,
            status = "FORMING",
            // ... from metadata
            version = 0,
            lastModifiedBy = localNodeId,
            lastModifiedAt = System.currentTimeMillis()
        )
    )
    
    // 3. Write membership (PENDING until accepted)
    db.memberDao().insert(
        MemberEntity(
            id = "member_${localNodeId}",
            roscaId = payload.roscaId,
            nodeId = localNodeId,
            role = "MEMBER",
            status = "PENDING",
            joinedAt = System.currentTimeMillis(),
            version = 1,
            lastModifiedBy = localNodeId,
            lastModifiedAt = System.currentTimeMillis()
        )
    )
    
    // 4. Configure sync target (who to send changes to)
    db.syncTargetDao().insert(
        SyncTargetEntity(
            id = "sync_${payload.roscaId}",
            roscaId = payload.roscaId,
            targetPeerId = "peer_${payload.creatorNodeId}",
            syncEnabled = true
        )
    )
    
    // 5. Queue membership request
    db.syncQueueDao().insert(
        SyncQueueEntity(
            entityType = "membership_request",
            entityId = "member_${localNodeId}",
            roscaId = payload.roscaId,
            operation = "INSERT",
            payload = Json.encodeToString(
                MembershipRequestPayload(
                    roscaId = payload.roscaId,
                    referralCode = originalCodeString,
                    nodeId = localNodeId,
                    publicKey = db.localNodeDao().getLocalNode()!!.publicKey,
                    displayName = null,
                    publicWalletAddress = null,
                    joinedAt = System.currentTimeMillis()
                )
            ),
            targetNodeIds = payload.creatorNodeId,
            createdAt = System.currentTimeMillis()
        )
    )
}
```

**After this function completes**:
- Device knows ROSCA exists
- Device knows creator's identity and endpoint
- Device has sync target configured
- Membership request is queued
- SyncWorker will automatically send request

**No further configuration needed. The data structure now demands sync.**

---

## State Machine

### States

```
INVITED → PENDING → REQUESTING → ACTIVE
                                   ↓
                    ┌──────────────┼──────────────┐
                    ↓              ↓              ↓
                EXITING        EXPELLED      COMPLETED
                    ↓              ↓              ↓
                    └──────────→ EXITED ←────────┘
                    
Also: REJECTED, TIMEOUT (error states)
```

### State Definitions

| State | Description | Visible to Others? | Next States |
|-------|-------------|-------------------|-------------|
| **INVITED** | Has referral, not consumed | No | PENDING |
| **PENDING** | Consumed referral, waiting to send | No | REQUESTING, TIMEOUT |
| **REQUESTING** | Membership request sent | Yes (creator) | ACTIVE, REJECTED, TIMEOUT |
| **ACTIVE** | Full member, can transact | Yes (all) | EXITING, EXPELLED, COMPLETED |
| **REJECTED** | Creator declined | No | None (terminal) |
| **TIMEOUT** | Network failure | No | PENDING (retry) |
| **EXITING** | Graceful exit in progress | Yes | EXITED |
| **EXPELLED** | Forced exit (penalty) | Yes | EXITED |
| **COMPLETED** | ROSCA finished | Yes | EXITED |
| **EXITED** | Fully out | Yes | None (terminal) |

### Transitions

#### consume_referral()
```
INVITED → PENDING

Actions:
1. Parse and validate referral
2. Write PeerEntity (creator)
3. Write RoscaEntity (placeholder)
4. Write MemberEntity (status=PENDING)
5. Write SyncTargetEntity
6. Queue membership_request
```

#### send_membership_request()
```
PENDING → REQUESTING

Actions:
1. SyncWorker reads queue
2. Build and sign message
3. Send over I2P
4. Update attempts counter
```

#### receive_acceptance()
```
REQUESTING → ACTIVE

Actions:
1. Verify signature
2. Update MemberEntity (status=ACTIVE)
3. Merge RoscaSnapshot into DB
4. Create LocalWalletEntity
5. Clear from sync_queue
6. Log success
```

#### receive_rejection()
```
REQUESTING → REJECTED

Actions:
1. Update MemberEntity (status=REJECTED)
2. Log reason
3. Clear from sync_queue
4. Notify user
```

#### timeout()
```
REQUESTING → TIMEOUT

Actions:
1. Check attempts >= maxAttempts
2. Update MemberEntity (status=TIMEOUT)
3. Clear from sync_queue
4. Notify user
```

#### voluntary_exit()
```
ACTIVE → EXITING

Actions:
1. Update MemberEntity (status=EXITING)
2. Queue exit notification
3. Begin wallet settlement
```

#### forced_exit()
```
ACTIVE → EXPELLED

Actions:
1. Creator sends EXIT command
2. Update MemberEntity (status=EXPELLED)
3. Log penalty
4. Begin forced settlement
```

#### rosca_completes()
```
ACTIVE → COMPLETED

Actions:
1. Creator sets RoscaEntity.status=COMPLETED
2. Sync to all members
3. Update MemberEntity (status=COMPLETED)
4. Calculate final dividends
```

#### settlement_complete()
```
EXITING/EXPELLED/COMPLETED → EXITED

Actions:
1. Verify all transactions settled
2. Close LocalWalletEntity
3. Update MemberEntity (status=EXITED)
4. Disable sync
```

### Retry Logic

```kotlin
// Exponential backoff
attempts = 0
while (attempts < maxAttempts) {
    try {
        sendSyncMessage()
        waitForAck(timeout = 30.seconds)
        break  // Success
    } catch (e: TimeoutException) {
        attempts++
        val backoff = min(2.pow(attempts) * 5, 300)  // Max 5 min
        delay(backoff.seconds)
    }
}

if (attempts >= maxAttempts) {
    transitionTo(TIMEOUT)
}
```

---

## Multisig Coordination

### The Challenge

Single device (current):
```kotlin
// All wallets on same device
multisigWallet.sign(wallet1)  // ✅
multisigWallet.sign(wallet2)  // ✅
multisigWallet.sign(wallet3)  // ✅
broadcast()                    // ✅ Done in ~1 second
```

P2P distributed:
```
Device A: has wallet1 only
Device B: has wallet2 only
Device C: has wallet3 only

Must coordinate signing asynchronously via sync
```

### Solution: Signature Chaining

#### Phase 1: Creator Initiates

```kotlin
// Device A (Creator)
val unsignedTx = moneroWallet.createTransaction(
    recipient = winner.publicWalletAddress,
    amount = payoutAmount
)

val partialTx = moneroWallet.signMultisigTransaction(unsignedTx)

// Store in Room (syncs to all)
db.multisigSignatureDao().insert(
    MultisigSignatureEntity(
        id = "sig_${UUID.randomUUID()}",
        roscaId = "rosca_001",
        transactionId = "tx_round1_payout",
        signerId = "member_creator",
        signature = partialTx.toHex(),
        timestamp = System.currentTimeMillis(),
        status = "CONFIRMED",
        version = 1,
        lastModifiedBy = localNodeId,
        lastModifiedAt = System.currentTimeMillis()
    )
)

db.transactionDao().insert(
    TransactionEntity(
        id = "tx_round1_payout",
        roscaId = "rosca_001",
        txHash = null,  // Not broadcast yet
        type = "DISTRIBUTION",
        amount = payoutAmount,
        toAddress = winner.publicWalletAddress,
        status = "PENDING_SIGNATURES",
        timestamp = System.currentTimeMillis(),
        version = 1,
        lastModifiedBy = localNodeId,
        lastModifiedAt = System.currentTimeMillis()
    )
)
```

#### Phase 2: Member 2 Signs

```kotlin
// Device B (Member 2)
// Receives sync update with new MultisigSignatureEntity

// Check if it's my turn
val tx = db.transactionDao().get("tx_round1_payout")
val existingSigs = db.multisigSignatureDao()
    .getByTransaction("tx_round1_payout")
    
if (existingSigs.size == 1) {  // Only creator has signed
    // My turn!
    val myWallet = loadWallet()
    val partialTx = myWallet.signMultisigTransaction(
        existingSigs.map { it.signature }
    )
    
    db.multisigSignatureDao().insert(
        MultisigSignatureEntity(
            id = "sig_${UUID.randomUUID()}",
            transactionId = "tx_round1_payout",
            signerId = "member_2",
            signature = partialTx.toHex(),
            // ... sync metadata
        )
    )
}
```

#### Phase 3: Member 3 Completes

```kotlin
// Device C (Member 3)
// Receives sync with 2 signatures

val existingSigs = db.multisigSignatureDao()
    .getByTransaction("tx_round1_payout")
    
if (existingSigs.size == 2) {  // Need my signature to complete
    val myWallet = loadWallet()
    val finalTx = myWallet.signMultisigTransaction(
        existingSigs.map { it.signature }
    )
    
    // finalTx is now COMPLETE
    
    // Broadcast to Monero network
    val txHash = moneroRPC.submitTransaction(finalTx)
    
    // Update transaction (syncs to all)
    db.transactionDao().update(
        tx.copy(
            txHash = txHash,
            status = "BROADCAST",
            blockHeight = null,  // Will be filled later
            confirmations = 0,
            version = tx.version + 1,
            lastModifiedBy = localNodeId,
            lastModifiedAt = System.currentTimeMillis()
        )
    )
}
```

### Signature Order

Monero multisig requires specific order:

```kotlin
data class MemberEntity(
    // ... existing fields
    val signingOrder: Int  // 1, 2, 3, etc.
)

// Members sign in order
fun canSign(tx: Transaction): Boolean {
    val existingSigs = db.multisigSignatureDao().getByTransaction(tx.id)
    val myMember = db.memberDao().getByNodeId(localNodeId, tx.roscaId)
    return existingSigs.size + 1 == myMember.signingOrder
}
```

### Timeouts & Expiry

```kotlin
// Background job checks for stale transactions
class MultisigTimeoutChecker : CoroutineWorker {
    override suspend fun doWork(): Result {
        val staleTxs = db.transactionDao()
            .getPending()
            .filter { it.createdAt + 24.hours < now }
        
        staleTxs.forEach { tx ->
            db.transactionDao().update(
                tx.copy(status = "EXPIRED")
            )
            notifyCreator("Transaction ${tx.id} expired, please recreate")
        }
        
        return Result.success()
    }
}
```

### Handling Offline Members

```kotlin
// UI shows status
when (val sigCount = getSignatureCount(txId)) {
    in 0..2 -> "Waiting for signatures: $sigCount/3"
    3 -> "Broadcasting transaction..."
}

// If member offline for too long
if (member.lastSeenAt < now - 6.hours && tx.status == "PENDING") {
    showNotification("Waiting for ${member.displayName} to sign")
}
```

---

## Security Model

### Cryptographic Guarantees

1. **Node Identity**: Ed25519 keypair
   - Private key stored encrypted (Android Keystore)
   - Node ID derived from public key (tamper-proof)
   - All messages signed

2. **Message Integrity**: Ed25519 signatures
   - Signing payload: `version|messageId|timestamp|payload`
   - Recipient verifies before processing
   - Invalid signatures dropped silently

3. **Wallet Privacy**: By design
   - Wallet files never synced
   - Only public addresses in sync messages
   - Private keys never leave device
   - Impossible to accidentally leak

### Threat Model

#### What We Protect Against

✅ **Man-in-the-middle**: I2P + Ed25519 signatures  
✅ **Impersonation**: Only creator's public key can sign as creator  
✅ **Replay attacks**: Message ID deduplication  
✅ **Wallet theft**: Wallet files never on network  
✅ **Eavesdropping**: I2P provides anonymity layer  

#### What We Don't Protect Against

❌ **Device compromise**: If attacker has physical device, they have wallet  
❌ **Social engineering**: User voluntarily shares wallet files  
❌ **Malicious creator**: Creator can reject members, but can't steal wallets  
❌ **51% attack on Monero**: Out of scope (blockchain layer)  

### Security Checklist

```kotlin
// Before processing any sync message
fun processSyncMessage(message: SyncMessage) {
    // 1. Verify signature
    if (!MessageSigner.verify(message, senderPublicKey)) {
        log("Invalid signature from ${message.senderNodeId}")
        return  // Drop silently
    }
    
    // 2. Check timestamp (prevent replay)
    val timeDiff = abs(message.timestamp - System.currentTimeMillis())
    if (timeDiff > 5.minutes.inMilliseconds) {
        log("Message timestamp too old/future: $timeDiff ms")
        return
    }
    
    // 3. Check sender is known peer
    val peer = db.peerDao().getPeerByNodeId(message.senderNodeId)
    if (peer == null) {
        log("Unknown sender: ${message.senderNodeId}")
        return
    }
    
    // 4. Check message not already processed
    if (isMessageIdSeen(message.messageId)) {
        log("Duplicate message: ${message.messageId}")
        return
    }
    
    // 5. Check payload doesn't contain secrets
    if (containsSecrets(message.payload)) {
        throw SecurityException("CRITICAL: Secret in sync message!")
    }
    
    // 6. Process message
    handleMessage(message)
}

fun containsSecrets(payload: String): Boolean {
    val forbidden = listOf(
        "privateKey",
        "walletPath",
        "password",
        "seedPhrase",
        "mnemonic",
        "/data/data/"  // File paths
    )
    return forbidden.any { payload.contains(it, ignoreCase = true) }
}
```

### Referral Security

```kotlin
// Referral validation
fun validateReferral(code: ReferralCode): ValidationResult {
    // 1. Signature check
    if (!verifySignature(code)) {
        return Invalid("Invalid signature - may be tampered")
    }
    
    // 2. Expiry check
    if (code.payload.expiresAt < now) {
        return Invalid("Code expired")
    }
    
    // 3. Use limit check
    val invite = db.inviteDao().getByCode(code.payload.code)
    if (invite?.usedCount >= code.payload.maxUses) {
        return Invalid("Code exhausted")
    }
    
    // 4. Capacity check
    if (code.payload.roscaMetadata.currentMembers >= code.payload.roscaMetadata.maxMembers) {
        return Invalid("ROSCA is full")
    }
    
    return Valid(code)
}
```

---

## Implementation Roadmap

### Phase 1: Database Migration (Week 1)

**Goal**: Update schema for P2P

**Tasks**:
1. Add new entities (LocalNode, Peer, SyncTarget, LocalWallet)
2. Add sync metadata to existing entities (version, lastModifiedBy, lastModifiedAt)
3. Update MemberEntity (userId → nodeId)
4. Add SyncQueue, SyncLog, SyncConflict tables
5. Create SQL triggers for automatic queue population
6. Write and test migration

**Deliverables**:
- Migration script (SQLite ALTER TABLE)
- Updated DAOs
- Unit tests for triggers

### Phase 2: Identity & Crypto (Week 2)

**Goal**: Node identity generation

**Tasks**:
1. Add Ed25519 library
2. Implement KeyManager
3. Generate node keypair on first launch
4. Store in LocalNodeEntity (encrypted private key)
5. Implement message signing/verification

**Deliverables**:
- KeyManager.kt
- MessageSigner.kt
- Tests for crypto operations

### Phase 3: Referral System (Week 2-3)

**Goal**: Bootstrap mechanism

**Tasks**:
1. Implement ReferralCodec (encode/decode)
2. Create referral generation UI (creator)
3. Add QR code generation
4. Create referral scanning UI (invitee)
5. Implement referral validation
6. Implement referral consumption (database writes)

**Deliverables**:
- ReferralCodec.kt
- InviteActivity.kt
- JoinRoscaActivity.kt (updated)
- Tests for referral flow

### Phase 4: Sync Engine (Week 3-4)

**Goal**: Core sync infrastructure

**Tasks**:
1. Create SyncWorker (WorkManager)
2. Implement outbound sync (read queue, send)
3. Implement inbound sync (receive, apply)
4. Add ACK protocol
5. Implement retry logic
6. Add conflict resolution

**Deliverables**:
- SyncWorker.kt
- SyncEngine.kt
- MessageHandler.kt
- ConflictResolver.kt

### Phase 5: I2P Integration (Week 4-5)

**Goal**: Network layer

**Tasks**:
1. Add I2P Android library
2. Initialize I2P router
3. Create I2P destination (address)
4. Implement send/receive
5. Handle I2P lifecycle (start/stop)
6. Add connection monitoring

**Deliverables**:
- I2PManager.kt
- I2PService.kt (foreground service)
- Network status UI

### Phase 6: Wallet Privacy (Week 5)

**Goal**: Ensure secrets don't leak

**Tasks**:
1. Update wallet creation to use LocalWalletEntity
2. Verify wallet paths are device-local
3. Add secret detection in sync messages
4. Test that wallet files never sync
5. Audit all sync payloads

**Deliverables**:
- Updated WalletRepository
- Security audit report
- Penetration test results

### Phase 7: Multisig Coordination (Week 5-6)

**Goal**: Distributed multisig

**Tasks**:
1. Implement signature chaining logic
2. Add signing order to MemberEntity
3. Create MultisigCoordinator
4. Handle async signing flow
5. Add timeout/expiry handling
6. Test with 3+ devices

**Deliverables**:
- MultisigCoordinator.kt
- Updated transaction UI
- End-to-end test suite

### Phase 8: Testing & Polish (Week 6-7)

**Goal**: Production readiness

**Tasks**:
1. E2E testing (3+ devices)
2. Network failure simulation
3. Conflict resolution testing
4. Performance optimization
5. UI polish (sync indicators)
6. Documentation

**Deliverables**:
- Test suite (unit + integration + E2E)
- Performance report
- User documentation

---

## Installation Requirements

### Dependencies

```gradle
// app/build.gradle

plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'kotlin-kapt'
    id 'org.jetbrains.kotlin.plugin.serialization' version '1.9.0'
}

dependencies {
    // Existing (already have)
    implementation "androidx.room:room-runtime:2.6.1"
    implementation "androidx.room:room-ktx:2.6.1"
    kapt "androidx.room:room-compiler:2.6.1"
    implementation "androidx.work:work-runtime-ktx:2.9.0"
    
    // NEW: Cryptography
    implementation 'net.i2p.crypto:eddsa:0.3.0'
    
    // NEW: I2P Networking
    implementation 'net.i2p.android:client:0.9.50'
    implementation 'net.i2p.android:helper:0.9.50'
    
    // NEW: JSON Serialization
    implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0'
    
    // QR Codes (if not already using)
    implementation 'com.google.zxing:core:3.5.1'
    implementation 'com.journeyapps:zxing-android-embedded:4.3.0'
}
```

### Permissions

```xml
<!-- AndroidManifest.xml -->
<manifest>
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.CAMERA" />  <!-- For QR scanning -->
    
    <application>
        <!-- I2P Service -->
        <service
            android:name="net.i2p.android.router.service.RouterService"
            android:enabled="true"
            android:exported="false" />
    </application>
</manifest>
```

### Initialization

```kotlin
// AjoApplication.kt
class AjoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize I2P
        lifecycleScope.launch {
            I2PAndroid.startService(this@AjoApplication)
        }
        
        // Create local node identity (if not exists)
        lifecycleScope.launch {
            KeyManager.getOrCreateLocalNode(this@AjoApplication)
        }
        
        // Schedule periodic sync
        scheduleSyncWorker()
    }
    
    private fun scheduleSyncWorker() {
        val syncWork = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ).build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "rosca_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWork
        )
    }
}
```

---

## FAQ & Edge Cases

### Q: What if creator's device is lost/broken?

**A**: Recovery mechanism needed:
1. Creator backs up private key (encrypted)
2. On new device, restore from backup
3. Re-establish I2P presence
4. Members detect creator is back online
5. Sync resumes

**Caveat**: If creator loses keys AND backup, ROSCA is orphaned.

**Mitigation**: Multi-admin support (future enhancement)

---

### Q: What if two members sign simultaneously?

**A**: Signing order enforced:
1. MemberEntity has `signingOrder` field
2. Members only sign when `existingSigCount + 1 == mySigningOrder`
3. If someone jumps the queue, their signature is rejected
4. They must wait for proper turn

---

### Q: Can members sync directly with each other?

**A**: No, by design:
- All sync goes through creator
- Creator is authoritative source
- Prevents Byzantine scenarios
- Simplifies conflict resolution

**Future**: Could add member-to-member for performance, but increases complexity.

---

### Q: What if I2P is blocked/unavailable?

**A**: Graceful degradation:
1. App works offline (queues changes)
2. User sees "Waiting for connection..."
3. When I2P available, sync resumes
4. No data loss (queue persists)

**Alternative**: Add backup transport (Tor, direct TCP)

---

### Q: How big can sync messages get?

**A**: Batching limits:
- Max 50 entity updates per message
- Max 5MB payload size
- If exceeded, split into multiple messages

**Optimization**: Use delta sync (only changed fields)

---

### Q: How to handle timezone differences?

**A**: All timestamps in UTC milliseconds:
```kotlin
val timestamp = System.currentTimeMillis()  // Always UTC
```

Display in local timezone:
```kotlin
val localTime = Instant.ofEpochMilli(timestamp)
    .atZone(ZoneId.systemDefault())
```

---

### Q: Can I test without I2P?

**A**: Yes, mock I2P for development:
```kotlin
interface NetworkTransport {
    suspend fun send(destination: String, data: ByteArray)
    fun setListener(listener: (ByteArray) -> Unit)
}

class I2PTransport : NetworkTransport { /* real I2P */ }
class MockTransport : NetworkTransport { /* in-memory queue */ }

// Inject in tests
val transport = if (BuildConfig.DEBUG) MockTransport() else I2PTransport()
```

---

### Q: What about database size growth?

**A**: Retention policies:
```kotlin
// Delete old sync logs
db.syncLogDao().deleteOldLogs(cutoff = now - 30.days)

// Archive completed ROSCAs
db.roscaDao().archiveCompleted(older = now - 90.days)

// Prune conflict history
db.syncConflictDao().deleteResolved(older = now - 7.days)
```

---

### Q: How to debug sync issues?

**A**: Built-in diagnostics:
1. Sync log viewer in settings
2. Pending queue inspector
3. Message payload viewer (sanitized)
4. Network connectivity test
5. Manual sync trigger

---

## Conclusion

This design provides a **complete, production-ready specification** for converting the Ajo Android app from a single-device simulation to a fully distributed P2P system.

**Key achievements**:
✅ Privacy-preserving (wallet files never sync)  
✅ Deterministic (no ambiguous states)  
✅ Decentralized (no server required)  
✅ Secure (Ed25519 + I2P)  
✅ Implementable (7-week roadmap)  

**What makes this special**:
- Configuration by data (referral codes program behavior)
- Creator authority (simple conflict resolution)
- Local-first (works offline)
- Privacy by structure (impossible to leak secrets)

**Next steps**:
1. Begin Phase 1 (database migration)
2. Follow roadmap sequentially
3. Test incrementally
4. Deploy progressively

The design is complete. The implementation path is clear. **Time to build.**
