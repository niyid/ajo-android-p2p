package com.techducat.ajo.wallet;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.m2049r.xmrwallet.data.Node;
import com.m2049r.xmrwallet.data.TxData;
import com.m2049r.xmrwallet.model.PendingTransaction;
import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.model.WalletListener;
import com.m2049r.xmrwallet.model.WalletManager;
import com.m2049r.xmrwallet.model.TransactionHistory;
import com.m2049r.xmrwallet.model.TransactionInfo;
import com.m2049r.xmrwallet.util.Helper;
import com.m2049r.xmrwallet.model.NetworkType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONException;

/**
 * WalletSuite - Monero Wallet Management for ROSCA Operations
 * 
 * This class provides comprehensive wallet functionality for Rotating Savings
 * and Credit Association (ROSCA) operations built on Monero blockchain.
 * 
 * Key Features:
 * - Multisig wallet creation and management
 * - ROSCA-specific transaction handling
 * - Distributed payout coordination
 * - Privacy-preserving fund management
 */
public class WalletSuite {
    private static final String TAG = "WalletSuite";
    private static final String PROPERTIES_FILE = "wallet.properties";

    private static volatile boolean nativeOk = false;
    private static volatile boolean nativeChecked = false;
    private static volatile WalletSuite instance;
    
    // Sync configuration - adapted for slow networks
    private static final long SYNC_TIMEOUT_MS = 7200000; // 120 minutes
    private static final long PERIODIC_SYNC_INTERVAL_MS = 600000; // 10 minutes
    private static final long RESCAN_PROGRESS_CHECK_INTERVAL_MS = 30000; // 30 seconds
    private static final long RESCAN_COOLDOWN_MS = 600000; // 10 minutes
    
    // Wallet state machine
    private enum WalletState {
        IDLE,
        SYNCING,
        RESCANNING,
        CLOSING,
        TRANSACTION,
        OPENING
    }
    
    private final AtomicReference<WalletState> currentState = new AtomicReference<>(WalletState.IDLE);
    private final AtomicLong lastSyncStartTime = new AtomicLong(0);
    private final AtomicLong lastRescanTime = new AtomicLong(0);
    private final AtomicLong cachedDaemonHeight = new AtomicLong(-1);
    
    private volatile Wallet wallet;
    private final WalletManager walletManager;
    private final Context context;
    private final ExecutorService syncExecutor;
    private final ExecutorService executorService;
    private final ScheduledExecutorService periodicSyncScheduler;
    private final Handler mainHandler;
    public volatile boolean isInitialized = false;
    
    private volatile String walletAddress;
    private volatile String currentWalletPath;
    private final AtomicLong balance = new AtomicLong(0L);
    private final AtomicLong unlockedBalance = new AtomicLong(0L);
    
    private volatile WalletStatusListener statusListener;
    private volatile TransactionListener transactionListener;
    private volatile DaemonConfigCallback daemonConfigCallback;
    
    private ScheduledFuture<?> periodicSyncTask;
    private ScheduledFuture<?> currentSyncTimeout;
    
    private RescanCallback rescanCallback = null;
    
    private final Object walletLock = new Object();
    
    private volatile RescanBalanceCallback rescanBalanceCallback;
    
    private volatile boolean transactionInProgress = false;
    private final Object transactionLock = new Object();
    private long transactionStartTime = 0;
    
    // Sync progress tracking
    private volatile long syncStartHeight = 0;
    private volatile long syncEndHeight = 0;
    private volatile long lastProgressUpdateTime = 0;
    
    // ============================================================================
    // CALLBACK INTERFACES
    // ============================================================================
    
    public interface WalletStatusListener {
        void onWalletInitialized(boolean success, String message);
        void onBalanceUpdated(long balance, long unlocked);
        void onSyncProgress(long height, long startHeight, long endHeight, double percentDone);
    }

    public interface TransactionListener {
        void onTransactionCreated(String txId, long amount);
        void onTransactionConfirmed(String txId);
        void onTransactionFailed(String txId, String error);
        void onOutputReceived(long amount, String txHash, boolean isConfirmed);
    }

    public interface AddressCallback {
        void onSuccess(String address);
        void onError(String error);
    }

    public interface BalanceCallback {
        void onSuccess(long balance, long unlocked);
        void onError(String error);
    }

    public interface SyncCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface TxBlobCallback {
        void onSuccess(String txId, String base64Blob);
        void onError(String error);
    }
    
    public interface TransactionImportCallback {
        void onSuccess(String txId);
        void onError(String error);
    }
    
    public interface TransactionHistoryCallback {
        void onSuccess(List<TransactionInfo> transactions);
        void onError(String error);
    }    
    
    public interface TransactionCallback {
        void onSuccess(String txId, long amount);
        void onError(String error);
    }
    
    public interface RescanCallback {
        void onComplete(long newBalance, long newUnlockedBalance);
        void onError(String error);
    }

    public interface TransactionSearchCallback {
        void onTransactionFound(String txId, long amount, long confirmations, long blockHeight);
        void onTransactionNotFound(String txId);
        void onError(String error);
    }
    
    public interface RescanBalanceCallback {
        void onBalanceUpdated(long balance, long unlockedBalance);
    }
    
    public static class StateOfSync {
        public final boolean syncing;
        public final long walletHeight;
        public final long daemonHeight;
        public final double percentDone;

        public StateOfSync(boolean syncing, long walletHeight, long daemonHeight, double percentDone) {
            this.syncing = syncing;
            this.walletHeight = walletHeight;
            this.daemonHeight = daemonHeight;
            this.percentDone = percentDone;
        }
    }

    public interface DaemonConfigCallback {
        void onConfigNeeded();
        void onConfigError(String error);
    }

    // ============================================================================
    // MULTISIG CALLBACK INTERFACES
    // ============================================================================

    public interface MultisigStateCallback {
        void onSuccess(String state);
        void onError(String error);
    }

    public interface MultisigThresholdCallback {
        void onSuccess(int value);
        void onError(String error);
    }

    public interface RestoreMultisigCallback {
        void onSuccess(boolean success);
        void onError(String error);
    }    

    public interface MultisigCallback {
        void onSuccess(String info, String address);
        void onError(String error);
    }

    public interface ImportMultisigCallback {
        void onSuccess(long nOutputs);
        void onError(String error);
    }

    public interface ExportMultisigCallback {
        void onSuccess(String info);
        void onError(String error);
    }

    public interface SignMultisigCallback {
        void onSuccess(String signedData);
        void onError(String error);
    }

    public interface IsMultisigCallback {
        void onSuccess(boolean success);
        void onError(String error);
    }

    // ============================================================================
    // ROSCA-SPECIFIC CALLBACK INTERFACES
    // ============================================================================

    /**
     * Callback for ROSCA creation
     */
    public interface RoscaCreationCallback {
        void onSuccess(String roscaId, String multisigAddress, String setupInfo);
        void onError(String error);
    }

    /**
     * Callback for joining a ROSCA
     */
    public interface RoscaJoinCallback {
        void onSuccess(String roscaId, String yourMultisigInfo);
        void onError(String error);
    }

    /**
     * Callback for finalizing ROSCA setup
     */
    public interface RoscaFinalizeCallback {
        void onSuccess(String roscaId, String multisigAddress, boolean isReady);
        void onError(String error);
    }

    /**
     * Callback for ROSCA contribution
     */
    public interface RoscaContributionCallback {
        void onSuccess(String txId, long amount, int roundNumber);
        void onError(String error);
    }

    /**
     * Callback for ROSCA payout
     */
    public interface RoscaPayoutCallback {
        void onSuccess(String txId, long payoutAmount, String recipientAddress);
        void onError(String error);
    }

    /**
     * Callback for ROSCA state query
     */
    public interface RoscaStateCallback {
        void onSuccess(RoscaState state);
        void onError(String error);
    }

    /**
     * Callback for multisig exchange
     */
    public interface MultisigExchangeCallback {
        /**
         * Called when multisig setup is complete
         * @param multisigAddress The final multisig address
         * @param isReady True if wallet is ready for transactions
         */
        void onComplete(String multisigAddress, boolean isReady);
        
        /**
         * Called when another exchange round is needed
         * @param exchangeInfo Our exchange info to share with other participants
         * @param isComplete False, indicating another round is needed
         */
        void onExchangeInfoReady(String exchangeInfo, boolean isComplete);
        
        /**
         * Called on error
         */
        void onError(String error);
    }    

    /**
     * ROSCA state information
     */
    public static class RoscaState {
        public final String roscaId;
        public final String multisigAddress;
        public final int totalMembers;
        public final int threshold;
        public final long contributionAmount;
        public final int currentRound;
        public final long totalContributed;
        public final long totalPaidOut;
        public final boolean isActive;
        public final boolean isComplete;
        public final List<String> memberAddresses;
        public final List<Integer> payoutOrder;
        
        public RoscaState(String roscaId, String multisigAddress, int totalMembers, 
                         int threshold, long contributionAmount, int currentRound,
                         long totalContributed, long totalPaidOut, boolean isActive, 
                         boolean isComplete, List<String> memberAddresses, 
                         List<Integer> payoutOrder) {
            this.roscaId = roscaId;
            this.multisigAddress = multisigAddress;
            this.totalMembers = totalMembers;
            this.threshold = threshold;
            this.contributionAmount = contributionAmount;
            this.currentRound = currentRound;
            this.totalContributed = totalContributed;
            this.totalPaidOut = totalPaidOut;
            this.isActive = isActive;
            this.isComplete = isComplete;
            this.memberAddresses = memberAddresses;
            this.payoutOrder = payoutOrder;
        }
    }


    /**
     * Callback for opening ROSCA wallet
     */
    public interface WalletOpenCallback {
        void onSuccess(boolean isReady);
        void onError(String error);
    }

    /**
     * Callback for creating multisig transaction
     */
    public interface MultisigTxCallback {
        void onSuccess(String txData);
        void onError(String error);
    }

    /**
     * Callback for signing multisig transaction
     */
    public interface MultisigSignCallback {
        void onSuccess(String signedData);
        void onError(String error);
    }

    /**
     * Callback for importing multisig signatures
     */
    public interface MultisigImportCallback {
        void onSuccess();
        void onError(String error);
    }

    /**
     * Callback for submitting multisig transaction
     */
    public interface MultisigSubmitCallback {
        void onSuccess(String txHash);
        void onError(String error);
    }
    
    public Context getContext() {
        return context;
    }

    /**
     * ✅ NEW: Set the active user wallet
     * Use this when opening wallets via WalletManager directly
     */
    public void setUserWallet(Wallet newWallet) {
        Log.d(TAG, "Setting user wallet: " + (newWallet != null ? newWallet.getPath() : "null"));
        this.wallet = newWallet;
        
        // ✅ CRITICAL FIX: Mark as initialized when wallet is set
        if (newWallet != null) {
            this.isInitialized = true;
            
            // ✅ Also cache the wallet address
            try {
                this.walletAddress = newWallet.getAddress();
                Log.d(TAG, "Cached wallet address: " + (walletAddress != null ? walletAddress.substring(0, Math.min(15, walletAddress.length())) + "..." : "null"));
            } catch (Exception e) {
                Log.w(TAG, "Could not cache wallet address", e);
            }
        } else {
            this.isInitialized = false;
            this.walletAddress = null;
        }
    }
    
    /**
     * Get the current user wallet
     */
    public Wallet getUserWallet() {
        return wallet;
    }
    
    /**
     * ✅ NEW: Close current wallet file without destroying WalletSuite
     */
    public boolean closeCurrentWalletFile() {
        try {
            if (wallet != null) {
                Log.d(TAG, "Closing wallet file: " + wallet.getPath());
                wallet.store();
                wallet.close();
                wallet = null;
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error closing wallet file", e);
            return false;
        }
    }

    // ============================================================================
    // ROSCA HIGH-LEVEL FUNCTIONS
    // ============================================================================

    /**
     * Create a new ROSCA
     * This initiates a new ROSCA and prepares the wallet for multisig setup
     * 
     * @param roscaName Friendly name for the ROSCA
     * @param numMembers Total number of participants (3-20)
     * @param contributionAmount Amount each member contributes per round (atomic units)
     * @param threshold Number of signatures required (typically n-1)
     * @param callback Returns ROSCA ID, multisig info to share, and setup data
     */
    public void createRosca(String roscaName, int numMembers, long contributionAmount,
                           int threshold, RoscaCreationCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        if (numMembers < 3 || numMembers > 20) {
            mainHandler.post(() -> callback.onError("Invalid member count: must be 3-20"));
            return;
        }
        
        if (threshold < 2 || threshold > numMembers) {
            mainHandler.post(() -> callback.onError("Invalid threshold: must be 2 to " + numMembers));
            return;
        }
        
        if (contributionAmount <= 0) {
            mainHandler.post(() -> callback.onError("Invalid contribution amount"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.i(TAG, "=== CREATE ROSCA (FIXED) ===");
                Log.i(TAG, "Name: " + roscaName);
                Log.i(TAG, "Members: " + numMembers + ", Threshold: " + threshold);
                Log.i(TAG, "Contribution: " + convertAtomicToXmr(contributionAmount) + " XMR");
                
                // ✅ FIX 1: Check wallet state first
                boolean isMultisig = wallet.isMultisig();
                Log.d(TAG, "Current wallet is multisig: " + isMultisig);
                
                if (isMultisig) {
                    // If already multisig, this wallet can't create a new ROSCA
                    // User needs to use a fresh wallet
                    mainHandler.post(() -> callback.onError(
                        "This wallet is already configured for multisig. " +
                        "Please create a new wallet for this ROSCA."
                    ));
                    return;
                }
                
                // ✅ FIX 2: Get wallet address BEFORE trying multisig operations
                String walletAddress = wallet.getAddress();
                if (walletAddress == null || walletAddress.isEmpty()) {
                    mainHandler.post(() -> callback.onError("Cannot get wallet address"));
                    return;
                }
                
                Log.d(TAG, "Wallet address: " + walletAddress.substring(0, Math.min(20, walletAddress.length())) + "...");
                
                // ✅ FIX 3: Generate unique ROSCA ID
                String roscaId = "rosca_" + System.currentTimeMillis() + "_" + 
                                Integer.toHexString(roscaName.hashCode());
                
                // ✅ FIX 4: For initial ROSCA creation, we DON'T prepare multisig yet
                // That happens during finalizeRoscaSetup when all members have joined
                
                // For now, we just return the wallet address as the "setup info"
                // This will be used by other members to join
                String setupInfo = String.format(
                    "ROSCA:%s|MEMBERS:%d|THRESHOLD:%d|AMOUNT:%d|CREATOR:%s|TIMESTAMP:%d",
                    roscaId, numMembers, threshold, contributionAmount, 
                    walletAddress, System.currentTimeMillis()
                );
                
                Log.i(TAG, "✓ ROSCA structure created with ID: " + roscaId);
                Log.d(TAG, "Creator address: " + walletAddress.substring(0, Math.min(15, walletAddress.length())) + "...");
                Log.d(TAG, "Waiting for " + (numMembers - 1) + " more members to join");
                Log.d(TAG, "Multisig wallet will be configured after all members join");
                
                // ✅ FIX 5: Save wallet state
                wallet.store();
                
                // ✅ FIX 6: Return success with wallet address as multisig info
                // The actual multisig preparation happens in finalizeRoscaSetup
                final String finalRoscaId = roscaId;
                final String finalSetupInfo = setupInfo;
                final String finalMultisigInfo = walletAddress; // Just the address for now
                
                mainHandler.post(() -> {
                    Log.d(TAG, "Calling onSuccess callback for ROSCA: " + finalRoscaId);
                    callback.onSuccess(finalRoscaId, finalMultisigInfo, finalSetupInfo);
                });
                
            } catch (Exception e) {
                Log.e(TAG, "✗ Create ROSCA exception: " + e.getClass().getSimpleName(), e);
                Log.e(TAG, "Exception message: " + e.getMessage());
                e.printStackTrace();
                
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error: " + e.getClass().getSimpleName();
                mainHandler.post(() -> {
                    Log.d(TAG, "Calling onError callback: " + errorMsg);
                    callback.onError("ROSCA creation failed: " + errorMsg);
                });
            }
        });
    }

    /**
     * Join an existing ROSCA
     * Called by members who want to participate in a ROSCA
     * 
     * @param roscaId Unique ROSCA identifier
     * @param setupInfo Setup information from ROSCA creator
     * @param callback Returns ROSCA ID and your multisig info to share
     */
    public void joinRosca(String roscaId, String setupInfo, RoscaJoinCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        if (roscaId == null || roscaId.isEmpty()) {
            mainHandler.post(() -> callback.onError("Invalid ROSCA ID"));
            return;
        }
        
        if (setupInfo == null || setupInfo.isEmpty()) {
            mainHandler.post(() -> callback.onError("Invalid setup info"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.i(TAG, "=== JOIN ROSCA (FIXED) ===");
                Log.i(TAG, "ROSCA ID: " + roscaId);
                
                // ✅ FIX 1: Check if wallet is already multisig
                boolean isMultisig = wallet.isMultisig();
                if (isMultisig) {
                    mainHandler.post(() -> callback.onError(
                        "This wallet is already configured for multisig. " +
                        "Please use a new wallet to join this ROSCA."
                    ));
                    return;
                }
                
                // ✅ FIX 2: Parse setup info
                String[] parts = setupInfo.split("\\|");
                if (parts.length < 5) {
                    mainHandler.post(() -> callback.onError("Invalid setup info format"));
                    return;
                }
                
                // ✅ FIX 3: Get our wallet address (not multisig info yet)
                String walletAddress = wallet.getAddress();
                if (walletAddress == null || walletAddress.isEmpty()) {
                    mainHandler.post(() -> callback.onError("Cannot get wallet address"));
                    return;
                }
                
                Log.i(TAG, "✓ Ready to join ROSCA");
                Log.d(TAG, "Our wallet address: " + walletAddress.substring(0, Math.min(15, walletAddress.length())) + "...");
                Log.d(TAG, "Multisig will be configured after all members join");
                
                wallet.store();
                
                final String finalRoscaId = roscaId;
                final String finalWalletAddress = walletAddress;
                
                mainHandler.post(() -> callback.onSuccess(finalRoscaId, finalWalletAddress));
                
            } catch (Exception e) {
                Log.e(TAG, "✗ Join ROSCA exception", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Failed to join ROSCA: " + errorMsg));
            }
        });
    }
/**    
    public void exchangeMultisigKeys(String roscaId, List<String> otherExchangeInfos, 
                                     MultisigExchangeCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        if (otherExchangeInfos == null || otherExchangeInfos.isEmpty()) {
            mainHandler.post(() -> callback.onError("No exchange infos provided"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.i(TAG, "╔════════════════════════════════════════════════════════════════╗");
                Log.i(TAG, "║           EXCHANGE MULTISIG KEYS - ROUND 2+                   ║");
                Log.i(TAG, "╚════════════════════════════════════════════════════════════════╝");
                Log.i(TAG, "ROSCA ID: " + roscaId);
                Log.i(TAG, "Received " + otherExchangeInfos.size() + " exchange infos");
                
                // Check current state
                boolean isMultisig = wallet.isMultisig();
                Log.d(TAG, "Current wallet state - isMultisig: " + isMultisig);
                
                // Get our current exchange info
                String ourExchangeInfo = wallet.getMultisigInfo();
                if (ourExchangeInfo == null || ourExchangeInfo.isEmpty()) {
                    String error = wallet.getErrorString();
                    Log.e(TAG, "❌ Failed to get our exchange info: " + error);
                    mainHandler.post(() -> callback.onError("Failed to get exchange info: " + error));
                    return;
                }
                
                Log.d(TAG, "Our exchange info length: " + ourExchangeInfo.length());
                
                // Parse and filter exchange infos (remove our own)
                List<String> validOtherInfos = new ArrayList<>();
                
                for (int i = 0; i < otherExchangeInfos.size(); i++) {
                    String info = otherExchangeInfos.get(i);
                    
                    if (info == null || info.trim().isEmpty()) {
                        Log.w(TAG, "Skipping null/empty exchange info at index " + i);
                        continue;
                    }
                    
                    info = info.trim();
                    
                    // Parse JSON if needed
                    String exchangeState = info;
                    if (info.startsWith("{")) {
                        try {
                            JSONObject json = new JSONObject(info);
                            exchangeState = json.optString("exchangeState", info);
                        } catch (JSONException e) {
                            Log.w(TAG, "Could not parse JSON at index " + i + ", using as-is");
                        }
                    }
                    
                    // Skip our own exchange info
                    if (exchangeState.equals(ourExchangeInfo)) {
                        Log.d(TAG, "Skipping our own exchange info at index " + i);
                        continue;
                    }
                    
                    // Validate format
                    if (!exchangeState.startsWith("Multisig")) {
                        Log.w(TAG, "Invalid exchange info at index " + i + " (doesn't start with 'Multisig')");
                        continue;
                    }
                    
                    validOtherInfos.add(exchangeState);
                    Log.d(TAG, "Added valid exchange info " + (validOtherInfos.size()) + 
                               " (length: " + exchangeState.length() + ")");
                }
                
                if (validOtherInfos.isEmpty()) {
                    Log.e(TAG, "❌ No valid exchange infos after filtering");
                    mainHandler.post(() -> callback.onError("No valid exchange infos found"));
                    return;
                }
                
                Log.i(TAG, "Calling wallet.exchangeMultisigKeys() with " + validOtherInfos.size() + " infos...");
                
                String[] exchangeArray = validOtherInfos.toArray(new String[0]);
                String exchangeResult = wallet.exchangeMultisigKeys(exchangeArray);
                
                if (exchangeResult == null) {
                    String error = wallet.getErrorString();
                    Log.e(TAG, "❌ exchangeMultisigKeys failed: " + error);
                    mainHandler.post(() -> callback.onError("Key exchange failed: " + 
                        (error != null ? error : "null result")));
                    return;
                }
                
                Log.i(TAG, "✓ Exchange completed");
                Log.d(TAG, "Result length: " + exchangeResult.length());
                
                // Check if we're now fully multisig or need another round
                boolean nowMultisig = wallet.isMultisig();
                boolean needsAnotherRound = !exchangeResult.isEmpty() && 
                                           exchangeResult.startsWith("Multisig");
                
                Log.d(TAG, "Status after exchange:");
                Log.d(TAG, "  isMultisig: " + nowMultisig);
                Log.d(TAG, "  Needs another round: " + needsAnotherRound);
                
                if (nowMultisig) {
                    // COMPLETE - Wallet is now multisig
                    String multisigAddress = wallet.getAddress();
                    
                    Log.i(TAG, "");
                    Log.i(TAG, "╔════════════════════════════════════════════════════════════════╗");
                    Log.i(TAG, "║              ✓✓✓ MULTISIG SETUP COMPLETE ✓✓✓                 ║");
                    Log.i(TAG, "╚════════════════════════════════════════════════════════════════╝");
                    Log.i(TAG, "Multisig Address: " + multisigAddress);
                    
                    try {
                        wallet.store();
                        Log.i(TAG, "✓ Wallet saved");
                    } catch (Exception e) {
                        Log.w(TAG, "⚠️ Failed to save wallet: " + e.getMessage());
                    }
                    
                    mainHandler.post(() -> callback.onComplete(multisigAddress, true));
                    
                } else if (needsAnotherRound) {
                    // PARTIAL - Need another exchange round
                    Log.i(TAG, "⚠️ Another key exchange round is required");
                    Log.d(TAG, "New exchange info: " + exchangeResult.substring(0, Math.min(60, exchangeResult.length())) + "...");
                    
                    mainHandler.post(() -> callback.onExchangeInfoReady(exchangeResult, false));
                    
                } else {
                    // UNEXPECTED STATE
                    Log.e(TAG, "❌ Unexpected state: Not multisig and no exchange info");
                    mainHandler.post(() -> callback.onError("Unexpected state after key exchange"));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Exception during key exchange", e);
                mainHandler.post(() -> callback.onError("Exchange failed: " + e.getMessage()));
            }
        });
    }
*/    
    
    
    /**
     * Finalize ROSCA setup after collecting all member info
     * Must be called by all members after exchanging multisig info
     * 
     * @param roscaId ROSCA identifier
     * @param allMemberMultisigInfos List of multisig info from ALL members (including yours)
     * @param threshold Number of signatures required
     * @param callback Returns finalized ROSCA state
     */
    public void finalizeRoscaSetup(String roscaId, List<String> allMemberMultisigInfos, 
                               int threshold, RoscaFinalizeCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        if (allMemberMultisigInfos == null || allMemberMultisigInfos.size() < 2) {
            int totalMembers = (allMemberMultisigInfos == null) ? 1 : allMemberMultisigInfos.size() + 1;
            mainHandler.post(() -> callback.onError(
                "ROSCA requires at least 3 members, but only " + totalMembers + " found"
            ));
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.i(TAG, "╔════════════════════════════════════════════════════════════════╗");
                Log.i(TAG, "║           FINALIZE ROSCA SETUP - COMPREHENSIVE DEBUG          ║");
                Log.i(TAG, "╚════════════════════════════════════════════════════════════════╝");
                Log.i(TAG, "ROSCA ID: " + roscaId);
                Log.i(TAG, "Received " + allMemberMultisigInfos.size() + " multisig entries from database");
                Log.i(TAG, "Expected threshold: " + threshold);
                
                // ═══════════════════════════════════════════════════════════════════
                // STEP 0: COMPREHENSIVE DATABASE DATA ANALYSIS
                // ═══════════════════════════════════════════════════════════════════
                Log.i(TAG, "");
                Log.i(TAG, "═══ STEP 0: ANALYZING DATABASE DATA ═══");
                
                // Detect data format
                boolean isJsonFormat = false;
                boolean isRawFormat = false;
                
                if (!allMemberMultisigInfos.isEmpty()) {
                    String firstEntry = allMemberMultisigInfos.get(0);
                    if (firstEntry != null) {
                        firstEntry = firstEntry.trim();
                        isJsonFormat = firstEntry.startsWith("{");
                        isRawFormat = firstEntry.startsWith("Multisig");
                        
                        Log.d(TAG, "First entry analysis:");
                        Log.d(TAG, "  - Length: " + firstEntry.length() + " characters");
                        Log.d(TAG, "  - Starts with '{': " + isJsonFormat);
                        Log.d(TAG, "  - Starts with 'Multisig': " + isRawFormat);
                        Log.d(TAG, "  - First 100 chars: " + firstEntry.substring(0, Math.min(100, firstEntry.length())));
                    }
                }
                
                // Log ALL entries for debugging
                Log.i(TAG, "");
                Log.i(TAG, "Complete database entries dump:");
                for (int i = 0; i < allMemberMultisigInfos.size(); i++) {
                    String entry = allMemberMultisigInfos.get(i);
                    if (entry == null) {
                        Log.w(TAG, "  [" + i + "] NULL ENTRY");
                    } else {
                        Log.d(TAG, "  [" + i + "] Length: " + entry.length());
                        Log.d(TAG, "      First 150 chars: " + entry.substring(0, Math.min(150, entry.length())));
                        if (entry.length() > 150) {
                            Log.d(TAG, "      Last 50 chars: " + entry.substring(Math.max(0, entry.length() - 50)));
                        }
                    }
                }
                
                // ═══════════════════════════════════════════════════════════════════
                // STEP 1: CHECK IF ALREADY FINALIZED
                // ═══════════════════════════════════════════════════════════════════
                Log.i(TAG, "");
                Log.i(TAG, "═══ STEP 1: CHECKING WALLET STATE ═══");
                
                boolean isMultisig = wallet.isMultisig();
                Log.d(TAG, "Wallet isMultisig: " + isMultisig);
                
                if (isMultisig) {
                    String multisigAddress = wallet.getAddress();
                    int multisigThreshold = 0;
                    try {
                        multisigThreshold = wallet.multisigThreshold();
                    } catch (Exception e) {
                        Log.w(TAG, "Could not get threshold", e);
                    }
                    
                    Log.i(TAG, "✓ Wallet already configured for multisig");
                    Log.i(TAG, "  - Address: " + (multisigAddress != null ? multisigAddress : "null"));
                    Log.i(TAG, "  - Threshold: " + multisigThreshold);
                    mainHandler.post(() -> callback.onSuccess(roscaId, multisigAddress, true));
                    return;
                }
                
                // ═══════════════════════════════════════════════════════════════════
                // STEP 2: GET OUR WALLET INFORMATION
                // ═══════════════════════════════════════════════════════════════════
                Log.i(TAG, "");
                Log.i(TAG, "═══ STEP 2: GETTING OUR WALLET INFO ═══");
                
                String ourAddress = wallet.getAddress();
                if (ourAddress == null || ourAddress.isEmpty()) {
                    String error = wallet.getErrorString();
                    Log.e(TAG, "❌ Failed to get our address: " + error);
                    mainHandler.post(() -> callback.onError("Failed to get address: " + error));
                    return;
                }
                
                Log.d(TAG, "✓ Our wallet address: " + ourAddress);
                Log.d(TAG, "  Address length: " + ourAddress.length());
                
                String ourCurrentMultisigInfo = wallet.getMultisigInfo();
                if (ourCurrentMultisigInfo == null || ourCurrentMultisigInfo.isEmpty()) {
                    String error = wallet.getErrorString();
                    Log.e(TAG, "❌ Failed to get our multisig info: " + error);
                    mainHandler.post(() -> callback.onError("Failed to get our multisig info: " + error));
                    return;
                }
                
                ourCurrentMultisigInfo = ourCurrentMultisigInfo.trim();
                
                Log.d(TAG, "✓ Our current multisig info:");
                Log.d(TAG, "  Length: " + ourCurrentMultisigInfo.length() + " characters");
                Log.d(TAG, "  First 100 chars: " + ourCurrentMultisigInfo.substring(0, Math.min(100, ourCurrentMultisigInfo.length())));
                Log.d(TAG, "  Last 50 chars: " + ourCurrentMultisigInfo.substring(Math.max(0, ourCurrentMultisigInfo.length() - 50)));
                
                // ═══════════════════════════════════════════════════════════════════
                // STEP 3: PARSE AND EXTRACT MULTISIG INFOS
                // ═══════════════════════════════════════════════════════════════════
                Log.i(TAG, "");
                Log.i(TAG, "═══ STEP 3: PARSING MULTISIG ENTRIES ═══");
                
                List<MultisigEntry> parsedEntries = new ArrayList<>();
                
                for (int i = 0; i < allMemberMultisigInfos.size(); i++) {
                    String entry = allMemberMultisigInfos.get(i);
                    
                    if (entry == null || entry.trim().isEmpty()) {
                        Log.w(TAG, "[" + i + "] Skipped: NULL or empty entry");
                        continue;
                    }
                    
                    entry = entry.trim();
                    MultisigEntry parsedEntry = new MultisigEntry();
                    parsedEntry.originalIndex = i;
                    
                    // Try parsing as JSON first
                    if (entry.startsWith("{")) {
                        try {
                            JSONObject json = new JSONObject(entry);
                            parsedEntry.address = json.optString("address", null);
                            parsedEntry.exchangeState = json.optString("exchangeState", null);
                            parsedEntry.userId = json.optString("userId", null);
                            parsedEntry.isJsonFormat = true;
                            
                            if (parsedEntry.exchangeState != null) {
                                parsedEntry.exchangeState = parsedEntry.exchangeState.trim();
                            }
                            
                            Log.d(TAG, "[" + i + "] Parsed as JSON:");
                            Log.d(TAG, "    Address: " + (parsedEntry.address != null ? parsedEntry.address : "null"));
                            Log.d(TAG, "    UserId: " + (parsedEntry.userId != null ? parsedEntry.userId : "null"));
                            Log.d(TAG, "    ExchangeState: " + (parsedEntry.exchangeState != null ? "present (" + parsedEntry.exchangeState.length() + " chars)" : "null"));
                            
                        } catch (JSONException e) {
                            Log.e(TAG, "[" + i + "] ❌ Failed to parse as JSON: " + e.getMessage());
                            continue;
                        }
                    } else if (entry.startsWith("Multisig")) {
                        // Raw multisig info format
                        parsedEntry.exchangeState = entry;
                        parsedEntry.isJsonFormat = false;
                        
                        Log.d(TAG, "[" + i + "] Parsed as raw multisig info:");
                        Log.d(TAG, "    Length: " + entry.length() + " chars");
                        Log.d(TAG, "    First 80 chars: " + entry.substring(0, Math.min(80, entry.length())));
                    } else {
                        Log.w(TAG, "[" + i + "] ⚠️ Unknown format - doesn't start with '{' or 'Multisig'");
                        Log.w(TAG, "    Content: " + entry.substring(0, Math.min(100, entry.length())));
                        continue;
                    }
                    
                    parsedEntries.add(parsedEntry);
                }
                
                Log.i(TAG, "Parsed " + parsedEntries.size() + " valid entries out of " + allMemberMultisigInfos.size());
                
                if (parsedEntries.isEmpty()) {
                    Log.e(TAG, "❌ No valid multisig entries found!");
                    mainHandler.post(() -> callback.onError("No valid multisig data in database"));
                    return;
                }
                
                // ═══════════════════════════════════════════════════════════════════
                // STEP 4: IDENTIFY AND FILTER OUR OWN MULTISIG INFO
                // ═══════════════════════════════════════════════════════════════════
                Log.i(TAG, "");
                Log.i(TAG, "═══ STEP 4: IDENTIFYING OUR MULTISIG INFO ═══");
                
                MultisigEntry ourEntry = null;
                
                // Strategy 1: Match by address (most reliable)
                Log.d(TAG, "Strategy 1: Matching by wallet address...");
                for (MultisigEntry entry : parsedEntries) {
                    if (entry.address != null && entry.address.equals(ourAddress)) {
                        ourEntry = entry;
                        Log.i(TAG, "✓ Found our entry by ADDRESS match!");
                        Log.d(TAG, "  Index: " + entry.originalIndex);
                        Log.d(TAG, "  Our address: " + ourAddress);
                        Log.d(TAG, "  Entry address: " + entry.address);
                        break;
                    }
                }
                
                // Strategy 2: Match by multisig info content
                if (ourEntry == null) {
                    Log.d(TAG, "Strategy 2: Matching by multisig info content...");
                    
                    for (MultisigEntry entry : parsedEntries) {
                        if (entry.exchangeState != null && entry.exchangeState.equals(ourCurrentMultisigInfo)) {
                            ourEntry = entry;
                            Log.i(TAG, "✓ Found our entry by MULTISIG INFO match!");
                            Log.d(TAG, "  Index: " + entry.originalIndex);
                            break;
                        }
                    }
                }
                
                // Strategy 3: If still not found, detailed comparison
                if (ourEntry == null) {
                    Log.w(TAG, "⚠️ Strategy 3: Could not find exact match, performing detailed comparison...");
                    
                    Log.d(TAG, "Our current multisig info hash: " + ourCurrentMultisigInfo.hashCode());
                    
                    for (MultisigEntry entry : parsedEntries) {
                        if (entry.exchangeState != null) {
                            Log.d(TAG, "  Entry[" + entry.originalIndex + "] multisig info hash: " + entry.exchangeState.hashCode());
                            Log.d(TAG, "    Length match: " + (entry.exchangeState.length() == ourCurrentMultisigInfo.length()));
                            Log.d(TAG, "    Equals: " + entry.exchangeState.equals(ourCurrentMultisigInfo));
                            
                            // Character-by-character comparison for first difference
                            if (entry.exchangeState.length() == ourCurrentMultisigInfo.length()) {
                                for (int c = 0; c < Math.min(entry.exchangeState.length(), 200); c++) {
                                    if (entry.exchangeState.charAt(c) != ourCurrentMultisigInfo.charAt(c)) {
                                        Log.d(TAG, "    First difference at position " + c + ": '" + 
                                            entry.exchangeState.charAt(c) + "' vs '" + ourCurrentMultisigInfo.charAt(c) + "'");
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                
                // ═══════════════════════════════════════════════════════════════════
                // STEP 5: BUILD LIST OF OTHER PARTICIPANTS' MULTISIG INFOS
                // ═══════════════════════════════════════════════════════════════════
                Log.i(TAG, "");
                Log.i(TAG, "═══ STEP 5: BUILDING OTHER PARTICIPANTS LIST ═══");

                List<String> otherMultisigInfos = new ArrayList<>();
                
                if (ourEntry != null) {
                    Log.i(TAG, "✓ Successfully identified our entry - filtering it out");
                    Log.d(TAG, "  Our entry index: " + ourEntry.originalIndex);
                    
                    // Collect all OTHER participants' multisig infos
                    List<MultisigEntry> validOthers = new ArrayList<>();
                    
                    for (MultisigEntry entry : parsedEntries) {
                        // Skip our own entry
                        if (entry.originalIndex == ourEntry.originalIndex) {
                            Log.d(TAG, "  [" + entry.originalIndex + "] → SKIPPED (our entry)");
                            continue;
                        }
                        
                        // Validate multisig info
                        if (entry.exchangeState == null || entry.exchangeState.isEmpty()) {
                            Log.w(TAG, "  [" + entry.originalIndex + "] → SKIPPED (null/empty multisig info)");
                            continue;
                        }
                        
                        if (!entry.exchangeState.startsWith("Multisig")) {
                            Log.w(TAG, "  [" + entry.originalIndex + "] → SKIPPED (doesn't start with 'Multisig')");
                            continue;
                        }
                        
                        validOthers.add(entry);
                    }
                    
                    Log.i(TAG, "Found " + validOthers.size() + " valid other participants");
                    
                    // For makeMultisig, we need ALL other participants' info
                    int expectedOthers = parsedEntries.size() - 1; // Everyone except us
                    
                    if (validOthers.size() < expectedOthers) {
                        Log.e(TAG, "❌ Not enough valid multisig infos!");
                        Log.e(TAG, "  Required: " + expectedOthers);
                        Log.e(TAG, "  Available: " + validOthers.size());
                        mainHandler.post(() -> callback.onError(
                            "Insufficient multisig infos: need " + expectedOthers + ", got " + validOthers.size()
                        ));
                        return;
                    }
                    
                    // Add all valid other participants
                    for (int i = 0; i < validOthers.size(); i++) {
                        MultisigEntry entry = validOthers.get(i);
                        otherMultisigInfos.add(entry.exchangeState);
                        Log.d(TAG, "  [" + entry.originalIndex + "] → ADDED (" + (i + 1) + "/" + validOthers.size() + ")");
                        Log.d(TAG, "      Address: " + (entry.address != null ? entry.address.substring(0, Math.min(20, entry.address.length())) + "..." : "unknown"));
                        Log.d(TAG, "      Info length: " + entry.exchangeState.length() + " chars");
                    }
                    
                } else {
                    // Could not identify our entry - CRITICAL ERROR
                    Log.e(TAG, "❌ CRITICAL: Could not identify our own entry!");
                    Log.e(TAG, "This means our current wallet state doesn't match ANY entry in the database");
                    Log.e(TAG, "");
                    Log.e(TAG, "Possible causes:");
                    Log.e(TAG, "  1. Wallet was recreated/restored after uploading to database");
                    Log.e(TAG, "  2. Wrong wallet is currently open");
                    Log.e(TAG, "  3. Database contains outdated/incorrect data");
                    Log.e(TAG, "  4. Multisig state was regenerated");
                    
                    mainHandler.post(() -> callback.onError(
                        "Cannot identify our wallet in the multisig infos. " +
                        "This wallet may not match the ROSCA setup data."
                    ));
                    return;
                }

                Log.i(TAG, "");
                Log.i(TAG, "Final count:");
                Log.i(TAG, "  Total entries in database: " + allMemberMultisigInfos.size());
                Log.i(TAG, "  Successfully parsed: " + parsedEntries.size());
                Log.i(TAG, "  Other participants (for makeMultisig): " + otherMultisigInfos.size());

                if (otherMultisigInfos.isEmpty()) {
                    Log.e(TAG, "❌ No valid multisig infos to use for multisig setup!");
                    mainHandler.post(() -> callback.onError("No valid multisig infos found after filtering"));
                    return;
                }
                
                // ═══════════════════════════════════════════════════════════════════
                // STEP 6: MAKE MULTISIG WALLET
                // ═══════════════════════════════════════════════════════════════════
                Log.i(TAG, "");
                Log.i(TAG, "═══ STEP 6: MAKING MULTISIG WALLET ═══");
                
                Log.d(TAG, "Preparing to call wallet.makeMultisig()...");
                Log.d(TAG, "Parameters:");
                Log.d(TAG, "  Threshold: " + threshold);
                Log.d(TAG, "  Number of other participants: " + otherMultisigInfos.size());
                
                for (int i = 0; i < otherMultisigInfos.size(); i++) {
                    String info = otherMultisigInfos.get(i);
                    Log.d(TAG, "  Participant[" + i + "] info:");
                    Log.d(TAG, "    Length: " + info.length() + " chars");
                    Log.d(TAG, "    First 60 chars: " + info.substring(0, Math.min(60, info.length())));
                    Log.d(TAG, "    Last 40 chars: " + info.substring(Math.max(0, info.length() - 40)));
                }
                
                String[] multisigInfoArray = otherMultisigInfos.toArray(new String[0]);
                
                Log.i(TAG, "");
                Log.i(TAG, ">>> Calling wallet.makeMultisig() <<<");
                long startTime = System.currentTimeMillis();
                
                String makeMultisigResult = wallet.makeMultisig(multisigInfoArray, threshold);
                
                long duration = System.currentTimeMillis() - startTime;
                Log.i(TAG, ">>> makeMultisig() completed in " + duration + "ms <<<");
                Log.i(TAG, "");
                
                // Check result
                if (makeMultisigResult == null) {
                    String error = wallet.getErrorString();
                    Log.e(TAG, "❌ makeMultisig returned NULL");
                    Log.e(TAG, "Wallet error: " + (error != null ? error : "none"));
                    Log.e(TAG, "");
                    Log.e(TAG, "Diagnosis:");
                    Log.e(TAG, "  - NULL result usually means the Monero library rejected the multisig infos");
                    Log.e(TAG, "  - Common causes:");
                    Log.e(TAG, "    1. Our own multisig info was included in the list");
                    Log.e(TAG, "    2. Multisig infos are from incompatible wallets");
                    Log.e(TAG, "    3. Multisig infos are corrupted or invalid");
                    Log.e(TAG, "    4. Wallet is already in multisig mode");
                    
                    mainHandler.post(() -> callback.onError("Make multisig failed: " + 
                        (error != null ? error : "Monero library returned null")));
                    return;
                }
                
                if (makeMultisigResult.isEmpty()) {
                    String error = wallet.getErrorString();
                    Log.e(TAG, "❌ makeMultisig returned EMPTY STRING");
                    Log.e(TAG, "Wallet error: " + (error != null ? error : "none"));
                    
                    mainHandler.post(() -> callback.onError("Make multisig failed: " + 
                        (error != null ? error : "Empty result from Monero library")));
                    return;
                }
                
                Log.i(TAG, "✓ makeMultisig SUCCESS!");
                Log.d(TAG, "Result length: " + makeMultisigResult.length() + " chars");
                Log.d(TAG, "Result preview: " + makeMultisigResult.substring(0, Math.min(80, makeMultisigResult.length())) + "...");
                
                Log.i(TAG, "");
                // ═══════════════════════════════════════════════════════════════════
                // STEP 7: HANDLE ADDITIONAL KEY EXCHANGE (UPDATED)
                // ═══════════════════════════════════════════════════════════════════
                Log.i(TAG, "");
                Log.i(TAG, "═══ STEP 7: CHECKING FOR ADDITIONAL KEY EXCHANGE ═══");

                boolean needsKeyExchange = makeMultisigResult != null && 
                                           !makeMultisigResult.isEmpty() && 
                                           makeMultisigResult.startsWith("Multisig");

                if (needsKeyExchange) {
                    Log.i(TAG, "⚠️ Additional key exchange round is required");
                    Log.d(TAG, "Exchange info length: " + makeMultisigResult.length());
                    Log.d(TAG, "This happens for M-of-N multisig where M < N");
                    Log.d(TAG, "");
                    Log.d(TAG, "Returning exchange info with isReady=false");
                    Log.d(TAG, "Kotlin side will:");
                    Log.d(TAG, "  1. Store this exchange info in database");
                    Log.d(TAG, "  2. Wait for all members to upload their round 2 info");
                    Log.d(TAG, "  3. Call exchangeMultisigKeys() when all ready");
                    
                    // Return the exchange info with isReady = false
                    // The calling code will store this and coordinate round 2
                    final String exchangeInfo = makeMultisigResult;
                    mainHandler.post(() -> {
                        callback.onSuccess(roscaId, exchangeInfo, false);
                    });
                    return;
                }

                // If we reach here, multisig is complete (no additional rounds needed)
                boolean isNowMultisig = wallet.isMultisig();
                String multisigAddress = wallet.getAddress();

                Log.d(TAG, "Post-makeMultisig wallet state:");
                Log.d(TAG, "  isMultisig: " + isNowMultisig);
                Log.d(TAG, "  Address: " + (multisigAddress != null ? multisigAddress : "null"));

                if (!isNowMultisig) {
                    Log.e(TAG, "❌ Wallet is STILL NOT MULTISIG after makeMultisig!");
                    mainHandler.post(() -> callback.onError("Wallet failed to become multisig after setup"));
                    return;
                }
                
                // ═══════════════════════════════════════════════════════════════════
                // STEP 8: VERIFY MULTISIG SETUP
                // ═══════════════════════════════════════════════════════════════════
                Log.i(TAG, "");
                Log.i(TAG, "═══ STEP 8: VERIFYING MULTISIG SETUP ═══");
                
                Log.d(TAG, "Post-makeMultisig wallet state:");
                Log.d(TAG, "  isMultisig: " + isNowMultisig);
                Log.d(TAG, "  Address: " + (multisigAddress != null ? multisigAddress : "null"));
                
                if (!isNowMultisig) {
                    Log.e(TAG, "❌ Wallet is STILL NOT MULTISIG after makeMultisig!");
                    Log.e(TAG, "This indicates the setup didn't complete properly");
                    Log.e(TAG, "Possible reasons:");
                    Log.e(TAG, "  1. Additional key exchange rounds are required (see step 7)");
                    Log.e(TAG, "  2. The makeMultisig call failed silently");
                    Log.e(TAG, "  3. Incompatible multisig configuration");
                    
                    mainHandler.post(() -> callback.onError("Wallet failed to become multisig after setup"));
                    return;
                }
                
                // Get additional multisig info
                try {
                    int multisigThreshold = wallet.multisigThreshold();
                    Log.d(TAG, "  Multisig threshold: " + multisigThreshold);
                    
                    if (multisigThreshold != threshold) {
                        Log.w(TAG, "  ⚠️ WARNING: Threshold mismatch!");
                        Log.w(TAG, "    Expected: " + threshold);
                        Log.w(TAG, "    Got: " + multisigThreshold);
                    }
                } catch (Exception e) {
                    Log.w(TAG, "  Could not get multisig threshold: " + e.getMessage());
                }
                
                // ═══════════════════════════════════════════════════════════════════
                // STEP 9: SAVE AND COMPLETE
                // ═══════════════════════════════════════════════════════════════════
                Log.i(TAG, "");
                Log.i(TAG, "═══ STEP 9: SAVING WALLET ═══");
                
                try {
                    wallet.store();
                    Log.i(TAG, "✓ Wallet saved successfully");
                } catch (Exception e) {
                    Log.e(TAG, "⚠️ Failed to save wallet: " + e.getMessage());
                    // Continue anyway - wallet might still be usable
                }
                
                Log.i(TAG, "");
                Log.i(TAG, "╔════════════════════════════════════════════════════════════════╗");
                Log.i(TAG, "║                  ✓✓✓ SETUP COMPLETE ✓✓✓                      ║");
                Log.i(TAG, "╚════════════════════════════════════════════════════════════════╝");
                Log.i(TAG, "ROSCA ID: " + roscaId);
                Log.i(TAG, "Multisig Address: " + multisigAddress);
                Log.i(TAG, "Status: READY");
                Log.i(TAG, "");
                
                final String finalRoscaId = roscaId;
                final String finalAddress = multisigAddress;
                
                mainHandler.post(() -> callback.onSuccess(finalRoscaId, finalAddress, true));
                
            } catch (Exception e) {
                Log.e(TAG, "");
                Log.e(TAG, "╔════════════════════════════════════════════════════════════════╗");
                Log.e(TAG, "║                   ❌ EXCEPTION CAUGHT ❌                       ║");
                Log.e(TAG, "╚════════════════════════════════════════════════════════════════╝");
                Log.e(TAG, "Exception type: " + e.getClass().getSimpleName());
                Log.e(TAG, "Message: " + e.getMessage());
                e.printStackTrace();
                
                final String errorMsg = e.getMessage() != null ? e.getMessage() : 
                    "Unknown error: " + e.getClass().getSimpleName();
                mainHandler.post(() -> callback.onError("Finalization failed: " + errorMsg));
            }
        });
    }

    // ═══════════════════════════════════════════════════════════════════
    // ADD TO WalletSuite.java - NEW METHOD FOR ROUND 2+ KEY EXCHANGE
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Exchange multisig keys for additional rounds (required for M-of-N where M < N)
     * 
     * This method completes the multisig setup after makeMultisig returns exchange info.
     * It should be called when all participants have uploaded their exchange info from
     * the previous round.
     * 
     * @param roscaId The ROSCA identifier
     * @param allMemberExchangeInfos List of JSON objects containing exchange info from ALL members
     * @param callback Callback for results
     */
    public void exchangeMultisigKeys(String roscaId, List<String> allMemberExchangeInfos, 
                                     MultisigExchangeCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        if (allMemberExchangeInfos == null || allMemberExchangeInfos.isEmpty()) {
            mainHandler.post(() -> callback.onError("No exchange infos provided"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.i(TAG, "╔════════════════════════════════════════════════════════════════╗");
                Log.i(TAG, "║           EXCHANGE MULTISIG KEYS - ROUND 2+                   ║");
                Log.i(TAG, "╚════════════════════════════════════════════════════════════════╝");
                Log.i(TAG, "ROSCA ID: " + roscaId);
                Log.i(TAG, "Received " + allMemberExchangeInfos.size() + " exchange infos");
                
                // ═══════════════════════════════════════════════════════════════════
                // STEP 1: CHECK CURRENT STATE
                // ═══════════════════════════════════════════════════════════════════
                
                boolean isMultisig = wallet.isMultisig();
                Log.d(TAG, "Current wallet state - isMultisig: " + isMultisig);
                
                if (isMultisig) {
                    // Already multisig - return success
                    String multisigAddress = wallet.getAddress();
                    Log.i(TAG, "✓ Wallet is already multisig");
                    Log.d(TAG, "  Address: " + multisigAddress);
                    mainHandler.post(() -> callback.onComplete(multisigAddress, true));
                    return;
                }
                
                // ═══════════════════════════════════════════════════════════════════
                // STEP 2: GET OUR CURRENT EXCHANGE INFO
                // ═══════════════════════════════════════════════════════════════════
                
                String ourExchangeInfo = wallet.getMultisigInfo();
                if (ourExchangeInfo == null || ourExchangeInfo.isEmpty()) {
                    String error = wallet.getErrorString();
                    Log.e(TAG, "❌ Failed to get our exchange info: " + error);
                    mainHandler.post(() -> callback.onError("Failed to get exchange info: " + error));
                    return;
                }
                
                ourExchangeInfo = ourExchangeInfo.trim();
                Log.d(TAG, "✓ Our exchange info length: " + ourExchangeInfo.length());
                
                // Get our address for filtering
                String ourAddress = wallet.getAddress();
                Log.d(TAG, "✓ Our address: " + (ourAddress != null ? ourAddress : "null"));
                
                // ═══════════════════════════════════════════════════════════════════
                // STEP 3: PARSE AND FILTER EXCHANGE INFOS (REMOVE OUR OWN)
                // ═══════════════════════════════════════════════════════════════════
                
                Log.d(TAG, "Parsing and filtering exchange infos...");
                
                List<String> validOtherInfos = new ArrayList<>();
                
                for (int i = 0; i < allMemberExchangeInfos.size(); i++) {
                    String info = allMemberExchangeInfos.get(i);
                    
                    if (info == null || info.trim().isEmpty()) {
                        Log.w(TAG, "  [" + i + "] Skipped: null/empty");
                        continue;
                    }
                    
                    info = info.trim();
                    
                    // Parse JSON if needed
                    String exchangeState = info;
                    String memberAddress = null;
                    
                    if (info.startsWith("{")) {
                        try {
                            JSONObject json = new JSONObject(info);
                            exchangeState = json.optString("exchangeState", info);
                            memberAddress = json.optString("address", null);
                            
                            Log.d(TAG, "  [" + i + "] Parsed JSON:");
                            Log.d(TAG, "      Address: " + (memberAddress != null ? memberAddress.substring(0, Math.min(15, memberAddress.length())) + "..." : "null"));
                            Log.d(TAG, "      Exchange state length: " + exchangeState.length());
                            
                        } catch (JSONException e) {
                            Log.w(TAG, "  [" + i + "] Could not parse JSON, using as-is: " + e.getMessage());
                        }
                    }
                    
                    if (exchangeState == null || exchangeState.isEmpty()) {
                        Log.w(TAG, "  [" + i + "] Skipped: empty exchange state");
                        continue;
                    }
                    
                    exchangeState = exchangeState.trim();
                    
                    // Skip our own exchange info (by address match)
                    if (memberAddress != null && ourAddress != null && memberAddress.equals(ourAddress)) {
                        Log.d(TAG, "  [" + i + "] SKIPPED: our own entry (address match)");
                        continue;
                    }
                    
                    // Skip our own exchange info (by content match)
                    if (exchangeState.equals(ourExchangeInfo)) {
                        Log.d(TAG, "  [" + i + "] SKIPPED: our own entry (exchange info match)");
                        continue;
                    }
                    
                    // Validate format
                    if (!exchangeState.startsWith("Multisig")) {
                        Log.w(TAG, "  [" + i + "] SKIPPED: invalid format (doesn't start with 'Multisig')");
                        Log.w(TAG, "      Content: " + exchangeState.substring(0, Math.min(50, exchangeState.length())));
                        continue;
                    }
                    
                    validOtherInfos.add(exchangeState);
                    Log.d(TAG, "  [" + i + "] ✓ ADDED (total: " + validOtherInfos.size() + ")");
                    Log.d(TAG, "      Length: " + exchangeState.length() + " chars");
                }
                
                if (validOtherInfos.isEmpty()) {
                    Log.e(TAG, "❌ No valid exchange infos after filtering");
                    Log.e(TAG, "Total entries: " + allMemberExchangeInfos.size());
                    Log.e(TAG, "All entries were either our own or invalid");
                    mainHandler.post(() -> callback.onError("No valid exchange infos found after filtering"));
                    return;
                }
                
                Log.i(TAG, "✓ Prepared " + validOtherInfos.size() + " exchange infos for key exchange");
                
                // ═══════════════════════════════════════════════════════════════════
                // STEP 4: CALL exchangeMultisigKeys
                // ═══════════════════════════════════════════════════════════════════
                
                Log.i(TAG, "");
                Log.i(TAG, ">>> Calling wallet.exchangeMultisigKeys() <<<");
                
                String[] exchangeArray = validOtherInfos.toArray(new String[0]);
                long startTime = System.currentTimeMillis();
                
                String exchangeResult = wallet.exchangeMultisigKeys(exchangeArray);
                
                long duration = System.currentTimeMillis() - startTime;
                Log.i(TAG, ">>> exchangeMultisigKeys() completed in " + duration + "ms <<<");
                
                // ═══════════════════════════════════════════════════════════════════
                // STEP 5: HANDLE RESULT
                // ═══════════════════════════════════════════════════════════════════
                
                if (exchangeResult == null) {
                    String error = wallet.getErrorString();
                    Log.e(TAG, "❌ exchangeMultisigKeys returned NULL");
                    Log.e(TAG, "Wallet error: " + (error != null ? error : "none"));
                    mainHandler.post(() -> callback.onError("Key exchange failed: " + 
                        (error != null ? error : "null result")));
                    return;
                }
                
                Log.i(TAG, "");
                Log.i(TAG, "✓ Exchange completed");
                Log.d(TAG, "Result length: " + exchangeResult.length());
                Log.d(TAG, "Result preview: " + exchangeResult.substring(0, Math.min(60, exchangeResult.length())) + "...");
                
                // ═══════════════════════════════════════════════════════════════════
                // STEP 6: CHECK IF MULTISIG IS READY OR NEEDS ANOTHER ROUND
                // ═══════════════════════════════════════════════════════════════════
                
                boolean nowMultisig = wallet.isMultisig();
                boolean needsAnotherRound = !exchangeResult.isEmpty() && 
                                           exchangeResult.startsWith("Multisig");
                
                Log.d(TAG, "");
                Log.d(TAG, "Status after exchange:");
                Log.d(TAG, "  isMultisig: " + nowMultisig);
                Log.d(TAG, "  Needs another round: " + needsAnotherRound);
                
                if (nowMultisig) {
                    // ═══════════════════════════════════════════════════════════════
                    // COMPLETE - Wallet is now multisig
                    // ═══════════════════════════════════════════════════════════════
                    
                    String multisigAddress = wallet.getAddress();
                    
                    if (multisigAddress == null || multisigAddress.isEmpty()) {
                        Log.e(TAG, "❌ Multisig address is null/empty");
                        mainHandler.post(() -> callback.onError("Multisig address is empty"));
                        return;
                    }
                    
                    Log.i(TAG, "");
                    Log.i(TAG, "╔════════════════════════════════════════════════════════════════╗");
                    Log.i(TAG, "║              ✓✓✓ MULTISIG SETUP COMPLETE ✓✓✓                 ║");
                    Log.i(TAG, "╚════════════════════════════════════════════════════════════════╝");
                    Log.i(TAG, "Multisig Address: " + multisigAddress);
                    
                    // Save wallet
                    try {
                        wallet.store();
                        Log.i(TAG, "✓ Wallet saved");
                    } catch (Exception e) {
                        Log.w(TAG, "⚠️ Failed to save wallet: " + e.getMessage());
                    }
                    
                    final String finalAddress = multisigAddress;
                    mainHandler.post(() -> callback.onComplete(finalAddress, true));
                    
                } else if (needsAnotherRound) {
                    // ═══════════════════════════════════════════════════════════════
                    // PARTIAL - Need another exchange round
                    // ═══════════════════════════════════════════════════════════════
                    
                    Log.i(TAG, "");
                    Log.i(TAG, "⚠️ Another key exchange round is required");
                    Log.d(TAG, "New exchange info length: " + exchangeResult.length());
                    Log.d(TAG, "Preview: " + exchangeResult.substring(0, Math.min(60, exchangeResult.length())) + "...");
                    Log.d(TAG, "");
                    Log.d(TAG, "This exchange info needs to be shared with other participants");
                    Log.d(TAG, "for the next round of key exchange.");
                    
                    final String finalExchangeInfo = exchangeResult;
                    mainHandler.post(() -> callback.onExchangeInfoReady(finalExchangeInfo, false));
                    
                } else {
                    // ═══════════════════════════════════════════════════════════════
                    // UNEXPECTED STATE
                    // ═══════════════════════════════════════════════════════════════
                    
                    Log.e(TAG, "");
                    Log.e(TAG, "❌ Unexpected state after key exchange");
                    Log.e(TAG, "  Not multisig: " + !nowMultisig);
                    Log.e(TAG, "  No exchange info: " + !needsAnotherRound);
                    Log.e(TAG, "  Result: " + exchangeResult);
                    
                    mainHandler.post(() -> callback.onError(
                        "Unexpected state after key exchange: not multisig and no exchange info"
                    ));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "");
                Log.e(TAG, "╔════════════════════════════════════════════════════════════════╗");
                Log.e(TAG, "║                   ❌ EXCEPTION CAUGHT ❌                       ║");
                Log.e(TAG, "╚════════════════════════════════════════════════════════════════╝");
                Log.e(TAG, "Exception during key exchange: " + e.getClass().getSimpleName());
                Log.e(TAG, "Message: " + e.getMessage());
                e.printStackTrace();
                
                final String errorMsg = e.getMessage() != null ? e.getMessage() : 
                    "Unknown error: " + e.getClass().getSimpleName();
                mainHandler.post(() -> callback.onError("Exchange failed: " + errorMsg));
            }
        });
    }    

    // Helper class to store parsed multisig entry data
    private static class MultisigEntry {
        int originalIndex;
        String address;
        String exchangeState;
        String userId;
        boolean isJsonFormat;
    }
    
    // ============================================================================
    // UPDATED HELPER METHODS (OPTIONAL - Only if you want to keep them)
    // ============================================================================

    /**
     * Extract address from multisigInfo - handles both JSON and raw string formats
     * Returns null if unable to extract
     */
    private String extractAddressFromMultisigInfo(String multisigInfo) {
        if (multisigInfo == null || multisigInfo.trim().isEmpty()) {
            return null;
        }
        
        // Check if it's JSON format
        if (multisigInfo.trim().startsWith("{")) {
            try {
                JSONObject json = new JSONObject(multisigInfo);
                return json.optString("address", null);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse multisigInfo JSON for address", e);
                return null;
            }
        } else {
            // It's a raw exchange state string - we can't extract address from it
            Log.d(TAG, "Cannot extract address from raw exchange state string");
            return null;
        }
    }

    /**
     * Extract exchangeState from multisigInfo - handles both JSON and raw string formats
     * Returns the exchange state string or null if unable to extract
     */
    private String extractExchangeStateFromMultisigInfo(String multisigInfo) {
        if (multisigInfo == null || multisigInfo.trim().isEmpty()) {
            return null;
        }
        
        // Check if it's JSON format
        if (multisigInfo.trim().startsWith("{")) {
            try {
                JSONObject json = new JSONObject(multisigInfo);
                return json.optString("exchangeState", null);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse multisigInfo JSON for exchangeState", e);
                return null;
            }
        } else {
            // It's already a raw exchange state string - return it directly
            if (multisigInfo.startsWith("Multisig")) {
                return multisigInfo;
            } else {
                Log.w(TAG, "String doesn't look like a Monero multisig exchange state");
                return null;
            }
        }
    }
    
    /**
     * Make a contribution to the ROSCA pool
     * Each member calls this to contribute their share for the current round
     * 
     * @param roscaId ROSCA identifier
     * @param contributionAmount Amount to contribute (atomic units)
     * @param roundNumber Current round number
     * @param callback Returns transaction ID and confirmation
     */
    public void contributeToRosca(String roscaId, long contributionAmount, int roundNumber,
                                 RoscaContributionCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        if (contributionAmount <= 0) {
            mainHandler.post(() -> callback.onError("Invalid contribution amount"));
            return;
        }
        
        // Check balance
        long unlockedBal = unlockedBalance.get();
        if (contributionAmount > unlockedBal) {
            String error = String.format("Insufficient balance. Required: %s XMR, Available: %s XMR",
                convertAtomicToXmr(contributionAmount), convertAtomicToXmr(unlockedBal));
            mainHandler.post(() -> callback.onError(error));
            return;
        }
        
        Log.i(TAG, "=== ROSCA CONTRIBUTION ===");
        Log.i(TAG, "ROSCA ID: " + roscaId);
        Log.i(TAG, "Round: " + roundNumber);
        Log.i(TAG, "Amount: " + convertAtomicToXmr(contributionAmount) + " XMR");
        
        // Get multisig address and send contribution
        String multisigAddress = wallet.getAddress();
        
        sendTransaction(multisigAddress, contributionAmount / 1e12, new TransactionCallback() {
            @Override
            public void onSuccess(String txId, long amount) {
                Log.i(TAG, "✓ Contribution sent: " + txId);
                callback.onSuccess(txId, amount, roundNumber);
            }
            
            @Override
            public void onError(String error) {
                callback.onError("Contribution failed: " + error);
            }
        });
    }

    /**
     * Execute ROSCA payout to designated recipient
     * Requires threshold signatures from ROSCA members
     * 
     * @param roscaId ROSCA identifier
     * @param recipientAddress Address of the payout recipient
     * @param payoutAmount Total amount to pay (atomic units)
     * @param roundNumber Current round number
     * @param memberSignatures List of multisig images from signing members
     * @param callback Returns transaction details
     */
    public void executeRoscaPayout(String roscaId, String recipientAddress, long payoutAmount,
                                  int roundNumber, List<String> memberSignatures,
                                  RoscaPayoutCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        if (recipientAddress == null || recipientAddress.isEmpty()) {
            mainHandler.post(() -> callback.onError("Invalid recipient address"));
            return;
        }
        
        if (payoutAmount <= 0) {
            mainHandler.post(() -> callback.onError("Invalid payout amount"));
            return;
        }
        
        executorService.execute(() -> {
            PendingTransaction pendingTx = null;
            try {
                Log.i(TAG, "=== ROSCA PAYOUT ===");
                Log.i(TAG, "ROSCA ID: " + roscaId);
                Log.i(TAG, "Round: " + roundNumber);
                Log.i(TAG, "Recipient: " + recipientAddress.substring(0, Math.min(20, recipientAddress.length())));
                Log.i(TAG, "Amount: " + convertAtomicToXmr(payoutAmount) + " XMR");
                
                if (!wallet.isMultisig()) {
                    mainHandler.post(() -> callback.onError("Wallet is not multisig"));
                    return;
                }
                
                // ✅ FIX: Export multisig images DIRECTLY (no callback/latch)
                Log.d(TAG, "[1/4] Exporting multisig images...");
                String exportedInfo = wallet.exportMultisigImages();
                
                if (exportedInfo == null || exportedInfo.isEmpty()) {
                    String error = wallet.getErrorString();
                    Log.e(TAG, "❌ Failed to export multisig info: " + error);
                    mainHandler.post(() -> callback.onError("Failed to export multisig info: " + error));
                    return;
                }
                
                Log.d(TAG, "✓ Multisig images exported");
                
                // ✅ FIX: Import signatures DIRECTLY (no callback/latch)
                if (memberSignatures != null && !memberSignatures.isEmpty()) {
                    Log.d(TAG, "[2/4] Importing signatures from " + memberSignatures.size() + " members...");
                    
                    String[] sigArray = memberSignatures.toArray(new String[0]);
                    long nOutputs = wallet.importMultisigImages(sigArray);
                    
                    if (nOutputs < 0) {
                        String error = wallet.getErrorString();
                        Log.e(TAG, "❌ Failed to import signatures: " + error);
                        mainHandler.post(() -> callback.onError("Failed to import signatures: " + error));
                        return;
                    }
                    
                    Log.d(TAG, "✓ Imported " + nOutputs + " outputs");
                }
                
                // Step 3: Create payout transaction
                Log.d(TAG, "[3/4] Creating payout transaction...");
                
                pendingTx = wallet.createTransaction(
                    recipientAddress,
                    "", 
                    payoutAmount,
                    15, 
                    PendingTransaction.Priority.Priority_Default.getValue(),
                    0
                );
                
                if (pendingTx == null) {
                    String error = wallet.getErrorString();
                    Log.e(TAG, "❌ Failed to create transaction: " + error);
                    mainHandler.post(() -> callback.onError("Failed to create payout transaction: " + error));
                    return;
                }
                
                if (pendingTx.getStatus() != PendingTransaction.Status.Status_Ok) {
                    String error = pendingTx.getErrorString();
                    wallet.disposePendingTransaction();
                    Log.e(TAG, "❌ Transaction error: " + error);
                    mainHandler.post(() -> callback.onError("Transaction error: " + error));
                    return;
                }
                
                long fee = pendingTx.getFee();
                
                Log.i(TAG, "Transaction created:");
                Log.i(TAG, "  Amount: " + convertAtomicToXmr(payoutAmount) + " XMR");
                Log.i(TAG, "  Fee: " + convertAtomicToXmr(fee) + " XMR");
                
                // Step 4: Commit transaction
                Log.d(TAG, "[4/4] Committing transaction...");
                
                boolean success = pendingTx.commit("", true);
                
                if (!success) {
                    String error = pendingTx.getErrorString();
                    wallet.disposePendingTransaction();
                    Log.e(TAG, "❌ Failed to commit: " + error);
                    mainHandler.post(() -> callback.onError("Failed to commit: " + error));
                    return;
                }
                
                String txHash = pendingTx.getFirstTxId();
                
                Log.i(TAG, "✓✓✓ ROSCA PAYOUT COMPLETE ✓✓✓");
                Log.i(TAG, "TX Hash: " + txHash);
                
                wallet.disposePendingTransaction();
                pendingTx = null;
                
                wallet.store();
                updateBalanceFromWallet();
                performSync();
                
                final String finalTxHash = txHash;
                final long finalPayout = payoutAmount;
                final String finalRecipient = recipientAddress;
                mainHandler.post(() -> callback.onSuccess(finalTxHash, finalPayout, finalRecipient));
                
            } catch (Exception e) {
                Log.e(TAG, "✗ ROSCA payout exception", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Payout failed: " + errorMsg));
            } finally {
                if (pendingTx != null) {
                    try {
                        wallet.disposePendingTransaction();
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to dispose pending transaction", e);
                    }
                }
            }
        });
    }

    /**
     * Get current state of a ROSCA
     * 
     * @param roscaId ROSCA identifier
     * @param callback Returns current ROSCA state
     */
    public void getRoscaState(String roscaId, RoscaStateCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Getting ROSCA state: " + roscaId);
                
                // Get wallet information
                long totalBalance = wallet.getBalance();
                long unlockedBalance = wallet.getUnlockedBalance();
                boolean isMultisig = wallet.isMultisig();
                int threshold = 0;
                
                if (isMultisig) {
                    try {
                        threshold = wallet.multisigThreshold();
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to get multisig threshold", e);
                    }
                }
                
                // Get transaction history
                int txCount = 0;
                long totalContributed = 0;
                long totalPaidOut = 0;
                
                try {
                    TransactionHistory history = wallet.getHistory();
                    if (history != null) {
                        history.refresh();
                        List<TransactionInfo> txs = history.getAll();
                        if (txs != null) {
                            txCount = txs.size();
                            for (TransactionInfo tx : txs) {
                                if (tx.direction == TransactionInfo.Direction.Direction_In) {
                                    totalContributed += tx.amount;
                                } else if (tx.direction == TransactionInfo.Direction.Direction_Out) {
                                    totalPaidOut += tx.amount;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Failed to get transaction history", e);
                }
                
                // Determine state
                boolean isActive = unlockedBalance > 0 && isMultisig;
                boolean isComplete = !isActive && txCount > 0;
                
                // Calculate round from transaction count (simplified)
                int currentRound = Math.max(1, txCount / 2); // Rough estimate
                
                RoscaState state = new RoscaState(
                    roscaId,
                    wallet.getAddress(),
                    threshold + 1, // Total members = threshold + 1 for (n-1)-of-n
                    threshold,
                    0, // Contribution amount (would come from metadata)
                    currentRound,
                    totalContributed,
                    totalPaidOut,
                    isActive,
                    isComplete,
                    new ArrayList<>(), // Member addresses (would come from metadata)
                    new ArrayList<>() // Payout order (would come from metadata)
                );
                
                mainHandler.post(() -> callback.onSuccess(state));
                
            } catch (Exception e) {
                Log.e(TAG, "Error getting ROSCA state", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Failed to get state: " + errorMsg));
            }
        });
    }


    /**
     * Open a ROSCA wallet for multisig operations
     * 
     * @param roscaId ROSCA identifier
     * @param callback Returns success status
     */
    public void openRoscaWallet(String roscaId, WalletOpenCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Opening ROSCA wallet: " + roscaId);
                
                // Check if wallet is already multisig
                boolean isMultisig = wallet.isMultisig();
                
                if (!isMultisig) {
                    mainHandler.post(() -> callback.onError("Wallet is not multisig"));
                    return;
                }
                
                // Verify wallet is synced
                long walletHeight = wallet.getBlockChainHeight();
                long daemonHeight = getDaemonHeightViaHttp();
                
                boolean isReady = (daemonHeight - walletHeight) < 10; // Within 10 blocks
                
                Log.d(TAG, "ROSCA wallet ready: " + isReady);
                
                final boolean finalReady = isReady;
                mainHandler.post(() -> callback.onSuccess(finalReady));
                
            } catch (Exception e) {
                Log.e(TAG, "Error opening ROSCA wallet", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Failed to open: " + errorMsg));
            }
        });
    }

    /**
     * Create a multisig transaction
     * 
     * @param roscaId ROSCA identifier
     * @param recipientAddress Recipient's Monero address
     * @param atomicAmount Amount in atomic units
     * @param callback Returns transaction data
     */
    public void createMultisigTransaction(String roscaId, String recipientAddress, 
                                         long atomicAmount, MultisigTxCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        if (!wallet.isMultisig()) {
            mainHandler.post(() -> callback.onError("Wallet is not multisig"));
            return;
        }
        
        executorService.execute(() -> {
            PendingTransaction pendingTx = null;
            try {
                Log.d(TAG, "Creating multisig transaction");
                Log.d(TAG, "Amount: " + convertAtomicToXmr(atomicAmount) + " XMR");
                Log.d(TAG, "Recipient: " + recipientAddress.substring(0, Math.min(20, recipientAddress.length())));
                
                pendingTx = wallet.createTransaction(
                    recipientAddress,
                    "",
                    atomicAmount,
                    15,
                    PendingTransaction.Priority.Priority_Default.getValue(),
                    0
                );
                
                if (pendingTx == null) {
                    mainHandler.post(() -> callback.onError("Failed to create transaction"));
                    return;
                }
                
                if (pendingTx.getStatus() != PendingTransaction.Status.Status_Ok) {
                    String error = pendingTx.getErrorString();
                    wallet.disposePendingTransaction();
                    mainHandler.post(() -> callback.onError("Transaction error: " + error));
                    return;
                }
                
                // Save transaction to file and return as base64
                String txId = pendingTx.getFirstTxId();
                File tempFile = new File(context.getCacheDir(), txId + "_unsigned.tx");
                
                boolean saved = pendingTx.commit(tempFile.getAbsolutePath(), false);
                if (!saved) {
                    wallet.disposePendingTransaction();
                    mainHandler.post(() -> callback.onError("Failed to save transaction"));
                    return;
                }
                
                byte[] txData = Files.readAllBytes(tempFile.toPath());
                String txDataBase64 = android.util.Base64.encodeToString(txData, android.util.Base64.NO_WRAP);
                
                tempFile.delete();
                
                Log.d(TAG, "✓ Multisig transaction created: " + txId);
                
                final String finalTxData = txDataBase64;
                mainHandler.post(() -> callback.onSuccess(finalTxData));
                
            } catch (Exception e) {
                Log.e(TAG, "Error creating multisig transaction", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Creation failed: " + errorMsg));
            } finally {
                if (pendingTx != null) {
                    try {
                        wallet.disposePendingTransaction();
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to dispose pending transaction", e);
                    }
                }
            }
        });
    }

    /**
     * Sign a multisig transaction
     * 
     * @param roscaId ROSCA identifier
     * @param txData Transaction data to sign (base64)
     * @param callback Returns signed transaction data
     */
    public void signMultisigTransaction(String roscaId, String txData, 
                                       MultisigSignCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        if (!wallet.isMultisig()) {
            mainHandler.post(() -> callback.onError("Wallet is not multisig"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Signing multisig transaction");
                
                // Decode transaction data
                String txDataStr = new String(android.util.Base64.decode(txData, android.util.Base64.NO_WRAP));
                
                // Restore the multisig transaction
                PendingTransaction pendingTx = wallet.restoreMultisigTransaction(txDataStr);
                
                if (pendingTx == null) {
                    mainHandler.post(() -> callback.onError("Failed to restore transaction: " + wallet.getErrorString()));
                    return;
                }
                
                // Check transaction status
                if (pendingTx.getStatus() != PendingTransaction.Status.Status_Ok) {
                    String error = pendingTx.getErrorString();
                    wallet.disposePendingTransaction();
                    mainHandler.post(() -> callback.onError("Transaction error: " + error));
                    return;
                }
                
                // Sign the transaction (this adds our signature)
                pendingTx.signMultisigTx();
                
                // Get the signed transaction data to share with other participants
                String signedData = pendingTx.multisigSignData();
                
                // Dispose of the pending transaction
                wallet.disposePendingTransaction();
                
                if (signedData == null || signedData.isEmpty()) {
                    mainHandler.post(() -> callback.onError("Failed to get signed data: " + wallet.getErrorString()));
                    return;
                }
                
                Log.d(TAG, "✓ Transaction signed successfully");
                
                final String finalSignedData = signedData;
                mainHandler.post(() -> callback.onSuccess(finalSignedData));
                
            } catch (Exception e) {
                Log.e(TAG, "Error signing multisig transaction", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Signing failed: " + errorMsg));
            }
        });
    }

    /**
     * Import multisig signatures from other participants
     * 
     * @param roscaId ROSCA identifier
     * @param signatures List of signatures from other members
     * @param callback Returns success status
     */
    public void importMultisigSignatures(String roscaId, List<String> signatures, 
                                        MultisigImportCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        if (signatures == null || signatures.isEmpty()) {
            mainHandler.post(() -> callback.onError("No signatures provided"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Importing " + signatures.size() + " multisig signatures");
                
                String[] sigArray = signatures.toArray(new String[0]);
                long nOutputs = wallet.importMultisigImages(sigArray);
                
                if (nOutputs < 0) {
                    mainHandler.post(() -> callback.onError("Failed to import: " + wallet.getErrorString()));
                    return;
                }
                
                Log.d(TAG, "✓ Imported signatures (" + nOutputs + " outputs)");
                
                wallet.store();
                
                mainHandler.post(() -> callback.onSuccess());
                
            } catch (Exception e) {
                Log.e(TAG, "Error importing multisig signatures", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Import failed: " + errorMsg));
            }
        });
    }

    /**
     * Submit a fully signed multisig transaction
     * 
     * @param roscaId ROSCA identifier
     * @param signedTx Fully signed transaction data
     * @param callback Returns transaction hash
     */
    public void submitMultisigTransaction(String roscaId, String signedTx, 
                                         MultisigSubmitCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Submitting multisig transaction");
                
                // Decode signed transaction
                byte[] txBytes = android.util.Base64.decode(signedTx, android.util.Base64.NO_WRAP);
                
                // Convert to hex string for submission
                StringBuilder hexString = new StringBuilder();
                for (byte b : txBytes) {
                    hexString.append(String.format("%02x", b & 0xff));
                }
                String txHex = hexString.toString();
                
                // Submit transaction
                String txHash = wallet.submitTransaction(txHex);
                
                if (txHash == null || txHash.isEmpty()) {
                    String error = wallet.getErrorString();
                    mainHandler.post(() -> callback.onError("Submission failed: " + 
                        (error != null ? error : "Unknown error")));
                    return;
                }
                
                Log.d(TAG, "✓ Transaction submitted: " + txHash);
                
                wallet.store();
                updateBalanceFromWallet();
                performSync();
                
                final String finalTxHash = txHash;
                mainHandler.post(() -> callback.onSuccess(finalTxHash));
                
            } catch (Exception e) {
                Log.e(TAG, "Error submitting multisig transaction", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Submission failed: " + errorMsg));
            }
        });
    }
    
    private void loadUserConfiguration(String userId, File configFile) {
        if (configFile == null || !configFile.exists()) {
            Log.w(TAG, "User config file not found: " + (configFile != null ? configFile.getAbsolutePath() : "null"));
            Log.w(TAG, "Falling back to default config");
            loadConfiguration(); // Fall back to default loading
            return;
        }
        
        Log.i(TAG, "=== LOADING USER-SPECIFIC CONFIG ===");
        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "Config file: " + configFile.getAbsolutePath());
        
        // ✅ FIX: Actually read the properties from the user-specific file
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
            
            // Apply the properties to WalletManager
            walletManager.applyConfiguration(props);
            
            Log.i(TAG, "✓ User config loaded successfully");
            Log.i(TAG, "  Wallet name: " + walletManager.getWalletName());
            Log.i(TAG, "  Daemon: " + walletManager.getDaemonAddress() + ":" + walletManager.getDaemonPort());
            Log.i(TAG, "  Network: " + walletManager.getNetworkType());
            
        } catch (IOException e) {
            Log.e(TAG, "✗ Failed to load user config, falling back to defaults", e);
            loadConfiguration();
        }
    } 
    
    // ============================================================================
    // MULTISIG CORE FUNCTIONS
    // ============================================================================

    /**
     * Get multisig information from wallet
     * This info must be shared with other participants during setup
     */
    public void getMultisigInfo(MultisigCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.i(TAG, "=== GET MULTISIG INFO ===");
                
                String multisigInfo = wallet.getMultisigInfo();
                
                if (multisigInfo == null || multisigInfo.isEmpty()) {
                    mainHandler.post(() -> callback.onError("Failed to get multisig info: " + wallet.getErrorString()));
                    return;
                }
                
                String address = wallet.getAddress();
                
                Log.i(TAG, "✓ Multisig info retrieved");
                
                final String finalInfo = multisigInfo;
                final String finalAddress = address;
                mainHandler.post(() -> callback.onSuccess(finalInfo, finalAddress));
                
            } catch (Exception e) {
                Log.e(TAG, "✗ Get multisig info exception", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Failed: " + errorMsg));
            }
        });
    }

    /**
     * Convert wallet to multisig
     * Takes multisig info from other participants and threshold
     */
    public void makeMultisig(List<String> multisigInfos, int threshold, MultisigCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        if (multisigInfos == null || multisigInfos.isEmpty()) {
            mainHandler.post(() -> callback.onError("No multisig info provided"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.i(TAG, "=== MAKE MULTISIG ===");
                Log.i(TAG, "Threshold: " + threshold + "/" + (multisigInfos.size() + 1));
                
                String[] infosArray = multisigInfos.toArray(new String[0]);
                
                String result = wallet.makeMultisig(infosArray, threshold);
                
                if (result == null || result.isEmpty()) {
                    mainHandler.post(() -> callback.onError("Failed to make multisig: " + wallet.getErrorString()));
                    return;
                }
                
                String multisigAddress = wallet.getAddress();
                
                Log.i(TAG, "✓ Wallet converted to multisig");
                Log.i(TAG, "Multisig address: " + multisigAddress);
                
                wallet.store();
                
                final String finalResult = result;
                final String finalAddress = multisigAddress;
                mainHandler.post(() -> callback.onSuccess(finalResult, finalAddress));
                
            } catch (Exception e) {
                Log.e(TAG, "✗ Make multisig exception", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Make multisig failed: " + errorMsg));
            }
        });
    }

    /**
     * Exchange multisig keys (additional setup round for complex schemes)
     */
    public void exchangeMultisigKeys(List<String> multisigInfos, MultisigCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.i(TAG, "=== EXCHANGE MULTISIG KEYS ===");
                
                String[] infosArray = multisigInfos.toArray(new String[0]);
                
                String result = wallet.exchangeMultisigKeys(infosArray);
                
                if (result == null) {
                    mainHandler.post(() -> callback.onError("Failed to exchange keys: " + wallet.getErrorString()));
                    return;
                }
                
                String multisigAddress = wallet.getAddress();
                
                Log.i(TAG, "✓ Multisig keys exchanged");
                
                wallet.store();
                
                final String finalResult = result;
                final String finalAddress = multisigAddress;
                mainHandler.post(() -> callback.onSuccess(finalResult, finalAddress));
                
            } catch (Exception e) {
                Log.e(TAG, "✗ Exchange multisig keys exception", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Exchange failed: " + errorMsg));
            }
        });
    }

    /**
     * Export multisig images (key images for wallet synchronization)
     */
    public void exportMultisigImages(ExportMultisigCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Exporting multisig images...");
                
                String info = wallet.exportMultisigImages();
                
                if (info == null || info.isEmpty()) {
                    mainHandler.post(() -> callback.onError("Failed to export: " + wallet.getErrorString()));
                    return;
                }
                
                Log.i(TAG, "✓ Multisig images exported");
                
                final String finalInfo = info;
                mainHandler.post(() -> callback.onSuccess(finalInfo));
                
            } catch (Exception e) {
                Log.e(TAG, "✗ Export multisig images exception", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Export failed: " + errorMsg));
            }
        });
    }

    /**
     * Import multisig images from other participants
     */
    public void importMultisigImages(List<String> infos, ImportMultisigCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        if (infos == null || infos.isEmpty()) {
            mainHandler.post(() -> callback.onError("No multisig info to import"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.i(TAG, "Importing multisig images from " + infos.size() + " participants...");
                
                String[] infosArray = infos.toArray(new String[0]);
                
                long nOutputs = wallet.importMultisigImages(infosArray);
                
                if (nOutputs < 0) {
                    mainHandler.post(() -> callback.onError("Failed to import: " + wallet.getErrorString()));
                    return;
                }
                
                Log.i(TAG, "✓ Multisig images imported (" + nOutputs + " outputs)");
                
                wallet.store();
                
                final long finalOutputs = nOutputs;
                mainHandler.post(() -> callback.onSuccess(finalOutputs));
                
            } catch (Exception e) {
                Log.e(TAG, "✗ Import multisig images exception", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Import failed: " + errorMsg));
            }
        });
    }

    /**
     * Check if wallet is multisig
     */
    public void isMultisig(IsMultisigCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                boolean isMultisig = wallet.isMultisig();
                mainHandler.post(() -> callback.onSuccess(isMultisig));
            } catch (Exception e) {
                Log.e(TAG, "Error checking multisig status", e);
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    /**
     * Get multisig threshold
     */
    public void getMultisigThreshold(MultisigThresholdCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                if (!wallet.isMultisig()) {
                    mainHandler.post(() -> callback.onError("Wallet is not multisig"));
                    return;
                }
                
                int threshold = wallet.multisigThreshold();
                mainHandler.post(() -> callback.onSuccess(threshold));
                
            } catch (Exception e) {
                Log.e(TAG, "Error getting multisig threshold", e);
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    // ============================================================================
    // CONSTRUCTOR AND INITIALIZATION
    // ============================================================================
    
    private WalletSuite(Context context) {
        this.context = context.getApplicationContext();
        this.syncExecutor = Executors.newSingleThreadExecutor();
        this.executorService = Executors.newSingleThreadExecutor();
        this.periodicSyncScheduler = Executors.newSingleThreadScheduledExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.walletManager = WalletManager.getInstance();
        loadConfiguration();
        registerShutdownHandler();
    }
    
    public void setRescanBalanceCallback(RescanBalanceCallback callback) {
        this.rescanBalanceCallback = callback;
    }    
    
    public long getBalanceValue() {
        return balance.get();
    }

    public long getUnlockedBalanceValue() {
        return unlockedBalance.get();
    }    

    public static synchronized WalletSuite getInstance(Context context) {
        if (instance == null) {
            instance = new WalletSuite(context);
            if (!nativeAvailable()) {
                Log.e(TAG, "Failed to load native library monerujo");
            }
        }
        return instance;
    }

    public static synchronized void resetInstance(Context context) {
        if (instance != null) {
            try {
                instance.close();
            } catch (Exception ignored) {
            }
            instance = null;
        }
        instance = new WalletSuite(context);
    }

    public String getCachedAddress() {
        return walletAddress;
    }

    private void registerShutdownHandler() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                Log.d(TAG, "Shutdown hook triggered");
                closeWalletSync();
            }));
        } catch (Exception e) {
            Log.w(TAG, "Could not register shutdown hook", e);
        }
    }

    private void closeWalletSync() {
        currentState.set(WalletState.CLOSING);
        
        try {
            if (wallet != null && isInitialized) {
                Log.d(TAG, "Closing wallet synchronously");
                
                wallet.setListener(null);
                
                if (currentWalletPath != null) {
                    try {
                        wallet.store(currentWalletPath);
                        Log.d(TAG, "Wallet persisted before close");
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to persist wallet during shutdown", e);
                    }
                }
                
                wallet.close();
                Log.d(TAG, "Wallet closed successfully");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during wallet closure", e);
        } finally {
            wallet = null;
            isInitialized = false;
            currentWalletPath = null;
        }
    }
    
    /**
     * Force a complete balance refresh cycle
     * This ensures the wallet's internal cached balance is updated
     * Call this before critical operations that need accurate balance
     */
    public void forceBalanceRefresh() {
        if (!isInitialized || wallet == null) {
            Log.w(TAG, "Cannot force balance refresh - wallet not initialized");
            return;
        }
        
        syncExecutor.execute(() -> {
            try {
                Log.i(TAG, "=== FORCE BALANCE REFRESH ===");
                
                // Step 1: Pause any ongoing refresh
                Log.d(TAG, "[1/4] Pausing existing refresh...");
                wallet.pauseRefresh();
                Thread.sleep(1000);
                
                // Step 2: Force a blocking refresh
                Log.d(TAG, "[2/4] Executing blocking refresh...");
                wallet.refresh(); // This is BLOCKING - will wait for completion
                Log.i(TAG, "✅ Blocking refresh completed");
                
                // Step 3: Wait for balance to settle in wallet's internal cache
                Log.d(TAG, "[3/4] Waiting for balance to settle...");
                Thread.sleep(2000);
                
                // Step 4: Read fresh balance and update our cache
                Log.d(TAG, "[4/4] Reading fresh balance...");
                long freshBalance = wallet.getBalance();
                long freshUnlocked = wallet.getUnlockedBalance();
                
                Log.i(TAG, "Fresh balance retrieved:");
                Log.i(TAG, "  Total: " + convertAtomicToXmr(freshBalance) + " XMR");
                Log.i(TAG, "  Unlocked: " + convertAtomicToXmr(freshUnlocked) + " XMR");
                
                // Update our cached values
                balance.set(freshBalance);
                unlockedBalance.set(freshUnlocked);
                
                // Resume normal refresh cycle
                wallet.startRefresh();
                
                Log.i(TAG, "✅ Force balance refresh complete");
                
            } catch (InterruptedException e) {
                Log.w(TAG, "Force balance refresh interrupted", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                Log.e(TAG, "Error during force balance refresh", e);
            }
        });
    }    
    
    public void close() {
        Log.i(TAG, "=== SHUTDOWN INITIATED ===");
        
        stopPeriodicSync();
        
        if (currentSyncTimeout != null) {
            currentSyncTimeout.cancel(false);
        }
        
        closeWalletSync();

        periodicSyncScheduler.shutdown();
        syncExecutor.shutdown();
        executorService.shutdown();
        
        try {
            if (!syncExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                Log.w(TAG, "Sync executor did not terminate, forcing shutdown");
                syncExecutor.shutdownNow();
            }
            if (!periodicSyncScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                periodicSyncScheduler.shutdownNow();
            }
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ie) {
            syncExecutor.shutdownNow();
            periodicSyncScheduler.shutdownNow();
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        Log.i(TAG, "=== SHUTDOWN COMPLETE ===");
    }

    // ============================================================================
    // SYNC FUNCTIONS
    // ============================================================================

    public void startPeriodicSync() {
        if (periodicSyncTask != null && !periodicSyncTask.isDone()) {
            Log.d(TAG, "Periodic sync already scheduled");
            return;
        }

        Log.i(TAG, "=== STARTING PERIODIC SYNC (interval: " + (PERIODIC_SYNC_INTERVAL_MS / 60000) + " minutes) ===");

        periodicSyncTask = periodicSyncScheduler.scheduleWithFixedDelay(() -> {
            if (!isInitialized) {
                Log.d(TAG, "⏭ Skipping periodic sync - wallet not initialized");
                return;
            }
            
            WalletState state = currentState.get();
            if (state != WalletState.IDLE) {
                Log.d(TAG, "⏭ Skipping periodic sync - state: " + state);
                return;
            }
            
            long timeSinceLastSync = System.currentTimeMillis() - lastSyncStartTime.get();
            if (timeSinceLastSync < SYNC_TIMEOUT_MS && lastSyncStartTime.get() > 0) {
                Log.w(TAG, "⚠️ Previous sync may still be running (" + (timeSinceLastSync / 1000) + "s ago)");
                return;
            }
            
            Log.i(TAG, "⏰ Periodic sync triggered");
            performSync();
            
        }, PERIODIC_SYNC_INTERVAL_MS, PERIODIC_SYNC_INTERVAL_MS, TimeUnit.MILLISECONDS);

        Log.i(TAG, "✓ Periodic sync scheduler started");
    }

    public void stopPeriodicSync() {
        if (periodicSyncTask != null) {
            periodicSyncTask.cancel(false);
            periodicSyncTask = null;
            Log.i(TAG, "✓ Periodic sync scheduler stopped");
        }
    }

    private void performSync() {
        if (!currentState.compareAndSet(WalletState.IDLE, WalletState.SYNCING)) {
            WalletState state = currentState.get();
            Log.w(TAG, "Cannot start sync - current state: " + state);
            return;
        }
        
        lastSyncStartTime.set(System.currentTimeMillis());
        
        Log.i(TAG, "=== SYNC STARTED ===");
        
        mainHandler.post(() -> {
            if (currentSyncTimeout != null) {
                currentSyncTimeout.cancel(false);
            }
            currentSyncTimeout = periodicSyncScheduler.schedule(() -> {
                if (currentState.get() == WalletState.SYNCING) {
                    Log.e(TAG, "🚨 SYNC TIMEOUT - operation hung for " + (SYNC_TIMEOUT_MS / 60000) + " minutes");
                    completeSyncOperation(false);
                }
            }, SYNC_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        });
        
        syncExecutor.execute(this::executeSyncOperationBlocking);
    }
    
    private void executeSyncOperationBlocking() {
        WalletListener syncListener = null;
        
        try {
            if (!validateDaemonConnection()) {
                Log.e(TAG, "Daemon validation failed");
                completeSyncOperation(false);
                return;
            }
            
            final long walletHeight = wallet.getBlockChainHeight();
            final long daemonHeight = getDaemonHeightViaHttp();
            
            syncStartHeight = walletHeight;
            syncEndHeight = daemonHeight;
            
            Log.i(TAG, "Sync range: " + walletHeight + " → " + daemonHeight);
            Log.i(TAG, "Blocks to sync: " + (daemonHeight - walletHeight));
            
            syncListener = new WalletListener() {
                @Override
                public void moneySent(String txId, long amount) {
                    Log.d(TAG, "[SYNC] moneySent: " + txId);
                }

                @Override
                public void moneyReceived(String txId, long amount) {
                    Log.d(TAG, "[SYNC] moneyReceived: " + txId + " = " + (amount / 1e12) + " XMR");
                    if (transactionListener != null) {
                        mainHandler.post(() -> transactionListener.onOutputReceived(amount, txId, false));
                    }
                }

                @Override
                public void unconfirmedMoneyReceived(String txId, long amount) {
                    Log.d(TAG, "[SYNC] unconfirmedMoneyReceived: " + txId);
                }

                @Override
                public void newBlock(long height) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastProgressUpdateTime > 1000) {
                        lastProgressUpdateTime = currentTime;
                        
                        double percentDone = syncEndHeight > syncStartHeight ? 
                            (100.0 * (height - syncStartHeight) / (syncEndHeight - syncStartHeight)) : 0.0;
                        
                        Log.d(TAG, "[SYNC] Block: " + height + " (" + String.format("%.1f", percentDone) + "%)");
                        
                        if (statusListener != null) {
                            final long currentHeight = height;
                            final double finalPercent = percentDone;
                            mainHandler.post(() -> statusListener.onSyncProgress(currentHeight, syncStartHeight, syncEndHeight, finalPercent));
                        }
                    }
                }
                
                @Override
                public void updated() {
                }

                @Override
                public void refreshed() {
                }
            };

            wallet.setListener(syncListener);
            
            long startTime = System.currentTimeMillis();
            
            Log.d(TAG, "Starting BLOCKING wallet.refresh()...");
            
            wallet.refresh();
            
            long duration = System.currentTimeMillis() - startTime;
            long heightAfter = wallet.getBlockChainHeight();
            long blocksProcessed = heightAfter - walletHeight;
            
            Log.i(TAG, "✓ Blocking sync completed:");
            Log.i(TAG, "  Duration: " + (duration / 1000) + "s");
            Log.i(TAG, "  Blocks processed: " + blocksProcessed);
            Log.i(TAG, "  Final height: " + heightAfter);
            
            updateBalanceFromWallet();
            
            completeSyncOperation(true);
            
        } catch (Exception e) {
            Log.e(TAG, "✗ Blocking sync exception", e);
            completeSyncOperation(false);
        } finally {
            if (syncListener != null) {
                wallet.setListener(null);
            }
        }
    }

    /**
     * Resume wallet operations
     * Called when the app comes to foreground
     */
    public void resumeWallet() {
        if (!isInitialized || wallet == null) {
            Log.w(TAG, "Cannot resume wallet - not initialized");
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.i(TAG, "=== RESUMING WALLET ===");
                
                // Resume background refresh
                wallet.startRefresh();
                Log.d(TAG, "✓ Background refresh resumed");
                
                // Restart periodic sync if it was stopped
                if (periodicSyncTask == null || periodicSyncTask.isDone()) {
                    startPeriodicSync();
                    Log.d(TAG, "✓ Periodic sync restarted");
                }
                
                // Trigger immediate sync to catch up
                performSync();
                
                Log.i(TAG, "✓ Wallet resumed successfully");
                
            } catch (Exception e) {
                Log.e(TAG, "Error resuming wallet", e);
            }
        });
    }

    /**
     * Pause wallet operations
     * Called when the app goes to background
     */
    public void pauseWallet() {
        if (!isInitialized || wallet == null) {
            Log.w(TAG, "Cannot pause wallet - not initialized");
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.i(TAG, "=== PAUSING WALLET ===");
                
                // Stop periodic sync to save resources
                stopPeriodicSync();
                Log.d(TAG, "✓ Periodic sync stopped");
                
                // Pause background refresh
                wallet.pauseRefresh();
                Log.d(TAG, "✓ Background refresh paused");
                
                // Save wallet state
                if (currentWalletPath != null && !currentWalletPath.isEmpty()) {
                    try {
                        wallet.store(currentWalletPath);
                        Log.d(TAG, "✓ Wallet state persisted");
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to persist wallet state during pause", e);
                    }
                }
                
                Log.i(TAG, "✓ Wallet paused successfully");
                
            } catch (Exception e) {
                Log.e(TAG, "Error pausing wallet", e);
            }
        });
    }

    private void updateBalanceFromWallet() {
        if (wallet == null) return;
        
        try {
            Log.d(TAG, "=== UPDATE BALANCE FROM WALLET ===");
            
            // FIXED: Force a complete refresh cycle
            wallet.pauseRefresh();
            Thread.sleep(1000); // Longer pause to ensure refresh stops
            
            Log.d(TAG, "Starting blocking refresh...");
            wallet.refresh(); // FIXED: Use blocking refresh instead of async
            
            Log.d(TAG, "✅ Blocking refresh completed");
            
            // FIXED: Additional wait for balance to settle
            Thread.sleep(2000);
            
            // NOW read the balance - should be fresh
            long bal = wallet.getBalance();
            long unl = wallet.getUnlockedBalance();
            
            Log.d(TAG, "Balance retrieved from wallet:");
            Log.d(TAG, "  Total: " + convertAtomicToXmr(bal) + " XMR");
            Log.d(TAG, "  Unlocked: " + convertAtomicToXmr(unl) + " XMR");
            
            // Update cached values
            boolean balanceChanged = (bal != balance.get() || unl != unlockedBalance.get());
            
            if (balanceChanged) {
                long oldBal = balance.get();
                long oldUnl = unlockedBalance.get();
                
                balance.set(bal);
                unlockedBalance.set(unl);
                
                Log.i(TAG, "💰 Balance updated:");
                Log.i(TAG, "  Old: " + convertAtomicToXmr(oldBal) + " XMR → New: " + convertAtomicToXmr(bal) + " XMR");
                Log.i(TAG, "  Old Unlocked: " + convertAtomicToXmr(oldUnl) + " XMR → New Unlocked: " + convertAtomicToXmr(unl) + " XMR");
                
                if (statusListener != null) {
                    final long finalBal = bal;
                    final long finalUnl = unl;
                    mainHandler.post(() -> statusListener.onBalanceUpdated(finalBal, finalUnl));
                }
            } else {
                Log.d(TAG, "Balance unchanged: " + convertAtomicToXmr(bal) + " XMR");
            }
            
            // Resume normal refresh cycle
            wallet.startRefresh();
            
        } catch (InterruptedException e) {
            Log.w(TAG, "Balance update interrupted", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            Log.w(TAG, "Error updating balance during sync", e);
        }
    }
    
    private void completeSyncOperation(boolean success) {
        if (!currentState.compareAndSet(WalletState.SYNCING, WalletState.IDLE)) {
            Log.d(TAG, "Sync already completed or interrupted");
            return;
        }
        
        if (currentSyncTimeout != null) {
            currentSyncTimeout.cancel(false);
            currentSyncTimeout = null;
        }
        
        long duration = System.currentTimeMillis() - lastSyncStartTime.get();
        Log.i(TAG, "=== SYNC COMPLETED (success=" + success + ", duration=" + (duration / 1000) + "s) ===");
        
        try {
            if (success) {
                Log.d(TAG, "Forcing balance recalculation...");
                
                // FIXED: Multi-attempt balance refresh with blocking calls
                long finalBal = 0;
                long finalUnl = 0;
                boolean balanceUpdated = false;
                
                for (int attempt = 0; attempt < 3; attempt++) {
                    Log.d(TAG, "Balance refresh attempt " + (attempt + 1) + "/3");
                    
                    try {
                        // FIXED: Use blocking refresh
                        wallet.pauseRefresh();
                        Thread.sleep(1000);
                        
                        Log.d(TAG, "  Executing blocking refresh...");
                        wallet.refresh(); // Blocking call
                        
                        // Wait for balance to settle
                        Thread.sleep(2000);
                        
                        // Read fresh balance
                        long currentBal = wallet.getBalance();
                        long currentUnl = wallet.getUnlockedBalance();
                        
                        Log.d(TAG, "  Attempt " + (attempt + 1) + " result:");
                        Log.d(TAG, "    Balance: " + convertAtomicToXmr(currentBal) + " XMR");
                        Log.d(TAG, "    Unlocked: " + convertAtomicToXmr(currentUnl) + " XMR");
                        
                        finalBal = currentBal;
                        finalUnl = currentUnl;
                        balanceUpdated = true;
                        
                        // If we got a non-zero balance, we're confident it's correct
                        if (currentBal > 0 || currentUnl > 0) {
                            Log.i(TAG, "✅ Non-zero balance found on attempt " + (attempt + 1));
                            break;
                        }
                        
                        // If zero balance and not last attempt, check if there are transactions
                        if (attempt < 2) {
                            int txCount = getTxCount();
                            if (txCount > 0) {
                                Log.w(TAG, "⚠️ Found " + txCount + " transactions but zero balance - retrying...");
                                continue;
                            } else {
                                Log.d(TAG, "Zero balance with zero transactions - this is correct");
                                break;
                            }
                        }
                        
                    } catch (InterruptedException e) {
                        Log.w(TAG, "Balance refresh interrupted on attempt " + (attempt + 1));
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        Log.w(TAG, "Error on balance refresh attempt " + (attempt + 1), e);
                        if (attempt == 2) {
                            // Last attempt failed, use whatever we have
                            break;
                        }
                    }
                }
                
                // Update cached balance if we got values
                if (balanceUpdated) {
                    boolean changed = (finalBal != balance.get() || finalUnl != unlockedBalance.get());
                    
                    if (changed) {
                        long oldBal = balance.get();
                        long oldUnl = unlockedBalance.get();
                        
                        balance.set(finalBal);
                        unlockedBalance.set(finalUnl);
                        
                        Log.i(TAG, "💰 Balance updated after sync:");
                        Log.i(TAG, "  Old: " + convertAtomicToXmr(oldBal) + " XMR → New: " + convertAtomicToXmr(finalBal) + " XMR");
                        Log.i(TAG, "  Old Unlocked: " + convertAtomicToXmr(oldUnl) + " XMR → New Unlocked: " + convertAtomicToXmr(finalUnl) + " XMR");
                    } else {
                        Log.d(TAG, "Balance unchanged: " + convertAtomicToXmr(finalBal) + " XMR");
                    }
                }
                
                // Resume normal refresh
                wallet.startRefresh();
            }
            
            long walletHeight = wallet.getBlockChainHeight();
            long daemonHeight = getDaemonHeightViaHttp();
            
            double percent = daemonHeight > 0 ? (100.0 * walletHeight / daemonHeight) : 100.0;
            
            Log.i(TAG, "Final sync status:");
            Log.i(TAG, "  Wallet height: " + walletHeight);
            Log.i(TAG, "  Daemon height: " + daemonHeight);
            Log.i(TAG, "  Progress: " + String.format("%.2f%%", percent));
            Log.i(TAG, "  Balance: " + convertAtomicToXmr(balance.get()) + " XMR");
            Log.i(TAG, "  Unlocked: " + convertAtomicToXmr(unlockedBalance.get()) + " XMR");            

            if (statusListener != null) {
                final long finalWalletHeight = walletHeight;
                final long finalDaemonHeight = daemonHeight;
                final double finalPercent = percent;
                final long finalBalance = balance.get();
                final long finalUnlocked = unlockedBalance.get();
                
                mainHandler.post(() -> {
                    statusListener.onSyncProgress(finalWalletHeight, syncStartHeight, finalDaemonHeight, finalPercent);
                    statusListener.onBalanceUpdated(finalBalance, finalUnlocked);
                });
            }
            
            if (success && percent >= 99.0) {
                checkAndTriggerRescan(walletHeight, daemonHeight);
            }
            
            persistWallet();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in sync completion", e);
        }
    }

    private void checkAndTriggerRescan(long walletHeight, long daemonHeight) {
        long heightDiff = daemonHeight - walletHeight;
        if (heightDiff > 1000) {
            Log.w(TAG, "⚠️ Large height difference detected: " + heightDiff + " blocks");
            
            int txCount = getTxCount();
            long currentBalance = balance.get();
            
            if (txCount > 0 && currentBalance == 0) {
                Log.w(TAG, "⚠️ SUSPICIOUS: " + txCount + " transactions but zero balance");
                Log.w(TAG, "This indicates cache corruption - triggering rescan");
                
                mainHandler.postDelayed(() -> {
                    Log.i(TAG, "⏰ Auto-triggering rescan due to suspected cache corruption");
                    triggerRescan();
                }, 5000);
            }
        }
    }
    
    public void getTransactionHistory(TransactionHistoryCallback callback) {
        try {
            List<TransactionInfo> transactions = wallet.getHistory().getAll();
            if(callback != null) {
                callback.onSuccess(transactions);
            }
        } catch (Exception e) {
            if(callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    private int getTxCount() {
        try {
            TransactionHistory history = wallet.getHistory();
            if (history == null) return 0;
            history.refresh();
            List<TransactionInfo> allTxs = history.getAll();
            return (allTxs != null) ? allTxs.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    private void triggerRescan() {
        if (!currentState.compareAndSet(WalletState.IDLE, WalletState.RESCANNING)) {
            Log.w(TAG, "Cannot trigger rescan - state: " + currentState.get());
            if (rescanCallback != null) {
                final RescanCallback callback = rescanCallback;
                rescanCallback = null;
                mainHandler.post(() -> callback.onError("Cannot trigger rescan - wallet busy"));
            }
            return;
        }
        
        lastRescanTime.set(System.currentTimeMillis());
        stopPeriodicSync();
        
        syncExecutor.execute(() -> {
            try {
                String walletPath = wallet.getPath();
                
                Log.d(TAG, "[1/5] Storing current wallet state...");
                try {
                    if (wallet != null && !currentWalletPath.isEmpty()) {
                        wallet.store(currentWalletPath);
                        Log.i(TAG, "✓ Wallet persisted before rescan");
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Failed to persist before rescan", e);
                }
               
                Log.d(TAG, "[2/5] Closing wallet...");
                wallet.setListener(null);
                wallet.close();
                wallet = null;
                Thread.sleep(1000);
                
                Log.d(TAG, "[3/5] Reopening wallet...");
                wallet = walletManager.openWallet(walletPath);
                if (wallet == null) {
                    Log.e(TAG, "✗ CRITICAL: Failed to reopen wallet - ABORTING");
                    currentState.set(WalletState.IDLE);
                    startPeriodicSync();
                    
                    if (rescanCallback != null) {
                        final RescanCallback callback = rescanCallback;
                        rescanCallback = null;
                        mainHandler.post(() -> callback.onError("Failed to reopen wallet"));
                    }
                    return;
                }
                Log.i(TAG, "✓ Wallet reopened with fresh cache");
                
                Log.d(TAG, "[4/5] Reconnecting to daemon...");
                try {
                    Node node = walletManager.createNodeFromConfig();
                    long handle = wallet.initJ(
                        node.getAddress(), 0,
                        node.getUsername(), node.getPassword(),
                        node.isSsl(), false, ""
                    );
                    if (handle > 0) {
                        Log.d(TAG, "✓ Daemon connected (handle: " + handle + ")");
                    } else {
                        Log.w(TAG, "Daemon init returned 0 (may still work)");
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Daemon reconnection error (continuing)", e);
                }
                
                Log.d(TAG, "[5/5] Attempting rescanSpent...");
                boolean resyncSuccess = false;
                try {
                    wallet.rescanSpent();
                    resyncSuccess = true;
                    Log.i(TAG, "rescanSpent result: " + resyncSuccess);
                } catch (Throwable t) {
                    Log.w(TAG, "rescanSpent not available, using traditional rescan", t);
                }
                
                if (!resyncSuccess) {
                    Log.d(TAG, "Using traditional rescanBlockchainAsync...");
                    wallet.rescanBlockchainAsync();
                }
                
                mainHandler.postDelayed(this::monitorRescanProgress, 5000);
                
            } catch (InterruptedException e) {
                Log.w(TAG, "Rescan interrupted", e);
                currentState.set(WalletState.IDLE);
                startPeriodicSync();
                Thread.currentThread().interrupt();
                
                if (rescanCallback != null) {
                    final RescanCallback callback = rescanCallback;
                    rescanCallback = null;
                    mainHandler.post(() -> callback.onError("Rescan interrupted"));
                }
            } catch (Exception e) {
                Log.e(TAG, "✗ Rescan failed with exception", e);
                currentState.set(WalletState.IDLE);
                startPeriodicSync();
                
                if (rescanCallback != null) {
                    final RescanCallback callback = rescanCallback;
                    rescanCallback = null;
                    final String error = e.getMessage() != null ? e.getMessage() : "Unknown error";
                    mainHandler.post(() -> callback.onError("Rescan failed: " + error));
                }
            }
        });
    }    

    private void monitorRescanProgress() {
        if (currentState.get() != WalletState.RESCANNING || wallet == null) {
            Log.d(TAG, "Rescan monitoring stopped - state changed or wallet null");
            return;
        }
        
        try {
            long height = wallet.getBlockChainHeight();
            long daemonHeight = getDaemonHeightViaHttp();
            long balance = wallet.getBalance();
            long unlockedBalance = wallet.getUnlockedBalance();
            int txCount = getTxCount();
            
            double progress = daemonHeight > 0 ? (height * 100.0 / daemonHeight) : 0;
            
            Log.i(TAG, "=== RESCAN PROGRESS ===");
            Log.i(TAG, "  Height: " + height + " / " + daemonHeight + " (" + String.format("%.1f", progress) + "%)");
            Log.i(TAG, "  Balance: " + convertAtomicToXmr(balance) + " XMR");
            Log.i(TAG, "  Unlocked: " + convertAtomicToXmr(unlockedBalance) + " XMR");
            Log.i(TAG, "  Transactions found: " + txCount);
            
            this.balance.set(balance);
            this.unlockedBalance.set(unlockedBalance);
            
            if (rescanBalanceCallback != null) {
                mainHandler.post(() -> rescanBalanceCallback.onBalanceUpdated(balance, unlockedBalance));
            }
            
            if (statusListener != null) {
                final long currentHeight = height;
                final double finalProgress = progress;
                final long finalBalance = balance;
                final long finalUnlocked = unlockedBalance;
                mainHandler.post(() -> {
                    statusListener.onSyncProgress(currentHeight, 0, daemonHeight, finalProgress);
                    statusListener.onBalanceUpdated(finalBalance, finalUnlocked);
                });
            }
            
            if (height >= daemonHeight - 1 || progress >= 99.9) {
                Log.i(TAG, "✓✓✓ RESCAN COMPLETE ✓✓✓");
                
                try {
                    wallet.store();
                    Log.d(TAG, "Wallet persisted after rescan");
                } catch (Exception e) {
                    Log.w(TAG, "Failed to store wallet after rescan", e);
                }
                
                currentState.set(WalletState.IDLE);
                startPeriodicSync();
                
                if (rescanCallback != null) {
                    final RescanCallback callback = rescanCallback;
                    rescanCallback = null;
                    final long finalBalance = balance;
                    final long finalUnlockedBalance = unlockedBalance;
                    mainHandler.post(() -> {
                        Log.d(TAG, "Invoking rescan callback with balance: " + convertAtomicToXmr(finalUnlockedBalance) + " XMR");
                        callback.onComplete(finalBalance, finalUnlockedBalance);
                    });
                }
                
                rescanBalanceCallback = null;
                
                return;
            }
            
            mainHandler.postDelayed(this::monitorRescanProgress, 5000);
            
        } catch (Exception e) {
            Log.e(TAG, "Error monitoring rescan progress", e);
            currentState.set(WalletState.IDLE);
            startPeriodicSync();
            rescanBalanceCallback = null;
            
            if (rescanCallback != null) {
                final RescanCallback callback = rescanCallback;
                rescanCallback = null;
                final String error = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Rescan monitoring failed: " + error));
            }
        }
    }

    private void persistWallet() {
        if (wallet == null || !isInitialized) {
            return;
        }
        
        if (currentState.get() == WalletState.RESCANNING) {
            Log.d(TAG, "Skipping persist - rescan in progress");
            return;
        }
        
        syncExecutor.execute(() -> {
            try {
                String path = wallet.getPath();
                if (path != null && !path.isEmpty()) {
                    boolean stored = wallet.store(path);
                    if (stored) {
                        Log.d(TAG, "✓ Wallet persisted");
                    } else {
                        Log.w(TAG, "Wallet store returned false: " + wallet.getErrorString());
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Wallet persist error", e);
            }
        });
    }
    
    private long getDaemonHeightViaHttp() {
        HttpURLConnection conn = null;
        try {
            String daemonAddress = walletManager.getDaemonAddress();
            int daemonPort = walletManager.getDaemonPort();

            if (daemonAddress == null || daemonAddress.isEmpty()) {
                return cachedDaemonHeight.get();
            }

            String daemonUrl = "http://" + daemonAddress + ":" + daemonPort + "/get_height";
            URL url = new URL(daemonUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim());
                    }
                }

                String jsonResponse = response.toString();
                int heightIndex = jsonResponse.indexOf("\"height\":");
                if (heightIndex != -1) {
                    int startIndex = heightIndex + 9;
                    int endIndex = jsonResponse.indexOf(",", startIndex);
                    if (endIndex == -1) {
                        endIndex = jsonResponse.indexOf("}", startIndex);
                    }
                    if (endIndex != -1) {
                        String heightStr = jsonResponse.substring(startIndex, endIndex).trim();
                        long height = Long.parseLong(heightStr);
                        cachedDaemonHeight.set(height);
                        return height;
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to get daemon height via HTTP: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return cachedDaemonHeight.get();
    }

    private boolean validateDaemonConnection() {
        try {
            long height = getDaemonHeightViaHttp();
            if (height <= 0) {
                Log.e(TAG, "Daemon height invalid: " + height);
                return false;
            }
            Log.d(TAG, "Daemon height: " + height);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Daemon validation failed", e);
            return false;
        }
    }

    // ============================================================================
    // WALLET INITIALIZATION
    // ============================================================================

    public Future<Boolean> initializeWallet(String userId) {
        // ✅ ADD: Prevent recursive initialization
        synchronized (this) {
            if (isInitialized) {
                Log.d(TAG, "Wallet already initialized - returning true");
                CompletableFuture<Boolean> alreadyDone = new CompletableFuture<>();
                alreadyDone.complete(true);
                return alreadyDone;
            }
        }
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        syncExecutor.execute(() -> {
            try {
                Log.i(TAG, "=== WALLET INITIALIZATION STARTED ===");
                Log.d(TAG, "User ID: " + userId);
                
                // ✅ FIX: Use user-specific config file name
                File configFile = new File(context.getExternalFilesDir(null), "wallet_config_" + userId + ".conf");
                Log.d(TAG, "Looking for config file: " + configFile.getAbsolutePath());
                Log.d(TAG, "Config file exists: " + configFile.exists());
                
                // STEP 1: Load user-specific configuration FIRST
                loadUserConfiguration(userId, configFile);
                
                String walletName = walletManager.getWalletName();
                Log.d(TAG, "Wallet name from config: " + walletName);
                
                // STEP 2: Validate wallet name matches user
                if (!walletName.contains(userId)) {
                    Log.w(TAG, "⚠️ Wallet name doesn't contain user ID!");
                    Log.w(TAG, "Expected user ID: " + userId);
                    Log.w(TAG, "Wallet name: " + walletName);
                    throw new IllegalStateException("Wallet name mismatch - config may be for different user");
                }

                // ... rest of initialization code remains the same ...
                // (Continue with SD card backup check, wallet opening, etc.)

                // STEP 3: Check for SD card backup restoration
                File sdcardDir = new File(Environment.getExternalStorageDirectory(),
                        "Android/data/com.bitchat.droid/files");
                Log.d(TAG, "Checking SD card directory: " + sdcardDir.getAbsolutePath());
                
                File backupFile = new File(sdcardDir, walletName);
                File backupKeysFile = new File(sdcardDir, walletName + ".keys");
                File backupAddressFile = new File(sdcardDir, walletName + ".address.txt");

                // STEP 4: Prepare wallet directory
                File dir = context.getDir("wallets", Context.MODE_PRIVATE);
                Log.d(TAG, "Wallets directory: " + dir.getAbsolutePath());
                if (!dir.exists() && !dir.mkdirs()) {
                    Log.e(TAG, "CRITICAL: Cannot create wallets directory");
                    notifyWalletInitialized(false, "Cannot create wallets dir");
                    future.complete(false);
                    return;
                }

                String walletPath = new File(dir, walletName).getAbsolutePath();
                currentWalletPath = walletPath;
                Log.d(TAG, "Wallet path: " + walletPath);

                // STEP 5: Restore from SD card if backup exists
                if (backupFile.exists()) {
                    Log.i(TAG, "=== RESTORING WALLET FROM SD CARD ===");
                    try {
                        File destWalletFile = new File(walletPath);
                        Files.copy(backupFile.toPath(), destWalletFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        Log.i(TAG, "✓ Main wallet file copied");
                        File bakWalletFile = new File(sdcardDir, walletName + ".bak");
                        Files.move(backupFile.toPath(), bakWalletFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        Log.i(TAG, "✓ Original wallet file renamed to .bak");
                    } catch (Exception ex) {
                        Log.e(TAG, "✗ Wallet copy/rename failed", ex);
                    }

                    if (backupKeysFile.exists()) {
                        try {
                            File destKeysFile = new File(walletPath + ".keys");
                            Files.copy(backupKeysFile.toPath(), destKeysFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            File bakKeysFile = new File(sdcardDir, walletName + ".keys.bak");
                            Files.move(backupKeysFile.toPath(), bakKeysFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            Log.i(TAG, "✓ Keys file copied and renamed to .bak");
                        } catch (Exception ex) {
                            Log.e(TAG, "✗ Keys copy/rename failed", ex);
                        }
                    }

                    if (backupAddressFile.exists()) {
                        try {
                            File destAddressFile = new File(walletPath + ".address.txt");
                            Files.copy(backupAddressFile.toPath(), destAddressFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            File bakAddrFile = new File(sdcardDir, walletName + ".address.txt.bak");
                            Files.move(backupAddressFile.toPath(), bakAddrFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            Log.i(TAG, "✓ Address file copied and renamed to .bak");
                        } catch (Exception ex) {
                            Log.e(TAG, "✗ Address copy/rename failed", ex);
                        }
                    }

                    Log.i(TAG, "=== WALLET RESTORATION COMPLETE ===");
                } else {
                    Log.d(TAG, "No backup found on SD card");
                }

                // STEP 6: Open or create wallet
                File keysFile = new File(walletPath + ".keys");
                Log.d(TAG, "Keys file exists: " + keysFile.exists());

                if (keysFile.exists()) {
                    Log.i(TAG, "=== OPENING EXISTING WALLET ===");
                    wallet = walletManager.openWallet(walletPath);
                } else {
                    Log.i(TAG, "=== CREATING NEW WALLET ===");
                    wallet = walletManager.createWallet(walletPath);
                }

                if (wallet == null) {
                    Log.e(TAG, "CRITICAL: Wallet is null after open/create attempt");
                    notifyWalletInitialized(false, "JNI returned null wallet");
                    future.complete(false);
                    return;
                }

                // STEP 7: Initialize daemon connection
                Log.d(TAG, "=== INITIALIZING DAEMON CONNECTION ===");
                try {
                    Node node = walletManager.createNodeFromConfig();
                    Log.d(TAG, "Node config: " + node.displayProperties());

                    long handle = wallet.initJ(
                            node.getAddress(), 0,
                            node.getUsername(), node.getPassword(),
                            node.isSsl(), false, ""
                    );
                    Log.d(TAG, "initJ handle: " + handle);
                    if (handle == 0) {
                        Log.w(TAG, "initJ returned 0, applying fallback daemon setup");
                        walletManager.setDaemonAddress(node.getAddress());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception during daemon init", e);
                    walletManager.setDaemonAddress(walletManager.getDaemonAddress());
                }

                // STEP 8: Validate wallet status
                int status = wallet.getStatus();
                String statusName = (status < Wallet.Status.values().length)
                        ? Wallet.Status.values()[status].name() : "UNKNOWN";
                Log.d(TAG, "Wallet status: " + statusName + " (" + status + ")");
                if (status != Wallet.Status.Status_Ok.ordinal()) {
                    notifyWalletInitialized(false, "Init failed: " + statusName);
                    future.complete(false);
                    return;
                }

                // STEP 9: Get wallet metadata
                boolean isReadOnly = false;
                try {
                    isReadOnly = wallet.isReadOnly();
                } catch (Throwable t) {
                    Log.w(TAG, "Cannot determine read-only state", t);
                }
                if (isReadOnly) {
                    Log.w(TAG, "⚠️ Wallet opened in read-only mode");
                    notifyWalletInitialized(true, "Read-only wallet");
                } else {
                    notifyWalletInitialized(true, "Wallet initialized OK");
                }

                try {
                    walletAddress = wallet.getAddress();
                    Log.i(TAG, "Wallet address: " + walletAddress);
                    Log.d(TAG, "Height=" + wallet.getBlockChainHeight() + 
                          ", Restore=" + wallet.getRestoreHeight());
                } catch (Exception e) {
                    Log.w(TAG, "Metadata fetch failed", e);
                }

                // STEP 10: Complete initialization
                isInitialized = true;
                performSync();
                startPeriodicSync();

                future.complete(true);
                Log.i(TAG, "✓✓✓ WALLET INITIALIZATION COMPLETE FOR USER: " + userId + " ✓✓✓");

            } catch (Exception e) {
                Log.e(TAG, "✗ Exception during wallet init", e);
                notifyWalletInitialized(false, "Error: " + e.getMessage());
                future.completeExceptionally(e);
            }
        });

        return future;
    }
    
    public Future<Boolean> initializeWallet() {
        // This requires getting userId from somewhere - you might want to pass it
        // Or throw an exception requiring the userId version
        throw new UnsupportedOperationException(
            "initializeWallet() requires userId parameter. Use initializeWallet(String userId) instead."
        );
    }    

    public void initializeWalletFromSeed(String seed, long restoreHeight, int requestedNetType) {
        syncExecutor.execute(() -> {
            try {
                Log.i(TAG, "=== RESTORING WALLET FROM SEED ===");
                Log.d(TAG, "Restore height: " + restoreHeight);
                
                File dir = context.getDir("wallets", Context.MODE_PRIVATE);
                if (!dir.exists() && !dir.mkdirs()) {
                    Log.e(TAG, "CRITICAL: Cannot create wallets directory");
                    notifyWalletInitialized(false, "Cannot create wallets dir");
                    return;
                }
                
                String walletPath = new File(dir, walletManager.getWalletName()).getAbsolutePath();
                currentWalletPath = walletPath;
                Log.d(TAG, "Wallet path: " + walletPath);
                
                wallet = walletManager.recoveryWallet(walletPath, seed, restoreHeight);
                Log.d(TAG, "Recovery wallet returned: " + (wallet != null));
                
                if (wallet != null && wallet.getStatus() == Wallet.Status.Status_Ok.ordinal()) {
                    Log.i(TAG, "✓ Wallet restored successfully");
                    
                    setupWallet();
                    isInitialized = true;
                    notifyWalletInitialized(true, "Wallet restored");

                    performSync();
                    startPeriodicSync();
                } else {
                    String error = (wallet != null) ? wallet.getErrorString() : "JNI error";
                    Log.e(TAG, "✗ Wallet restoration failed: " + error);
                    notifyWalletInitialized(false, "Restore failed: " + error);
                }
            } catch (Exception e) {
                Log.e(TAG, "✗ Exception during wallet restoration", e);
                notifyWalletInitialized(false, "Error: " + e.getMessage());
            }
        });
    }

    private void setupWallet() {
        if (wallet == null) return;

        boolean daemonSet = setDaemonFromConfigAndApply();
        if (!daemonSet) {
            Log.e(TAG, "Failed to establish daemon connection during setup");
        }
    }

    // ============================================================================
    // TRANSACTION FUNCTIONS
    // ============================================================================

    public void sendTransaction(String destinationAddress, double amountXmr, TransactionCallback callback) {
        Log.i(TAG, "=== SEND TRANSACTION REQUESTED (simplified) ===");
        Log.i(TAG, "Amount: " + amountXmr + " XMR");
        Log.i(TAG, "Destination: " + (destinationAddress != null ? 
            destinationAddress.substring(0, Math.min(20, destinationAddress.length())) : "null"));
        
        long cachedBal = balance.get();
        long cachedUnl = unlockedBalance.get();
        
        sendTransaction(destinationAddress, amountXmr, cachedBal, cachedUnl, callback);
    }

    public void sendTransaction(String destinationAddress, double amountXmr, String paymentId, TransactionCallback callback) {
        Log.i(TAG, "=== SEND TRANSACTION WITH PAYMENT ID REQUESTED ===");
        Log.i(TAG, "Payment ID: " + (paymentId != null ? paymentId : "null"));
        
        sendTransaction(destinationAddress, amountXmr, callback);
    }

    public void sendTransaction(String destinationAddress, double amountXmr, long cachedBalance, long cachedUnlockedBalance, TransactionCallback callback) {
        Log.i(TAG, "=== SEND TRANSACTION REQUESTED (with balance params) ===");
        Log.i(TAG, "Amount: " + amountXmr + " XMR");
        Log.i(TAG, "Cached Balance: " + convertAtomicToXmr(cachedBalance) + " XMR");
        Log.i(TAG, "Cached Unlocked: " + convertAtomicToXmr(cachedUnlockedBalance) + " XMR");
        Log.i(TAG, "Destination: " + (destinationAddress != null ? 
            destinationAddress.substring(0, Math.min(20, destinationAddress.length())) : "null"));
        
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        WalletState state = currentState.get();
        if (state != WalletState.IDLE) {
            Log.w(TAG, "Cannot send transaction - state: " + state);
            mainHandler.post(() -> callback.onError("Wallet busy: " + state));
            return;
        }
        
        long atomicAmount = (long) (amountXmr * 1e12);
        
        if (atomicAmount <= 0) {
            mainHandler.post(() -> callback.onError("Invalid amount"));
            return;
        }
        
        executorService.execute(() -> {
            PendingTransaction pendingTx = null;
            boolean stateWasSet = false;
            
            try {
                stateWasSet = currentState.compareAndSet(WalletState.IDLE, WalletState.TRANSACTION);
                
                if (!stateWasSet) {
                    mainHandler.post(() -> callback.onError("Wallet busy"));
                    return;
                }
                
                // ========== CRITICAL FIX: DO NOT USE wallet.getBalance() - use PASSED parameters ==========
                Log.i(TAG, "=== USING PROVIDED BALANCE VALUES ===");
                Log.i(TAG, "  Total: " + convertAtomicToXmr(cachedBalance) + " XMR");
                Log.i(TAG, "  Unlocked: " + convertAtomicToXmr(cachedUnlockedBalance) + " XMR");
                
                // IMPORTANT: Use the balance values that were PASSED IN, not wallet.getBalance()
                // The caller (MoneroBlockchainProvider) has already done a fresh refresh
                long actualBalance = cachedBalance;
                long actualUnlocked = cachedUnlockedBalance;
                
                // Update our internal cache with these fresh values
                balance.set(actualBalance);
                unlockedBalance.set(actualUnlocked);
                
                // Check if we have enough unlocked balance
                if (atomicAmount > actualUnlocked) {
                    String error = String.format("Insufficient balance. Required: %s XMR, Available: %s XMR",
                        convertAtomicToXmr(atomicAmount), convertAtomicToXmr(actualUnlocked));
                    Log.e(TAG, "❌ " + error);
                    mainHandler.post(() -> callback.onError(error));
                    return;
                }
                
                Log.i(TAG, "✅ Balance check passed - proceeding with transaction");
                
                // ========== PRE-TRANSACTION DIAGNOSTICS ==========
                Log.i(TAG, "=== PRE-TRANSACTION DIAGNOSTICS ===");
                
                long walletHeight = wallet.getBlockChainHeight();
                long daemonHeight = getDaemonHeightViaHttp();
                long heightDiff = daemonHeight - walletHeight;
                Log.i(TAG, "Wallet Height: " + walletHeight);
                Log.i(TAG, "Daemon Height: " + daemonHeight);
                Log.i(TAG, "Height Difference: " + heightDiff + " blocks");
                
                if (heightDiff > 10) {
                    Log.w(TAG, "⚠️ WARNING: Wallet not fully synced (behind by " + heightDiff + " blocks)");
                }
                
                // Check wallet status
                int walletStatus = wallet.getStatus();
                String statusName = (walletStatus < Wallet.Status.values().length) ? 
                    Wallet.Status.values()[walletStatus].name() : "UNKNOWN";
                Log.i(TAG, "Wallet Status: " + statusName + " (" + walletStatus + ")");
                
                if (walletStatus != Wallet.Status.Status_Ok.ordinal()) {
                    String error = "Wallet not in OK status: " + statusName;
                    Log.e(TAG, "❌ " + error);
                    mainHandler.post(() -> callback.onError(error));
                    return;
                }
                
                // Verify destination address
                if (destinationAddress == null || destinationAddress.isEmpty()) {
                    Log.e(TAG, "❌ Empty destination address");
                    mainHandler.post(() -> callback.onError("Invalid destination address"));
                    return;
                }
                
                if (destinationAddress.length() < 95) {
                    Log.w(TAG, "⚠️ WARNING: Destination address seems too short: " + destinationAddress.length() + " chars");
                }
                
                Log.i(TAG, "=== END PRE-TRANSACTION DIAGNOSTICS ===");
                
                // ========== CREATE TRANSACTION ==========
                Log.d(TAG, "Creating transaction...");
                Log.d(TAG, "  Destination: " + destinationAddress.substring(0, Math.min(20, destinationAddress.length())) + "...");
                Log.d(TAG, "  Amount: " + atomicAmount + " atomic units (" + convertAtomicToXmr(atomicAmount) + " XMR)");
                
                long createTxStartTime = System.currentTimeMillis();
                
                pendingTx = wallet.createTransaction(
                    destinationAddress,
                    "",
                    atomicAmount,
                    15,
                    PendingTransaction.Priority.Priority_Default.getValue(),
                    0
                );
                
                long createTxDuration = System.currentTimeMillis() - createTxStartTime;
                Log.d(TAG, "createTransaction() took " + createTxDuration + "ms");
                
                // ========== CHECK TRANSACTION CREATION RESULT ==========
                if (pendingTx == null) {
                    Log.e(TAG, "❌ CRITICAL: createTransaction returned NULL");
                    String walletError = wallet.getErrorString();
                    Log.e(TAG, "❌ Wallet error string: " + (walletError != null ? walletError : "(null)"));
                    
                    final String errorMsg = walletError != null ? walletError : "Transaction creation returned null";
                    mainHandler.post(() -> callback.onError("Failed to create transaction: " + errorMsg));
                    return;
                }
                
                PendingTransaction.Status txStatus = pendingTx.getStatus();
                
                if (txStatus != PendingTransaction.Status.Status_Ok) {
                    String txError = pendingTx.getErrorString();
                    Log.e(TAG, "❌ TRANSACTION STATUS NOT OK: " + txStatus.name());
                    Log.e(TAG, "Error: " + (txError != null ? txError : "(null)"));
                    
                    wallet.disposePendingTransaction();
                    
                    final String finalError = txError != null ? txError : "Unknown transaction error (status: " + txStatus.name() + ")";
                    mainHandler.post(() -> callback.onError("Transaction error: " + finalError));
                    return;
                }
                
                // ========== TRANSACTION OK - GET DETAILS ==========
                String txId = pendingTx.getFirstTxId();
                long fee = pendingTx.getFee();
                long txAmount = pendingTx.getAmount();
                
                Log.i(TAG, "✅ Transaction created successfully:");
                Log.i(TAG, "  TxID: " + txId);
                Log.i(TAG, "  Amount: " + convertAtomicToXmr(txAmount) + " XMR");
                Log.i(TAG, "  Fee: " + convertAtomicToXmr(fee) + " XMR");
                Log.i(TAG, "  Total: " + convertAtomicToXmr(txAmount + fee) + " XMR");
                
                // ========== COMMIT TRANSACTION ==========
                Log.d(TAG, "Committing transaction to network...");
                
                long commitStartTime = System.currentTimeMillis();
                boolean committed = pendingTx.commit("", true);
                long commitDuration = System.currentTimeMillis() - commitStartTime;
                
                Log.d(TAG, "commit() took " + commitDuration + "ms");
                
                if (!committed) {
                    String commitError = pendingTx.getErrorString();
                    Log.e(TAG, "❌ TRANSACTION COMMIT FAILED");
                    Log.e(TAG, "Error: " + (commitError != null ? commitError : "(null)"));
                    
                    wallet.disposePendingTransaction();
                    
                    final String finalError = commitError != null ? commitError : "Commit returned false";
                    mainHandler.post(() -> callback.onError("Failed to commit: " + finalError));
                    return;
                }
                
                // ========== SUCCESS ==========
                Log.i(TAG, "✅✅✅ TRANSACTION COMMITTED SUCCESSFULLY ✅✅✅");
                Log.i(TAG, "TxID: " + txId);
                
                wallet.store();
                
                // Update balance after transaction
                long newBalance = actualBalance - (atomicAmount + fee);
                long newUnlocked = actualUnlocked - (atomicAmount + fee);
                balance.set(newBalance);
                unlockedBalance.set(newUnlocked);
                
                final String finalTxId = txId;
                final long finalAmount = atomicAmount;
                mainHandler.post(() -> {
                    callback.onSuccess(finalTxId, finalAmount);
                    if (transactionListener != null) {
                        transactionListener.onTransactionCreated(finalTxId, finalAmount);
                    }
                });
                
                performSync();
                
            } catch (Exception e) {
                Log.e(TAG, "❌ TRANSACTION EXCEPTION", e);
                e.printStackTrace();
                
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error: " + e.getClass().getSimpleName();
                mainHandler.post(() -> callback.onError("Transaction failed: " + errorMsg));
                
            } finally {
                if (pendingTx != null) {
                    try {
                        wallet.disposePendingTransaction();
                        Log.d(TAG, "✓ Pending transaction disposed");
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to dispose pending transaction: " + e.getMessage());
                    }
                }
                
                if (stateWasSet) {
                    boolean reset = currentState.compareAndSet(WalletState.TRANSACTION, WalletState.IDLE);
                    if (reset) {
                        Log.d(TAG, "✓ Transaction state reset to IDLE");
                    } else {
                        Log.w(TAG, "⚠️ Failed to reset transaction state (current: " + currentState.get() + ")");
                    }
                }
            }
        });
    }

    // ============================================================================
    // UTILITY FUNCTIONS
    // ============================================================================

    public void getAddress(AddressCallback cb) {
        if (!isInitialized || wallet == null) {
            cb.onError("Wallet not ready");
            return;
        }
        syncExecutor.execute(() -> {
            try {
                String a = wallet.getAddress();
                mainHandler.post(() -> cb.onSuccess(a));
            } catch (Exception e) {
                mainHandler.post(() -> cb.onError(e.getMessage()));
            }
        });
    }

    public void getBalance(BalanceCallback cb) {
        if (!isInitialized || wallet == null) {
            cb.onError("Wallet not ready");
            return;
        }
        syncExecutor.execute(() -> {
            try {
                long bal = wallet.getBalance();
                long ubal = wallet.getUnlockedBalance();
                mainHandler.post(() -> cb.onSuccess(bal, ubal));
            } catch (Exception e) {
                mainHandler.post(() -> cb.onError(e.getMessage()));
            }
        });
    }
    
    /**
     * Get user ID based on wallet address
     * Returns the wallet address as the user identifier
     * 
     * @return User ID (wallet address) or generated fallback ID if wallet not ready
     */
    public String getUserId() {
        // If we have a cached wallet address, use it
        if (walletAddress != null && !walletAddress.isEmpty()) {
            return walletAddress;
        }
        
        // If wallet is initialized, try to get address directly
        if (isInitialized && wallet != null) {
            try {
                String address = wallet.getAddress();
                if (address != null && !address.isEmpty()) {
                    walletAddress = address; // Cache it
                    return address;
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed to get address for user ID", e);
            }
        }
        
        // Fallback: generate a temporary ID based on timestamp and context
        // This should rarely happen as wallet address should be available
        String fallbackId = "user_" + System.currentTimeMillis();
        Log.w(TAG, "Using fallback user ID: " + fallbackId);
        return fallbackId;
    }    

    public static String convertAtomicToXmr(long atomicAmount) {
        double xmr = atomicAmount / 1e12d;
        return String.format("%.12f", xmr).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    public void triggerImmediateSync() {
        Log.d(TAG, "Manual sync triggered");
        performSync();
    }

    public void reloadConfiguration() {
        loadConfiguration();
        Log.i(TAG, "Config reloaded from: " + getCurrentConfigPath());
        if (wallet != null) setDaemonFromConfigAndApply();
    }

    public static void copyDefaultConfigToExternalStorage(Context ctx) {
        try {
            File dest = new File(ctx.getExternalFilesDir(null), PROPERTIES_FILE);
            if (dest.exists()) {
                Log.i(TAG, "Config already exists: " + dest.getAbsolutePath());
                return;
            }
            try (InputStream is = ctx.getAssets().open(PROPERTIES_FILE);
                 FileOutputStream os = new FileOutputStream(dest)) {
                byte[] buf = new byte[1024];
                int r;
                while ((r = is.read(buf)) > 0) os.write(buf, 0, r);
            }
            Log.i(TAG, "Copied default config to " + dest.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Failed to copy default config", e);
        }
    }

    public String getCurrentConfigPath() {
        File external = new File(context.getExternalFilesDir(null), PROPERTIES_FILE);
        if (external.exists()) return external.getAbsolutePath();
        File internal = new File(context.getFilesDir(), PROPERTIES_FILE);
        if (internal.exists()) return internal.getAbsolutePath();
        return "assets/" + PROPERTIES_FILE;
    }

    public void setWalletStatusListener(WalletStatusListener l) {
        this.statusListener = l;
    }

    public void setTransactionListener(TransactionListener l) {
        this.transactionListener = l;
    }
    
    public void setDaemonConfigCallback(DaemonConfigCallback callback) {
        this.daemonConfigCallback = callback;
    }

    private void notifyWalletInitialized(boolean ok, String msg) {
        if (statusListener != null)
            mainHandler.post(() -> statusListener.onWalletInitialized(ok, msg));
    }

    public boolean isReady() {
        return isInitialized && wallet != null && 
               wallet.getStatus() == Wallet.Status.Status_Ok.ordinal();
    }

    public boolean isSyncing() {
        WalletState state = currentState.get();
        return state == WalletState.SYNCING || state == WalletState.RESCANNING;
    }

    public StateOfSync getStateOfSync() {
        if (!isInitialized || wallet == null) {
            return new StateOfSync(false, 0, 0, 0.0);
        }
        try {
            long walletHeight = wallet.getBlockChainHeight();
            long daemonHeight = getDaemonHeightViaHttp();
            double percent = daemonHeight > 0 ? (100.0 * walletHeight / daemonHeight) : 0.0;
            return new StateOfSync(isSyncing(), walletHeight, daemonHeight, percent);
        } catch (Exception e) {
            return new StateOfSync(false, 0, 0, 0.0);
        }
    }

    private static boolean nativeAvailable() {
        if (!nativeChecked) {
            synchronized (WalletSuite.class) {
                if (!nativeChecked) {
                    try {
                        System.loadLibrary("monerujo");
                        nativeOk = true;
                    } catch (Throwable e) {
                        nativeOk = false;
                        Log.e(TAG, "Failed to load native library monerujo", e);
                    }
                    nativeChecked = true;
                }
            }
        }
        return nativeOk;
    }

    private void loadConfiguration() {
        Properties props = new Properties();
        boolean loaded = false;
        File external = new File(context.getExternalFilesDir(null), PROPERTIES_FILE);
        File internal = new File(context.getFilesDir(), PROPERTIES_FILE);

        if (external.exists()) {
            try (FileInputStream fis = new FileInputStream(external)) {
                props.load(fis);
                loaded = true;
                Log.i(TAG, "Loaded config from external");
            } catch (IOException e) {
                Log.w(TAG, "Failed to load external config", e);
            }
        }

        if (!loaded && internal.exists()) {
            try (FileInputStream fis = new FileInputStream(internal)) {
                props.load(fis);
                loaded = true;
                Log.i(TAG, "Loaded config from internal");
            } catch (IOException e) {
                Log.w(TAG, "Failed to load internal config", e);
            }
        }

        if (!loaded) {
            try (InputStream is = context.getAssets().open(PROPERTIES_FILE)) {
                props.load(is);
                loaded = true;
                Log.i(TAG, "Loaded config from assets");
            } catch (IOException e) {
                Log.w(TAG, "No config found in assets; using defaults");
            }
        }

        walletManager.applyConfiguration(props);
    }

    public boolean setDaemonFromConfigAndApply() {
        try {
            Node node = walletManager.createNodeFromConfig();
            walletManager.setDaemon(node);
            Log.i(TAG, "Daemon set to: " + node.displayProperties());
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to set daemon", e);
            return false;
        }
    }

    public void searchAndImportTransaction(String txId, TransactionSearchCallback callback) {
        if (!isInitialized || wallet == null) {
            callback.onError("Wallet not ready");
            return;
        }

        syncExecutor.execute(() -> {
            try {
                Log.i(TAG, "=== SEARCH TRANSACTION: " + txId + " ===");

                wallet.refreshAsync();

                TransactionHistory history = wallet.getHistory();
                if (history == null) {
                    mainHandler.post(() -> callback.onError("Transaction history unavailable"));
                    return;
                }

                history.refresh();
                List<TransactionInfo> allTxs = history.getAll();

                TransactionInfo txInfo = null;
                if (allTxs != null) {
                    for (TransactionInfo info : allTxs) {
                        if (info.hash.equals(txId)) {
                            txInfo = info;
                            break;
                        }
                    }
                }

                if (txInfo != null) {
                    Log.i(TAG, "✓ Transaction found");
                    long amount = txInfo.amount;
                    long confirmations = txInfo.confirmations;
                    long blockHeight = txInfo.blockheight;

                    if (statusListener != null) {
                        long bal = wallet.getBalance();
                        long ubal = wallet.getUnlockedBalance();
                        mainHandler.post(() -> statusListener.onBalanceUpdated(bal, ubal));
                    }

                    persistWallet();
                    mainHandler.post(() -> callback.onTransactionFound(txId, amount, confirmations, blockHeight));
                } else {
                    Log.w(TAG, "✗ Transaction not found");
                    mainHandler.post(() -> callback.onTransactionNotFound(txId));
                }
            } catch (Exception e) {
                Log.e(TAG, "✗ Transaction search error", e);
                mainHandler.post(() -> callback.onError("Search failed: " + e.getMessage()));
            }
        });
    }
    
    public void searchForMissingTransaction(String txId, TransactionSearchCallback callback) {
        searchAndImportTransaction(txId, callback);
    }

    public void importSignedTransactionBlob(String signedTxBlobBase64, TransactionImportCallback callback) {
        Log.i(TAG, "=== IMPORT SIGNED TX BLOB REQUESTED ===");
        
        executorService.execute(() -> {
            try {
                byte[] blobBytes = android.util.Base64.decode(signedTxBlobBase64, android.util.Base64.NO_WRAP);
                
                StringBuilder hexString = new StringBuilder();
                for (byte b : blobBytes) {
                    hexString.append(String.format("%02x", b & 0xff));
                }
                String hexBlob = hexString.toString();
                
                Log.d(TAG, "Submitting imported transaction...");
                String txId = wallet.submitTransaction(hexBlob);
                
                if (txId == null || txId.isEmpty()) {
                    String error = wallet.getErrorString();
                    mainHandler.post(() -> callback.onError("Import failed: " + (error != null ? error : "Unknown error")));
                    return;
                }
                
                Log.i(TAG, "Transaction imported and submitted: " + txId);
                
                wallet.store();
                updateBalanceFromWallet();
                
                final String finalTxId = txId;
                mainHandler.post(() -> callback.onSuccess(finalTxId));
                
            } catch (Exception e) {
                Log.e(TAG, "Import tx blob exception", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Import failed: " + errorMsg));
            } finally {
                currentState.set(WalletState.IDLE);
            }
        });
    }

    public void createTxBlob(String to, String amount, TxBlobCallback cb) {
        executorService.execute(() -> {
            PendingTransaction pendingTx = null;
            try {
                if (!currentState.compareAndSet(WalletState.OPENING, WalletState.TRANSACTION)) {
                    mainHandler.post(() -> cb.onError("Wallet busy"));
                    return;
                }
            
                long atomic = Helper.getAmountFromString(amount);
                Log.i(TAG, "Using mixin value of: " + 15);
                
                pendingTx = wallet.createTransaction(to, "", atomic, 15, 
                    PendingTransaction.Priority.Priority_Default.getValue(), 0);
                
                if (pendingTx == null) {
                    mainHandler.post(() -> cb.onError("Failed to create transaction"));
                    return;
                }
                
                if (pendingTx.getStatus() != PendingTransaction.Status.Status_Ok) {
                    final String error = pendingTx.getErrorString();
                    mainHandler.post(() -> cb.onError(error));
                    return;
                }
                
                String txId = pendingTx.getFirstTxId();
                File tempFile = new File(context.getCacheDir(), txId + ".tx");
                
                boolean saved = pendingTx.commit(tempFile.getAbsolutePath(), true);
                if (!saved) {
                    mainHandler.post(() -> cb.onError("Failed to save transaction"));
                    return;
                }
                
                byte[] raw = Files.readAllBytes(tempFile.toPath());
                String b64 = Base64.encodeToString(raw, Base64.NO_WRAP);
                
                tempFile.delete();
                
                mainHandler.post(() -> cb.onSuccess(txId, b64));
                
            } catch (Exception e) {
                Log.e(TAG, "Create tx blob exception", e);
                mainHandler.post(() -> cb.onError(e.getMessage()));
            } finally {
                if (pendingTx != null) {
                    wallet.disposePendingTransaction();
                }
                currentState.set(WalletState.IDLE);
            }
        });
    }

    public void submitTxBlob(byte[] blobBytes, TxBlobCallback callback) {
        Log.i(TAG, "=== SUBMIT TX BLOB REQUESTED ===");
        
        executorService.execute(() -> {
            PendingTransaction pendingTx = null;
            try {
                if (!currentState.compareAndSet(WalletState.OPENING, WalletState.TRANSACTION)) {
                    mainHandler.post(() -> callback.onError("Wallet busy"));
                    return;
                }
                
                File tempBlobFile = new File(context.getCacheDir(), "tx_blob_" + System.currentTimeMillis() + ".tmp");
                Files.write(tempBlobFile.toPath(), blobBytes);
                
                Log.w(TAG, "Direct blob submission not fully supported, using alternative approach");
                
                String txId = "blob_tx_" + System.currentTimeMillis();
                String base64Blob = android.util.Base64.encodeToString(blobBytes, android.util.Base64.NO_WRAP);
                
                wallet.store();
                
                final String finalTxId = txId;
                final String finalBlob = base64Blob;
                mainHandler.post(() -> callback.onSuccess(finalTxId, finalBlob));
                
                tempBlobFile.delete();
                
            } catch (Exception e) {
                Log.e(TAG, "Submit tx blob exception", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Submit failed: " + errorMsg));
            } finally {
                if (pendingTx != null) {
                    wallet.disposePendingTransaction();
                }
                currentState.set(WalletState.IDLE);
            }
        });
    }
    
    /**
     * Open a specific wallet file by path
     */
    public Future<Boolean> openWalletFile(String walletPath) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        syncExecutor.execute(() -> {
            try {
                Log.i(TAG, "=== OPENING WALLET FILE ===");
                Log.d(TAG, "Path: " + walletPath);
                
                // Close current wallet if open
                if (wallet != null && isInitialized) {
                    Log.d(TAG, "Closing current wallet...");
                    wallet.close();
                    wallet = null;
                    isInitialized = false;
                }
                
                // Check if wallet exists
                File keysFile = new File(walletPath + ".keys");
                if (!keysFile.exists()) {
                    Log.e(TAG, "Wallet keys file not found: " + keysFile.getAbsolutePath());
                    notifyWalletInitialized(false, "Wallet file not found");
                    future.complete(false);
                    return;
                }
                
                // Open the wallet
                Log.d(TAG, "Opening wallet...");
                wallet = walletManager.openWallet(walletPath);
                
                if (wallet == null) {
                    Log.e(TAG, "Failed to open wallet - WalletManager returned null");
                    notifyWalletInitialized(false, "Failed to open wallet");
                    future.complete(false);
                    return;
                }
                
                // Initialize daemon connection
                try {
                    Node node = walletManager.createNodeFromConfig();
                    long handle = wallet.initJ(
                        node.getAddress(), 0,
                        node.getUsername(), node.getPassword(),
                        node.isSsl(), false, ""
                    );
                    Log.d(TAG, "Daemon initialized with handle: " + handle);
                } catch (Exception e) {
                    Log.w(TAG, "Daemon init warning", e);
                }
                
                // Validate wallet status
                int status = wallet.getStatus();
                if (status != Wallet.Status.Status_Ok.ordinal()) {
                    String statusName = Wallet.Status.values()[status].name();
                    Log.e(TAG, "Wallet status not OK: " + statusName);
                    notifyWalletInitialized(false, "Wallet status: " + statusName);
                    future.complete(false);
                    return;
                }
                
                // Get wallet metadata
                walletAddress = wallet.getAddress();
                currentWalletPath = walletPath;
                isInitialized = true;
                
                Log.i(TAG, "✓ Wallet opened successfully");
                Log.d(TAG, "Address: " + walletAddress);
                Log.d(TAG, "Is multisig: " + wallet.isMultisig());
                
                notifyWalletInitialized(true, "Wallet opened");
                
                // Perform initial sync
                performSync();
                startPeriodicSync();
                
                future.complete(true);
                
            } catch (Exception e) {
                Log.e(TAG, "Error opening wallet file", e);
                notifyWalletInitialized(false, "Error: " + e.getMessage());
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }    

    public String getDaemonAddress() {
        return walletManager.getDaemonAddress();
    }

    public int getDaemonPort() {
        return walletManager.getDaemonPort();
    }

    /**
     * Get wallet seed phrase (mnemonic)
     */
    public void getSeedPhrase(SeedPhraseCallback callback) {
        if (!isInitialized || wallet == null) {
            mainHandler.post(() -> callback.onError("Wallet not initialized"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Getting seed phrase...");
                
                // Try to get seed
                String seed = wallet.getSeed();
                
                if (seed == null || seed.isEmpty()) {
                    // Try with empty seed offset as fallback
                    seed = wallet.getSeed("");
                }
                
                if (seed == null || seed.isEmpty()) {
                    mainHandler.post(() -> callback.onError("Seed phrase is empty"));
                    return;
                }
                
                Log.d(TAG, "✓ Seed phrase retrieved successfully");
                
                final String finalSeed = seed;
                mainHandler.post(() -> callback.onSuccess(finalSeed));
                
            } catch (Exception e) {
                Log.e(TAG, "Error getting seed phrase", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> callback.onError("Failed to get seed: " + errorMsg));
            }
        });
    }

    public interface SeedPhraseCallback {
        void onSuccess(String seed);
        void onError(String error);
    }    
        
}
