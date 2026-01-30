package com.techducat.ajo.repository.impl

import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.*
import com.techducat.ajo.model.*
import com.techducat.ajo.repository.RoscaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.util.Log
import com.techducat.ajo.model.Round.RoundStatus
import com.techducat.ajo.model.Distribution.DistributionStatus
import androidx.room.withTransaction

/**
 * Room-backed implementation of RoscaRepository
 * 
 * ✅ NOW WITH TRANSACTION SUPPORT for data consistency!
 */
class RoscaRepositoryImpl(
    private val database: AjoDatabase
) : RoscaRepository {
    
    companion object {
        private const val TAG = "com.techducat.ajo.repository.impl.RoscaRepositoryImpl"
    }
    
    private val roscaDao = database.roscaDao()
    private val memberDao = database.memberDao()
    private val contributionDao = database.contributionDao()
    private val roundDao = database.roundDao()  
    private val bidDao = database.bidDao()      
    private val dividendDao = database.dividendDao()
    private val distributionDao = database.distributionDao()
    private val multisigSignatureDao = database.multisigSignatureDao()
    
    // Observable flows
    override val roscasFlow: Flow<List<Rosca>> = 
        try {
            roscaDao.observeAllRoscas().map { entities ->
                entities.map { it.toDomain() }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating roscas flow", e)
            kotlinx.coroutines.flow.flowOf(emptyList())
        }
    
    // ============================================================================
    // TRANSACTION OPERATIONS - THE KEY FIX!
    // ============================================================================
    
    /**
     * Execute operations within a database transaction.
     * 
     * Room's withTransaction ensures:
     * 1. All operations succeed together, or
     * 2. All operations are rolled back on any failure
     * 
     * This is CRITICAL for maintaining data consistency in joinRosca()!
     * 
     * Example usage:
     * ```
     * repository.withTransaction {
     *     insertMember(member)
     *     updateRosca(rosca)
     *     // If updateRosca fails, insertMember is rolled back!
     * }
     * ```
     */
    override suspend fun <R> withTransaction(block: suspend () -> R): R {
        return try {
            Log.d(TAG, "⚙️ Starting database transaction...")
            
            // Room's withTransaction provides automatic rollback on exception
            val result = database.withTransaction {
                block()
            }
            
            Log.d(TAG, "✅ Transaction completed successfully and committed")
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
            Log.d(TAG, "✓ ROSCA inserted successfully to database")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to insert ROSCA to database", e)
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
            roscaDao.getById(id)?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting ROSCA by ID: $id", e)
            null
        }
    }
    
    override suspend fun getAllRoscas(): List<Rosca> {
        return try {
            val entities = roscaDao.getAllRoscas()
            Log.d(TAG, "Retrieved ${entities.size} ROSCAs from database")
            entities.map { it.toDomain() }
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
            roscaDao.getRoscasByStatus(status.name).map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting ROSCAs by status: $status", e)
            emptyList()
        }
    }
    
    // NEW METHOD: Get ROSCAs by user using the DAO query
    suspend fun getRoscasByUser(userId: String): List<Rosca> {
        return try {
            Log.d(TAG, "Getting ROSCAs for user: $userId")
            val entities = roscaDao.getRoscasByUser(userId)
            Log.d(TAG, "Found ${entities.size} ROSCAs for user via DAO")
            entities.map { it.toDomain() }
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
            Log.d(TAG, "Inserting member: ${member.id}, wallet: ${member.walletAddress?.take(15)}...")
            Log.d(TAG, "  ROSCA ID: ${member.roscaId}")
            Log.d(TAG, "  User ID: ${member.userId}")
            val entity = member.toEntity()
            memberDao.insert(entity)
            Log.d(TAG, "✓ Member inserted successfully to database")
            
            // Verify insertion
            val inserted = memberDao.getById(member.id)
            if (inserted != null) {
                Log.d(TAG, "✓ Member verified in database")
                Log.d(TAG, "  Wallet: ${inserted.walletAddress}")
                Log.d(TAG, "  User ID: ${inserted.userId}")
                Log.d(TAG, "  ROSCA ID: ${inserted.roscaId}")
            } else {
                Log.e(TAG, "✗ Member NOT found after insertion!")
            }
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to insert member to database", e)
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
            Log.d(TAG, "getMembersByRoscaId: Querying for ROSCA: $roscaId")
            
            val entities = memberDao.getMembersByGroupSync(roscaId)
            Log.d(TAG, "getMembersByRoscaId: Retrieved ${entities.size} member entities")
            
            val members = entities.mapIndexed { index, entity ->
                Log.d(TAG, "  Member $index:")
                Log.d(TAG, "    ID: ${entity.id}")
                Log.d(TAG, "    User ID: ${entity.userId}")
                Log.d(TAG, "    Name: ${entity.name}")
                Log.d(TAG, "    MultisigInfo: ${if (entity.multisigInfo != null) "present" else "NULL"}")
                
                entity.toDomain()
            }
            
            Log.d(TAG, "getMembersByRoscaId: Converted to ${members.size} domain members")
            
            // Count members with multisig info
            val withMultisig = members.count { it.multisigInfo != null }
            Log.d(TAG, "getMembersByRoscaId: $withMultisig/${members.size} members have multisig info")
            
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
            database.inviteDao().getInviteByReferralCode(referralCode.uppercase())?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting invite by code: $referralCode", e)
            null
        }
    }

    override suspend fun updateInvite(invite: Invite) {
        try {
            Log.d(TAG, "Updating invite: ${invite.id}")
            database.inviteDao().updateInvite(invite.toEntity())
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
    
}

// ============================================================================
// ENTITY MAPPING EXTENSIONS (unchanged from original)
// ============================================================================

private fun Rosca.toEntity() = RoscaEntity(
    id = id,
    name = name,
    description = description,
    creatorId = creatorId,
    totalMembers = totalMembers,
    currentMembers = currentMembers,
    contributionAmount = contributionAmount,
    contributionFrequency = when (frequencyDays) {
        1 -> "daily"
        7 -> "weekly"
        14 -> "biweekly"
        30 -> "monthly"
        else -> "weekly"
    },
    frequencyDays = frequencyDays,
    distributionMethod = distributionMethod.name,
    payoutOrder = distributionMethod.name.lowercase(),
    multisigAddress = multisigAddress,
    walletAddress = multisigAddress,
    roscaWalletPath = roscaWalletPath,
    status = status.name,
    cycleNumber = currentRound,
    currentRound = currentRound,
    totalCycles = totalMembers,
    startDate = startedAt,
    startedAt = startedAt,
    completedAt = completedAt,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis(),
    lastSyncedAt = null,
    ipfsHash = null,
    ipfsCid = null,
    isDirty = true
)

private fun Contribution.toEntity() = ContributionEntity(
    id = id,
    roscaId = roundId ?: "",
    memberId = memberId,
    amount = amount,
    cycleNumber = 0,
    status = status.name.lowercase(),
    dueDate = createdAt,
    txHash = transactionHash,
    txId = transactionHash ?: "",
    proofOfPayment = null,
    verifiedAt = if (status == Contribution.ContributionStatus.CONFIRMED) createdAt else null,
    notes = null,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis(),
    isDirty = false,
    lastSyncedAt = null,
    ipfsHash = null
)

private fun RoscaEntity.toDomain() = Rosca(
    id = id,
    name = name,
    description = description,
    creatorId = creatorId ?: "",
    totalMembers = totalMembers,
    currentMembers = currentMembers,
    contributionAmount = contributionAmount,
    frequencyDays = frequencyDays,
    distributionMethod = try {
        Rosca.DistributionMethod.valueOf(distributionMethod.uppercase())
    } catch (e: Exception) {
        Rosca.DistributionMethod.PREDETERMINED
    },
    multisigAddress = multisigAddress,
    roscaWalletPath = roscaWalletPath,
    status = try {
        Rosca.RoscaState.valueOf(status.uppercase())
    } catch (e: Exception) {
        Rosca.RoscaState.SETUP
    },
    currentRound = currentRound.coerceAtLeast(cycleNumber),
    startedAt = startedAt ?: startDate,
    completedAt = completedAt,
    createdAt = createdAt
)

private fun ContributionEntity.toDomain() = Contribution(
    id = id,
    roundId = roscaId,
    memberId = memberId,
    amount = amount,
    txHash = txHash ?: txId ?: "",
    status = try {
        when (status.uppercase()) {
            "PENDING" -> Contribution.ContributionStatus.PENDING
            "CONFIRMED" -> Contribution.ContributionStatus.CONFIRMED
            "FAILED" -> Contribution.ContributionStatus.FAILED
            else -> Contribution.ContributionStatus.PENDING
        }
    } catch (e: Exception) {
        Contribution.ContributionStatus.PENDING
    },
    createdAt = createdAt
)

private fun Invite.toEntity() = InviteEntity(
    id = id,
    roscaId = roscaId,
    inviterUserId = inviterUserId,
    inviteeEmail = inviteeEmail,
    referralCode = referralCode,
    status = status.name.lowercase(),
    createdAt = createdAt,
    acceptedAt = acceptedAt,
    expiresAt = expiresAt,
    acceptedByUserId = acceptedByUserId
)

private fun InviteEntity.toDomain() = Invite(
    id = id,
    roscaId = roscaId,
    inviterUserId = inviterUserId,
    inviteeEmail = inviteeEmail,
    referralCode = referralCode,
    status = when (status.uppercase()) {
        "PENDING" -> Invite.InviteStatus.PENDING
        "ACCEPTED" -> Invite.InviteStatus.ACCEPTED
        "EXPIRED" -> Invite.InviteStatus.EXPIRED
        "DECLINED" -> Invite.InviteStatus.DECLINED
        else -> Invite.InviteStatus.PENDING
    },
    createdAt = createdAt,
    acceptedAt = acceptedAt,
    expiresAt = expiresAt,
    acceptedByUserId = acceptedByUserId
)

private fun Round.toEntity() = RoundEntity(
    id = id,
    roscaId = roscaId,
    roundNumber = roundNumber,
    recipientMemberId = recipientId ?: "",
    recipientAddress = recipientAddress ?: "",
    targetAmount = targetAmount,
    collectedAmount = collectedAmount,
    status = when (status) {
        RoundStatus.BIDDING -> RoundEntity.STATUS_ACTIVE
        RoundStatus.CONTRIBUTION -> RoundEntity.STATUS_ACTIVE
        RoundStatus.PAYOUT -> RoundEntity.STATUS_PAYOUT
        RoundStatus.COMPLETED -> RoundEntity.STATUS_COMPLETED
        RoundStatus.FAILED -> RoundEntity.STATUS_FAILED
    },
    expectedContributors = 0,
    actualContributors = 0,
    payoutAmount = if (status == RoundStatus.COMPLETED) collectedAmount else null,
    serviceFee = 0L,
    penaltyAmount = 0L,
    startedAt = startedAt,
    dueDate = contributionDeadline ?: System.currentTimeMillis(),
    payoutInitiatedAt = if (status == RoundStatus.PAYOUT) System.currentTimeMillis() else null,
    completedAt = completedAt,
    payoutTxHash = payoutTransactionHash,
    payoutTxId = payoutTransactionHash,
    payoutConfirmations = if (payoutTransactionHash != null) 1 else 0,
    notes = null,
    ipfsHash = null,
    isDirty = true,
    lastSyncedAt = null,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)

private fun RoundEntity.toDomain() = Round(
    id = id,
    roscaId = roscaId,
    roundNumber = roundNumber,
    recipientId = recipientMemberId,
    recipientAddress = recipientAddress,
    targetAmount = targetAmount,
    collectedAmount = collectedAmount,
    bidAmount = null,
    status = when (status.uppercase()) {
        RoundEntity.STATUS_ACTIVE -> RoundStatus.CONTRIBUTION
        RoundEntity.STATUS_PAYOUT -> RoundStatus.PAYOUT
        RoundEntity.STATUS_COMPLETED -> RoundStatus.COMPLETED
        RoundEntity.STATUS_FAILED -> RoundStatus.FAILED
        RoundEntity.STATUS_CANCELLED -> RoundStatus.FAILED
        else -> RoundStatus.CONTRIBUTION
    },
    biddingDeadline = null,
    startedAt = startedAt,
    contributionDeadline = dueDate,
    payoutTransactionHash = payoutTxHash ?: payoutTxId,
    completedAt = completedAt
)

private fun Dividend.toEntity() = DividendEntity(
    id = id,
    roundId = roundId,
    memberId = memberId,
    amount = amount,
    transactionHash = transactionHash,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis()
)

private fun DividendEntity.toDomain() = Dividend(
    id = id,
    roundId = roundId,
    memberId = memberId,
    amount = amount,
    transactionHash = transactionHash,
    createdAt = createdAt
)

private fun Distribution.toEntity() = DistributionEntity(
    id = id,
    roscaId = roscaId,
    roundId = roundId,
    roundNumber = roundNumber,
    recipientId = recipientId,
    recipientAddress = recipientAddress,
    amount = amount,
    txHash = txHash,
    txId = txHash,
    status = status.name.lowercase(),
    createdAt = createdAt,
    confirmedAt = confirmedAt,
    updatedAt = System.currentTimeMillis()
)

private fun DistributionEntity.toDomain() = Distribution(
    id = id,
    roscaId = roscaId,
    roundId = roundId,
    roundNumber = roundNumber,
    recipientId = recipientId,
    recipientAddress = recipientAddress,
    amount = amount,
    txHash = txHash ?: txId,
    status = try {
        DistributionStatus.valueOf(status.uppercase())
    } catch (e: Exception) {
        DistributionStatus.PENDING
    },
    createdAt = createdAt,
    confirmedAt = confirmedAt
)
