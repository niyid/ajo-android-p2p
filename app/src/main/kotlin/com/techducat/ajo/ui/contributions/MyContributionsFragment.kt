package com.techducat.ajo.ui.contributions

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.techducat.ajo.R
import com.techducat.ajo.databinding.FragmentMyContributionsBinding
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.ContributionEntity
import com.techducat.ajo.wallet.WalletSuite
import kotlinx.coroutines.launch
import android.content.Context
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.techducat.ajo.ui.auth.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyContributionsFragment : Fragment() {
    
    companion object {
        private const val TAG = "com.techducat.ajo.ui.contributions.MyContributionsFragment"
    }
    
    private var _binding: FragmentMyContributionsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var database: AjoDatabase
    private lateinit var walletSuite: WalletSuite
    private lateinit var adapter: ContributionAdapter
    
    private var allContributions = listOf<ContributionWithDetails>()
    private var currentFilter = "all"
    private val loginViewModel: LoginViewModel by viewModel()

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        loginViewModel.handleSignInResult(result.data)
    }    
    
    private fun setupLoginObservers() {
        lifecycleScope.launch {
            loginViewModel.signInIntent.collect { intent ->
                intent?.let { signInLauncher.launch(it) }
            }
        }
        
        lifecycleScope.launch {
            loginViewModel.uiState.collect { state ->
                updateLoginUI(state)
            }
        }
    }
    
    private fun updateLoginUI(state: com.techducat.ajo.ui.auth.LoginUiState) {
        if (state.isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
        
        state.error?.let { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            loginViewModel.clearError()
        }
        
        if (state.isSignedIn) {
            onLoginSuccess()
        }
    }    
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyContributionsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        database = AjoDatabase.getInstance(requireContext())
        walletSuite = WalletSuite.getInstance(requireContext())
        
        setupRecyclerView()
        setupFilters()

        setupLoginObservers()
        checkLoginAndLoadData()        
    }
    
    private fun checkLoginAndLoadData() {
        val userId = getUserId()
        
        if (userId.isNullOrEmpty()) {
            Log.d(TAG, "User not logged in, showing login prompt")
            showLoginPrompt()
        } else {
            Log.d(TAG, "User logged in, loading data")
            onLoginSuccess()
        }
    }
    
    private fun showLoginPrompt() {
        binding.contentLayout.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.loginLayout.visibility = View.VISIBLE
        
        binding.btnGoogleSignIn.setOnClickListener {
            loginViewModel.startGoogleSignIn()
        }
    }
    
    private fun hideLoginPrompt() {
        binding.loginLayout.visibility = View.GONE
        binding.contentLayout.visibility = View.VISIBLE
    }

    private fun getUserId(): String? {
        val sharedPrefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("user_id", null)
    }
    
    private fun onLoginSuccess() {
        hideLoginPrompt()
        // Load fragment data
        loadContributions()        
    }
        
    private fun setupRecyclerView() {
        adapter = ContributionAdapter { contribution ->
            showContributionDetails(contribution)
        }
        
        binding.recyclerViewContributions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewContributions.adapter = adapter
    }
    
    private fun setupFilters() {
        binding.chipAll.setOnClickListener { filterContributions("all") }
        binding.chipPending.setOnClickListener { filterContributions("pending") }
        binding.chipConfirmed.setOnClickListener { filterContributions("confirmed") }
        binding.chipFailed.setOnClickListener { filterContributions("failed") }
        
        binding.swipeRefresh.setOnRefreshListener {
            loadContributions()
        }
    }
    
    private fun loadContributions() {
        binding.swipeRefresh.isRefreshing = true
        
        lifecycleScope.launch {
            try {
                val userId = walletSuite.getUserId()
                
                // Get all members where user is involved
                val allMembers = database.memberDao().getAllMembers()
                val userMembers = allMembers.filter { it.userId == userId }
                
                val contributionsWithDetails = mutableListOf<ContributionWithDetails>()
                
                for (member in userMembers) {
                    // FIX 1: Use correct method name
                    val contributions = database.contributionDao()
                        .getContributionsByRoscaSync(member.roscaId)
                        // FIX 2: Explicit lambda parameter type
                        .filter { contribution: ContributionEntity -> 
                            contribution.memberId == member.id 
                        }
                    
                    val rosca = database.roscaDao().getRoscaById(member.roscaId)
                    
                    // FIX 3: Explicit lambda parameter type
                    contributions.forEach { contribution: ContributionEntity ->
                        contributionsWithDetails.add(
                            ContributionWithDetails(
                                contribution = contribution,
                                roscaName = rosca?.name ?: "Unknown ROSCA",
                                memberIdentifier = "You" // PRIVACY: Only show "You"
                            )
                        )
                    }
                }
                
                // Sort by createdAt descending (most recent first)
                allContributions = contributionsWithDetails.sortedByDescending { 
                    it.contribution.createdAt 
                }
                
                // Update summary
                updateSummary()
                
                // Apply filter
                filterContributions(currentFilter)
                
                binding.swipeRefresh.isRefreshing = false
                
            } catch (e: Exception) {
                binding.swipeRefresh.isRefreshing = false
                showError("Failed to load contributions: ${e.message}")
            }
        }
    }
    
    private fun filterContributions(filter: String) {
        currentFilter = filter
        
        // Update chip selection
        binding.chipAll.isChecked = (filter == "all")
        binding.chipPending.isChecked = (filter == "pending")
        binding.chipConfirmed.isChecked = (filter == "confirmed")
        binding.chipFailed.isChecked = (filter == "failed")
        
        val filtered = when (filter) {
            "pending" -> allContributions.filter { 
                it.contribution.status == ContributionEntity.STATUS_PENDING 
            }
            "confirmed" -> allContributions.filter { 
                it.contribution.status == ContributionEntity.STATUS_CONFIRMED 
            }
            "failed" -> allContributions.filter { 
                it.contribution.status == ContributionEntity.STATUS_FAILED 
            }
            else -> allContributions
        }
        
        adapter.submitList(filtered)
        
        if (filtered.isEmpty()) {
            binding.textViewEmpty.visibility = View.VISIBLE
            binding.recyclerViewContributions.visibility = View.GONE
        } else {
            binding.textViewEmpty.visibility = View.GONE
            binding.recyclerViewContributions.visibility = View.VISIBLE
        }
    }
    
    private fun updateSummary() {
        val totalAmount = allContributions.sumOf { it.contribution.amount }
        val confirmedAmount = allContributions
            .filter { it.contribution.status == ContributionEntity.STATUS_CONFIRMED }
            .sumOf { it.contribution.amount }
        
        val totalXMR = totalAmount / 1e12
        val confirmedXMR = confirmedAmount / 1e12
        
        binding.textViewTotalContributed.text = String.format(getString(R.string.MyContributions_xmr), totalXMR)
        binding.textViewConfirmedAmount.text = String.format(getString(R.string.MyContributions_xmr), confirmedXMR)
        // Fix 1: Add the lambda to count contributions
        binding.textViewContributionCount.text = getString(R.string.GroupContributions_allcontributions_size_contributions, allContributions.size)
        
        // Fix 2: Count unique ROSCAs
        val byRosca = allContributions.groupBy { it.roscaName }
        binding.textViewRoscaCount.text = getString(R.string.MyContributions_byrosca_size_roscas, byRosca.size)
    }
    
    private fun showContributionDetails(contribution: ContributionWithDetails) {
        val details = buildString {
            append("ROSCA: ${contribution.roscaName}\n")
            append("Cycle: ${contribution.contribution.cycleNumber}\n")
            append("Amount: ${formatXMR(contribution.contribution.amount.toString())}\n")
            append("Status: ${contribution.contribution.status}\n")
            contribution.contribution.txHash?.let { 
                append("TX Hash: ${it.take(16)}...\n") 
            }
            append("Date: ${formatDate(contribution.contribution.createdAt)}")
        }
        
        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.MyContributions_contribution_details))
            .setMessage(details)
            .setPositiveButton("OK", null)
        
        // Only show getString(R.string.MyContributions_view) button if txHash exists
        contribution.contribution.txHash?.let { txHash ->
            dialog.setNeutralButton("View TX") { _, _ ->
                openBlockchainExplorer(txHash)
            }
        }
        
        dialog.show()
    }

    private fun openBlockchainExplorer(txHash: String) {
        val explorerUrl = "https://xmrchain.net/search?value=$txHash"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(explorerUrl))
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), getString(R.string.MyContributions_browser_found), Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun formatXMR(amount: String): String {
        return try {
            val atomic = amount.toLongOrNull() ?: 0L
            val xmr = atomic / 1e12
            String.format("%.6f XMR", xmr)
        } catch (e: Exception) {
            "0.000000 XMR"
        }
    }
    
    private fun formatDate(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
    
    private fun showError(message: String) {
        com.google.android.material.snackbar.Snackbar.make(
            binding.root, 
            message, 
            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Data class - PRIVACY: Using memberIdentifier instead of name
data class ContributionWithDetails(
    val contribution: ContributionEntity,
    val roscaName: String,
    val memberIdentifier: String // "You" for own contributions
)

// Adapter
class ContributionAdapter(
    private val onItemClick: (ContributionWithDetails) -> Unit
) : androidx.recyclerview.widget.ListAdapter<ContributionWithDetails, ContributionAdapter.ViewHolder>(
    ContributionDiffCallback()
) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = com.techducat.ajo.databinding.ItemContributionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ViewHolder(
        private val binding: com.techducat.ajo.databinding.ItemContributionBinding
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: ContributionWithDetails) {
            binding.textViewRoscaName.text = item.roscaName
            binding.textViewAmount.text = formatXMR(item.contribution.amount.toString())
            binding.textViewDate.text = formatDate(item.contribution.createdAt)
            binding.textViewCycle.text = binding.root.context.getString(R.string.MyContributions_cycle_item_contribution_cyclenumber, binding.textViewCycle.text)
            
            // Status indicator
            val statusColor = when (item.contribution.status) {
                ContributionEntity.STATUS_CONFIRMED -> android.R.color.holo_green_dark
                ContributionEntity.STATUS_PENDING -> android.R.color.holo_orange_dark
                ContributionEntity.STATUS_FAILED -> android.R.color.holo_red_dark
                else -> android.R.color.darker_gray
            }
            binding.viewStatusIndicator.setBackgroundColor(
                binding.root.context.getColor(statusColor)
            )
            
            binding.textViewStatus.text = item.contribution.status.uppercase()
            
            binding.root.setOnClickListener { onItemClick(item) }
        }
        
        private fun formatXMR(amount: String): String {
            return try {
                val atomic = amount.toLongOrNull() ?: 0L
                val xmr = atomic / 1e12
                String.format("%.6f XMR", xmr)
            } catch (e: Exception) {
                "0.000000 XMR"
            }
        }
        
        private fun formatDate(timestamp: Long): String {
            val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
            return sdf.format(java.util.Date(timestamp))
        }
    }
}

class ContributionDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<ContributionWithDetails>() {
    override fun areItemsTheSame(
        oldItem: ContributionWithDetails,
        newItem: ContributionWithDetails
    ): Boolean = oldItem.contribution.id == newItem.contribution.id
    
    override fun areContentsTheSame(
        oldItem: ContributionWithDetails,
        newItem: ContributionWithDetails
    ): Boolean = oldItem == newItem
}
