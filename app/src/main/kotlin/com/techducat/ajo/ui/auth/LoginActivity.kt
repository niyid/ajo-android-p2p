package com.techducat.ajo.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.techducat.ajo.databinding.ActivityLoginBinding
import com.techducat.ajo.service.ReferralResult
import com.techducat.ajo.ui.MainActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

import com.techducat.ajo.R

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModel()
    
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleSignInResult(result.data)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        observeViewModel()
        checkExistingSignIn()
    }
    
    private fun setupUI() {
        binding.btnGoogleSignIn.setOnClickListener {
            viewModel.startGoogleSignIn()
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.signInIntent.collect { intent ->
                intent?.let { signInLauncher.launch(it) }
            }
        }
        
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUI(state)
            }
        }
        
        // ✨ NEW: Observe referral results
        lifecycleScope.launch {
            viewModel.referralResult.collect { result ->
                handleReferralResult(result)
            }
        }
    }
    
    private fun updateUI(state: LoginUiState) {
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        binding.btnGoogleSignIn.isEnabled = !state.isLoading
        
        state.error?.let { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
        
        if (state.isSignedIn) {
            // Don't navigate immediately - wait for referral processing
            // navigateToMain() will be called after referral handling
        }
    }
    
    private fun handleReferralResult(result: ReferralResult) {
        when (result) {
            is ReferralResult.Success -> {
                // Show success dialog and navigate to the ROSCA
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.Login_welcome))
                    .setMessage(getString(R.string.Login_you_successfully_joined_result, result.roscaId))
                    .setPositiveButton(getString(R.string.Login_view_group)) { _, _ ->
                        navigateToRoscaDetail(result.roscaId)
                    }
                    .setCancelable(false)
                    .show()
            }
            
            is ReferralResult.AlreadyMember -> {
                Toast.makeText(
                    this, 
                    "Welcome back! You're already a member of ${result.roscaName}", 
                    Toast.LENGTH_LONG
                ).show()
                navigateToMain()
            }
            
            is ReferralResult.Expired -> {
                Toast.makeText(
                    this, 
                    "The invite link has expired. Please request a new invitation.", 
                    Toast.LENGTH_LONG
                ).show()
                navigateToMain()
            }
            
            is ReferralResult.RoscaFull -> {
                Toast.makeText(
                    this, 
                    "This group is already full and cannot accept new members.", 
                    Toast.LENGTH_LONG
                ).show()
                navigateToMain()
            }
            
            is ReferralResult.InvalidCode -> {
                Toast.makeText(
                    this, 
                    "Invalid invite code. Please check the link and try again.", 
                    Toast.LENGTH_LONG
                ).show()
                navigateToMain()
            }
            
            is ReferralResult.EmailMismatch -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.Login_email_mismatch))
                    .setMessage(getString(R.string.Login_this_invite_was_sent, result.expectedEmail))
                    .setPositiveButton("OK") { _, _ ->
                        navigateToMain()
                    }
                    .show()
            }
            
            is ReferralResult.Error -> {
                Toast.makeText(
                    this, 
                    "Failed to process invitation: ${result.message}", 
                    Toast.LENGTH_LONG
                ).show()
                navigateToMain()
            }
            
            is ReferralResult.RoscaNotFound -> {
                Toast.makeText(
                    this, 
                    "The ROSCA group no longer exists.", 
                    Toast.LENGTH_LONG
                ).show()
                navigateToMain()
            }
            
            is ReferralResult.AlreadyUsed -> {
                Toast.makeText(
                    this, 
                    "This invite has already been used.", 
                    Toast.LENGTH_LONG
                ).show()
                navigateToMain()
            }
            
            ReferralResult.NoReferral,
            ReferralResult.AlreadyProcessed -> {
                // No referral to process - normal login flow
                navigateToMain()
            }
        }
    }
    
    private fun checkExistingSignIn() {
        viewModel.checkExistingSignIn()
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun navigateToRoscaDetail(roscaId: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to_rosca", roscaId)
        }
        startActivity(intent)
        finish()
    }
}
