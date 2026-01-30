package com.techducat.ajo.ui.exchange

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.techducat.ajo.databinding.ActivityExchangeBinding
import com.techducat.ajo.dlt.DLTProvider
import com.techducat.ajo.dlt.DLTProviderFactory
import com.techducat.ajo.dlt.IPFSProvider
import com.techducat.ajo.ui.auth.LoginViewModel
import com.techducat.ajo.util.SecureStorage
import com.techducat.ajo.wallet.WalletSuite
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

import com.techducat.ajo.R

/**
 * P2P Exchange Activity
 * Secure Monero trading with multisig escrow
 */
class ExchangeActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "com.techducat.ajo.ui.exchange.ExchangeActivity"
        private const val P2P_ESCROW_DURATION = 24 * 60 * 60 * 1000L
    }
    
    private lateinit var binding: ActivityExchangeBinding
    private lateinit var walletSuite: WalletSuite
    private lateinit var dltProvider: DLTProvider
    private lateinit var ipfsProvider: IPFSProvider
    private lateinit var secureStorage: SecureStorage
    private lateinit var changeNowApi: ChangeNowApi
    
    private var isBuyMode = true
    private var currentBalance = 0L
    private var currentUnlockedBalance = 0L
    
    private val decimalFormat = DecimalFormat("#,##0.00")
    private val xmrFormat = DecimalFormat("#,##0.000000000000")
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
    
    private var p2pOffers = mutableListOf<P2POffer>()
    private var activeTrades = mutableListOf<P2PTrade>()
    
    private val loginViewModel: LoginViewModel by viewModel()

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        loginViewModel.handleSignInResult(result.data)
    }    
        
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExchangeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        try {
            walletSuite = WalletSuite.getInstance(this)
            dltProvider = DLTProviderFactory.getInstance(this)
            ipfsProvider = IPFSProvider.getInstance(this)
            secureStorage = SecureStorage(this)
            changeNowApi = ChangeNowApi.getInstance(this)
                    
            setupToolbar()
            setupViews()

            setupLoginObservers()
            checkLoginAndInitialize()        
            
        } catch (e: Exception) {
            showError("Failed to initialize: ${e.message}")
            Timber.tag(TAG).e(e, "Initialization failed")
            finish()
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
            binding.progressBar?.visibility = View.VISIBLE
        } else {
            binding.progressBar?.visibility = View.GONE
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
        val userId = getCurrentUserId()
        
        if (userId.isNullOrEmpty()) {
            Log.d(TAG, "User not logged in, showing login prompt")
            showLoginPrompt()
        } else {
            Log.d(TAG, "User logged in, initializing")
            onLoginSuccess()
        }
    }
    
    private fun showLoginPrompt() {
        binding.layoutInputs.visibility = View.GONE
        binding.progressBar?.visibility = View.GONE
        binding.loginLayout.visibility = View.VISIBLE
        
        binding.btnGoogleSignIn.setOnClickListener {
            loginViewModel.startGoogleSignIn()
        }
    }
    
    private fun hideLoginPrompt() {
        binding.loginLayout.visibility = View.GONE
        binding.layoutInputs.visibility = View.VISIBLE
    }
    
    private fun getCurrentUserId(): String? {
        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("user_id", null)
    }
    
    private fun onLoginSuccess() {
        hideLoginPrompt()
        // Initialize activity content
        loadWalletBalance()
        initializeP2PTrading()            
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "P2P Exchange"
        }
    }
    
    private fun setupViews() {
        binding.radioGroupBuySell.setOnCheckedChangeListener { _, checkedId ->
            isBuyMode = when (checkedId) {
                binding.radioBuy.id -> true
                binding.radioSell.id -> false
                else -> true
            }
            updateUI()
            loadP2POffers()
        }
        
        binding.buttonExecute.setOnClickListener { showP2POptionsDialog() }
        binding.buttonRefreshRate?.setOnClickListener { loadP2POffers() }
        binding.buttonViewHistory?.setOnClickListener { showP2PTradeHistory() }
        binding.buttonCreateP2POffer?.setOnClickListener { showCreateP2POfferDialog() }
        binding.buttonViewP2POffers?.setOnClickListener { showMyP2POffersDialog() }
        
        binding.radioBuy.isChecked = true
        updateUI()
    }
    
    private fun updateUI() {
        binding.buttonExecute.text = if (isBuyMode) getString(R.string.Exchange_browse_buy_offers) else getString(R.string.Exchange_browse_sell_offers)
        binding.textViewModeDescription?.text = if (isBuyMode) {
            "Buy XMR with multisig escrow protection"
        } else {
            "Sell XMR with multisig escrow protection"
        }
    }
    
    private fun initializeP2PTrading() {
        lifecycleScope.launch {
            try {
                Timber.tag(TAG).i("Initializing P2P trading")
                loadActiveTrades()
                loadP2POffers()
                Timber.tag(TAG).i("P2P trading initialized")
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "P2P initialization failed")
            }
        }
    }
    
    private fun loadP2POffers() {
        binding.progressBarRate?.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val offers = loadLocalOffers()
                p2pOffers.clear()
                p2pOffers.addAll(offers)
                
                val filtered = if (isBuyMode) {
                    p2pOffers.filter { !it.isBuyOffer && it.isActive }
                } else {
                    p2pOffers.filter { it.isBuyOffer && it.isActive }
                }
                
                binding.textViewP2POffersCount?.text = getString(R.string.Exchange_filtered_size_offers, R.string.Exchange_filtered_size_offers)
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error loading offers")
            } finally {
                binding.progressBarRate?.visibility = View.GONE
            }
        }
    }
    
    private fun loadLocalOffers(): List<P2POffer> {
        val prefs = getSharedPreferences("p2p_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("p2p_offers", "[]") ?: "[]"
        val offers = mutableListOf<P2POffer>()
        
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                offers.add(P2POffer.fromJson(array.getJSONObject(i)))
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error loading local offers")
        }
        
        return offers.filter { it.isActive && it.remainingAmount > 0.0 }
    }
    
    private fun showCreateP2POfferDialog() {
        showInfo("Create P2P Offer - Coming soon")
    }
    
    private fun showP2POptionsDialog() {
        val options = arrayOf(
            "Browse Offers",
            "Create Offer",
            "My Offers",
            "Active Trades"
        )
        
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.Exchange_p2p_trading))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showP2POffersDialog()
                    1 -> showCreateP2POfferDialog()
                    2 -> showMyP2POffersDialog()
                    3 -> showActiveTradesDialog()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun showP2POffersDialog() {
        val filtered = if (isBuyMode) {
            p2pOffers.filter { !it.isBuyOffer && it.isActive }
        } else {
            p2pOffers.filter { it.isBuyOffer && it.isActive }
        }
        
        if (filtered.isEmpty()) {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.Exchange_offers))
                .setMessage(getString(R.string.Exchange_offers_available_create_one))
                .setPositiveButton(getString(R.string.Exchange_create)) { _, _ -> showCreateP2POfferDialog() }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
            return
        }
        
        showInfo("${filtered.size} offers available")
    }
    
    private fun showMyP2POffersDialog() {
        showInfo("My Offers - Coming soon")
    }
    
    private fun showActiveTradesDialog() {
        if (activeTrades.isEmpty()) {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.Exchange_active_trades))
                .setMessage(getString(R.string.Exchange_you_don_have_any))
                .setPositiveButton("OK", null)
                .show()
            return
        }
        
        showInfo("${activeTrades.size} active trades")
    }
    
    private fun showP2PTradeHistory() {
        showInfo("Trade History - Coming soon")
    }
    
    private fun loadActiveTrades() {
        lifecycleScope.launch {
            try {
                val prefs = getSharedPreferences("p2p_prefs", Context.MODE_PRIVATE)
                val json = prefs.getString("p2p_trades", "[]") ?: "[]"
                val trades = mutableListOf<P2PTrade>()
                
                val array = JSONArray(json)
                for (i in 0 until array.length()) {
                    val trade = P2PTrade.fromJson(array.getJSONObject(i))
                    if (trade.status !in listOf("completed", "cancelled")) {
                        trades.add(trade)
                    }
                }
                
                activeTrades.clear()
                activeTrades.addAll(trades)
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error loading trades")
            }
        }
    }
    
    private fun loadWalletBalance() {
        walletSuite.getBalance(object : WalletSuite.BalanceCallback {
            override fun onSuccess(balance: Long, unlocked: Long) {
                currentBalance = balance
                currentUnlockedBalance = unlocked
                
                val balXMR = WalletSuite.convertAtomicToXmr(balance)
                val unlXMR = WalletSuite.convertAtomicToXmr(unlocked)
                
                runOnUiThread {
                    binding.textViewBalance?.text = getString(R.string.Exchange_balance_balxmr_xmr, R.string.Exchange_balance_balxmr_xmr)
                }
            }
            
            override fun onError(error: String) {
                Timber.tag(TAG).e("Balance error: $error")
            }
        })
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
    
    private fun showInfo(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    
    override fun onResume() {
        super.onResume()
        loadWalletBalance()
        loadP2POffers()
        loadActiveTrades()
    }
}

// Data classes
data class P2POffer(
    val id: String,
    val userId: String,
    val userAddress: String,
    val isBuyOffer: Boolean,
    val amount: Double,
    var remainingAmount: Double,
    val rate: Double,
    val minTrade: Double,
    val maxTrade: Double,
    val paymentMethod: String,
    val escrowSetupInfo: String,
    val createdAt: Long,
    val expiresAt: Long,
    var ipfsHash: String? = null,
    var isActive: Boolean = true
) {
    fun toJson() = JSONObject().apply {
        put("id", id)
        put("userId", userId)
        put("userAddress", userAddress)
        put("isBuyOffer", isBuyOffer)
        put("amount", amount)
        put("remainingAmount", remainingAmount)
        put("rate", rate)
        put("minTrade", minTrade)
        put("maxTrade", maxTrade)
        put("paymentMethod", paymentMethod)
        put("escrowSetupInfo", escrowSetupInfo)
        put("createdAt", createdAt)
        put("expiresAt", expiresAt)
        put("ipfsHash", ipfsHash)
        put("isActive", isActive)
    }
    
    companion object {
        fun fromJson(json: JSONObject) = P2POffer(
            id = json.getString("id"),
            userId = json.getString("userId"),
            userAddress = json.getString("userAddress"),
            isBuyOffer = json.getBoolean("isBuyOffer"),
            amount = json.getDouble("amount"),
            remainingAmount = json.getDouble("remainingAmount"),
            rate = json.getDouble("rate"),
            minTrade = json.getDouble("minTrade"),
            maxTrade = json.getDouble("maxTrade"),
            paymentMethod = json.getString("paymentMethod"),
            escrowSetupInfo = json.getString("escrowSetupInfo"),
            createdAt = json.getLong("createdAt"),
            expiresAt = json.getLong("expiresAt"),
            ipfsHash = json.optString("ipfsHash"),
            isActive = json.optBoolean("isActive", true)
        )
    }
}

data class P2PTrade(
    val id: String,
    val offerId: String,
    val buyerUserId: String,
    val buyerAddress: String,
    val sellerUserId: String,
    val sellerAddress: String,
    val amount: Double,
    val rate: Double,
    val totalUSDT: Double,
    val paymentMethod: String,
    val escrowWalletName: String,
    val myMultisigInfo: String?,
    val counterpartyMultisigInfo: String?,
    var status: String,
    val createdAt: Long,
    val expiresAt: Long,
    var ipfsHash: String? = null
) {
    fun toJson() = JSONObject().apply {
        put("id", id)
        put("offerId", offerId)
        put("buyerUserId", buyerUserId)
        put("buyerAddress", buyerAddress)
        put("sellerUserId", sellerUserId)
        put("sellerAddress", sellerAddress)
        put("amount", amount)
        put("rate", rate)
        put("totalUSDT", totalUSDT)
        put("paymentMethod", paymentMethod)
        put("escrowWalletName", escrowWalletName)
        put("myMultisigInfo", myMultisigInfo)
        put("counterpartyMultisigInfo", counterpartyMultisigInfo)
        put("status", status)
        put("createdAt", createdAt)
        put("expiresAt", expiresAt)
        put("ipfsHash", ipfsHash)
    }
    
    companion object {
        fun fromJson(json: JSONObject) = P2PTrade(
            id = json.getString("id"),
            offerId = json.getString("offerId"),
            buyerUserId = json.getString("buyerUserId"),
            buyerAddress = json.getString("buyerAddress"),
            sellerUserId = json.getString("sellerUserId"),
            sellerAddress = json.getString("sellerAddress"),
            amount = json.getDouble("amount"),
            rate = json.getDouble("rate"),
            totalUSDT = json.getDouble("totalUSDT"),
            paymentMethod = json.getString("paymentMethod"),
            escrowWalletName = json.getString("escrowWalletName"),
            myMultisigInfo = json.optString("myMultisigInfo"),
            counterpartyMultisigInfo = json.optString("counterpartyMultisigInfo"),
            status = json.getString("status"),
            createdAt = json.getLong("createdAt"),
            expiresAt = json.getLong("expiresAt"),
            ipfsHash = json.optString("ipfsHash")
        )
    }
}
