package com.techducat.ajo.ui.dashboard

import com.techducat.ajo.databinding.ItemRoscaBinding
import com.techducat.ajo.model.Rosca

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techducat.ajo.R

class RoscaListAdapter(
    private val onItemClick: (Rosca) -> Unit
) : ListAdapter<Rosca, RoscaListAdapter.RoscaViewHolder>(RoscaDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoscaViewHolder {
        val binding = ItemRoscaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoscaViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: RoscaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class RoscaViewHolder(
        private val binding: ItemRoscaBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(rosca: Rosca) {
            // Set status indicator color
            val statusColor = when (rosca.status.name.uppercase()) {
                "ACTIVE" -> ContextCompat.getColor(binding.root.context, R.color.status_active)
                "FORMING" -> ContextCompat.getColor(binding.root.context, R.color.status_forming)
                "COMPLETED" -> ContextCompat.getColor(binding.root.context, R.color.status_completed)
                "PAUSED" -> ContextCompat.getColor(binding.root.context, R.color.status_paused)
                else -> ContextCompat.getColor(binding.root.context, R.color.status_forming)
            }
            binding.statusIndicator.setBackgroundColor(statusColor)
            
            // Set ROSCA icon/emoji
            binding.tvRoscaEmoji.text = "💰" // You can make this dynamic based on ROSCA type
            
            // Set ROSCA name
            binding.tvRoscaName.text = rosca.name
            
            // Set ROSCA details (members and frequency)
            binding.tvRoscaDetails.text = binding.root.context.getString(R.string.RoscaList_rosca_currentmembers_rosca_totalmembers, 
                rosca.currentMembers, 
                rosca.totalMembers, 
                getFrequencyText(rosca))
            
            // Set contribution amount
            binding.tvAmount.text = binding.root.context.getString(R.string.RoscaList_rosca_contributionamount_xmr, rosca.status.name)
            
            // Set status chip
            binding.chipStatus.text = rosca.status.name
            binding.chipStatus.chipBackgroundColor = 
                android.content.res.ColorStateList.valueOf(statusColor)
            
            // Set progress info
            binding.tvProgress.text = binding.root.context.getString(
                R.string.RoscaList_rosca_currentround_rosca_totalmembers, 
                rosca.currentRound, 
                rosca.totalMembers
            )
            
            // Set next payout info
            binding.tvNextPayout.text = getNextPayoutText(rosca)
            
            // Show "Your Turn" indicator if applicable
            // You'll need to add logic to determine if it's the user's turn
            binding.yourTurnLayout.visibility = View.GONE
            
            // Calculate and set progress bar
            val progress = if (rosca.totalMembers > 0) {
                (rosca.currentRound.toFloat() / rosca.totalMembers * 100).toInt()
            } else 0
            binding.progressBar.progress = progress
            // Show progress bar only if you want to display it
            binding.progressBar.visibility = View.VISIBLE
            
            // Set click listener
            binding.root.setOnClickListener { onItemClick(rosca) }
        }
        
        private fun getFrequencyText(rosca: Rosca): String {
            // Add logic based on your Rosca model
            // For now, return a placeholder
            return "Weekly"
        }
        
        private fun getNextPayoutText(rosca: Rosca): String {
            // Add logic to calculate next payout date
            // For now, return a placeholder
            return "In ${rosca.totalMembers - rosca.currentRound} rounds"
        }
    }
    
    private class RoscaDiffCallback : DiffUtil.ItemCallback<Rosca>() {
        override fun areItemsTheSame(oldItem: Rosca, newItem: Rosca): Boolean {
            return oldItem.roscaId == newItem.roscaId
        }
        
        override fun areContentsTheSame(oldItem: Rosca, newItem: Rosca): Boolean {
            return oldItem == newItem
        }
    }
}
