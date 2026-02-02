package com.techducat.ajo.repository.impl

import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.*
import com.techducat.ajo.model.*
import com.techducat.ajo.repository.RoscaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.util.Log
import androidx.room.withTransaction

/**
 * Room-backed implementation of RoscaRepository
 * 
 * This class provides:
 * - Database transaction support for atomic operations
 * - Entity-to-domain model mapping (via extension functions in entity files)
 * - Comprehensive error handling and logging
 * - Observable flows for reactive UI updates
 * 
 * Key Features:
 * ✅ Transaction support via withTransaction() for data consistency
 * ✅ Flow-based observables for real-time updates
 * ✅ Complete CRUD operations for all ROSCA entities
 * ✅ Proper error handling with detailed logging
 * 
 * Note: All entity-to-domain mappings are in their respective entity files:
 * - Member.toEntity() / MemberEntity.toDomain() in MemberEntity.kt
 * - Bid.toEntity() / BidEntity.toDomain() in BidEntity.kt
 * - Rosca.toEntity() / RoscaEntity.toDomain() in RoscaEntity.kt
 * - etc.
 * 
 * @property database Room database instance
 */
class RoscaRepositoryImpl(
    internal val database: AjoDatabase
) : RoscaRepository {
    
    companion object {
        private const val TAG = "RoscaRepositoryImpl"
    }
    
    // DAO references for database access
    private val roscaDao = database.roscaDao()
    private val memberDao = database.memberDao()
    private val contributionDao = database.contributionDao()
    private val roundDao = database.roundDao()  
    private val bidDao = database.bidDao()      
    private val dividendDao = database.dividendDao()
    private val distributionDao = database.distributionDao()
    private val multisigSignatureDao = database.multisigSignatureDao()
    private val transactionDao = database.transactionDao()
    private val inviteDao = database.inviteDao()
    
    // ============================================================================
    // OBSERVABLE FLOWS
    // ============================================================================
    
    /**
     * Observable flow of all ROSCAs
     * Emits updated list whenever any ROSCA changes
     */
    override val roscasFlow: Flow<List<Rosca>> = 
        try {
            roscaDao.observeAllRoscas().map { entities ->
                entities.map { it.toRosca() }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating roscas flow", e)
            kotlinx.coroutines.flow.flowOf(emptyList())
        }
    
    // ============================================================================
    // TRANSACTION OPERATIONS
    // ============================================================================
    
    override suspend fun <R> withTransaction(block: suspend () -> R): R {
        return try {
            Log.d(TAG, "⚙️ Starting database transaction...")
            
            val result = database.withTransaction {
                block()
            }
            
            Log.d(TAG, "✅ Transaction completed successfully")
            result
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Transaction failed - all changes rolled back", e)
            throw e
        }
    }
    
    // ============================================================================
    // ROSCA OPERATIONS
    // ============================================================================
    
    override suspend fun insertRosca(rosca: Rosca) {
        try {
            Log.d(TAG, "Inserting ROSCA: ${rosca.id}, name: ${rosca.name}")
            val entity = rosca.toEntity()
            roscaDao.insert(entity)
            Log.d(TAG, "✓ ROSCA inserted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to insert ROSCA", e)
            throw e
        }
    }
    
    override suspend fun updateRosca(rosca: Rosca) {
        try {
            Log.d(TAG, "Updating ROSCA: ${rosca.id}")
            val entity = rosca.toEntity()
            roscaDao.update(entity)
            Log.d(TAG, "✓ ROSCA updated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to update ROSCA", e)
            throw e
        }
    }
    
    override suspend fun getRoscaById(id: String): Rosca? {
        return try {
            roscaDao.getById(id)?.toRosca()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting ROSCA by ID: $id", e)
            null
        }
    }
    
    override suspend fun getAllRoscas(): List<Rosca> {
        return try {
            val entities = roscaDao.getAllRoscas()
            Log.d(TAG, "Retrieved ${entities.size} ROSCAs from database")
            entities.map { it.toRosca() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all ROSCAs", e)
            emptyList()
        }
    }   
    
    override suspend fun deleteRosca(roscaId: String) {
        try {
            Log.d(TAG, "Deleting ROSCA: $roscaId")
            roscaDao.deleteById(roscaId)
            Log.d(TAG, "✓ ROSCA deleted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to delete ROSCA", e)
            throw e
        }
    }    
    
    override suspend fun getRoscasByStatus(status: Rosca.RoscaState): List<Rosca> {
        return try {
            roscaDao.getRoscasByStatus(status.name).map { it.toRosca() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting ROSCAs by status: $status", e)
            emptyList()
        }
    }
    
    suspend fun getRoscasByUser(userId: String): List<Rosca> {
        return try {
            Log.d(TAG, "Getting ROSCAs for user: $userId")
            val entities = roscaDao.getRoscasByUser(userId)
            Log.d(TAG, "Found ${entities.size} ROSCAs for user")
            entities.map { it.toRosca() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting ROSCAs by user: $userId", e)
            emptyList()
        }
    }
    
    // ============================================================================
    // MEMBER OPERATIONS
    // ============================================================================
    
    override suspend fun insertMember(member: Member) {
        try {
            Log.d(TAG, "Inserting member: ${member.id}")
            Log.d(TAG, "  ROSCA ID: ${member.roscaId}")
            Log.d(TAG, "  User ID: ${member.userId}")
            Log.d(TAG, "  Wallet: ${member.walletAddress?.take(15)}...")
            
            val entity = member.toEntity()
            memberDao.insert(entity)
            Log.d(TAG, "✓ Member inserted successfully")
            
            // Verify insertion
            val inserted = memberDao.getById(member.id)
            if (inserted != null) {
                Log.d(TAG, "✓ Member verified in database")
            } else {
                Log.e(TAG, "✗ Member NOT found after insertion!")
            }
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to insert member", e)
            throw e
        }
    }
    
    override suspend fun updateMember(member: Member) {
        try {
            val entity = member.toEntity()
            memberDao.update(entity)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating member", e)
            throw e
        }
    }
    
    override suspend fun getMemberById(id: String): Member? {
        return try {
            memberDao.getById(id)?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting member by ID: $id", e)
            null
        }
    }
    
    override suspend fun getMembersByRoscaId(roscaId: String): List<Member> {
        return try {
            Log.d(TAG, "Getting members for ROSCA: $roscaId")
            
            val entities = memberDao.getMembersByGroupSync(roscaId)
            Log.d(TAG, "Retrieved ${entities.size} member entities")
            
            val members = entities.map { it.toDomain() }
            
            // Log multisig info status
            val withMultisig = members.count { it.multisigInfo != null }
            Log.d(TAG, "$withMultisig/${members.size} members have multisig info")
            
            members
        } catch (e: Exception) {
            Log.e(TAG, "Error getting members for ROSCA: $roscaId", e)
            emptyList()
        }
    }
        
    override suspend fun getMemberByWalletAddress(address: String): Member? {
        return try {
            memberDao.getAllMembers().firstOrNull { it.walletAddress == address }?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting member by wallet address", e)
            null
        }
    }
    
    override suspend fun getAllMembers(): List<Member> {
        return try {
            val entities = memberDao.getAllMembers()
            Log.d(TAG, "Retrieved ${entities.size} members from database")
            entities.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all members", e)
            emptyList()
        }
    }
    
    // ============================================================================
    // ROUND OPERATIONS
    // ============================================================================
    
    override suspend fun insertRound(round: Round) {
        try {
            Log.d(TAG, "Inserting round: ${round.id}, number: ${round.roundNumber}")
            val entity = round.toEntity()
            roundDao.insert(entity)
            Log.d(TAG, "✓ Round inserted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to insert round", e)
            throw e
        }
    }
    
    override suspend fun updateRound(round: Round) {
        try {
            Log.d(TAG, "Updating round: ${round.id}")
            val entity = round.toEntity()
            roundDao.update(entity)
            Log.d(TAG, "✓ Round updated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to update round", e)
            throw e
        }
    }
    
    override suspend fun getRoundById(id: String): Round? {
        return try {
            roundDao.getById(id)?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting round by ID: $id", e)
            null
        }
    }
    
    override suspend fun getRoundsByRoscaId(roscaId: String): List<Round> {
        return try {
            val entities = roundDao.getByRoscaId(roscaId)
            entities.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting rounds for ROSCA: $roscaId", e)
            emptyList()
        }
    }
    
    override suspend fun getRoundByNumber(roscaId: String, roundNumber: Int): Round? {
        return try {
            roundDao.getRoundByNumber(roscaId, roundNumber)?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting round by number: $roundNumber", e)
            null
        }
    }
    
    override suspend fun getCurrentRound(roscaId: String): Round? {
        return try {
            roundDao.getCurrentRound(roscaId)?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current round", e)
            null
        }
    }
    
    // ============================================================================
    // CONTRIBUTION OPERATIONS
    // ============================================================================
    
    override suspend fun insertContribution(contribution: Contribution) {
        try {
            Log.d(TAG, "Inserting contribution: ${contribution.id}")
            val entity = contribution.toEntity()
            contributionDao.insert(entity)
            Log.d(TAG, "✓ Contribution inserted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to insert contribution", e)
            throw e
        }
    }
    
    override suspend fun updateContribution(contribution: Contribution) {
        try {
            val entity = contribution.toEntity()
            contributionDao.update(entity)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating contribution", e)
            throw e
        }
    }
    
    override suspend fun getContributionById(id: String): Contribution? {
        return try {
            contributionDao.getContributionById(id)?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting contribution by ID: $id", e)
            null
        }
    }
    
    override suspend fun getContributionsByRoundId(roundId: String): List<Contribution> {
        return try {
            Log.w(TAG, "getContributionsByRoundId not properly implemented - need to add method to DAO")
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting contributions for round: $roundId", e)
            emptyList()
        }
    }
    
    override suspend fun getContributionsByMemberId(memberId: String): List<Contribution> {
        return try {
            Log.w(TAG, "getContributionsByMemberId not properly implemented - need to add method to DAO")
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting contributions for member: $memberId", e)
            emptyList()
        }
    }
    
    override suspend fun getContribution(
        roscaId: String, 
        roundNumber: Int, 
        memberId: String
    ): Contribution? {
        return try {
            contributionDao.getContributionByMemberAndCycle(memberId, roscaId, roundNumber)?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting contribution", e)
            null
        }
    }

    override suspend fun getContributionsByRound(
        roscaId: String, 
        roundNumber: Int
    ): List<Contribution> {
        return try {
            contributionDao.getContributionsByRound(roscaId, roundNumber).map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting contributions for round", e)
            emptyList()
        }
    }
    
    // ============================================================================
    // BID OPERATIONS
    // ============================================================================
    
    override suspend fun insertBid(bid: Bid) {
        try {
            Log.d(TAG, "Inserting bid: ${bid.id}")
            val entity = bid.toEntity()
            bidDao.insert(entity)
            Log.d(TAG, "✓ Bid inserted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to insert bid", e)
            throw e
        }
    }
    
    override suspend fun updateBid(bid: Bid) {
        try {
            val entity = bid.toEntity()
            bidDao.update(entity)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating bid", e)
            throw e
        }
    }
    
    override suspend fun getBidsByRoundId(roundId: String): List<Bid> {
        return try {
            bidDao.getBidsByRoundId(roundId).map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting bids for round: $roundId", e)
            emptyList()
        }
    }
    
    override suspend fun getBidByMemberAndRound(roundId: String, memberId: String): Bid? {
        return try {
            bidDao.getBidByMemberAndRound(roundId, memberId)?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting bid", e)
            null
        }
    }    
    
    override suspend fun getHighestBid(roundId: String): Bid? {
        return try {
            bidDao.getHighestBid(roundId)?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting highest bid", e)
            null
        }
    }

    override suspend fun getBidsByRound(roscaId: String, roundNumber: Int): List<Bid> {
        return try {
            bidDao.getBidsByRound(roscaId, roundNumber).map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting bids for round", e)
            emptyList()
        }
    }

    // ============================================================================
    // INVITE OPERATIONS
    // ============================================================================

    override suspend fun getInviteByReferralCode(referralCode: String): Invite? {
        return try {
            inviteDao.getInviteByReferralCode(referralCode.uppercase())?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting invite by code: $referralCode", e)
            null
        }
    }

    override suspend fun updateInvite(invite: Invite) {
        try {
            Log.d(TAG, "Updating invite: ${invite.id}")
            inviteDao.updateInvite(invite.toEntity())
            Log.d(TAG, "✓ Invite updated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to update invite", e)
            throw e
        }
    }
    
    // ============================================================================
    // DIVIDEND OPERATIONS
    // ============================================================================
    
    override suspend fun insertDividend(dividend: Dividend) {
        try {
            Log.d(TAG, "Inserting dividend: ${dividend.id}")
            val entity = dividend.toEntity()
            dividendDao.insert(entity)
            Log.d(TAG, "✓ Dividend inserted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to insert dividend", e)
            throw e
        }
    }

    override suspend fun getDividendsByRoundId(roundId: String): List<Dividend> {
        return try {
            dividendDao.getDividendsByRoundId(roundId).map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting dividends for round: $roundId", e)
            emptyList()
        }
    }

    override suspend fun getDividendsByMember(memberId: String): List<Dividend> {
        return try {
            dividendDao.getDividendsByMember(memberId).map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting dividends for member: $memberId", e)
            emptyList()
        }
    }

    // ============================================================================
    // DISTRIBUTION OPERATIONS
    // ============================================================================

    override suspend fun insertDistribution(distribution: Distribution) {
        try {
            Log.d(TAG, "Inserting distribution: ${distribution.id}")
            val entity = distribution.toEntity()
            distributionDao.insert(entity)
            Log.d(TAG, "✓ Distribution inserted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to insert distribution", e)
            throw e
        }
    }

    override suspend fun updateDistribution(distribution: Distribution) {
        try {
            val entity = distribution.toEntity()
            distributionDao.update(entity)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating distribution", e)
            throw e
        }
    }

    override suspend fun getDistributionById(id: String): Distribution? {
        return try {
            distributionDao.getById(id)?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting distribution by ID: $id", e)
            null
        }
    }

    override suspend fun getDistributionsByRoscaId(roscaId: String): List<Distribution> {
        return try {
            distributionDao.getByRoscaId(roscaId).map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting distributions for ROSCA: $roscaId", e)
            emptyList()
        }
    }

    override suspend fun getDistributionByRound(
        roscaId: String, 
        roundNumber: Int
    ): Distribution? {
        return try {
            distributionDao.getByRound(roscaId, roundNumber)?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting distribution for round", e)
            null
        }
    }
    
    // ============================================================================
    // MULTISIG SIGNATURE OPERATIONS
    // ============================================================================

    override suspend fun insertMultisigSignature(signature: MultisigSignature) {
        try {
            Log.d(TAG, "Inserting multisig signature: ${signature.id}")
            val entity = signature.toEntity()
            multisigSignatureDao.insert(entity)
            Log.d(TAG, "✓ Multisig signature inserted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to insert multisig signature", e)
            throw e
        }
    }

    override suspend fun getMultisigSignatures(roscaId: String, roundNumber: Int): List<MultisigSignature> {
        return try {
            multisigSignatureDao
                .getMultisigSignatures(roscaId, roundNumber)
                .map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting multisig signatures", e)
            emptyList()
        }
    }

    override suspend fun getMultisigSignature(
        roscaId: String,
        roundNumber: Int,
        memberId: String
    ): MultisigSignature? {
        return try {
            multisigSignatureDao
                .getMultisigSignature(roscaId, roundNumber, memberId)
                ?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting multisig signature", e)
            null
        }
    }

    override suspend fun upsertMultisigSignature(signature: MultisigSignature) {
        try {
            Log.d(TAG, "Upserting multisig signature: ${signature.id}")
            val entity = signature.toEntity()
            multisigSignatureDao.upsert(entity)
            Log.d(TAG, "✓ Multisig signature upserted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to upsert multisig signature", e)
            throw e
        }
    }
    
    override suspend fun getSignaturesForTransaction(txHash: String): List<MultisigSignature> {
        return try {
            multisigSignatureDao.getByTransaction(txHash).map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting signatures for transaction: $txHash", e)
            emptyList()
        }
    }

    override fun observeSignatures(roscaId: String): Flow<List<MultisigSignature>> {
        return try {
            multisigSignatureDao.getByRoscaFlow(roscaId).map { entities ->
                entities.map { it.toDomain() }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating signatures flow", e)
            kotlinx.coroutines.flow.flowOf(emptyList())
        }
    }
    
    // ============================================================================
    // TRANSACTION OPERATIONS
    // ============================================================================
    
    override suspend fun getTransactionById(txId: String): Transaction? {
        return try {
            transactionDao.getByTxHash(txId)?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting transaction by ID: $txId", e)
            null
        }
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        try {
            Log.d(TAG, "Updating transaction: ${transaction.id}")
            val entity = transaction.toEntity()
            transactionDao.update(entity)
            Log.d(TAG, "✓ Transaction updated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to update transaction", e)
            throw e
        }
    }

    override suspend fun getPendingTransactions(roscaId: String): List<Transaction> {
        return try {
            transactionDao.getPendingSignatures()
                .filter { it.roscaId == roscaId }
                .map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting pending transactions for ROSCA: $roscaId", e)
            emptyList()
        }
    }

    override fun observePendingTransactions(roscaId: String): Flow<List<Transaction>> {
        return try {
            // Create a flow that emits pending transactions
            kotlinx.coroutines.flow.flow {
                val pending = transactionDao.getPendingSignatures()
                    .filter { it.roscaId == roscaId }
                    .map { it.toDomain() }
                emit(pending)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating pending transactions flow", e)
            kotlinx.coroutines.flow.flowOf(emptyList())
        }
    }
}
