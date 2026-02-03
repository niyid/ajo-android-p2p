package com.techducat.ajo.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.techducat.ajo.R
import com.techducat.ajo.databinding.FragmentDashboardBinding
import com.techducat.ajo.service.RoscaManager
import com.techducat.ajo.wallet.WalletSuite
import com.techducat.ajo.model.Rosca
import com.techducat.ajo.ui.auth.LoginViewModel
import com.techducat.ajo.ui.create.CreateRoscaActivity
import androidx.appcompat.app.AppCompatActivity
import java.util.UUID
import com.techducat.ajo.model.Member
import com.techducat.ajo.model.Invite
import com.techducat.ajo.util.WalletSelectionManager
import com.techducat.ajo.ui.sync.ReferralScannerActivity

class DashboardFragment : Fragment() {

    companion object {
        private const val TAG = "DashboardFragment"
    }  
    
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private val roscaManager: RoscaManager by inject()
    private val walletSuite: WalletSuite by inject()
    private val loginViewModel: LoginViewModel by viewModel()
    private lateinit var adapter: RoscaListAdapter
    
    // ✅ NEW: Track if join operation is in progress
    private var isJoiningRosca = false
    
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        loginViewModel.handleSignInResult(result.data)
    }

    private val createRoscaLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            Log.d(TAG, "✓ ROSCA created successfully, refreshing list...")
            checkLoginAndLoadData()
        } else {
            Log.d(TAG, "Create ROSCA cancelled or failed")
        }
    }
    
    // ✅ NEW: QR Scanner Launcher
    private val qrScannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val scannedCode = result.data?.getStringExtra("referral_code")
            if (!scannedCode.isNullOrEmpty()) {
                Log.d(TAG, "✓ QR Code scanned: $scannedCode")
                binding.editTextInviteCode.setText(scannedCode)
                processInviteCode(scannedCode)
            } else {
                Log.d(TAG, "QR Scanner returned but no code found")
                // Refresh the list anyway in case the scanner handled joining internally
                checkLoginAndLoadData()
            }
        } else {
            Log.d(TAG, "QR Scanner cancelled or failed")
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupEmptyState()
        setupErrorState()
        setupSwipeRefresh()
        setupFab()
        setupLoginObservers()
        setupInviteInput()
        setupQRScanner()  // ✅ NEW: Setup QR scanner button
        
        debugUserIdentity()
        checkLoginAndLoadData()
        
        viewLifecycleOwner.lifecycleScope.launch {
            com.techducat.ajo.util.AuthStateManager.isLoggedIn.collect { isLoggedIn ->
                if (!isLoggedIn) {
                    Log.d(TAG, "Auth state changed: User logged out")
                    showLoginPrompt()
                    adapter.submitList(emptyList())
                    updateSummaryStats(emptyList())
                    resetDashboardUI()
                }
            }
        }    
    }
    
    private fun setupInviteInput() {
        binding.btnJoinWithCode.setOnClickListener {
            val code = binding.editTextInviteCode.text?.toString()?.trim()?.uppercase()
            
            if (code.isNullOrEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.Dashboard_please_enter_invite_code), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            processInviteCode(code)
        }
    }
    
    // ✅ NEW: Setup QR Scanner Button
    private fun setupQRScanner() {
        binding.btnScanQR.setOnClickListener {
            val userId = getUserId()
            if (userId.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(), 
                    getString(R.string.Dashboard_please_log_first), 
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            
            Log.d(TAG, "Launching QR Scanner...")
            try {
                val intent = Intent(requireContext(), ReferralScannerActivity::class.java)
                qrScannerLauncher.launch(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Error launching QR scanner", e)
                Toast.makeText(
                    requireContext(), 
                    getString(R.string.Dashboard_qr_scanner_manual_fallback), 
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun processInviteCode(code: String) {
        val userId = getUserId()
        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.Dashboard_please_log_first), Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            try {
                binding.btnJoinWithCode.isEnabled = false
                
                val invite: Invite? = withContext(Dispatchers.IO) {
                    roscaManager.repository.getInviteByReferralCode(code)
                }
                
                if (invite == null) {
                    Toast.makeText(requireContext(), getString(R.string.Dashboard_invalid_invite_code), Toast.LENGTH_SHORT).show()
                    binding.btnJoinWithCode.isEnabled = true
                    return@launch
                }
                
                if (invite.isExpired) {
                    Toast.makeText(requireContext(), getString(R.string.Dashboard_this_invite_has_expired), Toast.LENGTH_SHORT).show()
                    binding.btnJoinWithCode.isEnabled = true
                    return@launch
                }
                
                if (invite.status != Invite.InviteStatus.PENDING) {
                    Toast.makeText(requireContext(), getString(R.string.Dashboard_this_invite_has_already), Toast.LENGTH_SHORT).show()
                    binding.btnJoinWithCode.isEnabled = true
                    return@launch
                }
                
                val rosca = withContext(Dispatchers.IO) {
                    roscaManager.repository.getRoscaById(invite.roscaId)
                }
                
                if (rosca == null) {
                    Toast.makeText(requireContext(), getString(R.string.Dashboard_rosca_not_found), Toast.LENGTH_SHORT).show()
                    binding.btnJoinWithCode.isEnabled = true
                    return@launch
                }
                
                val existingMember: Member? = withContext(Dispatchers.IO) {
                    roscaManager.repository.getMembersByRoscaId(invite.roscaId)
                        .find { member -> member.userId == userId || member.walletAddress == userId }
                }
                
                if (existingMember != null) {
                    Toast.makeText(requireContext(), getString(R.string.Dashboard_you_already_member_this), Toast.LENGTH_SHORT).show()
                    binding.btnJoinWithCode.isEnabled = true
                    return@launch
                }
                
                val currentMembers: Int = withContext(Dispatchers.IO) {
                    roscaManager.repository.getMembersByRoscaId(invite.roscaId)
                        .count { member -> member.isActive }
                }
                
                if (currentMembers >= rosca.totalMembers) {
                    Toast.makeText(requireContext(), getString(R.string.Dashboard_this_rosca_full), Toast.LENGTH_SHORT).show()
                    binding.btnJoinWithCode.isEnabled = true
                    return@launch
                }
                
                showJoinConfirmationDialog(rosca.name, invite, userId)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error processing invite code", e)
                Toast.makeText(requireContext(), getString(R.string.Dashboard_error_message, e.message), Toast.LENGTH_LONG).show()
                binding.btnJoinWithCode.isEnabled = true
            }
        }
    }
    
    private fun showJoinConfirmationDialog(roscaName: String, invite: Invite, userId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.Dashboard_join_rosca))
            .setMessage(getString(R.string.Dashboard_you_want_join_roscaname, roscaName))
            .setPositiveButton(getString(R.string.Dashboard_join)) { _, _ ->
                joinRosca(invite, userId)
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                binding.btnJoinWithCode.isEnabled = true
            }
            .setOnCancelListener {
                binding.btnJoinWithCode.isEnabled = true
            }
            .show()
    }
    
    private fun joinRosca(invite: Invite, userId: String) {
        lifecycleScope.launch {
            try {
                // ✅ NEW: Show loading overlay and prevent interaction
                isJoiningRosca = true
                showJoinLoading(true)
                
                Log.d(TAG, "Joining ROSCA via RoscaManager for invite: ${invite.referralCode}")
                
                val result = roscaManager.joinRosca(
                    roscaId = invite.roscaId,
                    setupInfo = "", // Empty for manual invite code join
                    context = requireContext()
                )
                
                if (result.isSuccess) {
                    val member = result.getOrThrow()
                    
                    Log.d(TAG, "✓ Successfully joined ROSCA")
                    Log.d(TAG, "  Member ID: ${member.id}")
                    Log.d(TAG, "  User ID: ${member.userId}")
                    Log.d(TAG, "  Has MultisigInfo: ${member.multisigInfo != null}")
                    
                    // Update invite status
                    val updatedInvite = invite.copy(
                        status = Invite.InviteStatus.ACCEPTED,
                        acceptedAt = System.currentTimeMillis(),
                        acceptedByUserId = userId
                    )
                    
                    withContext(Dispatchers.IO) {
                        roscaManager.repository.updateInvite(updatedInvite)
                    }
                    
                    Toast.makeText(
                        requireContext(), 
                        getString(R.string.Dashboard_successfully_joined_rosca), 
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    binding.editTextInviteCode.text?.clear()
                    
                    // Refresh the ROSCA list
                    loadRoscas()
                    
                } else {
                    val error = result.exceptionOrNull()?.message ?: getString(R.string.RoscaDetail_unknown_error)
                    Log.e(TAG, "Failed to join ROSCA: $error", result.exceptionOrNull())
                    
                    Toast.makeText(
                        requireContext(), 
                        getString(R.string.Dashboard_failed_join_message, error), 
                        Toast.LENGTH_LONG
                    ).show()
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error joining ROSCA", e)
                Toast.makeText(
                    requireContext(), 
                    getString(R.string.Dashboard_failed_join_message, e.message), 
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                // ✅ NEW: Always hide loading overlay and re-enable interaction
                isJoiningRosca = false
                showJoinLoading(false)
                binding.btnJoinWithCode.isEnabled = true
            }
        }
    }
    
    /**
     * ✅ NEW: Show/hide loading overlay for ROSCA join operation
     */
    private fun showJoinLoading(show: Boolean) {
        if (show) {
            // Show loading overlay
            binding.roscaCreationOverlay.visibility = View.VISIBLE
            
            // Disable all interactive elements
            binding.btnJoinWithCode.isEnabled = false
            binding.editTextInviteCode.isEnabled = false
            binding.fabCreate.isEnabled = false
            binding.swipeRefreshLayout.isEnabled = false
            
            // Update loading text
            // Note: The layout reuses the same overlay from CreateRoscaActivity
            // If you want different text, you'll need to add a TextView ID to the overlay
            
        } else {
            // Hide loading overlay
            binding.roscaCreationOverlay.visibility = View.GONE
            
            // Re-enable interactive elements
            binding.btnJoinWithCode.isEnabled = true
            binding.editTextInviteCode.isEnabled = true
            binding.fabCreate.isEnabled = true
            binding.swipeRefreshLayout.isEnabled = true
        }
    }
    
    private fun debugUserIdentity() {
        val sharedPrefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val storedUserId = sharedPrefs.getString("user_id", null)
        
        lifecycleScope.launch {
            val walletAddress = walletSuite.userWallet?.address
            
            Log.d(TAG, "=== USER IDENTITY DEBUG ===")
            Log.d(TAG, "SharedPrefs user_id: $storedUserId")
            Log.d(TAG, "Wallet address: $walletAddress")
            Log.d(TAG, "Match: ${storedUserId == walletAddress}")
            
            try {
                val allRoscas = withContext(Dispatchers.IO) {
                    roscaManager.repository.getAllRoscas()
                }
                
                Log.d(TAG, "Total ROSCAs in database: ${allRoscas.size}")
                allRoscas.forEach { rosca ->
                    Log.d(TAG, "  ROSCA: ${rosca.name} (ID: ${rosca.id})")
                }
                
                val allMembers = withContext(Dispatchers.IO) {
                    roscaManager.repository.getAllMembers()
                }
                
                Log.d(TAG, "All members (${allMembers.size}):")
                allMembers.forEach { member ->
                    Log.d(TAG, "  - Member ID: ${member.id}")
                    Log.d(TAG, "    userId: ${member.userId}")
                    Log.d(TAG, "    walletAddress: ${member.walletAddress}")
                    Log.d(TAG, "    roscaId: ${member.roscaId}")
                    Log.d(TAG, "    name: ${member.name}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in debug", e)
            }
            
            Log.d(TAG, "=========================")
        }
    }
    
    private fun updateSummaryStats(roscas: List<Rosca>) {
        val activeCount = roscas.count { it.status == Rosca.RoscaState.ACTIVE }
        val completedCount = roscas.count { it.status == Rosca.RoscaState.COMPLETED }
        val totalCount = roscas.size
        
        binding.apply {
            tvActiveCount.text = activeCount.toString()
            tvTotalCount.text = totalCount.toString()
            tvCompletedCount.text = completedCount.toString()
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
            showLoading()
        } else {
            hideLoading()
        }
        
        state.error?.let { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            loginViewModel.clearError()
        }
        
        if (state.isSignedIn) {
            loadRoscas()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = RoscaListAdapter { rosca ->
            WalletSelectionManager.selectRoscaWallet(
                roscaId = rosca.id,
                roscaName = rosca.name,
                multisigAddress = rosca.multisigAddress
            )
            lifecycleScope.launch {
                WalletSelectionManager.saveSelection(requireContext())
            }
            
            navigateToRoscaDetail(rosca.id)
        }
        
        binding.recyclerViewRoscas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DashboardFragment.adapter
            setHasFixedSize(true)
        }
    }
    
    private fun setupEmptyState() {
        binding.btnCreateRosca.setOnClickListener {
            navigateToCreateRosca()
        }
    }
    
    private fun setupErrorState() {
        binding.btnRetry.setOnClickListener {
            checkLoginAndLoadData()
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                checkLoginAndLoadData()
            }
            setColorSchemeResources(
                R.color.purple_500,
                R.color.purple_700,
                R.color.teal_200
            )
        }
    }
    
    private fun setupFab() {
        binding.fabCreate.setOnClickListener {
            navigateToCreateRosca()
        }
    }
    
    private fun checkLoginAndLoadData() {
        val userId = getUserId()
        
        if (userId.isNullOrEmpty()) {
            Log.d(TAG, "User not logged in, showing login prompt")
            showLoginPrompt()
        } else {
            Log.d(TAG, "User logged in with ID: $userId, loading ROSCAs")
            loadRoscas()

            lifecycleScope.launch {
                try {
                    val syncResult = roscaManager.syncMemberMultisigInfo(userId)
                    if (syncResult.isSuccess) {
                        val report = syncResult.getOrNull()
                        
                        if (report != null) {
                            if (report.updatedCount > 0) {
                                Log.i(TAG, "✓ Synced ${report.updatedCount} member multisig info(s)")
                                loadRoscas()
                            }
                            
                            if (report.hasFailures) {
                                showSyncFailuresDialog(report.failures)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error syncing multisig info", e)
                }
            }          
        }
    }
    
    private fun showSyncFailuresDialog(failures: List<RoscaManager.SyncFailure>) {
        val message = buildString {
            append(getString(R.string.Dashboard_some_roscas_need_setup))
            append("\n\n")
            
            failures.forEach { failure ->
                append("• ${failure.roscaName}\n")
                append("  ${failure.reason}\n")
                append("  ${failure.actionNeeded}\n\n")
            }
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.Dashboard_setup_required))
            .setMessage(message)
            .setPositiveButton(getString(R.string.Dashboard_view_details)) { _, _ ->
                navigateToRoscaDetail(failures.first().roscaId)
            }
            .setNegativeButton(getString(R.string.Dashboard_later), null)
            .show()
    }    
    
    private fun showLoginPrompt() {
        binding.apply {
            swipeRefreshLayout.isRefreshing = false
            progressBar.visibility = View.GONE
            recyclerViewRoscas.visibility = View.GONE
            emptyStateLayout.visibility = View.GONE
            errorLayout.visibility = View.GONE
            fabCreate.visibility = View.GONE
            inviteCard.visibility = View.GONE
            loginLayout.visibility = View.VISIBLE
            
            btnGoogleSignIn.setOnClickListener {
                loginViewModel.startGoogleSignIn()
            }
        }
    }
    
    private fun hideLoginPrompt() {
        binding.loginLayout.visibility = View.GONE
        binding.inviteCard.visibility = View.VISIBLE
    }
    
    private fun loadRoscas() {
        hideLoginPrompt()
        
        lifecycleScope.launch {
            try {
                showLoading()
                
                val userId = getUserId()
                
                if (userId.isNullOrEmpty()) {
                    hideLoading()
                    showLoginPrompt()
                    return@launch
                }
                
                Log.d(TAG, "Loading ROSCAs for user: $userId")
                
                val roscas = withContext(Dispatchers.IO) {
                    val userMembers = roscaManager.repository.getAllMembers()
                        .filter { it.userId == userId || it.walletAddress == userId }
                    
                    val roscaIds = userMembers.map { it.roscaId }.distinct()
                    roscaIds.mapNotNull { roscaId ->
                        roscaManager.repository.getRoscaById(roscaId)
                    }
                }
                
                hideLoading()
                
                Log.d(TAG, "Loaded ${roscas.size} ROSCAs")
                
                if (roscas.isEmpty()) {
                    showEmptyState()
                } else {
                    showRoscasList(roscas)
                }
                
                // ✅ NEW: Check if creator needs to finalize any ROSCAs
                lifecycleScope.launch {
                    checkCreatorFinalizationTasks(roscas, userId)
                }
                
                lifecycleScope.launch {
                    checkPendingSetup(roscas)
                }                
                
            } catch (e: Exception) {
                Log.e(TAG, "Error loading ROSCAs", e)
                hideLoading()
                showError(getString(R.string.error_loading_roscas))
            }
        }
    }
    
    /**
     * ✅ FIXED: Check if creator needs to finalize any ROSCAs
     * Collects ALL members' multisig infos (including creator)
     */
    private suspend fun checkCreatorFinalizationTasks(roscas: List<Rosca>, userId: String) {
        try {
            Log.d(TAG, "=== CHECKING CREATOR FINALIZATION TASKS ===")
            Log.d(TAG, "User ID: $userId")
            
            // Filter ROSCAs where user is the creator
            val creatorRoscas = roscas.filter { it.creatorId == userId }
            
            Log.d(TAG, "Found ${creatorRoscas.size} ROSCAs created by this user")
            
            for (rosca in creatorRoscas) {
                // Only check ROSCAs in SETUP state that are full
                if (rosca.status != Rosca.RoscaState.SETUP) {
                    Log.d(TAG, "  ${rosca.name}: Already ${rosca.status}, skipping")
                    continue
                }
                
                if (rosca.currentMembers < rosca.totalMembers) {
                    Log.d(TAG, "  ${rosca.name}: Not full yet (${rosca.currentMembers}/${rosca.totalMembers})")
                    continue
                }
                
                Log.i(TAG, "  ${rosca.name}: Full and ready - checking if can finalize...")
                
                // Check if all members have multisig info
                val members = withContext(Dispatchers.IO) {
                    roscaManager.repository.getMembersByRoscaId(rosca.id)
                }
                
                val membersWithInfo = members.filter { member ->
                    val info = member.multisigInfo
                    info != null && !info.exchangeState.isNullOrEmpty()
                }
                
                Log.d(TAG, "    Members with multisig info: ${membersWithInfo.size}/${rosca.totalMembers}")
                
                if (membersWithInfo.size == rosca.totalMembers) {
                    Log.i(TAG, "    ✅ All members ready! Attempting finalization...")
                    
                    // ✅ FIXED: Collect ALL members' multisig infos (including creator)
                    // The WalletSuite will internally filter out the current user's info by address
                    val allMultisigInfos = membersWithInfo.mapNotNull { member ->
                        org.json.JSONObject().apply {
                            put("address", member.walletAddress)
                            put("exchangeState", member.multisigInfo?.exchangeState)
                            put("userId", member.userId)
                        }.toString()
                    }
                    
                    Log.d(TAG, "    Collected ${allMultisigInfos.size} member multisig infos (including creator)")
                    
                    if (allMultisigInfos.size < rosca.totalMembers) {
                        Log.w(TAG, "    ⚠️ Not enough multisig infos: ${allMultisigInfos.size}/${rosca.totalMembers} members")
                        continue
                    }
                    
                    Log.d(TAG, "    ✓ All ${rosca.totalMembers} members have multisig info, proceeding with finalization")
                    
                    // Attempt finalization
                    val result = withContext(Dispatchers.IO) {
                        roscaManager.finalizeSetup(
                            roscaId = rosca.id,
                            allMemberMultisigInfos = allMultisigInfos,  // Now correctly includes ALL members
                            userId = userId
                        )
                    }
                    
                    if (result.isSuccess) {
                        Log.i(TAG, "    🎉 Successfully finalized ${rosca.name}!")
                        
                        // Show success message to creator
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.Dashboard_rosca_finalized, rosca.name),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        
                        // Reload ROSCAs to show updated status
                        kotlinx.coroutines.delay(500)
                        loadRoscas()
                        
                    } else {
                        val error = result.exceptionOrNull()?.message ?: getString(R.string.RoscaDetail_unknown_error)
                        Log.e(TAG, "    ❌ Finalization failed: $error")
                    }
                } else {
                    Log.d(TAG, "    ⏳ Waiting for ${rosca.totalMembers - membersWithInfo.size} more member(s)")
                }
            }
            
            Log.d(TAG, "=== CREATOR FINALIZATION CHECK COMPLETE ===")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking creator finalization tasks", e)
        }
    }
    
    private suspend fun checkPendingSetup(roscas: List<Rosca>) {
        try {
            val userId = getUserId() ?: return
            
            val needsSetup = roscas.filter { rosca ->
                val member = withContext(Dispatchers.IO) {
                    roscaManager.repository.getMembersByRoscaId(rosca.id)
                        .find { it.userId == userId }
                }
                
                member != null && member.multisigInfo == null
            }
            
            if (needsSetup.isNotEmpty()) {
                val rosca = needsSetup.first()
                showSetupRequiredDialog(rosca)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking pending setup", e)
        }
    }

    private fun showSetupRequiredDialog(rosca: Rosca) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.Dashboard_complete_rosca_setup))
            .setMessage(
                getString(R.string.Dashboard_setup_message, rosca.name)
            )
            .setPositiveButton(getString(R.string.Dashboard_complete_setup)) { _, _ ->
                navigateToRoscaDetail(rosca.id)
            }
            .setNegativeButton(getString(R.string.Dashboard_later), null)
            .setCancelable(false)
            .show()
    }    
    
    private fun showLoading() {
        binding.apply {
            swipeRefreshLayout.isRefreshing = true
            recyclerViewRoscas.visibility = View.GONE
            emptyStateLayout.visibility = View.GONE
            errorLayout.visibility = View.GONE
            loginLayout.visibility = View.GONE
            
            if (!swipeRefreshLayout.isRefreshing) {
                progressBar.visibility = View.VISIBLE
            }
        }
    }
    
    private fun hideLoading() {
        binding.apply {
            swipeRefreshLayout.isRefreshing = false
            progressBar.visibility = View.GONE
        }
    }
    
    private fun showEmptyState() {
        binding.apply {
            recyclerViewRoscas.visibility = View.GONE
            errorLayout.visibility = View.GONE
            loginLayout.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
            fabCreate.visibility = View.VISIBLE
            
            tvActiveCount.text = getString(R.string.Dashboard_stats_total)
            tvTotalCount.text = getString(R.string.Dashboard_stats_total)
            tvCompletedCount.text = getString(R.string.Dashboard_stats_total)
        }
    }
        
    private fun showRoscasList(roscas: List<Rosca>) {
        binding.apply {
            recyclerViewRoscas.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
            errorLayout.visibility = View.GONE
            loginLayout.visibility = View.GONE
            fabCreate.visibility = View.VISIBLE
            
            adapter.submitList(roscas)
            updateSummaryStats(roscas)
        }
    }    
    
    private fun showError(message: String) {
        binding.apply {
            recyclerViewRoscas.visibility = View.GONE
            emptyStateLayout.visibility = View.GONE
            loginLayout.visibility = View.GONE
            errorLayout.visibility = View.VISIBLE
            fabCreate.visibility = View.GONE
            
            errorMessage.text = message
        }
        
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    
    private fun getUserId(): String? {
        return com.techducat.ajo.util.AuthStateManager.getCurrentUserId(requireContext())
    }
    
    private fun navigateToRoscaDetail(roscaId: String) {
        try {
            val bundle = Bundle().apply {
                putString("rosca_id", roscaId)
            }
            findNavController().navigate(
                R.id.action_dashboard_to_roscaDetail,
                bundle
            )
            Log.d(TAG, "Navigating to ROSCA detail: $roscaId")
        } catch (e: Exception) {
            Log.e(TAG, "Navigation error", e)
            try {
                val bundle = Bundle().apply {
                    putString("rosca_id", roscaId)
                }
                findNavController().navigate(
                    R.id.roscaDetailFragment,
                    bundle
                )
            } catch (fallbackError: Exception) {
                Log.e(TAG, "Fallback navigation also failed", fallbackError)
                Toast.makeText(
                    requireContext(), 
                    getString(R.string.error_failed_to_open_rosca_details, fallbackError.message), 
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun navigateToCreateRosca() {
        try {
            val intent = Intent(requireContext(), CreateRoscaActivity::class.java)
            createRoscaLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Navigation error", e)
            Toast.makeText(
                requireContext(), 
                getString(R.string.error_failed_to_open_exchange, e.message), 
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    private fun resetDashboardUI() {
        binding.apply {
            swipeRefreshLayout.isRefreshing = false
            progressBar.visibility = View.GONE
            recyclerViewRoscas.visibility = View.GONE
            emptyStateLayout.visibility = View.GONE
            errorLayout.visibility = View.GONE
            fabCreate.visibility = View.GONE
            inviteCard.visibility = View.GONE
            
            tvActiveCount.text = getString(R.string.Dashboard_stats_total)
            tvTotalCount.text = getString(R.string.Dashboard_stats_total)
            tvCompletedCount.text = getString(R.string.Dashboard_stats_total)
        }
    }   
    
    override fun onResume() {
        super.onResume()
        
        val userId = getUserId()
        
        if (userId.isNullOrEmpty()) {
            Log.d(TAG, "onResume: User not logged in")
            showLoginPrompt()
            adapter.submitList(emptyList())
            updateSummaryStats(emptyList())
        } else {
            Log.d(TAG, "onResume: User logged in, refreshing data")
            checkLoginAndLoadData()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
