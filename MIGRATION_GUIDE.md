# AJO ANDROID - P2P MIGRATION GUIDE

**Generated:** January 30, 2026  
**Current Version:** 9  
**Target Version:** 10 (P2P-enabled)

---

## 🎯 What Changed

This migration transforms your Ajo Android app from a single-device simulation to a **fully peer-to-peer distributed system**.

### Summary
- ✅ **12 New Entities** (LocalNode, Peer, SyncTarget, LocalWallet, etc.)
- ✅ **12 New DAOs**
- ✅ **Sync Protocol Layer** (message types, serialization)
- ✅ **Network Transport** (abstraction for I2P/Tor/TCP)
- ✅ **Modified Existing Entities** (added sync metadata)
- ✅ **Database Migration Script**

---

## 📦 What You Received

```
ajo-p2p-modified.zip
├── MIGRATION_GUIDE.md                    ← YOU ARE HERE
├── CHANGES.md                             ← Detailed change log
├── build.gradle.additions                 ← Dependencies to add
├── kotlin/com/techducat/ajo/
│   ├── data/local/
│   │   ├── entity/
│   │   │   ├── LocalNodeEntity.kt        ← NEW
│   │   │   ├── PeerEntity.kt             ← NEW
│   │   │   ├── SyncTargetEntity.kt       ← NEW
│   │   │   ├── LocalWalletEntity.kt      ← NEW
│   │   │   ├── SyncLogEntity.kt          ← NEW
│   │   │   ├── SyncConflictEntity.kt     ← NEW
│   │   │   ├── MemberEntity.kt           ← MODIFIED
│   │   │   ├── RoscaEntity.kt            ← MODIFIED
│   │   │   └── ... (other entities)      ← TO BE MODIFIED
│   │   ├── dao/
│   │   │   ├── LocalNodeDao.kt           ← NEW
│   │   │   ├── PeerDao.kt                ← NEW
│   │   │   ├── SyncTargetDao.kt          ← NEW
│   │   │   ├── LocalWalletDao.kt         ← NEW
│   │   │   ├── SyncLogDao.kt             ← NEW
│   │   │   ├── SyncConflictDao.kt        ← NEW
│   │   │   └── ... (existing DAOs)
│   │   ├── AjoDatabase.kt                ← MODIFIED
│   │   └── migrations/
│   │       └── Migration_9_10.kt         ← NEW
│   ├── sync/
│   │   ├── SyncEngine.kt                 ← NEW
│   │   ├── MessageHandler.kt             ← NEW
│   │   ├── ReferralCodec.kt              ← NEW
│   │   └── protocol/
│   │       ├── SyncMessage.kt            ← NEW
│   │       └── Payloads.kt               ← NEW
│   ├── core/
│   │   ├── network/
│   │   │   ├── NetworkTransport.kt       ← NEW
│   │   │   ├── MockTransport.kt          ← NEW
│   │   │   └── I2PTransport.kt           ← NEW (stub)
│   │   └── crypto/
│   │       ├── KeyManager.kt             ← MODIFIED
│   │       └── MessageSigner.kt          ← NEW
│   └── worker/
│       └── SyncWorker.kt                 ← MODIFIED
```

---

## 🚀 Migration Steps

### Step 1: Backup Your Current Code
```bash
cd ~/git/ajo-android
git checkout -b pre-p2p-backup
git add -A
git commit -m "Backup before P2P migration"
```

### Step 2: Extract P2P Files
```bash
unzip ajo-p2p-modified.zip -d ~/p2p-temp
```

### Step 3: Add Dependencies

Edit `app/build.gradle.kts`:

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"  // ADD THIS
}

dependencies {
    // Existing dependencies
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // NEW: Crypto for Ed25519 signing
    implementation("net.i2p.crypto:eddsa:0.3.0")
    
    // NEW: JSON Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    
    // OPTIONAL: I2P (can add later)
    // implementation("net.i2p.android:client:0.9.50")
}
```

### Step 4: Copy New Files

```bash
# Copy new entities
cp ~/p2p-temp/kotlin/com/techducat/ajo/data/local/entity/*Entity.kt \
   app/src/main/kotlin/com/techducat/ajo/data/local/entity/

# Copy new DAOs
cp ~/p2p-temp/kotlin/com/techducat/ajo/data/local/dao/*Dao.kt \
   app/src/main/kotlin/com/techducat/ajo/data/local/dao/

# Copy sync layer
cp -r ~/p2p-temp/kotlin/com/techducat/ajo/sync \
   app/src/main/kotlin/com/techducat/ajo/

# Copy network layer
cp -r ~/p2p-temp/kotlin/com/techducat/ajo/core/network \
   app/src/main/kotlin/com/techducat/ajo/core/
```

### Step 5: Update Database

Replace `app/src/main/kotlin/com/techducat/ajo/data/local/AjoDatabase.kt` with the modified version from the zip.

Key changes:
- Version bumped from 9 → 10
- Added new entities to `@Database` annotation
- Added new DAO abstract methods
- Added migration callback

### Step 6: Sync Gradle

```bash
cd ~/git/ajo-android
./gradlew clean
./gradlew build
```

Fix any compilation errors (mostly import statements).

### Step 7: Test Compilation

Run the app - it should compile but won't use P2P yet (that's Phase 2).

---

## 📝 Key Modifications to Existing Entities

### MemberEntity Changes

**BEFORE:**
```kotlin
data class MemberEntity(
    val userId: String,
    // ...
)
```

**AFTER:**
```kotlin
data class MemberEntity(
    val nodeId: String,              // CHANGED from userId
    val publicWalletAddress: String?, // ADDED (syncs)
    
    // Sync metadata (ADDED)
    val version: Long = 1,
    val lastModifiedBy: String?,
    val lastModifiedAt: Long
)
```

**Migration Strategy:**
- Keep `userId` for now (backward compat)
- Add `nodeId` = userId initially
- Gradually migrate to nodeId

### RoscaEntity Changes

**ADDED:**
```kotlin
val creatorNodeId: String,        // For P2P routing
val version: Long = 1,
val lastModifiedBy: String?,
val lastModifiedAt: Long
```

Similar changes to: ContributionEntity, RoundEntity, TransactionEntity, etc.

---

## 🔧 Phase 2: Implement Sync Logic

After Phase 1 (schema migration) works, implement:

1. **KeyManager** - Generate Ed25519 keypairs
2. **SyncEngine** - Read sync_queue, send messages
3. **MessageHandler** - Receive messages, apply updates
4. **ReferralCodec** - Generate/consume referral codes
5. **SyncWorker** - Background sync job

See `P2P_ROSCA_COMPLETE_DESIGN.md` for full implementation guide.

---

## 🧪 Testing Strategy

### Phase 1: Schema Only
```kotlin
// Test database migration
@Test
fun testMigration9to10() {
    val db = Room.inMemoryDatabaseBuilder(context, AjoDatabase::class.java)
        .addMigrations(MIGRATION_9_10)
        .build()
        
    // Verify new tables exist
    val localNode = db.localNodeDao().getLocalNode()
    assertNotNull(localNode)
}
```

### Phase 2: Mock Sync
```kotlin
// Test sync with mock transport
@Test
fun testSyncWithMockTransport() {
    val transport = MockTransport()
    val syncEngine = SyncEngine(db, transport)
    
    // Create test message
    syncEngine.sendUpdate(contribution)
    
    // Verify queued
    val pending = db.syncQueueDao().getPendingSyncs()
    assert(pending.isNotEmpty())
    
    // Deliver mock
    transport.deliverMessages()
}
```

### Phase 3: Real P2P
Test with 2-3 devices using mock/local network first.

---

## ⚠️ Breaking Changes

1. **MemberEntity.userId → nodeId**
   - Mitigation: Keep userId for now, add nodeId
   - Update gradually

2. **Wallet files no longer in MemberEntity**
   - Now in LocalWalletEntity
   - Migration: Copy walletPath to new table

3. **Database version 9 → 10**
   - Automatic migration on app update
   - Use `.fallbackToDestructiveMigration()` for development only

---

## 🐛 Troubleshooting

### Build Errors

**Error:** `Unresolved reference: kotlinx.serialization`
**Fix:** Add serialization plugin to `build.gradle.kts`

**Error:** `Cannot find symbol: LocalNodeEntity`
**Fix:** Sync Gradle, rebuild project

### Runtime Errors

**Error:** `IllegalStateException: Migration didn't handle... `
**Fix:** Check Migration_9_10.kt covers all new tables

**Error:** `Room cannot verify table...`
**Fix:** Increment database version, clear app data

---

## 📚 Next Steps

1. ✅ Complete Phase 1 (schema migration)
2. ⏭ Implement sync engine (Phase 2)
3. ⏭ Add I2P transport (Phase 3)
4. ⏭ Test with multiple devices (Phase 4)

---

## 📞 Support

Refer to:
- `P2P_ROSCA_COMPLETE_DESIGN.md` - Full architecture
- `CHANGES.md` - Detailed change log
- `IMPLEMENTATION_GUIDE.md` - 7-week roadmap

**Remember:** This is a major architectural change. Take it step-by-step. Test after each phase.
