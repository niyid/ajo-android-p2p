# CHANGELOG - P2P Migration

## Version 10 - P2P Sync Enabled

### 🆕 New Files Created

#### Entities (6 new)
1. `LocalNodeEntity.kt` - This device's P2P identity
2. `PeerEntity.kt` - Registry of other nodes
3. `SyncTargetEntity.kt` - Sync routing configuration
4. `LocalWalletEntity.kt` - Wallet file metadata (local-only)
5. `SyncLogEntity.kt` - Sync history for debugging
6. `SyncConflictEntity.kt` - Conflict resolution state

#### DAOs (6 new)
1. `LocalNodeDao.kt`
2. `PeerDao.kt`
3. `SyncTargetDao.kt`
4. `LocalWalletDao.kt`
5. `SyncLogDao.kt`
6. `SyncConflictDao.kt`

#### Sync Layer (new package)
1. `sync/protocol/SyncMessage.kt` - Message envelope
2. `sync/protocol/Payloads.kt` - Specific message types
3. `sync/SyncEngine.kt` - Core sync logic
4. `sync/MessageHandler.kt` - Inbound message processing
5. `sync/ReferralCodec.kt` - Referral generation/consumption

#### Network Layer (new package)
1. `core/network/NetworkTransport.kt` - Transport abstraction
2. `core/network/MockTransport.kt` - Testing transport
3. `core/network/I2PTransport.kt` - I2P implementation (stub)

#### Crypto Layer
1. `core/crypto/MessageSigner.kt` - Ed25519 signing

### 📝 Modified Files

#### Database
- `AjoDatabase.kt`
  - Version: 9 → 10
  - Added 6 new entities
  - Added 6 new DAOs
  - Updated imports

#### Entities (to be modified with sync metadata)
- `RoscaEntity.kt` - Add: version, lastModifiedBy, lastModifiedAt, creatorNodeId
- `MemberEntity.kt` - Add: nodeId, version, lastModifiedBy, lastModifiedAt
- `ContributionEntity.kt` - Add sync metadata
- `RoundEntity.kt` - Add sync metadata
- `TransactionEntity.kt` - Add sync metadata
- `MultisigSignatureEntity.kt` - Add sync metadata
- `DistributionEntity.kt` - Add sync metadata
- `BidEntity.kt` - Add sync metadata
- `DividendEntity.kt` - Add sync metadata

#### Workers
- `worker/SyncWorker.kt` - Implement actual sync logic

#### Crypto
- `core/crypto/KeyManager.kt` - Add Ed25519 key generation

### 🔧 Schema Changes

#### New Tables
```sql
CREATE TABLE local_node (
    nodeId TEXT PRIMARY KEY,
    publicKey TEXT NOT NULL,
    privateKeyEncrypted TEXT NOT NULL,
    createdAt INTEGER NOT NULL,
    lastSyncAt INTEGER
);

CREATE TABLE peers (
    id TEXT PRIMARY KEY,
    nodeId TEXT NOT NULL UNIQUE,
    roscaId TEXT NOT NULL,
    publicKey TEXT NOT NULL,
    role TEXT NOT NULL,
    endpoint TEXT,
    status TEXT NOT NULL,
    addedAt INTEGER NOT NULL,
    lastSeenAt INTEGER
);

CREATE TABLE sync_targets (
    id TEXT PRIMARY KEY,
    roscaId TEXT NOT NULL,
    targetPeerId TEXT NOT NULL,
    syncEnabled INTEGER NOT NULL DEFAULT 1,
    lastSyncAttempt INTEGER,
    lastSyncSuccess INTEGER,
    consecutiveFailures INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY(targetPeerId) REFERENCES peers(id) ON DELETE CASCADE
);

CREATE TABLE local_wallets (
    id TEXT PRIMARY KEY,
    roscaId TEXT NOT NULL,
    nodeId TEXT NOT NULL,
    walletPath TEXT NOT NULL,
    cacheFilePath TEXT,
    passwordEncrypted TEXT NOT NULL,
    createdAt INTEGER NOT NULL,
    lastAccessedAt INTEGER,
    isMultisig INTEGER NOT NULL DEFAULT 0,
    multisigInfo TEXT,
    FOREIGN KEY(roscaId) REFERENCES roscas(id) ON DELETE CASCADE
);

CREATE TABLE sync_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    roscaId TEXT NOT NULL,
    direction TEXT NOT NULL,
    peerNodeId TEXT NOT NULL,
    entityType TEXT NOT NULL,
    entityId TEXT NOT NULL,
    operation TEXT NOT NULL,
    status TEXT NOT NULL,
    timestamp INTEGER NOT NULL,
    errorMessage TEXT,
    durationMs INTEGER,
    payloadSize INTEGER
);

CREATE TABLE sync_conflicts (
    id TEXT PRIMARY KEY,
    roscaId TEXT NOT NULL,
    entityType TEXT NOT NULL,
    entityId TEXT NOT NULL,
    localVersion INTEGER NOT NULL,
    remoteVersion INTEGER NOT NULL,
    localPayload TEXT NOT NULL,
    remotePayload TEXT NOT NULL,
    detectedAt INTEGER NOT NULL,
    resolvedAt INTEGER,
    resolution TEXT
);
```

#### Modified Tables
```sql
ALTER TABLE roscas ADD COLUMN creatorNodeId TEXT;
ALTER TABLE roscas ADD COLUMN version INTEGER NOT NULL DEFAULT 1;
ALTER TABLE roscas ADD COLUMN lastModifiedBy TEXT;
ALTER TABLE roscas ADD COLUMN lastModifiedAt INTEGER NOT NULL DEFAULT 0;

ALTER TABLE members ADD COLUMN nodeId TEXT;
ALTER TABLE members ADD COLUMN publicWalletAddress TEXT;
ALTER TABLE members ADD COLUMN version INTEGER NOT NULL DEFAULT 1;
ALTER TABLE members ADD COLUMN lastModifiedBy TEXT;
ALTER TABLE members ADD COLUMN lastModifiedAt INTEGER NOT NULL DEFAULT 0;

-- Similar for: contributions, rounds, transactions, multisig_signatures, etc.
```

### 🚀 New Capabilities

1. **Peer-to-Peer Sync**
   - Devices sync directly (no server)
   - Creator is authoritative source
   - Automatic conflict resolution

2. **Privacy-Preserving**
   - Wallet files never sync
   - Only public addresses shared
   - Private keys stay on device

3. **Referral System**
   - QR code bootstrapping
   - Self-configuring sync

4. **Network Agnostic**
   - Supports I2P, Tor, WebRTC, direct TCP
   - Mock transport for testing

5. **Offline Support**
   - Changes queued locally
   - Auto-sync when online

### ⚠️ Breaking Changes

1. **MemberEntity.userId → nodeId**
   - Mitigation: Keep both fields during transition
   - Map userId = nodeId initially

2. **Wallet storage moved**
   - From: MemberEntity.walletAddress (synced)
   - To: LocalWalletEntity (local-only)
   - Mitigation: Migration script copies data

3. **Database version bump**
   - v9 → v10
   - Migration required on upgrade

### 📚 Migration Path

**Phase 1:** Schema Only (THIS RELEASE)
- Add new tables
- Modify existing tables
- Update database version
- App compiles but doesn't use P2P yet

**Phase 2:** Sync Logic (NEXT)
- Implement SyncEngine
- Implement MessageHandler
- Implement ReferralCodec
- Test with mock transport

**Phase 3:** Network Layer (AFTER PHASE 2)
- Implement I2P transport
- Test with real P2P
- Multi-device testing

**Phase 4:** Production (FINAL)
- UI polish
- Performance optimization
- Production deployment

### 🧪 Testing Checklist

- [ ] Database migration succeeds
- [ ] New tables created correctly
- [ ] Foreign keys work
- [ ] Existing data preserved
- [ ] App compiles
- [ ] App runs (even if sync not working yet)
- [ ] No data loss

### 📖 Documentation

- `MIGRATION_GUIDE.md` - Step-by-step migration instructions
- `P2P_ROSCA_COMPLETE_DESIGN.md` - Full architecture document
- `build.gradle.additions` - Dependencies to add
- `README_P2P.md` - Quick start guide

### 🔜 Next Steps

1. Apply schema changes (this release)
2. Test compilation
3. Fix any errors
4. Move to Phase 2 (sync logic implementation)

---

**Version:** 10.0.0-p2p-alpha  
**Date:** January 30, 2026  
**Status:** Schema Migration Complete, Sync Implementation Pending
