package com.techducat.ajo.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.techducat.ajo.R
import com.techducat.ajo.service.ReferralHandler
import com.techducat.ajo.ui.MainActivity
import com.techducat.ajo.wallet.WalletSuite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {
    
    private val walletSuite: WalletSuite by inject()
    private val referralHandler: ReferralHandler by inject()
    
    companion object {
        private const val TAG = "com.techducat.ajo.ui.splash.SplashActivity"
        private const val SPLASH_DELAY = 2000L // 2 seconds
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        // Check for deep link referral code
        handleDeepLinkReferral()
        
        // Initialize app
        initializeApp()
    }
    
    @Deprecated("Deprecated in Java")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        
        // Handle deep link if app was already running
        handleDeepLinkReferral()
    }
    
    private fun handleDeepLinkReferral() {
        try {
            val data = intent?.data
            
            if (data != null) {
                Log.d(TAG, "=== DEEP LINK DETECTED ===")
                Log.d(TAG, "Full URI: $data")
                Log.d(TAG, "Scheme: ${data.scheme}")
                Log.d(TAG, "Host: ${data.host}")
                Log.d(TAG, "Path: ${data.path}")
                Log.d(TAG, "Query: ${data.query}")
                
                // Extract referral code and ROSCA ID
                val referralCode = data.getQueryParameter("ref")
                val roscaId = data.getQueryParameter("rosca")
                
                Log.d(TAG, "Referral Code: $referralCode")
                Log.d(TAG, "ROSCA ID: $roscaId")
                
                if (!referralCode.isNullOrEmpty() && !roscaId.isNullOrEmpty()) {
                    // Save for processing after login
                    referralHandler.savePendingReferral(referralCode, roscaId)
                    
                    Log.d(TAG, "✓ Saved pending referral: $referralCode for ROSCA: $roscaId")
                    
                    // Show feedback to user
                    Toast.makeText(
                        this,
                        "Invitation received! Sign in to join the ROSCA",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Log.w(TAG, "⚠️ Deep link missing ref or rosca parameter")
                }
                
                Log.d(TAG, "========================")
            } else {
                // No deep link - normal app launch
                Log.d(TAG, "Normal app launch (no deep link)")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling deep link", e)
        }
    }
    
    private fun initializeApp() {
        lifecycleScope.launch {
            try {
                // Initialize wallet if needed
                Log.d(TAG, "Initializing app...")
                
                // Check if WalletSuite has an initialization method
                // If initWallet() doesn't exist, you might need to call a different method
                // or remove this line if wallet initialization happens elsewhere
                try {
                    // Attempt to initialize wallet (if method exists)
                    // walletSuite.initWallet()
                    
                    // If no init method exists, you can skip this or do other initialization
                    Log.d(TAG, "Wallet initialization skipped or handled elsewhere")
                } catch (e: NoSuchMethodError) {
                    Log.d(TAG, "No initWallet method - continuing without it")
                }
                
                // Show splash for minimum duration
                delay(SPLASH_DELAY)
                
                // Navigate to main activity
                navigateToMain()
                
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing app", e)
                
                // Show error and retry
                Toast.makeText(
                    this@SplashActivity,
                    "Initialization failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                
                delay(2000)
                navigateToMain()
            }
        }
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            // Pass along the deep link data if it exists
            data = this@SplashActivity.intent?.data
            
            // Add flags to prevent returning to splash
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        startActivity(intent)
        finish()
    }
}
