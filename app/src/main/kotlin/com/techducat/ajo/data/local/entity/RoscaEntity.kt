package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for ROSCA (Rotating Savings and Credit Association) groups
 * Represents the local database schema for ROSCAs
 */
@Entity(tableName = "roscas")
data class RoscaEntity(
    // ============ Primary Key ============
    @PrimaryKey 
    val id: String,
    
    // ============ Basic Info ============
    val name: String,
    val description: String,
    val creatorId: String? = null,
    val groupType: String = "standard",
    
    // ============ Contribution Details ============
    val contributionAmount: Long,  // Amount in atomic units
    val contributionFrequency: String,  // "daily", "weekly", "monthly"
    val frequencyDays: Int = 7,  // Numeric representation of frequency
    
    // ============ Member Management ============
    val totalMembers: Int,
    val currentMembers: Int = 0,
    
    // ============ Distribution & Payout ============
    val payoutOrder: String = "sequential",  // "sequential", "random", "bidding"
    val distributionMethod: String = "PREDETERMINED",  // PREDETERMINED, LOTTERY, BIDDING
    
    // ============ Cycle & Status ============
    val cycleNumber: Int = 0,  // Current cycle/round number
    val currentRound: Int = 0,  // Alias for cycleNumber
    val totalCycles: Int = 0,  // Total number of cycles (usually = totalMembers)
    val status: String = "SETUP",  // SETUP, ACTIVE, PAUSED, COMPLETED, CANCELLED
    
    // ============ Blockchain & Wallet ============
    val walletAddress: String? = null,  // Monero wallet address
    val roscaWalletPath: String? = null,  // Path to ROSCA wallet file
    val multisigAddress: String? = null,  // Multisig wallet if used
    val multisigInfo: String? = null,  // Multisig configuration details
    
    // ============ IPFS & DLT ============
    val ipfsHash: String? = null,  // IPFS content hash (CID)
    val ipfsCid: String? = null,  // Alternative field name for IPFS CID
    val ipnsKey: String? = null,  // IPNS key for mutable IPFS content
    
    // ============ Sync & Version Control ============
    val version: Long = 1,  // Version number for conflict resolution
    val isDirty: Boolean = false,  // Has local changes not synced
    val lastSyncedAt: Long? = null,  // Last successful sync timestamp
    val lastSyncTimestamp: Long? = null,  // Alternative field name
    
    // ============ Timestamps ============
    val startDate: Long? = null,  // When ROSCA starts/started
    val startedAt: Long? = null,  // Alternative field name
    val completedAt: Long? = null,  // When ROSCA completed
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null
) {
    companion object {
        // Status constants
        const val STATUS_SETUP = "SETUP"
        const val STATUS_ACTIVE = "ACTIVE"
        const val STATUS_PAUSED = "PAUSED"
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_CANCELLED = "CANCELLED"
        
        // Distribution methods
        const val DISTRIBUTION_PREDETERMINED = "PREDETERMINED"
        const val DISTRIBUTION_LOTTERY = "LOTTERY"
        const val DISTRIBUTION_BIDDING = "BIDDING"
        
        // Contribution frequencies
        const val FREQUENCY_DAILY = "daily"
        const val FREQUENCY_WEEKLY = "weekly"
        const val FREQUENCY_BIWEEKLY = "biweekly"
        const val FREQUENCY_MONTHLY = "monthly"
    }
    
    /**
     * Convert to domain model (Rosca)
     */
    fun toRosca(): com.techducat.ajo.model.Rosca {
        return com.techducat.ajo.model.Rosca(
            id = id,
            roscaId = id,
            name = name,
            description = description,
            creatorId = creatorId ?: "",
            totalMembers = totalMembers,
            currentMembers = currentMembers,
            contributionAmount = contributionAmount,
            frequencyDays = frequencyDays,
            distributionMethod = when (distributionMethod.uppercase()) {
                DISTRIBUTION_LOTTERY -> com.techducat.ajo.model.Rosca.DistributionMethod.LOTTERY
                DISTRIBUTION_BIDDING -> com.techducat.ajo.model.Rosca.DistributionMethod.BIDDING
                else -> com.techducat.ajo.model.Rosca.DistributionMethod.PREDETERMINED
            },
            multisigAddress = multisigAddress ?: walletAddress,
            roscaWalletPath = roscaWalletPath,
            status = try {
                com.techducat.ajo.model.Rosca.RoscaState.valueOf(status.uppercase())
            } catch (e: IllegalArgumentException) {
                com.techducat.ajo.model.Rosca.RoscaState.SETUP
            },
            currentRound = currentRound.coerceAtLeast(cycleNumber),
            createdAt = createdAt,
            startedAt = startedAt ?: startDate,
            completedAt = completedAt
        )
    }
    
    /**
     * Get the effective IPFS hash (checking both field names)
     */
    fun resolveIPFSHash(): String? = ipfsHash ?: ipfsCid
    
    /**
     * Get the effective wallet address (checking all field names)
     */
    fun resolveWalletAddress(): String? = walletAddress ?: multisigAddress
    
    /**
     * Get the effective sync timestamp
     */
    fun resolveLastSync(): Long? = lastSyncedAt ?: lastSyncTimestamp
    
    /**
     * Calculate progress (0.0 to 1.0)
     */
    fun calculateProgress(): Float {
        val total = if (totalCycles > 0) totalCycles else totalMembers
        if (total == 0) return 0f
        val current = if (currentRound > 0) currentRound else cycleNumber
        return current.toFloat() / total.toFloat()
    }
    
    /**
     * Check if ROSCA is active
     */
    fun isActive(): Boolean = status == STATUS_ACTIVE
    
    /**
     * Check if ROSCA is completed
     */
    fun isCompleted(): Boolean = status == STATUS_COMPLETED
    
    /**
     * Check if ROSCA needs sync
     */
    fun needsSync(): Boolean = isDirty || lastSyncedAt == null
}

/**
 * Extension function to convert from domain model to entity
 */
fun com.techducat.ajo.model.Rosca.toEntity(): RoscaEntity {
    return RoscaEntity(
        id = id,
        name = name,
        description = description,
        creatorId = creatorId,
        contributionAmount = contributionAmount,
        contributionFrequency = when (frequencyDays) {
            1 -> RoscaEntity.FREQUENCY_DAILY
            7 -> RoscaEntity.FREQUENCY_WEEKLY
            14 -> RoscaEntity.FREQUENCY_BIWEEKLY
            30 -> RoscaEntity.FREQUENCY_MONTHLY
            else -> RoscaEntity.FREQUENCY_WEEKLY
        },
        frequencyDays = frequencyDays,
        totalMembers = totalMembers,
        currentMembers = currentMembers,
        payoutOrder = distributionMethod.name.lowercase(),
        distributionMethod = distributionMethod.name,
        cycleNumber = currentRound,
        currentRound = currentRound,
        totalCycles = totalMembers,
        status = status.name,
        walletAddress = multisigAddress,
        roscaWalletPath = roscaWalletPath,
        multisigAddress = multisigAddress,
        startDate = startedAt,
        startedAt = startedAt,
        completedAt = completedAt,
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis()
    )
}
