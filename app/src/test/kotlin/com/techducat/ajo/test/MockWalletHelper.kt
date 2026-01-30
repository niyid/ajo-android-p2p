package com.techducat.ajo.test

import com.m2049r.xmrwallet.model.Wallet
import com.m2049r.xmrwallet.model.WalletManager
import com.m2049r.xmrwallet.model.NetworkType
import org.mockito.Mockito.*

/**
 * Helper to create properly mocked Wallet instances
 * Avoids UnsatisfiedLinkError from native libraries
 */
object MockWalletHelper {
    
    fun createMockWallet(): Wallet {
        val mockWallet = mock(Wallet::class.java)
        
        // Setup default behaviors that don't call native code
        `when`(mockWallet.address).thenReturn("48test_mock_address_123")
        `when`(mockWallet.defaultMixin).thenReturn(15)
        `when`(mockWallet.status).thenReturn(0) // Status_Ok
        `when`(mockWallet.getBalance()).thenReturn(0L)
        `when`(mockWallet.getUnlockedBalance()).thenReturn(0L)
        `when`(mockWallet.isSynchronized).thenReturn(false)
        
        return mockWallet
    }
    
    fun createMockWalletManager(): WalletManager {
        val mockManager = mock(WalletManager::class.java)
        
        // Setup default behaviors
        `when`(mockManager.networkType).thenReturn(NetworkType.NetworkType_Mainnet)
        
        return mockManager
    }
}
