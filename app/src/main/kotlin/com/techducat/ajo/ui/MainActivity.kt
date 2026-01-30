package com.techducat.ajo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.techducat.ajo.R
import com.techducat.ajo.databinding.ActivityMainBinding
import com.techducat.ajo.service.ReferralHandler
import org.koin.android.ext.android.inject
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.techducat.ajo.service.RoscaManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val referralHandler: ReferralHandler by inject()
    private val roscaManager: RoscaManager by inject()
    
    companion object {
        private const val TAG = "com.techducat.ajo.ui.MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
        
        // Handle deep link on first launch
        intent?.let { referralHandler.handleDeepLink(it) }
        
        // ✨ ADD THIS: Handle navigation from login with referral
        intent?.getStringExtra("navigate_to_rosca")?.let { roscaId ->
            navigateToRoscaDetail(roscaId)
        }
        
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val userId = prefs.getString("user_id", null)
        if (userId != null) {
            lifecycleScope.launch {
                try {
                    roscaManager.syncMemberMultisigInfo(userId)
                } catch (e: Exception) {
                    Log.e(TAG, "Error syncing on startup", e)
                }
            }
        }        
        
        checkPendingReferral()
    }

    // ✨ ADD THIS METHOD:
    private fun navigateToRoscaDetail(roscaId: String) {
        val bundle = Bundle().apply {
            putString("rosca_id", roscaId)
        }
        // Assuming you have a navigation action or destination ID
        navController.navigate(R.id.roscaDetailFragment, bundle)
    }
    
    @Deprecated("Deprecated in Java")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        
        // Handle deep link if app was already running
        referralHandler.handleDeepLink(intent)
        checkPendingReferral()
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        // Setup bottom navigation with NavController
        binding.bottomNavigation.setupWithNavController(navController)
    }
    
    /**
     * Check if there's a pending referral waiting to be processed
     */
    private fun checkPendingReferral() {
        if (referralHandler.hasPendingReferral()) {
            val (referralCode, roscaId) = referralHandler.getPendingReferralInfo() ?: return
            
            Log.d(TAG, "Pending referral detected: Code=$referralCode, ROSCA=$roscaId")
            
            // Check if user is logged in
            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val userId = prefs.getString("user_id", null)
            
            if (userId.isNullOrEmpty()) {
                // Not logged in - show message to login
                showReferralWaitingDialog()
            } else {
                // Already logged in - this shouldn't happen as referral should be processed on login
                // But just in case, show a message
                Log.w(TAG, "User already logged in but referral still pending - may need manual processing")
            }
        }
    }
    
    /**
     * Show dialog informing user they have a pending invitation
     */
    private fun showReferralWaitingDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.Main_invitation_received))
            .setMessage(getString(R.string.Main_you_been_invited_join))
            .setPositiveButton(getString(R.string.Main_sign)) { dialog, _ ->
                // Navigate to a fragment that has login (like Dashboard or Wallet)
                navController.navigate(R.id.dashboardFragment)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.Main_later)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
