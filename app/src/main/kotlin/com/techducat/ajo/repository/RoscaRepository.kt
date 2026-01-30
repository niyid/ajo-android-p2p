package com.techducat.ajo.repository

import com.techducat.ajo.model.*
import kotlinx.coroutines.flow.Flow

interface RoscaRepository {
    
    // Observable flows
    val roscasFlow: Flow<List<Rosca>>
    
    // ============ ROSCA Operations ============
    suspend fun insertRosca(rosca: Rosca)
    suspend fun updateRosca(rosca: Rosca)
    suspend fun getRoscaById(id: String): Rosca?
    suspend fun getAllRoscas(): List<Rosca>
    suspend fun deleteRosca(roscaId: String)
    suspend fun getRoscasByStatus(status: Rosca.RoscaState): List<Rosca>
    
    // ============ Member Operations ============
    suspend fun insertMember(member: Member)
    suspend fun updateMember(member: Member)
    suspend fun getMemberById(id: String): Member?
    suspend fun getMembersByRoscaId(roscaId: String): List<Member>
    suspend fun getMemberByWalletAddress(address: String): Member?
    suspend fun getAllMembers(): List<Member>
    
    // ============ Round Operations ============
    suspend fun insertRound(round: Round)
    suspend fun updateRound(round: Round)
    suspend fun getRoundById(id: String): Round?
    suspend fun getRoundsByRoscaId(roscaId: String): List<Round>
    suspend fun getRoundByNumber(roscaId: String, roundNumber: Int): Round?
    suspend fun getCurrentRound(roscaId: String): Round?
    
    // ============ Contribution Operations ============
    suspend fun insertContribution(contribution: Contribution)
    suspend fun updateContribution(contribution: Contribution)
    suspend fun getContributionById(id: String): Contribution?
    suspend fun getContributionsByRoundId(roundId: String): List<Contribution>
    suspend fun getContributionsByMemberId(memberId: String): List<Contribution>
    
    // ADDED: Methods that are actually being called
    suspend fun getContribution(roscaId: String, roundNumber: Int, memberId: String): Contribution?
    suspend fun getContributionsByRound(roscaId: String, roundNumber: Int): List<Contribution>
    
    // ============ Distribution Operations ============
    suspend fun insertDistribution(distribution: Distribution)
    suspend fun updateDistribution(distribution: Distribution)
    suspend fun getDistributionById(id: String): Distribution?
    suspend fun getDistributionsByRoscaId(roscaId: String): List<Distribution>
    suspend fun getDistributionByRound(roscaId: String, roundNumber: Int): Distribution?
    
    // ============ Bid Operations ============
    suspend fun insertBid(bid: Bid)
    suspend fun updateBid(bid: Bid)
    suspend fun getBidsByRoundId(roundId: String): List<Bid>
    suspend fun getBidByMemberAndRound(roundId: String, memberId: String): Bid?
    suspend fun getHighestBid(roundId: String): Bid?
    
    // ADDED: Method that RoscaManager actually calls
    suspend fun getBidsByRound(roscaId: String, roundNumber: Int): List<Bid>
    
    // ============ Invite Operations ============
    suspend fun getInviteByReferralCode(referralCode: String): Invite?
    suspend fun updateInvite(invite: Invite)
    
    // ============ Dividend Operations ============
    suspend fun insertDividend(dividend: Dividend)
    suspend fun getDividendsByRoundId(roundId: String): List<Dividend>
    suspend fun getDividendsByMember(memberId: String): List<Dividend>
    
    // ============ Multisig Signature Operations ============
    suspend fun insertMultisigSignature(signature: MultisigSignature)
    suspend fun getMultisigSignatures(roscaId: String, roundNumber: Int): List<MultisigSignature>
    suspend fun getMultisigSignature(roscaId: String, roundNumber: Int, memberId: String): MultisigSignature?
    suspend fun upsertMultisigSignature(signature: MultisigSignature)
    
    // ============ Transaction Operations ============
    /**
     * Execute a block of operations within a database transaction.
     * If any operation fails, all changes are rolled back.
     * 
     * Example:
     * ```
     * repository.withTransaction {
     *     insertMember(member)
     *     updateRosca(rosca)
     *     // If this throws, both operations are rolled back
     * }
     * ```
     */
    suspend fun <R> withTransaction(block: suspend () -> R): R
    
}
