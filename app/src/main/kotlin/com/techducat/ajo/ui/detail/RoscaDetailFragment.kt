package com.techducat.ajo.ui.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.MemberEntity
import com.techducat.ajo.data.local.entity.ContributionEntity
import com.techducat.ajo.data.local.entity.RoscaEntity
import com.techducat.ajo.data.local.entity.InviteEntity
import com.techducat.ajo.databinding.FragmentRoscaDetailBinding
import com.techducat.ajo.service.RoscaManager
import com.techducat.ajo.ui.auth.LoginViewModel
import com.techducat.ajo.ui.contributions.GroupContributionsActivity
import com.techducat.ajo.ui.sync.QRCodeGenerator
import com.techducat.ajo.util.CurrencyFormatter
import com.techducat.ajo.util.WalletSelectionManager
import com.techducat.ajo.wallet.WalletSuite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import java.io.File
import android.content.Context
import com.techducat.ajo.R

class RoscaDetailFragment : Fragment() {
    
    companion object {
        private const val TAG = "com.techducat.ajo.ui.detail.RoscaDetailFragment"
    }
    
    private var _binding: FragmentRoscaDetailBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var database: AjoDatabase
    private lateinit var memberAdapter: RoscaMemberAdapter
    
    private val roscaManager: RoscaManager by inject()
    private val walletSuite: WalletSuite by inject()
    
    private var roscaId: String? = null
    private var rosca: RoscaEntity? = null
    private val loginViewModel: LoginViewModel by viewModel()

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        loginViewModel.handleSignInResult(result.data)
    }    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roscaId = arguments?.getString("rosca_id")
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
        
        lifecycleScope.launch {
            loginViewModel.referralResult.collect { result ->
                handleReferralResult(result)
            }
        }
    }
    
    private fun checkAndActivateRosca(rosca: RoscaEntity) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val members = withContext(Dispatchers.IO) {
                    database.memberDao().getMembersByGroupSync(roscaId!!)
                }
                
                val activeMemberCount = members.count { it.isActive }
                
                Log.d(TAG, "Member count check: $activeMemberCount/${rosca.totalMembers}")
                
                // ✅ FIX: Only activate if we have enough members AND status is SETUP
                if (activeMemberCount >= rosca.totalMembers && 
                    rosca.status == RoscaEntity.STATUS_SETUP) {
                    
                    Log.d(TAG, "Member count reached! Calling RoscaManager.finalizeSetup()...")
                    
                    // ✅ Get current user ID
                    val userId = getUserId()
                    if (userId.isNullOrEmpty()) {
                        if (!isAdded) return@launch
                        showError(getString(R.string.RoscaDetail_not_logged_in))
                        return@launch
                    }
                    
                    // ✅✅✅ CRITICAL FIX: Collect OTHER members' multisig info (exclude current user)
                    val allMemberMultisigInfos = members
                        .filter { it.userId != userId }  // ← Filter out current user
                        .mapNotNull { member ->
                            member.multisigInfo?.exchangeState
                        }
                    
                    Log.d(TAG, "Collected ${allMemberMultisigInfos.size} OTHER member multisig infos (excluding self)")
                    
                    // ✅✅✅ FIXED: Should be totalMembers - 1 (all members except current user)
                    val expectedCount = rosca.totalMembers - 1
                    if (allMemberMultisigInfos.size < expectedCount) {
                        if (!isAdded) return@launch
                        Log.w(TAG, "Not all members have multisig info yet: ${allMemberMultisigInfos.size}/$expectedCount other members")
                        showError(getString(R.string.RoscaDetail_waiting_multisig_info))
                        return@launch
                    }
                    
                    Log.d(TAG, "✓ All $expectedCount other members have multisig info, proceeding with finalization")
                    
                    // ✅ Call RoscaManager with userId parameter
                    val result = withContext(Dispatchers.IO) {
                        roscaManager.finalizeSetup(
                            roscaId = roscaId!!,
                            allMemberMultisigInfos = allMemberMultisigInfos,
                            userId = userId
                        )
                    }
                    
                    // ✅ CHECK IF FRAGMENT IS STILL ATTACHED BEFORE ACCESSING RESOURCES
                    if (!isAdded) return@launch
                    
                    if (result.isSuccess) {
                        Log.i(TAG, "✅ ROSCA setup finalized successfully via RoscaManager")
                        
                        // Reload from database to get updated status and multisig address
                        loadRoscaDetails()
                        
                        showSuccess(getString(R.string.RoscaDetail_rosca_now_active))
                    } else {
                        val error = result.exceptionOrNull()?.message ?: "Unknown error"
                        Log.e(TAG, "❌ Failed to finalize ROSCA setup: $error")
                        
                        showError(getString(R.string.RoscaDetail_failed_activate_rosca, error))
                    }
                }
                
            } catch (e: Exception) {
                // ✅ CHECK IF FRAGMENT IS STILL ATTACHED BEFORE SHOWING ERROR
                if (!isAdded) return@launch
                
                Log.e(TAG, "Error checking ROSCA activation", e)
                showError(getString(R.string.RoscaDetail_error_checking_activation, e.message ?: "Unknown error"))
            }
        }
    }
    
    private fun handleReferralResult(result: com.techducat.ajo.service.ReferralResult) {
        when (result) {
            is com.techducat.ajo.service.ReferralResult.Success -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.Login_welcome))
                    .setMessage(getString(R.string.Login_you_successfully_joined_result, result.roscaName))
                    .setPositiveButton(getString(R.string.Login_view_group)) { _, _ ->
                        if (roscaId != result.roscaId) {
                            try {
                                val bundle = Bundle().apply {
                                    putString("rosca_id", result.roscaId)
                                }
                                findNavController().navigate(
                                    R.id.action_roscaDetail_to_self,
                                    bundle
                                )
                            } catch (e: Exception) {
                                Log.e(TAG, "Self-navigation failed, using fallback", e)
                                val bundle = Bundle().apply {
                                    putString("rosca_id", result.roscaId)
                                }
                                findNavController().navigate(R.id.roscaDetailFragment, bundle)
                            }
                        } else {
                            loadRoscaDetails()
                            rosca?.let { checkAndActivateRosca(it) }
                        }
                    }
                    .setCancelable(false)
                    .show()
            }
            
            is com.techducat.ajo.service.ReferralResult.AlreadyMember -> {
                showSuccess(getString(R.string.RoscaDetail_welcome_back_member, result.roscaName))
            }
            
            is com.techducat.ajo.service.ReferralResult.Expired -> {
                showError(getString(R.string.RoscaDetail_invite_expired))
            }
            
            is com.techducat.ajo.service.ReferralResult.RoscaFull -> {
                showError(getString(R.string.RoscaDetail_group_full))
            }
            
            is com.techducat.ajo.service.ReferralResult.InvalidCode -> {
                showError(getString(R.string.RoscaDetail_invalid_invite_code))
            }
            
            is com.techducat.ajo.service.ReferralResult.EmailMismatch -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.Login_email_mismatch))
                    .setMessage(getString(R.string.Login_this_invite_was_sent, result.expectedEmail))
                    .setPositiveButton(getString(R.string.ok), null)
                    .show()
            }
            
            is com.techducat.ajo.service.ReferralResult.Error -> {
                showError(getString(R.string.RoscaDetail_failed_process_invitation, result.message))
            }
            
            is com.techducat.ajo.service.ReferralResult.RoscaNotFound -> {
                showError(getString(R.string.RoscaDetail_group_no_longer_exists))
            }
            
            is com.techducat.ajo.service.ReferralResult.AlreadyUsed -> {
                showError(getString(R.string.RoscaDetail_invite_already_used))
            }
            
            com.techducat.ajo.service.ReferralResult.NoReferral,
            com.techducat.ajo.service.ReferralResult.AlreadyProcessed -> {
                // No action needed
            }
        }
    }

    private fun getUserId(): String? {
        return com.techducat.ajo.util.AuthStateManager.getCurrentUserId(requireContext())
    }

    override fun onResume() {
        super.onResume()
        
        val userId = getUserId()
        if (userId.isNullOrEmpty()) {
            handleLogoutNavigation()
            return
        }
        
        loadRoscaDetails()
    }
    
    private fun updateLoginUI(state: com.techducat.ajo.ui.auth.LoginUiState) {
        if (state.isLoading) {
            binding.loginProgressBar.visibility = View.VISIBLE
        } else {
            binding.loginProgressBar.visibility = View.GONE
        }
        
        state.error?.let { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            loginViewModel.clearError()
        }
        
        if (state.isSignedIn) {
            onLoginSuccess()
        }
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
        binding.swipeRefresh.visibility = View.GONE
        binding.loginLayout.visibility = View.VISIBLE
        
        binding.btnGoogleSignIn.setOnClickListener {
            loginViewModel.startGoogleSignIn()
        }
    }
    
    private fun hideLoginPrompt() {
        binding.loginLayout.visibility = View.GONE
        binding.swipeRefresh.visibility = View.VISIBLE
    }
    
    private fun onLoginSuccess() {
        hideLoginPrompt()
        loadRoscaDetails()
    }        
            
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoscaDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database FIRST before any other operations
        database = AjoDatabase.getInstance(requireContext())
        
        setupLoginObservers()
        
        viewLifecycleOwner.lifecycleScope.launch {
            com.techducat.ajo.util.AuthStateManager.isLoggedIn.collect { isLoggedIn ->
                if (!isLoggedIn) {
                    Log.d(TAG, "Auth state changed: User logged out")
                    handleLogoutNavigation()
                }
            }
        }
        
        if (roscaId == null) {
            Toast.makeText(requireContext(), getString(R.string.RoscaDetail_invalid_rosca), Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }
        
        // Load ROSCA data and update wallet selection
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val rosca = database.roscaDao().getRoscaById(roscaId!!)
                if (rosca != null) {
                    withContext(Dispatchers.Main) {
                        WalletSelectionManager.selectRoscaWallet(
                            roscaId = rosca.id,
                            roscaName = rosca.name,
                            multisigAddress = rosca.multisigAddress
                        )
                        WalletSelectionManager.saveSelection(requireContext())
                        Log.d(TAG, "Set ROSCA wallet selection for: ${rosca.name}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting ROSCA wallet selection", e)
            }
        }
        
        setupRecyclerView()
        setupButtons()
        checkLoginAndLoadData()
    }
        
    private fun setupRecyclerView() {
        memberAdapter = RoscaMemberAdapter(
            onInviteClick = { member ->
                showMemberDetails(member)
            }
        )
        binding.recyclerViewMembers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = memberAdapter
        }
    }
    
    private fun setupButtons() {
        binding.btnViewContributions.setOnClickListener {
            Log.d(TAG, "View contributions clicked")
            navigateToContributions()
        }
        
        binding.btnMakeContribution.setOnClickListener {
            Log.d(TAG, "Make contribution button clicked")
            makeContribution()
        }
        
        // Payout button - only visible when round is ready for payout
        binding.btnTriggerPayout.setOnClickListener {
            Log.d(TAG, "Trigger payout button clicked")
            triggerPayout()
        }
        
        binding.swipeRefresh.setOnRefreshListener {
            loadRoscaDetails()
        }
        
        binding.btnAddMember.setOnClickListener {
            Log.d(TAG, "Add member button clicked")
            showAddMemberDialog()
        }
    }
    
    private fun loadRoscaDetails() {
        binding.swipeRefresh.isRefreshing = true
        
        lifecycleScope.launch {
            try {
                rosca = withContext(Dispatchers.IO) {
                    database.roscaDao().getRoscaById(roscaId!!)
                }
                
                if (rosca == null) {
                    Toast.makeText(requireContext(), getString(R.string.Dashboard_rosca_not_found), Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                    return@launch
                }
                
                updateUI(rosca!!)
                loadMembers()
                
                binding.swipeRefresh.isRefreshing = false
                
            } catch (e: Exception) {
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(
                    requireContext(),
                    getString(R.string.RoscaDetail_failed_load_details, e.message ?: ""),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    /**
     * Update round status and show payout button if ready
     * SIMPLIFIED VERSION - Works without RoundEntity
     */
    private suspend fun updateRoundStatus(rosca: RoscaEntity) {
        try {
            if (rosca.status != RoscaEntity.STATUS_ACTIVE && rosca.status != RoscaEntity.STATUS_COMPLETED) {
                binding.textViewNextPayout.text = getString(R.string.RoscaDetail_expecting_members)
                binding.textViewNextPayout.visibility = View.VISIBLE
                binding.layoutPayoutSection.visibility = View.GONE
                binding.btnTriggerPayout.visibility = View.GONE
                return
            }
            
            // For now, hide payout button since we don't have Round tracking yet
            // This will be enabled once Round entities are properly implemented
            binding.layoutPayoutSection.visibility = View.GONE
            binding.btnTriggerPayout.visibility = View.GONE
            
            // Show next payout calculation if active
            if (rosca.status == RoscaEntity.STATUS_ACTIVE) {
                val nextPayoutTimestamp = calculateNextPayout(rosca)
                if (nextPayoutTimestamp != null) {
                    binding.textViewNextPayout.text = getString(
                        R.string.RoscaDetail_next_payout_formatdate_nextpayouttimestamp, 
                        formatDate(nextPayoutTimestamp)
                    )
                    binding.textViewNextPayout.visibility = View.VISIBLE
                } else {
                    binding.textViewNextPayout.visibility = View.GONE
                }
            }
            
            /* 
            // TODO: Enable this when Round DAO is implemented
            val round = withContext(Dispatchers.IO) {
                database.roundDao().getRoundByNumber(roscaId!!, rosca.currentRound)
            }
            
            if (round != null) {
                val roundStatus = when (round.status) {
                    "CONTRIBUTION" -> getString(R.string.RoscaDetail_collecting_contributions)
                    "PAYOUT" -> getString(R.string.RoscaDetail_ready_for_payout)
                    "COMPLETED" -> getString(R.string.RoscaDetail_completed)
                    else -> round.status
                }
                
                binding.textViewNextPayout.text = "Round ${rosca.currentRound}: $roundStatus"
                binding.textViewNextPayout.visibility = View.VISIBLE
                
                // Show payout button if round is ready
                val isPayoutReady = round.status == "PAYOUT"
                binding.layoutPayoutSection.visibility = if (isPayoutReady) View.VISIBLE else View.GONE
                binding.btnTriggerPayout.visibility = if (isPayoutReady) View.VISIBLE else View.GONE
                binding.btnTriggerPayout.isEnabled = isPayoutReady
                
                if (isPayoutReady && round.recipientAddress != null) {
                    binding.textViewPayoutRecipient.text = getString(
                        R.string.RoscaDetail_payout_to,
                        round.recipientAddress.take(20)
                    )
                    binding.textViewPayoutRecipient.visibility = View.VISIBLE
                } else {
                    binding.textViewPayoutRecipient.visibility = View.GONE
                }
            } else {
                binding.layoutPayoutSection.visibility = View.GONE
            }
            */
            
        } catch (e: Exception) {
            Log.e(TAG, "Error updating round status", e)
            binding.textViewNextPayout.visibility = View.GONE
            binding.layoutPayoutSection.visibility = View.GONE
        }
    }
    
    private fun triggerPayout() {
        val currentRosca = rosca
        
        if (currentRosca == null) {
            showError(getString(R.string.RoscaDetail_rosca_data_not_loaded))
            return
        }
        
        if (currentRosca.status != RoscaEntity.STATUS_ACTIVE) {
            showError(getString(R.string.RoscaDetail_rosca_not_active))
            return
        }
        
        // Show confirmation dialog
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.RoscaDetail_trigger_payout))
            .setMessage(getString(R.string.RoscaDetail_initiate_payout_confirm, currentRosca.currentRound))
            .setPositiveButton(getString(R.string.RoscaDetail_proceed)) { _, _ ->
                processPayout(currentRosca)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
     /**
     * Process the payout
     */
    private fun processPayout(rosca: RoscaEntity) {
        lifecycleScope.launch {
            showProgress(getString(R.string.RoscaDetail_initiating_payout))
            Log.d(TAG, "=== TRIGGERING PAYOUT ===")
            Log.d(TAG, "ROSCA: ${rosca.name}")
            Log.d(TAG, "Round: ${rosca.currentRound}")
            
            try {
                // Call processPayout from RoscaManager
                val payoutResult: Result<String> = withContext(Dispatchers.IO) {
                    roscaManager.processPayout(rosca.id, rosca.currentRound)
                }
                
                hideProgress()
                
                // Handle the Result type properly
                payoutResult.fold(
                    onSuccess = { txHash: String ->
                        Log.i(TAG, "✅ Payout initiated successfully: $txHash")
                        showSuccess(getString(R.string.RoscaDetail_payout_initiated))
                        showSignatureCollectionDialog(rosca)
                        
                        // Reload details after 2 seconds
                        delay(2000)
                        loadRoscaDetails()
                    },
                    onFailure = { error: Throwable ->
                        Log.e(TAG, "❌ Payout failed: ${error.message}")
                        showError(getString(
                            R.string.RoscaDetail_payout_failed, 
                            error.message ?: getString(R.string.RoscaDetail_unknown_error)
                        ))
                    }
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "Exception triggering payout", e)
                hideProgress()
                showError(getString(
                    R.string.RoscaDetail_error_triggering_payout, 
                    e.message ?: getString(R.string.RoscaDetail_unknown_error)
                ))
            }
        }
    }
    
    /**
     * Show signature collection progress dialog
     */
    private fun showSignatureCollectionDialog(rosca: RoscaEntity) {
        val threshold = rosca.totalMembers - 1
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.RoscaDetail_collecting_signatures))
            .setMessage(getString(R.string.RoscaDetail_payout_tx_created_collecting, threshold, rosca.totalMembers))
            .setPositiveButton(getString(R.string.ok), null)
            .show()
    }     
    
    private fun updateUI(rosca: RoscaEntity) {
        binding.apply {
            textViewRoscaName.text = rosca.name
            textViewDescription.text = rosca.description?.ifEmpty { 
                getString(R.string.RoscaDetail_description) 
            } ?: getString(R.string.RoscaDetail_description)
            
            textViewStatus.text = rosca.status.uppercase()
            
            val statusColor = when (rosca.status) {
                RoscaEntity.STATUS_ACTIVE -> android.R.color.holo_green_dark
                RoscaEntity.STATUS_SETUP -> android.R.color.holo_orange_dark
                RoscaEntity.STATUS_COMPLETED -> android.R.color.holo_blue_dark
                else -> android.R.color.darker_gray
            }
            viewStatusIndicator.setBackgroundColor(requireContext().getColor(statusColor))
            
            textViewContributionAmount.text = formatXMR(rosca.contributionAmount)
            textViewFrequency.text = getString(
                R.string.RoscaDetail_every_rosca_frequencydays_days, 
                rosca.frequencyDays
            )
            textViewDistributionMethod.text = rosca.distributionMethod.uppercase()
            textViewCurrentRound.text = getString(
                R.string.RoscaDetail_round_rosca_currentround, 
                rosca.currentRound
            )
            textViewTotalRounds.text = getString(
                R.string.RoscaDetail_rosca_totalmembers, 
                rosca.totalMembers
            )
            
            textViewCreatedDate.text = getString(
                R.string.RoscaDetail_created_formatdate_rosca_createdat, 
                formatDate(rosca.createdAt)
            )
            
            // ✅ NEW: Show current round status and payout info
            lifecycleScope.launch {
                updateRoundStatus(rosca)
            }
            
            // Update button states
            val isContributionEnabled = rosca.status == RoscaEntity.STATUS_ACTIVE
            btnMakeContribution.isEnabled = isContributionEnabled
            btnMakeContribution.alpha = if (isContributionEnabled) 1.0f else 0.4f
            
            val isAddMemberEnabled = rosca.status == RoscaEntity.STATUS_ACTIVE || 
                                     rosca.status == RoscaEntity.STATUS_SETUP
            btnAddMember.isEnabled = isAddMemberEnabled
            btnAddMember.alpha = if (isAddMemberEnabled) 1.0f else 0.4f
            
            if (rosca.status == RoscaEntity.STATUS_SETUP) {
                btnMakeContribution.text = getString(R.string.RoscaDetail_not_setup)
            } else {
                btnMakeContribution.text = getString(R.string.RoscaDetail_make_contribution)
            }
            
            Log.d(TAG, "UI updated for ROSCA: ${rosca.name}, Status: ${rosca.status}")
        }
    }
    
    private fun handleLogoutNavigation() {
        rosca = null
        roscaId = null
        memberAdapter.submitList(emptyList())
        
        try {
            findNavController().navigateUp()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to navigate back on logout", e)
            showLoginPrompt()
        }
    }    
    
    private fun calculateNextPayout(rosca: RoscaEntity): Long? {
        if (rosca.status != RoscaEntity.STATUS_ACTIVE) return null
        
        val startDate = rosca.startDate ?: rosca.startedAt ?: return null
        val currentTime = System.currentTimeMillis()
        
        if (startDate > currentTime) {
            return startDate
        }
        
        val millisSinceStart = currentTime - startDate
        val daysSinceStart = millisSinceStart / (1000L * 60L * 60L * 24L)
        val cyclesPassed = (daysSinceStart / rosca.frequencyDays).toInt()
        val nextCycle = cyclesPassed + 1
        val nextPayoutDays = nextCycle * rosca.frequencyDays
        val nextPayoutMillis = startDate + (nextPayoutDays * 24L * 60L * 60L * 1000L)
        
        val totalCycles = if (rosca.totalCycles > 0) rosca.totalCycles else rosca.totalMembers
        return if (nextCycle <= totalCycles && nextPayoutMillis > currentTime) {
            nextPayoutMillis
        } else {
            null
        }
    }
    
    private fun loadMembers() {
        lifecycleScope.launch {
            try {
                val members = withContext(Dispatchers.IO) {
                    database.memberDao().getMembersByGroupSync(roscaId!!)
                }
                memberAdapter.submitList(members)
                
                val activeMembers = members.count { it.isActive }
                
                rosca?.let { currentRosca ->
                    binding.textViewMemberCount.text = getString(R.string.RoscaDetail_members_size_members, activeMembers, currentRosca.totalMembers)
                    binding.textViewActiveMembers.text = getString(R.string.RoscaDetail_activemembers_active, activeMembers)
                    
                    if (currentRosca.status == RoscaEntity.STATUS_SETUP) {
                        val progress = getString(R.string.RoscaDetail_members_joined_progress, activeMembers, currentRosca.totalMembers)
                        binding.textViewNextPayout.text = progress
                        binding.textViewNextPayout.visibility = View.VISIBLE
                        
                        if (activeMembers >= currentRosca.totalMembers) {
                            checkAndActivateRosca(currentRosca)
                        }
                    }
                }
                
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.RoscaDetail_failed_load_members, e.message ?: ""),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun navigateToContributions() {
        val intent = Intent(requireContext(), GroupContributionsActivity::class.java).apply {
            putExtra("rosca_id", roscaId)
        }
        startActivity(intent)
    }
    
    private fun makeContribution() {
        Log.d(TAG, "makeContribution() called")
        val currentRosca = rosca
        
        if (currentRosca == null) {
            Log.e(TAG, "ROSCA is null, cannot make contribution")
            showError(getString(R.string.RoscaDetail_rosca_data_not_loaded))
            return
        }
        
        Log.d(TAG, "Checking wallet balance...")
        
        try {
            walletSuite.getBalance(object : WalletSuite.BalanceCallback {
                override fun onSuccess(balance: Long, unlocked: Long) {
                    Log.d(TAG, "Balance check success - Balance: $balance, Unlocked: $unlocked, Required: ${currentRosca.contributionAmount}")
                    activity?.runOnUiThread {
                        if (unlocked < currentRosca.contributionAmount) {
                            showInsufficientBalanceDialog(currentRosca.contributionAmount, unlocked)
                        } else {
                            showContributionConfirmDialog(currentRosca)
                        }
                    }
                }
                
                override fun onError(error: String) {
                    Log.e(TAG, "Balance check failed: $error")
                    activity?.runOnUiThread {
                        showError(getString(R.string.RoscaDetail_failed_check_balance, error))
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Exception checking balance", e)
            showError(getString(R.string.RoscaDetail_error_checking_balance, e.message ?: ""))
        }
    }
    
    private fun showInsufficientBalanceDialog(required: Long, available: Long) {
        val requiredXmr = formatXMR(required)
        val availableXmr = formatXMR(available)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.Wallet_insufficient_balance))
            .setMessage(getString(R.string.RoscaDetail_you_need_requiredxmr_but, requiredXmr, availableXmr))
            .setPositiveButton(getString(R.string.ok), null)
            .show()
    }
    
    private fun showContributionConfirmDialog(rosca: RoscaEntity) {
        val amountXmr = formatXMR(rosca.contributionAmount)
        
        Log.d(TAG, "Showing contribution confirmation dialog for $amountXmr")
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.RoscaDetail_make_contribution))
            .setMessage(getString(R.string.RoscaDetail_contribute_amountxmr_rosca_name, amountXmr, rosca.name))
            .setPositiveButton(getString(R.string.contribute)) { _, _ ->
                Log.d(TAG, "User confirmed contribution")
                processContribution(rosca)
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                Log.d(TAG, "User cancelled contribution")
            }
            .show()
    }
    
    private fun processContribution(rosca: RoscaEntity) {
        lifecycleScope.launch {
            try {
                showProgress(getString(R.string.RoscaDetail_processing_contribution))
                Log.d(TAG, "=== Starting contribution process ===")
                Log.d(TAG, "ROSCA: ${rosca.name}")
                
                val userId = getUserId() ?: run {
                    hideProgress()
                    showError(getString(R.string.RoscaDetail_not_logged_in))
                    Log.e(TAG, "User ID is null")
                    return@launch
                }
                
                Log.d(TAG, "User ID: $userId")
                Log.d(TAG, "Contribution amount: ${rosca.contributionAmount} atomic units (${formatXMR(rosca.contributionAmount)})")
                
                val poolAddress = rosca.resolveWalletAddress()
                if (poolAddress.isNullOrEmpty()) {
                    hideProgress()
                    showError(getString(R.string.RoscaDetail_pool_address_not_set))
                    Log.e(TAG, "ROSCA pool address is null or empty")
                    return@launch
                }
                
                Log.d(TAG, "Pool address: $poolAddress")
                
                val amountXmr = rosca.contributionAmount.toDouble() / 1_000_000_000_000.0
                Log.d(TAG, "Sending $amountXmr XMR to pool")
                
                walletSuite.sendTransaction(
                    poolAddress,
                    amountXmr,
                    object : WalletSuite.TransactionCallback {
                        override fun onSuccess(txId: String, amount: Long) {
                            Log.d(TAG, "✓ Transaction successful!")
                            Log.d(TAG, "TX ID: $txId")
                            Log.d(TAG, "Amount sent: $amount atomic units")
                            
                            lifecycleScope.launch {
                                try {
                                    val currentTime = System.currentTimeMillis()
                                    
                                    val startDate = rosca.startDate ?: rosca.startedAt ?: currentTime
                                    val dueDate = startDate + (rosca.currentRound * rosca.frequencyDays * 24L * 60L * 60L * 1000L)
                                    
                                    val contribution = ContributionEntity(
                                        id = UUID.randomUUID().toString(),
                                        roscaId = roscaId!!,
                                        memberId = userId,
                                        amount = rosca.contributionAmount,
                                        cycleNumber = rosca.currentRound,
                                        status = ContributionEntity.STATUS_CONFIRMED,
                                        dueDate = dueDate,
                                        txHash = txId,
                                        txId = txId,
                                        proofOfPayment = txId,
                                        verifiedAt = currentTime,
                                        notes = getString(R.string.RoscaDetail_contribution_for_cycle, rosca.currentRound),
                                        createdAt = currentTime,
                                        updatedAt = currentTime,
                                        isDirty = true,
                                        lastSyncedAt = null,
                                        ipfsHash = null
                                    )
                                    
                                    withContext(Dispatchers.IO) {
                                        database.contributionDao().insert(contribution)
                                        Log.d(TAG, "✓ Contribution recorded in database: ${contribution.id}")
                                    }
                                    
                                    hideProgress()
                                    showSuccess(getString(R.string.RoscaDetail_contribution_successful, txId.take(8)))
                                    loadRoscaDetails()
                                    
                                } catch (e: Exception) {
                                    Log.e(TAG, "Failed to record contribution", e)
                                    hideProgress()
                                    showError(getString(R.string.RoscaDetail_tx_sent_failed_record, e.message ?: ""))
                                }
                            }
                        }
                        
                        override fun onError(error: String) {
                            Log.e(TAG, "✗ Transaction failed: $error")
                            activity?.runOnUiThread {
                                hideProgress()
                                showError(getString(R.string.RoscaDetail_transaction_failed, error))
                            }
                        }
                    }
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "Exception in processContribution", e)
                hideProgress()
                showError(getString(R.string.RoscaDetail_contribution_failed, e.message ?: ""))
            }
        }
    }
    
    private fun showAddMemberDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.RoscaDetail_invite_member))
            .setMessage(getString(R.string.RoscaDetail_generate_invite_link_that))
            .setPositiveButton(getString(R.string.RoscaDetail_generate_invite_link)) { _, _ ->
                createInvite()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun createInvite() {
        lifecycleScope.launch {
            try {
                showProgress(getString(R.string.RoscaDetail_creating_invite))
                
                val userId = getUserId() ?: run {
                    hideProgress()
                    showError(getString(R.string.RoscaDetail_not_logged_in))
                    return@launch
                }
                
                Log.d(TAG, "Creating new invite link")
                
                val inviteCode = generateUniqueInviteCode()
                
                val invite = InviteEntity(
                    id = UUID.randomUUID().toString(),
                    roscaId = roscaId!!,
                    inviterUserId = userId,
                    inviteeEmail = "",
                    referralCode = inviteCode,
                    status = InviteEntity.STATUS_PENDING,
                    createdAt = System.currentTimeMillis(),
                    expiresAt = System.currentTimeMillis() + (30L * 24L * 60L * 60L * 1000L),
                    acceptedAt = null,
                    acceptedByUserId = null
                )
                
                withContext(Dispatchers.IO) {
                    database.inviteDao().insertInvite(invite)
                    Log.d(TAG, "✓ Invite created: ${invite.id}, Code: $inviteCode")
                }
                
                hideProgress()
                showInviteLinkDialog(inviteCode)
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create invite", e)
                hideProgress()
                showError(getString(R.string.RoscaDetail_failed_create_invite, e.message ?: ""))
            }
        }
    }
    
    private suspend fun generateUniqueInviteCode(): String {
        return withContext(Dispatchers.IO) {
            var code: String
            var attempts = 0
            do {
                code = UUID.randomUUID().toString().substring(0, 8).uppercase()
                val existing = database.inviteDao().getInviteByReferralCode(code)
                attempts++
            } while (existing != null && attempts < 10)
            
            if (attempts >= 10) {
                throw Exception(getString(R.string.RoscaDetail_failed_generate_code))
            }
            code
        }
    }

    private fun showInviteLinkDialog(inviteCode: String) {
        val appDeepLink = "ajo://join?ref=$inviteCode&rosca=$roscaId"
        
        try {
            // ✅ Generate QR code bitmap to display inline
            val qrBitmap = QRCodeGenerator.generate(appDeepLink, 512)
            
            // Create custom view with QR code displayed
            val dialogView = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(48, 48, 48, 48)
                
                // QR Code ImageView
                addView(ImageView(context).apply {
                    setImageBitmap(qrBitmap)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 24
                    }
                    scaleType = ImageView.ScaleType.FIT_CENTER
                })
                
                // Invite code text (tappable to copy)
                addView(TextView(context).apply {
                    text = "Code: $inviteCode"
                    textSize = 18f
                    gravity = android.view.Gravity.CENTER
                    setPadding(16, 16, 16, 16)
                    setBackgroundColor(0xFFF5F5F5.toInt())
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 16
                    }
                    setOnClickListener {
                        copyToClipboard(inviteCode)
                        showSuccess("Invite code copied to clipboard")
                    }
                })
                
                // Instructions
                addView(TextView(context).apply {
                    text = "Share this QR code with people you want to invite. Tap the code above to copy it."
                    textSize = 14f
                    gravity = android.view.Gravity.CENTER
                    setTextColor(android.graphics.Color.GRAY)
                })
            }
            
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Invite via QR Code")
                .setView(dialogView)
                .setPositiveButton("View Full Screen") { _, _ ->
                    showQRCodeFullScreen(appDeepLink, inviteCode)
                }
                .setNeutralButton("Share QR Image") { _, _ ->
                    shareQRCodeImage(appDeepLink, inviteCode)
                }
                .setNegativeButton("Save to Gallery") { _, _ ->
                    saveQRCodeToGallery(appDeepLink, inviteCode)
                }
                .show()
                
        } catch (e: Exception) {
            Log.e(TAG, "Error generating QR code for dialog", e)
            showError("Failed to generate QR code: ${e.message}")
        }
    }

    private fun shareInviteLink(inviteLink: String, inviteCode: String) {
        val shareText = buildString {
            append(getString(R.string.RoscaDetail_share_intro))
            append("\n\n")
            
            rosca?.let { r ->
                append(getString(R.string.RoscaDetail_share_group_name, r.name))
                append("\n")
                append(getString(R.string.RoscaDetail_share_contribution, formatXMR(r.contributionAmount)))
                append("\n")
                append(getString(R.string.RoscaDetail_share_frequency, r.frequencyDays))
                append("\n\n")
            }
            
            append(getString(R.string.RoscaDetail_share_join_link, inviteLink))
            append("\n\n")
            append(getString(R.string.RoscaDetail_share_or_code, inviteCode))
            append("\n\n")
            append(getString(R.string.RoscaDetail_share_how_to_join))
            append("\n")
            append(getString(R.string.RoscaDetail_share_step1))
            append("\n")
            append(getString(R.string.RoscaDetail_share_step2))
            append("\n")
            append(getString(R.string.RoscaDetail_share_step3))
            append("\n\n")
            append(getString(R.string.RoscaDetail_share_expires))
            append("\n\n")
            append(getString(R.string.RoscaDetail_share_closing))
        }
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.RoscaDetail_share_subject))
        }
        
        try {
            startActivity(Intent.createChooser(shareIntent, getString(R.string.RoscaDetail_share_via)))
            Log.d(TAG, "Share dialog opened for invite code: $inviteCode")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open share dialog", e)
            copyToClipboard(inviteLink)
            showSuccess(getString(R.string.RoscaDetail_link_copied_clipboard_fallback, inviteLink))
        }
    }
    
    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText(getString(R.string.RoscaDetail_invite), text)
        clipboard.setPrimaryClip(clip)
    }
    
    private fun showQRCodeFullScreen(deepLink: String, inviteCode: String) {
        try {
            // Generate QR code bitmap
            val qrBitmap = QRCodeGenerator.generate(deepLink, 800)
            
            // Create a custom dialog with full-screen QR code
            val imageView = ImageView(requireContext()).apply {
                setImageBitmap(qrBitmap)
                setPadding(32, 32, 32, 32)
                setBackgroundColor(android.graphics.Color.WHITE)
            }
            
            val codeTextView = TextView(requireContext()).apply {
                text = "Invite Code: $inviteCode"
                textSize = 16f
                gravity = android.view.Gravity.CENTER
                setPadding(16, 24, 16, 16)
            }
            
            val instructionTextView = TextView(requireContext()).apply {
                text = "Have the new member scan this QR code to join the ROSCA"
                textSize = 14f
                gravity = android.view.Gravity.CENTER
                setPadding(16, 8, 16, 32)
                setTextColor(android.graphics.Color.GRAY)
            }
            
            val layout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                addView(imageView)
                addView(codeTextView)
                addView(instructionTextView)
            }
            
            MaterialAlertDialogBuilder(requireContext())
                .setView(layout)
                .setPositiveButton("Close", null)
                .setNeutralButton("Share") { _, _ ->
                    shareQRCodeImage(deepLink, inviteCode)
                }
                .setNegativeButton("Save") { _, _ ->
                    saveQRCodeToGallery(deepLink, inviteCode)
                }
                .show()
                
        } catch (e: Exception) {
            Log.e(TAG, "Error showing QR code", e)
            showError("Failed to generate QR code: ${e.message}")
        }
    }
    
    private fun shareQRCodeImage(deepLink: String, inviteCode: String) {
        lifecycleScope.launch {
            try {
                showProgress("Preparing QR code...")
                
                // Generate QR code bitmap
                val qrBitmap = QRCodeGenerator.generate(deepLink, 800)
                
                // Save to cache directory temporarily
                val cachePath = File(requireContext().cacheDir, "qr_codes")
                cachePath.mkdirs()
                val imageFile = File(cachePath, "invite_qr_$inviteCode.png")
                
                withContext(Dispatchers.IO) {
                    imageFile.outputStream().use { out ->
                        qrBitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
                    }
                }
                
                // Create share intent with file provider
                val imageUri = androidx.core.content.FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    imageFile
                )
                
                val shareText = buildString {
                    rosca?.let { r ->
                        append("Join my ROSCA: ${r.name}\n\n")
                        append("Contribution: ${formatXMR(r.contributionAmount)} XMR\n")
                        append("Frequency: Every ${r.frequencyDays} days\n\n")
                    }
                    append("Scan the QR code to join!\n")
                    append("Invite Code: $inviteCode")
                }
                
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    putExtra(Intent.EXTRA_SUBJECT, "Join my ROSCA")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                hideProgress()
                startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
                
            } catch (e: Exception) {
                hideProgress()
                Log.e(TAG, "Error sharing QR code", e)
                showError("Failed to share QR code: ${e.message}")
            }
        }
    }
    
    private fun saveQRCodeToGallery(deepLink: String, inviteCode: String) {
        lifecycleScope.launch {
            try {
                showProgress("Saving QR code...")
                
                // Generate QR code bitmap
                val qrBitmap = QRCodeGenerator.generate(deepLink, 1024)
                
                withContext(Dispatchers.IO) {
                    // Save to Pictures directory
                    val picturesDir = android.os.Environment.getExternalStoragePublicDirectory(
                        android.os.Environment.DIRECTORY_PICTURES
                    )
                    val ajoDir = File(picturesDir, "AJO_QR_Codes")
                    ajoDir.mkdirs()
                    
                    val imageFile = File(ajoDir, "ROSCA_Invite_${inviteCode}_${System.currentTimeMillis()}.png")
                    
                    imageFile.outputStream().use { out ->
                        qrBitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
                    }
                    
                    // Notify media scanner
                    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    mediaScanIntent.data = android.net.Uri.fromFile(imageFile)
                    requireContext().sendBroadcast(mediaScanIntent)
                }
                
                hideProgress()
                showSuccess("QR code saved to Gallery in AJO_QR_Codes folder")
                
            } catch (e: Exception) {
                hideProgress()
                Log.e(TAG, "Error saving QR code", e)
                showError("Failed to save QR code: ${e.message}")
            }
        }
    }
    
    private fun showMemberDetails(member: MemberEntity) {
        val details = buildString {
            append(getString(R.string.RoscaDetail_member_number, member.position))
            append("\n\n")
            append(getString(R.string.RoscaDetail_name, member.name))
            append("\n")
            append(getString(R.string.RoscaDetail_status, if (member.isActive) getString(R.string.RoscaDetail_active) else getString(R.string.RoscaDetail_pending)))
            append("\n")
            append(getString(R.string.RoscaDetail_joined, formatDate(member.joinedAt)))
            append("\n")
            if (member.totalContributed > 0) {
                append(getString(R.string.RoscaDetail_total_contributed, formatXMR(member.totalContributed)))
                append("\n")
            }
            if (member.hasReceivedPayout) {
                append("\n")
                append(getString(R.string.RoscaDetail_has_received_payout))
            }
        }
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.RoscaDetail_member_details))
            .setMessage(details)
            .setPositiveButton(getString(R.string.ok), null)
            .show()
    }
    
    private fun formatXMR(atomic: Long): String {
        return CurrencyFormatter.formatXMR(atomic, decimals = 6)
    }
    
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    private fun showProgress(message: String) {
        binding.swipeRefresh.isRefreshing = true
    }
    
    private fun hideProgress() {
        binding.swipeRefresh.isRefreshing = false
    }
    
    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
    
    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.RoscaDetail_dismiss)) { }
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class RoscaMemberAdapter(
    private val onInviteClick: (MemberEntity) -> Unit = {}
) : androidx.recyclerview.widget.ListAdapter<MemberEntity, RoscaMemberAdapter.ViewHolder>(
    MemberDiffCallback()
) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = com.techducat.ajo.databinding.ItemRoscaMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onInviteClick)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
    
    class ViewHolder(
        private val binding: com.techducat.ajo.databinding.ItemRoscaMemberBinding,
        private val onInviteClick: (MemberEntity) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        
        fun bind(member: MemberEntity, position: Int) {
            val context = binding.root.context
            
            binding.textViewMemberName.text = member.name.ifEmpty { 
                context.getString(R.string.RoscaDetail_member_position, member.position) 
            }
            
            val statusText = if (member.isActive) {
                context.getString(R.string.RoscaDetail_active)
            } else {
                context.getString(R.string.RoscaDetail_pending)
            }
            binding.textViewStatus.text = statusText
            
            val statusColor = if (member.isActive) {
                android.R.color.holo_green_dark
            } else {
                android.R.color.darker_gray
            }
            binding.viewStatusIndicator.setBackgroundColor(context.getColor(statusColor))
            
            if (member.hasReceivedPayout) {
                binding.textViewPayoutReceived.visibility = View.VISIBLE
                binding.textViewPayoutReceived.text = context.getString(R.string.RoscaDetail_payout_received)
            } else {
                binding.textViewPayoutReceived.visibility = View.GONE
            }
            
            binding.textViewJoinedDate.text = context.getString(
                R.string.RoscaDetail_joined_date,
                formatDate(member.joinedAt)
            )
            
            binding.root.setOnClickListener {
                onInviteClick(member)
            }
        }
        
        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
}

class MemberDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<MemberEntity>() {
    override fun areItemsTheSame(oldItem: MemberEntity, newItem: MemberEntity): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: MemberEntity, newItem: MemberEntity): Boolean {
        return oldItem == newItem
    }
}
