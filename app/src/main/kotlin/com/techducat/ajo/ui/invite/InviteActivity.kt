package com.techducat.ajo.ui.invite

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.techducat.ajo.R
import com.techducat.ajo.databinding.ActivityInviteBinding
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.InviteEntity
import com.techducat.ajo.ui.auth.LoginViewModel
import com.techducat.ajo.wallet.WalletSuite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.security.SecureRandom
import java.util.UUID

class InviteActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "com.techducat.ajo.ui.invite.InviteActivity"
        private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.techducat.ajo"
    } 
    
    private lateinit var binding: ActivityInviteBinding
    private lateinit var database: AjoDatabase
    private lateinit var walletSuite: WalletSuite
    private val loginViewModel: LoginViewModel by viewModel()
    
    private var roscaId: String? = null
    private var roscaName: String? = null
    
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        loginViewModel.handleSignInResult(result.data)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInviteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        database = AjoDatabase.getInstance(this)
        walletSuite = WalletSuite.getInstance(this)
        
        roscaId = intent.getStringExtra("rosca_id")
        
        setupToolbar()
        setupLoginObservers()
        checkLoginAndInitialize()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Invite Members"
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
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
        
        state.error?.let { error ->
            showError(error)
            loginViewModel.clearError()
        }
        
        if (state.isSignedIn) {
            if (roscaId == null) {
                showError("Invalid ROSCA")
                finish()
                return
            }
            setupViews()
            loadRoscaInfo()
        }
    }
    
    private fun checkLoginAndInitialize() {
        val userId = getUserId()
        
        if (userId.isNullOrEmpty()) {
            Log.d(TAG, "User not logged in, showing login prompt")
            showLoginPrompt()
        } else {
            Log.d(TAG, "User logged in, initializing invite")
            if (roscaId == null) {
                showError("Invalid ROSCA")
                finish()
                return
            }
            setupViews()
            loadRoscaInfo()
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
        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("user_id", null)
    }
    
    private fun setupViews() {
        hideLoginPrompt()
        
        // Generate link button
        binding.buttonGenerateLink.setOnClickListener { 
            generateInviteLink() 
        }
        
        // Copy link button
        binding.buttonCopyLink.setOnClickListener {
            val link = binding.textViewInviteLink.text.toString()
            if (link.isNotEmpty()) {
                copyToClipboard(link)
            }
        }
        
        // Share link button
        binding.buttonShareLink.setOnClickListener {
            val link = binding.textViewInviteLink.text.toString()
            if (link.isNotEmpty()) {
                shareInviteLink(link)
            }
        }
    }
    
    private fun loadRoscaInfo() {
        lifecycleScope.launch {
            try {
                val rosca = withContext(Dispatchers.IO) {
                    database.roscaDao().getRoscaById(roscaId!!)
                }
                
                if (rosca != null) {
                    roscaName = rosca.name
                    binding.textViewRoscaName.text = rosca.name
                    
                    val members = withContext(Dispatchers.IO) {
                        database.memberDao().getMembersByGroupSync(roscaId!!)
                    }
                    
                    val activeCount = members.count { it.isActive }
                    binding.textViewMemberCount.text = getString(R.string.Invite_activecount_rosca_totalmembers, R.string.Invite_activecount_rosca_totalmembers, rosca.totalMembers)
                    
                    val spotsLeft = rosca.totalMembers - activeCount
                    if (spotsLeft <= 0) {
                        binding.textViewWarning.text = getString(R.string.Invite_rosca_full)
                        binding.textViewWarning.visibility = View.VISIBLE
                        binding.buttonGenerateLink.isEnabled = false
                    } else {
                        binding.textViewWarning.text = getString(R.string.Invite_spotsleft_spots_available, R.string.Invite_spotsleft_spots_available)
                        binding.textViewWarning.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                showError("Failed to load ROSCA: ${e.message}")
            }
        }
    }
    
    /**
     * Generate a shareable invite link with QR code
     */
    private fun generateInviteLink() {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                
                // Generate unique referral code
                val referralCode = generateReferralCode()
                
                // Save invite to database
                val invite = InviteEntity(
                    id = UUID.randomUUID().toString(),
                    roscaId = roscaId!!,
                    inviterUserId = getUserId()!!,
                    inviteeEmail = "", // No specific email for shareable links
                    referralCode = referralCode,
                    status = InviteEntity.STATUS_PENDING,
                    createdAt = System.currentTimeMillis(),
                    expiresAt = System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000) // 30 days
                )
                
                withContext(Dispatchers.IO) {
                    database.inviteDao().insertInvite(invite)
                }
                
                // Create deep link: ajo://join?ref=ABC123&rosca=xyz
                // This will be caught by the app's intent filter
                val inviteLink = "ajo://join?ref=$referralCode&rosca=$roscaId"
                
                // For sharing outside the app, use universal link
                val shareableLink = "https://ajo.app/join?ref=$referralCode&rosca=$roscaId"
                
                // Generate QR code with the deep link
                val qrBitmap = generateQRCode(inviteLink)
                binding.imageViewQR.setImageBitmap(qrBitmap)
                binding.textViewInviteLink.text = inviteLink
                binding.textViewShareableLink.text = shareableLink
                
                // Show the link section
                binding.layoutInviteLink.visibility = View.VISIBLE
                
                binding.progressBar.visibility = View.GONE
                
                showSuccess("Invite link generated! Code: $referralCode")
                
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                showError("Failed to generate link: ${e.message}")
                Log.e(TAG, "Error generating link", e)
            }
        }
    }
    
    /**
     * Generate a unique referral code
     */
    private fun generateReferralCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // No confusing characters
        val random = SecureRandom()
        return (1..8).map { chars[random.nextInt(chars.length)] }.joinToString("")
    }
    
    /**
     * Generate QR code bitmap
     */
    private fun generateQRCode(text: String): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) 
                    android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        
        return bitmap
    }
    
    /**
     * Copy text to clipboard
     */
    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Invite Link", text)
        clipboard.setPrimaryClip(clip)
        showSuccess("Link copied to clipboard!")
    }
    
    /**
     * Share invite link via any app
     */
    private fun shareInviteLink(link: String) {
        val inviterName = getUserDisplayName()
        
        // Use the shareable https link for sharing
        val shareableLink = binding.textViewShareableLink.text.toString()
        
        // Extract just the code from the link
        val code = link.substringAfter("ref=").substringBefore("&")
        
        val message = """
            🎉 Join me on Ajo!
            
            I'm inviting you to join my savings group "$roscaName" on Ajo - a secure, private group savings app.
            
            🔑 Invite Code: $code
            
            📥 Download the app:
            $PLAY_STORE_URL
            
            Or use this link to join directly:
            $shareableLink
            
            Ajo uses Monero for complete privacy and security.
        """.trimIndent()
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_SUBJECT, "Join my Ajo savings group")
        }
        startActivity(Intent.createChooser(intent, "Share Invite"))
    }
    
    private fun getUserDisplayName(): String {
        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("user_name", "Someone") ?: "Someone"
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
