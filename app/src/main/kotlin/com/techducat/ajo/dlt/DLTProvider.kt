package com.techducat.ajo.dlt

import kotlinx.coroutines.flow.Flow

/**
 * DLT Provider Interface
 * Defines operations for storing and retrieving data from the blockchain
 */
interface DLTProvider {
    
    /**
     * Store ROSCA metadata on-chain
     */
    suspend fun storeRoscaMetadata(rosca: RoscaMetadata): Result<String>
    
    /**
     * Retrieve ROSCA metadata from chain
     */
    suspend fun getRoscaMetadata(roscaId: String): Result<RoscaMetadata>
    
    /**
     * Record a contribution on-chain
     */
    suspend fun recordContribution(contribution: ContributionRecord): Result<String>
    
    /**
     * Record a distribution on-chain
     */
    suspend fun recordDistribution(distribution: DistributionRecord): Result<String>
    
    /**
     * Store arbitrary metadata
     */
    suspend fun storeMetadata(
        roscaId: String,
        metadataType: String,
        data: String
    ): Result<String>
    
    /**
     * Get metadata by type
     */
    suspend fun getMetadata(roscaId: String, metadataType: String): Result<String>
    
    /**
     * Observe ROSCA updates
     */
    fun observeRoscaUpdates(roscaId: String): Flow<DLTUpdate>
}
