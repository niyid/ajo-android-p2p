package com.techducat.ajo.ui.sync

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.worker.P2PSyncWorker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Complete Sync Status UI - shows P2P sync health
 */
class SyncStatusActivity : AppCompatActivity() {
    
    private lateinit var db: AjoDatabase
    private lateinit var statusText: TextView
    private lateinit var nodeIdText: TextView
    private lateinit var lastSyncText: TextView
    private lateinit var pendingText: TextView
    private lateinit var peersText: TextView
    private lateinit var syncButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        db = AjoDatabase.getInstance(this)
        
        setContentView(createLayout())
        loadStatus()
    }
    
    private fun createLayout(): ScrollView {
        return ScrollView(this).apply {
            addView(LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(48, 48, 48, 48)
                
                // Title
                addView(TextView(context).apply {
                    text = "P2P Sync Status"
                    textSize = 28f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setPadding(0, 0, 0, 32)
                })
                
                // Status card
                addView(LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(24, 24, 24, 24)
                    setBackgroundColor(0xFFF5F5F5.toInt())
                    
                    addView(TextView(context).apply {
                        statusText = this
                        text = "⏳ Loading..."
                        textSize = 18f
                        setPadding(0, 0, 0, 12)
                    })
                    
                    addView(TextView(context).apply {
                        nodeIdText = this
                        text = "Node: Unknown"
                        textSize = 14f
                        setPadding(0, 0, 0, 8)
                    })
                    
                    addView(TextView(context).apply {
                        lastSyncText = this
                        text = "Last sync: Never"
                        textSize = 14f
                        setPadding(0, 0, 0, 8)
                    })
                    
                    addView(TextView(context).apply {
                        pendingText = this
                        text = "Pending: 0"
                        textSize = 14f
                        setPadding(0, 0, 0, 8)
                    })
                    
                    addView(TextView(context).apply {
                        peersText = this
                        text = "Peers: 0"
                        textSize = 14f
                    })
                })
                
                // Sync button
                addView(Button(context).apply {
                    syncButton = this
                    text = "Sync Now"
                    textSize = 16f
                    setPadding(32, 24, 32, 24)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 32
                    }
                    setOnClickListener {
                        triggerSync()
                    }
                })
            })
        }
    }
    
    private fun loadStatus() {
        lifecycleScope.launch {
            try {
                val localNode = db.localNodeDao().getLocalNode()
                val pending = db.syncQueueDao().getPendingSyncs()
                val peers = db.peerDao().getPeersByRosca("")
                
                if (localNode != null) {
                    statusText.text = "✓ Online"
                    statusText.setTextColor(0xFF4CAF50.toInt())
                    nodeIdText.text = "Node: ${localNode.nodeId.take(16)}..."
                    
                    if (localNode.lastSyncAt != null) {
                        val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                        lastSyncText.text = "Last sync: ${formatter.format(Date(localNode.lastSyncAt))}"
                    } else {
                        lastSyncText.text = "Last sync: Never"
                    }
                } else {
                    statusText.text = "⚠ Not Initialized"
                    statusText.setTextColor(0xFFFFA000.toInt())
                }
                
                pendingText.text = "Pending: ${pending.size} items"
                peersText.text = "Peers: ${peers.size}"
                
            } catch (e: Exception) {
                statusText.text = "❌ Error: ${e.message}"
                statusText.setTextColor(0xFFF44336.toInt())
            }
        }
    }
    
    private fun triggerSync() {
        syncButton.isEnabled = false
        syncButton.text = "Syncing..."
        
        P2PSyncWorker.syncNow(this)
        
        // Re-enable after delay
        syncButton.postDelayed({
            syncButton.isEnabled = true
            syncButton.text = "Sync Now"
            loadStatus()
        }, 3000)
    }
}
