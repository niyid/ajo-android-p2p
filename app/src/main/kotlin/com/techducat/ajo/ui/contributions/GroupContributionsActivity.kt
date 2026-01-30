package com.techducat.ajo.ui.contributions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.techducat.ajo.R
import com.techducat.ajo.databinding.ActivityGroupContributionsBinding
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.ContributionEntity
import kotlinx.coroutines.launch
import android.content.Context
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.techducat.ajo.ui.auth.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupContributionsActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "com.techducat.ajo.ui.contributions.GroupContributionsActivity"
    }
    
    private lateinit var binding: ActivityGroupContributionsBinding
    private lateinit var database: AjoDatabase
    private lateinit var adapter: GroupContributionAdapter
    
    private var roscaId: String? = null
    private var allContributions = listOf<GroupContributionItem>()
    private val loginViewModel: LoginViewModel by viewModel()

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        loginViewModel.handleSignInResult(result.data)
    }
        
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupContributionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    
        setupLoginObservers()
        checkLoginAndInitialize()        
        
        database = AjoDatabase.getInstance(this)
        roscaId = intent.getStringExtra("rosca_id")
        
        if (roscaId == null) {
            finish()
            return
        }
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
            binding.progressBar?.visibility = View.VISIBLE
        } else {
            binding.progressBar?.visibility = View.GONE
        }
        
        state.error?.let { error ->
            showError(error)
            loginViewModel.clearError()
        }
        
        if (state.isSignedIn) {
            onLoginSuccess()
        }
    }    

    private fun checkLoginAndInitialize() {
        val userId = getUserId()
        
        if (userId.isNullOrEmpty()) {
            Log.d(TAG, "User not logged in, showing login prompt")
            showLoginPrompt()
        } else {
            Log.d(TAG, "User logged in, initializing")
            onLoginSuccess()
        }
    }

    private fun showLoginPrompt() {
        binding.contentLayout?.visibility = View.GONE
        binding.progressBar?.visibility = View.GONE
        binding.loginLayout.visibility = View.VISIBLE
        
        binding.btnGoogleSignIn.setOnClickListener {
            loginViewModel.startGoogleSignIn()
        }
    }
    
    private fun hideLoginPrompt() {
        binding.loginLayout.visibility = View.GONE
        binding.contentLayout?.visibility = View.VISIBLE
    }
    
    private fun getUserId(): String? {
        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("user_id", null)
    }
    
    private fun onLoginSuccess() {
        hideLoginPrompt()
        // Initialize activity content

        setupToolbar()
        setupRecyclerView()
        setupTabs()
        loadContributions()
        
    }        
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Rosca Contributions"
        }
    }
    
    private fun setupRecyclerView() {
        adapter = GroupContributionAdapter()
        binding.recyclerViewContributions.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewContributions.adapter = adapter
    }
    
    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.GroupContributions_all)))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.GroupContributions_cycle)))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.GroupContributions_member)))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.GroupContributions_stats)))
        
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showAllContributions()
                    1 -> showByCycle()
                    2 -> showByMember()
                    3 -> showStats()
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Optional: Clear any tab-specific state or animations
            }
            
            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Optional: Scroll to top or refresh content
                when (tab?.position) {
                    0, 1, 2 -> binding.recyclerViewContributions.smoothScrollToPosition(0)
                }
            }
        })
        
        binding.swipeRefresh.setOnRefreshListener {
            loadContributions()
        }
    }
    
    private fun loadContributions() {
        binding.swipeRefresh.isRefreshing = true
        
        lifecycleScope.launch {
            try {
                // FIX 1: Use correct method name
                val contributions = database.contributionDao()
                    .getContributionsByRoscaSync(roscaId!!)
                
                val members = database.memberDao().getMembersByGroupSync(roscaId!!)
                
                // PRIVACY: Create anonymous member identifiers
                // FIX 2: Explicit lambda parameter types
                val memberMap = members.mapIndexed { index: Int, member: com.techducat.ajo.data.local.entity.MemberEntity ->
                    member.id to "Member #${index + 1}"
                }.toMap()
                
                // FIX 3: Explicit lambda parameter type and property access
                allContributions = contributions.map { contribution: ContributionEntity ->
                    GroupContributionItem(
                        contribution = contribution,
                        memberIdentifier = memberMap[contribution.memberId] ?: "Unknown"
                    )
                }
                
                updateSummary()
                showAllContributions()
                
                binding.swipeRefresh.isRefreshing = false
                
            } catch (e: Exception) {
                binding.swipeRefresh.isRefreshing = false
                showError("Failed to load: ${e.message}")
            }
        }
    }
    
    private fun updateSummary() {
        val totalAmount = allContributions.sumOf { it.contribution.amount }
        val confirmedAmount = allContributions
            .filter { it.contribution.status == ContributionEntity.STATUS_CONFIRMED }
            .sumOf { it.contribution.amount }
        
        val confirmedCount = allContributions.count { 
            it.contribution.status == ContributionEntity.STATUS_CONFIRMED 
        }
        
        binding.textViewTotalCollected.text = formatXMR(confirmedAmount)
        binding.textViewTotalContributions.text = getString(
            R.string.GroupContributions_allcontributions_size_contributions, 
            allContributions.size
        )
        binding.textViewConfirmedCount.text = getString(
            R.string.GroupContributions_confirmedcount_confirmed, 
            confirmedCount
        )
        
        // Calculate completion rate
        lifecycleScope.launch {
            try {
                val rosca = database.roscaDao().getRoscaById(roscaId!!)
                val members = database.memberDao().getMembersByGroupSync(roscaId!!)
                val activeMembers = members.count { it.isActive }
                
                if (rosca != null && activeMembers > 0) {
                    val expectedTotal = activeMembers * rosca.currentRound
                    val completionRate = if (expectedTotal > 0) {
                        (confirmedCount.toFloat() / expectedTotal * 100).toInt()
                    } else 0
                    
                    binding.textViewCompletionRate.text = getString(
                        R.string.GroupContributions_completionrate_complete, 
                        completionRate
                    )
                    binding.progressBarCompletion.progress = completionRate
                }
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }
    
    private fun showAllContributions() {
        val sorted = allContributions.sortedByDescending { it.contribution.createdAt }
        adapter.submitList(sorted)
        binding.recyclerViewContributions.visibility = View.VISIBLE
        binding.layoutStats.visibility = View.GONE
    }
    
    private fun showByCycle() {
        val grouped = allContributions.groupBy { it.contribution.cycleNumber }
        val items = grouped.flatMap { (cycle, contributions) ->
            val header = GroupContributionItem(
                contribution = contributions.first().contribution.copy(
                    id = "header_cycle_$cycle"
                ),
                memberIdentifier = "Cycle $cycle (${contributions.size} contributions)",
                isHeader = true
            )
            listOf(header) + contributions.sortedBy { it.memberIdentifier }
        }
        
        adapter.submitList(items)
        binding.recyclerViewContributions.visibility = View.VISIBLE
        binding.layoutStats.visibility = View.GONE
    }
    
    private fun showByMember() {
        val grouped = allContributions.groupBy { it.memberIdentifier }
        val items = grouped.flatMap { (memberIdentifier, contributions) ->
            val totalAmount = contributions.sumOf { it.contribution.amount }
            val header = GroupContributionItem(
                contribution = contributions.first().contribution.copy(
                    id = "header_member_$memberIdentifier"
                ),
                memberIdentifier = "$memberIdentifier (${formatXMR(totalAmount)})",
                isHeader = true
            )
            listOf(header) + contributions.sortedByDescending { it.contribution.createdAt }
        }
        
        adapter.submitList(items)
        binding.recyclerViewContributions.visibility = View.VISIBLE
        binding.layoutStats.visibility = View.GONE
    }
    
    private fun showStats() {
        binding.recyclerViewContributions.visibility = View.GONE
        binding.layoutStats.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                // PRIVACY: Member participation stats with anonymous identifiers
                val members = database.memberDao().getMembersByGroupSync(roscaId!!)
                val activeMembers = members.filter { it.isActive }
                
                val memberStats = activeMembers.mapIndexed { index, member ->
                    val contributions = allContributions.filter { 
                        it.contribution.memberId == member.id &&
                        it.contribution.status == ContributionEntity.STATUS_CONFIRMED
                    }
                    val totalAmount = contributions.sumOf { it.contribution.amount }
                    
                    MemberStat(
                        memberIdentifier = "Member #${index + 1}", // PRIVACY: Anonymous
                        contributionCount = contributions.size,
                        totalAmount = totalAmount
                    )
                }.sortedByDescending { it.totalAmount }
                
                val statsText = buildString {
                    append("Member Participation (Anonymous):\n\n")
                    memberStats.forEach { stat ->
                        append("${stat.memberIdentifier}\n")
                        append("  ${stat.contributionCount} contributions\n")
                        append("  ${formatXMR(stat.totalAmount)}\n\n")
                    }
                }
                
                binding.textViewStats.text = statsText
                
            } catch (e: Exception) {
                binding.textViewStats.text = getString(R.string.GroupContributions_failed_load_stats)
            }
        }
    }
    
    private fun formatXMR(atomic: Long): String {
        val xmr = atomic / 1e12
        return String.format("%.6f XMR", xmr)
    }
    
    private fun showError(message: String) {
        com.google.android.material.snackbar.Snackbar.make(
            binding.root,
            message,
            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

// Data classes - PRIVACY: Using memberIdentifier instead of name
data class GroupContributionItem(
    val contribution: ContributionEntity,
    val memberIdentifier: String, // Anonymous identifier like "Member #1"
    val isHeader: Boolean = false
)

data class MemberStat(
    val memberIdentifier: String, // Anonymous identifier
    val contributionCount: Int,
    val totalAmount: Long
)

// Adapter
class GroupContributionAdapter : 
    androidx.recyclerview.widget.ListAdapter<GroupContributionItem, RecyclerView.ViewHolder>(
    GroupContributionDiffCallback()
) {
    
    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }
    
    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isHeader) TYPE_HEADER else TYPE_ITEM
    }
    
    override fun onCreateViewHolder(
        parent: ViewGroup, 
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val binding = com.techducat.ajo.databinding.ItemContributionHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            HeaderViewHolder(binding)
        } else {
            val binding = com.techducat.ajo.databinding.ItemGroupContributionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ItemViewHolder(binding)
        }
    }
    
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, 
        position: Int
    ) {
        val item = getItem(position)
        when (holder) {
            is HeaderViewHolder -> holder.bind(item)
            is ItemViewHolder -> holder.bind(item)
        }
    }
    
    class HeaderViewHolder(
        private val binding: com.techducat.ajo.databinding.ItemContributionHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupContributionItem) {
            binding.textViewHeader.text = item.memberIdentifier
        }
    }
    
    class ItemViewHolder(
        private val binding: com.techducat.ajo.databinding.ItemGroupContributionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupContributionItem) {
            // PRIVACY: Show anonymous member identifier
            binding.textViewMemberName.text = item.memberIdentifier
            binding.textViewAmount.text = formatXMR(item.contribution.amount)
            binding.textViewDate.text = formatDate(item.contribution.createdAt)
            binding.textViewStatus.text = item.contribution.status.uppercase()
            
            val statusColor = when (item.contribution.status) {
                ContributionEntity.STATUS_CONFIRMED -> android.R.color.holo_green_dark
                ContributionEntity.STATUS_PENDING -> android.R.color.holo_orange_dark
                else -> android.R.color.holo_red_dark
            }
            binding.viewStatusIndicator.setBackgroundColor(
                binding.root.context.getColor(statusColor)
            )
        }
        
        private fun formatXMR(atomic: Long): String {
            return String.format("%.6f XMR", atomic / 1e12)
        }
        
        private fun formatDate(timestamp: Long): String {
            val sdf = java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault())
            return sdf.format(java.util.Date(timestamp))
        }
    }
}

class GroupContributionDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<GroupContributionItem>() {
    override fun areItemsTheSame(
        oldItem: GroupContributionItem,
        newItem: GroupContributionItem
    ): Boolean = oldItem.contribution.id == newItem.contribution.id
    
    override fun areContentsTheSame(
        oldItem: GroupContributionItem,
        newItem: GroupContributionItem
    ): Boolean = oldItem == newItem
}
