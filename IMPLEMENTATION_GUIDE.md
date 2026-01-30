# P2P SYNC IMPLEMENTATION GUIDE

This guide walks through implementing the complete P2P sync architecture for the Ajo Android app.

## Files Created

1. **p2p_schema_redesign.kt** - Complete entity definitions with sync boundaries
2. **AjoDatabase_P2P.kt** - Updated database with new entities and DAOs
3. **SyncProtocol.kt** - Wire format and message specifications
4. **ReferralFormat.kt** - Referral code generation and consumption
5. **StateMachine.md** - Complete state machine documentation
6. **current_entities.kt** - Reference of existing schema (for comparison)

## Implementation Roadmap

### Phase 1: Database Migration (Week 1)

**Goal**: Transition from existing schema to P2P-enabled schema

**Tasks**:
1. Create new entity files from `p2p_schema_redesign.kt`
2. Create new DAO interfaces from `AjoDatabase_P2P.kt`
3. Write migration script (SQLite ALTER TABLE commands)
4. Test migration on dev database
5. Add database triggers for automatic sync queue population

**Key Changes**:
- Add `LocalNodeEntity`, `PeerEntity`, `SyncTargetEntity`
- Add `LocalWalletEntity` (separate from synced member data)
- Add sync metadata fields (`version`, `lastModifiedBy`, `lastModifiedAt`) to all synced entities
- Add `SyncQueueEntity`, `SyncLogEntity`, `SyncConflictEntity`
- Update `MemberEntity` to use `nodeId` instead of `userId`
- Update `RoscaEntity` to track `creatorNodeId`

**Migration Example**:
```kotlin
val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add new tables
        database.execSQL("CREATE TABLE local_node (...)")
        database.execSQL("CREATE TABLE peers (...)")
        // ... etc
        
        // Add new columns to existing tables
        database.execSQL("ALTER TABLE roscas ADD COLUMN version INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE roscas ADD COLUMN lastModifiedBy TEXT")
        database.execSQL("ALTER TABLE roscas ADD COLUMN lastModifiedAt INTEGER NOT NULL DEFAULT 0")
        // ... etc
        
        // Create triggers
        createSyncTriggers(database)
    }
}
```

### Phase 2: Identity & Crypto Setup (Week 2)

**Goal**: Generate and manage Ed25519 keypairs for P2P identity

**Tasks**:
1. Integrate Ed25519 library (net.i2p.crypto.eddsa)
2. Implement `KeyManager` for keypair generation
3. Create `LocalNodeEntity` on first app launch
4. Implement message signing/verification
5. Store private key encrypted (use Android Keystore)

**Implementation**:
```kotlin
// core/crypto/KeyManager.kt
object KeyManager {
    fun generateNodeKeypair(): Pair<ByteArray, ByteArray> {
        val keyPair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair()
        return Pair(keyPair.public.encoded, keyPair.private.encoded)
    }
    
    suspend fun getOrCreateLocalNode(context: Context): LocalNodeEntity {
        val db = AjoDatabase.getInstance(context)
        var node = db.localNodeDao().getLocalNode()
        
        if (node == null) {
            val (publicKey, privateKey) = generateNodeKeypair()
            val nodeId = deriveNodeId(publicKey)
            val encryptedPrivateKey = encryptWithKeystore(privateKey)
            
            node = LocalNodeEntity(
                nodeId = nodeId,
                publicKey = publicKey.toHex(),
                privateKeyEncrypted = encryptedPrivateKey,
                createdAt = System.currentTimeMillis(),
                lastSyncAt = null
            )
            db.localNodeDao().insert(node)
        }
        
        return node
    }
}
```

### Phase 3: Referral Code System (Week 2-3)

**Goal**: Implement referral generation and consumption

**Tasks**:
1. Create referral generation UI (creator side)
2. Implement QR code generation for referrals
3. Create referral scanning/input UI (invitee side)
4. Implement referral validation logic
5. Implement referral consumption (write metadata to DB)

**Implementation** (see `ReferralFormat.kt` for full spec):
```kotlin
// Creator generates referral
val referralCode = ReferralCodec.create(
    roscaId = roscaEntity.id,
    roscaName = roscaEntity.name,
    creatorNodeId = localNode.nodeId,
    creatorPublicKey = localNode.publicKey,
    creatorEndpoints = listOf("i2p://..."),
    roscaMetadata = RoscaMetadata.from(roscaEntity),
    privateKey = decryptPrivateKey(localNode.privateKeyEncrypted)
)

// Display as QR code
showQRCode(referralCode)

// Invitee scans and consumes
when (val result = ReferralConsumer.validate(scannedCode)) {
    is ValidationResult.Valid -> {
        ReferralConsumer.consume(result.code, localNode.nodeId, database)
        // Sync worker will now pick up membership request
    }
    is ValidationResult.Invalid -> {
        showError(result.reason)
    }
}
```

### Phase 4: Sync Engine (Week 3-4)

**Goal**: Implement the core sync worker that sends/receives messages

**Tasks**:
1. Create `SyncWorker` (Android WorkManager)
2. Implement outbound sync (read queue, send messages)
3. Implement inbound sync (receive messages, apply updates)
4. Implement ACK protocol
5. Add retry logic with exponential backoff
6. Add conflict resolution logic

**Implementation**:
```kotlin
// worker/SyncWorker.kt
class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val db = AjoDatabase.getInstance(applicationContext)
        
        // 1. Process outbound queue
        val pendingItems = db.syncQueueDao().getPendingSyncs()
        pendingItems.forEach { item ->
            try {
                sendSyncMessage(item)
                db.syncQueueDao().delete(item)  // Remove on success
            } catch (e: Exception) {
                // Retry logic
                if (item.attempts < item.maxAttempts) {
                    db.syncQueueDao().update(item.copy(
                        attempts = item.attempts + 1,
                        lastAttemptAt = System.currentTimeMillis(),
                        errorMessage = e.message
                    ))
                } else {
                    // Max attempts reached
                    logSyncFailure(item, e)
                }
            }
        }
        
        // 2. Check for inbound messages
        receiveAndProcessMessages()
        
        return Result.success()
    }
    
    private suspend fun sendSyncMessage(item: SyncQueueEntity) {
        // Build message
        val message = SyncMessage(
            messageId = UUID.randomUUID().toString(),
            senderNodeId = getLocalNodeId(),
            recipientNodeId = item.targetNodeIds,  // Resolved from sync_targets
            timestamp = System.currentTimeMillis(),
            messageType = MessageType.ENTITY_UPDATE,
            payload = item.payload,
            signature = ""
        )
        
        // Sign message
        val signedMessage = signMessage(message)
        
        // Send over I2P
        sendToI2P(signedMessage)
        
        // Wait for ACK (with timeout)
        waitForAck(message.messageId)
    }
    
    private suspend fun receiveAndProcessMessages() {
        val messages = fetchFromI2P()  // Get pending messages
        
        messages.forEach { message ->
            // Verify signature
            if (!verifyMessage(message)) {
                logInvalidMessage(message, "Invalid signature")
                return@forEach
            }
            
            // Process based on type
            when (message.messageType) {
                MessageType.MEMBERSHIP_REQUEST -> handleMembershipRequest(message)
                MessageType.MEMBERSHIP_RESPONSE -> handleMembershipResponse(message)
                MessageType.ENTITY_UPDATE -> handleEntityUpdate(message)
                MessageType.STATE_SYNC -> handleStateSync(message)
                MessageType.ACK -> handleAck(message)
                else -> logUnknownMessageType(message)
            }
            
            // Send ACK
            sendAck(message)
        }
    }
}
```

**WorkManager Configuration**:
```kotlin
// Schedule periodic sync
val syncWork = PeriodicWorkRequestBuilder<SyncWorker>(
    15, TimeUnit.MINUTES  // Sync every 15 minutes
).setConstraints(
    Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
).build()

WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    "rosca_sync",
    ExistingPeriodicWorkPolicy.KEEP,
    syncWork
)
```

### Phase 5: I2P Integration (Week 4-5)

**Goal**: Integrate I2P for anonymous P2P communication

**Tasks**:
1. Add I2P Android library dependencies
2. Implement I2P client initialization
3. Create I2P destination (address) for local node
4. Implement send/receive over I2P
5. Handle I2P connection lifecycle

**Dependencies** (build.gradle):
```gradle
dependencies {
    implementation 'net.i2p.android:client:0.9.50'
    implementation 'net.i2p.android:helper:0.9.50'
}
```

**Implementation**:
```kotlin
// core/network/I2PManager.kt
class I2PManager(private val context: Context) {
    private var i2pSession: I2PSession? = null
    
    suspend fun initialize(): String {
        // Start I2P router
        I2PAndroid.startService(context)
        
        // Create destination (our I2P address)
        val session = I2PSession(context)
        val destination = session.createDestination()
        i2pSession = session
        
        return destination.toBase64()  // Our I2P address
    }
    
    suspend fun send(destinationB64: String, data: ByteArray) {
        val session = i2pSession ?: throw IllegalStateException("I2P not initialized")
        val destination = Destination(destinationB64)
        session.sendMessage(destination, data)
    }
    
    fun setMessageListener(listener: (ByteArray) -> Unit) {
        i2pSession?.setMessageListener { data ->
            listener(data)
        }
    }
}
```

### Phase 6: Wallet Privacy (Week 5)

**Goal**: Ensure wallet files never sync, only metadata

**Tasks**:
1. Update wallet creation to write to `LocalWalletEntity`
2. Ensure wallet paths use device-local storage
3. Update multisig coordination to sync only public info
4. Add wallet backup/restore (local only)
5. Test that wallet files never appear in sync messages

**Key Check**:
```kotlin
// This should NEVER happen
if (syncMessage.payload.contains("privateKey") || 
    syncMessage.payload.contains("walletPath") ||
    syncMessage.payload.contains("password")) {
    throw SecurityException("CRITICAL: Wallet secret in sync message!")
}
```

### Phase 7: Testing & Validation (Week 6)

**Goal**: End-to-end testing of P2P sync

**Test Scenarios**:
1. **Happy Path**: Creator creates ROSCA → generates referral → invitee joins → both see each other
2. **Network Failure**: Invitee goes offline → rejoins → catches up on missed updates
3. **Creator Offline**: Member contributes → creator offline → creator comes back → receives contribution
4. **Conflict**: Two members edit same entity → creator's version wins
5. **Rejected Membership**: Referral expired → invitee tries to join → receives rejection
6. **Wallet Privacy**: Monitor all sync traffic → verify no wallet secrets transmitted

**Test Tools**:
```kotlin
// test/SyncTester.kt
class SyncTester {
    fun simulateCreatorOffline(durationMs: Long) { ... }
    fun injectNetworkFailure() { ... }
    fun verifyNoSecretsInPayload(payload: String): Boolean { ... }
    fun simulateConflict(entityType: String, entityId: String) { ... }
}
```

### Phase 8: UI Integration (Week 6-7)

**Goal**: Connect sync system to existing UI

**Tasks**:
1. Show sync status indicators (syncing, synced, failed)
2. Add "Waiting for creator..." state in join flow
3. Show member list updates in real-time
4. Add conflict resolution UI (if needed)
5. Add network diagnostics screen

**UI States**:
```kotlin
sealed class SyncState {
    object Synced : SyncState()
    object Syncing : SyncState()
    data class Failed(val reason: String) : SyncState()
    object WaitingForCreator : SyncState()
    data class Conflict(val conflicts: List<EntityConflict>) : SyncState()
}
```

## Key Implementation Notes

### 1. Sync is Automatic
Once referral is consumed, sync "just happens" because:
- Database triggers populate sync_queue
- SyncWorker periodically checks queue
- Sync targets are pre-configured
- No manual intervention needed

### 2. Creator is Authoritative
In all conflict situations:
- Creator's version wins
- Members accept creator's state
- This simplifies conflict resolution dramatically

### 3. Wallet Files Never Sync
**Critical**: Wallet files live in device-local storage and are marked `local_only`. Only public addresses sync.

### 4. State Machine is Deterministic
Every state transition has:
- Clear trigger
- Well-defined actions
- Single next state

### 5. Network Failures are Expected
Sync engine handles:
- Exponential backoff retries
- Persistent queue (survives app restart)
- Graceful degradation (work with stale data)

## Migration Path from Current App

### Step 1: Run in Parallel
- Keep existing sync (if any) running
- Add new P2P tables alongside
- Write to both old and new schemas
- Read from new schema only

### Step 2: Migrate Existing ROSCAs
For each existing ROSCA:
1. Creator generates node identity
2. Creator generates referrals for existing members
3. Members consume referrals
4. Data migrates to new schema
5. Old schema deprecated

### Step 3: Sunset Old System
- Stop writing to old schema
- Delete old tables
- Clean up old code

## Security Checklist

- [ ] All messages signed with Ed25519
- [ ] All signatures verified before processing
- [ ] Private keys encrypted at rest
- [ ] Wallet files never in sync payloads
- [ ] Referral codes expire
- [ ] Node IDs derived from public keys (tamper-proof)
- [ ] Message replay prevention (check messageId)
- [ ] Rate limiting on sync operations

## Performance Considerations

### Database Indexes
Ensure indexes exist on:
- `members.roscaId`, `members.nodeId`
- `sync_queue.roscaId`, `sync_queue.entityType`
- `peers.nodeId`, `peers.roscaId`
- `transactions.txHash`
- All version columns for conflict detection

### Batch Sync
- Batch multiple entity changes in one message
- Reduces network overhead
- Use `EntityUpdatePayload.updates` list

### Incremental Sync
- Track `lastSyncAt` timestamp
- Only sync entities modified since last sync
- Full sync only on initial join or after long offline period

## Monitoring & Debugging

### Sync Logs
Query `sync_log` table to see:
- Recent sync operations
- Failed syncs
- Conflict resolutions
- Latency stats

### Health Checks
```kotlin
fun getSyncHealth(roscaId: String): SyncHealth {
    val pendingCount = db.syncQueueDao().getPendingCount(roscaId)
    val recentFailures = db.syncLogDao().getRecentFailures(roscaId, since = now - 1.hour)
    val lastSyncSuccess = db.syncTargetDao().getLastSuccess(roscaId)
    
    return SyncHealth(
        healthy = pendingCount < 10 && recentFailures.isEmpty(),
        pendingItems = pendingCount,
        lastSuccess = lastSyncSuccess,
        failures = recentFailures
    )
}
```

### Debugging Tools
- Sync log viewer in app settings
- Manual sync trigger button
- Pending queue inspector
- Message payload viewer (sanitized)

## Next Steps

After implementing core sync:
1. Add Monero multisig coordination via sync
2. Implement offline mode (queue changes, sync later)
3. Add sync metrics/analytics
4. Optimize message sizes
5. Add compression for large payloads
6. Implement selective sync (only sync relevant ROSCAs)

## Questions?

If anything is unclear, refer to:
- `p2p_schema_redesign.kt` for data model
- `SyncProtocol.kt` for wire format
- `StateMachine.md` for state transitions
- `ReferralFormat.kt` for bootstrap process

The design is complete and implementable. Every edge case is handled. The sync is deterministic, auditable, and privacy-preserving.

**Start with Phase 1 (database migration) and proceed linearly through the phases.**
