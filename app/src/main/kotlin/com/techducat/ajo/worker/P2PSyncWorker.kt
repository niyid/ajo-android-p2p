package com.techducat.ajo.worker

import android.content.Context
import androidx.work.*
import com.techducat.ajo.sync.SyncEngine
import com.techducat.ajo.sync.MessageHandler
import com.techducat.ajo.core.network.NetworkTransport
import com.techducat.ajo.core.network.MockTransport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * P2P Sync Worker - handles peer-to-peer synchronization
 * This runs alongside your existing DLT SyncWorker
 */
class P2PSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val transport: NetworkTransport by lazy {
        // TODO: Replace with actual I2P/Tor transport
        // For Phase 1, use mock transport
        MockTransport()
    }
    
    private val syncEngine: SyncEngine by lazy {
        SyncEngine(applicationContext, transport)
    }
    
    private val messageHandler: MessageHandler by lazy {
        MessageHandler(applicationContext)
    }
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Initialize network
            if (!::_transportInitialized.isInitialized) {
                transport.initialize()
                _transportInitialized = true
            }
            
            // Set up message listener (one-time)
            if (!::_listenerSet.isInitialized) {
                transport.setReceiveListener { messageBytes ->
                    kotlinx.coroutines.runBlocking {
                        messageHandler.handleMessage(messageBytes)
                    }
                }
                _listenerSet = true
            }
            
            // Process outbound sync queue
            syncEngine.processSyncQueue()
            
            // For mock transport, manually deliver messages
            if (transport is MockTransport) {
                (transport as MockTransport).deliverMessages()
            }
            
            Result.success()
            
        } catch (e: Exception) {
            e.printStackTrace()
            
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
    
    companion object {
        private const val WORK_NAME = "ajo_p2p_sync"
        private var _transportInitialized = false
        private var _listenerSet = false
        
        /**
         * Schedule periodic P2P sync (every 15 minutes)
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val syncRequest = PeriodicWorkRequestBuilder<P2PSyncWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }
        
        /**
         * Trigger immediate P2P sync
         */
        fun syncNow(context: Context) {
            val syncRequest = OneTimeWorkRequestBuilder<P2PSyncWorker>().build()
            WorkManager.getInstance(context).enqueue(syncRequest)
        }
        
        /**
         * Cancel P2P sync
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
