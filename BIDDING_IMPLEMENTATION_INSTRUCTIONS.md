# ROSCA Bidding Implementation - Manual Updates Required

## ✅ Files Created by Script

The setup script has created the following files:

### Models (with Parcelable)
- `app/src/main/kotlin/com/techducat/ajo/model/Bid.kt` ✅
- `app/src/main/kotlin/com/techducat/ajo/model/Dividend.kt` ✅
- `app/src/main/kotlin/com/techducat/ajo/model/Round.kt` ✅ (updated)

### Entities
- `app/src/main/kotlin/com/techducat/ajo/data/local/entity/BidEntity.kt` ✅
- `app/src/main/kotlin/com/techducat/ajo/data/local/entity/DividendEntity.kt` ✅

### DAOs
- `app/src/main/kotlin/com/techducat/ajo/data/local/dao/BidDao.kt` ✅
- `app/src/main/kotlin/com/techducat/ajo/data/local/dao/DividendDao.kt` ✅

---

## 📝 Manual Updates Required

### 1. Update AjoDatabase.kt

**File:** `app/src/main/kotlin/com/techducat/ajo/data/local/AjoDatabase.kt`

**Changes needed:**

```kotlin
// Add imports at the top
import com.techducat.ajo.data.local.entity.BidEntity
import com.techducat.ajo.data.local.entity.DividendEntity
import com.techducat.ajo.data.local.dao.BidDao
import com.techducat.ajo.data.local.dao.DividendDao

// Update @Database annotation
@Database(
    entities = [
        RoscaEntity::class,
        MemberEntity::class,
        ContributionEntity::class,
        RoundEntity::class,              // Make sure this exists
        BidEntity::class,                // ADD THIS
        DividendEntity::class,           // ADD THIS
        DistributionEntity::class,
        ServiceFeeEntity::class,
        SyncQueueEntity::class,
        UserProfileEntity::class,
        PayoutEntity::class,
        PenaltyEntity::class,
        TransactionEntity::class,
        InviteEntity::class
    ],
    version = 6,  // INCREMENT VERSION from 5 to 6
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class AjoDatabase : RoomDatabase() {
    // ... existing DAOs ...
    abstract fun roundDao(): RoundDao        // Make sure this exists
    abstract fun bidDao(): BidDao            // ADD THIS
    abstract fun dividendDao(): DividendDao  // ADD THIS
    // ... rest of existing DAOs ...
}
```

---

### 2. Update RoscaRepository.kt

**File:** `app/src/main/kotlin/com/techducat/ajo/repository/RoscaRepository.kt`

**Add these methods to the interface:**

```kotlin
// Bid operations
suspend fun insertBid(bid: Bid)
suspend fun updateBid(bid: Bid)
suspend fun getBidsByRoundId(roundId: String): List<Bid>
suspend fun getBidByMemberAndRound(roundId: String, memberId: String): Bid?
suspend fun getHighestBid(roundId: String): Bid?

// Dividend operations
suspend fun insertDividend(dividend: Dividend)
suspend fun getDividendsByRoundId(roundId: String): List<Dividend>
suspend fun getDividendsByMember(memberId: String): List<Dividend>
```

---

### 3. Update RoscaRepositoryImpl.kt

**File:** `app/src/main/kotlin/com/techducat/ajo/repository/impl/RoscaRepositoryImpl.kt`

**Step 3a: Add DAO references**

```kotlin
class RoscaRepositoryImpl(
    private val database: AjoDatabase
) : RoscaRepository {
    
    private val roscaDao = database.roscaDao()
    private val memberDao = database.memberDao()
    private val contributionDao = database.contributionDao()
    private val roundDao = database.roundDao()      // ADD THIS
    private val bidDao = database.bidDao()          // ADD THIS
    private val dividendDao = database.dividendDao() // ADD THIS
    
    // ... rest of code
}
```

**Step 3b: Replace stub methods with implementations**

Replace the stub bid/dividend methods (around lines 340-360) with:

```kotlin
// ============================================================================
// BID OPERATIONS
// ============================================================================

override suspend fun insertBid(bid: Bid) {
    try {
        Log.d(TAG, "Inserting bid: ${bid.id}")
        val entity = bid.toEntity()
        bidDao.insert(entity)
        Log.d(TAG, "✓ Bid inserted successfully")
    } catch (e: Exception) {
        Log.e(TAG, "✗ Failed to insert bid", e)
        throw e
    }
}

override suspend fun updateBid(bid: Bid) {
    try {
        val entity = bid.toEntity()
        bidDao.update(entity)
    } catch (e: Exception) {
        Log.e(TAG, "Error updating bid", e)
        throw e
    }
}

override suspend fun getBidsByRoundId(roundId: String): List<Bid> {
    return try {
        bidDao.getBidsByRoundId(roundId).map { it.toDomain() }
    } catch (e: Exception) {
        Log.e(TAG, "Error getting bids for round: $roundId", e)
        emptyList()
    }
}

override suspend fun getBidByMemberAndRound(roundId: String, memberId: String): Bid? {
    return try {
        bidDao.getBidByMemberAndRound(roundId, memberId)?.toDomain()
    } catch (e: Exception) {
        Log.e(TAG, "Error getting bid", e)
        null
    }
}

override suspend fun getHighestBid(roundId: String): Bid? {
    return try {
        bidDao.getHighestBid(roundId)?.toDomain()
    } catch (e: Exception) {
        Log.e(TAG, "Error getting highest bid", e)
        null
    }
}

// ============================================================================
// DIVIDEND OPERATIONS
// ============================================================================

override suspend fun insertDividend(dividend: Dividend) {
    try {
        Log.d(TAG, "Inserting dividend: ${dividend.id}")
        val entity = dividend.toEntity()
        dividendDao.insert(entity)
        Log.d(TAG, "✓ Dividend inserted successfully")
    } catch (e: Exception) {
        Log.e(TAG, "✗ Failed to insert dividend", e)
        throw e
    }
}

override suspend fun getDividendsByRoundId(roundId: String): List<Dividend> {
    return try {
        dividendDao.getDividendsByRoundId(roundId).map { it.toDomain() }
    } catch (e: Exception) {
        Log.e(TAG, "Error getting dividends for round: $roundId", e)
        emptyList()
    }
}

override suspend fun getDividendsByMember(memberId: String): List<Dividend> {
    return try {
        dividendDao.getDividendsByMember(memberId).map { it.toDomain() }
    } catch (e: Exception) {
        Log.e(TAG, "Error getting dividends for member: $memberId", e)
        emptyList()
    }
}
```

**Step 3c: Add mapping extensions at the bottom**

Add these after the existing mapping extensions:

```kotlin
// Bid mappings
private fun Bid.toEntity() = BidEntity(
    id = id,
    roundId = roundId,
    memberId = memberId,
    bidAmount = bidAmount,
    timestamp = timestamp,
    status = status.name
)

private fun BidEntity.toDomain() = Bid(
    id = id,
    roundId = roundId,
    memberId = memberId,
    bidAmount = bidAmount,
    timestamp = timestamp,
    status = try {
        BidStatus.valueOf(status.uppercase())
    } catch (e: Exception) {
        BidStatus.PENDING
    }
)

// Dividend mappings
private fun Dividend.toEntity() = DividendEntity(
    id = id,
    roundId = roundId,
    memberId = memberId,
    amount = amount,
    transactionHash = transactionHash,
    createdAt = createdAt
)

private fun DividendEntity.toDomain() = Dividend(
    id = id,
    roundId = roundId,
    memberId = memberId,
    amount = amount,
    transactionHash = transactionHash,
    createdAt = createdAt
)
```

---

### 4. Update RoscaManager.kt

**File:** `app/src/main/kotlin/com/techducat/ajo/service/RoscaManager.kt`

**Add the bidding methods from the first artifact:**

Copy these methods into RoscaManager.kt:
- `submitBid()`
- `evaluateBids()`
- `distributeBidDividend()`
- Update `startNewRound()` to handle BIDDING status
- `scheduleBiddingDeadlineCheck()`

All the code is in artifact #1 (rosca_bidding).

---

### 5. Create BiddingActivity (Optional - for UI)

**File:** `app/src/main/kotlin/com/techducat/ajo/ui/bidding/BiddingActivity.kt`

The full UI code is in artifact #2 (bidding_ui).

---

## 🚀 After Completing Manual Updates

1. **Sync Gradle**: File → Sync Project with Gradle Files
2. **Clean Build**: Build → Clean Project
3. **Rebuild**: Build → Rebuild Project
4. **Run App**: The new bidding features should be available

---

## 📊 Testing Checklist

- [ ] Create a ROSCA with BIDDING distribution method
- [ ] All members join the ROSCA
- [ ] First round starts in BIDDING status
- [ ] Members can submit bids
- [ ] Bidding deadline triggers evaluation
- [ ] Highest bidder is selected
- [ ] Contributions are collected
- [ ] Winner receives net payout (pool - bid)
- [ ] All members receive dividend share
- [ ] Next round starts

---

## 🐛 Troubleshooting

### Database version conflict
If you get "Migration" errors, uninstall and reinstall the app to force database recreation.

### Build errors
Make sure all imports are correct and run Gradle sync.

### Round not found errors
Check that RoundEntity exists and RoundDao is properly implemented.

---

## 📚 Additional Resources

See the three artifacts for complete code:
1. **rosca_bidding** - RoscaManager bidding methods
2. **bidding_ui** - BiddingActivity UI components
3. **domain_models** - All model classes with Parcelable

