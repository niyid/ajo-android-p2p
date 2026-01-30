package com.techducat.ajo.ui.exit

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.techducat.ajo.databinding.ActivityExitConfirmationBinding
import com.techducat.ajo.ui.auth.LoginViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

import com.techducat.ajo.R

class ExitConfirmationActivity : AppCompatActivity() {
    
    companion object {
        const val EXTRA_MEMBER_ID = "member_id"
        const val EXTRA_ROSCA_ID = "rosca_id"
        private const val TAG = "com.techducat.ajo.ui.exit.ExitConfirmationActivity"
    }
    
    private lateinit var binding: ActivityExitConfirmationBinding
    private val viewModel: ExitConfirmationViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()
    
    private var memberId: String? = null
    private var roscaId: String? = null

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        loginViewModel.handleSignInResult(result.data)
    }
        
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExitConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        memberId = intent.getStringExtra(EXTRA_MEMBER_ID)
        roscaId = intent.getStringExtra(EXTRA_ROSCA_ID)
        
        setupLoginObservers()
        checkLoginAndInitialize()
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
        binding.contentContainer.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.loginLayout.visibility = View.VISIBLE
        
        binding.btnGoogleSignIn.setOnClickListener {
            loginViewModel.startGoogleSignIn()
        }
    }
    
    private fun hideLoginPrompt() {
        binding.loginLayout.visibility = View.GONE
        binding.contentContainer.visibility = View.VISIBLE
    }
    
    private fun getUserId(): String? {
        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("user_id", null)
    }
    
    private fun onLoginSuccess() {
        hideLoginPrompt()
        
        // Validate data
        if (memberId == null || roscaId == null) {
            showError("Invalid data")
            finish()
            return
        }
        
        setupToolbar()
        setupViews()
        observeViewModel()
        
        // Calculate penalty
        viewModel.calculatePenalty(memberId!!, roscaId!!)
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Exit ROSCA"
        }
    }
    
    private fun setupViews() {
        binding.btnCancel.setOnClickListener {
            finish()
        }
        
        binding.btnConfirm.setOnClickListener {
            val reason = binding.etExitReason.text.toString().trim()
            if (reason.isBlank()) {
                showError("Please provide a reason for leaving")
                return@setOnClickListener
            }
            
            viewModel.confirmExit(memberId!!, roscaId!!, reason)
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ExitConfirmationUiState.Loading -> {
                        showLoading()
                    }
                    
                    is ExitConfirmationUiState.Calculated -> {
                        hideLoading()
                        showCalculatedState(state)
                    }
                    
                    is ExitConfirmationUiState.Processing -> {
                        showProcessing()
                    }
                    
                    is ExitConfirmationUiState.Success -> {
                        hideProcessing()
                        showSuccess("Successfully exited ROSCA")
                        setResult(RESULT_OK)
                        finish()
                    }
                    
                    is ExitConfirmationUiState.Error -> {
                        hideProcessing()
                        hideLoading()
                        showErrorState(state.message)
                    }
                }
            }
        }
    }
    
    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.calculatedContainer.visibility = View.GONE
        binding.processingContainer.visibility = View.GONE
        binding.errorContainer.visibility = View.GONE
    }
    
    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }
    
    private fun showCalculatedState(state: ExitConfirmationUiState.Calculated) {
        binding.calculatedContainer.visibility = View.VISIBLE
        binding.processingContainer.visibility = View.GONE
        binding.errorContainer.visibility = View.GONE
        
        binding.tvPenalty.text = getString(R.string.ExitConfirmation_penalty_formatxmr_state_penalty, R.string.ExitConfirmation_penalty_formatxmr_state_penalty)
        binding.tvReimbursement.text = getString(R.string.ExitConfirmation_you_will_receive_formatxmr, binding.tvReimbursement.text)
        
        binding.btnConfirm.isEnabled = true
    }
    
    private fun showProcessing() {
        binding.calculatedContainer.visibility = View.GONE
        binding.processingContainer.visibility = View.VISIBLE
        binding.errorContainer.visibility = View.GONE
    }
    
    private fun hideProcessing() {
        binding.processingContainer.visibility = View.GONE
    }
    
    private fun showErrorState(message: String) {
        binding.calculatedContainer.visibility = View.GONE
        binding.processingContainer.visibility = View.GONE
        binding.errorContainer.visibility = View.VISIBLE
        
        binding.tvErrorMessage.text = message
        
        binding.btnClose.setOnClickListener {
            finish()
        }
    }
    
    private fun formatXMR(amount: Long): String {
        return String.format("%.4f XMR", amount / 1_000_000.0)
    }
    
    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(getColor(android.R.color.holo_green_dark))
            .show()
    }
    
    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(android.R.color.holo_red_dark))
            .show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
