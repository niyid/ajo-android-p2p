package com.techducat.ajo.ui.profile

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.techducat.ajo.BuildConfig
import com.techducat.ajo.R
import com.techducat.ajo.databinding.FragmentProfileBinding
import com.techducat.ajo.ui.auth.LoginViewModel
import com.techducat.ajo.util.Logger
import com.techducat.ajo.util.SecureStorage
import com.techducat.ajo.util.CurrencyFormatter
import com.techducat.ajo.wallet.WalletSuite
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale
import java.net.URL
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import java.text.SimpleDateFormat
import com.techducat.ajo.util.WalletSelectionManager

class ProfileFragment : Fragment() {
    
    companion object {
        private const val TAG = "com.techducat.ajo.ui.profile.ProfileFragment"
        private const val BIOMETRIC_ENROLLMENT_REQUEST_CODE = 1001
        private const val CHANNEL_ID = "profile_notifications"
        private const val NOTIFICATION_ID = 1001
    }
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private val secureStorage: SecureStorage by inject()
    private val walletSuite: WalletSuite by inject()
    private val loginViewModel: LoginViewModel by viewModel()
    
    private var currentLanguage: String = "en"
    private var biometricEnabled: Boolean = false
    
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        loginViewModel.handleSignInResult(result.data)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        lifecycleScope.launch {
            WalletSelectionManager.loadSelection(requireContext())
        }
        
        setupLoginObservers()
        checkLoginAndLoadProfile()
    }
    
    private fun setupLoginObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.signInIntent.collect { intent ->
                intent?.let { signInLauncher.launch(it) }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.uiState.collect { state ->
                updateLoginUI(state)
            }
        }
    }
    
    private fun updateLoginUI(state: com.techducat.ajo.ui.auth.LoginUiState) {
        _binding?.let { binding ->
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
                setupViews()
                initializeSecureStorage()
                loadProfileData()
            }
        }
    }
    
    private fun checkLoginAndLoadProfile() {
        val googleAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        
        if (googleAccount == null) {
            Logger.d("$TAG: User not logged in, showing login prompt")
            showLoginPrompt()
        } else {
            Logger.d("$TAG: User logged in with Google: ${googleAccount.email}")
            setupViews()
            initializeSecureStorage()
            loadProfileData()
        }
    }
    
    private fun showLoginPrompt() {
        _binding?.let { binding ->
            binding.scrollViewProfile.visibility = View.GONE
            binding.loginProgressBar.visibility = View.GONE
            binding.tvLoadingMessage.visibility = View.GONE
            binding.loginLayout.visibility = View.VISIBLE
            
            binding.btnGoogleSignIn.setOnClickListener {
                loginViewModel.startGoogleSignIn()
            }
        }
    }
    
    private fun hideLoginPrompt() {
        _binding?.let { binding ->
            binding.loginLayout.visibility = View.GONE
            binding.scrollViewProfile.visibility = View.VISIBLE
        }
    }
    
    private fun setupViews() {
        hideLoginPrompt()
        
        _binding?.let { binding ->
            binding.layoutChangeAvatar.setOnClickListener { handleChangeAvatar() }
            binding.layoutVerification.setOnClickListener { handleVerification() }
            binding.layoutDataPrivacy.setOnClickListener { handleDataPrivacy() }
            binding.layoutAnonymousSettings.setOnClickListener { handleAnonymousSettings() }
            binding.layoutSecurityPin.setOnClickListener { handleSecurityPin() }
            binding.layoutBiometric.setOnClickListener { handleBiometric() }
            binding.layoutNotifications.setOnClickListener { handleNotifications() }
            binding.layoutLanguage.setOnClickListener { handleLanguage() }
            binding.layoutBackupRestore.setOnClickListener { handleBackupRestore() }
            binding.layoutHelp.setOnClickListener { handleHelp() }
            binding.layoutAbout.setOnClickListener { handleAbout() }
            binding.btnLogout.setOnClickListener { handleLogout() }
        }
    }
    
    private fun initializeSecureStorage() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val currentApiKey = secureStorage.getChangeNowApiKey()
                if (currentApiKey.isNullOrEmpty()) {
                    val apiKey = BuildConfig.CHANGENOW_API_KEY
                    secureStorage.setChangeNowApiKey(apiKey)
                    Logger.i("$TAG: ChangeNow API key initialized")
                }
                Logger.i("$TAG: Secure storage initialized successfully")
            } catch (e: Exception) {
                Logger.e("$TAG: Error initializing secure storage", e)
                showError("Failed to initialize secure storage: ${e.message}")
            }
        }
    }
    
    private fun loadProfileData() {
        _binding?.let { binding ->
            binding.loginProgressBar.visibility = View.VISIBLE
            binding.tvLoadingMessage.visibility = View.VISIBLE
            binding.tvLoadingMessage.text = getString(R.string.Profile_loading_profile)
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val googleAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
                
                if (googleAccount != null) {
                    val email = googleAccount.email ?: "No email available"
                    _binding?.profileId?.text = email
                    Logger.d("$TAG: Displaying Google account: $email")
                    
                    val displayName = googleAccount.displayName
                    if (displayName != null && displayName.isNotEmpty()) {
                        _binding?.profileName?.text = displayName
                        _binding?.profileName?.visibility = View.VISIBLE
                        Logger.d("$TAG: Display name: $displayName")
                    } else {
                        _binding?.profileName?.visibility = View.GONE
                    }
                    
                    val photoUrl = googleAccount.photoUrl
                    if (photoUrl != null) {
                        loadProfilePhoto(photoUrl.toString())
                    } else {
                        _binding?.tvAvatar?.visibility = View.VISIBLE
                        _binding?.profileImage?.visibility = View.GONE
                    }
                    
                    val sharedPrefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    sharedPrefs.edit().apply {
                        putString("user_email", email)
                        putString("user_display_name", displayName)
                        putString("user_photo_url", photoUrl?.toString())
                        apply()
                    }
                } else {
                    Logger.w("$TAG: No Google account found")
                    _binding?.profileId?.text = getString(R.string.Profile_not_signed)
                    _binding?.profileName?.visibility = View.GONE
                    _binding?.tvAvatar?.visibility = View.VISIBLE
                    _binding?.profileImage?.visibility = View.GONE
                }
                
                loadWalletBalanceBasedOnSelection()
                
                val isVerified = secureStorage.isTokenValid()
                _binding?.tvVerificationStatus?.text = if (isVerified) getString(R.string.verified_status) else getString(R.string.not_verified_status)
                _binding?.tvVerificationStatus?.setTextColor(
                    if (isVerified) 
                        resources.getColor(android.R.color.holo_green_dark, null)
                    else 
                        resources.getColor(android.R.color.holo_orange_dark, null)
                )
                
                val memberSince = System.currentTimeMillis()
                _binding?.profileMemberSince?.text = getString(R.string.Profile_member_since_formatdate_membersince, formatDate(memberSince))
                
                loadRoscaStatistics()
                loadUserPreferences()
                
            } catch (e: Exception) {
                Logger.e("$TAG: Error loading profile data", e)
                showError("Failed to load profile: ${e.message}")
            } finally {
                _binding?.loginProgressBar?.visibility = View.GONE
                _binding?.tvLoadingMessage?.visibility = View.GONE
            }
        }
    }
    
    private fun loadWalletBalanceBasedOnSelection() {
        // In single-wallet paradigm, always show personal wallet balance
        // WalletSelectionManager just tracks UI context (Personal vs ROSCA view)
        loadPersonalWalletBalance()
    }

    private fun loadPersonalWalletBalance() {
        walletSuite.getBalance(object : WalletSuite.BalanceCallback {
            override fun onSuccess(balance: Long, unlocked: Long) {
                if (isAdded && view != null) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        _binding?.tvWalletBalance?.text = CurrencyFormatter.formatXMR(unlocked)
                        
                        // Note: In single-wallet paradigm, we always show personal wallet balance
                        // WalletSelectionManager just tracks UI context (Personal vs ROSCA view)
                        // The actual balance is always from the user's single wallet
                        
                        Logger.d("$TAG: Wallet balance loaded: ${CurrencyFormatter.formatXMR(unlocked)}")
                    }
                }
            }
            
            override fun onError(error: String) {
                Logger.e("$TAG: Error loading balance: $error")
                if (isAdded && view != null) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        _binding?.tvWalletBalance?.text = CurrencyFormatter.formatXMR(0L)
                    }
                }
            }
        })
    }

    private fun loadRoscaStatistics() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val prefs = requireContext().getSharedPreferences("rosca_stats", Context.MODE_PRIVATE)
                
                val totalRoscas = prefs.getInt("total_roscas", 0)
                val activeRoscas = prefs.getInt("active_roscas", 0)
                val completedRoscas = prefs.getInt("completed_roscas", 0)
                val totalContributions = prefs.getFloat("total_contributions", 0f)
                val successRate = prefs.getFloat("success_rate", 0f)
                
                _binding?.apply {
                    tvTotalRoscas.text = totalRoscas.toString()
                    tvActiveRoscas.text = activeRoscas.toString()
                    tvCompletedRoscas.text = completedRoscas.toString()
                    tvTotalContributions.text = CurrencyFormatter.formatUSD(totalContributions.toDouble())
                    tvSuccessRate.text = CurrencyFormatter.formatPercentage(successRate.toDouble() / 100.0)
                }
            } catch (e: Exception) {
                Logger.e("$TAG: Error loading ROSCA statistics", e)
                _binding?.apply {
                    tvTotalRoscas.text = "0"
                    tvActiveRoscas.text = "0"
                    tvCompletedRoscas.text = "0"
                    tvTotalContributions.text = CurrencyFormatter.formatUSD(0.0)
                    tvSuccessRate.text = CurrencyFormatter.formatPercentage(0.0)
                }
            }
        }
    }
    
    private fun loadUserPreferences() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                currentLanguage = prefs.getString("language", "en") ?: "en"
                biometricEnabled = prefs.getBoolean("biometric_enabled", false)
                
                val avatarType = prefs.getString("avatar_type", "google")
                val googleAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
                
                when (avatarType) {
                    "google" -> {
                        val photoUrl = googleAccount?.photoUrl
                        if (photoUrl != null) {
                            loadProfilePhoto(photoUrl.toString())
                        } else {
                            val savedAvatar = prefs.getString("avatar", "😀")
                            _binding?.tvAvatar?.text = savedAvatar
                            _binding?.tvAvatar?.visibility = View.VISIBLE
                            _binding?.profileImage?.visibility = View.GONE
                        }
                    }
                    "emoji" -> {
                        val savedAvatar = prefs.getString("avatar", "😀")
                        _binding?.tvAvatar?.text = savedAvatar
                        _binding?.tvAvatar?.visibility = View.VISIBLE
                        _binding?.profileImage?.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                Logger.e("$TAG: Error loading user preferences", e)
            }
        }
    }
    
    private fun saveUserPreferences() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putString("language", currentLanguage)
                    putBoolean("biometric_enabled", biometricEnabled)
                    apply()
                }
            } catch (e: Exception) {
                Logger.e("$TAG: Error saving user preferences", e)
            }
        }
    }
    
    private fun handleChangeAvatar() {
        val googleAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        val hasGooglePhoto = googleAccount?.photoUrl != null
        
        val options = if (hasGooglePhoto) {
            arrayOf("Use Google Photo", "Choose Emoji Avatar")
        } else {
            arrayOf("Choose Emoji Avatar")
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.change_avatar))
            .setItems(options) { dialog, which ->
                if (hasGooglePhoto && which == 0) {
                    googleAccount.photoUrl?.let { photoUrl ->
                        loadProfilePhoto(photoUrl.toString())
                        saveAvatarPreference("google")
                    }
                } else {
                    showAvatarEmojiPicker()
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .show()
    }
    
    private fun loadProfilePhoto(photoUrl: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    val url = URL(photoUrl)
                    val connection = url.openConnection()
                    connection.connect()
                    val inputStream = connection.getInputStream()
                    BitmapFactory.decodeStream(inputStream)
                }
                
                if (!isAdded || view == null) {
                    Logger.d("$TAG: Fragment not added, skipping photo update")
                    return@launch
                }
                
                if (bitmap != null) {
                    val circularBitmap = getCircularBitmap(bitmap)
                    _binding?.profileImage?.setImageBitmap(circularBitmap)
                    _binding?.profileImage?.visibility = View.VISIBLE
                    _binding?.tvAvatar?.visibility = View.GONE
                    Logger.d("$TAG: Profile photo loaded successfully")
                } else {
                    _binding?.tvAvatar?.visibility = View.VISIBLE
                    _binding?.profileImage?.visibility = View.GONE
                }
            } catch (e: Exception) {
                Logger.e("$TAG: Error loading profile photo", e)
                if (isAdded && view != null) {
                    _binding?.tvAvatar?.visibility = View.VISIBLE
                    _binding?.profileImage?.visibility = View.GONE
                }
            }
        }
    }
    
    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = Math.min(bitmap.width, bitmap.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, size, size)
        val rectF = RectF(rect)
        
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawOval(rectF, paint)
        
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        
        val left = ((bitmap.width - size) / 2).toFloat()
        val top = ((bitmap.height - size) / 2).toFloat()
        canvas.drawBitmap(bitmap, -left, -top, paint)
        
        return output
    }
    
    private fun showAvatarEmojiPicker() {
        val emojis = listOf(
            "😀", "😎", "🤓", "😇", "🤩", "🥳", 
            "😺", "🐶", "🐼", "🦊", "🐯", "🦁",
            "🌟", "💎", "🎯", "🎨", "🎭", "🎪"
        )
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, emojis)
        
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.choose_avatar))
            .setAdapter(adapter) { dialog, which ->
                val selectedEmoji = emojis[which]
                updateAvatar(selectedEmoji)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun updateAvatar(emoji: String) {
        _binding?.tvAvatar?.text = emoji
        _binding?.tvAvatar?.visibility = View.VISIBLE
        _binding?.profileImage?.visibility = View.GONE
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                prefs.edit().putString("avatar", emoji).apply()
                saveAvatarPreference("emoji")
                Toast.makeText(requireContext(), getString(R.string.avatar_updated), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Logger.e("$TAG: Error updating avatar", e)
                showError("Failed to update avatar")
            }
        }
    }
    
    private fun saveAvatarPreference(type: String) {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("avatar_type", type).apply()
    }
    
    private fun handleVerification() {
        Toast.makeText(
            requireContext(),
            "Your account is authenticated via Google Sign-In",
            Toast.LENGTH_SHORT
        ).show()
    }
    
    private fun showOpenSourceLicenses() {
        val licenses = """
            Open Source Libraries:
            
            • Monero - BSD 3-Clause License
            • Koin - Apache 2.0
            • Kotlin Coroutines - Apache 2.0
            • Material Components - Apache 2.0
            • Navigation Component - Apache 2.0
            
            See GitHub repository for full license texts.
        """.trimIndent()
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.Profile_open_source_licenses))
            .setMessage(licenses)
            .setPositiveButton("OK", null)
            .show()
    }    
    
    private fun handleDataPrivacy() {
        val message = """
            Data Privacy Information:
            • All data is stored locally on your device
            • Sensitive data is encrypted using Android Keystore
            • No personal information is shared without consent
            • Monero provides transaction privacy by default
        """.trimIndent()
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    
    private fun handleAnonymousSettings() {
        val message = """
            Anonymous Settings:
            • Your identity is protected by Monero's privacy features
            • No email or phone number required
            • User ID is derived from your wallet address
        """.trimIndent()
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    
    private fun navigateToPinSetupFragment() {
        try {
            findNavController().navigate(R.id.action_profile_to_pinSetup)
        } catch (e: Exception) {
            Logger.e("$TAG: Navigation error", e)
            Toast.makeText(requireContext(), getString(R.string.Profile_failed_open_pin_setup), Toast.LENGTH_SHORT).show()
        }
    }  
    
    private fun handleSecurityPin() {
        navigateToPinSetupFragment()
    }
    
    private fun setupBiometricAuth() {
        val biometricManager = BiometricManager.from(requireContext())
        
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> showBiometricPrompt()
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(requireContext(), getString(R.string.no_biometric_hardware), Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(requireContext(), getString(R.string.biometric_hardware_unavailable), Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BiometricManager.Authenticators.BIOMETRIC_STRONG)
                }
                startActivityForResult(enrollIntent, BIOMETRIC_ENROLLMENT_REQUEST_CODE)
            }
        }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    biometricEnabled = true
                    saveUserPreferences()
                    Toast.makeText(requireContext(), getString(R.string.biometric_enabled), Toast.LENGTH_SHORT).show()
                }
                
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(requireContext(), getString(R.string.Profile_authentication_error_errstring), Toast.LENGTH_SHORT).show()
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(requireContext(), getString(R.string.auth_failed), Toast.LENGTH_SHORT).show()
                }
            }
        )
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_prompt_title))
            .setSubtitle("Secure your account with biometrics")
            .setNegativeButtonText(getString(R.string.cancel))
            .build()
        
        biometricPrompt.authenticate(promptInfo)
    }
    
    private fun handleBiometric() {
        setupBiometricAuth()
    }
    
    private fun handleNotifications() {
        navigateToNotificationSettings()
    }

    private fun navigateToNotificationSettings() {
        try {
            findNavController().navigate(R.id.action_profile_to_notificationSettings)
        } catch (e: Exception) {
            Logger.e("$TAG: Navigation error", e)
            Toast.makeText(requireContext(), getString(R.string.Profile_failed_open_notification_settings), Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showNotificationSettings() {
        val message = """
            Notification Settings:
            • ROSCA invitations
            • Payment reminders
            • Payout notifications
            • Security alerts
            
            Manage notification preferences in your device settings.
        """.trimIndent()
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    
    private fun showLocalNotification(message: String) {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Profile Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        
        val notification = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setContentTitle("Profile Update")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("English", "Spanish", "French", "German", "Chinese", "Japanese")
        val languageCodes = arrayOf("en", "es", "fr", "de", "zh", "ja")
        
        val selectedIndex = languageCodes.indexOf(currentLanguage)
        
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.select_language))
            .setSingleChoiceItems(languages, selectedIndex) { dialog, which ->
                val selectedLanguageCode = languageCodes[which]
                currentLanguage = selectedLanguageCode
                saveUserPreferences()
                updateLocale(selectedLanguageCode)
                dialog.dismiss()
                requireActivity().recreate()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }    
    
    private fun handleLanguage() {
        showLanguageSelectionDialog()
    }
    
    private fun handleBackupRestore() {
        navigateToBackupRestore()
    }

    private fun navigateToBackupRestore() {
        try {
            findNavController().navigate(R.id.action_profile_to_backupRestore)
        } catch (e: Exception) {
            Logger.e("$TAG: Navigation error", e)
            Toast.makeText(requireContext(), getString(R.string.Profile_failed_open_backup_screen), Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun handleHelp() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.help))
            .setMessage(
                "App Version: ${BuildConfig.VERSION_NAME}\n\n" +
                "Need help?\n" +
                "• Check the FAQ in our documentation\n" +
                "• Contact support: support@ajo-app.com\n" +
                "• Report bugs on GitHub\n\n" +
                "Visit our website for guides and tutorials."
            )
            .setPositiveButton("OK", null)
            .setNeutralButton(getString(R.string.Profile_visit_website)) { _, _ ->
                Toast.makeText(requireContext(), getString(R.string.Profile_website_coming_soon), Toast.LENGTH_SHORT).show()
            }
            .show()
    }
    
    private fun handleAbout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.Profile_about_ajo))
            .setMessage(
                "Ajo - Decentralized ROSCA Platform\n\n" +
                "Version: ${BuildConfig.VERSION_NAME}\n" +
                "Build: ${BuildConfig.VERSION_CODE}\n\n" +
                "Built with:\n" +
                "• Monero for privacy-preserving transactions\n" +
                "• Android Keystore for secure storage\n" +
                "• Kotlin & Coroutines\n\n" +
                "© 2024 Ajo. All rights reserved.\n" +
                "Licensed under MIT License"
            )
            .setPositiveButton("OK", null)
            .setNeutralButton(getString(R.string.Profile_view_licenses)) { _, _ ->
                showOpenSourceLicenses()
            }
            .show()
    }
    
    private fun handleLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.Profile_are_you_sure_you))
            .setPositiveButton(getString(R.string.logout)) { _, _ ->  
                performLogout()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
    
    private fun clearAllPreferences() {
        val prefsToClean = listOf(
            "app_prefs",
            "user_prefs", 
            "wallet_prefs",
            "rosca_stats",
            "referral_prefs"
        )
        
        prefsToClean.forEach { prefsName ->
            try {
                requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                    .edit().clear().apply()
                Logger.d("$TAG: Cleared $prefsName")
            } catch (e: Exception) {
                Logger.e("$TAG: Error clearing $prefsName", e)
            }
        }
    }    
    
    private fun performLogout() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
                googleSignInClient.signOut().addOnCompleteListener {
                    Logger.d("$TAG: Google sign out successful")
                }
                
                clearAllPreferences()
                
                loginViewModel.clearError()
                
                try {
                    walletSuite.pauseWallet()
                } catch (e: Exception) {
                    Logger.e("$TAG: Error pausing wallet during logout", e)
                }
                
                com.techducat.ajo.util.AuthStateManager.onLogout(requireContext())
                
                WalletSelectionManager.clearSelection(requireContext())
                
                Toast.makeText(requireContext(), getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
                
                showLoginPrompt()
                
                requireActivity().finish()
                requireActivity().startActivity(requireActivity().intent)
                
            } catch (e: Exception) {
                Logger.e("$TAG: Error during logout", e)
                showError("Failed to logout: ${e.message}")
            }
        }
    }
    
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        return sdf.format(timestamp)
    }
    
    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
