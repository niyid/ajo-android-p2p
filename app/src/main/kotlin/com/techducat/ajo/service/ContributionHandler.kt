package com.techducat.ajo.service

import android.content.Context
import com.m2049r.xmrwallet.data.TxData
import com.m2049r.xmrwallet.model.PendingTransaction
import com.m2049r.xmrwallet.model.Wallet
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.ContributionEntity
import com.techducat.ajo.dlt.DLTProvider
import com.techducat.ajo.dlt.ContributionRecord
import com.techducat.ajo.model.Rosca
import com.techducat.ajo.util.Logger
import com.techducat.ajo.wallet.WalletSuite
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap
import com.m2049r.xmrwallet.model.TransactionInfo

/**
 * Handles contribution operations for ROSCA roscas
 * NOTE: P2P distribution methods removed - now handled by RoscaManager with database coordination
 */
class ContributionHandler(
    private val context: Context,
    private val walletSuite: WalletSuite,
    private val dltProvider: DLTProvider,
    private val database: AjoDatabase
) {
    companion object {
        private const val TAG = "com.techducat.ajo.service.ContributionHandler"
        private const val CONTRIBUTION_TIMEOUT_MS = 300000L
    }
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val prefs = context.getSharedPreferences("contribution_prefs", Context.MODE_PRIVATE)
    
    var roscaManager: RoscaManager? = null
    
    private val pendingContributions = ConcurrentHashMap<String, ContributionState>()
    
    private val _contributionEvents = MutableSharedFlow<ContributionEvent>(replay = 10)
    val contributionEvents: SharedFlow<ContributionEvent> = _contributionEvents.asSharedFlow()
    
    init {
        startOperationMonitoring()
        restorePendingOperations()
    }

    private suspend fun getTransactionInfo(txHash: String): TransactionInfo? {
        return try {
            val wallet = walletSuite.userWallet ?: return null
            wallet.history?.all?.find { it.hash == txHash }
        } catch (e: Exception) {
            Logger.e("$TAG: Error getting transaction info", e)
            null
        }
    }

    suspend fun retryFailedDLTRecordings() {
        try {
            val unsyncedContributions = database.contributionDao().getDirtyContributions()
            Logger.i("$TAG: Retrying ${unsyncedContributions.size} failed DLT recordings")
            
            for (contribution in unsyncedContributions) {
                try {
                    val record = ContributionRecord(
                        id = contribution.id,
                        roscaId = contribution.roscaId,
                        memberId = contribution.memberId,
                        amount = contribution.amount,
                        roundNumber = contribution.cycleNumber,
                        dueDate = contribution.dueDate,
                        txHash = contribution.txHash ?: ""
                    )
                    
                    val result = dltProvider.recordContribution(record)
                    
                    if (result.isSuccess) {
                        updateContributionDLTStatus(contribution.id, true)
                        Logger.i("$TAG: Successfully synced contribution ${contribution.id} to DLT")
                    }
                } catch (e: Exception) {
                    Logger.e("$TAG: Failed to retry DLT recording for ${contribution.id}", e)
                }
            }
        } catch (e: Exception) {
            Logger.e("$TAG: Error in retryFailedDLTRecordings", e)
        }
    }

    suspend fun retryContribution(contributionId: String): ContributionResult {
        val contribution = database.contributionDao().getContributionById(contributionId)
            ?: return ContributionResult.error("Contribution not found")
        
        if (contribution.status != "failed") {
            return ContributionResult.error("Can only retry failed contributions")
        }
        
        val roscaStatus = roscaManager?.getRoscaStatus(contribution.roscaId)
            ?: return ContributionResult.error("ROSCA not found")

        return contributeToRosca(roscaStatus.rosca, contribution.memberId, contribution.amount)
    }

    suspend fun cancelContribution(contributionId: String): Boolean {
        return try {
            val state = pendingContributions[contributionId]
            
            if (state != null && state.status == "creating") {
                state.status = "cancelled"
                pendingContributions.remove(contributionId)
                
                val contribution = database.contributionDao().getContributionById(contributionId)
                if (contribution != null) {
                    database.contributionDao().update(
                        contribution.copy(
                            status = "cancelled",
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
                
                Logger.i("$TAG: Contribution $contributionId cancelled")
                true
            } else {
                Logger.w("$TAG: Cannot cancel contribution $contributionId - status: ${state?.status}")
                false
            }
        } catch (e: Exception) {
            Logger.e("$TAG: Error cancelling contribution", e)
            false
        }
    }
    
    suspend fun contributeToRosca(
        rosca: Rosca,
        userId: String,
        amount: Long? = null
    ): ContributionResult = withContext(Dispatchers.IO) {
        return@withContext try {
            Logger.i("$TAG: Starting contribution for user $userId to ROSCA ${rosca.roscaId}")
            
            val validation = validateContribution(rosca, userId, amount)
            if (!validation.isValid) {
                return@withContext ContributionResult.error(validation.errorMessage!!)
            }
            
            val contributionAmount = amount ?: rosca.contributionAmount
            
            val userWallet = walletSuite.userWallet
                ?: return@withContext ContributionResult.error("Wallet not found")
            
            val balance = userWallet.unlockedBalance
            if (balance < contributionAmount) {
                Logger.w("$TAG: Insufficient balance. Required: $contributionAmount, Available: $balance")
                return@withContext ContributionResult.error(
                    "Insufficient balance. Required: ${formatAmount(contributionAmount)}, Available: ${formatAmount(balance)}"
                )
            }
            
            val multisigAddress = rosca.multisigAddress
                ?: return@withContext ContributionResult.error("Multisig wallet not ready")
            
            val contributionId = generateContributionId(rosca.roscaId, userId)
            
            val state = ContributionState(
                contributionId = contributionId,
                roscaId = rosca.roscaId,
                userId = userId,
                amount = contributionAmount,
                roundNumber = rosca.currentRound,
                status = "creating",
                createdAt = System.currentTimeMillis()
            )
            pendingContributions[contributionId] = state
            
            val txResult = createAndSendContribution(
                userWallet = userWallet,
                destinationAddress = multisigAddress,
                amount = contributionAmount,
                contributionId = contributionId
            )
            
            if (txResult.isFailure) {
                state.status = "failed"
                state.errorMessage = txResult.exceptionOrNull()?.message
                pendingContributions.remove(contributionId)
                return@withContext ContributionResult.error(state.errorMessage ?: "Transaction failed")
            }
            
            val txHash = txResult.getOrThrow()
            state.txHash = txHash
            state.status = "pending"
            
            saveContribution(rosca, userId, contributionAmount, txHash, contributionId)
            recordContributionOnDLT(rosca, userId, contributionAmount, txHash, contributionId)
            
            _contributionEvents.emit(
                ContributionEvent.ContributionCreated(
                    contributionId = contributionId,
                    roscaId = rosca.roscaId,
                    userId = userId,
                    amount = contributionAmount,
                    txHash = txHash
                )
            )
            
            Logger.i("$TAG: Contribution sent successfully. TX: $txHash")
            ContributionResult.success(txHash, contributionAmount, contributionId)
            
        } catch (e: Exception) {
            Logger.e("$TAG: Error contributing to ROSCA", e)
            ContributionResult.error("Error: ${e.message}")
        }
    }

    suspend fun verifyContribution(contributionId: String): VerificationResult {
        return try {
            val state = pendingContributions[contributionId]
                ?: return VerificationResult.error("Contribution not found")
            
            val txHash = state.txHash ?: return VerificationResult.error("No transaction hash")
            
            val txInfo = getTransactionInfo(txHash)
            
            if (txInfo != null) {
                val confirmations = txInfo.confirmations.toInt()
                val isConfirmed = confirmations >= 10
                
                if (isConfirmed) {
                    state.status = "confirmed"
                    state.confirmations = confirmations
                    
                    updateContributionStatus(contributionId, "confirmed", confirmations)
                    pendingContributions.remove(contributionId)
                    
                    _contributionEvents.emit(
                        ContributionEvent.ContributionConfirmed(
                            contributionId = contributionId,
                            confirmations = confirmations
                        )
                    )
                    
                    Logger.i("$TAG: Contribution $contributionId confirmed with $confirmations confirmations")
                    VerificationResult.success(confirmations, isConfirmed)
                } else {
                    state.confirmations = confirmations
                    VerificationResult.success(confirmations, false)
                }
            } else {
                VerificationResult.error("Transaction not found")
            }
        } catch (e: Exception) {
            Logger.e("$TAG: Error verifying contribution", e)
            VerificationResult.error(e.message ?: "Verification failed")
        }
    }
    
    suspend fun getContributionStatus(contributionId: String): ContributionStatus {
        val state = pendingContributions[contributionId]
        
        return if (state != null) {
            ContributionStatus(
                contributionId = contributionId,
                status = state.status,
                txHash = state.txHash,
                confirmations = state.confirmations,
                amount = state.amount,
                errorMessage = state.errorMessage
            )
        } else {
            val contribution = withContext(Dispatchers.IO) {
                database.contributionDao().getContributionById(contributionId)
            }
            
            if (contribution != null) {
                ContributionStatus(
                    contributionId = contributionId,
                    status = contribution.status,
                    txHash = contribution.txHash,
                    confirmations = 10,
                    amount = contribution.amount,
                    errorMessage = null
                )
            } else {
                ContributionStatus(
                    contributionId = contributionId,
                    status = "not_found",
                    txHash = null,
                    confirmations = 0,
                    amount = 0,
                    errorMessage = "Contribution not found"
                )
            }
        }
    }
    
    private suspend fun createAndSendContribution(
        userWallet: Wallet,
        destinationAddress: String,
        amount: Long,
        contributionId: String
    ): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val txData = TxData().apply {
                destination = destinationAddress
                this.amount = amount
            }
            
            Logger.d("$TAG: Creating transaction to $destinationAddress for $amount")
            
            val tx = userWallet.createTransaction(txData)
            
            if (tx.status != PendingTransaction.Status.Status_Ok) {
                Logger.e("$TAG: Transaction creation failed: ${tx.errorString}")
                return@withContext Result.failure(Exception("Transaction failed: ${tx.errorString}"))
            }
            
            Logger.d("$TAG: Transaction created. Fee: ${tx.fee}")
            
            val success = tx.commit("", true)
            
            if (!success) {
                Logger.e("$TAG: Failed to broadcast transaction")
                return@withContext Result.failure(Exception("Failed to broadcast transaction"))
            }
            
            val txHash = tx.firstTxId
            Logger.i("$TAG: Transaction broadcasted successfully: $txHash")
            
            Result.success(txHash)
            
        } catch (e: Exception) {
            Logger.e("$TAG: Exception creating transaction", e)
            Result.failure(e)
        }
    }
    
    private suspend fun saveContribution(
        rosca: Rosca,
        userId: String,
        amount: Long,
        txHash: String,
        contributionId: String
    ) {
        try {
            val contribution = ContributionEntity(
                id = contributionId,
                roscaId = rosca.roscaId,
                memberId = userId,
                amount = amount,
                cycleNumber = rosca.currentRound,
                status = "pending",
                dueDate = System.currentTimeMillis(),
                txHash = txHash,
                txId = null,
                proofOfPayment = null,
                verifiedAt = null,
                notes = null,
                createdAt = System.currentTimeMillis(),
                isDirty = true
            )
            
            database.contributionDao().insert(contribution)
            Logger.d("$TAG: Contribution saved to database")
            
        } catch (e: Exception) {
            Logger.e("$TAG: Error saving contribution to database", e)
        }
    }
    
    private suspend fun recordContributionOnDLT(
        rosca: Rosca,
        userId: String,
        amount: Long,
        txHash: String,
        contributionId: String
    ) {
        scope.launch {
            try {
                val record = ContributionRecord(
                    id = contributionId,
                    roscaId = rosca.roscaId,
                    memberId = userId,
                    amount = amount,
                    roundNumber = rosca.currentRound,
                    dueDate = System.currentTimeMillis(),
                    txHash = txHash
                )
                
                val result = dltProvider.recordContribution(record)
                
                if (result.isSuccess) {
                    Logger.i("$TAG: Contribution recorded on DLT")
                    updateContributionDLTStatus(contributionId, true)
                } else {
                    Logger.e("$TAG: Failed to record contribution on DLT: ${result.exceptionOrNull()?.message}")
                    updateContributionDLTStatus(contributionId, false)
                }
                
            } catch (e: Exception) {
                Logger.e("$TAG: Error recording contribution on DLT", e)
            }
        }
    }
    
    private suspend fun updateContributionDLTStatus(
        contributionId: String,
        synced: Boolean
    ) {
        try {
            val contribution = database.contributionDao().getContributionById(contributionId)
            if (contribution != null) {
                val updated = contribution.copy(
                    isDirty = !synced,
                    updatedAt = System.currentTimeMillis()
                )
                database.contributionDao().update(updated)
                Logger.d("$TAG: Updated DLT sync status for contribution $contributionId: synced=$synced")
            }
        } catch (e: Exception) {
            Logger.e("$TAG: Error updating contribution DLT status", e)
        }
    }    
    
    private suspend fun updateContributionStatus(
        contributionId: String,
        status: String,
        confirmations: Int
    ) {
        try {
            val contribution = database.contributionDao().getContributionById(contributionId)
            if (contribution != null) {
                val updated = contribution.copy(
                    status = status,
                    verifiedAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                database.contributionDao().update(updated)
                
                Logger.d("$TAG: Updated contribution $contributionId status to $status")
            }
        } catch (e: Exception) {
            Logger.e("$TAG: Error updating contribution status", e)
        }
    }
    
    private suspend fun validateContribution(rosca: Rosca, userId: String, amount: Long?): ValidationResult {
        if (rosca.status != Rosca.RoscaState.ACTIVE) {
            return ValidationResult.invalid("ROSCA is not active")
        }
        
        val isMember = rosca.members.any { member -> member.id == userId }
        
        if (!isMember) {
            return ValidationResult.invalid("User is not a member of this ROSCA")
        }
        
        val contributionAmount = amount ?: rosca.contributionAmount
        if (contributionAmount <= 0) {
            return ValidationResult.invalid("Invalid contribution amount")
        }
        
        val existingContribution = database.contributionDao().getContributionByMemberAndCycle(
            userId,
            rosca.roscaId,
            rosca.currentRound
        )
        
        if (existingContribution != null && existingContribution.status != "failed") {
            return ValidationResult.invalid("Already contributed for this round")
        }
        
        return ValidationResult.valid()
    }
    
    private fun startOperationMonitoring() {
        scope.launch {
            while (isActive) {
                try {
                    monitorPendingContributions()
                    delay(10000)
                } catch (e: Exception) {
                    Logger.e("$TAG: Error in operation monitoring", e)
                }
            }
        }
    }
    
    private suspend fun monitorPendingContributions() {
        val iterator = pendingContributions.entries.iterator()
        while (iterator.hasNext()) {
            val (id, state) = iterator.next()
            
            try {
                val age = System.currentTimeMillis() - state.createdAt
                if (age > CONTRIBUTION_TIMEOUT_MS && state.status == "pending") {
                    verifyContribution(id)
                } else if (age > CONTRIBUTION_TIMEOUT_MS * 2) {
                    iterator.remove()
                    Logger.w("$TAG: Removed stale contribution $id")
                }
            } catch (e: Exception) {
                Logger.e("$TAG: Error monitoring contribution $id", e)
            }
        }
    }
    
    private fun restorePendingOperations() {
        scope.launch {
            try {
                val pendingContribs = database.contributionDao().getPendingContributions()
                for (contrib in pendingContribs) {
                    val state = ContributionState(
                        contributionId = contrib.id,
                        roscaId = contrib.roscaId,
                        userId = contrib.memberId,
                        amount = contrib.amount,
                        roundNumber = contrib.cycleNumber,
                        status = contrib.status,
                        txHash = contrib.txHash,
                        createdAt = contrib.createdAt
                    )
                    pendingContributions[contrib.id] = state
                }
                
                Logger.i("$TAG: Restored ${pendingContribs.size} pending contributions")
                
            } catch (e: Exception) {
                Logger.e("$TAG: Error restoring pending operations", e)
            }
        }
    }
    
    private fun generateContributionId(roscaId: String, userId: String): String {
        return "contrib_${roscaId}_${userId}_${System.currentTimeMillis()}"
    }
    
    private fun formatAmount(amount: Long): String {
        val xmr = amount.toDouble() / 1e12
        return String.format("%.12f XMR", xmr)
    }
    
    fun cleanup() {
        scope.cancel()
        pendingContributions.clear()
    }
}

data class ContributionState(
    val contributionId: String,
    val roscaId: String,
    val userId: String,
    val amount: Long,
    val roundNumber: Int,
    var status: String,
    var txHash: String? = null,
    var confirmations: Int = 0,
    var errorMessage: String? = null,
    val createdAt: Long
)

data class ContributionResult(
    val success: Boolean,
    val txHash: String?,
    val amount: Long,
    val contributionId: String?,
    val errorMessage: String?
) {
    companion object {
        fun success(txHash: String, amount: Long, contributionId: String) = 
            ContributionResult(true, txHash, amount, contributionId, null)
        
        fun error(message: String) = 
            ContributionResult(false, null, 0, null, message)
    }
}

data class VerificationResult(
    val success: Boolean,
    val confirmations: Int,
    val isConfirmed: Boolean,
    val errorMessage: String?
) {
    companion object {
        fun success(confirmations: Int, isConfirmed: Boolean) =
            VerificationResult(true, confirmations, isConfirmed, null)
        
        fun error(message: String) =
            VerificationResult(false, 0, false, message)
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String?
) {
    companion object {
        fun valid() = ValidationResult(true, null)
        fun invalid(message: String) = ValidationResult(false, message)
    }
}

data class ContributionStatus(
    val contributionId: String,
    val status: String,
    val txHash: String?,
    val confirmations: Int,
    val amount: Long,
    val errorMessage: String?
)

sealed class ContributionEvent {
    data class ContributionCreated(
        val contributionId: String,
        val roscaId: String,
        val userId: String,
        val amount: Long,
        val txHash: String
    ) : ContributionEvent()
    
    data class ContributionConfirmed(
        val contributionId: String,
        val confirmations: Int
    ) : ContributionEvent()
    
    data class ContributionFailed(
        val contributionId: String,
        val errorMessage: String
    ) : ContributionEvent()
}

fun Long.toXMR(): Double = this.toDouble() / 1e12
fun Double.toAtomic(): Long = (this * 1e12).toLong()
fun Long.formatAsXMR(): String = String.format("%.12f XMR", this.toXMR())
fun ContributionState.isRecent(): Boolean = (System.currentTimeMillis() - createdAt) < 300000
