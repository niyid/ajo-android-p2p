package com.techducat.ajo.repository.impl

import com.techducat.ajo.repository.WalletRepository
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory implementation of WalletRepository
 */
class WalletRepositoryImpl : WalletRepository {
    
    private data class WalletData(
        val address: String,
        val balance: Long,
        val unlockedBalance: Long,
        val syncHeight: Long,
        val lastSyncTime: Long
    )
    
    private val walletCache = ConcurrentHashMap<String, WalletData>()
    private var currentWalletAddress: String? = null
    
    override suspend fun saveWalletAddress(address: String) {
        currentWalletAddress = address
        walletCache.putIfAbsent(address, WalletData(
            address = address,
            balance = 0L,
            unlockedBalance = 0L,
            syncHeight = 0L,
            lastSyncTime = System.currentTimeMillis()
        ))
    }
    
    override suspend fun getWalletAddress(): String? {
        return currentWalletAddress
    }
    
    override suspend fun saveBalance(address: String, balance: Long, unlockedBalance: Long) {
        val existing = walletCache[address]
        if (existing != null) {
            walletCache[address] = existing.copy(
                balance = balance,
                unlockedBalance = unlockedBalance
            )
        } else {
            walletCache[address] = WalletData(
                address = address,
                balance = balance,
                unlockedBalance = unlockedBalance,
                syncHeight = 0L,
                lastSyncTime = System.currentTimeMillis()
            )
        }
    }
    
    override suspend fun getBalance(address: String): Pair<Long, Long>? {
        return walletCache[address]?.let { data ->
            Pair(data.balance, data.unlockedBalance)
        }
    }
    
    override suspend fun saveSyncHeight(address: String, height: Long) {
        val existing = walletCache[address]
        if (existing != null) {
            walletCache[address] = existing.copy(
                syncHeight = height,
                lastSyncTime = System.currentTimeMillis()
            )
        }
    }
    
    override suspend fun getSyncHeight(address: String): Long {
        return walletCache[address]?.syncHeight ?: 0L
    }
    
    override suspend fun saveTransactionHistory(address: String, txHash: String, amount: Long, timestamp: Long) {
        // Simplified - would store in a separate tx map in production
    }
    
    override suspend fun getTransactionHistory(address: String): List<Map<String, Any>> {
        // Simplified - return empty list for now
        return emptyList()
    }
}
