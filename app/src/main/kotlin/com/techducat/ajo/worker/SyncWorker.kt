package com.techducat.ajo.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONArray
import java.io.File

import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.ContributionEntity
import com.techducat.ajo.dlt.IPFSProvider
import com.techducat.ajo.dlt.MoneroBlockchainProvider
import com.techducat.ajo.util.Logger
import com.techducat.ajo.wallet.WalletSuite
import java.util.concurrent.TimeUnit
import com.m2049r.xmrwallet.model.TransactionInfo.Direction

/**
 * Background worker for syncing with DLT (IPFS + Monero Blockchain)
 */
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val database: AjoDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AjoDatabase::class.java,
            "ajo_database"
        ).build()
    }
    
    private val ipfsProvider: IPFSProvider by lazy {
        IPFSProvider.getInstance(applicationContext)
    }
    
    private val moneroProvider: MoneroBlockchainProvider by lazy {
        val walletSuite = WalletSuite.getInstance(applicationContext)
        MoneroBlockchainProvider(walletSuite, ipfsProvider)
    }
    
    private val walletSuite = WalletSuite.getInstance(context)
    
    private val prefs = context.getSharedPreferences("ajo_prefs", Context.MODE_PRIVATE)
    
    // Sync statistics
    private var recordsPushed = 0
    private var recordsPulled = 0
    private var transactionsVerified = 0
    private var conflictsResolved = 0
    private var errors = 0
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            Logger.i("SyncWorker: Starting sync cycle...")
            val startTime = System.currentTimeMillis()
            
            // Reset stats
            recordsPushed = 0
            recordsPulled = 0
            transactionsVerified = 0
            conflictsResolved = 0
            errors = 0
            
            // Check network connectivity
            if (!isNetworkAvailable()) {
                Logger.w("SyncWorker: No network connectivity, skipping sync")
                return@withContext Result.retry()
            }
            
            // Check if wallet is ready
            if (!isWalletReady()) {
                Logger.w("SyncWorker: Wallet not ready, skipping sync")
                return@withContext Result.retry()
            }
            
            // Step 1: Sync dirty records to IPFS
            syncDirtyRecordsToIPFS()
            
            // Step 2: Verify pending Monero transactions using MoneroBlockchainProvider
            verifyPendingTransactions()
            
            // Step 3: Update transaction statuses
            updateTransactionStatuses()
            
            // Step 4: Cleanup old data
            cleanupOldData()
            
            // Update sync metadata
            val duration = System.currentTimeMillis() - startTime
            updateSyncMetadata(duration)
            
            Logger.i("SyncWorker: Sync completed - Pushed: $recordsPushed, Pulled: $recordsPulled, " +
                    "Verified: $transactionsVerified, Conflicts: $conflictsResolved, Errors: $errors, " +
                    "Duration: ${duration}ms")
            
            // Determine result
            if (errors > 0 && recordsPushed == 0 && recordsPulled == 0) {
                Result.retry()
            } else {
                Result.success(createOutputData())
            }
            
        } catch (e: Exception) {
            Logger.e("SyncWorker: Sync failed - ${e.message}", e)
            errors++
            Result.retry()
        }
    }
    
    
    /**
     * Step 1: Push dirty (unsynced) records to IPFS
     */
    private suspend fun syncDirtyRecordsToIPFS() {
        try {
            Logger.d("SyncWorker: Syncing dirty records to IPFS...")
            
            // Get all dirty contributions
            val dirtyContributions: List<ContributionEntity> = database.contributionDao().getDirtyContributions()
            Logger.d("SyncWorker: Found ${dirtyContributions.size} dirty contributions")
            
            for (contribution in dirtyContributions) {
                try {
                    // Prepare contribution data for IPFS
                    val contributionData = prepareContributionDataForIPFS(contribution)
                    
                    // Upload to IPFS
                    val ipfsHash = ipfsProvider.uploadJson(contributionData.toString())
                    
                    if (ipfsHash != null) {
                        // Update contribution with IPFS hash
                        contribution.ipfsHash = ipfsHash
                        contribution.isDirty = false
                        contribution.lastSyncedAt = System.currentTimeMillis()
                        database.contributionDao().update(contribution)
                        
                        recordsPushed++
                        Logger.d("SyncWorker: Contribution ${contribution.id} synced to IPFS: $ipfsHash")
                    } else {
                        Logger.w("SyncWorker: Failed to upload contribution ${contribution.id} to IPFS")
                        errors++
                    }
                } catch (e: Exception) {
                    Logger.e("SyncWorker: Error syncing contribution ${contribution.id}: ${e.message}")
                    errors++
                }
            }
            
        } catch (e: Exception) {
            Logger.e("SyncWorker: Error in syncDirtyRecordsToIPFS: ${e.message}")
            errors++
        }
    }    
    
    /**
     * Step 2: Verify pending Monero transactions using MoneroBlockchainProvider
     */
    private suspend fun verifyPendingTransactions() {
        try {
            Logger.d("SyncWorker: Verifying pending transactions...")
            
            val pendingContributions: List<ContributionEntity> = database.contributionDao().getPendingContributions()
            Logger.d("SyncWorker: Found ${pendingContributions.size} pending contributions")
            
            for (contribution in pendingContributions) {
                try {
                    val txHash = contribution.txHash ?: continue
                    
                    // Query Monero blockchain for transaction status using MoneroBlockchainProvider
                    val txInfo = moneroProvider.getTransactionInfo(txHash)
                    
                    if (txInfo != null) {
                        val confirmations = txInfo.optInt("confirmations", 0)
                        val isConfirmed = confirmations >= 10 // Monero confirmation threshold
                        
                        if (isConfirmed && contribution.status != "confirmed") {
                            // Update contribution as confirmed
                            contribution.status = "confirmed"
                            contribution.verifiedAt = System.currentTimeMillis()
                            database.contributionDao().update(contribution)
                            
                            transactionsVerified++
                            Logger.d("SyncWorker: Contribution ${contribution.id} confirmed with $confirmations confirmations")
                            
                            // Send notification
                            sendTransactionConfirmedNotification(contribution)
                        }
                    } else {
                        // Check if transaction is too old (> 24 hours)
                        val age = System.currentTimeMillis() - contribution.createdAt
                        if (age > 24 * 60 * 60 * 1000) {
                            contribution.status = "failed"
                            database.contributionDao().update(contribution)
                            Logger.w("SyncWorker: Contribution ${contribution.id} marked as failed (timeout)")
                        }
                    }
                } catch (e: Exception) {
                    Logger.e("SyncWorker: Error verifying contribution ${contribution.id}: ${e.message}")
                    errors++
                }
            }
            
        } catch (e: Exception) {
            Logger.e("SyncWorker: Error in verifyPendingTransactions: ${e.message}")
            errors++
        }
    }
    
    /**
     * Step 3: Update transaction statuses from wallet
     */
    private suspend fun updateTransactionStatuses() {
        try {
            Logger.d("SyncWorker: Updating transaction statuses...")
            
            // Get wallet transaction history
            val walletTxs = getWalletTransactions()
            
            for (walletTx in walletTxs) {
                val txHash = walletTx.optString("hash", "")
                if (txHash.isEmpty()) continue
                
                // Find matching local contribution
                val localContribution = database.contributionDao().getByTxHash(txHash)
                
                if (localContribution != null) {
                    val updated = updateContributionFromWallet(localContribution, walletTx)
                    
                    if (updated) {
                        transactionsVerified++
                    }
                }
            }
            
        } catch (e: Exception) {
            Logger.e("SyncWorker: Error in updateTransactionStatuses: ${e.message}")
            errors++
        }
    }
    
    /**
     * Step 4: Cleanup old synced data
     */
    private suspend fun cleanupOldData() {
        try {
            Logger.d("SyncWorker: Cleaning up old data...")
            
            // Cleanup old sync logs
            cleanupSyncLogs()
            
        } catch (e: Exception) {
            Logger.e("SyncWorker: Error in cleanupOldData: ${e.message}")
        }
    }
    
    // ============ Helper Methods ============
    
    private fun prepareContributionDataForIPFS(contribution: ContributionEntity): JSONObject {
        return JSONObject().apply {
            put("id", contribution.id)
            put("groupId", contribution.roscaId)
            put("memberId", contribution.memberId)
            put("amount", contribution.amount)
            put("cycleNumber", contribution.cycleNumber)
            put("status", contribution.status)
            put("dueDate", contribution.dueDate)
            put("txHash", contribution.txHash)
            put("txId", contribution.txId)
            put("proofOfPayment", contribution.proofOfPayment)
            put("verifiedAt", contribution.verifiedAt)
            put("notes", contribution.notes)
            put("createdAt", contribution.createdAt)
            put("updatedAt", System.currentTimeMillis())
        }
    }
    
    private suspend fun getWalletTransactions(): List<JSONObject> {
        return try {
            val transactions = mutableListOf<JSONObject>()
            
            walletSuite.getUserWallet()?.let { wallet ->
                try {
                    val history = wallet.history
                    history?.refresh()
                    
                    val allTxs = history?.all
                    if (allTxs != null) {
                        for (tx in allTxs) {
                            val txJson = JSONObject().apply {
                                put("hash", tx.hash)
                                put("amount", tx.amount)
                                put("fee", tx.fee)
                                put("confirmations", tx.confirmations)
                                put("blockheight", tx.blockheight)
                                put("timestamp", tx.timestamp)
                                put("direction", when (tx.direction) {
                                    Direction.Direction_In -> "in"
                                    Direction.Direction_Out -> "out"
                                    else -> "unknown"
                                })
                                put("isPending", tx.isPending)
                                put("isFailed", tx.isFailed)
                            }
                            transactions.add(txJson)
                        }
                        
                        Logger.d("SyncWorker: Retrieved ${transactions.size} transactions from wallet")
                    }
                } catch (e: Exception) {
                    Logger.e("SyncWorker: Error getting wallet history: ${e.message}")
                }
            } ?: run {
                Logger.w("SyncWorker: Wallet not available for transaction retrieval")
            }
            
            transactions
            
        } catch (e: Exception) {
            Logger.e("SyncWorker: Error getting wallet transactions: ${e.message}")
            emptyList()
        }
    }
    
    private suspend fun updateContributionFromWallet(
        contribution: ContributionEntity,
        walletTx: JSONObject
    ): Boolean {
        return try {
            var updated = false
            val confirmations = walletTx.optInt("confirmations", 0)
            
            if (confirmations >= 10 && contribution.status != "confirmed") {
                contribution.status = "confirmed"
                contribution.verifiedAt = System.currentTimeMillis()
                database.contributionDao().update(contribution)
                updated = true
            }
            
            updated
        } catch (e: Exception) {
            Logger.e("SyncWorker: Error updating contribution from wallet: ${e.message}")
            false
        }
    }
    
    private suspend fun sendTransactionConfirmedNotification(contribution: ContributionEntity) {
        try {
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) 
                as NotificationManager
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "ajo_sync_notifications"
                val channelName = "Ajo Sync Notifications"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, channelName, importance).apply {
                    description = "Notifications for Ajo transaction confirmations"
                    enableLights(true)
                    lightColor = android.graphics.Color.GREEN
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 250, 500)
                }
                notificationManager.createNotificationChannel(channel)
            }
            
            val title = "Contribution Confirmed"
            val message = "Your contribution of ${contribution.amount} has been confirmed"
            
            val intent = try {
                Intent(applicationContext, Class.forName("com.techducat.ajo.ui.MainActivity")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            } catch (e: ClassNotFoundException) {
                Intent(applicationContext, Class.forName("com.techducat.ajo.ui.MainActivity")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            }
            
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                contribution.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notificationBuilder = NotificationCompat.Builder(applicationContext, "ajo_sync_notifications")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 500, 250, 500))
                .setLights(android.graphics.Color.GREEN, 1000, 3000)
            
            notificationManager.notify(contribution.id.hashCode(), notificationBuilder.build())
            
            Logger.d("SyncWorker: Contribution ${contribution.id} confirmation notification sent")
            
        } catch (e: Exception) {
            Logger.e("SyncWorker: Error sending notification: ${e.message}")
        }
    }
    
    private fun cleanupSyncLogs() {
        try {
            Logger.d("SyncWorker: Starting sync log cleanup...")
            
            val cutoffTime = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
            
            val allPrefs = prefs.all
            val keysToRemove = mutableListOf<String>()
            
            for ((key, value) in allPrefs) {
                if (key.startsWith("sync_log_") || key.startsWith("last_sync_")) {
                    if (value is Long && value < cutoffTime) {
                        keysToRemove.add(key)
                    }
                }
                
                if (key.startsWith("ipfs_hash_time_")) {
                    if (value is Long && value < cutoffTime) {
                        keysToRemove.add(key)
                        val recordId = key.removePrefix("ipfs_hash_time_")
                        keysToRemove.add("ipfs_hash_$recordId")
                    }
                }
            }
            
            if (keysToRemove.isNotEmpty()) {
                val editor = prefs.edit()
                keysToRemove.forEach { editor.remove(it) }
                editor.apply()
                Logger.d("SyncWorker: Removed ${keysToRemove.size} old preference keys")
            }
            
            try {
                val logsDir = File(applicationContext.filesDir, "sync_logs")
                if (logsDir.exists() && logsDir.isDirectory) {
                    val oldFiles = logsDir.listFiles { file ->
                        file.isFile && file.lastModified() < cutoffTime
                    }
                    
                    oldFiles?.forEach { file ->
                        if (file.delete()) {
                            Logger.d("SyncWorker: Deleted old log file: ${file.name}")
                        }
                    }
                }
            } catch (e: Exception) {
                Logger.e("SyncWorker: Error cleaning up log files: ${e.message}")
            }
            
            Logger.d("SyncWorker: Sync log cleanup completed")
            
        } catch (e: Exception) {
            Logger.e("SyncWorker: Error in cleanupSyncLogs: ${e.message}")
        }
    }
    
    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) 
                as android.net.ConnectivityManager
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } catch (e: Exception) {
            false
        }
    }
    
    private fun isWalletReady(): Boolean {
        return try {
            prefs.getBoolean("wallet_initialized", false)
        } catch (e: Exception) {
            false
        }
    }
    
    private fun updateSyncMetadata(duration: Long) {
        prefs.edit()
            .putLong("last_sync_time", System.currentTimeMillis())
            .putLong("last_sync_duration", duration)
            .putInt("last_sync_pushed", recordsPushed)
            .putInt("last_sync_pulled", recordsPulled)
            .putInt("last_sync_verified", transactionsVerified)
            .putInt("last_sync_errors", errors)
            .apply()
    }
    
    private fun createOutputData(): Data {
        return Data.Builder()
            .putInt("records_pushed", recordsPushed)
            .putInt("records_pulled", recordsPulled)
            .putInt("transactions_verified", transactionsVerified)
            .putInt("conflicts_resolved", conflictsResolved)
            .putInt("errors", errors)
            .build()
    }
    
    companion object {
        private const val WORK_NAME = "ajo_sync_work"
        private const val SYNC_INTERVAL_HOURS = 1L
        
        /**
         * Schedule periodic sync work
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
            
            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                SYNC_INTERVAL_HOURS,
                TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
            
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncRequest
                )
            
            Logger.i("SyncWorker: Scheduled periodic sync every $SYNC_INTERVAL_HOURS hours")
        }
        
        /**
         * Trigger immediate sync
         */
        fun syncNow(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()
            
            WorkManager.getInstance(context)
                .enqueue(syncRequest)
            
            Logger.i("SyncWorker: Triggered immediate sync")
        }
        
        /**
         * Cancel all scheduled sync work
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context)
                .cancelUniqueWork(WORK_NAME)
            
            Logger.i("SyncWorker: Cancelled all sync work")
        }
        
        /**
         * Get current sync status
         */
        fun getSynchronizationState(context: Context): SynchronizationState {
            val prefs = context.getSharedPreferences("ajo_prefs", Context.MODE_PRIVATE)
            
            return SynchronizationState(
                lastSyncTime = prefs.getLong("last_sync_time", 0),
                lastSyncDuration = prefs.getLong("last_sync_duration", 0),
                recordsPushed = prefs.getInt("last_sync_pushed", 0),
                recordsPulled = prefs.getInt("last_sync_pulled", 0),
                transactionsVerified = prefs.getInt("last_sync_verified", 0),
                errors = prefs.getInt("last_sync_errors", 0),
                isRunning = isSyncing(context)
            )
        }
        
        /**
         * Check if sync is currently running
         */
        fun isSyncing(context: Context): Boolean {
            val workInfos = WorkManager.getInstance(context)
                .getWorkInfosForUniqueWork(WORK_NAME)
                .get()
            
            return workInfos.any { workInfo ->
                workInfo.state == WorkInfo.State.RUNNING
            }
        }
        
        /**
         * Get detailed work info
         */
        fun getWorkInfo(context: Context): List<WorkInfo> {
            return WorkManager.getInstance(context)
                .getWorkInfosForUniqueWork(WORK_NAME)
                .get()
        }
    }
}

/**
 * Data class for sync status
 */
data class SynchronizationState(
    val lastSyncTime: Long,
    val lastSyncDuration: Long,
    val recordsPushed: Int,
    val recordsPulled: Int,
    val transactionsVerified: Int,
    val errors: Int,
    val isRunning: Boolean
)
