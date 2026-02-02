package com.techducat.ajo.repository

import com.techducat.ajo.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for ROSCA-related data operations
 * 
 * This repository provides:
 * - CRUD operations for ROSCAs, members, rounds, contributions, etc.
 * - Observable flows for reactive UI updates
 * - Transaction support for atomic operations
 * - Multisig signature and transaction management
 * 
 * Implementation notes:
 * - All suspend functions are safe to call from any coroutine context
 * - Observable flows automatically update when underlying data changes
 * - Use withTransaction() for operations that must succeed or fail together
 */
interface RoscaRepository {
    
    // ============================================================================
    // OBSERVABLE FLOWS
    // ============================================================================
    
    /**
     * Observable flow of all ROSCAs
     * Emits updated list whenever any ROSCA changes in the database
     */
    val roscasFlow: Flow<List<Rosca>>
    
    // ============================================================================
    // ROSCA OPERATIONS
    // ============================================================================
    
    suspend fun insertRosca(rosca: Rosca)
    suspend fun updateRosca(rosca: Rosca)
    suspend fun getRoscaById(id: String): Rosca?
    suspend fun getAllRoscas(): List<Rosca>
    suspend fun deleteRosca(roscaId: String)
    suspend fun getRoscasByStatus(status: Rosca.RoscaState): List<Rosca>
    
    // ============================================================================
    // MEMBER OPERATIONS
    // ============================================================================
    
    suspend fun insertMember(member: Member)
    suspend fun updateMember(member: Member)
    suspend fun getMemberById(id: String): Member?
    suspend fun getMembersByRoscaId(roscaId: String): List<Member>
    suspend fun getMemberByWalletAddress(address: String): Member?
    suspend fun getAllMembers(): List<Member>
    
    // ============================================================================
    // ROUND OPERATIONS
    // ============================================================================
    
    suspend fun insertRound(round: Round)
    suspend fun updateRound(round: Round)
    suspend fun getRoundById(id: String): Round?
    suspend fun getRoundsByRoscaId(roscaId: String): List<Round>
    suspend fun getRoundByNumber(roscaId: String, roundNumber: Int): Round?
    suspend fun getCurrentRound(roscaId: String): Round?
    
    // ============================================================================
    // CONTRIBUTION OPERATIONS
    // ============================================================================
    
    suspend fun insertContribution(contribution: Contribution)
    suspend fun updateContribution(contribution: Contribution)
    suspend fun getContributionById(id: String): Contribution?
    suspend fun getContributionsByRoundId(roundId: String): List<Contribution>
    suspend fun getContributionsByMemberId(memberId: String): List<Contribution>
    suspend fun getContribution(roscaId: String, roundNumber: Int, memberId: String): Contribution?
    suspend fun getContributionsByRound(roscaId: String, roundNumber: Int): List<Contribution>
    
    // ============================================================================
    // DISTRIBUTION OPERATIONS
    // ============================================================================
    
    suspend fun insertDistribution(distribution: Distribution)
    suspend fun updateDistribution(distribution: Distribution)
    suspend fun getDistributionById(id: String): Distribution?
    suspend fun getDistributionsByRoscaId(roscaId: String): List<Distribution>
    suspend fun getDistributionByRound(roscaId: String, roundNumber: Int): Distribution?
    
    // ============================================================================
    // BID OPERATIONS
    // ============================================================================
    
    suspend fun insertBid(bid: Bid)
    suspend fun updateBid(bid: Bid)
    suspend fun getBidsByRoundId(roundId: String): List<Bid>
    suspend fun getBidByMemberAndRound(roundId: String, memberId: String): Bid?
    suspend fun getHighestBid(roundId: String): Bid?
    suspend fun getBidsByRound(roscaId: String, roundNumber: Int): List<Bid>
    
    // ============================================================================
    // INVITE OPERATIONS
    // ============================================================================
    
    suspend fun getInviteByReferralCode(referralCode: String): Invite?
    suspend fun updateInvite(invite: Invite)
    
    // ============================================================================
    // DIVIDEND OPERATIONS
    // ============================================================================
    
    suspend fun insertDividend(dividend: Dividend)
    suspend fun getDividendsByRoundId(roundId: String): List<Dividend>
    suspend fun getDividendsByMember(memberId: String): List<Dividend>
    
    // ============================================================================
    // MULTISIG SIGNATURE OPERATIONS
    // ============================================================================
    
    /**
     * Insert a new multisig signature
     * @param signature The signature to store
     */
    suspend fun insertMultisigSignature(signature: MultisigSignature)
    
    /**
     * Get all signatures for a specific round
     * @param roscaId The ROSCA identifier
     * @param roundNumber The round number
     * @return List of signatures for this round
     */
    suspend fun getMultisigSignatures(roscaId: String, roundNumber: Int): List<MultisigSignature>
    
    /**
     * Get a specific member's signature for a round
     * @param roscaId The ROSCA identifier
     * @param roundNumber The round number
     * @param memberId The member identifier
     * @return The signature if it exists, null otherwise
     */
    suspend fun getMultisigSignature(
        roscaId: String,
        roundNumber: Int,
        memberId: String
    ): MultisigSignature?
    
    /**
     * Insert or update a multisig signature
     * @param signature The signature to upsert
     */
    suspend fun upsertMultisigSignature(signature: MultisigSignature)
    
    /**
     * Get all signatures for a specific transaction
     * Added to support MultisigCoordinator without direct database access
     * 
     * @param txHash Transaction hash or ID
     * @return List of signatures for this transaction
     */
    suspend fun getSignaturesForTransaction(txHash: String): List<MultisigSignature>
    
    /**
     * Observe signatures for a ROSCA as a Flow
     * Useful for real-time UI updates
     * 
     * @param roscaId The ROSCA identifier
     * @return Flow that emits updated signature lists
     */
    fun observeSignatures(roscaId: String): Flow<List<MultisigSignature>>
    
    // ============================================================================
    // TRANSACTION OPERATIONS
    // ============================================================================
    
    /**
     * Get a transaction by its hash or ID
     * Added to support MultisigCoordinator without direct database access
     * 
     * @param txId Transaction hash or internal ID
     * @return Transaction if found, null otherwise
     */
    suspend fun getTransactionById(txId: String): Transaction?
    
    /**
     * Update a transaction
     * Added to support MultisigCoordinator without direct database access
     * 
     * @param transaction The transaction to update
     */
    suspend fun updateTransaction(transaction: Transaction)
    
    /**
     * Get all pending transactions for a ROSCA
     * 
     * @param roscaId The ROSCA identifier
     * @return List of transactions awaiting signatures
     */
    suspend fun getPendingTransactions(roscaId: String): List<Transaction>
    
    /**
     * Observe pending transactions as a Flow
     * Useful for real-time UI updates
     * 
     * @param roscaId The ROSCA identifier
     * @return Flow that emits updated pending transaction lists
     */
    fun observePendingTransactions(roscaId: String): Flow<List<Transaction>>
    
    // ============================================================================
    // TRANSACTION SUPPORT
    // ============================================================================
    
    /**
     * Execute a block of operations within a database transaction
     * 
     * If any operation in the block fails, all changes are rolled back.
     * This is CRITICAL for maintaining data consistency, especially for:
     * - Member joins (insert member + update ROSCA atomically)
     * - Round progression (update round + create next round atomically)
     * - Payout processing (update transaction + signatures atomically)
     * 
     * @param block Suspend function containing operations to execute atomically
     * @return Result of the block execution
     * @throws Exception if any operation fails (triggers rollback)
     * 
     * Example usage:
     * ```kotlin
     * repository.withTransaction {
     *     insertMember(member)
     *     updateRosca(rosca.copy(currentMembers = rosca.currentMembers + 1))
     *     // If updateRosca fails, insertMember is automatically rolled back
     * }
     * ```
     */
    suspend fun <R> withTransaction(block: suspend () -> R): R
    
}
