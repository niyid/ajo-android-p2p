package com.techducat.ajo.ui.join

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.techducat.ajo.R
import com.techducat.ajo.databinding.ActivityJoinRoscaBinding
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.service.RoscaManager
import com.techducat.ajo.ui.auth.LoginViewModel
import com.techducat.ajo.wallet.WalletSuite
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay  // ← ADD THIS
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.techducat.ajo.model.Rosca
import com.techducat.ajo.model.Rosca.RoscaState

class JoinRoscaActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "com.techducat.ajo.ui.join.JoinRoscaActivity"
    }
    
    private lateinit var binding: ActivityJoinRoscaBinding
    private val walletSuite: WalletSuite by inject()
    private val roscaManager: RoscaManager by inject()
    private lateinit var database: AjoDatabase
    
    private var roscaId: String? = null
    private var setupInfo: String? = null
    private var inviteToken: String? = null
    private val loginViewModel: LoginViewModel by viewModel()
    private var isJoiningRosca = false

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        loginViewModel.handleSignInResult(result.data)
    }    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinRoscaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        database = AjoDatabase.getInstance(this)
        
        setupToolbar()
        parseIntentData()
        setupViews()
        
        setupLoginObservers()
        checkLoginAndInitialize()        
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isJoiningRosca) {
            // Prevent back button during join process
            Toast.makeText(
                this, 
                "Please wait while joining ROSCA...", 
                Toast.LENGTH_SHORT
            ).show()
        } else {
            super.onBackPressed()
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
            binding.loginProgressBar.visibility = View.VISIBLE
        } else {
            binding.loginProgressBar.visibility = View.GONE
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
        binding.layoutContent.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.loginLayout.visibility = View.VISIBLE
        
        binding.btnGoogleSignIn.setOnClickListener {
            loginViewModel.startGoogleSignIn()
        }
    }
    
    private fun hideLoginPrompt() {
        binding.loginLayout.visibility = View.GONE
        binding.layoutContent.visibility = View.VISIBLE
    }
    
    private fun getUserId(): String? {
        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("user_id", null)
    }
    
    private fun onLoginSuccess() {
        hideLoginPrompt()
        // Initialize activity content
        loadRoscaDetails()        
    }            
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Join ROSCA"
        }
    }
    
    private fun parseIntentData() {
        // From invite link: ajo://join/{roscaId}/{token}
        roscaId = intent.getStringExtra("rosca_id")
        setupInfo = intent.getStringExtra("setup_info")
        inviteToken = intent.getStringExtra("invite_token")
        
        // Handle deep link
        intent.data?.let { uri ->
            if (uri.scheme == "ajo" && uri.host == "join") {
                uri.pathSegments?.let { segments ->
                    if (segments.size >= 1) roscaId = segments[0]
                    if (segments.size >= 2) inviteToken = segments[1]
                }
            }
        }
    }
    
    private fun setupViews() {
        binding.buttonJoin.setOnClickListener { handleJoinClick() }
        binding.buttonCancel.setOnClickListener { finish() }
    }
    
    private fun loadRoscaDetails() {
        if (roscaId == null) {
            showError("Invalid ROSCA invitation")
            finish()
            return
        }
        
        showLoading("Loading ROSCA details...")
        
        lifecycleScope.launch {
            try {
                val rosca = database.roscaDao().getRoscaById(roscaId!!)
                
                if (rosca == null) {
                    showError("ROSCA not found")
                    finish()
                    return@launch
                }
                
                // Display ROSCA details
                binding.textViewRoscaName.text = rosca.name
                binding.textViewDescription.text = rosca.description
                binding.textViewMembers.text = getString(R.string.JoinRosca_rosca_totalmembers_members, rosca.totalMembers) // Fixed
                binding.textViewContribution.text = formatXMR(rosca.contributionAmount)
                binding.textViewFrequency.text = getString(R.string.JoinRosca_rosca_frequencydays_days, rosca.frequencyDays) // Fixed
                
                // Load members count (anonymous count only)
                val members = database.memberDao().getMembersByGroupSync(roscaId!!)
                val activeMembers = members.count { it.isActive }
                binding.textViewCurrentMembers.text = getString(R.string.JoinRosca_activemembers_joined, activeMembers) // Fixed
                
                // Check if user can join
                val canJoin = activeMembers < rosca.totalMembers
                binding.buttonJoin.isEnabled = canJoin
                
                if (!canJoin) {
                    binding.textViewWarning.text = getString(R.string.Invite_rosca_full)
                    binding.textViewWarning.visibility = View.VISIBLE
                }
                
                hideLoading()
                
            } catch (e: Exception) {
                hideLoading()
                showError("Failed to load ROSCA: ${e.message}")
            }
        }
    }
    
    private fun handleJoinClick() {
        showLoading("Preparing to join...")
        
        lifecycleScope.launch {
            try {
                // ✅ UPDATED: Using single personal wallet for all ROSCAs
                // joinRosca() will use the user's personal wallet address
                
                hideLoading() // Hide loading before showing dialog
                
                MaterialAlertDialogBuilder(this@JoinRoscaActivity)
                    .setTitle(getString(R.string.JoinRosca_confirm_join))
                    .setMessage(getString(R.string.JoinRosca_confirm_join_message))
                    .setPositiveButton(getString(R.string.Dashboard_join)) { _, _ ->
                        performJoin()
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            } catch (e: Exception) {
                hideLoading()
                showError("Error: ${e.message}")
            }
        }
    }

    private fun performJoin() {
        isJoiningRosca = true    
        showLoading("Joining ROSCA...")
        
        lifecycleScope.launch {
            try {
                val result = roscaManager.joinRosca(
                    roscaId = roscaId!!,
                    setupInfo = setupInfo ?: "",
                    context = this@JoinRoscaActivity
                )
                
                if (result.isSuccess) {
                    val member = result.getOrThrow()
                    
                    // ✅ Give finalization a moment to complete (it runs in background)
                    delay(1500)
                    
                    // ✅ Check current ROSCA status to see if finalization happened
                    val rosca = database.roscaDao().getRoscaById(roscaId!!)
                    
                    if (rosca == null) {
                        hideLoading()
                        showError("Error: ROSCA not found after joining")
                        return@launch
                    }
                    
                    val isFinalized = RoscaState.ACTIVE.equals(rosca.status) && rosca.multisigAddress != null
                    
                    hideLoading()
                    
                    if (isFinalized) {
                        // ✅ ROSCA finalized successfully - show success
                        Log.d(TAG, "ROSCA finalized! Status: ${rosca.status}, Address: ${rosca.multisigAddress?.take(20)}...")
                        showSuccessDialog(
                            message = "Successfully joined! ROSCA is now active and ready for contributions.",
                            isFinalized = true,
                            multisigAddress = rosca.multisigAddress ?: ""
                        )
                    } else {
                        // ✅ Still in setup - show waiting message
                        Log.d(TAG, "ROSCA still in setup. Members: ${rosca.currentMembers}/${rosca.totalMembers}")
                        val multisigInfo = member.multisigInfo?.exchangeState ?: 
                                          member.multisigInfo?.address ?: 
                                          "No multisig info"
                        showSuccessDialog(
                            message = "Successfully joined! Waiting for ${rosca.totalMembers - rosca.currentMembers} more member(s) to complete setup.",
                            isFinalized = false,
                            multisigAddress = multisigInfo
                        )
                    }
                    
                } else {
                    hideLoading()
                    showError("Failed to join: ${result.exceptionOrNull()?.message}")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error joining ROSCA", e)
                hideLoading()
                showError("Failed to join: ${e.message}")
            } finally {
                isJoiningRosca = false
            }
        }
    }

    private fun showSuccessDialog(
        message: String,
        isFinalized: Boolean,
        multisigAddress: String
    ) {
        val title = if (isFinalized) {
            "Setup Complete! 🎉"
        } else {
            getString(R.string.JoinRosca_joined_successfully)
        }
        
        val fullMessage = if (isFinalized) {
            "$message\n\nMultisig Address: ${formatAddress(multisigAddress)}\n\nYou can now view your ROSCA wallet and make contributions."
        } else {
            "$message\n\nYour multisig info has been stored. You'll be able to contribute once all members join."
        }
        
        val dialogBuilder = MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(fullMessage)
            .setCancelable(false)
        
        if (isFinalized) {
            // ✅ ROSCA is active - just go back to view it
            dialogBuilder
                .setPositiveButton("View ROSCA") { _, _ -> 
                    finish() // Returns to dashboard where they can see the active ROSCA
                }
        } else {
            // ✅ Still in setup - offer to copy/share multisig info (optional)
            dialogBuilder
                .setPositiveButton("Done") { _, _ -> 
                    finish() 
                }
                .setNeutralButton("Copy Info") { _, _ ->
                    copyToClipboard(multisigAddress)
                    showSuccess("Multisig info copied")
                    finish()
                }
        }
        
        dialogBuilder.show()
    }
    
    private fun formatAddress(address: String): String {
        return if (address.length > 20) {
            "${address.take(10)}...${address.takeLast(10)}"
        } else address
    }
    
    private fun formatXMR(atomic: Long): String {
        return try {
            val xmr = atomic / 1e12
            String.format("%.6f XMR", xmr)
        } catch (e: Exception) {
            "0 XMR"
        }
    }
    
    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Multisig Info", text)
        clipboard.setPrimaryClip(clip)
        showSuccess("Copied to clipboard")
    }
    
    private fun shareMultisigInfo(info: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "My ROSCA Multisig Info:\n\n$info")
            putExtra(Intent.EXTRA_SUBJECT, "ROSCA Multisig Information")
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }
    
    private fun showLoading(message: String = "Loading...") {
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.loadingProgressBar.visibility = View.VISIBLE
        binding.loadingText.text = message
        
        // Disable buttons
        binding.buttonJoin.isEnabled = false
        binding.buttonCancel.isEnabled = false
    }
    
    private fun hideLoading() {
        binding.loadingOverlay.visibility = View.GONE
        binding.loadingProgressBar.visibility = View.GONE
        
        // Re-enable buttons
        binding.buttonJoin.isEnabled = true
        binding.buttonCancel.isEnabled = true
    }
    
    private fun showSuccess(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(getColor(android.R.color.holo_green_dark))
            .show()
    }
    
    private fun showError(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(android.R.color.holo_red_dark))
            .show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
