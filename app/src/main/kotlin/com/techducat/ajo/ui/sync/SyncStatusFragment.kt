package com.techducat.ajo.ui.sync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.worker.P2PSyncWorker
import kotlinx.coroutines.launch

/**
 * COMPLETE Sync Status UI
 */
class SyncStatusFragment : Fragment() {
    
    private lateinit var db: AjoDatabase
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createSyncStatusView()
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        db = AjoDatabase.getInstance(requireContext())
        
        loadSyncStatus()
    }
    
    private fun createSyncStatusView(): View {
        // Create programmatic UI (no XML needed)
        return android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            
            // Title
            addView(android.widget.TextView(context).apply {
                text = "P2P Sync Status"
                textSize = 24f
                setPadding(0, 0, 0, 24)
            })
            
            // Status indicator
            addView(android.widget.TextView(context).apply {
                id = View.generateViewId()
                tag = "status"
                text = "Checking..."
                textSize = 16f
            })
            
            // Last sync time
            addView(android.widget.TextView(context).apply {
                id = View.generateViewId()
                tag = "lastSync"
                text = "Last sync: Never"
                textSize = 14f
                setPadding(0, 16, 0, 0)
            })
            
            // Pending items
            addView(android.widget.TextView(context).apply {
                id = View.generateViewId()
                tag = "pending"
                text = "Pending: 0"
                textSize = 14f
                setPadding(0, 8, 0, 0)
            })
            
            // Sync now button
            addView(android.widget.Button(context).apply {
                text = "Sync Now"
                setPadding(0, 32, 0, 0)
                setOnClickListener {
                    P2PSyncWorker.syncNow(requireContext())
                    loadSyncStatus()
                }
            })
        }
    }
    
    private fun loadSyncStatus() {
        lifecycleScope.launch {
            val localNode = db.localNodeDao().getLocalNode()
            val pending = db.syncQueueDao().getPendingSyncs()
            val recentLogs = db.syncLogDao().getRecentLogs("")
            
            view?.findViewWithTag<android.widget.TextView>("status")?.text =
                if (localNode != null) "✓ Online (${localNode.nodeId.take(12)}...)" 
                else "⚠ Not initialized"
            
            view?.findViewWithTag<android.widget.TextView>("lastSync")?.text =
                "Last sync: ${formatTime(localNode?.lastSyncAt)}"
            
            view?.findViewWithTag<android.widget.TextView>("pending")?.text =
                "Pending: ${pending.size} items"
        }
    }
    
    private fun formatTime(timestamp: Long?): String {
        if (timestamp == null) return "Never"
        val diff = System.currentTimeMillis() - timestamp
        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000} min ago"
            diff < 86400000 -> "${diff / 3600000} hours ago"
            else -> "${diff / 86400000} days ago"
        }
    }
}
