// TODO: Update RoscaMetadata constructor to match new schema
package com.techducat.ajo.dlt


import kotlinx.serialization.Serializable

class MockMoneroBlockchainProvider : DLTProvider {
    
    private val storedMetadata = mutableMapOf<String, RoscaMetadata>()
    private val storedContributions = mutableMapOf<String, MutableList<ContributionRecord>>()
    private val storedDistributions = mutableMapOf<String, MutableList<DistributionRecord>>()
    private val storedData = mutableMapOf<Pair<String, String>, String>()
    
    override suspend fun storeRoscaMetadata(rosca: RoscaMetadata): Result<String> {
        return try {
            storedMetadata[rosca.roscaId] = rosca
            Result.success(DLTTestUtils.generateMockTxHash())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRoscaMetadata(roscaId: String): Result<RoscaMetadata> {
        return storedMetadata[roscaId]?.let {
            Result.success(it)
        } ?: Result.failure(Exception("ROSCA not found"))
    }
    
    override suspend fun recordContribution(contribution: ContributionRecord): Result<String> {
        return try {
            storedContributions
                .getOrPut(contribution.roscaId) { mutableListOf() }
                .add(contribution)
            Result.success(DLTTestUtils.generateMockTxHash())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun recordDistribution(distribution: DistributionRecord): Result<String> {
        return try {
            storedDistributions
                .getOrPut(distribution.roscaId) { mutableListOf() }
                .add(distribution)
            Result.success(DLTTestUtils.generateMockTxHash())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun storeMetadata(
        roscaId: String,
        metadataType: String,
        data: String
    ): Result<String> {
        return try {
            storedData[Pair(roscaId, metadataType)] = data
            Result.success(DLTTestUtils.generateMockTxHash())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMetadata(roscaId: String, metadataType: String): Result<String> {
        return storedData[Pair(roscaId, metadataType)]?.let {
            Result.success(it)
        } ?: Result.failure(Exception("Metadata not found"))
    }
    
    override fun observeRoscaUpdates(roscaId: String): kotlinx.coroutines.flow.Flow<DLTUpdate> {
        return kotlinx.coroutines.flow.flowOf()
    }
    
    // Helper methods for testing
    fun getContributions(roscaId: String): List<ContributionRecord> {
        return storedContributions[roscaId] ?: emptyList()
    }
    
    fun getDistributions(roscaId: String): List<DistributionRecord> {
        return storedDistributions[roscaId] ?: emptyList()
    }
    
    fun clear() {
        storedMetadata.clear()
        storedContributions.clear()
        storedDistributions.clear()
        storedData.clear()
    }
}

/**
 * Performance measurement utilities
 */
