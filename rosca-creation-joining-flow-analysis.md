# ROSCA Creation and Joining Process - Method Call Chain Analysis

This document provides a comprehensive method-by-method analysis of the ROSCA (Rotating Savings and Credit Association) creation and joining processes in the AJO Android P2P application.

---

## Table of Contents
1. [ROSCA Creation Flow](#rosca-creation-flow)
2. [ROSCA Joining Flow](#rosca-joining-flow)
3. [Key Components](#key-components)
4. [Database Operations](#database-operations)

---

## ROSCA Creation Flow

### High-Level Overview
The ROSCA creation process involves:
1. **UI Layer**: User input validation
2. **Service Layer**: Wallet creation and multisig initialization
3. **Repository Layer**: Database persistence
4. **Wallet Layer**: Monero wallet operations

### Detailed Method Call Chain

```
┌─────────────────────────────────────────────────────────────────────┐
│                    ROSCA CREATION SEQUENCE                          │
└─────────────────────────────────────────────────────────────────────┘

User Interaction
    ↓
CreateRoscaActivity.onCreate()
    ↓
    ├─→ setupViews()
    │   └─→ buttonCreate.setOnClickListener { createRosca() }
    │
    ├─→ setupLoginObservers()
    │   └─→ loginViewModel.uiState.collect()
    │
    └─→ checkLoginAndInitialize()
        └─→ getUserId() from SharedPreferences

User Clicks "Create ROSCA" Button
    ↓
CreateRoscaActivity.createRosca()
    │
    ├─→ Validate Inputs
    │   ├─→ name.isBlank()
    │   ├─→ members < 2
    │   ├─→ contributionAmount <= 0
    │   └─→ frequencyDays <= 0
    │
    ├─→ showLoading(true)
    │   ├─→ buttonCreate.isEnabled = false
    │   ├─→ loadingOverlay.visibility = VISIBLE
    │   └─→ Disable all input fields
    │
    └─→ lifecycleScope.launch {
            roscaManager.createRosca(...)
        }
            ↓
    ┌───────────────────────────────────────────────────────────────┐
    │            RoscaManager.createRosca()                         │
    └───────────────────────────────────────────────────────────────┘
            │
            ├─→ PHASE 1: INPUT VALIDATION
            │   ├─→ Validate name.isBlank()
            │   ├─→ Validate totalMembers >= 2
            │   ├─→ Validate contributionAmount > 0
            │   ├─→ Validate frequencyDays > 0
            │   ├─→ Get userId from SharedPreferences
            │   └─→ Generate roscaId: "rosca_${timestamp}_${hashCode}"
            │
            ├─→ PHASE 2: WALLET CREATION
            │   │
            │   └─→ WalletSelectionManager.createFreshRoscaWallet()
            │       ├─→ Generate unique wallet path
            │       │   walletPath = "rosca_${userId}_${roscaId}.wallet"
            │       │
            │       ├─→ walletSuite.createWallet()
            │       │   ├─→ Generate random seed (25-word mnemonic)
            │       │   ├─→ Create wallet file at walletPath
            │       │   ├─→ Initialize wallet structure
            │       │   └─→ Return wallet path
            │       │
            │       └─→ Return Result<walletPath>
            │
            ├─→ PHASE 3: WALLET VERIFICATION
            │   │
            │   ├─→ WalletSelectionManager.switchToRoscaWallet()
            │   │   ├─→ walletSuite.closeCurrentWallet()
            │   │   ├─→ walletSuite.openWallet(walletPath)
            │   │   │   ├─→ Load wallet from file
            │   │   │   ├─→ Unlock with password
            │   │   │   └─→ Prepare for multisig
            │   │   │
            │   │   └─→ Return Result<Unit>
            │   │
            │   ├─→ getCurrentWalletAddress()
            │   │   └─→ walletSuite.getAddress()
            │   │       └─→ Return wallet.getAddress()
            │   │
            │   └─→ getMultisigInfo()
            │       └─→ walletSuite.getMultisigInfo()
            │           ├─→ wallet.prepareMultisig()
            │           │   ├─→ Generate multisig preparation data
            │           │   ├─→ Create exchange information
            │           │   └─→ Return multisig info string
            │           │
            │           └─→ Validate multisigInfo
            │               ├─→ Check not blank
            │               └─→ Check length >= 100
            │
            ├─→ PHASE 4: DATABASE TRANSACTION (Atomic)
            │   │
            │   ├─→ Calculate threshold = totalMembers - 1
            │   │
            │   ├─→ Create Rosca object
            │   │   ├─→ id = roscaId
            │   │   ├─→ name = name
            │   │   ├─→ creatorId = userId
            │   │   ├─→ totalMembers = totalMembers
            │   │   ├─→ currentMembers = 1
            │   │   ├─→ contributionAmount = contributionAmount
            │   │   ├─→ frequencyDays = frequencyDays
            │   │   ├─→ status = RoscaState.SETUP
            │   │   ├─→ multisigAddress = null (not yet finalized)
            │   │   └─→ roscaWalletPath = walletPath
            │   │
            │   ├─→ Create Member object (Creator)
            │   │   ├─→ id = UUID.randomUUID()
            │   │   ├─→ roscaId = roscaId
            │   │   ├─→ userId = userId
            │   │   ├─→ walletAddress = roscaWalletAddress
            │   │   ├─→ multisigInfo = MultisigInfo(
            │   │   │       address = roscaWalletAddress,
            │   │   │       exchangeState = multisigInfo,
            │   │   │       isReady = false
            │   │   │   )
            │   │   ├─→ position = 0
            │   │   └─→ isActive = true
            │   │
            │   └─→ repository.withTransaction {
            │           │
            │           ├─→ repository.insertRosca(rosca)
            │           │   └─→ roscaDao.insert(rosca.toEntity())
            │           │       └─→ Database INSERT into rosca_table
            │           │
            │           └─→ repository.insertMember(creatorMember)
            │               └─→ memberDao.insert(member.toEntity())
            │                   └─→ Database INSERT into member_table
            │       }
            │
            ├─→ PHASE 5: POST-CREATION (Optional)
            │   │
            │   └─→ dltProvider.publishRoscaCreation()
            │       ├─→ Create DLT record
            │       ├─→ ipfsProvider.publishToIPFS()
            │       └─→ Store IPFS hash
            │
            └─→ Return Result.success(rosca)

    ↓
CreateRoscaActivity receives Result
    │
    ├─→ if (result.isSuccess)
    │   ├─→ showLoading(false)
    │   ├─→ Toast.makeText("ROSCA created successfully!")
    │   └─→ finish() // Return to dashboard
    │
    └─→ if (result.isFailure)
        ├─→ showLoading(false)
        ├─→ Toast.makeText("Error: ${error}")
        └─→ Keep form open for retry
```

---

## ROSCA Joining Flow

### High-Level Overview
The ROSCA joining process involves:
1. **Discovery**: Receiving invite link or scanning QR code
2. **Verification**: Loading ROSCA details and validating availability
3. **Wallet Creation**: Creating member-specific wallet
4. **Registration**: Joining as member and exchanging multisig info
5. **Finalization**: Auto-triggering multisig setup when all members joined

### Detailed Method Call Chain

```
┌─────────────────────────────────────────────────────────────────────┐
│                     ROSCA JOINING SEQUENCE                          │
└─────────────────────────────────────────────────────────────────────┘

User Receives Invite Link
    │ (e.g., ajo://join/{roscaId}/{token})
    ↓
Android System
    └─→ Launch JoinRoscaActivity with intent data

JoinRoscaActivity.onCreate()
    │
    ├─→ setupToolbar()
    │   └─→ Set title "Join ROSCA"
    │
    ├─→ parseIntentData()
    │   ├─→ Extract roscaId from intent
    │   ├─→ Extract setupInfo from intent
    │   ├─→ Extract inviteToken from intent
    │   └─→ Parse deep link URI if present
    │
    ├─→ setupViews()
    │   ├─→ buttonJoin.setOnClickListener { handleJoinClick() }
    │   └─→ buttonCancel.setOnClickListener { finish() }
    │
    ├─→ setupLoginObservers()
    │   └─→ Monitor login state
    │
    └─→ checkLoginAndInitialize()
        ├─→ getUserId() from SharedPreferences
        │
        └─→ if (userId.isNotEmpty())
                onLoginSuccess()
                    └─→ loadRoscaDetails()
                            ↓
                    ┌─────────────────────────────────────────┐
                    │  loadRoscaDetails() - Display ROSCA Info │
                    └─────────────────────────────────────────┘
                            │
                            ├─→ showLoading("Loading ROSCA details...")
                            │
                            ├─→ lifecycleScope.launch {
                            │       database.roscaDao().getRoscaById(roscaId)
                            │   }
                            │   └─→ Query ROSCA from local database
                            │
                            ├─→ Display ROSCA Information
                            │   ├─→ textViewRoscaName.text = rosca.name
                            │   ├─→ textViewDescription.text = rosca.description
                            │   ├─→ textViewMembers.text = "${rosca.totalMembers} members"
                            │   ├─→ textViewContribution.text = formatXMR(amount)
                            │   └─→ textViewFrequency.text = "${rosca.frequencyDays} days"
                            │
                            ├─→ Load Current Members
                            │   └─→ database.memberDao().getMembersByGroupSync(roscaId)
                            │       └─→ Count active members
                            │           ├─→ textViewCurrentMembers = "${activeMembers} joined"
                            │           └─→ Check if ROSCA is full
                            │
                            ├─→ Validate Can Join
                            │   └─→ if (activeMembers < totalMembers)
                            │       ├─→ buttonJoin.isEnabled = true
                            │       └─→ else show "ROSCA is full" warning
                            │
                            └─→ hideLoading()

User Clicks "Join ROSCA" Button
    ↓
JoinRoscaActivity.handleJoinClick()
    │
    ├─→ showLoading("Preparing to join...")
    │
    └─→ Show Confirmation Dialog
            MaterialAlertDialogBuilder
            .setTitle("Confirm Join")
            .setMessage("Are you sure you want to join this ROSCA?")
            .setPositiveButton("Join") {
                performJoin()
            }
                ↓
JoinRoscaActivity.performJoin()
    │
    ├─→ isJoiningRosca = true
    ├─→ showLoading("Joining ROSCA...")
    │
    └─→ lifecycleScope.launch {
            roscaManager.joinRosca(roscaId, setupInfo, context)
        }
            ↓
    ┌───────────────────────────────────────────────────────────────┐
    │              RoscaManager.joinRosca()                         │
    └───────────────────────────────────────────────────────────────┘
            │
            ├─→ PHASE 1: VALIDATION
            │   │
            │   ├─→ repository.getRoscaById(roscaId)
            │   │   └─→ Verify ROSCA exists
            │   │
            │   ├─→ Check ROSCA not full
            │   │   └─→ if (currentMembers >= totalMembers) → FAIL
            │   │
            │   ├─→ Get userId from SharedPreferences
            │   │   └─→ Verify user logged in
            │   │
            │   └─→ repository.getMembersByRoscaId(roscaId)
            │       └─→ Check user hasn't already joined
            │           └─→ if (members.any { it.userId == userId }) → FAIL
            │
            ├─→ PHASE 2: WALLET CREATION
            │   │
            │   └─→ WalletSelectionManager.createFreshRoscaWallet()
            │       ├─→ Generate wallet path
            │       │   walletPath = "rosca_${userId}_${roscaId}.wallet"
            │       │
            │       ├─→ walletSuite.createWallet()
            │       │   ├─→ Generate NEW random seed (independent)
            │       │   ├─→ Create wallet file
            │       │   ├─→ Initialize wallet
            │       │   └─→ Return wallet path
            │       │
            │       └─→ Return Result<walletPath>
            │
            ├─→ PHASE 3: WALLET VERIFICATION
            │   │
            │   ├─→ WalletSelectionManager.switchToRoscaWallet()
            │   │   ├─→ Close current wallet
            │   │   ├─→ Open new ROSCA wallet
            │   │   └─→ Prepare for multisig
            │   │
            │   ├─→ getCurrentWalletAddress()
            │   │   └─→ Get joiner's wallet address
            │   │
            │   ├─→ Verify wallet address not already used
            │   │   └─→ Check against existing member addresses
            │   │
            │   └─→ getMultisigInfo()
            │       └─→ walletSuite.getMultisigInfo()
            │           ├─→ wallet.prepareMultisig()
            │           ├─→ Generate exchange information
            │           └─→ Validate multisigInfo (length >= 100)
            │
            ├─→ PHASE 4: DATABASE TRANSACTION (Atomic)
            │   │
            │   ├─→ Calculate position = existingMembers.size
            │   │
            │   ├─→ Create Member object
            │   │   ├─→ id = UUID.randomUUID()
            │   │   ├─→ roscaId = roscaId
            │   │   ├─→ userId = userId
            │   │   ├─→ walletAddress = userWalletAddress
            │   │   ├─→ multisigInfo = MultisigInfo(
            │   │   │       address = userWalletAddress,
            │   │   │       exchangeState = multisigInfo,
            │   │   │       isReady = false
            │   │   │   )
            │   │   ├─→ position = position
            │   │   └─→ isActive = true
            │   │
            │   ├─→ Update ROSCA object
            │   │   └─→ updatedRosca = rosca.copy(
            │   │           currentMembers = currentMembers + 1
            │   │       )
            │   │
            │   └─→ repository.withTransaction {
            │           │
            │           ├─→ repository.insertMember(member)
            │           │   └─→ memberDao.insert(member.toEntity())
            │           │       └─→ Database INSERT into member_table
            │           │
            │           └─→ repository.updateRosca(updatedRosca)
            │               └─→ roscaDao.update(rosca.toEntity())
            │                   └─→ Database UPDATE rosca_table
            │                       SET currentMembers = currentMembers + 1
            │       }
            │
            ├─→ PHASE 5: AUTO-FINALIZATION CHECK
            │   │
            │   └─→ checkAndTriggerFinalization(roscaId, userId)
            │       │
            │       ├─→ repository.getRoscaById(roscaId)
            │       │   └─→ Get latest ROSCA state
            │       │
            │       ├─→ Check if ready to finalize
            │       │   └─→ if (currentMembers == totalMembers)
            │       │           AND status == SETUP
            │       │
            │       └─→ if (ready to finalize)
            │               │
            │               ├─→ repository.getMembersByRoscaId(roscaId)
            │               │   └─→ Get all members
            │               │
            │               ├─→ Collect all multisig infos
            │               │   └─→ allMultisigInfos = members.map {
            │               │           it.multisigInfo?.exchangeState
            │               │       }
            │               │
            │               ├─→ Validate all members have multisig info
            │               │   └─→ if (any info is null or blank) → SKIP
            │               │
            │               └─→ finalizeSetup(roscaId, allMultisigInfos, userId)
            │                       ↓
            │               ┌────────────────────────────────────────┐
            │               │    MULTISIG FINALIZATION PROCESS       │
            │               └────────────────────────────────────────┘
            │                       │
            │                       ├─→ STEP 1: Validate State
            │                       │   ├─→ Get ROSCA
            │                       │   ├─→ Check status != ACTIVE
            │                       │   ├─→ Validate member count
            │                       │   └─→ Validate multisig info count
            │                       │
            │                       ├─→ STEP 2: Validate Format
            │                       │   └─→ For each multisig info:
            │                       │       ├─→ Check not blank
            │                       │       ├─→ Parse as JSON
            │                       │       ├─→ Verify required fields
            │                       │       └─→ Validate exchangeState
            │                       │
            │                       ├─→ STEP 3: Switch Wallet
            │                       │   └─→ WalletSelectionManager.switchToRoscaWallet()
            │                       │       └─→ allowIncompleteMultisig = true
            │                       │
            │                       ├─→ STEP 4: Call makeMultisig (Round 1)
            │                       │   │
            │                       │   ├─→ Calculate threshold = totalMembers - 1
            │                       │   │
            │                       │   └─→ walletSuite.finalizeRoscaSetup()
            │                       │       │
            │                       │       ├─→ Filter out own multisig info
            │                       │       │   └─→ otherInfos = allInfos.filter {
            │                       │       │           it.address != myAddress
            │                       │       │       }
            │                       │       │
            │                       │       ├─→ wallet.makeMultisig()
            │                       │       │   ├─→ Process other members' info
            │                       │       │   ├─→ Generate multisig keys
            │                       │       │   ├─→ Create partial multisig wallet
            │                       │       │   └─→ Return (result, isReady)
            │                       │       │
            │                       │       └─→ Return to callback
            │                       │           ├─→ onSuccess(roscaId, result, isReady)
            │                       │           └─→ onError(error)
            │                       │
            │                       ├─→ STEP 5: Handle Result
            │                       │   │
            │                       │   ├─→ if (!isReady) // Multi-Round Path
            │                       │   │   │
            │                       │   │   ├─→ Log "Multi-round exchange required"
            │                       │   │   │
            │                       │   │   ├─→ Store Round 2 exchange info
            │                       │   │   │   └─→ repository.withTransaction {
            │                       │   │   │           updateMember(
            │                       │   │   │               multisigInfo.copy(
            │                       │   │   │                   exchangeState = result
            │                       │   │   │               )
            │                       │   │   │           )
            │                       │   │   │       }
            │                       │   │   │
            │                       │   │   ├─→ scheduleKeyExchangeCheck()
            │                       │   │   │   └─→ Monitor for all members' round 2 info
            │                       │   │   │
            │                       │   │   └─→ Return success (partial)
            │                       │   │
            │                       │   └─→ if (isReady) // Single-Round Path
            │                       │       │
            │                       │       ├─→ multisigAddress = result
            │                       │       │
            │                       │       ├─→ Validate address
            │                       │       │   ├─→ Check not blank
            │                       │       │   └─→ Check length >= 95
            │                       │       │
            │                       │       └─→ Continue to Step 6
            │                       │
            │                       └─→ STEP 6: Update Database (Atomic)
            │                           │
            │                           ├─→ updatedRosca = rosca.copy(
            │                           │       multisigAddress = multisigAddress,
            │                           │       status = RoscaState.ACTIVE,
            │                           │       startedAt = currentTime
            │                           │   )
            │                           │
            │                           ├─→ updatedMembers = members.map {
            │                           │       it.copy(
            │                           │           multisigInfo = 
            │                           │               multisigInfo.copy(isReady = true)
            │                           │       )
            │                           │   }
            │                           │
            │                           └─→ repository.withTransaction {
            │                                   ├─→ updateRosca(updatedRosca)
            │                                   └─→ updatedMembers.forEach {
            │                                           updateMember(it)
            │                                       }
            │                               }
            │
            └─→ Return Result.success(member)

    ↓
JoinRoscaActivity receives Result
    │
    ├─→ delay(1500) // Allow finalization to complete
    │
    ├─→ Check ROSCA status
    │   └─→ database.roscaDao().getRoscaById(roscaId)
    │
    ├─→ Determine finalization state
    │   └─→ isFinalized = (status == ACTIVE) && (multisigAddress != null)
    │
    ├─→ if (result.isSuccess)
    │   │
    │   ├─→ hideLoading()
    │   │
    │   └─→ if (isFinalized)
    │       │   └─→ showSuccessDialog(
    │       │           message = "Successfully joined! ROSCA is now active.",
    │       │           isFinalized = true,
    │       │           multisigAddress = rosca.multisigAddress
    │       │       )
    │       │       └─→ Show "View ROSCA" button → finish()
    │       │
    │       └─→ if (!isFinalized)
    │           └─→ showSuccessDialog(
    │                   message = "Successfully joined! Waiting for more members.",
    │                   isFinalized = false,
    │                   multisigAddress = multisigInfo
    │               )
    │               └─→ Show "Done" and "Copy Info" buttons → finish()
    │
    └─→ if (result.isFailure)
        ├─→ hideLoading()
        └─→ showError("Failed to join: ${error}")

```

---

## Key Components

### 1. WalletSelectionManager

**Purpose**: Manages wallet creation, switching, and lifecycle

**Key Methods**:
```kotlin
// Create a fresh wallet with random seed
createFreshRoscaWallet(context, userId, roscaId, walletSuite)
    → Generate unique wallet path
    → Call walletSuite.createWallet()
    → Return wallet path

// Switch to a specific ROSCA wallet
switchToRoscaWallet(context, userId, roscaId, roscaName, multisigAddress, walletSuite)
    → Close current wallet
    → Open target wallet
    → Optionally import multisig info
    → Return success/failure

// Get wallet path for a ROSCA
getRoscaWalletPath(context, userId, roscaId)
    → Return standardized path

// Check if wallet exists
walletExists(walletPath)
    → Check file system
```

### 2. WalletSuite

**Purpose**: Interface to Monero wallet functionality

**Key Methods**:
```kotlin
// Create new wallet
createWallet(path, password)
    → Generate 25-word seed
    → Create wallet files
    → Return wallet instance

// Get multisig preparation info
getMultisigInfo(callback)
    → wallet.prepareMultisig()
    → Return exchange information

// Finalize multisig setup
finalizeRoscaSetup(roscaId, allMultisigInfos, threshold, callback)
    → Filter own info
    → wallet.makeMultisig(otherInfos, threshold)
    → Check if ready (single vs multi-round)
    → Return (addressOrExchangeInfo, isReady)

// Exchange multisig keys (Round 2+)
exchangeMultisigKeys(exchangeInfos, callback)
    → wallet.exchangeMultisigKeys(exchangeInfos)
    → Check wallet.isMultisig()
    → Return final address if ready
```

### 3. RoscaRepository

**Purpose**: Database abstraction layer

**Key Methods**:
```kotlin
// ROSCA operations
insertRosca(rosca): Long
getRoscaById(roscaId): Rosca?
updateRosca(rosca): Int
deleteRosca(roscaId): Int

// Member operations
insertMember(member): Long
getMembersByRoscaId(roscaId): List<Member>
updateMember(member): Int

// Transaction wrapper
withTransaction(block: suspend () -> Unit)
    → Begin transaction
    → Execute block
    → Commit or rollback
```

### 4. MultisigCoordinator

**Purpose**: Handle multi-round multisig key exchange

**Key Methods**:
```kotlin
// Monitor pending signatures
observePendingSignatures(roscaId): Flow<List<PendingSignature>>

// Sign a transaction
signTransaction(txId): SigningResult
    → Load transaction
    → wallet.signMultisigTxHex()
    → Store signature
    → Return status

// Coordinate key exchange rounds
performKeyExchange(roscaId, userId): Result<Unit>
    → Collect all members' exchange info
    → wallet.exchangeMultisigKeys()
    → Update database
    → Check if finalized
```

---

## Database Operations

### Tables Involved

#### 1. rosca_table
```sql
CREATE TABLE rosca_table (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    creator_id TEXT NOT NULL,
    total_members INTEGER NOT NULL,
    current_members INTEGER NOT NULL,
    contribution_amount INTEGER NOT NULL,
    frequency_days INTEGER NOT NULL,
    current_round INTEGER NOT NULL,
    distribution_method TEXT NOT NULL,
    multisig_address TEXT,
    rosca_wallet_path TEXT,
    status TEXT NOT NULL,
    started_at INTEGER,
    completed_at INTEGER,
    created_at INTEGER NOT NULL
)
```

#### 2. member_table
```sql
CREATE TABLE member_table (
    id TEXT PRIMARY KEY,
    rosca_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    wallet_address TEXT NOT NULL,
    name TEXT,
    multisig_info_json TEXT, -- JSON containing MultisigInfo
    position INTEGER NOT NULL,
    joined_at INTEGER NOT NULL,
    is_active INTEGER NOT NULL,
    FOREIGN KEY(rosca_id) REFERENCES rosca_table(id)
)
```

#### 3. contribution_table
```sql
CREATE TABLE contribution_table (
    id TEXT PRIMARY KEY,
    rosca_id TEXT NOT NULL,
    round_number INTEGER NOT NULL,
    member_id TEXT NOT NULL,
    amount INTEGER NOT NULL,
    status TEXT NOT NULL,
    tx_hash TEXT,
    created_at INTEGER NOT NULL,
    FOREIGN KEY(rosca_id) REFERENCES rosca_table(id),
    FOREIGN KEY(member_id) REFERENCES member_table(id)
)
```

#### 4. round_table
```sql
CREATE TABLE round_table (
    id TEXT PRIMARY KEY,
    rosca_id TEXT NOT NULL,
    round_number INTEGER NOT NULL,
    recipient_id TEXT,
    collected_amount INTEGER NOT NULL,
    target_amount INTEGER NOT NULL,
    payout_amount INTEGER,
    payout_transaction_hash TEXT,
    status TEXT NOT NULL,
    started_at INTEGER,
    ended_at INTEGER,
    FOREIGN KEY(rosca_id) REFERENCES rosca_table(id),
    FOREIGN KEY(recipient_id) REFERENCES member_table(id)
)
```

### Transaction Flow

#### Create ROSCA Transaction:
```kotlin
repository.withTransaction {
    // 1. Insert ROSCA record
    insertRosca(rosca)
    
    // 2. Insert creator member record
    insertMember(creatorMember)
}
// Either both succeed or both fail
```

#### Join ROSCA Transaction:
```kotlin
repository.withTransaction {
    // 1. Insert new member record
    insertMember(newMember)
    
    // 2. Update ROSCA member count
    updateRosca(rosca.copy(
        currentMembers = currentMembers + 1
    ))
}
// Atomic operation
```

#### Finalize Setup Transaction:
```kotlin
repository.withTransaction {
    // 1. Update ROSCA with multisig address and status
    updateRosca(rosca.copy(
        multisigAddress = address,
        status = RoscaState.ACTIVE,
        startedAt = System.currentTimeMillis()
    ))
    
    // 2. Update all members to ready state
    members.forEach { member ->
        updateMember(member.copy(
            multisigInfo = multisigInfo.copy(isReady = true)
        ))
    }
}
// All updates are atomic
```

---

## Error Handling & Cleanup

### Wallet Cleanup on Failure

Both create and join flows include cleanup logic:

```kotlin
try {
    // Wallet creation
    val walletPath = createFreshRoscaWallet(...)
    
    // Operations that might fail
    switchToRoscaWallet(...)
    getMultisigInfo(...)
    
    // Database operations
    repository.withTransaction { ... }
    
} catch (e: Exception) {
    // Clean up wallet file on any failure
    if (walletPath != null) {
        cleanupWalletFile(walletPath)
    }
    
    return Result.failure(e)
}
```

The `cleanupWalletFile()` function:
```kotlin
private fun cleanupWalletFile(walletPath: String) {
    try {
        val walletFile = File(walletPath)
        if (walletFile.exists()) {
            walletFile.delete()
            // Also delete .keys file
            File("$walletPath.keys").delete()
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to cleanup wallet file", e)
    }
}
```

---

## Summary

### Create ROSCA Flow Summary:
1. **User Input** → Validate parameters
2. **Wallet Creation** → Generate fresh wallet with random seed
3. **Multisig Prep** → Get multisig exchange information
4. **Database** → Atomically save ROSCA + creator member
5. **UI Update** → Show success and return to dashboard

### Join ROSCA Flow Summary:
1. **Discovery** → Receive invite link
2. **Validation** → Load ROSCA details, check availability
3. **Wallet Creation** → Generate independent wallet
4. **Multisig Prep** → Get multisig exchange information
5. **Database** → Atomically add member + update count
6. **Auto-Finalize** → If all members joined, trigger multisig setup
7. **UI Update** → Show success or waiting state

### Key Design Principles:
- **Atomic Transactions**: Database operations use transactions for consistency
- **Independent Wallets**: Each member has their own fresh wallet
- **Fail-Safe**: Wallet cleanup on any error
- **Multi-Round Support**: Handles both 2-of-2 and N-of-M multisig schemes
- **Auto-Finalization**: Last member joining triggers setup automatically
- **State Management**: Clear state transitions (SETUP → ACTIVE)
