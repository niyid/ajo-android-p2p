package com.techducat.ajo.ui.wallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techducat.ajo.R
import com.techducat.ajo.util.CurrencyFormatter

data class RoscaWalletItem(
    val roscaId: String,
    val roscaName: String,
    val multisigAddress: String,
    val status: String,
    val currentRound: Int,
    val totalRounds: Int,
    val balance: Long,
    val memberCount: String
)

class RoscaWalletsAdapter(
    private val onRoscaClick: (String) -> Unit
) : ListAdapter<RoscaWalletItem, RoscaWalletsAdapter.RoscaViewHolder>(RoscaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoscaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rosca_wallet, parent, false)
        return RoscaViewHolder(view, onRoscaClick)
    }

    override fun onBindViewHolder(holder: RoscaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RoscaViewHolder(
        itemView: View,
        private val onRoscaClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val nameText: TextView = itemView.findViewById(R.id.textViewRoscaItemName)
        private val statusText: TextView = itemView.findViewById(R.id.textViewRoscaItemStatus)
        private val roundText: TextView = itemView.findViewById(R.id.textViewRoscaItemRound)
        private val balanceText: TextView = itemView.findViewById(R.id.textViewRoscaItemBalance)
        
        fun bind(item: RoscaWalletItem) {
            nameText.text = item.roscaName
            statusText.text = "Status: ${item.status}"
            roundText.text = "Round ${item.currentRound}/${item.totalRounds} • ${item.memberCount} members"
            balanceText.text = CurrencyFormatter.formatXMR(item.balance, decimals = 2)
            
            itemView.setOnClickListener {
                onRoscaClick(item.roscaId)
            }
        }
    }
    
    class RoscaDiffCallback : DiffUtil.ItemCallback<RoscaWalletItem>() {
        override fun areItemsTheSame(oldItem: RoscaWalletItem, newItem: RoscaWalletItem): Boolean {
            return oldItem.roscaId == newItem.roscaId
        }

        override fun areContentsTheSame(oldItem: RoscaWalletItem, newItem: RoscaWalletItem): Boolean {
            return oldItem == newItem
        }
    }
}
