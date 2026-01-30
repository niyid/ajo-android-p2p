package com.techducat.ajo

import android.app.Application
import com.techducat.ajo.di.appModule
import com.techducat.ajo.dlt.MoneroBlockchainProvider
import com.techducat.ajo.util.AuthStateManager
import com.techducat.ajo.wallet.WalletSuite
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class AjoApplication : Application() {
    
    // Inject dependencies after Koin initialization
    private val walletSuite: WalletSuite by inject()
    private val blockchainProvider: MoneroBlockchainProvider by inject()
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (com.techducat.ajo.BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Initialize Koin
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@AjoApplication)
            modules(appModule)
        }
        
        // Initialize AuthStateManager - must be done after Koin
        AuthStateManager.initialize(this)
        Timber.i("AuthStateManager initialized")
        
        // ✅ DON'T initialize WalletSuite here - it will be initialized when user logs in
        // The wallet requires a userId, which we don't have until after login
        setupWalletListeners()
        
        Timber.i("AjoApplication initialized successfully")
    }
    
    /**
     * ✅ NEW: Setup wallet listeners without initializing the wallet
     * The wallet will be initialized later when the user logs in
     */
    private fun setupWalletListeners() {
        try {
            Timber.i("Setting up WalletSuite listeners (wallet not initialized yet)")
            
            // Set up wallet status listener
            walletSuite.setWalletStatusListener(object : WalletSuite.WalletStatusListener {
                override fun onWalletInitialized(success: Boolean, message: String) {
                    if (success) {
                        Timber.i("WalletSuite initialized successfully: $message")
                        // Wallet is ready, you can now perform blockchain operations
                    } else {
                        Timber.e("WalletSuite initialization failed: $message")
                    }
                }
                
                override fun onBalanceUpdated(balance: Long, unlocked: Long) {
                    Timber.d("Balance updated - Balance: ${WalletSuite.convertAtomicToXmr(balance)} XMR, Unlocked: ${WalletSuite.convertAtomicToXmr(unlocked)} XMR")
                }
                
                override fun onSyncProgress(height: Long, startHeight: Long, endHeight: Long, percentDone: Double) {
                    Timber.d("Sync progress: $height/$endHeight (${"%.2f".format(percentDone)}%)")
                }
            })
            
            // Set up transaction listener for ROSCA operations
            walletSuite.setTransactionListener(object : WalletSuite.TransactionListener {
                override fun onTransactionCreated(txId: String, amount: Long) {
                    Timber.i("Transaction created: $txId, Amount: ${WalletSuite.convertAtomicToXmr(amount)} XMR")
                }
                
                override fun onTransactionConfirmed(txId: String) {
                    Timber.i("Transaction confirmed: $txId")
                }
                
                override fun onTransactionFailed(txId: String, error: String) {
                    Timber.e("Transaction failed: $txId, Error: $error")
                }
                
                override fun onOutputReceived(amount: Long, txHash: String, isConfirmed: Boolean) {
                    Timber.i("Output received: ${WalletSuite.convertAtomicToXmr(amount)} XMR, TX: $txHash, Confirmed: $isConfirmed")
                }
            })
            
        } catch (e: Exception) {
            Timber.e(e, "Failed to setup WalletSuite listeners")
        }
    }
    
    override fun onTerminate() {
        super.onTerminate()
        // Clean up WalletSuite resources
        try {
            walletSuite.close()
            Timber.i("WalletSuite closed")
        } catch (e: Exception) {
            Timber.e(e, "Error closing WalletSuite")
        }
        Timber.i("AjoApplication terminated")
    }
}
