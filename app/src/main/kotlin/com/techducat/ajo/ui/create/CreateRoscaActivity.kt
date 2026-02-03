package com.techducat.ajo.ui.create

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.techducat.ajo.R
import com.techducat.ajo.databinding.ActivityCreateRoscaBinding
import com.techducat.ajo.model.Rosca
import com.techducat.ajo.model.Rosca.DistributionMethod
import com.techducat.ajo.service.RoscaManager
import com.techducat.ajo.wallet.WalletSuite
import com.techducat.ajo.ui.auth.LoginViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CreateRoscaActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "com.techducat.ajo.ui.create.CreateRoscaActivity"
    }
    
    private lateinit var binding: ActivityCreateRoscaBinding
    private val roscaManager: RoscaManager by inject()
    private val loginViewModel: LoginViewModel by viewModel()
    private val walletSuite: WalletSuite by inject()
    private var isCreatingRosca = false

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        loginViewModel.handleSignInResult(result.data)
    }
        
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRoscaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up views first (including button listeners)
        setupViews()
        
        // Then set up observers and check login
        setupLoginObservers()
        checkLoginAndInitialize()
        
        // Check wallet initialization
        checkWalletInitialization()
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isCreatingRosca) {
            // Prevent back button during ROSCA creation
            Toast.makeText(
                this, 
                "Please wait while ROSCA is being created...", 
                Toast.LENGTH_SHORT
            ).show()
        } else {
            super.onBackPressed()
        }
    }    
    
    private fun setupViews() {
        // Set up toolbar navigation
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        // Set up create button
        binding.buttonCreate.setOnClickListener {
            Log.d(TAG, "Create ROSCA button clicked")
            createRosca()
        }
    }
    
    private fun createRosca() {
        // Get values from the correct XML IDs
        val name = binding.editTextName.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        val members = binding.editTextMembers.text.toString().toIntOrNull() ?: 0
        val contributionAmountXmr = binding.editTextAmount.text.toString().toDoubleOrNull() ?: 0.0
        val contributionAmount = (contributionAmountXmr * 1_000_000_000_000.0).toLong()
        val frequencyDays = binding.editTextFrequency.text.toString().toIntOrNull() ?: 7
        
        val distributionMethod = when (binding.radioGroupDistribution.checkedRadioButtonId) {
            R.id.radio_lottery -> DistributionMethod.LOTTERY
            R.id.radio_bidding -> DistributionMethod.BIDDING
            R.id.radio_predetermined -> DistributionMethod.PREDETERMINED
            else -> DistributionMethod.PREDETERMINED
        }

        // Validate inputs
        if (name.isBlank()) {
            Toast.makeText(this, getString(R.string.CreateRosca_please_enter_rosca_name), Toast.LENGTH_SHORT).show()
            return
        }
        
        if (members < 2) {
            Toast.makeText(this, getString(R.string.CreateRosca_rosca_must_have_least), Toast.LENGTH_SHORT).show()
            return
        }
        
        if (contributionAmount <= 0) {
            Toast.makeText(this, getString(R.string.CreateRosca_contribution_amount_must_greater), Toast.LENGTH_SHORT).show()
            return
        }
        
        if (frequencyDays <= 0) {
            Toast.makeText(this, getString(R.string.CreateRosca_frequency_must_greater_than), Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ FIX BUG #11: Check balance before creating ROSCA
        lifecycleScope.launch {
            try {
                showLoading(true)
                
                // Get personal wallet balance
                val (_, unlockedBalance) = suspendCoroutine<Pair<Long, Long>> { continuation ->
                    walletSuite.getBalance(object : WalletSuite.BalanceCallback {
                        override fun onSuccess(balance: Long, unlocked: Long) {
                            continuation.resume(Pair(balance, unlocked))
                        }
                        
                        override fun onError(error: String) {
                            continuation.resumeWithException(Exception(error))
                        }
                    })
                }
                
                if (unlockedBalance < contributionAmount) {
                    showLoading(false)
                    
                    // Show dialog asking if user wants to create anyway
                    val dialog = androidx.appcompat.app.AlertDialog.Builder(this@CreateRoscaActivity)
                        .setTitle("Insufficient Funds")
                        .setMessage(
                            "You need ${formatXMR(contributionAmount)} XMR to participate in this ROSCA, " +
                            "but you only have ${formatXMR(unlockedBalance)} XMR unlocked. " +
                            "\n\nWould you like to create the ROSCA anyway? " +
                            "You won't be able to contribute until you have sufficient funds."
                        )
                        .setPositiveButton("Create Anyway") { _, _ ->
                            proceedWithCreation(name, description, members, contributionAmount, frequencyDays, distributionMethod)
                        }
                        .setNegativeButton("Cancel", null)
                        .create()
                    
                    dialog.show()
                    return@launch
                }
                
                // User has sufficient funds, proceed
                showLoading(false)
                proceedWithCreation(name, description, members, contributionAmount, frequencyDays, distributionMethod)
                
            } catch (e: Exception) {
                showLoading(false)
                Log.e(TAG, "Error checking balance", e)
                Toast.makeText(
                    this@CreateRoscaActivity,
                    "Could not check balance: ${e.message}. Proceeding with creation...",
                    Toast.LENGTH_LONG
                ).show()
                
                // Proceed anyway if balance check fails
                proceedWithCreation(name, description, members, contributionAmount, frequencyDays, distributionMethod)
            }
        }
    }
    
    private fun formatXMR(atomicUnits: Long): String {
        return "%.12f".format(atomicUnits / 1_000_000_000_000.0)
    }
    
    private fun proceedWithCreation(
        name: String,
        description: String,
        members: Int,
        contributionAmount: Long,
        frequencyDays: Int,
        distributionMethod: DistributionMethod
    ) {
        lifecycleScope.launch {
            try {
                isCreatingRosca = true
                showLoading(true)
                
                val result = roscaManager.createRosca(
                    name = name,
                    description = description,
                    totalMembers = members,
                    contributionAmount = contributionAmount,
                    frequencyDays = frequencyDays,
                    distributionMethod = distributionMethod,
                    context = this@CreateRoscaActivity
                )

                if (result.isSuccess) {
                    val rosca = result.getOrNull()
                    Log.d(TAG, "ROSCA created successfully: ${rosca?.id}")
                    Toast.makeText(
                        this@CreateRoscaActivity, 
                        "ROSCA created successfully!", 
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    Log.e(TAG, "Failed to create ROSCA: $error")
                    Toast.makeText(
                        this@CreateRoscaActivity, 
                        "Error: $error", 
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating ROSCA", e)
                Toast.makeText(
                    this@CreateRoscaActivity, 
                    "Error: ${e.message}", 
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                isCreatingRosca = false            
                showLoading(false)
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        if (show) {
            // Disable button
            binding.buttonCreate.isEnabled = false
            binding.buttonCreate.text = getString(R.string.CreateRosca_creating)
            
            // Show loading overlay
            binding.loadingOverlay.visibility = View.VISIBLE
            binding.loadingProgressBar.visibility = View.VISIBLE
            binding.loadingText.text = "Creating ROSCA..."
            
            // Disable all input fields
            binding.editTextName.isEnabled = false
            binding.editTextDescription.isEnabled = false
            binding.editTextMembers.isEnabled = false
            binding.editTextAmount.isEnabled = false
            binding.editTextFrequency.isEnabled = false
            binding.radioGroupDistribution.isEnabled = false
            
            // Disable each radio button
            for (i in 0 until binding.radioGroupDistribution.childCount) {
                binding.radioGroupDistribution.getChildAt(i).isEnabled = false
            }
            
        } else {
            // Hide loading overlay
            binding.loadingOverlay.visibility = View.GONE
            binding.loadingProgressBar.visibility = View.GONE
            
            // Re-enable button
            binding.buttonCreate.isEnabled = true
            binding.buttonCreate.text = getString(R.string.button_create)
            
            // Re-enable all input fields
            binding.editTextName.isEnabled = true
            binding.editTextDescription.isEnabled = true
            binding.editTextMembers.isEnabled = true
            binding.editTextAmount.isEnabled = true
            binding.editTextFrequency.isEnabled = true
            binding.radioGroupDistribution.isEnabled = true
            
            // Re-enable each radio button
            for (i in 0 until binding.radioGroupDistribution.childCount) {
                binding.radioGroupDistribution.getChildAt(i).isEnabled = true
            }
        }
    }
    
    private fun checkWalletInitialization() {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "=== Wallet Diagnostic ===")
                Log.d(TAG, "WalletSuite instance: $walletSuite")
                Log.d(TAG, "Is initialized: ${walletSuite.isInitialized}")
                
                val userWallet = walletSuite.userWallet
                if (userWallet != null) {
                    Log.d(TAG, "User wallet: $userWallet")
                    val address = userWallet.address
                    if (address != null && address.isNotEmpty()) {
                        Log.d(TAG, "User wallet address: ${address.take(20)}...")
                    } else {
                        Log.w(TAG, "User wallet address is null or empty")
                    }
                } else {
                    Log.w(TAG, "User wallet is null")
                }
                Log.d(TAG, "========================")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to check wallet initialization", e)
            }
        }
    }
    
    private fun setupLoginObservers() {
        lifecycleScope.launch {
            loginViewModel.signInIntent.collect { intent ->
                intent?.let { 
                    Log.d(TAG, "Launching sign-in intent")
                    signInLauncher.launch(it) 
                }
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
            Log.d(TAG, "User signed in successfully")
            onLoginSuccess()
        }
    }

    private fun checkLoginAndInitialize() {
        val userId = getUserId()
        
        if (userId.isNullOrEmpty()) {
            Log.d(TAG, "User not logged in, showing login prompt")
            showLoginPrompt()
        } else {
            Log.d(TAG, "User logged in with ID: $userId, showing form")
            onLoginSuccess()
        }
    }
    
    private fun showLoginPrompt() {
        binding.scrollContent.visibility = View.GONE
        binding.loginLayout.visibility = View.VISIBLE
        
        binding.btnGoogleSignIn.setOnClickListener {
            Log.d(TAG, "Google sign-in button clicked")
            loginViewModel.startGoogleSignIn()
        }
    }
    
    private fun hideLoginPrompt() {
        binding.loginLayout.visibility = View.GONE
        binding.scrollContent.visibility = View.VISIBLE
    }

    private fun getUserId(): String? {
        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getString("user_id", null)
        
        if (userId != null) {
            Log.d(TAG, "Found user ID: $userId")
        } else {
            Log.w(TAG, "No user ID found in SharedPreferences")
        }
        
        return userId
    }
    
    private fun onLoginSuccess() {
        Log.d(TAG, "Login success, showing form content")
        hideLoginPrompt()
    }
    
    private fun showError(error: String) {
        Log.e(TAG, "Error: $error")
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }
}
