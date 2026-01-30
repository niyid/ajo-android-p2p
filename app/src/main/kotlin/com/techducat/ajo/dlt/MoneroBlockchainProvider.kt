package com.techducat.ajo.dlt

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlinx.coroutines.delay
import com.m2049r.xmrwallet.model.TransactionInfo
import com.techducat.ajo.wallet.WalletSuite

// ============================================================================
// Data Classes
// ============================================================================

data class TransactionHistoryResult(
    val transactions: List<TransactionInfo>
)

data class RoscaCreationResult(
    val roscaId: String,
    val multisigAddress: String,
    val setupInfo: String
)

data class RoscaFinalizationResult(
    val roscaId: String,
    val multisigAddress: String,
    val isReady: Boolean
)

data class ContributionResult(
    val txId: String,
    val amount: Long,
    val roundNumber: Int
)

data class WalletInfo(
    val address: String,
    val balance: Double,
    val isSynced: Boolean,
    val syncProgress: Int,
    val blockHeight: Long
)

class MoneroBlockchainProvider(
    private val walletSuite: WalletSuite,
    private val ipfsProvider: IPFSProvider
) : DLTProvider {
    
    companion object {
        private const val TAG = "com.techducat.ajo.dlt.TransactionHistoryResult"
        private const val MAGIC_BYTES = "AJO:"
        private const val MAX_TX_EXTRA_SIZE = 1024
        private const val MAX_SYNC_WAIT_SECONDS = 300
        private const val SYNC_CHECK_INTERVAL_MS = 3000L
        private const val BALANCE_FETCH_RETRIES = 3
    }
    
    private val updateFlow = MutableSharedFlow<DLTUpdate>()
    private val json = Json { ignoreUnknownKeys = true }
    
    // ============================================================================
    // DLTProvider Interface Implementation
    // ============================================================================
    
    override suspend fun getMetadata(roscaId: String, metadataType: String): Result<String> = 
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🔍 getMetadata: roscaId=$roscaId, type=$metadataType")
                
                val historyResult = getTransactionHistory().getOrNull()
                    ?: return@withContext Result.failure(Exception("Failed to get transaction history"))
                
                for (tx in historyResult.transactions) {
                    val note = tx.notes ?: continue
                    if (note.startsWith(MAGIC_BYTES)) {
                        val data = note.substring(MAGIC_BYTES.length)
                        val envelope = json.decodeFromString<Map<String, String>>(data)
                        
                        if (envelope["type"] == metadataType && envelope["roscaId"] == roscaId) {
                            Log.i(TAG, "✅ Found matching metadata")
                            return@withContext Result.success(envelope["data"] ?: "")
                        }
                    }
                }
                Result.failure(Exception("Metadata not found"))
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exception in getMetadata", e)
                Result.failure(e)
            }
        }
    
    override suspend fun storeMetadata(
        roscaId: String,
        metadataType: String,
        data: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "💾 storeMetadata: type=$metadataType, roscaId=$roscaId")
            
            val wrappedData = "$MAGIC_BYTES{\"type\":\"$metadataType\",\"roscaId\":\"$roscaId\",\"data\":$data}"
            
            val dataToStore = if (wrappedData.length > MAX_TX_EXTRA_SIZE) {
                val ipfsHash = ipfsProvider.store(wrappedData).getOrThrow()
                "$MAGIC_BYTES{\"type\":\"$metadataType\",\"roscaId\":\"$roscaId\",\"ipfsHash\":\"$ipfsHash\"}"
            } else {
                wrappedData
            }
            
            val txHash = storeOnChain(dataToStore)
            Result.success(txHash)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to store metadata", e)
            Result.failure(e)
        }
    }
    
    override suspend fun storeRoscaMetadata(rosca: RoscaMetadata): Result<String> = 
        withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "💾 storeRoscaMetadata: roscaId=${rosca.roscaId}")
                
                val jsonData = json.encodeToString(rosca)
                
                val dataToStore = if (jsonData.length > MAX_TX_EXTRA_SIZE) {
                    val ipfsHash = ipfsProvider.store(jsonData).getOrThrow()
                    "$MAGIC_BYTES{\"type\":\"rosca_metadata\",\"ipfsHash\":\"$ipfsHash\"}"
                } else {
                    "$MAGIC_BYTES$jsonData"
                }
                
                val txHash = storeOnChain(dataToStore)
                Result.success(txHash)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error storing ROSCA metadata", e)
                Result.failure(e)
            }
        }
    
    override suspend fun getRoscaMetadata(roscaId: String): Result<RoscaMetadata> = 
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🔍 getRoscaMetadata: roscaId=$roscaId")
                
                val historyResult = getTransactionHistory().getOrNull()
                    ?: return@withContext Result.failure(Exception("Failed to get transaction history"))
                
                for (tx in historyResult.transactions) {
                    val note = tx.notes ?: continue
                    if (note.startsWith(MAGIC_BYTES)) {
                        try {
                            val data = note.substring(MAGIC_BYTES.length)
                            val metadata = json.decodeFromString<RoscaMetadata>(data)
                            if (metadata.roscaId == roscaId) {
                                return@withContext Result.success(metadata)
                            }
                        } catch (e: Exception) {
                            continue
                        }
                    }
                }
                Result.failure(Exception("ROSCA metadata not found"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    override suspend fun recordContribution(contribution: ContributionRecord): Result<String> = 
        withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "💰 recordContribution: ${contribution.roscaId}")
                
                val jsonData = json.encodeToString(contribution)
                val dataToStore = "$MAGIC_BYTES{\"type\":\"contribution\",\"data\":$jsonData}"
                val txHash = storeOnChain(dataToStore)
                
                updateFlow.emit(DLTUpdate.ContributionReceived(contribution))
                Result.success(txHash)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    override suspend fun recordDistribution(distribution: DistributionRecord): Result<String> = 
        withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "💸 recordDistribution: ${distribution.roscaId}")
                
                val jsonData = json.encodeToString(distribution)
                val dataToStore = "$MAGIC_BYTES{\"type\":\"distribution\",\"data\":$jsonData}"
                val txHash = storeOnChain(dataToStore)
                
                updateFlow.emit(DLTUpdate.DistributionCompleted(distribution))
                Result.success(txHash)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    override fun observeRoscaUpdates(roscaId: String): Flow<DLTUpdate> = updateFlow
    
    // ============================================================================
    // ROSCA Operations
    // ============================================================================
    
    suspend fun createRosca(
        roscaName: String,
        numMembers: Int,
        contributionAmount: Long,
        threshold: Int
    ): Result<RoscaCreationResult> = withContext(Dispatchers.IO) {
        try {
            val future = CompletableFuture<Result<RoscaCreationResult>>()
            
            walletSuite.createRosca(roscaName, numMembers, contributionAmount, threshold,
                object : WalletSuite.RoscaCreationCallback {
                    override fun onSuccess(roscaId: String, multisigAddress: String, setupInfo: String) {
                        future.complete(Result.success(
                            RoscaCreationResult(roscaId, multisigAddress, setupInfo)
                        ))
                    }
                    
                    override fun onError(error: String) {
                        future.complete(Result.failure(Exception(error)))
                    }
                })
            
            future.get()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun joinRosca(roscaId: String, setupInfo: String): Result<String> = 
        withContext(Dispatchers.IO) {
            try {
                val future = CompletableFuture<Result<String>>()
                
                walletSuite.joinRosca(roscaId, setupInfo, 
                    object : WalletSuite.RoscaJoinCallback {
                        override fun onSuccess(roscaId: String, yourMultisigInfo: String) {
                            future.complete(Result.success(yourMultisigInfo))
                        }
                        
                        override fun onError(error: String) {
                            future.complete(Result.failure(Exception(error)))
                        }
                    })
                
                future.get()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    suspend fun finalizeRoscaSetup(
        roscaId: String,
        allMemberMultisigInfos: List<String>,
        threshold: Int
    ): Result<RoscaFinalizationResult> = withContext(Dispatchers.IO) {
        try {
            val future = CompletableFuture<Result<RoscaFinalizationResult>>()
            
            walletSuite.finalizeRoscaSetup(roscaId, allMemberMultisigInfos, threshold,
                object : WalletSuite.RoscaFinalizeCallback {
                    override fun onSuccess(roscaId: String, multisigAddress: String, isReady: Boolean) {
                        future.complete(Result.success(
                            RoscaFinalizationResult(roscaId, multisigAddress, isReady)
                        ))
                    }
                    
                    override fun onError(error: String) {
                        future.complete(Result.failure(Exception(error)))
                    }
                })
            
            future.get()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun contributeToRosca(
        roscaId: String,
        contributionAmount: Long,
        roundNumber: Int
    ): Result<ContributionResult> = withContext(Dispatchers.IO) {
        try {
            val future = CompletableFuture<Result<ContributionResult>>()
            
            walletSuite.contributeToRosca(roscaId, contributionAmount, roundNumber,
                object : WalletSuite.RoscaContributionCallback {
                    override fun onSuccess(txId: String, amount: Long, roundNumber: Int) {
                        future.complete(Result.success(
                            ContributionResult(txId, amount, roundNumber)
                        ))
                    }
                    
                    override fun onError(error: String) {
                        future.complete(Result.failure(Exception(error)))
                    }
                })
            
            future.get()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRoscaState(roscaId: String): Result<WalletSuite.RoscaState> = 
        withContext(Dispatchers.IO) {
            try {
                val future = CompletableFuture<Result<WalletSuite.RoscaState>>()
                
                walletSuite.getRoscaState(roscaId, 
                    object : WalletSuite.RoscaStateCallback {
                        override fun onSuccess(state: WalletSuite.RoscaState) {
                            future.complete(Result.success(state))
                        }
                        
                        override fun onError(error: String) {
                            future.complete(Result.failure(Exception(error)))
                        }
                    })
                
                future.get()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    // ============================================================================
    // Query Operations
    // ============================================================================
    
    suspend fun getContributions(roscaId: String): Result<List<ContributionRecord>> = 
        withContext(Dispatchers.IO) {
            try {
                val contributions = mutableListOf<ContributionRecord>()
                
                val historyResult = getTransactionHistory().getOrNull()
                    ?: return@withContext Result.failure(Exception("Failed to get transaction history"))
                
                for (tx in historyResult.transactions) {
                    val note = tx.notes ?: continue
                    if (note.startsWith(MAGIC_BYTES)) {
                        try {
                            val data = note.substring(MAGIC_BYTES.length)
                            val envelope = json.decodeFromString<Map<String, String>>(data)
                            if (envelope["type"] == "contribution") {
                                val contribution = json.decodeFromString<ContributionRecord>(
                                    envelope["data"] ?: continue
                                )
                                if (contribution.roscaId == roscaId) {
                                    contributions.add(contribution)
                                }
                            }
                        } catch (e: Exception) {
                            continue
                        }
                    }
                }
                Result.success(contributions)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    suspend fun getDistributions(roscaId: String): Result<List<DistributionRecord>> = 
        withContext(Dispatchers.IO) {
            try {
                val distributions = mutableListOf<DistributionRecord>()

                val historyResult = getTransactionHistory().getOrNull()
                    ?: return@withContext Result.failure(Exception("Failed to get transaction history"))
                
                for (tx in historyResult.transactions) {
                    val note = tx.notes ?: continue
                    if (note.startsWith(MAGIC_BYTES)) {
                        try {
                            val data = note.substring(MAGIC_BYTES.length)
                            val envelope = json.decodeFromString<Map<String, String>>(data)
                            if (envelope["type"] == "distribution") {
                                val distribution = json.decodeFromString<DistributionRecord>(
                                    envelope["data"] ?: continue
                                )
                                if (distribution.roscaId == roscaId) {
                                    distributions.add(distribution)
                                }
                            }
                        } catch (e: Exception) {
                            continue
                        }
                    }
                }
                Result.success(distributions)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    suspend fun verifyIntegrity(roscaId: String): Result<Boolean> = 
        withContext(Dispatchers.IO) {
            try {
                val contributions = getContributions(roscaId).getOrThrow()
                
                for (contribution in contributions) {
                    if (findTransaction(contribution.txHash) == null) {
                        return@withContext Result.success(false)
                    }
                }
                
                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    suspend fun getTransactionInfo(txHash: String): JSONObject? = 
        withContext(Dispatchers.IO) {
            try {
                val tx = findTransaction(txHash)
                tx?.let {
                    JSONObject().apply {
                        put("hash", it.hash)
                        put("confirmations", it.confirmations)
                        put("timestamp", it.timestamp)
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    
    suspend fun getWalletInfo(): Result<WalletInfo> = withContext(Dispatchers.IO) {
        try {
            val address = walletSuite.getCachedAddress() ?: "N/A"
            
            val balanceFuture = CompletableFuture<Pair<Long, Long>>()
            walletSuite.getBalance(object : WalletSuite.BalanceCallback {
                override fun onSuccess(balance: Long, unlocked: Long) {
                    balanceFuture.complete(Pair(balance, unlocked))
                }
                
                override fun onError(error: String) {
                    balanceFuture.completeExceptionally(Exception(error))
                }
            })
            
            val (balance, _) = balanceFuture.get()
            val balanceXmr = balance / 1e12
            
            val syncStatus = walletSuite.getStateOfSync()
            val isSynced = !syncStatus.syncing && syncStatus.percentDone >= 99.0
            
            Result.success(WalletInfo(
                address = address,
                balance = balanceXmr,
                isSynced = isSynced,
                syncProgress = syncStatus.percentDone.toInt(),
                blockHeight = syncStatus.walletHeight
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ============================================================================
    // Transaction Operations
    // ============================================================================
    
    suspend fun sendTransaction(
        recipientAddress: String,
        amount: Double,
        priority: Int = 1
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val balanceFuture = CompletableFuture<Pair<Long, Long>>()
            walletSuite.getBalance(object : WalletSuite.BalanceCallback {
                override fun onSuccess(balance: Long, unlocked: Long) {
                    balanceFuture.complete(Pair(balance, unlocked))
                }
                
                override fun onError(error: String) {
                    balanceFuture.completeExceptionally(Exception(error))
                }
            })
            
            val (balance, _) = balanceFuture.get()
            val balanceXmr = balance / 1e12
            
            if (amount > balanceXmr) {
                return@withContext Result.failure(
                    Exception("Insufficient balance. Required: $amount XMR, Available: $balanceXmr XMR")
                )
            }
            
            val txFuture = CompletableFuture<Result<String>>()
            
            walletSuite.sendTransaction(recipientAddress, amount, 
                object : WalletSuite.TransactionCallback {
                    override fun onSuccess(txId: String, amount: Long) {
                        txFuture.complete(Result.success(txId))
                    }
                    
                    override fun onError(error: String) {
                        txFuture.complete(Result.failure(Exception(error)))
                    }
                })
            
            txFuture.get()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ============================================================================
    // Private Helper Methods
    // ============================================================================
    
    private suspend fun storeOnChain(data: String): String = withContext(Dispatchers.IO) {
        val METADATA_AMOUNT_XMR = 0.00003
        val FEE_BUFFER_XMR = 0.0001
        val TOTAL_MIN_XMR = METADATA_AMOUNT_XMR + FEE_BUFFER_XMR
        
        Log.i(TAG, "=== STORE ON CHAIN START ===")
        var stoppedPeriodicSync = false
        
        try {
            // STEP 1: Check if wallet is ready
            Log.d(TAG, "[1/9] Checking if wallet is ready...")
            var walletReadyRetries = 0
            val maxWalletRetries = 15
            
            while (!walletSuite.isReady() && walletReadyRetries < maxWalletRetries) {
                Log.d(TAG, "⏳ Wallet not ready yet, waiting... (${walletReadyRetries + 1}/$maxWalletRetries)")
                delay(1000)
                walletReadyRetries++
            }
            
            if (!walletSuite.isReady()) {
                throw Exception("❌ Wallet failed to initialize after $maxWalletRetries seconds")
            }
            Log.i(TAG, "✅ Wallet is ready")
            
            // STEP 2: STOP PERIODIC SYNC
            Log.d(TAG, "[2/9] Stopping periodic sync to prevent conflicts...")
            try {
                walletSuite.stopPeriodicSync()
                stoppedPeriodicSync = true
                Log.i(TAG, "✅ Periodic sync stopped")
                delay(1000)
            } catch (e: Exception) {
                Log.w(TAG, "⚠️ Could not stop periodic sync: ${e.message}")
            }
            
            // STEP 3: FORCE IMMEDIATE WALLET REFRESH
            Log.d(TAG, "[3/9] Forcing immediate wallet refresh...")
            try {
                walletSuite.triggerImmediateSync()
                Log.d(TAG, "✓ Immediate sync triggered")
            } catch (e: Exception) {
                Log.w(TAG, "⚠️ Could not trigger immediate sync: ${e.message}")
            }
            
            // STEP 4: Wait for sync/refresh to complete
            Log.d(TAG, "[4/9] Waiting for wallet refresh to complete...")
            var syncWaitAttempts = 0
            val maxSyncWaitAttempts = 60
            
            while (syncWaitAttempts < maxSyncWaitAttempts) {
                val syncStatus = walletSuite.getStateOfSync()
                
                if (!syncStatus.syncing && syncStatus.percentDone >= 99.0) {
                    Log.i(TAG, "✅ Wallet refresh complete (${syncStatus.percentDone}%)")
                    break
                }
                
                if (syncWaitAttempts % 5 == 0) {
                    val remaining = (maxSyncWaitAttempts - syncWaitAttempts) * 2
                    Log.d(TAG, "⏳ Refreshing: ${syncStatus.percentDone}% (${syncStatus.walletHeight}/${syncStatus.daemonHeight}) - ${remaining}s remaining")
                }
                
                delay(2000)
                syncWaitAttempts++
            }
            
            // Extra delay to ensure balance is updated
            delay(2000)
            
            // STEP 5: CRITICAL FIX - Force WalletSuite to do a complete balance refresh
            Log.d(TAG, "[5/9] Forcing complete balance refresh cycle...")
            
            // This will trigger WalletSuite to call wallet.refresh() and update its cached balance
            walletSuite.forceBalanceRefresh()
            
            // Wait for the refresh to complete
            delay(3000)
            
            // NOW get balance - should be fresh
            var balance = 0L
            var unlocked = 0L
            var balanceRetrieved = false
            
            for (attempt in 1..BALANCE_FETCH_RETRIES) {
                try {
                    val balanceFuture = CompletableFuture<Pair<Long, Long>>()
                    
                    walletSuite.getBalance(object : WalletSuite.BalanceCallback {
                        override fun onSuccess(bal: Long, unl: Long) {
                            Log.d(TAG, "✅ Balance retrieved (attempt $attempt):")
                            Log.d(TAG, "   Total: ${bal / 1e12} XMR")
                            Log.d(TAG, "   Unlocked: ${unl / 1e12} XMR")
                            balanceFuture.complete(Pair(bal, unl))
                        }
                        override fun onError(error: String) {
                            Log.e(TAG, "❌ Balance error (attempt $attempt): $error")
                            balanceFuture.completeExceptionally(Exception(error))
                        }
                    })
                    
                    val result = balanceFuture.get(10, TimeUnit.SECONDS)
                    balance = result.first
                    unlocked = result.second
                    balanceRetrieved = true
                    
                    if (unlocked > 0) {
                        Log.i(TAG, "✅ Non-zero balance found on attempt $attempt")
                        break
                    }
                    
                    if (attempt < BALANCE_FETCH_RETRIES) {
                        Log.w(TAG, "⚠️ Zero balance on attempt $attempt, retrying...")
                        delay(3000)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "⚠️ Balance fetch attempt $attempt failed: ${e.message}")
                    if (attempt < BALANCE_FETCH_RETRIES) {
                        delay(2000)
                    } else {
                        throw Exception("Failed to get balance after $BALANCE_FETCH_RETRIES attempts", e)
                    }
                }
            }
            
            if (!balanceRetrieved) {
                throw Exception("❌ Could not retrieve wallet balance after $BALANCE_FETCH_RETRIES attempts")
            }
            
            val unlockedXmr = unlocked / 1e12
            val totalXmr = balance / 1e12
            
            Log.d(TAG, "Balance check:")
            Log.d(TAG, "  Total: $totalXmr XMR")
            Log.d(TAG, "  Unlocked: $unlockedXmr XMR")
            Log.d(TAG, "  Required: $TOTAL_MIN_XMR XMR")
            
            if (unlockedXmr < TOTAL_MIN_XMR) {
                if (totalXmr > unlockedXmr) {
                    throw Exception("❌ Insufficient unlocked balance. Need $TOTAL_MIN_XMR XMR, have $unlockedXmr XMR unlocked ($totalXmr XMR total). Wait for ${(totalXmr - unlockedXmr)} XMR to unlock (needs 10 confirmations).")
                } else {
                    throw Exception("❌ Insufficient balance. Need $TOTAL_MIN_XMR XMR, have $unlockedXmr XMR.")
                }
            }
            
            Log.i(TAG, "✅ Sufficient balance confirmed")
            
            // STEP 6: Check final sync status
            Log.d(TAG, "[6/9] Final sync status check...")
            val finalSyncCheck = walletSuite.getStateOfSync()
            Log.d(TAG, "Final sync: syncing=${finalSyncCheck.syncing}, progress=${finalSyncCheck.percentDone}%")
            
            if (finalSyncCheck.syncing && finalSyncCheck.percentDone < 95.0) {
                throw Exception("❌ Wallet still syncing (${finalSyncCheck.percentDone}%). Please wait and try again.")
            }
            
            // STEP 7: Get wallet address
            Log.d(TAG, "[7/9] Getting wallet address...")
            val myAddress = walletSuite.getCachedAddress() 
                ?: throw Exception("❌ Cannot get wallet address")
            Log.d(TAG, "✅ Address: ${myAddress.substring(0, 10)}...")
            
            // STEP 8: Send transaction
            Log.d(TAG, "[8/9] Sending transaction with metadata...")
            Log.d(TAG, "Transaction params:")
            Log.d(TAG, "  To: ${myAddress.substring(0, 15)}...")
            Log.d(TAG, "  Amount: $METADATA_AMOUNT_XMR XMR")
            Log.d(TAG, "  Balance: $totalXmr XMR")
            Log.d(TAG, "  Unlocked: $unlockedXmr XMR")
            
            val txFuture = CompletableFuture<String>()
            
            // Pass the fresh balance values we just retrieved
            walletSuite.sendTransaction(myAddress, METADATA_AMOUNT_XMR, balance, unlocked,
                object : WalletSuite.TransactionCallback {
                    override fun onSuccess(txId: String, amountAtomic: Long) {
                        Log.i(TAG, "🎉 TRANSACTION SUCCESS!")
                        Log.i(TAG, "   TxID: $txId")
                        Log.i(TAG, "   Amount: ${amountAtomic / 1e12} XMR")
                        txFuture.complete(txId)
                    }
                    
                    override fun onError(error: String) {
                        Log.e(TAG, "🔥 TRANSACTION FAILED: $error")
                        txFuture.completeExceptionally(Exception("Transaction failed: $error"))
                    }
                })
            
            val txId = try {
                txFuture.get(120, TimeUnit.SECONDS)
            } catch (e: TimeoutException) {
                throw Exception("⏱️ Transaction timed out after 120 seconds", e)
            }
            
            Log.i(TAG, "=== STORE ON CHAIN COMPLETE ===")
            Log.i(TAG, "✅ Metadata stored with TxID: $txId")
            txId
            
        } catch (e: Exception) {
            Log.e(TAG, "💥 STORE ON CHAIN FAILED", e)
            Log.e(TAG, "   Error: ${e.message}")
            e.printStackTrace()
            throw Exception("Failed to store data on-chain: ${e.message}", e)
        } finally {
            // STEP 9: Restart periodic sync
            if (stoppedPeriodicSync) {
                try {
                    Log.d(TAG, "[9/9] Restarting periodic sync...")
                    delay(2000)
                    walletSuite.startPeriodicSync()
                    Log.i(TAG, "✅ Periodic sync restarted")
                } catch (e: Exception) {
                    Log.w(TAG, "⚠️ Could not restart periodic sync: ${e.message}")
                }
            }
            Log.i(TAG, "=== STORE ON CHAIN PROCESS ENDED ===")
        }
    }
    
    private suspend fun getTransactionHistory(): Result<TransactionHistoryResult> = 
        withContext(Dispatchers.IO) {
            try {
                val future = CompletableFuture<Result<TransactionHistoryResult>>()
                
                walletSuite.getTransactionHistory(
                    object : WalletSuite.TransactionHistoryCallback {
                        override fun onSuccess(transactions: List<TransactionInfo>) {
                            future.complete(Result.success(TransactionHistoryResult(transactions)))
                        }
                        
                        override fun onError(error: String) {
                            future.complete(Result.failure(Exception(error)))
                        }
                    })
                
                future.get()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    private suspend fun findTransaction(txHash: String): TransactionInfo? = 
        withContext(Dispatchers.IO) {
            try {
                val historyResult = getTransactionHistory().getOrNull()
                historyResult?.transactions?.find { it.hash == txHash }
            } catch (e: Exception) {
                null
            }
        }
}
