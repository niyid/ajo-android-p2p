package com.techducat.ajo.ui.wallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.techducat.ajo.R
import com.techducat.ajo.databinding.FragmentWalletMultiBinding
import com.techducat.ajo.ui.auth.LoginViewModel
import com.techducat.ajo.ui.exchange.ExchangeActivity
import com.techducat.ajo.util.CurrencyFormatter
import com.techducat.ajo.util.WalletHistoryLogger
import com.techducat.ajo.util.WalletCreationRecord
import com.techducat.ajo.wallet.WalletSuite
import com.techducat.ajo.repository.RoscaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.android.ext.android.inject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import com.m2049r.xmrwallet.model.TransactionInfo
import java.security.SecureRandom
import com.techducat.ajo.util.WalletSelectionManager

class WalletFragment : Fragment() {
    private var _binding: FragmentWalletMultiBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var walletSuite: WalletSuite
    private val repository: RoscaRepository by inject()
    
    private var isRefreshing = false
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    
    private var currentExchangeRate = 150.0
    private var lastExchangeRateUpdate = 0L
    private val EXCHANGE_RATE_CACHE_DURATION = 60_000L
    
    private val COINGECKO_API_URL = "https://api.coingecko.com/api/v3/simple/price?ids=monero&vs_currencies=usd"
    
    private var isWalletInitialized = false
    private var currentWalletMode = WalletMode.PERSONAL
    private var selectedRoscaId: String? = null
    private var isInitializationInProgress = false
    
    private lateinit var roscaWalletsAdapter: RoscaWalletsAdapter
    
    private val loginViewModel: LoginViewModel by viewModel()
    
    private var isUpdatingWalletSelection = false

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        loginViewModel.handleSignInResult(result.data)
    }
    
    enum class WalletMode {
        PERSONAL,  // User's personal wallet
        ROSCA      // Selected ROSCA multisig wallet
    }
    
    companion object {
        private const val TAG = "com.techducat.ajo.ui.wallet.WalletFragment"
        private const val PREFS_WALLET_CREDENTIALS = "wallet_credentials"
        private const val KEY_CONFIG_GENERATED = "config_generated"
        private const val WALLET_NAME_LENGTH = 32
        private const val WALLET_PASSWORD_LENGTH = 64
        
        private val configLock = Any()
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletMultiBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupTabs()
        setupClickListeners()
        setupSwipeRefresh()
        setupLoginObservers()
        setupRoscaWalletsList()
        
        WalletSelectionManager.selectPersonalWallet()
        
        lifecycleScope.launch {
            WalletSelectionManager.loadSelection(requireContext())
        }
        
        // Observe wallet selection changes
        WalletSelectionManager.selectedWallet.observe(viewLifecycleOwner) { selectedWallet ->
            if (isUpdatingWalletSelection) {
                Log.d(TAG, "⏭️ Skipping observer update - already updating")
                return@observe
            }
            
            // Set flag BEFORE calling update to prevent circular loops
            isUpdatingWalletSelection = true
            try {
                updateUIForSelectedWallet(selectedWallet)
            } finally {
                // Reset flag after UI updates complete
                binding.root.post {
                    isUpdatingWalletSelection = false
                }
            }
        }
        
        // Initialize wallet and check login
        checkLoginAndLoadData()
    }
    
    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Personal Wallet"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("ROSCA Wallets"))
        
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Prevent circular updates from LiveData observer
                if (isUpdatingWalletSelection) {
                    Log.d(TAG, "⏭️ Skipping tab selection - update in progress")
                    return
                }
                
                Log.d(TAG, "👆 User selected tab: ${tab?.position}")
                
                // Set flag to prevent LiveData observer from triggering
                isUpdatingWalletSelection = true
                
                showLoading()
                
                when (tab?.position) {
                    0 -> {  // Personal Wallet tab
                        lifecycleScope.launch {
                            try {
                                Log.d(TAG, "User clicked Personal Wallet tab")
                                
                                // 1. Update UI first
                                switchToPersonalWallet()
                                
                                // 2. Clear ROSCA detail when switching to personal tab
                                binding.layoutRoscaWalletDetail.visibility = View.GONE
                                selectedRoscaId = null
                                
                                // 3. Update WalletSelectionManager (triggers LiveData but flag prevents loop)
                                WalletSelectionManager.selectPersonalWallet()
                                WalletSelectionManager.saveSelection(requireContext())
                                
                            } finally {
                                hideLoading()
                                // Reset flag after UI settles
                                binding.root.postDelayed({
                                    isUpdatingWalletSelection = false
                                }, 100)
                            }
                        }
                    }
                    
                    1 -> {  // ROSCA Wallets tab
                        lifecycleScope.launch {
                            try {
                                Log.d(TAG, "User clicked ROSCA Wallets tab")
                                
                                // 1. Update UI to show ROSCA list
                                switchToRoscaWallets()
                                
                                // 2. ⚠️ CRITICAL: Hide detail view - no ROSCA selected yet!
                                binding.layoutRoscaWalletDetail.visibility = View.GONE
                                selectedRoscaId = null
                                
                                // 3. Check if there's a pre-selected ROSCA from dashboard
                                val currentSelection = WalletSelectionManager.getCurrentWallet()
                                if (currentSelection?.type != WalletSelectionManager.WalletType.ROSCA) {
                                    // No ROSCA pre-selected - clear selection
                                    // Don't call clearSelection() to avoid triggering observer
                                    // Just let user browse the list
                                }
                                // If there IS a ROSCA pre-selected, keep it but don't show detail
                                // User needs to tap it from the list to see detail
                                
                            } finally {
                                hideLoading()
                                binding.root.postDelayed({
                                    isUpdatingWalletSelection = false
                                }, 100)
                            }
                        }
                    }
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    
    private fun setupRoscaWalletsList() {
        roscaWalletsAdapter = RoscaWalletsAdapter { roscaId ->
            selectRoscaWallet(roscaId)
        }
        
        binding.recyclerViewRoscaWallets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = roscaWalletsAdapter
        }
    }
    
    private fun updateUIForSelectedWallet(selectedWallet: WalletSelectionManager.SelectedWallet?) {
        Log.d(TAG, "📱 updateUIForSelectedWallet called with: $selectedWallet")
        
        if (selectedWallet == null) {
            // No selection - default to personal wallet
            Log.d(TAG, "No wallet selected - showing personal wallet")
            currentWalletMode = WalletMode.PERSONAL
            selectedRoscaId = null
            
            // Show ONLY personal wallet
            binding.layoutPersonalWallet.visibility = View.VISIBLE
            binding.layoutRoscaWallets.visibility = View.GONE
            binding.layoutRoscaWalletDetail.visibility = View.GONE
            
            // Sync tab to match current mode
            syncTabWithMode()
            return
        }
        
        when (selectedWallet.type) {
            WalletSelectionManager.WalletType.PERSONAL -> {
                Log.d(TAG, "Showing personal wallet (from LiveData)")
                currentWalletMode = WalletMode.PERSONAL
                selectedRoscaId = null
                
                // Show ONLY personal wallet
                binding.layoutPersonalWallet.visibility = View.VISIBLE
                binding.layoutRoscaWallets.visibility = View.GONE
                binding.layoutRoscaWalletDetail.visibility = View.GONE
                
                if (isWalletInitialized) {
                    loadPersonalWalletData()
                }
            }
            
            WalletSelectionManager.WalletType.ROSCA -> {
                // User has a ROSCA selected (from dashboard or previous selection)
                selectedWallet.roscaId?.let { roscaId ->
                    Log.d(TAG, "Showing ROSCA detail for: $roscaId (from LiveData)")
                    currentWalletMode = WalletMode.ROSCA
                    selectedRoscaId = roscaId
                    
                    // Hide personal wallet
                    binding.layoutPersonalWallet.visibility = View.GONE
                    
                    // Show ROSCA list
                    binding.layoutRoscaWallets.visibility = View.VISIBLE
                    
                    // ✅ Show ROSCA detail ONLY because a specific ROSCA is selected
                    binding.layoutRoscaWalletDetail.visibility = View.VISIBLE
                    
                    // Load the specific ROSCA's data
                    selectRoscaWalletInList(roscaId)
                }
            }
        }
        
        // Sync tab to match current mode (uses flag to prevent listener trigger)
        syncTabWithMode()
    }
    
    private fun syncTabWithMode() {
        // Only sync if not currently updating (prevents triggering listener)
        if (isUpdatingWalletSelection) {
            Log.d(TAG, "⏭️ Skipping tab sync - update in progress")
            return
        }
        
        val targetPosition = when (currentWalletMode) {
            WalletMode.PERSONAL -> 0
            WalletMode.ROSCA -> 1
        }
        
        if (binding.tabLayout.selectedTabPosition != targetPosition) {
            Log.d(TAG, "🔄 Syncing tab to position: $targetPosition")
            
            // Temporarily set flag to prevent listener from firing
            isUpdatingWalletSelection = true
            
            // Select the correct tab
            binding.tabLayout.getTabAt(targetPosition)?.select()
            
            // Reset flag after a short delay
            binding.root.postDelayed({
                isUpdatingWalletSelection = false
            }, 50)
        }
    }
    
    private fun switchToPersonalWallet() {
        Log.d(TAG, "switchToPersonalWallet() called")
        
        currentWalletMode = WalletMode.PERSONAL
        selectedRoscaId = null
        
        // Show ONLY personal wallet layout
        binding.layoutPersonalWallet.visibility = View.VISIBLE
        binding.layoutRoscaWallets.visibility = View.GONE
        binding.layoutRoscaWalletDetail.visibility = View.GONE
        
        if (isWalletInitialized) {
            loadPersonalWalletData()
        }
    }
    
    private fun switchToRoscaWallets() {
        Log.d(TAG, "switchToRoscaWallets() called")
        
        currentWalletMode = WalletMode.ROSCA
        
        // Hide personal wallet
        binding.layoutPersonalWallet.visibility = View.GONE
        
        // Show ROSCA list
        binding.layoutRoscaWallets.visibility = View.VISIBLE
        
        // ⚠️ CRITICAL: Hide ROSCA detail until user selects one
        binding.layoutRoscaWalletDetail.visibility = View.GONE
        
        // Load the list of available ROSCAs
        loadRoscaWalletsList()
    }
    
    private fun selectRoscaWallet(roscaId: String) {
        Log.d(TAG, "👆 User selected ROSCA from list: $roscaId")
        
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val rosca = repository.getRoscaById(roscaId)
                if (rosca != null) {
                    withContext(Dispatchers.Main) {
                        // Set flag to prevent circular updates
                        isUpdatingWalletSelection = true
                        
                        try {
                            // 1. Update local state
                            selectedRoscaId = roscaId
                            
                            // 2. ✅ NOW show the detail view (user explicitly selected this ROSCA)
                            binding.layoutRoscaWalletDetail.visibility = View.VISIBLE
                            
                            // 3. Update WalletSelectionManager
                            WalletSelectionManager.selectRoscaWallet(
                                roscaId = roscaId,
                                roscaName = rosca.name,
                                multisigAddress = rosca.multisigAddress
                            )
                            WalletSelectionManager.saveSelection(requireContext())
                            
                            // 4. Load ROSCA data
                            loadRoscaWalletData(roscaId)
                            
                        } finally {
                            // Reset flag after UI settles
                            binding.root.postDelayed({
                                isUpdatingWalletSelection = false
                            }, 100)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error selecting ROSCA wallet", e)
                withContext(Dispatchers.Main) {
                    isUpdatingWalletSelection = false
                }
            }
        }
    }
    
    private fun selectRoscaWalletInList(roscaId: String) {
        Log.d(TAG, "📱 selectRoscaWalletInList called for: $roscaId")
        
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val userId = getUserId() ?: return@launch
                val allRoscas = repository.getAllRoscas()
                val userRoscas = allRoscas.filter { rosca ->
                    val members = repository.getMembersByRoscaId(rosca.id)
                    members.any { it.userId == userId }
                }
                
                val targetRosca = userRoscas.find { it.id == roscaId }
                withContext(Dispatchers.Main) {
                    if (targetRosca != null) {
                        selectedRoscaId = roscaId
                        
                        // ✅ Show detail view (ROSCA is already selected from dashboard/previous)
                        binding.layoutRoscaWalletDetail.visibility = View.VISIBLE
                        
                        loadRoscaWalletData(roscaId)
                    } else {
                        Log.w(TAG, "ROSCA not found in user's list: $roscaId")
                        // ROSCA not found - hide detail
                        binding.layoutRoscaWalletDetail.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error selecting ROSCA in list", e)
            }
        }
    }
    
    private fun loadRoscaWalletsList() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val userId = getUserId() ?: return@launch
                
                val allRoscas = repository.getAllRoscas()
                val userRoscas = allRoscas.filter { rosca ->
                    val members = repository.getMembersByRoscaId(rosca.id)
                    members.any { it.userId == userId }
                }
                
                val roscaWalletData = userRoscas.map { rosca ->
                    RoscaWalletItem(
                        roscaId = rosca.id,
                        roscaName = rosca.name,
                        multisigAddress = rosca.multisigAddress ?: "Setting up...",
                        status = rosca.status.name,
                        currentRound = rosca.currentRound,
                        totalRounds = rosca.totalMembers,
                        balance = 0L,
                        memberCount = "${rosca.currentMembers}/${rosca.totalMembers}"
                    )
                }
                
                withContext(Dispatchers.Main) {
                    if (roscaWalletData.isEmpty()) {
                        // No ROSCAs available
                        binding.textViewNoRoscaWallets.visibility = View.VISIBLE
                        binding.recyclerViewRoscaWallets.visibility = View.GONE
                        
                        // ⚠️ CRITICAL: Ensure detail view is hidden
                        binding.layoutRoscaWalletDetail.visibility = View.GONE
                        
                    } else {
                        // ROSCAs available - show list
                        binding.textViewNoRoscaWallets.visibility = View.GONE
                        binding.recyclerViewRoscaWallets.visibility = View.VISIBLE
                        roscaWalletsAdapter.submitList(roscaWalletData)
                        
                        // ⚠️ CRITICAL: Detail view stays hidden
                        // Will only show when user taps a ROSCA from the list
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error loading ROSCA wallets", e)
                withContext(Dispatchers.Main) {
                    showError(getString(R.string.error_failed_to_load_rosca_wallets, e.message))
                }
            }
        }
    }
    
    private fun loadRoscaWalletData(roscaId: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val rosca = repository.getRoscaById(roscaId)
                if (rosca == null) {
                    withContext(Dispatchers.Main) {
                        showError(getString(R.string.Dashboard_rosca_not_found))
                    }
                    return@launch
                }
                
                withContext(Dispatchers.Main) {
                    binding.textViewRoscaName.text = rosca.name
                    binding.textViewRoscaMultisigAddress.text = rosca.multisigAddress ?: "Setting up..."
                    binding.textViewRoscaStatus.text = getString(R.string.rosca_status_format, rosca.status.name)
                    binding.textViewRoscaRound.text = getString(R.string.rosca_round_format, rosca.currentRound, rosca.totalMembers)
                }
                
                loadRoscaBalance(roscaId)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error loading ROSCA wallet data", e)
                withContext(Dispatchers.Main) {
                    showError(getString(R.string.error_failed_to_load_rosca_wallet, e.message))
                }
            }
        }
    }
    
    private fun loadRoscaBalance(roscaId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val rosca = repository.getRoscaById(roscaId)
                if (rosca == null) {
                    showError(getString(R.string.Dashboard_rosca_not_found))
                    return@launch
                }
                
                val userId = getUserId()
                if (userId == null) {
                    showError(getString(R.string.error_user_not_logged_in))
                    return@launch
                }
                
                // Switch to ROSCA wallet
                val switchResult = WalletSelectionManager.switchToRoscaWallet(
                    context = requireContext(),
                    userId = userId,
                    roscaId = roscaId,
                    roscaName = rosca.name,
                    multisigAddress = rosca.multisigAddress,
                    walletSuite = walletSuite
                )
                
                if (switchResult.isFailure) {
                    showError("Failed to switch to ROSCA wallet: ${switchResult.exceptionOrNull()?.message}")
                    return@launch
                }
                
                // Get balance from ROSCA wallet
                val balanceResult = suspendCoroutine<Pair<Long, Long>> { continuation ->
                    walletSuite.getBalance(object : WalletSuite.BalanceCallback {
                        override fun onSuccess(balance: Long, unlocked: Long) {
                            continuation.resume(Pair(balance, unlocked))
                        }
                        
                        override fun onError(error: String) {
                            continuation.resumeWithException(Exception(error))
                        }
                    })
                }
                
                val (balance, unlocked) = balanceResult
                
                // Update UI
                binding.textViewRoscaBalance.text = CurrencyFormatter.formatXMR(balance)
                binding.textViewRoscaUnlockedBalance.text = "Unlocked: ${CurrencyFormatter.formatXMR(unlocked)}"
                
                binding.textViewRoscaBalanceLabel.text = "ROSCA Multisig Wallet Balance"
                binding.textViewRoscaBalanceNote.text = "This wallet is shared by all ROSCA members"
                binding.textViewRoscaBalanceNote.visibility = View.VISIBLE
                
            } catch (e: Exception) {
                Log.e(TAG, "Error loading ROSCA balance", e)
                showError("Error: ${e.message}")
            }
        }
    }
        
    private fun initializeWallets() {
        synchronized(this) {
            if (isWalletInitialized) {
                Log.d(TAG, "✅ Wallet already initialized")
                loadPersonalWalletData()
                return
            }
            
            if (isInitializationInProgress) {
                Log.d(TAG, "⚠️ Initialization already in progress")
                return
            }
            
            isInitializationInProgress = true
        }
        
        try {
            val userId = getUserId() ?: throw IllegalStateException("User not logged in")
            
            Log.i(TAG, "=== INITIALIZING WALLETS ===")
            Log.d(TAG, "User ID: $userId")
            
            ensureUserConfigFile()
            walletSuite = WalletSuite.getInstance(requireContext())
            setupWalletStatusListener()
            
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    Log.d(TAG, "🔧 Initializing personal wallet for user: $userId")
                    val success = walletSuite.initializeWallet(userId).get()
                    
                    withContext(Dispatchers.Main) {
                        synchronized(this@WalletFragment) {
                            isWalletInitialized = success
                            isInitializationInProgress = false
                        }
                        
                        if (success) {
                            Log.d(TAG, "✅ Wallet initialized successfully")
                            
                            // ✅ Ensure UI is visible AND showing personal wallet
                            if (_binding != null && isAdded) {
                                binding.loginLayout.visibility = View.GONE
                                binding.swipeRefreshLayout.visibility = View.VISIBLE
                                binding.layoutWalletContent.visibility = View.VISIBLE
                                
                                // ✅ CRITICAL: Set initial UI state to personal wallet
                                binding.layoutPersonalWallet.visibility = View.VISIBLE
                                binding.layoutRoscaWallets.visibility = View.GONE
                                binding.layoutRoscaWalletDetail.visibility = View.GONE
                                
                                // ✅ Ensure personal wallet tab is selected
                                binding.tabLayout.getTabAt(0)?.select()
                            }
                            
                            // ✅ Set default selection to personal wallet
                            WalletSelectionManager.selectPersonalWallet()
                            WalletSelectionManager.saveSelection(requireContext())
                            
                            // Load initial data
                            loadPersonalWalletData()
                            loadRoscaWalletsList()
                            loadExchangeRate()
                            
                            // ✅ Refresh balance after delay to catch processed transactions
                            binding.root.postDelayed({
                                if (isAdded && _binding != null) {
                                    Log.d(TAG, "🔄 Refreshing balance after initialization delay...")
                                    loadPersonalWalletBalance()
                                }
                            }, 3000)
                            
                            Log.d(TAG, "✓ Wallet data loaded")
                        } else {
                            Log.e(TAG, "❌ Wallet initialization failed")
                            showError("Wallet initialization failed")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e(TAG, "❌ Wallet init exception", e)
                        synchronized(this@WalletFragment) {
                            isWalletInitialized = false
                            isInitializationInProgress = false
                        }
                        showError("Failed to initialize: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to start initialization", e)
            synchronized(this) {
                isWalletInitialized = false
                isInitializationInProgress = false
            }
            showError("Initialization error: ${e.message}")
        }
    }
        
    private fun getUserId(): String? {
        val sharedPrefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("user_id", null)
    }
    
    private fun getConfigFile(): File {
        val userId = getUserId() ?: throw IllegalStateException("User not logged in")
        val configFileName = "wallet_config_${userId}.conf"
        return File(requireContext().getExternalFilesDir(null), configFileName)
    }
    
    private fun ensureUserConfigFile() {
        val userId = getUserId() ?: throw IllegalStateException("User not logged in")
        
        synchronized(configLock) {
            val prefs = requireContext().getSharedPreferences(PREFS_WALLET_CREDENTIALS, Context.MODE_PRIVATE)
            val configKey = "${KEY_CONFIG_GENERATED}_$userId"
            val configGenerated = prefs.getBoolean(configKey, false)
            
            val configFile = getConfigFile()
            
            Log.d(TAG, "=== CONFIG CHECK FOR USER: $userId ===")
            Log.d(TAG, "Config file: ${configFile.name}")
            Log.d(TAG, "Config exists: ${configFile.exists()}")
            Log.d(TAG, "Config flag: $configGenerated")
            
            if (configFile.exists()) {
                Log.d(TAG, "✅ Config file found on disk for user: $userId")
                if (!configGenerated) {
                    Log.i(TAG, "   Restoring missing config flag")
                    prefs.edit().putBoolean(configKey, true).apply()
                }
                return
            }
            
            val history = WalletHistoryLogger.getUserWalletHistory(requireContext(), userId)
            if (history.isNotEmpty()) {
                val latestWallet = history.first()
                val walletDir = requireContext().getDir("wallets", Context.MODE_PRIVATE)
                val keysFile = File(walletDir, "${latestWallet.walletName}.keys")
                
                if (keysFile.exists()) {
                    Log.i(TAG, "✅ RECOVERING wallet from history!")
                    
                    createUserConfigFile(
                        userId = userId,
                        walletName = latestWallet.walletName,
                        walletPassword = latestWallet.walletPassword
                    )
                    
                    prefs.edit().putBoolean(configKey, true).apply()
                    
                    WalletHistoryLogger.logWalletCreation(
                        context = requireContext(),
                        userId = userId,
                        walletName = latestWallet.walletName,
                        walletPassword = latestWallet.walletPassword,
                        reason = "WALLET_RECOVERED_FROM_HISTORY",
                        additionalInfo = mapOf(
                            "original_creation_time" to latestWallet.timestamp.time.toString(),
                            "original_reason" to latestWallet.reason,
                            "config_file" to configFile.name
                        )
                    )
                    
                    return
                }
            }
            
            Log.i(TAG, "🔨 Creating new wallet for user: $userId")
            
            createNewWalletWithLogging(
                userId = userId,
                reason = WalletHistoryLogger.CreationReason.INITIAL_SETUP,
                additionalInfo = mapOf(
                    "first_time_user" to (history.isEmpty()).toString(),
                    "history_count" to history.size.toString(),
                    "config_file" to configFile.name
                )
            )
        }
    }
    
    private fun createNewWalletWithLogging(
        userId: String,
        reason: String,
        additionalInfo: Map<String, String> = emptyMap()
    ) {
        synchronized(configLock) {
            val walletName = generateRandomWalletName()
            val walletPassword = generateRandomPassword()
            
            createUserConfigFile(userId, walletName, walletPassword)
            
            val prefs = requireContext().getSharedPreferences(PREFS_WALLET_CREDENTIALS, Context.MODE_PRIVATE)
            val configKey = "${KEY_CONFIG_GENERATED}_$userId"
            prefs.edit().putBoolean(configKey, true).apply()
            
            WalletHistoryLogger.logWalletCreation(
                context = requireContext(),
                userId = userId,
                walletName = walletName,
                walletPassword = walletPassword,
                reason = reason,
                additionalInfo = additionalInfo + mapOf(
                    "android_version" to android.os.Build.VERSION.SDK_INT.toString(),
                    "app_version" to getAppVersion(),
                    "device_model" to "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
                )
            )
            
            Log.i(TAG, "✅ New wallet config created and logged")
            Log.d(TAG, "   Wallet name: $walletName")
            Log.d(TAG, "   Reason: $reason")
        }
    }
    
    private fun getAppVersion(): String {
        return try {
            val packageInfo = requireContext().packageManager.getPackageInfo(
                requireContext().packageName, 
                0
            )
            packageInfo.versionName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }
    
    private fun createUserConfigFile(userId: String, walletName: String, walletPassword: String) {
        synchronized(configLock) {
            val configFile = getConfigFile()
            
            if (configFile.exists()) {
                Log.w(TAG, "⚠️ Config file already exists, will overwrite")
                Log.w(TAG, "   File: ${configFile.name}")
            }
            
            val configContent = """
                # Wallet Configuration - User: $userId
                # Generated: ${System.currentTimeMillis()}
                # Config File: ${configFile.name}
                
                # Wallet Settings
                wallet.name=$walletName
                wallet.password=$walletPassword
                wallet.language=English
                
                # Daemon Settings (Stagenet)
                daemon.address=stagenet.xmr-tw.org
                daemon.port=38081
                daemon.username=
                daemon.password=
                daemon.ssl=false
                
                # Network Type (0=Mainnet, 1=Testnet, 2=Stagenet)
                network.type=2
            """.trimIndent()
            
            try {
                configFile.writeText(configContent)
                configFile.setReadable(true, true)
                configFile.setWritable(true, true)
                
                Log.d(TAG, "✓ User-specific config file created: ${configFile.name}")
                Log.d(TAG, "  Wallet name: $walletName")
                
            } catch (e: Exception) {
                Log.e(TAG, "✗ Failed to create config file", e)
                configFile.delete()
                throw e
            }
        }
    }
    
    private fun generateRandomWalletName(): String {
        val userId = getUserId() ?: throw IllegalStateException("User not logged in")
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = SecureRandom()
        val sb = StringBuilder(16)
        
        repeat(16) {
            sb.append(chars[random.nextInt(chars.length)])
        }
        
        val walletName = "wallet_${userId}_${sb}"
        Log.d(TAG, "Generated wallet name: $walletName")
        return walletName
    }
    
    private fun generateRandomPassword(): String {
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#\$%^&*()_+-=[]{}|;:,.<>?"
        val random = SecureRandom()
        val sb = StringBuilder(WALLET_PASSWORD_LENGTH)
        
        repeat(WALLET_PASSWORD_LENGTH) {
            sb.append(chars[random.nextInt(chars.length)])
        }
        
        return sb.toString()
    }
    
    private fun clearWalletCredentials() {
        synchronized(configLock) {
            try {
                val userId = getUserId()
                if (userId != null) {
                    synchronized(this@WalletFragment) {
                        isWalletInitialized = false
                        isInitializationInProgress = false
                    }
                    Log.d(TAG, "✅ Cleared in-memory wallet state for user: $userId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing credentials", e)
            }
        }
    }
    
    private fun loadPersonalWalletData() {
        showLoading()
        loadPersonalWalletAddress()
        loadPersonalWalletBalance()
        updateSyncStatus()
        loadTransactionCount()
        hideLoading()
    }
    
    private fun loadPersonalWalletAddress() {
        walletSuite.getAddress(object : WalletSuite.AddressCallback {
            override fun onSuccess(address: String) {
                activity?.runOnUiThread {
                    val binding = _binding ?: return@runOnUiThread
                    if (!isAdded) return@runOnUiThread
                    
                    binding.textViewPersonalAddress.text = address
                    binding.textViewPersonalAddressShort.text = formatAddressShort(address)
                    binding.buttonCopyPersonalAddress.isEnabled = true
                }
            }
            
            override fun onError(error: String) {
                activity?.runOnUiThread {
                    val binding = _binding ?: return@runOnUiThread
                    if (!isAdded) return@runOnUiThread
                    
                    binding.textViewPersonalAddress.text = getString(R.string.error_loading_address)
                    showError(getString(R.string.error_failed_to_load_address, error))
                }
            }
        })
    }
    
    private fun loadPersonalWalletBalance() {
        walletSuite.getBalance(object : WalletSuite.BalanceCallback {
            override fun onSuccess(balance: Long, unlocked: Long) {
                activity?.runOnUiThread {
                    if (_binding == null || !isAdded) return@runOnUiThread
                    updatePersonalBalanceDisplay(balance, unlocked)
                }
            }
            
            override fun onError(error: String) {
                activity?.runOnUiThread {
                    val binding = _binding ?: return@runOnUiThread
                    if (!isAdded) return@runOnUiThread
                    
                    binding.textViewPersonalBalance.text = getString(R.string.error_generic)
                    showError(getString(R.string.error_failed_to_load_balance, error))
                }
            }
        })
    }
    
    private fun updatePersonalBalanceDisplay(balance: Long, unlocked: Long) {
        val binding = _binding ?: return
        
        try {
            val balanceFormatted = CurrencyFormatter.formatXMR(balance, decimals = 6)
            val unlockedFormatted = CurrencyFormatter.formatXMR(unlocked, decimals = 6)
            
            binding.textViewPersonalBalance.text = balanceFormatted
            binding.textViewPersonalUnlockedBalance.text = "Unlocked: $unlockedFormatted"
            
            val balanceXmr = CurrencyFormatter.atomicUnitsToXMR(balance)
            val balanceUsd = balanceXmr * currentExchangeRate
            binding.textViewPersonalBalanceUsd.text = getString(R.string.balance_usd_format, CurrencyFormatter.formatUSD(balanceUsd))
            
            val locked = balance - unlocked
            if (locked > 0) {
                val lockedFormatted = CurrencyFormatter.formatXMR(locked, decimals = 6)
                binding.textViewPersonalLockedBalance.text = getString(R.string.locked_balance_format, lockedFormatted)
                binding.textViewPersonalLockedBalance.visibility = View.VISIBLE
            } else {
                binding.textViewPersonalLockedBalance.visibility = View.GONE
            }
            
        } catch (e: Exception) {
            if (_binding != null && isAdded) {
                showError(getString(R.string.error_displaying_balance, e.message))
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.buttonCopyPersonalAddress.setOnClickListener {
            copyPersonalAddressToClipboard()
        }
        
        binding.buttonSendFromPersonal.setOnClickListener {
            handleSendFromPersonal()
        }
        
        binding.buttonReceiveToPersonal.setOnClickListener {
            handleReceiveToPersonal()
        }
        
        binding.buttonExchange.setOnClickListener {
            handleExchangeClick()
        }
        
        binding.buttonContributeToRosca.setOnClickListener {
            selectedRoscaId?.let { roscaId ->
                handleContributeToRosca(roscaId)
            }
        }
        
        binding.buttonCopyRoscaAddress.setOnClickListener {
            copyRoscaAddressToClipboard()
        }
        
        binding.fabRefresh.setOnClickListener {
            refreshCurrentWallet()
        }
        
        binding.cardPersonalBalance.setOnLongClickListener {
            showWalletManagementDialog()
            true
        }
        
        binding.textViewPersonalAddress.setOnClickListener {
            copyPersonalAddressToClipboard()
        }
    }
    
    private fun handleExchangeClick() {
        try {
            val intent = Intent(requireContext(), ExchangeActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            showError(getString(R.string.error_failed_to_open_exchange, e.message))
        }
    }
    
    private fun handleContributeToRosca(roscaId: String) {
        try {
            val bundle = Bundle().apply {
                putString("rosca_id", roscaId)
            }
            findNavController().navigate(R.id.action_wallet_to_roscaDetail, bundle)
        } catch (e: Exception) {
            Log.e(TAG, "Navigation error", e)
            showError(getString(R.string.error_failed_to_open_rosca_details, e.message))
        }
    }
    
    private fun handleSendFromPersonal() {
        val syncStatus = walletSuite.stateOfSync
        if (syncStatus.syncing && syncStatus.percentDone < 100.0) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.wallet_not_synced_title))
                .setMessage(getString(R.string.dialog_msg_wait_for_sync))
                .setPositiveButton("OK", null)
                .show()
            return
        }
        
        walletSuite.getBalance(object : WalletSuite.BalanceCallback {
            override fun onSuccess(balance: Long, unlocked: Long) {
                activity?.runOnUiThread {
                    if (unlocked <= 0) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(getString(R.string.insufficient_balance_title))
                            .setMessage(getString(R.string.dialog_msg_no_unlocked_funds))
                            .setPositiveButton("OK", null)
                            .show()
                    } else {
                        try {
                            findNavController().navigate(R.id.sendFragment)
                        } catch (e: Exception) {
                            showInfo(getString(R.string.send_feature_coming_soon))
                        }
                    }
                }
            }
            
            override fun onError(error: String) {
                activity?.runOnUiThread {
                    showError(getString(R.string.error_failed_to_check_balance, error))
                }
            }
        })
    }
    
    private fun handleReceiveToPersonal() {
        walletSuite.getAddress(object : WalletSuite.AddressCallback {
            override fun onSuccess(address: String) {
                activity?.runOnUiThread {
                    showReceiveDialog(address)
                }
            }
            
            override fun onError(error: String) {
                activity?.runOnUiThread {
                    showError(getString(R.string.error_failed_to_load_address, error))
                }
            }
        })
    }
    
    private fun copyPersonalAddressToClipboard() {
        val address = binding.textViewPersonalAddress.text.toString()
        
        if (address.isNotEmpty() && address != "Loading..." && address != "Error loading address") {
            copyToClipboard("Personal Wallet Address", address, "Personal address copied")
        } else {
            showError(getString(R.string.no_valid_address))
        }
    }
    
    private fun copyRoscaAddressToClipboard() {
        val address = binding.textViewRoscaMultisigAddress.text.toString()
        
        if (address.isNotEmpty() && address != "Setting up...") {
            copyToClipboard("ROSCA Multisig Address", address, "ROSCA address copied")
        } else {
            showError(getString(R.string.error_no_valid_rosca_address))
        }
    }
    
    private fun showReceiveDialog(address: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.receive_monero_title))
            .setMessage(getString(R.string.dialog_your_address, address))
            .setPositiveButton(getString(R.string.action_copy)) { _, _ ->
                copyPersonalAddressToClipboard()
            }
            .setNeutralButton(getString(R.string.action_share)) { _, _ ->
                shareAddress(address)
            }
            .setNegativeButton(getString(R.string.action_close), null)
            .show()
    }
    
    private fun shareAddress(address: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "My Monero Address:\n$address")
                putExtra(Intent.EXTRA_SUBJECT, "Monero Address")
            }
            startActivity(Intent.createChooser(intent, "Share Address"))
        } catch (e: Exception) {
            showError(getString(R.string.error_failed_to_share_address, e.message))
        }
    }
    
    private fun showWalletManagementDialog() {
        val options = arrayOf(
            "View Current Credentials",
            "View Wallet History",
            "Export Wallet History",
            "Show Creation Statistics",
            "Show History File Path",
            "⚠️ Delete Wallet (Reset)"
        )
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title_wallet_management))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showWalletCredentialsDialog()
                    1 -> showWalletHistoryDialog()
                    2 -> exportWalletHistory()
                    3 -> showWalletStatistics()
                    4 -> showHistoryFilePath()
                    5 -> confirmWalletDeletion()
                }
            }
            .setNegativeButton(getString(R.string.action_cancel), null)
            .show()
    }
    
    private fun showWalletCredentialsDialog() {
        val credentials = exportWalletCredentials()
        
        if (credentials != null) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.dialog_title_current_credentials))
                .setMessage("$credentials\n\n⚠️ IMPORTANT: This information is also saved in your wallet history file for recovery purposes.")
                .setPositiveButton(getString(R.string.action_copy)) { _, _ ->
                    try {
                        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Wallet Credentials", credentials)
                        clipboard.setPrimaryClip(clip)
                        showSuccess(getString(R.string.credentials_copied))
                    } catch (e: Exception) {
                        showError(getString(R.string.error_failed_to_copy, e.message))
                    }
                }
                .setNegativeButton(getString(R.string.action_close), null)
                .show()
        } else {
            showError(getString(R.string.failed_export_credentials))
        }
    }
    
    private fun exportWalletCredentials(): String? {
        return try {
            val userId = getUserId() ?: return null
            val configFile = getConfigFile()
            
            if (!configFile.exists()) {
                return null
            }
            
            val properties = java.util.Properties()
            configFile.inputStream().use { properties.load(it) }
            
            val walletName = properties.getProperty("wallet.name", "")
            val walletPassword = properties.getProperty("wallet.password", "")
            
            "User ID: $userId\nWallet Name: $walletName\nWallet Password: $walletPassword"
        } catch (e: Exception) {
            Log.e(TAG, "Failed to export credentials", e)
            null
        }
    }
    
    private fun showWalletHistoryDialog() {
        val userId = getUserId()
        if (userId == null) {
            showError(getString(R.string.error_user_not_logged_in))
            return
        }
        
        lifecycleScope.launch(Dispatchers.IO) {
            val history = WalletHistoryLogger.getUserWalletHistory(requireContext(), userId)
            
            withContext(Dispatchers.Main) {
                if (history.isEmpty()) {
                    showInfo(getString(R.string.info_no_wallet_history))
                    return@withContext
                }
                
                val historyItems = history.map { record ->
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    val timestamp = dateFormat.format(record.timestamp)
                    val reasonDisplay = record.reason.replace("_", " ")
                    "[$timestamp]\n${record.walletName}\nReason: $reasonDisplay"
                }.toTypedArray()
                
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.dialog_wallet_history_title, history.size))
                    .setItems(historyItems) { _, which ->
                        showWalletHistoryDetails(history[which])
                    }
                    .setNegativeButton(getString(R.string.action_close), null)
                    .show()
            }
        }
    }
    
    private fun showWalletHistoryDetails(record: WalletCreationRecord) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
        val timestamp = dateFormat.format(record.timestamp)
        
        val details = """
            Timestamp: $timestamp
            
            User ID: ${record.userId}
            
            Wallet Name: ${record.walletName}
            
            Wallet Password: ${record.walletPassword}
            
            Creation Reason: ${record.reason.replace("_", " ")}
            
            ⚠️ Keep this information secure!
        """.trimIndent()
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title_wallet_record_details))
            .setMessage(details)
            .setPositiveButton(getString(R.string.action_copy_all)) { _, _ ->
                copyToClipboard("Wallet Record", details, "Record copied to clipboard")
            }
            .setNeutralButton(getString(R.string.button_copy_password)) { _, _ ->
                copyToClipboard("Wallet Password", record.walletPassword, "Password copied")
            }
            .setNegativeButton(getString(R.string.action_close), null)
            .show()
    }
    
    private fun exportWalletHistory() {
        val userId = getUserId()
        if (userId == null) {
            showError(getString(R.string.error_user_not_logged_in))
            return
        }
        
        lifecycleScope.launch(Dispatchers.IO) {
            val historyText = WalletHistoryLogger.exportHistoryForUser(requireContext(), userId)
            
            withContext(Dispatchers.Main) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.dialog_title_export_history))
                    .setMessage("${historyText.take(500)}${if (historyText.length > 500) "..." else ""}")
                    .setPositiveButton(getString(R.string.button_copy_full_history)) { _, _ ->
                        copyToClipboard("Wallet History", historyText, "History copied to clipboard")
                    }
                    .setNeutralButton(getString(R.string.action_share)) { _, _ ->
                        shareText("Wallet History", historyText)
                    }
                    .setNegativeButton(getString(R.string.action_close), null)
                    .show()
            }
        }
    }
    
    private fun showWalletStatistics() {
        val userId = getUserId()
        if (userId == null) {
            showError(getString(R.string.error_user_not_logged_in))
            return
        }
        
        lifecycleScope.launch(Dispatchers.IO) {
            val stats = WalletHistoryLogger.getCreationStatistics(requireContext(), userId)
            
            withContext(Dispatchers.Main) {
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val firstCreated = stats.firstCreation?.let { dateFormat.format(it) } ?: "N/A"
                val lastCreated = stats.lastCreation?.let { dateFormat.format(it) } ?: "N/A"
                
                val reasonBreakdown = stats.reasonBreakdown.entries
                    .sortedByDescending { it.value }
                    .joinToString("\n") { "  • ${it.key.replace("_", " ")}: ${it.value}" }
                
                val statsText = """
                    Total Wallets Created: ${stats.totalCreations}
                    
                    First Created: $firstCreated
                    Last Created: $lastCreated
                    
                    Current Wallet: ${stats.currentWallet ?: "Unknown"}
                    
                    Creation Reasons:
                    $reasonBreakdown
                    
                    💡 If you see multiple wallets created, check the history to understand why.
                """.trimIndent()
                
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.dialog_title_wallet_statistics))
                    .setMessage(statsText)
                    .setPositiveButton(getString(R.string.button_view_history)) { _, _ ->
                        showWalletHistoryDialog()
                    }
                    .setNegativeButton(getString(R.string.action_close), null)
                    .show()
            }
        }
    }
    
    private fun showHistoryFilePath() {
        val filePath = WalletHistoryLogger.getHistoryFilePath(requireContext())
        val userId = getUserId() ?: "unknown"
        val configFile = try { getConfigFile() } catch (e: Exception) { null }
        
        val message = """
            Your wallet creation history is stored at:
            
            $filePath
            
            Your wallet config (user-specific):
            ${configFile?.absolutePath ?: "Not available"}
            
            📋 This file contains ALL your wallet credentials and creation history.
            
            ⚠️ IMPORTANT:
            • Each user has their own config file
            • Config file: ${configFile?.name ?: "unknown"}
            • Back up both files securely
            • You can access via file manager or ADB
            
            🔧 ADB Commands to retrieve:
            adb pull "$filePath"
            adb pull "${configFile?.absolutePath ?: "config_path"}"
        """.trimIndent()
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title_wallet_files_location))
            .setMessage(message)
            .setPositiveButton(getString(R.string.button_copy_history_path)) { _, _ ->
                copyToClipboard("File Path", filePath, "Path copied to clipboard")
            }
            .setNeutralButton(getString(R.string.button_copy_config_path)) { _, _ ->
                configFile?.let {
                    copyToClipboard("Config Path", it.absolutePath, "Config path copied")
                }
            }
            .setNegativeButton(getString(R.string.action_close), null)
            .show()
    }
    
    private fun confirmWalletDeletion() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title_delete_wallet))
            .setMessage("""
                This will PERMANENTLY DELETE your current wallet and create a new one.
                
                ⚠️ WARNING:
                • Your wallet address will change
                • You will lose access to funds in the current wallet
                • This action CANNOT be undone
                • Wallet history will be preserved for recovery
                
                Only do this if you're absolutely sure!
                
                Are you sure you want to continue?
            """.trimIndent())
            .setPositiveButton(getString(R.string.button_yes_delete_wallet)) { _, _ ->
                showFinalDeletionConfirmation()
            }
            .setNegativeButton(getString(R.string.action_cancel), null)
            .show()
    }
    
    private fun showFinalDeletionConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title_final_warning))
            .setMessage(getString(R.string.dialog_msg_last_chance_delete))
            .setPositiveButton(getString(R.string.button_delete)) { _, _ ->
                performWalletDeletion()
            }
            .setNegativeButton(getString(R.string.action_cancel), null)
            .show()
    }
    
    private fun performWalletDeletion() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val userId = getUserId()
                if (userId == null) {
                    withContext(Dispatchers.Main) {
                        showError(getString(R.string.error_cannot_delete_not_logged_in))
                    }
                    return@launch
                }
                
                val currentWallet = try {
                    val configFile = getConfigFile()
                    val properties = java.util.Properties()
                    configFile.inputStream().use { properties.load(it) }
                    properties.getProperty("wallet.name", "unknown")
                } catch (e: Exception) {
                    "unknown"
                }
                
                WalletHistoryLogger.logWalletCreation(
                    context = requireContext(),
                    userId = userId,
                    walletName = "WALLET_DELETED_BY_USER",
                    walletPassword = "N/A",
                    reason = WalletHistoryLogger.CreationReason.MANUAL_RESET,
                    additionalInfo = mapOf(
                        "deleted_wallet" to currentWallet,
                        "deletion_timestamp" to System.currentTimeMillis().toString()
                    )
                )
                
                deleteWalletCompletely()
                
                withContext(Dispatchers.Main) {
                    showSuccess("Wallet deleted. Please restart the app to create a new wallet.")
                    
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.dialog_title_wallet_deleted))
                        .setMessage("The wallet has been deleted. Please close and restart the app to create a new wallet.")
                        .setPositiveButton(getString(R.string.action_ok)) { _, _ -> }
                        .setCancelable(false)
                        .show()
                }
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError(getString(R.string.error_failed_to_delete_wallet, e.message))
                }
            }
        }
    }
    
    private fun deleteWalletCompletely() {
        synchronized(configLock) {
            try {
                val userId = getUserId()
                if (userId != null) {
                    val prefs = requireContext().getSharedPreferences(PREFS_WALLET_CREDENTIALS, Context.MODE_PRIVATE)
                    val configKey = "${KEY_CONFIG_GENERATED}_$userId"
                    
                    Log.w(TAG, "⚠️ DELETING WALLET COMPLETELY FOR USER: $userId")
                    
                    prefs.edit().remove(configKey).apply()
                    
                    val configFile = getConfigFile()
                    if (configFile.exists()) {
                        val deleted = configFile.delete()
                        Log.d(TAG, "Config file deleted: $deleted (${configFile.name})")
                    }
                    
                    try {
                        val walletPath = getUserWalletPath()
                        val walletDir = requireContext().getDir("wallets", Context.MODE_PRIVATE)
                        File(walletDir, walletPath).delete()
                        File(walletDir, "$walletPath.keys").delete()
                        File(walletDir, "$walletPath.address.txt").delete()
                        
                        Log.w(TAG, "Wallet files deleted for complete reset")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error deleting wallet files", e)
                    }
                    
                    isWalletInitialized = false
                    
                    Log.d(TAG, "✅ Wallet completely deleted for user: $userId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting wallet", e)
            }
        }
    }
    
    private fun getUserWalletPath(): String {
        val configFile = getConfigFile()
        
        if (!configFile.exists()) {
            throw IllegalStateException("Config file not found: ${configFile.absolutePath}")
        }
        
        val properties = java.util.Properties()
        try {
            configFile.inputStream().use { properties.load(it) }
        } catch (e: Exception) {
            throw IllegalStateException("Failed to read config file", e)
        }
        
        val walletName = properties.getProperty("wallet.name", "")
        if (walletName.isEmpty()) {
            throw IllegalStateException("Wallet name not found in config")
        }
        
        Log.d(TAG, "Retrieved wallet path from config: $walletName")
        return walletName
    }
    
    private fun shareText(subject: String, text: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }
            startActivity(Intent.createChooser(intent, "Share $subject"))
        } catch (e: Exception) {
            showError(getString(R.string.error_failed_to_share, e.message))
        }
    }
    
    private fun refreshCurrentWallet() {
        if (isRefreshing) {
            showInfo(getString(R.string.info_refresh_in_progress))
            return
        }
        
        isRefreshing = true
        binding.swipeRefreshLayout.isRefreshing = true
        showInfo(getString(R.string.info_refreshing_wallet))
        
        when (currentWalletMode) {
            WalletMode.PERSONAL -> {
                loadExchangeRate(forceRefresh = true)
                walletSuite.triggerImmediateSync()
                
                binding.root.postDelayed({
                    if (_binding != null && isAdded) {
                        loadPersonalWalletData()
                        isRefreshing = false
                        binding.swipeRefreshLayout.isRefreshing = false
                        showSuccess(getString(R.string.success_personal_wallet_refreshed))
                    }
                }, 2000)
            }
            WalletMode.ROSCA -> {
                selectedRoscaId?.let { roscaId ->
                    loadRoscaWalletData(roscaId)
                }
                
                binding.root.postDelayed({
                    if (_binding != null && isAdded) {
                        isRefreshing = false
                        binding.swipeRefreshLayout.isRefreshing = false
                        showSuccess(getString(R.string.success_rosca_wallet_refreshed))
                    }
                }, 2000)
            }
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshCurrentWallet()
        }
    }
    
    private fun setupWalletStatusListener() {
        walletSuite.setWalletStatusListener(object : WalletSuite.WalletStatusListener {
            override fun onWalletInitialized(success: Boolean, message: String) {
                activity?.runOnUiThread {
                    if (_binding == null || !isAdded) return@runOnUiThread
                    
                    if (success) {
                        loadPersonalWalletData()
                        showSuccess(getString(R.string.success_wallet_initialized))
                    } else {
                        showError(getString(R.string.error_wallet_init_failed_msg, message))
                    }
                }
            }
            
            override fun onBalanceUpdated(balance: Long, unlocked: Long) {
                activity?.runOnUiThread {
                    if (_binding == null || !isAdded) return@runOnUiThread
                    updatePersonalBalanceDisplay(balance, unlocked)
                }
            }
            
            override fun onSyncProgress(height: Long, startHeight: Long, endHeight: Long, percentDone: Double) {
                activity?.runOnUiThread {
                    val binding = _binding
                    if (binding == null || !isAdded) return@runOnUiThread
                    updateSyncProgress(height, endHeight, percentDone)
                }
            }
        })
    }
    
    private fun updateSyncProgress(height: Long, endHeight: Long, percentDone: Double) {
        val binding = _binding ?: return
        
        binding.progressBarSync.progress = percentDone.toInt()
        binding.progressBarSync.visibility = if (percentDone >= 100.0) View.GONE else View.VISIBLE
        
        val status = if (percentDone >= 100.0) {
            "Synced at height $height"
        } else {
            String.format("Syncing: %.1f%% (%d/%d)", percentDone, height, endHeight)
        }
        
        binding.textViewSyncStatus.text = status
        
        if (percentDone >= 100.0) {
            updateLastSyncTime()
            loadPersonalWalletBalance()
            loadTransactionCount()
        }
    }
    
    private fun updateSyncStatus() {
        try {
            val syncStatus = walletSuite.stateOfSync
            
            if (syncStatus.syncing) {
                val percent = syncStatus.percentDone
                updateSyncProgress(syncStatus.walletHeight, syncStatus.daemonHeight, percent)
            } else {
                binding.progressBarSync.progress = 100
                binding.progressBarSync.visibility = View.GONE
                binding.textViewSyncStatus.text = getString(R.string.sync_status_synced_height, syncStatus.walletHeight)
                updateLastSyncTime()
                loadPersonalWalletBalance()
                loadTransactionCount()
            }
        } catch (e: Exception) {
            if (_binding != null && isAdded) {
                binding.textViewSyncStatus.text = getString(R.string.sync_status_unavailable)
            }
        }
    }
    
    private fun updateLastSyncTime() {
        val binding = _binding ?: return
        
        val currentTime = dateFormat.format(Date())
        binding.textViewLastSync.text = getString(R.string.last_sync_format, currentTime)
    }
    
    private fun loadTransactionCount() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                walletSuite.getTransactionHistory(object : WalletSuite.TransactionHistoryCallback {
                    override fun onSuccess(transactions: List<TransactionInfo>) {
                        activity?.runOnUiThread {
                            if (_binding == null || !isAdded) return@runOnUiThread
                            
                            val confirmedTxs = transactions.filter { it.confirmations > 0 }
                            val txCount = confirmedTxs.size
                            
                            binding.textViewTransactionCount.text = getString(R.string.transactions_count_format, txCount)
                            
                            if (confirmedTxs.isNotEmpty()) {
                                updateLastTransactionInfo(confirmedTxs)
                            }
                        }
                    }
                    
                    override fun onError(error: String) {
                        Log.e(TAG, "Transaction history error: $error")
                    }
                })
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading transactions", e)
            }
        }
    }
    
    private fun updateLastTransactionInfo(transactions: List<TransactionInfo>) {
        try {
            val recentTx = transactions.maxByOrNull { it.timestamp }
            recentTx?.let { tx ->
                val date = Date(tx.timestamp * 1000)
                val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
                val amountXmr = CurrencyFormatter.formatXMR(tx.amount, decimals = 6, includeSymbol = false)
                val direction = if (tx.direction == TransactionInfo.Direction.Direction_In) 
                    "Received" else "Sent"
                
                Log.d(TAG, "Most recent transaction: $direction $amountXmr XMR on $formattedDate")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to update last transaction info", e)
        }
    }
    
    private fun showTransactionHistory() {
        Log.d(TAG, "Loading transaction history...")
        showInfo(getString(R.string.info_loading_transactions))
        
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                walletSuite.getTransactionHistory(object : WalletSuite.TransactionHistoryCallback {
                    override fun onSuccess(transactions: List<TransactionInfo>) {
                        activity?.runOnUiThread {
                            val confirmedTxs = transactions.filter { it.confirmations > 0 }
                            
                            if (confirmedTxs.isEmpty()) {
                                showInfo(getString(R.string.no_transactions))
                            } else {
                                showTransactionListDialog(confirmedTxs)
                            }
                        }
                    }
                    
                    override fun onError(error: String) {
                        activity?.runOnUiThread {
                            showError(getString(R.string.error_failed_to_load_transactions_2, error))
                        }
                    }
                })
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    showError(getString(R.string.error_loading_transactions, e.message))
                }
            }
        }
    }
    
    private fun showTransactionListDialog(transactions: List<TransactionInfo>) {
        val transactionStrings = transactions.sortedByDescending { it.timestamp }.map { tx ->
            val date = Date(tx.timestamp * 1000)
            val formattedDate = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(date)
            val amount = CurrencyFormatter.formatXMR(tx.amount, decimals = 6, includeSymbol = false)
            val direction = if (tx.direction == TransactionInfo.Direction.Direction_In) "↓" else "↑"
            val confirmations = tx.confirmations
            
            "$direction $amount XMR - $formattedDate ($confirmations conf)"
        }.toTypedArray()
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.transaction_history_title))
            .setItems(transactionStrings) { _, which ->
                showTransactionDetails(transactions.sortedByDescending { it.timestamp }[which])
            }
            .setNegativeButton(getString(R.string.action_close), null)
            .show()
    }
    
    private fun showTransactionDetails(tx: TransactionInfo) {
        val date = Date(tx.timestamp * 1000)
        val formattedDate = dateFormat.format(date)
        val amount = CurrencyFormatter.formatXMR(tx.amount, decimals = 12, includeSymbol = false)
        val direction = if (tx.direction == TransactionInfo.Direction.Direction_In) 
            "Received" else "Sent"
        
        val details = "$direction $amount XMR\nDate: $formattedDate\nConfirmations: ${tx.confirmations}\nHash: ${tx.hash}"
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.transaction_details_title))
            .setMessage(details)
            .setPositiveButton(getString(R.string.action_copy_hash)) { _, _ ->
                copyToClipboard("Transaction Hash", tx.hash, "Transaction hash copied")
            }
            .setNegativeButton(getString(R.string.action_close), null)
            .show()
    }
    
    private fun formatAddressShort(address: String): String {
        return if (address.length > 20) {
            "${address.take(10)}...${address.takeLast(10)}"
        } else {
            address
        }
    }
    
    private fun copyToClipboard(label: String, text: String, successMessage: String) {
        try {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
            showSuccess(successMessage)
        } catch (e: Exception) {
            showError(getString(R.string.error_failed_to_copy, e.message))
        }
    }
    
    private fun loadExchangeRate(forceRefresh: Boolean = false) {
        val currentTime = System.currentTimeMillis()
        
        if (!forceRefresh && (currentTime - lastExchangeRateUpdate) < EXCHANGE_RATE_CACHE_DURATION) {
            Log.d(TAG, "Using cached exchange rate: $currentExchangeRate")
            return
        }
        
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val rate = fetchExchangeRateFromApi()
                withContext(Dispatchers.Main) {
                    if (_binding != null && isAdded) {
                        currentExchangeRate = rate
                        lastExchangeRateUpdate = currentTime
                        Log.d(TAG, "Exchange rate updated: $currentExchangeRate USD/XMR")
                        loadPersonalWalletBalance()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch exchange rate", e)
            }
        }
    }
    
    private suspend fun fetchExchangeRateFromApi(): Double = withContext(Dispatchers.IO) {
        try {
            val url = URL(COINGECKO_API_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()
                
                val json = JSONObject(response)
                val rate = json.getJSONObject("monero").getDouble("usd")
                
                Log.d(TAG, "Fetched XMR/USD rate: $rate")
                rate
            } else {
                Log.e(TAG, "API request failed with code: $responseCode")
                currentExchangeRate
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching exchange rate", e)
            currentExchangeRate
        }
    }
    
    private fun showLoading() {
        _binding?.progressBarLoading?.visibility = View.VISIBLE
    }
    
    private fun hideLoading() {
        _binding?.progressBarLoading?.visibility = View.GONE
    }
    
    private fun showSuccess(message: String) {
        if (_binding != null && isAdded) {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }
    
    private fun showError(message: String) {
        if (_binding != null && isAdded) {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
                .setAction("Dismiss") { }
                .show()
        }
    }
    
    private fun showInfo(message: String) {
        if (_binding != null && isAdded) {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }
    
    private fun resetWalletUI() {
        val binding = _binding ?: return
        
        binding.textViewPersonalBalance.text = "0.000000 XMR"
        binding.textViewPersonalUnlockedBalance.text = "Unlocked: 0.000000 XMR"
        binding.textViewPersonalBalanceUsd.text = "≈ $0.00"
        binding.textViewPersonalAddress.text = getString(R.string.RoscaDetail_not_logged_in)
        binding.textViewPersonalAddressShort.text = "---"
        
        // Clear ROSCA state completely
        binding.layoutRoscaWalletDetail.visibility = View.GONE
        binding.textViewRoscaName.text = ""
        binding.textViewRoscaMultisigAddress.text = ""
        binding.textViewRoscaBalance.text = "0.000000 XMR"
        selectedRoscaId = null
        
        // Default to personal wallet
        WalletSelectionManager.selectPersonalWallet()
        lifecycleScope.launch {
            WalletSelectionManager.saveSelection(requireContext())
        }
    }
    
    private fun showLoginPrompt() {
        val binding = _binding ?: return
        
        binding.layoutWalletContent.visibility = View.GONE
        binding.loginLayout.visibility = View.VISIBLE
        
        binding.btnGoogleSignIn.setOnClickListener {
            loginViewModel.startGoogleSignIn()
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
                if (state.isSignedIn) {
                    Log.d(TAG, "User signed in successfully")
                    checkLoginAndLoadData()
                }
                
                state.error?.let { error ->
                    if (isAdded) {
                        Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                    }
                    loginViewModel.clearError()
                }
            }
        }
    }

    private fun checkLoginAndLoadData() {
        val userId = getUserId()
        
        if (userId.isNullOrEmpty()) {
            Log.d(TAG, "User not logged in, showing login prompt")
            showLoginPrompt()
        } else {
            Log.d(TAG, "User logged in: $userId, initializing wallet")
            
            // ✅ SHOW THE WALLET UI
            binding.loginLayout.visibility = View.GONE
            binding.swipeRefreshLayout.visibility = View.VISIBLE
            binding.layoutWalletContent.visibility = View.VISIBLE
            
            synchronized(this) {
                if (!isWalletInitialized && !isInitializationInProgress) {
                    Log.d(TAG, "Starting wallet initialization...")
                    initializeWallets()
                } else if (isWalletInitialized) {
                    Log.d(TAG, "Wallet already initialized, just showing UI")
                    loadPersonalWalletData()
                } else {
                    Log.d(TAG, "Initialization already in progress")
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        
        Log.d(TAG, "=== onDestroyView called ===")
        
        try {
            walletSuite.setWalletStatusListener(null)
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing wallet listener", e)
        }
        
        _binding = null
    }
    
    override fun onPause() {
        super.onPause()
        
        try {
            lifecycleScope.launch {
                WalletSelectionManager.saveSelection(requireContext())
            }
            Log.d(TAG, "Saved wallet selection context on pause")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving wallet selection", e)
        }
    }

    override fun onResume() {
        super.onResume()
        
        try {
            lifecycleScope.launch {
                WalletSelectionManager.loadSelection(requireContext())
            }            
            val selectedWallet = WalletSelectionManager.getCurrentWallet()
            when (selectedWallet?.type) {
                WalletSelectionManager.WalletType.PERSONAL -> {
                    if (isWalletInitialized) {
                        loadPersonalWalletData()
                    }
                }
                
                WalletSelectionManager.WalletType.ROSCA -> {
                    selectedWallet.roscaId?.let { roscaId ->
                        selectedRoscaId = roscaId
                        binding.layoutRoscaWalletDetail.visibility = View.VISIBLE
                        loadRoscaWalletData(roscaId)
                    }
                }
                
                null -> {
                    if (isWalletInitialized) {
                        loadPersonalWalletData()
                    }
                    binding.layoutRoscaWalletDetail.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error restoring wallet selection", e)
        }
    }
}
