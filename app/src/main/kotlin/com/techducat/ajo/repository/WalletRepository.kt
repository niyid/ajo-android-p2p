package com.techducat.ajo.repository

interface WalletRepository {
    suspend fun saveWalletAddress(address: String)
    suspend fun getWalletAddress(): String?
    suspend fun saveBalance(address: String, balance: Long, unlockedBalance: Long)
    suspend fun getBalance(address: String): Pair<Long, Long>?
    suspend fun saveSyncHeight(address: String, height: Long)
    suspend fun getSyncHeight(address: String): Long
    suspend fun saveTransactionHistory(address: String, txHash: String, amount: Long, timestamp: Long)
    suspend fun getTransactionHistory(address: String): List<Map<String, Any>>
}
