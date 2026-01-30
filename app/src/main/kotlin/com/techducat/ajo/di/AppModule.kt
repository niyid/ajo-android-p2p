package com.techducat.ajo.di

import android.content.Context
import androidx.room.Room
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.MemberEntity
import com.techducat.ajo.data.local.entity.PenaltyEntity
import com.techducat.ajo.data.local.entity.PayoutEntity
import com.techducat.ajo.ui.payout.PayoutHistoryViewModel
import com.techducat.ajo.ui.exit.ExitConfirmationViewModel
import com.techducat.ajo.util.Logger
import com.techducat.ajo.wallet.WalletSuite

// Coroutines
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

// Koin
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

// DLT & Blockchain
import com.techducat.ajo.dlt.DLTProvider
import com.techducat.ajo.dlt.MoneroBlockchainProvider
import com.techducat.ajo.dlt.IPFSProvider
import com.techducat.ajo.dlt.RoscaMetadata
import com.techducat.ajo.dlt.ContributionRecord
import com.techducat.ajo.dlt.DistributionRecord

// Repositories
import com.techducat.ajo.repository.RoscaRepository
import com.techducat.ajo.repository.UserRepository
import com.techducat.ajo.repository.WalletRepository
import com.techducat.ajo.repository.impl.RoscaRepositoryImpl
import com.techducat.ajo.repository.impl.UserRepositoryImpl
import com.techducat.ajo.repository.impl.WalletRepositoryImpl

// Services
import com.techducat.ajo.service.ReferralHandler
import com.techducat.ajo.service.RoscaManager
import com.techducat.ajo.service.ContributionHandler
import com.techducat.ajo.service.DistributionSelector
import com.techducat.ajo.service.fee.ServiceFeeManager
import com.techducat.ajo.service.auth.GoogleAuthService

// Utils
import com.techducat.ajo.util.SecureStorage

// UI
import com.techducat.ajo.ui.auth.LoginViewModel

// ============================================================
// Blockchain Event Definitions
// ============================================================

sealed class BlockchainEvent {
    data class RoscaStored(val rosca: RoscaMetadata, val txHash: String, val ipfsHash: String) : BlockchainEvent()
    data class RoscaUpdated(val roscaId: String, val txHash: String) : BlockchainEvent()
    data class ContributionRecorded(val contribution: ContributionRecord, val txHash: String) : BlockchainEvent()
    data class DistributionCompleted(val distribution: DistributionRecord, val txHash: String) : BlockchainEvent()
    data class MemberAdded(val roscaId: String, val memberId: String, val txHash: String) : BlockchainEvent()
    data class MemberRemoved(val roscaId: String, val memberId: String, val txHash: String) : BlockchainEvent()
    data class TransactionConfirmed(val txHash: String, val confirmations: Int) : BlockchainEvent()
}

// ============================================================
// DLT Update Definitions
// ============================================================

sealed class DLTUpdate {
    data class RoscaStored(val rosca: RoscaMetadata) : DLTUpdate()
    data class RoscaUpdated(val roscaId: String, val txHash: String) : DLTUpdate()
    data class ContributionAdded(val contribution: ContributionRecord) : DLTUpdate()
    data class DistributionCompleted(val distribution: DistributionRecord) : DLTUpdate()
    data class MemberAdded(val roscaId: String, val memberId: String) : DLTUpdate()
    data class MemberRemoved(val roscaId: String, val memberId: String) : DLTUpdate()
}

// ============================================================
// Service Definitions
// ============================================================

class ExitService(
    private val database: AjoDatabase,
    private val dltProvider: DLTProvider,
    private val walletSuite: WalletSuite,
    private val scope: CoroutineScope
) {
    private val memberDao = database.memberDao()
    private val penaltyDao = database.penaltyDao()
    private val payoutDao = database.payoutDao()
    private val contributionDao = database.contributionDao()
    private val roscaDao = database.roscaDao()
    
    suspend fun processEarlyExit(
        memberId: String,
        roscaId: String,
        exitReason: String
    ): Result<String> {
        return try {
            Logger.d("ExitService: Processing early exit for member $memberId")
            
            val member = memberDao.getById(memberId)
                ?: throw Exception("Member not found")
            
            val penalty = calculateExitPenalty(member, roscaId, exitReason)
            penaltyDao.insert(penalty)
            
            val payout = createExitPayout(member, penalty)
            payoutDao.insert(payout)
            
            penaltyDao.linkToPayout(penalty.id, payout.id, System.currentTimeMillis())
            memberDao.updateStatus(memberId, "exited", System.currentTimeMillis())
            
            penaltyDao.markAsApplied(
                penaltyId = penalty.id,
                appliedAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            val penaltyRecord = ContributionRecord(
                id = penalty.id,
                roscaId = roscaId,
                memberId = memberId,
                amount = penalty.penaltyAmount,
                roundNumber = penalty.cyclesParticipated,
                dueDate = System.currentTimeMillis(),
                txHash = "penalty_${penalty.id}"
            )
            dltProvider.recordContribution(penaltyRecord)
            
            Logger.i("ExitService: Early exit processed successfully")
            Result.success(payout.id)
            
        } catch (e: Exception) {
            Logger.e("ExitService: Error processing early exit: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    private suspend fun calculateExitPenalty(
        member: MemberEntity,
        roscaId: String,
        exitReason: String
    ): PenaltyEntity {
        val contributions = contributionDao.getByMemberAndRosca(member.id, roscaId)
        val totalContributed = contributions.sumOf { it.amount }
        
        val rosca = roscaDao.getById(roscaId)
            ?: throw Exception("ROSCA not found")
        
        val cyclesParticipated = contributions.size
        val cyclesRemaining = rosca.totalCycles - cyclesParticipated
        
        val penaltyPercentage = 0.10
        val penaltyAmount = (totalContributed * penaltyPercentage).toLong()
        val reimbursementAmount = totalContributed - penaltyAmount
        
        return PenaltyEntity(
            id = "penalty_${System.currentTimeMillis()}_${member.id}",
            roscaId = roscaId,
            memberId = member.id,
            payoutId = null,
            penaltyType = PenaltyEntity.TYPE_EARLY_EXIT,
            totalContributed = totalContributed,
            cyclesParticipated = cyclesParticipated,
            cyclesRemaining = cyclesRemaining,
            penaltyPercentage = penaltyPercentage,
            penaltyAmount = penaltyAmount,
            reimbursementAmount = reimbursementAmount,
            calculationMethod = PenaltyEntity.METHOD_PERCENTAGE,
            reason = "Early exit from ROSCA",
            exitReason = exitReason,
            status = PenaltyEntity.STATUS_CALCULATED,
            appliedAt = null,
            waivedAt = null,
            waivedBy = null,
            waiverReason = null,
            notes = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = null
        )
    }
    
    private fun createExitPayout(
        member: MemberEntity,
        penalty: PenaltyEntity
    ): PayoutEntity {
        val grossAmount = penalty.reimbursementAmount
        val serviceFee = (grossAmount * 0.05).toLong()
        val netAmount = grossAmount - serviceFee
        
        return PayoutEntity(
            id = "payout_${System.currentTimeMillis()}_${member.id}",
            roscaId = penalty.roscaId,
            recipientId = member.id,
            roundId = null,
            payoutType = PayoutEntity.TYPE_EARLY_EXIT,
            grossAmount = grossAmount,
            serviceFee = serviceFee,
            penaltyAmount = penalty.penaltyAmount,
            netAmount = netAmount,
            txHash = null,
            txId = null,
            recipientAddress = member.walletAddress ?: "",
            status = PayoutEntity.STATUS_PENDING,
            initiatedAt = System.currentTimeMillis(),
            completedAt = null,
            failedAt = null,
            errorMessage = null,
            confirmations = 0,
            verifiedAt = null,
            notes = "Early exit payout with penalty deduction",
            createdAt = System.currentTimeMillis(),
            updatedAt = null,
            ipfsHash = null,
            lastSyncedAt = null,
            isDirty = true
        )
    }
    
    suspend fun waivePenalty(
        penaltyId: String,
        waivedBy: String,
        waiverReason: String
    ): Result<Boolean> {
        return try {
            penaltyDao.markAsWaived(
                penaltyId = penaltyId,
                waivedAt = System.currentTimeMillis(),
                waivedBy = waivedBy,
                waiverReason = waiverReason,
                updatedAt = System.currentTimeMillis()
            )
            
            Logger.i("ExitService: Penalty waived: $penaltyId")
            Result.success(true)
            
        } catch (e: Exception) {
            Logger.e("ExitService: Error waiving penalty: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun processPayout(payoutId: String): Result<String> {
        return try {
            val payout = payoutDao.getById(payoutId)
                ?: throw Exception("Payout not found")
            
            payoutDao.updateStatus(
                payoutId, 
                PayoutEntity.STATUS_PROCESSING, 
                System.currentTimeMillis()
            )
            
            Logger.i("ExitService: Processing payout $payoutId for ${payout.netAmount} atomic units")
            
            val txHash = suspendCancellableCoroutine<String> { continuation ->
                val amountXmr = payout.netAmount / 1_000_000_000_000.0
                
                Logger.d("ExitService: Sending ${amountXmr} XMR to ${payout.recipientAddress}")
                
                walletSuite.sendTransaction(
                    payout.recipientAddress,
                    amountXmr,
                    object : WalletSuite.TransactionCallback {
                        override fun onSuccess(txId: String, amount: Long) {
                            Logger.i("ExitService: Transaction successful - TX: $txId")
                            continuation.resume(txId)
                        }
                        
                        override fun onError(error: String) {
                            Logger.e("ExitService: Transaction failed: $error")
                            continuation.resumeWith(Result.failure(Exception(error)))
                        }
                    }
                )
                
                continuation.invokeOnCancellation {
                    Logger.w("ExitService: Transaction cancelled for payout $payoutId")
                }
            }
            
            payoutDao.completeTransaction(
                payoutId = payoutId,
                txHash = txHash,
                txId = txHash,
                status = PayoutEntity.STATUS_COMPLETED,
                completedAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            try {
                val distributionRecord = DistributionRecord(
                    id = payoutId,
                    roscaId = payout.roscaId,
                    recipientId = payout.recipientId,
                    recipientAddress = payout.recipientAddress,
                    amount = payout.netAmount,
                    roundNumber = payout.roundId?.removePrefix("round_")?.toIntOrNull() ?: 0,
                    dueDate = System.currentTimeMillis(),
                    txHash = txHash
                )
                
                dltProvider.recordDistribution(distributionRecord)
                Logger.i("ExitService: Payout recorded to blockchain")
            } catch (e: Exception) {
                Logger.e("ExitService: Failed to record payout to DLT: ${e.message}", e)
            }
            
            Logger.i("ExitService: Payout processed successfully - TX: $txHash")
            Result.success(txHash)
            
        } catch (e: Exception) {
            Logger.e("ExitService: Error processing payout: ${e.message}", e)
            
            try {
                payoutDao.markAsFailed(
                    payoutId = payoutId,
                    status = PayoutEntity.STATUS_FAILED,
                    failedAt = System.currentTimeMillis(),
                    errorMessage = e.message,
                    updatedAt = System.currentTimeMillis()
                )
            } catch (dbError: Exception) {
                Logger.e("ExitService: Failed to mark payout as failed: ${dbError.message}", dbError)
            }
            
            Result.failure(e)
        }
    }
}

// ============================================================
// Koin Module Definition
// ============================================================

val appModule = module {
    
    // ============ Coroutine Scopes ============
    
    single<SecureStorage> {
        SecureStorage(androidContext())
    }    
    
    single<CoroutineScope>(named("AppScope")) { 
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    
    single<CoroutineScope>(named("IOScope")) {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
    
    // ============ Core Components ============
    
    single<AjoDatabase> {
        Room.databaseBuilder(
            androidContext(),
            AjoDatabase::class.java,
            "ajo_database"
        )
            .build()
    }
    
    single<WalletSuite> {
        WalletSuite.getInstance(androidContext())
    }
    
    single<IPFSProvider> {
        IPFSProvider.getInstance(androidContext())
    }
    
    // ============ Blockchain & DLT ============
    
    single<MoneroBlockchainProvider> {
        MoneroBlockchainProvider(
            walletSuite = get(),
            ipfsProvider = get()
        )
    }

    single<DLTProvider> { 
        get<MoneroBlockchainProvider>()
    }
    
    // ============ Services ============
    
    single<ServiceFeeManager> {
        ServiceFeeManager(
            context = androidContext(),
            db = get(),
            monero = get()
        )
    }   

    single<ContributionHandler> {
        ContributionHandler(
            context = androidContext(),
            walletSuite = get(),
            dltProvider = get(),
            database = get()
        )
    }

    single<DistributionSelector> {
        DistributionSelector(
            repository = get()
        )
    }

    single<RoscaManager> {
        RoscaManager(
            walletSuite = get(),
            repository = get(),
            dltProvider = get(),
            contributionHandler = get(),
            distributionSelector = get(),
            context = androidContext(),
            scope = get(named("IOScope"))
        )
    }

    single<ExitService> {
        ExitService(
            database = get(),
            dltProvider = get(),
            walletSuite = get(),
            scope = get(named("IOScope"))
        )
    }

    single<ReferralHandler> {
        ReferralHandler(
            context = androidContext(),
            database = get(),
            roscaManager = get()
        )
    }

    single<GoogleAuthService> {
        GoogleAuthService(
            context = androidContext()
        )
    }    

    // ============ DAOs ============
    
    single { get<AjoDatabase>().contributionDao() }
    single { get<AjoDatabase>().memberDao() }
    single { get<AjoDatabase>().payoutDao() }
    single { get<AjoDatabase>().penaltyDao() }
    single { get<AjoDatabase>().roscaDao() }
    single { get<AjoDatabase>().serviceFeeDao() }
    single { get<AjoDatabase>().transactionDao() }
    single { get<AjoDatabase>().userProfileDao() }
    single { get<AjoDatabase>().inviteDao() }
    single { get<AjoDatabase>().bidDao() }
    single { get<AjoDatabase>().roundDao() }
    single { get<AjoDatabase>().dividendDao() }
    single { get<AjoDatabase>().distributionDao() }
    single { get<AjoDatabase>().multisigSignatureDao() }  
    
    // ============ Repositories ============
    
    single<RoscaRepository> {
        RoscaRepositoryImpl(
            database = get()
        )
    }

    single<UserRepository> {
        UserRepositoryImpl(
            context = androidContext(),
            googleAuthService = get(),
            userProfileDao = get()
        )
    }

    single<WalletRepository> {
        WalletRepositoryImpl()
    }
    
    // ============ ViewModels ============
    
    viewModel {
        PayoutHistoryViewModel(
            database = get()
        )
    }
    
    viewModel {
        ExitConfirmationViewModel(
            database = get(),
            exitService = get()
        )
    }
    
    viewModel {
        LoginViewModel(
            userRepository = get(),
            referralHandler = get()
        )
    }    
    
    // ============ Use Cases ============
    
    factory<CreateGroupUseCase> {
        CreateGroupUseCase(
            roscaRepository = get(),
            dltProvider = get()
        )
    }
    
    factory<MakeContributionUseCase> {
        MakeContributionUseCase(
            roscaRepository = get(),
            dltProvider = get()
        )
    }
    
    factory<ProcessDistributionUseCase> {
        ProcessDistributionUseCase(
            roscaRepository = get(),
            dltProvider = get()
        )
    }
    
    factory<SyncDataUseCase> {
        SyncDataUseCase(
            roscaRepository = get(),
            dltProvider = get()
        )
    }
}

// ============================================================
// Use Cases Implementation
// ============================================================

class CreateGroupUseCase(
    private val roscaRepository: RoscaRepository,
    private val dltProvider: DLTProvider
) {
    suspend operator fun invoke(
        name: String,
        description: String,
        contributionAmount: Long,
        frequency: String,
        creatorId: String,
        totalMembers: Int
    ): Result<String> {
        return try {
            val groupId = "group_${System.currentTimeMillis()}"
            
            val metadata = RoscaMetadata(
                roscaId = groupId,
                name = name,
                description = description,
                creatorId = creatorId,
                totalMembers = totalMembers,
                contributionAmount = contributionAmount,
                contributionFrequency = frequency,
                payoutOrder = "sequential",
                startDate = System.currentTimeMillis(),
                memberIds = emptyList(),
                multisigAddress = null,
                status = "ACTIVE",
                createdAt = System.currentTimeMillis()
            )
            
            dltProvider.storeRoscaMetadata(metadata)
            
            Result.success(groupId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class MakeContributionUseCase(
    private val roscaRepository: RoscaRepository,
    private val dltProvider: DLTProvider
) {
    suspend operator fun invoke(
        groupId: String,
        memberId: String,
        amount: Long,
        cycle: Int
    ): Result<String> {
        return try {
            val contributionId = "contrib_${System.currentTimeMillis()}"
            
            val record = ContributionRecord(
                id = contributionId,
                roscaId = groupId,
                memberId = memberId,
                amount = amount,
                roundNumber = cycle,
                dueDate = System.currentTimeMillis(),
                txHash = "pending"
            )
            
            dltProvider.recordContribution(record)
            
            Result.success(contributionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class ProcessDistributionUseCase(
    private val roscaRepository: RoscaRepository,
    private val dltProvider: DLTProvider
) {
    suspend operator fun invoke(
        groupId: String,
        recipientId: String,
        cycle: Int,
        recipientAddress: String
    ): Result<String> {
        return try {
            val distributionId = "dist_${System.currentTimeMillis()}"
            val totalContributions = 0L
            
            val record = DistributionRecord(
                id = distributionId,
                roscaId = groupId,
                recipientId = recipientId,
                recipientAddress = recipientAddress,
                amount = totalContributions,
                roundNumber = cycle,
                dueDate = System.currentTimeMillis(),
                txHash = "pending"
            )
            
            dltProvider.recordDistribution(record)
            
            Result.success(distributionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class SyncDataUseCase(
    private val roscaRepository: RoscaRepository,
    private val dltProvider: DLTProvider
) {
    suspend operator fun invoke(): Result<Int> {
        return try {
            var syncedCount = 0
            
            val roscas = roscaRepository.getAllRoscas()
            for (rosca in roscas) {
                val metadataResult = dltProvider.getRoscaMetadata(rosca.id)
                if (metadataResult.isSuccess) {
                    syncedCount++
                }
            }
            
            Result.success(syncedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
