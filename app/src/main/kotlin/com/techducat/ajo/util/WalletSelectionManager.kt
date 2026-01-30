package com.techducat.ajo.util

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.techducat.ajo.wallet.WalletSuite
import com.m2049r.xmrwallet.model.Wallet
import com.m2049r.xmrwallet.model.WalletManager
import com.m2049r.xmrwallet.model.NetworkType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.atomic.AtomicReference

/**
 * WalletSelectionManager - Manages wallet context switching
 * 
 * THREAD-SAFE VERSION: Added proper synchronization for multithreading
 * 
 * Key Thread-Safety Features:
 * - Mutex for wallet switching operations (prevents concurrent switches)
 * - AtomicReference for currentWalletPath
 * - Synchronized access to SharedPreferences
 * - Thread-safe state updates
 * - Prevents race conditions in wallet operations
 */
object WalletSelectionManager {
    private const val TAG = "com.techducat.ajo.util.WalletSelectionManager"
    
    enum class WalletType {
        PERSONAL,
        ROSCA
    }
    
    data class SelectedWallet(
        val type: WalletType,
        val walletPath: String,
        val roscaId: String? = null,
        val roscaName: String? = null,
        val multisigAddress: String? = null
    )
    
    private val _selectedWallet = MutableLiveData<SelectedWallet?>()
    val selectedWallet = _selectedWallet
    
    private const val PREFS_NAME = "wallet_selection"
    private const val KEY_SELECTED_TYPE = "selected_wallet_type"
    private const val KEY_SELECTED_WALLET_PATH = "selected_wallet_path"
    private const val KEY_SELECTED_ROSCA_ID = "selected_rosca_id"
    private const val KEY_SELECTED_ROSCA_NAME = "selected_rosca_name"
    private const val KEY_SELECTED_MULTISIG_ADDRESS = "selected_multisig_address"
    
    // ============================================================================
    // THREAD-SAFETY PRIMITIVES
    // ============================================================================
    
    /**
     * Mutex to ensure only one wallet switch operation at a time
     * Prevents race conditions when switching between wallets
     */
    private val walletSwitchMutex = Mutex()
    
    /**
     * Mutex for wallet creation operations
     * Prevents concurrent wallet creation attempts
     */
    private val walletCreationMutex = Mutex()
    
    /**
     * AtomicReference for thread-safe currentWalletPath access
     * Ensures visibility across threads without additional locking for reads
     */
    private val currentWalletPath = AtomicReference<String?>(null)
    
    /**
     * Mutex for SharedPreferences operations
     * Ensures atomic read-modify-write cycles
     */
    private val prefsMutex = Mutex()
    
    init {
        _selectedWallet.postValue(null) 
    }
    
    // ============================================================================
    // WALLET PATH MANAGEMENT (Thread-safe, read-only operations)
    // ============================================================================
    
    fun getPersonalWalletPath(context: Context, userId: String): String {
        val dir = context.getDir("wallets", Context.MODE_PRIVATE)
        return File(dir, "wallet_$userId").absolutePath
    }
    
    fun getRoscaWalletPath(context: Context, userId: String, roscaId: String): String {
        val dir = context.getDir("wallets", Context.MODE_PRIVATE)
        return File(dir, "rosca_${roscaId}_$userId").absolutePath
    }
    
    fun walletExists(walletPath: String): Boolean {
        val keysFile = File("$walletPath.keys")
        return keysFile.exists()
    }
    
    // ============================================================================
    // WALLET SWITCHING (THREAD-SAFE with Mutex)
    // ============================================================================
    
    /**
     * ✅ THREAD-SAFE: Switch to personal wallet
     * Uses mutex to prevent concurrent wallet switches
     */
    suspend fun switchToPersonalWallet(
        context: Context,
        userId: String,
        walletSuite: WalletSuite
    ): Result<Unit> = withContext(Dispatchers.IO) {
        // 🔒 CRITICAL: Lock to prevent concurrent wallet switches
        walletSwitchMutex.withLock {
            try {
                val personalWalletPath = getPersonalWalletPath(context, userId)
                
                // ✅ Thread-safe read using AtomicReference
                val currentPath = currentWalletPath.get()
                if (currentPath == personalWalletPath) {
                    Log.d(TAG, "Already on personal wallet, no switch needed")
                    return@withContext Result.success(Unit)
                }
                
                Log.i(TAG, "=== SWITCH TO PERSONAL WALLET (THREAD-SAFE) ===")
                
                // ✅ Close current wallet file (safe - we hold the mutex)
                walletSuite.closeCurrentWalletFile()
                
                // ✅ Thread-safe password retrieval
                val password = getWalletPassword(context)
                
                // ✅ Use WalletManager to open personal wallet WITH PASSWORD
                Log.d(TAG, "Opening personal wallet: $personalWalletPath")
                val walletManager = WalletManager.getInstance()
                val personalWallet = walletManager.openWallet(personalWalletPath, password)
                
                if (personalWallet == null) {
                    val error = walletManager.errorString
                    Log.e(TAG, "❌ Failed to open personal wallet: $error")
                    return@withContext Result.failure(Exception("Failed to open personal wallet: $error"))
                }
                
                // ✅ Check status
                val status = personalWallet.getStatus()
                if (status != Wallet.Status.Status_Ok.ordinal) {
                    val error = personalWallet.getErrorString()
                    Log.e(TAG, "❌ Personal wallet status not OK: $error")
                    personalWallet.close()
                    return@withContext Result.failure(Exception("Wallet status error: $error"))
                }
                
                // ✅ Initialize daemon
                try {
                    val node = walletManager.createNodeFromConfig()
                    personalWallet.initJ(
                        node.hostAddress, 0,
                        node.username, node.password,
                        node.ssl, false, ""
                    )
                    Log.d(TAG, "✓ Daemon initialized")
                } catch (e: Exception) {
                    Log.w(TAG, "Daemon init warning: ${e.message}")
                }
                
                // ✅✅✅ CRITICAL: Sync with WalletSuite
                walletSuite.setUserWallet(personalWallet)
                
                // ✅ Thread-safe update using AtomicReference
                currentWalletPath.set(personalWalletPath)
                
                // ✅ Update UI state (LiveData is thread-safe)
                selectPersonalWallet(personalWalletPath)
                
                Log.i(TAG, "✅ Switched to personal wallet (thread-safe)")
                Result.success(Unit)
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error switching to personal wallet", e)
                Result.failure(e)
            }
        }
    }

    /**
     * ✅ THREAD-SAFE: Helper to get wallet password
     */
    private suspend fun getWalletPassword(context: Context): String = withContext(Dispatchers.IO) {
        prefsMutex.withLock {
            val sharedPrefs = context.getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)
            sharedPrefs.getString("wallet_password", "") ?: ""
        }
    }

    /**
     * ✅ THREAD-SAFE: Helper to reopen a wallet by path
     */
    private suspend fun reopenWallet(
        walletPath: String,
        walletManager: WalletManager,
        walletSuite: WalletSuite,
        context: Context
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Reopening wallet: $walletPath")
            
            // ✅ Thread-safe password retrieval
            val password = getWalletPassword(context)
            
            val wallet = walletManager.openWallet(walletPath, password)
            
            if (wallet == null) {
                Log.e(TAG, "❌ Failed to reopen: ${walletManager.errorString}")
                return@withContext false
            }
            
            if (wallet.getStatus() != Wallet.Status.Status_Ok.ordinal) {
                Log.e(TAG, "❌ Status error: ${wallet.getErrorString()}")
                wallet.close()
                return@withContext false
            }
            
            // Reinitialize daemon
            try {
                val node = walletManager.createNodeFromConfig()
                wallet.initJ(
                    node.hostAddress, 0,
                    node.username, node.password,
                    node.ssl, false, ""
                )
            } catch (e: Exception) {
                Log.w(TAG, "Daemon warning: ${e.message}")
            }
            
            // ✅✅✅ KEY FIX: Sync WalletSuite's wallet field
            walletSuite.setUserWallet(wallet)
            
            Log.i(TAG, "✅ Wallet reopened and synced")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception reopening wallet", e)
            false
        }
    }
    
    /**
     * ✅✅✅ THREAD-SAFE: Switch to ROSCA wallet using WalletManager directly
     * 
     * @param allowIncompleteMultisig Set to true during ROSCA creation/join to bypass multisig validation
     */
    suspend fun switchToRoscaWallet(
        context: Context,
        userId: String,
        roscaId: String,
        roscaName: String,
        multisigAddress: String?,
        walletSuite: WalletSuite,
        allowIncompleteMultisig: Boolean = false
    ): Result<Unit> = withContext(Dispatchers.IO) {
        // 🔒 CRITICAL: Lock to prevent concurrent wallet switches
        walletSwitchMutex.withLock {
            try {
                val walletPath = getRoscaWalletPath(context, userId, roscaId)
                
                // ✅ CHECK 1: Wallet file exists
                if (!walletExists(walletPath)) {
                    Log.e(TAG, "❌ ROSCA wallet does not exist: $walletPath")
                    return@withContext Result.failure(
                        Exception("ROSCA wallet not found. Please complete setup first.")
                    )
                }
                
                // ✅ CHECK 2: Multisig setup is complete (SKIP during creation/join)
                if (!allowIncompleteMultisig && multisigAddress == null) {
                    Log.w(TAG, "⚠️ Multisig setup incomplete for ROSCA: $roscaId")
                    return@withContext Result.failure(
                        Exception("Multisig setup not complete. Waiting for all members to join.")
                    )
                }
                
                // ✅ Thread-safe check if already on this wallet
                val currentPath = currentWalletPath.get()
                if (currentPath == walletPath) {
                    Log.d(TAG, "Already on ROSCA wallet, no switch needed")
                    return@withContext Result.success(Unit)
                }
                
                Log.i(TAG, "=== SWITCH TO ROSCA WALLET (THREAD-SAFE) ===")
                Log.i(TAG, "ROSCA ID: $roscaId")
                Log.i(TAG, "Path: $walletPath")
                if (allowIncompleteMultisig) {
                    Log.i(TAG, "⚠️ Allowing incomplete multisig (creation/join phase)")
                }
                
                // ✅ Close current wallet file
                walletSuite.closeCurrentWalletFile()
                
                // ✅ Thread-safe password retrieval
                val password = getWalletPassword(context)
                
                // ✅ Use WalletManager to open ROSCA wallet
                Log.d(TAG, "Opening ROSCA wallet: $walletPath")
                val walletManager = WalletManager.getInstance()
                val roscaWallet = walletManager.openWallet(walletPath, password)
                
                if (roscaWallet == null) {
                    val error = walletManager.errorString
                    Log.e(TAG, "❌ Failed to open ROSCA wallet: $error")
                    return@withContext Result.failure(Exception("Failed to open ROSCA wallet: $error"))
                }
                
                // ✅ Check status
                val status = roscaWallet.getStatus()
                if (status != Wallet.Status.Status_Ok.ordinal) {
                    val error = roscaWallet.getErrorString()
                    Log.e(TAG, "❌ ROSCA wallet status not OK: $error")
                    roscaWallet.close()
                    return@withContext Result.failure(Exception("Wallet status error: $error"))
                }
                
                // ✅ Initialize daemon
                try {
                    val node = walletManager.createNodeFromConfig()
                    roscaWallet.initJ(
                        node.hostAddress, 0,
                        node.username, node.password,
                        node.ssl, false, ""
                    )
                    Log.d(TAG, "✓ Daemon initialized")
                } catch (e: Exception) {
                    Log.w(TAG, "Daemon init warning: ${e.message}")
                }
                
                // ✅✅✅ CRITICAL: Sync with WalletSuite
                walletSuite.setUserWallet(roscaWallet)
                
                // ✅ Thread-safe update using AtomicReference
                currentWalletPath.set(walletPath)
                
                // ✅ Update UI state (LiveData is thread-safe)
                selectRoscaWallet(roscaId, roscaName, multisigAddress)
                
                Log.i(TAG, "✅ Switched to ROSCA wallet (thread-safe)")
                if (multisigAddress != null) {
                    Log.i(TAG, "Multisig address: ${multisigAddress.take(15)}...")
                }
                
                Result.success(Unit)
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error switching to ROSCA wallet", e)
                Result.failure(e)
            }
        }
    }
    
    /**
     * ✅✅✅ THREAD-SAFE: Create FRESH ROSCA wallet
     * Uses mutex to prevent concurrent wallet creation
     */
    suspend fun createFreshRoscaWallet(
        context: Context,
        userId: String,
        roscaId: String,
        walletSuite: WalletSuite
    ): Result<String> = withContext(Dispatchers.IO) {
        // 🔒 CRITICAL: Lock to prevent concurrent wallet creation
        walletCreationMutex.withLock {
            var previousWalletPath: String? = null
            var roscaWallet: Wallet? = null
            
            try {
                val roscaWalletPath = getRoscaWalletPath(context, userId, roscaId)
                
                Log.i(TAG, "=== CREATE FRESH ROSCA WALLET (THREAD-SAFE) ===")
                Log.i(TAG, "User: $userId, ROSCA: $roscaId")
                Log.i(TAG, "Target path: $roscaWalletPath")
                
                // ✅ Thread-safe check if ROSCA wallet already exists
                if (walletExists(roscaWalletPath)) {
                    Log.w(TAG, "⚠️ ROSCA wallet already exists: $roscaWalletPath")
                    return@withContext Result.success(roscaWalletPath)
                }
                
                // ✅ Save reference to current wallet for restoration
                val currentWallet = walletSuite.getUserWallet()
                previousWalletPath = currentWallet?.getPath()
                
                Log.d(TAG, "Current wallet path: $previousWalletPath")
                
                Log.d(TAG, "Saving and closing current wallet file...")
                walletSuite.closeCurrentWalletFile()
                Log.d(TAG, "✓ Current wallet file closed (WalletSuite synced)")
                
                // ✅✅✅ KEY CHANGE: Use createWallet() to create FRESH wallet
                Log.d(TAG, "Creating fresh ROSCA wallet with new seed...")
                val walletManager = WalletManager.getInstance()
                
                // ✅ Thread-safe password retrieval
                val password = getWalletPassword(context)
                
                // ✅ CREATE FRESH WALLET - generates new seed internally
                val node = walletManager.createNodeFromConfig()
                roscaWallet = walletManager.createWallet(
                    roscaWalletPath,
                    password,
                    "English",
                    node.networkType.value
                )
                
                if (roscaWallet == null) {
                    val error = walletManager.errorString
                    Log.e(TAG, "❌ Failed to create fresh ROSCA wallet: $error")
                    
                    // ✅ Restore previous wallet on failure
                    if (previousWalletPath != null) {
                        Log.d(TAG, "Restoring previous wallet after failure...")
                        reopenWallet(previousWalletPath, walletManager, walletSuite, context)
                    }
                    
                    return@withContext Result.failure(Exception("Failed to create ROSCA wallet: $error"))
                }
                
                // ✅ Check wallet status
                val status = roscaWallet.getStatus()
                if (status != Wallet.Status.Status_Ok.ordinal) {
                    val error = roscaWallet.getErrorString()
                    Log.e(TAG, "❌ ROSCA wallet status not OK: $error")
                    
                    roscaWallet.close()
                    
                    // ✅ Restore previous wallet
                    if (previousWalletPath != null) {
                        reopenWallet(previousWalletPath, walletManager, walletSuite, context)
                    }
                    
                    return@withContext Result.failure(Exception("ROSCA wallet creation failed: $error"))
                }
                
                // ✅ Initialize daemon
                try {
                    roscaWallet.initJ(
                        node.hostAddress, 0,
                        node.username, node.password,
                        node.ssl, false, ""
                    )
                    Log.d(TAG, "✓ Daemon initialized for ROSCA wallet")
                } catch (e: Exception) {
                    Log.w(TAG, "Daemon init warning (non-fatal): ${e.message}")
                }
                
                // ✅ Save and close ROSCA wallet
                Log.d(TAG, "Saving fresh ROSCA wallet...")
                roscaWallet.store()
                Log.d(TAG, "✓ ROSCA wallet saved")
                
                Log.d(TAG, "Closing ROSCA wallet file...")
                roscaWallet.close()
                roscaWallet = null
                Log.d(TAG, "✓ ROSCA wallet file closed")
                
                // ✅ CRITICAL - ALWAYS restore previous wallet
                if (previousWalletPath != null) {
                    Log.d(TAG, "Restoring previous wallet: $previousWalletPath")
                    val restored = reopenWallet(previousWalletPath, walletManager, walletSuite, context)
                    
                    if (restored) {
                        Log.i(TAG, "✓ Previous wallet restored successfully")
                    } else {
                        Log.w(TAG, "⚠️ Failed to restore previous wallet")
                    }
                } else {
                    Log.w(TAG, "⚠️ No previous wallet to restore")
                }
                
                Log.i(TAG, "✅✅✅ FRESH ROSCA WALLET CREATED (THREAD-SAFE) ✅✅✅")
                Log.i(TAG, "Path: $roscaWalletPath")
                Log.i(TAG, "This wallet has its own independent seed")
                Log.i(TAG, "Previous wallet has been restored")
                
                Result.success(roscaWalletPath)
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exception creating fresh ROSCA wallet", e)
                
                // ✅ Cleanup and restore on ANY exception
                if (roscaWallet != null) {
                    try {
                        roscaWallet.close()
                        Log.d(TAG, "Closed ROSCA wallet during error cleanup")
                    } catch (closeError: Exception) {
                        Log.w(TAG, "Error closing ROSCA wallet: ${closeError.message}")
                    }
                }
                
                if (previousWalletPath != null) {
                    Log.d(TAG, "Restoring previous wallet after exception...")
                    val walletManager = WalletManager.getInstance()
                    reopenWallet(previousWalletPath, walletManager, walletSuite, context)
                }
                
                Result.failure(e)
            }
        }
    }
    
    /**
     * ✅ DEPRECATED: Old method that caused JNI crash
     */
    @Deprecated(
        message = "Use createFreshRoscaWallet instead - this method causes JNI crashes",
        replaceWith = ReplaceWith("createFreshRoscaWallet(context, userId, roscaId, walletSuite)")
    )
    suspend fun createRoscaWallet(
        context: Context,
        userId: String,
        roscaId: String,
        personalSeed: String,
        walletSuite: WalletSuite
    ): Result<String> {
        Log.w(TAG, "⚠️ DEPRECATED: createRoscaWallet() with seed parameter")
        Log.w(TAG, "⚠️ This method causes JNI crashes - use createFreshRoscaWallet() instead")
        return createFreshRoscaWallet(context, userId, roscaId, walletSuite)
    }
    
    // ============================================================================
    // UI STATE SELECTION (Thread-safe, doesn't switch wallets)
    // ============================================================================
    
    fun selectPersonalWallet() {
        val walletPath = currentWalletPath.get() ?: "personal_wallet"
        selectPersonalWallet(walletPath)
    }
    
    fun selectRoscaWallet(
        roscaId: String,
        roscaName: String,
        multisigAddress: String?
    ) {
        val walletPath = currentWalletPath.get() ?: "rosca_wallet_$roscaId"
        selectRoscaWallet(walletPath, roscaId, roscaName, multisigAddress)
    }
    
    /**
     * ✅✅✅ THREAD-SAFE: Prevents infinite loop
     * Only updates LiveData if selection actually changes
     */
    private fun selectPersonalWallet(walletPath: String) {
        // Check if same wallet is already selected
        val current = _selectedWallet.value
        if (current?.type == WalletType.PERSONAL && current.walletPath == walletPath) {
            Log.d(TAG, "⏭️ Same personal wallet already selected, skipping LiveData update")
            return
        }
        
        Log.d(TAG, "📱 Updating LiveData: Personal wallet")
        _selectedWallet.postValue(SelectedWallet(
            type = WalletType.PERSONAL,
            walletPath = walletPath
        ))
    }
    
    /**
     * ✅✅✅ THREAD-SAFE: Prevents infinite loop
     * Only updates LiveData if selection actually changes
     */
    private fun selectRoscaWallet(
        walletPath: String,
        roscaId: String,
        roscaName: String,
        multisigAddress: String?
    ) {
        // Check if same wallet is already selected
        val current = _selectedWallet.value
        if (current?.type == WalletType.ROSCA && 
            current.roscaId == roscaId && 
            current.walletPath == walletPath) {
            Log.d(TAG, "⏭️ Same ROSCA wallet already selected, skipping LiveData update")
            return
        }
        
        Log.d(TAG, "📱 Updating LiveData: ROSCA wallet $roscaId")
        _selectedWallet.postValue(SelectedWallet(
            type = WalletType.ROSCA,
            walletPath = walletPath,
            roscaId = roscaId,
            roscaName = roscaName,
            multisigAddress = multisigAddress
        ))
    }
    
    // ============================================================================
    // THREAD-SAFE GETTERS
    // ============================================================================
    
    fun getCurrentWallet(): SelectedWallet? = _selectedWallet.value
    
    fun isPersonalWalletSelected(): Boolean = _selectedWallet.value?.type == WalletType.PERSONAL
    
    fun isRoscaWalletSelected(): Boolean = _selectedWallet.value?.type == WalletType.ROSCA
    
    fun getSelectedRoscaId(): String? = _selectedWallet.value?.roscaId
    
    fun getSelectedMultisigAddress(): String? = _selectedWallet.value?.multisigAddress
    
    fun getCurrentWalletPath(): String? = currentWalletPath.get()
    
    // ============================================================================
    // THREAD-SAFE PERSISTENCE
    // ============================================================================
    
    suspend fun saveSelection(context: Context) = withContext(Dispatchers.IO) {
        prefsMutex.withLock {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val current = _selectedWallet.value
            
            prefs.edit().apply {
                putString(KEY_SELECTED_TYPE, current?.type?.name)
                putString(KEY_SELECTED_WALLET_PATH, current?.walletPath)
                putString(KEY_SELECTED_ROSCA_ID, current?.roscaId)
                putString(KEY_SELECTED_ROSCA_NAME, current?.roscaName)
                putString(KEY_SELECTED_MULTISIG_ADDRESS, current?.multisigAddress)
                apply()
            }
        }
    }
    
    suspend fun loadSelection(context: Context) = withContext(Dispatchers.IO) {
        prefsMutex.withLock {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            
            val typeName = prefs.getString(KEY_SELECTED_TYPE, null)
            val walletPath = prefs.getString(KEY_SELECTED_WALLET_PATH, null)
            val roscaId = prefs.getString(KEY_SELECTED_ROSCA_ID, null)
            val roscaName = prefs.getString(KEY_SELECTED_ROSCA_NAME, null)
            val multisigAddress = prefs.getString(KEY_SELECTED_MULTISIG_ADDRESS, null)
            
            val type = when (typeName) {
                "PERSONAL" -> WalletType.PERSONAL
                "ROSCA" -> WalletType.ROSCA
                else -> return@withContext
            }
            
            if (walletPath == null) return@withContext
            
            _selectedWallet.postValue(if (type == WalletType.ROSCA && roscaId != null) {
                SelectedWallet(
                    type = WalletType.ROSCA,
                    walletPath = walletPath,
                    roscaId = roscaId,
                    roscaName = roscaName,
                    multisigAddress = multisigAddress
                )
            } else {
                SelectedWallet(
                    type = WalletType.PERSONAL,
                    walletPath = walletPath
                )
            })
            
            currentWalletPath.set(walletPath)
        }
    }
    
    suspend fun clearSelection(context: Context) = withContext(Dispatchers.IO) {
        prefsMutex.withLock {
            _selectedWallet.postValue(null)
            currentWalletPath.set(null)
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
        }
    }
}
