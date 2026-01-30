package com.techducat.ajo.dlt

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import com.techducat.ajo.wallet.WalletSuite

/**
 * DLTProvider Factory
 * Singleton access to MoneroBlockchainProvider implementation
 */
object DLTProviderFactory {
    @Volatile
    private var instance: DLTProvider? = null
    
    fun getInstance(context: Context): DLTProvider {
        return instance ?: synchronized(this) {
            instance ?: createProvider(context).also { instance = it }
        }
    }
    
    private fun createProvider(context: Context): DLTProvider {
        val walletSuite = WalletSuite.getInstance(context)
        val ipfs = IPFSProvider.getInstance(context)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        return MoneroBlockchainProvider(walletSuite, ipfs)
    }
    
    fun reset() {
        synchronized(this) {
            instance = null
        }
    }
}

/**
 * Extension function for easy access
 */
fun Context.getDLTProvider(): DLTProvider {
    return DLTProviderFactory.getInstance(this)
}
