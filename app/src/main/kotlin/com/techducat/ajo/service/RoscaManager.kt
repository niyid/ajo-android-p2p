// RoscaManager.kt
package com.techducat.ajo.service

import android.content.Context
import android.util.Log
import com.techducat.ajo.dlt.DLTProvider
import com.techducat.ajo.model.*
import com.techducat.ajo.model.Rosca.RoscaState
import com.techducat.ajo.model.Rosca.DistributionMethod
import com.techducat.ajo.model.Contribution.ContributionStatus
import com.techducat.ajo.repository.RoscaRepository
import com.techducat.ajo.util.WalletSelectionManager
import com.techducat.ajo.util.WalletSelectionManager.WalletType
import com.techducat.ajo.util.WalletSelectionManager.SelectedWallet
import com.techducat.ajo.wallet.WalletSuite
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.techducat.ajo.model.Round.RoundStatus
import com.techducat.ajo.model.MultisigSignature

/**
 * RoscaManager - Core business logic for ROSCA lifecycle management
 * 
 * UPDATED: Each ROSCA now has a fresh, independent wallet (no seed derivation)
 * - Personal wallet: User's individual funds (NOT used for ROSCA operations)
 * - ROSCA wallet(s): Fresh, independent wallet per ROSCA per user
 * 
 * ROSCA funds are held in multisig addresses within each ROSCA wallet.
 * 
 * ✅ FIXED: Added allowIncompleteMultisig = true in finalizeSetup() method
 */
class RoscaManager(
    private val walletSuite: WalletSuite,
    internal val repository: RoscaRepository,
    private val dltProvider: DLTProvider,
    private val contributionHandler: ContributionHandler,
    private val distributionSelector: DistributionSelector,
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    private val TAG = "RoscaManager"
    
    init {
        contributionHandler.roscaManager = this
    }
    
    private val _roscaStates = MutableStateFlow<Map<String, RoscaStatus?>>(emptyMap())
    val roscaStates: StateFlow<Map<String, RoscaStatus?>> = _roscaStates.asStateFlow()
        
    data class RoscaStatus(
        val rosca: Rosca,
        val currentRound: Round?,
        val nextPayoutRecipient: Member?,
        val syncStatus: SyncStatus,
        val memberStatuses: Map<String, MemberStatus>
    )
    
    data class MemberStatus(
        val memberId: String,
        val hasContributed: Boolean,
        val hasSigned: Boolean,
        val isActive: Boolean
    )
    
    data class MultisigExchangeRound(
        val id: String,
        val roscaId: String,
        val roundNumber: Int,  // 1, 2, 3, etc.
        val memberId: String,
        val exchangeInfo: String,
        val timestamp: Long,
        val isComplete: Boolean = false
    )    

    enum class SyncStatus {
        SYNCED,
        SYNCING,
        ERROR
    }
    
   
    data class SyncReport(
        val updatedCount: Int,
        val skippedCount: Int,
        val failures: List<SyncFailure>
    ) {
        val hasFailures: Boolean get() = failures.isNotEmpty()
        val isSuccessful: Boolean get() = failures.isEmpty()
    }
    
    data class SyncFailure(
        val roscaId: String,
        val roscaName: String,
        val reason: String,
        val actionNeeded: String
    )
        
    // ============================================================================
    // WALLET MANAGEMENT (SEPARATE WALLETS PER ROSCA)
    // ============================================================================
    
    /**
     * ✅ REMOVED: No longer need getPersonalSeed()
     * Each ROSCA wallet is created fresh with its own seed
     */
    
    /**
     * Get the current wallet's address
     */
    private suspend fun getCurrentWalletAddress(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val address = suspendCoroutine<String> { continuation ->
                walletSuite.getAddress(object : WalletSuite.AddressCallback {
                    override fun onSuccess(address: String) {
                        continuation.resume(address)
                    }
                    
                    override fun onError(error: String) {
                        continuation.resumeWithException(Exception(error))
                    }
                })
            }
            Result.success(address)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting wallet address", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get multisig info from current wallet
     */
    private suspend fun getMultisigInfo(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val multisigInfo = suspendCoroutine<String> { continuation ->
                walletSuite.getMultisigInfo(object : WalletSuite.MultisigCallback {
                    override fun onSuccess(info: String, address: String) {
                        continuation.resume(info)
                    }
                    
                    override fun onError(error: String) {
                        continuation.resumeWithException(Exception(error))
                    }
                })
            }
            Result.success(multisigInfo)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting multisig info", e)
            Result.failure(e)
        }
    }
    
    /**
     * Ensure wallet is initialized and ready
     */
    private suspend fun ensureWalletReady(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            if (!walletSuite.isReady()) {
                return@withContext Result.failure(Exception("Wallet not initialized"))
            }
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking wallet readiness", e)
            Result.failure(e)
        }
    }
    
    // ============================================================================
    // CORE ROSCA MANAGEMENT METHODS (UPDATED FOR FRESH WALLET CREATION)
    // ============================================================================
    
    /**
     * ✅ FIXED: Create ROSCA with transaction support and validation
     * 
     * CHANGES:
     * 1. Validates multisig info before saving
     * 2. Uses database transaction for atomicity
     * 3. Cleans up wallet on failure
     * 4. Better error messages
     */
    suspend fun createRosca(
        name: String,
        description: String,
        totalMembers: Int,
        contributionAmount: Long,
        frequencyDays: Int,
        distributionMethod: DistributionMethod,
        context: Context
    ): Result<Rosca> = withContext(Dispatchers.IO) {
        var walletPath: String? = null
        
        try {
            Log.d(TAG, "=== CREATING ROSCA (FRESH WALLET WITH VALIDATION) ===")
            Log.d(TAG, "Name: $name with $totalMembers members")
            
            // ============================================================
            // INPUT VALIDATION PHASE
            // ============================================================
            
            if (name.isBlank()) {
                return@withContext Result.failure(Exception("ROSCA name cannot be empty"))
            }
            if (totalMembers < 2) {
                return@withContext Result.failure(Exception("ROSCA must have at least 2 members"))
            }
            if (contributionAmount <= 0) {
                return@withContext Result.failure(Exception("Contribution amount must be positive"))
            }
            if (frequencyDays <= 0) {
                return@withContext Result.failure(Exception("Frequency must be positive"))
            }

            val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getString("user_id", null)
            
            if (userId.isNullOrEmpty()) {
                Log.e(TAG, "User ID not found in SharedPreferences")
                return@withContext Result.failure(Exception("User not logged in. Please sign in first."))
            }
            
            Log.d(TAG, "Creator userId: $userId")
            
            // Generate ROSCA ID early
            val roscaId = "rosca_${System.currentTimeMillis()}_${name.hashCode().toString().replace("-", "n")}"
            Log.d(TAG, "Generated ROSCA ID: $roscaId")
            
            // ============================================================
            // WALLET CREATION PHASE
            // ============================================================
            
            Log.d(TAG, "Step 1: Creating fresh ROSCA wallet...")
            val roscaWalletResult = WalletSelectionManager.createFreshRoscaWallet(
                context = context,
                userId = userId,
                roscaId = roscaId,
                walletSuite = walletSuite
            )
            
            if (roscaWalletResult.isFailure) {
                return@withContext Result.failure(
                    roscaWalletResult.exceptionOrNull() ?: Exception("Failed to create ROSCA wallet")
                )
            }
            
            walletPath = roscaWalletResult.getOrThrow()
            Log.d(TAG, "✓ Fresh ROSCA wallet created: $walletPath")
            
            // ============================================================
            // WALLET VERIFICATION PHASE
            // ============================================================
            
            Log.d(TAG, "Step 2: Switching to ROSCA wallet...")
            val switchResult = WalletSelectionManager.switchToRoscaWallet(
                context = context,
                userId = userId,
                roscaId = roscaId,
                roscaName = name,
                multisigAddress = null,
                walletSuite = walletSuite,
                allowIncompleteMultisig = true
            )
            
            if (switchResult.isFailure) {
                cleanupWalletFile(walletPath)
                return@withContext Result.failure(
                    switchResult.exceptionOrNull() ?: Exception("Failed to switch to ROSCA wallet")
                )
            }
            Log.d(TAG, "✓ Switched to ROSCA wallet")
            
            Log.d(TAG, "Step 3: Getting ROSCA wallet address...")
            val roscaAddressResult = getCurrentWalletAddress()
            if (roscaAddressResult.isFailure) {
                cleanupWalletFile(walletPath)
                return@withContext Result.failure(
                    roscaAddressResult.exceptionOrNull() ?: Exception("Failed to get ROSCA wallet address")
                )
            }
            val roscaWalletAddress = roscaAddressResult.getOrThrow()
            Log.d(TAG, "✓ ROSCA wallet address: ${roscaWalletAddress.take(15)}...")
            
            // ============================================================
            // MULTISIG INFO GENERATION & VALIDATION
            // ============================================================
            
            Log.d(TAG, "Step 4: Getting multisig info from wallet...")
            val multisigInfoResult = getMultisigInfo()
            if (multisigInfoResult.isFailure) {
                cleanupWalletFile(walletPath)
                return@withContext Result.failure(
                    multisigInfoResult.exceptionOrNull() ?: Exception("Failed to get multisig info")
                )
            }
            val multisigInfo = multisigInfoResult.getOrThrow()
            Log.d(TAG, "✓ Got multisig info (length: ${multisigInfo.length} chars)")
            
            // ✅ CRITICAL VALIDATION: Ensure multisig info is valid
            if (multisigInfo.isBlank()) {
                cleanupWalletFile(walletPath)
                return@withContext Result.failure(
                    Exception("Multisig info is empty. Wallet may not be properly initialized.")
                )
            }
            
            if (multisigInfo.length < 100) {
                cleanupWalletFile(walletPath)
                return@withContext Result.failure(
                    Exception("Multisig info appears invalid (too short). Please try again.")
                )
            }
            
            // ============================================================
            // DATABASE TRANSACTION - ATOMIC OPERATIONS
            // ============================================================
            
            val threshold = totalMembers - 1
            Log.d(TAG, "Step 5: Creating ROSCA database records (atomic transaction)...")
            Log.d(TAG, "Multisig will be $threshold-of-$totalMembers")
            
            val rosca = Rosca(
                id = roscaId,
                name = name,
                description = description,
                creatorId = userId,
                totalMembers = totalMembers,
                currentMembers = 1,
                contributionAmount = contributionAmount,
                frequencyDays = frequencyDays,
                currentRound = 0,
                distributionMethod = distributionMethod,
                multisigAddress = null,
                roscaWalletPath = walletPath,
                status = RoscaState.SETUP,
                startedAt = null,
                completedAt = null,
                createdAt = System.currentTimeMillis()
            )

            val creatorMember = Member(
                id = UUID.randomUUID().toString(),
                roscaId = roscaId,
                userId = userId,
                walletAddress = roscaWalletAddress,
                name = "Creator",
                multisigInfo = Member.MultisigInfo(
                    address = roscaWalletAddress,
                    viewKey = "",
                    isReady = false,
                    exchangeState = multisigInfo
                ),
                position = 0,
                joinedAt = System.currentTimeMillis(),
                isActive = true
            )

            // ✅✅✅ USE TRANSACTION - Either both succeed or both fail
            try {
                repository.withTransaction {
                    Log.d(TAG, "  → Inserting ROSCA record...")
                    repository.insertRosca(rosca)
                    
                    Log.d(TAG, "  → Inserting creator member record...")
                    repository.insertMember(creatorMember)
                    
                    Log.d(TAG, "  ✓ Transaction committed")
                }
            } catch (e: Exception) {
                // Transaction failed - cleanup wallet
                Log.e(TAG, "✗ Database transaction failed", e)
                cleanupWalletFile(walletPath)
                return@withContext Result.failure(
                    Exception("Failed to create ROSCA: ${e.message}")
                )
            }
            
            // ============================================================
            // POST-CREATE OPERATIONS
            // ============================================================
            
            // Check if somehow all members joined immediately (edge case)
            checkAndTriggerFinalization(roscaId, userId)            
            
            Log.i(TAG, "✅✅✅ ROSCA CREATION COMPLETE ✅✅✅")
            Log.i(TAG, "ROSCA ID: $roscaId")
            Log.i(TAG, "Status: SETUP (waiting for members)")
            Log.i(TAG, "Members: 1/$totalMembers")
            Log.i(TAG, "Fresh wallet created with independent seed")
            Log.i(TAG, "Multisig info ready for exchange")
            Log.i(TAG, "Wallet: $walletPath")

            Result.success(rosca)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error creating ROSCA", e)
            
            // Cleanup wallet on any failure
            if (walletPath != null) {
                cleanupWalletFile(walletPath)
            }
            
            Result.failure(e)
        }
    }
    
    /**
     * Helper: Clean up wallet file on join failure
     */
    private fun cleanupWalletFile(walletPath: String?) {
        if (walletPath == null) return
        
        try {
            Log.d(TAG, "Cleaning up wallet file: $walletPath")
            
            val walletFile = File(walletPath)
            val keysFile = File("$walletPath.keys")
            val addressFile = File("$walletPath.address.txt")
            
            var deleted = 0
            if (walletFile.exists() && walletFile.delete()) deleted++
            if (keysFile.exists() && keysFile.delete()) deleted++
            if (addressFile.exists() && addressFile.delete()) deleted++
            
            Log.d(TAG, "✓ Cleaned up $deleted wallet file(s)")
            
        } catch (e: Exception) {
            Log.w(TAG, "Error cleaning up wallet file: ${e.message}")
        }
    }
        
    /**
     * Check if ROSCA is ready for finalization and trigger it automatically
     * Called after a member joins to see if we can now finalize
     * 
     * ✅ FIXED: Only triggers finalization when CREATOR is logged in
     */
    private suspend fun checkAndTriggerFinalization(
        roscaId: String,
        userId: String
    ) = withContext(Dispatchers.IO) {
        try {
            val rosca = repository.getRoscaById(roscaId) ?: return@withContext
            
            // ✅ NEW: Only proceed if current user is the creator
            if (rosca.creatorId != userId) {
                Log.d(TAG, "⏭️ Skipping finalization check - only creator can trigger finalization")
                Log.d(TAG, "  Current user: $userId")
                Log.d(TAG, "  Creator: ${rosca.creatorId}")
                return@withContext
            }
            
            Log.d(TAG, "✓ Current user is creator - checking finalization eligibility...")
            
            // Check if ROSCA is full
            if (rosca.currentMembers < rosca.totalMembers) {
                Log.d(TAG, "ROSCA not full yet: ${rosca.currentMembers}/${rosca.totalMembers}")
                return@withContext
            }
            
            // Check if already finalized
            if (rosca.status == RoscaState.ACTIVE && rosca.multisigAddress != null) {
                Log.d(TAG, "ROSCA already finalized")
                return@withContext
            }
            
            Log.i(TAG, "🎯 ROSCA is full! Checking if ready for finalization...")
            
            // Get all members with multisig info
            val membersWithInfo = getMembersWithMultisigInfo(roscaId)
            
            Log.d(TAG, "Members with multisig info: ${membersWithInfo.size}/${rosca.totalMembers}")
            
            // Check if all members have multisig info
            if (membersWithInfo.size < rosca.totalMembers) {
                Log.w(TAG, "Not all members have multisig info yet. Waiting...")
                Log.w(TAG, "Members still need to join/generate info: ${rosca.totalMembers - membersWithInfo.size}")
                return@withContext
            }
            
            Log.i(TAG, "✅ All members have multisig info! Triggering finalization...")
            
            // Collect all multisig exchange states as JSON objects with addresses
            val allMultisigInfos = membersWithInfo.map { member ->
                org.json.JSONObject().apply {
                    put("address", member.walletAddress)
                    put("exchangeState", member.multisigInfo?.exchangeState)
                    put("userId", member.userId)
                }.toString()
            }
            
            if (allMultisigInfos.size != rosca.totalMembers) {
                Log.e(TAG, "❌ Mismatch in multisig info count: ${allMultisigInfos.size} vs ${rosca.totalMembers}")
                return@withContext
            }
            
            Log.d(TAG, "Collected ${allMultisigInfos.size} multisig infos from database (with addresses)")

            
            // Trigger finalization
            val result = finalizeSetup(
                roscaId = roscaId,
                allMemberMultisigInfos = allMultisigInfos,
                userId = userId
            )
            
            if (result.isSuccess) {
                Log.i(TAG, "🎉🎉🎉 ROSCA FINALIZED SUCCESSFULLY! 🎉🎉🎉")
                Log.i(TAG, "Status: ACTIVE")
                Log.i(TAG, "Multisig wallet ready for contributions")
                
                // Update all members to mark them as ready
                val updatedMembers = membersWithInfo.map { member ->
                    member.copy(
                        multisigInfo = member.multisigInfo?.copy(isReady = true)
                    )
                }
                updatedMembers.forEach { repository.updateMember(it) }
                
            } else {
                Log.e(TAG, "❌ Finalization failed: ${result.exceptionOrNull()?.message}")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking/triggering finalization", e)
        }
    }
    
    suspend fun joinRosca(
        roscaId: String,
        setupInfo: String,
        context: Context
    ): Result<Member> = withContext(Dispatchers.IO) {
        var walletPath: String? = null
        
        try {
            Log.i(TAG, "=== JOINING ROSCA (FRESH WALLET) ===")
            Log.i(TAG, "ROSCA ID: $roscaId")
            
            val rosca = repository.getRoscaById(roscaId)
            if (rosca == null) {
                Log.e(TAG, "✗ ROSCA not found: $roscaId")
                return@withContext Result.failure(Exception("ROSCA not found"))
            }
            
            if (rosca.currentMembers >= rosca.totalMembers) {
                Log.e(TAG, "✗ ROSCA is full: ${rosca.currentMembers}/${rosca.totalMembers}")
                return@withContext Result.failure(Exception("ROSCA is full"))
            }
            
            val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getString("user_id", null)
            
            if (userId.isNullOrEmpty()) {
                Log.e(TAG, "✗ User not logged in")
                return@withContext Result.failure(Exception("User not logged in"))
            }
            
            Log.d(TAG, "Joining user ID: $userId")
            
            // Check if user already joined
            val existingMembers = repository.getMembersByRoscaId(roscaId)
            if (existingMembers.any { it.userId == userId }) {
                Log.e(TAG, "✗ User already joined this ROSCA")
                return@withContext Result.failure(Exception("You have already joined this ROSCA"))
            }
            
            // ============================================================
            // WALLET CREATION PHASE
            // ============================================================
            
            Log.d(TAG, "Step 1: Creating fresh ROSCA wallet...")
            val roscaWalletResult = WalletSelectionManager.createFreshRoscaWallet(
                context = context,
                userId = userId,
                roscaId = roscaId,
                walletSuite = walletSuite
            )
            
            if (roscaWalletResult.isFailure) {
                return@withContext Result.failure(
                    roscaWalletResult.exceptionOrNull() ?: Exception("Failed to create ROSCA wallet")
                )
            }
            
            walletPath = roscaWalletResult.getOrThrow()
            Log.d(TAG, "✓ Fresh ROSCA wallet created: $walletPath")
            
            // ============================================================
            // WALLET VERIFICATION PHASE
            // ============================================================
            
            Log.d(TAG, "Step 2: Switching to ROSCA wallet...")
            val switchResult = WalletSelectionManager.switchToRoscaWallet(
                context = context,
                userId = userId,
                roscaId = roscaId,
                roscaName = rosca.name,
                multisigAddress = rosca.multisigAddress,
                walletSuite = walletSuite,
                allowIncompleteMultisig = true
            )
            
            if (switchResult.isFailure) {
                cleanupWalletFile(walletPath)
                return@withContext Result.failure(
                    switchResult.exceptionOrNull() ?: Exception("Failed to switch to ROSCA wallet")
                )
            }
            Log.d(TAG, "✓ Switched to ROSCA wallet")
            
            Log.d(TAG, "Step 3: Getting wallet address...")
            val userWalletAddressResult = getCurrentWalletAddress()
            if (userWalletAddressResult.isFailure) {
                cleanupWalletFile(walletPath)
                return@withContext Result.failure(
                    userWalletAddressResult.exceptionOrNull() ?: Exception("Failed to get wallet address")
                )
            }
            val userWalletAddress = userWalletAddressResult.getOrThrow()
            Log.d(TAG, "✓ Joiner's ROSCA wallet address: ${userWalletAddress.take(15)}...")
            
            // Check if this wallet address already joined
            if (existingMembers.any { it.walletAddress == userWalletAddress }) {
                Log.e(TAG, "✗ This wallet has already joined this ROSCA")
                cleanupWalletFile(walletPath)
                return@withContext Result.failure(Exception("This wallet has already joined this ROSCA"))
            }
            
            // ============================================================
            // MULTISIG INFO GENERATION & VALIDATION
            // ============================================================
            
            Log.d(TAG, "Step 4: Getting multisig info from wallet...")
            val multisigInfoResult = getMultisigInfo()
            if (multisigInfoResult.isFailure) {
                cleanupWalletFile(walletPath)
                return@withContext Result.failure(
                    multisigInfoResult.exceptionOrNull() ?: Exception("Failed to get multisig info")
                )
            }
            val multisigInfo = multisigInfoResult.getOrThrow()
            Log.d(TAG, "✓ Got multisig info (length: ${multisigInfo.length} chars)")
            
            if (multisigInfo.isBlank()) {
                cleanupWalletFile(walletPath)
                return@withContext Result.failure(
                    Exception("Multisig info is empty. Wallet may not be properly initialized.")
                )
            }
            
            if (multisigInfo.length < 100) {
                cleanupWalletFile(walletPath)
                return@withContext Result.failure(
                    Exception("Multisig info appears invalid (too short). Please try again.")
                )
            }
            
            // ============================================================
            // DATABASE TRANSACTION - ATOMIC OPERATIONS
            // ============================================================
            
            val position = existingMembers.size
            
            Log.d(TAG, "Step 5: Creating member record (atomic transaction)...")
            
            val member = Member(
                id = UUID.randomUUID().toString(),
                roscaId = roscaId,
                userId = userId,
                walletAddress = userWalletAddress,
                name = "",
                multisigInfo = Member.MultisigInfo(
                    address = userWalletAddress,
                    viewKey = "",
                    isReady = false,
                    exchangeState = multisigInfo
                ),
                position = position,
                joinedAt = System.currentTimeMillis(),
                isActive = true
            )
            
            val updatedRosca = rosca.copy(
                currentMembers = rosca.currentMembers + 1
            )
            
            try {
                repository.withTransaction {
                    Log.d(TAG, "  → Inserting member record...")
                    repository.insertMember(member)
                    
                    Log.d(TAG, "  → Updating ROSCA member count...")
                    repository.updateRosca(updatedRosca)
                    
                    Log.d(TAG, "  ✓ Transaction committed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "✗ Database transaction failed", e)
                cleanupWalletFile(walletPath)
                return@withContext Result.failure(
                    Exception("Failed to join ROSCA: ${e.message}")
                )
            }
            
            // ============================================================
            // POST-JOIN OPERATIONS
            // ============================================================
            
            Log.i(TAG, "✓ Updated ROSCA members: ${updatedRosca.currentMembers}/${rosca.totalMembers}")

            // ✅ Check and auto-trigger finalization if ready
            checkAndTriggerFinalization(roscaId, userId)

            Log.i(TAG, "✅✅✅ JOINED ROSCA SUCCESSFULLY ✅✅✅")
            Log.i(TAG, "Member ID: ${member.id}")
            Log.i(TAG, "Position: $position")
            Log.i(TAG, "Fresh wallet created with independent seed")
            Log.i(TAG, "Multisig info ready for exchange")
            Log.i(TAG, "Wallet: $walletPath")
            
            Result.success(member)
            
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to join ROSCA", e)
            
            if (walletPath != null) {
                cleanupWalletFile(walletPath)
            }
            
            Result.failure(e)
        }
    }
    
    /**
     * ✅ IMPROVED: Sync member multisig info with detailed error reporting
     */
 
    suspend fun syncMemberMultisigInfo(userId: String): Result<SyncReport> = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "=== SYNCING MEMBER MULTISIG INFO (IMPROVED) ===")
            Log.d(TAG, "User ID: $userId")
            
            var updatedCount = 0
            var skippedCount = 0
            val failures = mutableListOf<SyncFailure>()
            
            val allMembers = repository.getAllMembers()
            val userMembers = allMembers.filter { it.userId == userId }
            
            Log.d(TAG, "Found ${userMembers.size} ROSCA memberships for user")
            
            for (member in userMembers) {
                try {
                    val currentMultisigInfo = member.multisigInfo
                    
                    if (currentMultisigInfo != null && 
                        !currentMultisigInfo.exchangeState.isNullOrBlank()) {
                        Log.d(TAG, "Member ${member.id} already has multisig info, skipping")
                        skippedCount++
                        continue
                    }
                    
                    val rosca = repository.getRoscaById(member.roscaId)
                    if (rosca == null) {
                        Log.w(TAG, "ROSCA ${member.roscaId} not found")
                        continue
                    }
                    
                    if (rosca.status != RoscaState.SETUP) {
                        Log.d(TAG, "ROSCA ${rosca.name} not in SETUP state, skipping")
                        skippedCount++
                        continue
                    }
                    
                    Log.d(TAG, "Attempting to sync multisig info for ROSCA: ${rosca.name}")
                    
                    val walletPath = WalletSelectionManager.getRoscaWalletPath(context, userId, rosca.id)
                    if (!WalletSelectionManager.walletExists(walletPath)) {
                        Log.w(TAG, "❌ Wallet does not exist: $walletPath")
                        failures.add(SyncFailure(
                            roscaId = rosca.id,
                            roscaName = rosca.name,
                            reason = "Wallet file not found",
                            actionNeeded = "Complete ROSCA setup in ROSCA details"
                        ))
                        continue
                    }
                    
                    val switchResult = WalletSelectionManager.switchToRoscaWallet(
                        context = context,
                        userId = userId,
                        roscaId = rosca.id,
                        roscaName = rosca.name,
                        multisigAddress = rosca.multisigAddress,
                        walletSuite = walletSuite
                    )
                    
                    if (switchResult.isFailure) {
                        val error = switchResult.exceptionOrNull()?.message ?: "Unknown error"
                        Log.w(TAG, "❌ Failed to switch wallet: $error")
                        failures.add(SyncFailure(
                            roscaId = rosca.id,
                            roscaName = rosca.name,
                            reason = "Cannot access wallet: $error",
                            actionNeeded = "Retry setup or contact support"
                        ))
                        continue
                    }
                    
                    if (!walletSuite.isReady()) {
                        Log.w(TAG, "❌ Wallet not ready for ${rosca.name}")
                        failures.add(SyncFailure(
                            roscaId = rosca.id,
                            roscaName = rosca.name,
                            reason = "Wallet not initialized",
                            actionNeeded = "Wallet may be corrupted - retry setup"
                        ))
                        continue
                    }
                    
                    val multisigInfoResult = getMultisigInfo()
                    if (multisigInfoResult.isFailure) {
                        val error = multisigInfoResult.exceptionOrNull()?.message ?: "Unknown error"
                        Log.w(TAG, "❌ Failed to get multisig info: $error")
                        failures.add(SyncFailure(
                            roscaId = rosca.id,
                            roscaName = rosca.name,
                            reason = "Cannot extract multisig info: $error",
                            actionNeeded = "Retry setup - wallet may need reinitialization"
                        ))
                        continue
                    }
                    
                    val multisigInfo = multisigInfoResult.getOrThrow()
                    
                    if (multisigInfo.isBlank()) {
                        Log.w(TAG, "❌ Multisig info is empty for ${rosca.name}")
                        failures.add(SyncFailure(
                            roscaId = rosca.id,
                            roscaName = rosca.name,
                            reason = "Multisig info is empty",
                            actionNeeded = "Complete multisig setup in wallet"
                        ))
                        continue
                    }
                    
                    if (multisigInfo.length < 100) {
                        Log.w(TAG, "❌ Multisig info too short (${multisigInfo.length} chars) for ${rosca.name}")
                        failures.add(SyncFailure(
                            roscaId = rosca.id,
                            roscaName = rosca.name,
                            reason = "Invalid multisig info (too short)",
                            actionNeeded = "Retry wallet setup"
                        ))
                        continue
                    }
                    
                    val addressResult = getCurrentWalletAddress()
                    if (addressResult.isFailure) {
                        val error = addressResult.exceptionOrNull()?.message ?: "Unknown error"
                        Log.w(TAG, "❌ Failed to get wallet address: $error")
                        failures.add(SyncFailure(
                            roscaId = rosca.id,
                            roscaName = rosca.name,
                            reason = "Cannot get wallet address: $error",
                            actionNeeded = "Check wallet integrity"
                        ))
                        continue
                    }
                    val walletAddress = addressResult.getOrThrow()
                    
                    Log.d(TAG, "✓ Validation passed - updating member record...")
                    
                    val updatedMember = member.copy(
                        walletAddress = walletAddress,
                        multisigInfo = Member.MultisigInfo(
                            address = walletAddress,
                            viewKey = "",
                            isReady = false,
                            exchangeState = multisigInfo
                        )
                    )
                    
                    try {
                        repository.withTransaction {
                            repository.updateMember(updatedMember)
                        }
                        
                        updatedCount++
                        Log.i(TAG, "✅ Updated multisig info for member in ${rosca.name}")
                        
                        checkAndTriggerFinalization(rosca.id, userId)
                        
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Failed to update member in database", e)
                        failures.add(SyncFailure(
                            roscaId = rosca.id,
                            roscaName = rosca.name,
                            reason = "Database update failed: ${e.message}",
                            actionNeeded = "Retry sync"
                        ))
                    }
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error syncing member ${member.id}", e)
                    val rosca = repository.getRoscaById(member.roscaId)
                    failures.add(SyncFailure(
                        roscaId = member.roscaId,
                        roscaName = rosca?.name ?: "Unknown ROSCA",
                        reason = "Unexpected error: ${e.message}",
                        actionNeeded = "Contact support"
                    ))
                }
            }
            
            val report = SyncReport(
                updatedCount = updatedCount,
                skippedCount = skippedCount,
                failures = failures
            )
            
            Log.i(TAG, "=== SYNC COMPLETE ===")
            Log.i(TAG, "✓ Updated: $updatedCount")
            Log.i(TAG, "⏭️ Skipped: $skippedCount (already have info)")
            Log.i(TAG, "❌ Failed: ${failures.size}")
            
            if (failures.isNotEmpty()) {
                Log.w(TAG, "Failures:")
                failures.forEach { failure ->
                    Log.w(TAG, "  - ${failure.roscaName}: ${failure.reason}")
                    Log.w(TAG, "    Action: ${failure.actionNeeded}")
                }
            }
            
            Result.success(report)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during multisig sync", e)
            Result.failure(e)
        }
    }

    private suspend fun getAllMembers(): List<Member> {
        return try {
            repository.getAllMembers()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all members", e)
            emptyList()
        }
    }    
    
    // ============================================================================
    // CONTRIBUTION METHODS
    // ============================================================================
    
    suspend fun contributeToRosca(
        roscaId: String,
        roundNumber: Int,
        context: Context
    ): Result<Contribution> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "=== CONTRIBUTING TO ROSCA ===")
            Log.d(TAG, "ROSCA: $roscaId, Round: $roundNumber")
            
            val rosca = repository.getRoscaById(roscaId)
                ?: return@withContext Result.failure(Exception("ROSCA not found"))
            
            val round = repository.getRoundByNumber(roscaId, roundNumber)
                ?: return@withContext Result.failure(Exception("Round not found"))
            
            if (round.status != Round.RoundStatus.CONTRIBUTION) {
                return@withContext Result.failure(Exception("Round is not in contribution phase"))
            }
            
            val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getString("user_id", null)
            
            if (userId.isNullOrEmpty()) {
                return@withContext Result.failure(Exception("User not logged in"))
            }
            
            val members = repository.getMembersByRoscaId(roscaId)
            val member = members.find { it.userId == userId }
                ?: return@withContext Result.failure(Exception("You are not a member of this ROSCA"))
            
            val switchResult = WalletSelectionManager.switchToRoscaWallet(
                context = context,
                userId = userId,
                roscaId = roscaId,
                roscaName = rosca.name,
                multisigAddress = rosca.multisigAddress,
                walletSuite = walletSuite
            )
            
            if (switchResult.isFailure) {
                return@withContext Result.failure(
                    switchResult.exceptionOrNull() ?: Exception("Failed to switch to ROSCA wallet")
                )
            }
            
            Log.d(TAG, "Checking ROSCA wallet balance...")
            val (balance, unlocked) = suspendCoroutine<Pair<Long, Long>> { continuation ->
                walletSuite.getBalance(object : WalletSuite.BalanceCallback {
                    override fun onSuccess(balance: Long, unlocked: Long) {
                        continuation.resume(Pair(balance, unlocked))
                    }
                    
                    override fun onError(error: String) {
                        continuation.resumeWithException(Exception(error))
                    }
                })
            }
            
            if (rosca.contributionAmount > unlocked) {
                return@withContext Result.failure(Exception(
                    "Insufficient balance in ROSCA wallet. " +
                    "Required: ${formatAtomicToXmr(rosca.contributionAmount)} XMR, " +
                    "Available: ${formatAtomicToXmr(unlocked)} XMR"
                ))
            }
            
            val existingContribution = repository.getContribution(roscaId, roundNumber, member.id)
            if (existingContribution != null) {
                return@withContext Result.failure(Exception("You have already contributed to this round"))
            }
            
            val contribution = Contribution(
                id = UUID.randomUUID().toString(),
                roscaId = roscaId,
                roundNumber = roundNumber,
                memberId = member.id,
                amount = rosca.contributionAmount,
                status = ContributionStatus.PENDING,
                txHash = null,
                createdAt = System.currentTimeMillis()
            )
            
            repository.insertContribution(contribution)
            
            WalletSelectionManager.selectRoscaWallet(
                roscaId = roscaId,
                roscaName = rosca.name,
                multisigAddress = rosca.multisigAddress
            )
            
            Log.d(TAG, "Sending contribution to multisig address...")
            val txHash = suspendCoroutine<String> { continuation ->
                walletSuite.sendTransaction(
                    rosca.multisigAddress ?: "",
                    rosca.contributionAmount / 1e12,
                    object : WalletSuite.TransactionCallback {
                        override fun onSuccess(txId: String, amount: Long) {
                            Log.d(TAG, "✓ Contribution sent: $txId")
                            continuation.resume(txId)
                        }
                        
                        override fun onError(error: String) {
                            continuation.resumeWithException(Exception(error))
                        }
                    }
                )
            }
            
            val updatedContribution = contribution.copy(
                status = ContributionStatus.CONFIRMED,
                txHash = txHash
            )
            repository.updateContribution(updatedContribution)
            
            val updatedRound = round.copy(
                collectedAmount = round.collectedAmount + rosca.contributionAmount
            )
            repository.updateRound(updatedRound)
            
            Log.d(TAG, "✓ Contribution successful: $txHash")
            Log.d(TAG, "Remaining on ROSCA wallet for further operations")
            Result.success(updatedContribution)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error contributing to round", e)
            Result.failure(e)
        }
    }
    
    // ============================================================================
    // PAYOUT AND MULTISIG MANAGEMENT METHODS
    // ============================================================================
    
    /**
     * ✅ COMPLETE REWRITE: Finalize ROSCA setup with multi-round key exchange support
     * 
     * This method handles both:
     * 1. Single-round multisig (e.g., 2-of-2) - completes immediately
     * 2. Multi-round multisig (e.g., 2-of-3) - requires additional key exchange rounds
     * 
     * For multi-round:
     * - Round 1: makeMultisig() returns exchange info (not final address)
     * - Round 2+: exchangeMultisigKeys() with others' exchange info
     * - Continues until wallet.isMultisig() returns true
     */
    suspend fun finalizeSetup(
        roscaId: String,
        allMemberMultisigInfos: List<String>,
        userId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "╔══════════════════════════════════════════════════════════════╗")
            Log.i(TAG, "║           FINALIZING ROSCA SETUP (MULTI-ROUND)               ║")
            Log.i(TAG, "╚══════════════════════════════════════════════════════════════╝")
            Log.i(TAG, "ROSCA ID: $roscaId")
            Log.i(TAG, "User ID: $userId")
            Log.i(TAG, "Received ${allMemberMultisigInfos.size} multisig info entries")
            
            // ════════════════════════════════════════════════════════════
            // STEP 1: VALIDATE ROSCA STATE
            // ════════════════════════════════════════════════════════════
            
            val rosca = repository.getRoscaById(roscaId)
                ?: return@withContext Result.failure(Exception("ROSCA not found"))
            
            if (rosca.status == RoscaState.ACTIVE) {
                Log.w(TAG, "⚠️ ROSCA already finalized - nothing to do")
                return@withContext Result.success(Unit)
            }
            
            Log.d(TAG, "ROSCA State:")
            Log.d(TAG, "  Name: ${rosca.name}")
            Log.d(TAG, "  Status: ${rosca.status}")
            Log.d(TAG, "  Total Members: ${rosca.totalMembers}")
            Log.d(TAG, "  Current Members: ${rosca.currentMembers}")
            
            // ════════════════════════════════════════════════════════════
            // STEP 2: VALIDATE MEMBER COUNT
            // ════════════════════════════════════════════════════════════
            
            if (rosca.currentMembers < rosca.totalMembers) {
                val error = "Not all members have joined yet: ${rosca.currentMembers}/${rosca.totalMembers}"
                Log.e(TAG, "❌ $error")
                return@withContext Result.failure(Exception(error))
            }
            
            // Validate we received info for ALL members (including ourselves)
            if (allMemberMultisigInfos.size != rosca.totalMembers) {
                val error = "Invalid multisig info count: received ${allMemberMultisigInfos.size}, expected ${rosca.totalMembers}"
                Log.e(TAG, "❌ $error")
                return@withContext Result.failure(Exception(error))
            }
            
            Log.d(TAG, "✓ Member count validation passed")
            
            // ════════════════════════════════════════════════════════════
            // STEP 3: VALIDATE MULTISIG INFO FORMAT
            // ════════════════════════════════════════════════════════════
            
            Log.d(TAG, "Validating multisig info format...")
            
            for (i in allMemberMultisigInfos.indices) {
                val info = allMemberMultisigInfos[i]
                
                if (info.isNullOrBlank()) {
                    Log.e(TAG, "❌ Entry $i is null or empty")
                    return@withContext Result.failure(Exception("Multisig info entry $i is empty"))
                }
                
                // Validate JSON structure
                try {
                    val json = org.json.JSONObject(info)
                    
                    // Check required fields
                    if (!json.has("address") || !json.has("exchangeState") || !json.has("userId")) {
                        Log.e(TAG, "❌ Entry $i missing required fields")
                        return@withContext Result.failure(Exception("Multisig info entry $i has invalid structure"))
                    }
                    
                    val exchangeState = json.optString("exchangeState", "")
                    if (exchangeState.isBlank()) {
                        Log.e(TAG, "❌ Entry $i has empty exchangeState")
                        return@withContext Result.failure(Exception("Multisig info entry $i has empty exchangeState"))
                    }
                    
                    if (exchangeState.length < 100) {
                        Log.e(TAG, "❌ Entry $i exchangeState too short (${exchangeState.length} chars)")
                        return@withContext Result.failure(Exception("Multisig info entry $i has invalid exchangeState"))
                    }
                    
                    Log.d(TAG, "  Entry $i: address=${json.optString("address").take(15)}..., exchangeState length=${exchangeState.length}")
                    
                } catch (e: org.json.JSONException) {
                    Log.e(TAG, "❌ Entry $i is not valid JSON: ${e.message}")
                    return@withContext Result.failure(Exception("Multisig info entry $i is not valid JSON"))
                }
            }
            
            Log.d(TAG, "✓ Format validation passed - all entries are valid JSON with required fields")
            
            // ════════════════════════════════════════════════════════════
            // STEP 4: SWITCH TO ROSCA WALLET
            // ════════════════════════════════════════════════════════════
            
            Log.d(TAG, "Switching to ROSCA wallet...")
            
            val switchResult = WalletSelectionManager.switchToRoscaWallet(
                context = context,
                userId = userId,
                roscaId = roscaId,
                roscaName = rosca.name,
                multisigAddress = rosca.multisigAddress,
                walletSuite = walletSuite,
                allowIncompleteMultisig = true  // Allow switching before multisig is finalized
            )
            
            if (switchResult.isFailure) {
                val error = switchResult.exceptionOrNull()?.message ?: "Unknown error"
                Log.e(TAG, "❌ Failed to switch to ROSCA wallet: $error")
                return@withContext Result.failure(
                    Exception("Cannot access ROSCA wallet: $error")
                )
            }
            
            Log.d(TAG, "✓ Switched to ROSCA wallet")
            
            // ════════════════════════════════════════════════════════════
            // STEP 5: CALL makeMultisig (ROUND 1)
            // ════════════════════════════════════════════════════════════
            
            val threshold = rosca.totalMembers - 1
            
            Log.i(TAG, "")
            Log.i(TAG, "═══ CALLING makeMultisig (ROUND 1) ═══")
            Log.i(TAG, "Calling WalletSuite.finalizeRoscaSetup()...")
            Log.d(TAG, "  Multisig scheme: $threshold-of-${rosca.totalMembers}")
            Log.d(TAG, "  Total info entries: ${allMemberMultisigInfos.size}")
            Log.d(TAG, "  WalletSuite will filter out our own entry by address")
            
            val (resultString, isReady) = try {
                suspendCoroutine<Pair<String, Boolean>> { continuation ->
                    walletSuite.finalizeRoscaSetup(
                        roscaId,
                        allMemberMultisigInfos,
                        threshold,
                        object : WalletSuite.RoscaFinalizeCallback {
                            override fun onSuccess(
                                roscaId: String, 
                                addressOrExchangeInfo: String,  // Can be final address OR exchange info for round 2
                                isReady: Boolean
                            ) {
                                Log.d(TAG, "✓ WalletSuite.finalizeRoscaSetup() succeeded")
                                Log.d(TAG, "  Result length: ${addressOrExchangeInfo.length}")
                                Log.d(TAG, "  isReady: $isReady")
                                continuation.resume(Pair(addressOrExchangeInfo, isReady))
                            }
                            
                            override fun onError(error: String) {
                                Log.e(TAG, "❌ WalletSuite.finalizeRoscaSetup() failed: $error")
                                continuation.resumeWithException(Exception(error))
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Multisig setup failed", e)
                return@withContext Result.failure(
                    Exception("Multisig setup failed: ${e.message}")
                )
            }
            
            // ════════════════════════════════════════════════════════════
            // STEP 6: HANDLE RESULT - SINGLE VS MULTI-ROUND
            // ════════════════════════════════════════════════════════════
            
            if (!isReady) {
                // ════════════════════════════════════════════════════════
                // MULTI-ROUND PATH: Additional key exchange needed
                // ════════════════════════════════════════════════════════
                
                Log.i(TAG, "")
                Log.i(TAG, "═══ MULTI-ROUND EXCHANGE REQUIRED ═══")
                Log.d(TAG, "Round 1 complete, but wallet not ready yet")
                Log.d(TAG, "Got exchange info for round 2 (length: ${resultString.length})")
                Log.d(TAG, "This is expected for $threshold-of-${rosca.totalMembers} multisig")
                
                // Store our round 2 exchange info in the database
                val members = repository.getMembersByRoscaId(roscaId)
                val ourMember = members.find { it.userId == userId }
                    ?: return@withContext Result.failure(Exception("Member not found for user $userId"))
                
                Log.d(TAG, "Storing our round 2 exchange info in database...")
                
                val updatedMember = ourMember.copy(
                    multisigInfo = ourMember.multisigInfo?.copy(
                        exchangeState = resultString,  // Store round 2 exchange info
                        isReady = false
                    )
                )
                
                try {
                    repository.withTransaction {
                        repository.updateMember(updatedMember)
                        Log.d(TAG, "✓ Stored round 2 exchange info in database")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to store exchange info", e)
                    return@withContext Result.failure(
                        Exception("Failed to store exchange info: ${e.message}")
                    )
                }
                
                Log.i(TAG, "")
                Log.i(TAG, "⚠️ WAITING FOR OTHER MEMBERS TO UPLOAD ROUND 2 INFO")
                Log.i(TAG, "Current status: 1/${rosca.totalMembers} members ready for round 2")
                Log.i(TAG, "Once all members upload their round 2 info, key exchange will auto-trigger")
                
                // Schedule periodic check for when all members have round 2 info
                scheduleKeyExchangeCheck(roscaId, userId)
                
                Log.i(TAG, "")
                Log.i(TAG, "╔══════════════════════════════════════════════════════════════╗")
                Log.i(TAG, "║        ✓ ROUND 1 COMPLETE - AWAITING ROUND 2 INFO           ║")
                Log.i(TAG, "╚══════════════════════════════════════════════════════════════╝")
                
                return@withContext Result.success(Unit)
            }
            
            // ════════════════════════════════════════════════════════════
            // SINGLE-ROUND PATH: Multisig is ready immediately
            // ════════════════════════════════════════════════════════════
            
            val multisigAddress = resultString
            
            if (multisigAddress.isBlank()) {
                Log.e(TAG, "❌ Multisig address is empty")
                return@withContext Result.failure(Exception("Multisig address is empty"))
            }
            
            if (multisigAddress.length < 95) {  // Monero addresses are 95+ chars
                Log.e(TAG, "❌ Multisig address appears invalid (too short): $multisigAddress")
                return@withContext Result.failure(Exception("Invalid multisig address"))
            }
            
            Log.i(TAG, "")
            Log.i(TAG, "✓ Multisig wallet finalized successfully (single-round)")
            Log.d(TAG, "  Multisig Address: ${multisigAddress.take(20)}...${multisigAddress.takeLast(10)}")
            Log.d(TAG, "  Address Length: ${multisigAddress.length}")
            
            // ════════════════════════════════════════════════════════════
            // STEP 7: UPDATE DATABASE (ATOMIC TRANSACTION)
            // ════════════════════════════════════════════════════════════
            
            Log.i(TAG, "")
            Log.d(TAG, "Updating database with atomic transaction...")
            
            val updatedRosca = rosca.copy(
                multisigAddress = multisigAddress,
                status = RoscaState.ACTIVE,
                startedAt = System.currentTimeMillis()
            )
            
            val members = repository.getMembersByRoscaId(roscaId)
            
            if (members.size != rosca.totalMembers) {
                Log.e(TAG, "❌ Member count mismatch: DB has ${members.size}, expected ${rosca.totalMembers}")
                return@withContext Result.failure(
                    Exception("Member count mismatch in database")
                )
            }
            
            val updatedMembers = members.map { member ->
                member.copy(
                    multisigInfo = member.multisigInfo?.copy(isReady = true)
                )
            }
            
            try {
                repository.withTransaction {
                    Log.d(TAG, "  → Updating ROSCA record...")
                    repository.updateRosca(updatedRosca)
                    
                    Log.d(TAG, "  → Updating ${updatedMembers.size} member records...")
                    updatedMembers.forEach { member ->
                        repository.updateMember(member)
                    }
                    
                    Log.d(TAG, "  ✓ Transaction committed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Database transaction failed", e)
                return@withContext Result.failure(
                    Exception("Failed to save finalization to database: ${e.message}")
                )
            }
            
            // ════════════════════════════════════════════════════════════
            // STEP 8: SUCCESS
            // ════════════════════════════════════════════════════════════
            
            Log.i(TAG, "")
            Log.i(TAG, "╔══════════════════════════════════════════════════════════════╗")
            Log.i(TAG, "║              ✅ ROSCA FINALIZATION COMPLETE ✅               ║")
            Log.i(TAG, "╚══════════════════════════════════════════════════════════════╝")
            Log.i(TAG, "ROSCA: ${rosca.name}")
            Log.i(TAG, "Status: ${updatedRosca.status}")
            Log.i(TAG, "Multisig Address: ${multisigAddress.take(15)}...")
            Log.i(TAG, "Members: ${members.size}/${rosca.totalMembers} (all ready)")
            Log.i(TAG, "Threshold: $threshold-of-${rosca.totalMembers}")
            Log.i(TAG, "Started At: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(updatedRosca.startedAt)}")
            Log.i(TAG, "")
            
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e(TAG, "")
            Log.e(TAG, "╔══════════════════════════════════════════════════════════════╗")
            Log.e(TAG, "║            ❌ FINALIZATION FAILED ❌                         ║")
            Log.e(TAG, "╚══════════════════════════════════════════════════════════════╝")
            Log.e(TAG, "Error: ${e.message}")
            Log.e(TAG, "Exception type: ${e.javaClass.simpleName}")
            e.printStackTrace()
            Log.e(TAG, "")
            
            Result.failure(Exception("Finalization failed: ${e.message}"))
        }
    }
    
    /**
     * ✅ NEW METHOD: Perform round 2+ key exchange
     * 
     * This method is called after all members have uploaded their round 2 exchange info.
     * It collects the exchange info from all members and calls exchangeMultisigKeys().
     * 
     * @param roscaId The ROSCA identifier
     * @param userId The current user's ID
     * @return Result indicating success or failure
     */
    suspend fun performKeyExchange(
        roscaId: String,
        userId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "╔══════════════════════════════════════════════════════════════╗")
            Log.i(TAG, "║           PERFORMING KEY EXCHANGE ROUND 2+                   ║")
            Log.i(TAG, "╚══════════════════════════════════════════════════════════════╝")
            Log.i(TAG, "ROSCA ID: $roscaId")
            Log.i(TAG, "User ID: $userId")
            
            val rosca = repository.getRoscaById(roscaId)
                ?: return@withContext Result.failure(Exception("ROSCA not found"))
            
            if (rosca.status == RoscaState.ACTIVE) {
                Log.w(TAG, "⚠️ ROSCA already active - skipping key exchange")
                return@withContext Result.success(Unit)
            }
            
            // ════════════════════════════════════════════════════════════
            // STEP 1: SWITCH TO ROSCA WALLET
            // ════════════════════════════════════════════════════════════
            
            Log.d(TAG, "Switching to ROSCA wallet...")
            
            val switchResult = WalletSelectionManager.switchToRoscaWallet(
                context = context,
                userId = userId,
                roscaId = roscaId,
                roscaName = rosca.name,
                multisigAddress = rosca.multisigAddress,
                walletSuite = walletSuite,
                allowIncompleteMultisig = true
            )
            
            if (switchResult.isFailure) {
                return@withContext Result.failure(
                    switchResult.exceptionOrNull() ?: Exception("Failed to switch wallet")
                )
            }
            
            Log.d(TAG, "✓ Switched to ROSCA wallet")
            
            // ════════════════════════════════════════════════════════════
            // STEP 2: COLLECT ALL MEMBERS' EXCHANGE INFO
            // ════════════════════════════════════════════════════════════
            
            Log.d(TAG, "Collecting exchange info from all members...")
            
            val members = repository.getMembersByRoscaId(roscaId)
            
            val allExchangeInfos = members.mapNotNull { member ->
                member.multisigInfo?.exchangeState?.let { exchangeState ->
                    org.json.JSONObject().apply {
                        put("address", member.walletAddress)
                        put("exchangeState", exchangeState)
                        put("userId", member.userId)
                    }.toString()
                }
            }
            
            if (allExchangeInfos.size != rosca.totalMembers) {
                Log.w(TAG, "Not all members have exchange info yet: ${allExchangeInfos.size}/${rosca.totalMembers}")
                
                // Log which members are missing
                members.forEach { member ->
                    val memberMultisigInfo = member.multisigInfo
                    val hasInfo = memberMultisigInfo?.exchangeState != null && 
                                  memberMultisigInfo.exchangeState.isNotEmpty()
                    val status = if (hasInfo) "✓" else "✗"
                    Log.d(TAG, "  $status Member ${member.userId.takeLast(8)}: ${if (hasInfo) "has info" else "MISSING INFO"}")
                }
                
                return@withContext Result.failure(
                    Exception("Waiting for all members to provide exchange info (${allExchangeInfos.size}/${rosca.totalMembers})")
                )
            }
            
            Log.d(TAG, "✓ All ${allExchangeInfos.size} members have exchange info")
            
            // ════════════════════════════════════════════════════════════
            // STEP 3: PERFORM KEY EXCHANGE
            // ════════════════════════════════════════════════════════════
            
            Log.i(TAG, "")
            Log.i(TAG, "═══ CALLING exchangeMultisigKeys ═══")
            
            val (multisigAddress, isReady) = suspendCoroutine<Pair<String, Boolean>> { continuation ->
                walletSuite.exchangeMultisigKeys(
                    roscaId,
                    allExchangeInfos,
                    object : WalletSuite.MultisigExchangeCallback {
                        override fun onComplete(multisigAddress: String, isReady: Boolean) {
                            Log.d(TAG, "✓ Exchange complete!")
                            Log.d(TAG, "  Multisig address: ${multisigAddress.take(15)}...")
                            Log.d(TAG, "  Is ready: $isReady")
                            continuation.resume(Pair(multisigAddress, true))
                        }
                        
                        override fun onExchangeInfoReady(exchangeInfo: String, isComplete: Boolean) {
                            Log.w(TAG, "⚠️ Another round needed")
                            Log.w(TAG, "  Exchange info length: ${exchangeInfo.length}")
                            Log.w(TAG, "  Additional rounds beyond round 2 not yet implemented")
                            continuation.resumeWithException(
                                Exception("Additional rounds beyond round 2 not yet implemented. Got exchange info of ${exchangeInfo.length} chars.")
                            )
                        }
                        
                        override fun onError(error: String) {
                            Log.e(TAG, "❌ Exchange failed: $error")
                            continuation.resumeWithException(Exception(error))
                        }
                    }
                )
            }
            
            if (!isReady) {
                return@withContext Result.failure(
                    Exception("Exchange completed but wallet not ready")
                )
            }
            
            // ════════════════════════════════════════════════════════════
            // STEP 4: VALIDATE MULTISIG ADDRESS
            // ════════════════════════════════════════════════════════════
            
            if (multisigAddress.isBlank()) {
                Log.e(TAG, "❌ Multisig address is empty")
                return@withContext Result.failure(Exception("Multisig address is empty"))
            }
            
            if (multisigAddress.length < 95) {
                Log.e(TAG, "❌ Multisig address too short: ${multisigAddress.length} chars")
                return@withContext Result.failure(Exception("Invalid multisig address"))
            }
            
            Log.d(TAG, "✓ Multisig address validated")
            
            // ════════════════════════════════════════════════════════════
            // STEP 5: UPDATE DATABASE WITH FINAL MULTISIG ADDRESS
            // ════════════════════════════════════════════════════════════
            
            Log.i(TAG, "")
            Log.d(TAG, "Updating database with final multisig address...")
            
            val updatedRosca = rosca.copy(
                multisigAddress = multisigAddress,
                status = RoscaState.ACTIVE,
                startedAt = System.currentTimeMillis()
            )
            
            val updatedMembers = members.map { member ->
                member.copy(
                    multisigInfo = member.multisigInfo?.copy(isReady = true)
                )
            }
            
            try {
                repository.withTransaction {
                    Log.d(TAG, "  → Updating ROSCA record...")
                    repository.updateRosca(updatedRosca)
                    
                    Log.d(TAG, "  → Updating ${updatedMembers.size} member records...")
                    updatedMembers.forEach { member ->
                        repository.updateMember(member)
                    }
                    
                    Log.d(TAG, "  ✓ Transaction committed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update database", e)
                return@withContext Result.failure(
                    Exception("Failed to save to database: ${e.message}")
                )
            }
            
            // ════════════════════════════════════════════════════════════
            // STEP 6: SUCCESS
            // ════════════════════════════════════════════════════════════
            
            Log.i(TAG, "")
            Log.i(TAG, "╔══════════════════════════════════════════════════════════════╗")
            Log.i(TAG, "║         ✅ KEY EXCHANGE COMPLETE - ROSCA ACTIVE ✅           ║")
            Log.i(TAG, "╚══════════════════════════════════════════════════════════════╝")
            Log.i(TAG, "ROSCA: ${rosca.name}")
            Log.i(TAG, "Status: ACTIVE")
            Log.i(TAG, "Multisig Address: ${multisigAddress.take(15)}...")
            Log.i(TAG, "Members: ${members.size}/${rosca.totalMembers} (all ready)")
            Log.i(TAG, "")
            
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e(TAG, "Key exchange failed", e)
            Result.failure(Exception("Key exchange failed: ${e.message}"))
        }
    }
    
    /**
     * ✅ NEW METHOD: Schedule periodic checks for when all members have uploaded round 2 info
     * 
     * This runs in the background and automatically triggers performKeyExchange() when ready.
     * Only the creator performs the final exchange to avoid duplicate attempts.
     * 
     * @param roscaId The ROSCA identifier
     * @param userId The current user's ID
     */
    private fun scheduleKeyExchangeCheck(roscaId: String, userId: String) {
        scope.launch {
            var attempts = 0
            val maxAttempts = 60  // Check for 5 minutes (every 5 seconds)
            
            Log.d(TAG, "📡 Starting periodic exchange check (every 5 seconds, max ${maxAttempts * 5}s)")
            
            while (attempts < maxAttempts) {
                delay(5000)  // Wait 5 seconds
                attempts++
                
                try {
                    val rosca = repository.getRoscaById(roscaId)
                    
                    // Stop if ROSCA not found
                    if (rosca == null) {
                        Log.w(TAG, "ROSCA $roscaId not found, stopping exchange check")
                        break
                    }
                    
                    // Stop if already active
                    if (rosca.status == RoscaState.ACTIVE) {
                        Log.d(TAG, "✓ ROSCA already active, stopping exchange check")
                        break
                    }
                    
                    // Count members with exchange info
                    val members = repository.getMembersByRoscaId(roscaId)
                    val membersWithExchangeInfo = members.count { member ->
                        val info = member.multisigInfo?.exchangeState
                        !info.isNullOrBlank() && info.length > 100
                    }
                    
                    Log.d(TAG, "📊 Exchange check ($attempts/$maxAttempts): $membersWithExchangeInfo/${rosca.totalMembers} members ready")
                    
                    // Check if all members have exchange info
                    if (membersWithExchangeInfo == rosca.totalMembers) {
                        Log.i(TAG, "")
                        Log.i(TAG, "🎯 ALL MEMBERS HAVE EXCHANGE INFO!")
                        Log.i(TAG, "Triggering key exchange...")
                        
                        // Only creator performs the final exchange
                        if (rosca.creatorId == userId) {
                            Log.d(TAG, "✓ Current user is creator - performing exchange")
                            
                            val result = performKeyExchange(roscaId, userId)
                            
                            if (result.isSuccess) {
                                Log.i(TAG, "")
                                Log.i(TAG, "🎉🎉🎉 KEY EXCHANGE COMPLETED SUCCESSFULLY! 🎉🎉🎉")
                                Log.i(TAG, "ROSCA is now ACTIVE and ready for contributions")
                            } else {
                                Log.e(TAG, "")
                                Log.e(TAG, "❌ Key exchange failed: ${result.exceptionOrNull()?.message}")
                                Log.e(TAG, "Will retry on next check...")
                                
                                // Don't break - let it retry
                                continue
                            }
                        } else {
                            Log.d(TAG, "⏭️ Current user is not creator - waiting for creator to finalize")
                            Log.d(TAG, "  Current user: $userId")
                            Log.d(TAG, "  Creator: ${rosca.creatorId}")
                            
                            // Keep checking until creator completes
                            continue
                        }
                        
                        break
                    }
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error during exchange check (attempt $attempts): ${e.message}")
                }
            }
            
            if (attempts >= maxAttempts) {
                Log.w(TAG, "")
                Log.w(TAG, "⏱️ Exchange check timeout after ${maxAttempts * 5} seconds")
                Log.w(TAG, "Some members may not have uploaded their exchange info yet")
                Log.w(TAG, "Exchange will auto-trigger when all members are ready")
            }
        }
    }    
    
    suspend fun getMembersWithMultisigInfo(roscaId: String): List<Member> {
        return try {
            Log.d(TAG, "getMembersWithMultisigInfo: Querying for ROSCA: $roscaId")
            
            val allMembers = repository.getMembersByRoscaId(roscaId)
            val membersWithInfo = allMembers.filter { member -> 
                val info = member.multisigInfo
                info != null && !info.exchangeState.isNullOrEmpty()
            }
            
            Log.d(TAG, "getMembersWithMultisigInfo: Found ${membersWithInfo.size}/${allMembers.size} members with multisig info")
            
            membersWithInfo.forEach { member ->
                val info = member.multisigInfo
                Log.d(TAG, "  Member ${member.name}:")
                Log.d(TAG, "    User ID: ${member.userId}")
                Log.d(TAG, "    Address: ${info?.address?.take(20)}...")
                Log.d(TAG, "    IsReady: ${info?.isReady}")
            }
            
            membersWithInfo
        } catch (e: Exception) {
            Log.e(TAG, "Error getting members with multisig info for ROSCA: $roscaId", e)
            emptyList()
        }
    }
    
    suspend fun startNewRound(roscaId: String): Result<Round> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting new round for ROSCA: $roscaId")
            
            val rosca = repository.getRoscaById(roscaId)
                ?: return@withContext Result.failure(Exception("ROSCA not found"))
            
            if (rosca.status != RoscaState.ACTIVE) {
                return@withContext Result.failure(Exception("ROSCA is not active"))
            }
            
            val rounds = repository.getRoundsByRoscaId(roscaId)
            val nextRoundNumber = (rounds.maxOfOrNull { it.roundNumber } ?: 0) + 1
            
            if (nextRoundNumber > rosca.totalMembers) {
                return@withContext Result.failure(Exception("All rounds completed"))
            }
            
            val (recipientId, recipientAddress) = when (rosca.distributionMethod) {
                DistributionMethod.PREDETERMINED -> {
                    val members = repository.getMembersByRoscaId(roscaId)
                        .sortedBy { it.position }
                    val recipient = members.getOrNull(nextRoundNumber - 1)
                        ?: return@withContext Result.failure(Exception("No recipient found"))
                    Pair(recipient.id, recipient.walletAddress)
                }
                DistributionMethod.BIDDING -> {
                    Pair(null, null)
                }
                DistributionMethod.LOTTERY -> {
                    val members = repository.getMembersByRoscaId(roscaId)
                    val previousRecipients = rounds.mapNotNull { it.recipientId }
                    val availableMembers = members.filter { it.id !in previousRecipients }
                    val recipient = availableMembers.randomOrNull()
                        ?: return@withContext Result.failure(Exception("No available recipients"))
                    Pair(recipient.id, recipient.walletAddress)
                }
            }
            
            val now = System.currentTimeMillis()
            val contributionDeadline = now + (rosca.frequencyDays * 24 * 60 * 60 * 1000L)
            
            val round = Round(
                id = UUID.randomUUID().toString(),
                roscaId = roscaId,
                roundNumber = nextRoundNumber,
                recipientId = recipientId,
                recipientAddress = recipientAddress,
                targetAmount = rosca.contributionAmount * rosca.totalMembers,
                collectedAmount = 0L,
                distributedAmount = 0L,
                bidAmount = null,
                status = if (rosca.distributionMethod == DistributionMethod.BIDDING) 
                    RoundStatus.BIDDING else RoundStatus.CONTRIBUTION,
                biddingDeadline = if (rosca.distributionMethod == DistributionMethod.BIDDING)
                    now + (3 * 24 * 60 * 60 * 1000L) else null,
                startedAt = now,
                contributionDeadline = contributionDeadline,
                payoutTransactionHash = null,
                completedAt = null
            )
            
            repository.insertRound(round)
            
            val updatedRosca = rosca.copy(currentRound = nextRoundNumber)
            repository.updateRosca(updatedRosca)
            
            Log.d(TAG, "New round started: Round $nextRoundNumber")
            Result.success(round)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting new round", e)
            Result.failure(e)
        }
    }

    suspend fun processPayout(roscaId: String, roundNumber: Int): Result<String> = 
        withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Processing payout for ROSCA: $roscaId, Round: $roundNumber")
            
            val round = repository.getRoundByNumber(roscaId, roundNumber)
                ?: return@withContext Result.failure(Exception("Round not found"))
            
            if (round.status != RoundStatus.CONTRIBUTION) {
                return@withContext Result.failure(Exception("Round not ready for payout"))
            }
            
            if (round.recipientAddress == null) {
                return@withContext Result.failure(Exception("No recipient for this round"))
            }
            
            if (round.collectedAmount < round.targetAmount) {
                return@withContext Result.failure(
                    Exception("Insufficient contributions: ${round.collectedAmount}/${round.targetAmount}")
                )
            }
            
            val rosca = repository.getRoscaById(roscaId)
                ?: return@withContext Result.failure(Exception("ROSCA not found"))
            
            val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getString("user_id", null)
            
            if (userId.isNullOrEmpty()) {
                return@withContext Result.failure(Exception("User not logged in"))
            }
            
            val switchResult = WalletSelectionManager.switchToRoscaWallet(
                context = context,
                userId = userId,
                roscaId = roscaId,
                roscaName = rosca.name,
                multisigAddress = rosca.multisigAddress,
                walletSuite = walletSuite
            )
            
            if (switchResult.isFailure) {
                return@withContext Result.failure(
                    switchResult.exceptionOrNull() ?: Exception("Failed to switch to ROSCA wallet")
                )
            }
            
            val updatingRound = round.copy(status = RoundStatus.PAYOUT)
            repository.updateRound(updatingRound)
            
            WalletSelectionManager.selectRoscaWallet(
                roscaId = roscaId,
                roscaName = rosca.name,
                multisigAddress = rosca.multisigAddress
            )
            
            val txHash = withTimeout(60000L) {
                suspendCoroutine<String> { continuation ->
                    walletSuite.createMultisigTransaction(
                        roscaId,
                        round.recipientAddress,
                        round.collectedAmount,
                        object : WalletSuite.MultisigTxCallback {
                            override fun onSuccess(txData: String) {
                                Log.d(TAG, "Multisig transaction created")
                                scope.launch {
                                    storeUnsignedTransaction(roscaId, roundNumber, txData, txData)
                                }
                                continuation.resume(txData)
                            }
                            
                            override fun onError(error: String) {
                                Log.e(TAG, "Failed to create multisig transaction: $error")
                                continuation.resumeWithException(Exception(error))
                            }
                        }
                    )
                }
            }
            
            val updatedRound = updatingRound.copy(
                payoutTransactionHash = txHash,
                status = RoundStatus.PAYOUT
            )
            repository.updateRound(updatedRound)
            
            coordinateSigningThroughDatabase(roscaId, roundNumber, txHash)
            
            Log.d(TAG, "Payout transaction created: $txHash")
            Log.d(TAG, "Remaining on ROSCA wallet for signing")
            Result.success(txHash)
            
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Payout timeout")
            Result.failure(Exception("Payout timeout - transaction creation took too long"))
        } catch (e: Exception) {
            Log.e(TAG, "Error processing payout", e)
            Result.failure(e)
        }
    }

    private suspend fun storeUnsignedTransaction(
        roscaId: String,
        roundNumber: Int,
        txHash: String,
        unsignedTx: String
    ) {
        try {
            val sharedPrefs = context.getSharedPreferences("rosca_transactions", Context.MODE_PRIVATE)
            sharedPrefs.edit().putString("${roscaId}_${roundNumber}_unsigned", unsignedTx).apply()
            Log.d(TAG, "Stored unsigned transaction for coordination")
        } catch (e: Exception) {
            Log.e(TAG, "Error storing unsigned transaction", e)
        }
    }

    private suspend fun coordinateSigningThroughDatabase(
        roscaId: String,
        roundNumber: Int,
        txHash: String
    ) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Coordinating signing through database for tx: $txHash")
            
            val members = repository.getMembersByRoscaId(roscaId)
            val rosca = repository.getRoscaById(roscaId) ?: return@withContext
            val threshold = rosca.totalMembers - 1
            
            members.forEach { member ->
                val signature = MultisigSignature(
                    id = UUID.randomUUID().toString(),
                    roscaId = roscaId,
                    roundNumber = roundNumber,
                    txHash = txHash,
                    memberId = member.id,
                    hasSigned = false,
                    signature = null,
                    timestamp = System.currentTimeMillis()
                )
                repository.insertMultisigSignature(signature)
            }
            
            monitorSigningProgress(roscaId, roundNumber, txHash, threshold)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error coordinating signing", e)
        }
    }

    private suspend fun monitorSigningProgress(
        roscaId: String,
        roundNumber: Int,
        txHash: String,
        threshold: Int
    ) = withContext(Dispatchers.IO) {
        var signedCount = 0
        try {
            var attempts = 0
            val maxAttempts = 120
            
            while (attempts < maxAttempts) {
                delay(5000)
                attempts++
                
                val signatures = repository.getMultisigSignatures(roscaId, roundNumber)
                signedCount = signatures.count { it.hasSigned }
                
                Log.d(TAG, "Signing progress: $signedCount/$threshold signatures (attempt $attempts/$maxAttempts)")
                
                if (signedCount >= threshold) {
                    Log.d(TAG, "Threshold reached! Finalizing transaction...")
                    finalizeTransaction(roscaId, roundNumber, txHash, signatures)
                    return@withContext
                }
            }
            
            Log.e(TAG, "Signing timeout: Only got $signedCount/$threshold signatures")
            val round = repository.getRoundByNumber(roscaId, roundNumber)
            if (round != null) {
                repository.updateRound(round.copy(status = RoundStatus.FAILED))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error monitoring signing progress", e)
        }
    }

    private suspend fun finalizeTransaction(
        roscaId: String,
        roundNumber: Int,
        txHash: String,
        signatures: List<MultisigSignature>
    ) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Finalizing transaction: $txHash")
            
            val signatureStrings = signatures
                .filter { it.hasSigned && it.signature != null }
                .map { it.signature!! }
            
            val rosca = repository.getRoscaById(roscaId)
            val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getString("user_id", null)
            
            if (userId == null || rosca == null) {
                Log.e(TAG, "Cannot finalize: missing user or ROSCA data")
                return@withContext
            }
            
            val switchResult = WalletSelectionManager.switchToRoscaWallet(
                context = context,
                userId = userId,
                roscaId = roscaId,
                roscaName = rosca.name,
                multisigAddress = rosca.multisigAddress,
                walletSuite = walletSuite
            )
            
            if (switchResult.isFailure) {
                Log.e(TAG, "Failed to switch to ROSCA wallet for finalization")
                return@withContext
            }
            
            WalletSelectionManager.selectRoscaWallet(
                roscaId = roscaId,
                roscaName = rosca.name,
                multisigAddress = rosca.multisigAddress
            )
            
            val result = withTimeout(60000L) {
                suspendCoroutine<Boolean> { continuation ->
                    walletSuite.submitMultisigTransaction(
                        roscaId,
                        txHash,
                        object : WalletSuite.MultisigSubmitCallback {
                            override fun onSuccess(finalTxHash: String) {
                                Log.d(TAG, "Transaction submitted successfully: $finalTxHash")
                                continuation.resume(true)
                            }
                            
                            override fun onError(error: String) {
                                Log.e(TAG, "Failed to submit transaction: $error")
                                continuation.resumeWithException(Exception(error))
                            }
                        }
                    )
                }
            }
            
            if (result) {
                val round = repository.getRoundByNumber(roscaId, roundNumber)
                if (round != null) {
                    val updatedRound = round.copy(
                        status = RoundStatus.COMPLETED,
                        distributedAmount = round.collectedAmount,
                        completedAt = System.currentTimeMillis()
                    )
                    repository.updateRound(updatedRound)
                    
                    if (roundNumber < rosca.totalMembers) {
                        delay(1000)
                        startNewRound(roscaId)
                    } else if (roundNumber == rosca.totalMembers) {
                        repository.updateRosca(
                            rosca.copy(
                                status = RoscaState.COMPLETED,
                                completedAt = System.currentTimeMillis()
                            )
                        )
                        Log.d(TAG, "ROSCA completed: $roscaId")
                    }
                }
            }
            
            Log.d(TAG, "Remaining on ROSCA wallet after finalization")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error finalizing transaction", e)
        }
    }

    suspend fun signPayout(roscaId: String, roundNumber: Int): Result<Unit> = 
        withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Signing payout for ROSCA: $roscaId, Round: $roundNumber")
            
            val round = repository.getRoundByNumber(roscaId, roundNumber)
                ?: return@withContext Result.failure(Exception("Round not found"))
            
            if (round.status != RoundStatus.PAYOUT) {
                return@withContext Result.failure(Exception("Round not in payout phase"))
            }
            
            val txHash = round.payoutTransactionHash
                ?: return@withContext Result.failure(Exception("No transaction to sign"))
            
            val sharedPrefs = context.getSharedPreferences("rosca_transactions", Context.MODE_PRIVATE)
            val unsignedTx = sharedPrefs.getString("${roscaId}_${roundNumber}_unsigned", null)
                ?: return@withContext Result.failure(Exception("Unsigned transaction not found"))
            
            val sharedPrefsApp = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val userId = sharedPrefsApp.getString("user_id", null)
            
            if (userId == null) {
                return@withContext Result.failure(Exception("User not logged in"))
            }
            
            val rosca = repository.getRoscaById(roscaId)
            
            if (rosca == null) {
                return@withContext Result.failure(Exception("ROSCA not found"))
            }
            
            val switchResult = WalletSelectionManager.switchToRoscaWallet(
                context = context,
                userId = userId,
                roscaId = roscaId,
                roscaName = rosca.name,
                multisigAddress = rosca.multisigAddress,
                walletSuite = walletSuite
            )
            
            if (switchResult.isFailure) {
                return@withContext Result.failure(
                    switchResult.exceptionOrNull() ?: Exception("Failed to switch to ROSCA wallet")
                )
            }
            
            WalletSelectionManager.selectRoscaWallet(
                roscaId = roscaId,
                roscaName = rosca.name,
                multisigAddress = rosca.multisigAddress
            )
            
            val signatureString = withTimeout(30000L) {
                suspendCoroutine<String> { continuation ->
                    walletSuite.signMultisigTransaction(
                        roscaId,
                        unsignedTx,
                        object : WalletSuite.MultisigSignCallback {
                            override fun onSuccess(signedData: String) {
                                Log.d(TAG, "Transaction signed successfully")
                                continuation.resume(signedData)
                            }
                            
                            override fun onError(error: String) {
                                Log.e(TAG, "Signing failed: $error")
                                continuation.resumeWithException(Exception(error))
                            }
                        }
                    )
                }
            }
            
            val members = repository.getMembersByRoscaId(roscaId)
            val member = members.find { it.userId == userId }
            
            if (member != null) {
                val existingRecord = repository.getMultisigSignature(roscaId, roundNumber, member.id)
                val multisigSignature = existingRecord?.copy(
                    hasSigned = true,
                    signature = signatureString,
                    timestamp = System.currentTimeMillis()
                ) ?: MultisigSignature(
                    id = UUID.randomUUID().toString(),
                    roscaId = roscaId,
                    roundNumber = roundNumber,
                    txHash = txHash,
                    memberId = member.id,
                    hasSigned = true,
                    signature = signatureString,
                    timestamp = System.currentTimeMillis()
                )
                
                repository.upsertMultisigSignature(multisigSignature)
                Log.d(TAG, "Signature stored in database")
            }
            
            Log.d(TAG, "Remaining on ROSCA wallet after signing")
            Result.success(Unit)
            
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Signing timeout")
            Result.failure(Exception("Signing timeout"))
        } catch (e: Exception) {
            Log.e(TAG, "Error signing payout", e)
            Result.failure(e)
        }
    }

    // ============================================================================
    // QUERY METHODS
    // ============================================================================

    suspend fun getCurrentRound(roscaId: String): Round? {
        return try {
            val rosca = repository.getRoscaById(roscaId) ?: return null
            repository.getRoundByNumber(roscaId, rosca.currentRound)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current round", e)
            null
        }
    }

    suspend fun getRoscaStatus(roscaId: String): RoscaStatus? {
        return try {
            val rosca = repository.getRoscaById(roscaId) ?: return null
            val currentRound = getCurrentRound(roscaId)
            val members = repository.getMembersByRoscaId(roscaId)
            
            val nextRecipient = if (currentRound != null && currentRound.recipientId != null) {
                members.find { it.id == currentRound.recipientId }
            } else null
            
            val memberStatuses = members.associate { member ->
                val hasContributed = if (currentRound != null) {
                    repository.getContribution(roscaId, currentRound.roundNumber, member.id) != null
                } else false
                
                val hasSigned = if (currentRound != null) {
                    repository.getMultisigSignature(roscaId, currentRound.roundNumber, member.id)?.hasSigned ?: false
                } else false
                
                member.id to MemberStatus(
                    memberId = member.id,
                    hasContributed = hasContributed,
                    hasSigned = hasSigned,
                    isActive = member.isActive
                )
            }
            
            RoscaStatus(
                rosca = rosca,
                currentRound = currentRound,
                nextPayoutRecipient = nextRecipient,
                syncStatus = SyncStatus.SYNCED,
                memberStatuses = memberStatuses
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting ROSCA status", e)
            null
        }
    }

    // ============================================================================
    // UTILITY METHODS
    // ============================================================================

    private fun formatAtomicToXmr(atomic: Long): String {
        return try {
            val xmr = atomic / 1e12
            String.format("%.6f", xmr)
        } catch (e: Exception) {
            "0"
        }
    }

    fun cleanup() {
        scope.cancel()
    }
}
