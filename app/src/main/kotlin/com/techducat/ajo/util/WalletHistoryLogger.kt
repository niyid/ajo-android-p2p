package com.techducat.ajo.util

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * WalletHistoryLogger - Persistent logging of wallet creation events
 * 
 * This utility maintains a permanent record of all wallet creations,
 * including credentials and reasons. Critical for recovery scenarios.
 * 
 * Security Note: This file contains sensitive information and should
 * be backed up securely by the user.
 */
object WalletHistoryLogger {
    private const val TAG = "com.techducat.ajo.util.WalletHistoryLogger"
    private const val HISTORY_FILE = "wallet_creation_history.txt"
    private const val BACKUP_HISTORY_FILE = "wallet_creation_history.backup.txt"
    private const val MAX_FILE_SIZE = 5 * 1024 * 1024 // 5MB
    
    // Reasons for wallet creation
    object CreationReason {
        const val INITIAL_SETUP = "INITIAL_SETUP"
        const val CONFIG_MISSING = "CONFIG_FILE_MISSING"
        const val VALIDATION_FAILED = "WALLET_NAME_VALIDATION_FAILED"
        const val USER_MISMATCH = "USER_ID_MISMATCH"
        const val MANUAL_RESET = "MANUAL_CREDENTIAL_RESET"
        const val CORRUPTION_DETECTED = "CONFIG_CORRUPTION_DETECTED"
        const val MIGRATION = "DATA_MIGRATION"
        const val RESTORE_FROM_BACKUP = "RESTORE_FROM_BACKUP"
        const val UNKNOWN = "UNKNOWN"
    }
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    private val logLock = Any()
    
    /**
     * Log a wallet creation event
     */
    fun logWalletCreation(
        context: Context,
        userId: String,
        walletName: String,
        walletPassword: String,
        reason: String,
        additionalInfo: Map<String, String> = emptyMap()
    ) {
        synchronized(logLock) {
            try {
                val historyFile = getHistoryFile(context)
                
                // Check file size and rotate if needed
                if (historyFile.exists() && historyFile.length() > MAX_FILE_SIZE) {
                    rotateHistoryFile(context)
                }
                
                val timestamp = dateFormat.format(Date())
                val logEntry = buildLogEntry(
                    timestamp = timestamp,
                    userId = userId,
                    walletName = walletName,
                    walletPassword = walletPassword,
                    reason = reason,
                    additionalInfo = additionalInfo
                )
                
                // Append to history file
                historyFile.appendText(logEntry)
                
                Log.i(TAG, "✅ Wallet creation logged: $walletName")
                Log.d(TAG, "   Reason: $reason")
                Log.d(TAG, "   User: $userId")
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to log wallet creation", e)
                // Don't throw - logging failure shouldn't break wallet creation
            }
        }
    }
    
    /**
     * Build a formatted log entry
     */
    private fun buildLogEntry(
        timestamp: String,
        userId: String,
        walletName: String,
        walletPassword: String,
        reason: String,
        additionalInfo: Map<String, String>
    ): String {
        val separator = "=" * 80
        val minorSeparator = "-" * 80
        
        val builder = StringBuilder()
        builder.appendLine(separator)
        builder.appendLine("WALLET CREATION EVENT")
        builder.appendLine(separator)
        builder.appendLine("Timestamp: $timestamp")
        builder.appendLine("User ID: $userId")
        builder.appendLine("Wallet Name: $walletName")
        builder.appendLine("Wallet Password: $walletPassword")
        builder.appendLine("Creation Reason: $reason")
        
        if (additionalInfo.isNotEmpty()) {
            builder.appendLine(minorSeparator)
            builder.appendLine("Additional Information:")
            additionalInfo.forEach { (key, value) ->
                builder.appendLine("  $key: $value")
            }
        }
        
        builder.appendLine(separator)
        builder.appendLine()
        
        return builder.toString()
    }
    
    /**
     * Rotate history file when it gets too large
     */
    private fun rotateHistoryFile(context: Context) {
        try {
            val historyFile = getHistoryFile(context)
            val backupFile = getBackupHistoryFile(context)
            
            // Delete old backup if exists
            if (backupFile.exists()) {
                backupFile.delete()
                Log.d(TAG, "Deleted old backup file")
            }
            
            // Move current history to backup
            historyFile.renameTo(backupFile)
            Log.i(TAG, "✅ History file rotated to backup")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to rotate history file", e)
        }
    }
    
    /**
     * Get all wallet creation records for a specific user
     */
    fun getUserWalletHistory(context: Context, userId: String): List<WalletCreationRecord> {
        synchronized(logLock) {
            try {
                val historyFile = getHistoryFile(context)
                if (!historyFile.exists()) {
                    return emptyList()
                }
                
                val content = historyFile.readText()
                return parseHistoryFile(content, userId)
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to read wallet history", e)
                return emptyList()
            }
        }
    }
    
    /**
     * Get the most recent wallet for a user
     */
    fun getMostRecentWallet(context: Context, userId: String): WalletCreationRecord? {
        return getUserWalletHistory(context, userId).maxByOrNull { it.timestamp }
    }
    
    /**
     * Export wallet history as readable text
     */
    fun exportHistoryForUser(context: Context, userId: String): String {
        synchronized(logLock) {
            try {
                val historyFile = getHistoryFile(context)
                if (!historyFile.exists()) {
                    return "No wallet history found."
                }
                
                val content = historyFile.readText()
                val lines = content.lines()
                
                val userEntries = StringBuilder()
                userEntries.appendLine("WALLET HISTORY FOR USER: $userId")
                userEntries.appendLine("=" * 80)
                userEntries.appendLine()
                
                var inUserEntry = false
                var currentEntry = StringBuilder()
                
                for (line in lines) {
                    if (line.startsWith("User ID: $userId")) {
                        inUserEntry = true
                        currentEntry = StringBuilder()
                    }
                    
                    if (inUserEntry) {
                        currentEntry.appendLine(line)
                        
                        if (line.startsWith("=" * 80) && currentEntry.length > 100) {
                            userEntries.append(currentEntry.toString())
                            userEntries.appendLine()
                            inUserEntry = false
                        }
                    }
                }
                
                return userEntries.toString()
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to export history", e)
                return "Error exporting history: ${e.message}"
            }
        }
    }
    
    /**
     * Parse history file and extract records for a user
     */
    private fun parseHistoryFile(content: String, userId: String): List<WalletCreationRecord> {
        val records = mutableListOf<WalletCreationRecord>()
        val lines = content.lines()
        
        var currentRecord: MutableMap<String, String>? = null
        
        for (line in lines) {
            when {
                line.startsWith("WALLET CREATION EVENT") -> {
                    currentRecord = mutableMapOf()
                }
                line.startsWith("Timestamp: ") -> {
                    currentRecord?.put("timestamp", line.substringAfter("Timestamp: ").trim())
                }
                line.startsWith("User ID: ") -> {
                    currentRecord?.put("userId", line.substringAfter("User ID: ").trim())
                }
                line.startsWith("Wallet Name: ") -> {
                    currentRecord?.put("walletName", line.substringAfter("Wallet Name: ").trim())
                }
                line.startsWith("Wallet Password: ") -> {
                    currentRecord?.put("walletPassword", line.substringAfter("Wallet Password: ").trim())
                }
                line.startsWith("Creation Reason: ") -> {
                    currentRecord?.put("reason", line.substringAfter("Creation Reason: ").trim())
                }
                line.startsWith("=" * 80) && currentRecord != null -> {
                    // End of record
                    if (currentRecord["userId"] == userId) {
                        try {
                            records.add(
                                WalletCreationRecord(
                                    timestamp = dateFormat.parse(currentRecord["timestamp"] ?: ""),
                                    userId = currentRecord["userId"] ?: "",
                                    walletName = currentRecord["walletName"] ?: "",
                                    walletPassword = currentRecord["walletPassword"] ?: "",
                                    reason = currentRecord["reason"] ?: CreationReason.UNKNOWN
                                )
                            )
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to parse record", e)
                        }
                    }
                    currentRecord = null
                }
            }
        }
        
        return records.sortedByDescending { it.timestamp }
    }
    
    /**
     * Get statistics about wallet creations
     */
    fun getCreationStatistics(context: Context, userId: String): WalletStatistics {
        val history = getUserWalletHistory(context, userId)
        
        val reasonCounts = history.groupBy { it.reason }
            .mapValues { it.value.size }
        
        return WalletStatistics(
            totalCreations = history.size,
            firstCreation = history.minByOrNull { it.timestamp }?.timestamp,
            lastCreation = history.maxByOrNull { it.timestamp }?.timestamp,
            reasonBreakdown = reasonCounts,
            currentWallet = history.maxByOrNull { it.timestamp }?.walletName
        )
    }
    
    /**
     * Clear history for a specific user (use with caution!)
     */
    fun clearUserHistory(context: Context, userId: String) {
        synchronized(logLock) {
            try {
                val historyFile = getHistoryFile(context)
                if (!historyFile.exists()) return
                
                val content = historyFile.readText()
                val lines = content.lines()
                
                val filteredLines = mutableListOf<String>()
                var skipBlock = false
                var currentBlock = mutableListOf<String>()
                
                for (line in lines) {
                    if (line.startsWith("WALLET CREATION EVENT")) {
                        if (currentBlock.isNotEmpty() && !skipBlock) {
                            filteredLines.addAll(currentBlock)
                        }
                        currentBlock.clear()
                        skipBlock = false
                    }
                    
                    currentBlock.add(line)
                    
                    if (line.startsWith("User ID: $userId")) {
                        skipBlock = true
                    }
                }
                
                // Write filtered content back
                historyFile.writeText(filteredLines.joinToString("\n"))
                
                Log.i(TAG, "✅ Cleared history for user: $userId")
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear user history", e)
            }
        }
    }
    
    private fun getHistoryFile(context: Context): File {
        return File(context.getExternalFilesDir(null), HISTORY_FILE)
    }
    
    private fun getBackupHistoryFile(context: Context): File {
        return File(context.getExternalFilesDir(null), BACKUP_HISTORY_FILE)
    }
    
    /**
     * Get the file path for user to backup
     */
    fun getHistoryFilePath(context: Context): String {
        return getHistoryFile(context).absolutePath
    }
}

/**
 * Data class representing a wallet creation record
 */
data class WalletCreationRecord(
    val timestamp: Date,
    val userId: String,
    val walletName: String,
    val walletPassword: String,
    val reason: String
)

/**
 * Statistics about wallet creations
 */
data class WalletStatistics(
    val totalCreations: Int,
    val firstCreation: Date?,
    val lastCreation: Date?,
    val reasonBreakdown: Map<String, Int>,
    val currentWallet: String?
)

/**
 * Extension function for string multiplication (for separators)
 */
private operator fun String.times(count: Int): String {
    return this.repeat(count)
}
