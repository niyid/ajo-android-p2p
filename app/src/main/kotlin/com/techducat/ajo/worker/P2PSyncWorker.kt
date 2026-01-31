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

class P2PSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val transport: NetworkTransport by lazy {
        MockTransport()
    }
    
    private val syncEngine: SyncEngine by lazy {
        SyncEngine(applicationContext, transport)
    }
    
    private val messageHandler: MessageHandler by lazy {
        MessageHandler(applicationContext)
    }
    
    // FIXED: Simple flags instead of lateinit checks
    private var transportInitialized = false
    private var listenerSet = false
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Initialize network
            if (!transportInitialized) {
                transport.initialize()
                transportInitialized = true
            }
            
            // Set up message listener
            if (!listenerSet) {
                transport.setReceiveListener { messageBytes ->
                    kotlinx.coroutines.runBlocking {
                        messageHandler.handleMessage(messageBytes)
                    }
                }
                listenerSet = true
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
        
        fun syncNow(context: Context) {
            val syncRequest = OneTimeWorkRequestBuilder<P2PSyncWorker>().build()
            WorkManager.getInstance(context).enqueue(syncRequest)
        }
        
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
