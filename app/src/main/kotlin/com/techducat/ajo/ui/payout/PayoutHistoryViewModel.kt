package com.techducat.ajo.ui.payout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.PayoutEntity
import com.techducat.ajo.model.Payout
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class PayoutHistoryUiState {
    object Loading : PayoutHistoryUiState()
    data class Success(
        val payouts: List<Payout>,
        val totalPaid: Long,
        val totalPenalties: Long
    ) : PayoutHistoryUiState()
    data class Error(val message: String) : PayoutHistoryUiState()
}

class PayoutHistoryViewModel(
    private val database: AjoDatabase
) : ViewModel() {
    
    // Fix: Get the DAO from database - verify the actual method name in AjoDatabase
    private val payoutDao = database.payoutDao()
    
    private val _uiState = MutableStateFlow<PayoutHistoryUiState>(PayoutHistoryUiState.Loading)
    val uiState: StateFlow<PayoutHistoryUiState> = _uiState.asStateFlow()
    
    private var currentRoscaId: String? = null
    private var currentMemberId: String? = null
    
    fun loadPayoutsByRosca(roscaId: String) {
        currentRoscaId = roscaId
        viewModelScope.launch {
            try {
                _uiState.value = PayoutHistoryUiState.Loading
                // Fix: Explicitly specify types in the Flow transformation
                payoutDao.observeByRoscaId(roscaId)
                    .map { payoutEntities: List<PayoutEntity> ->
                        payoutEntities.map { it.toDomainModel() }
                    }
                    .collect { payouts: List<Payout> ->
                        val totalPaid = payoutDao.getTotalPayoutsByRosca(
                            roscaId, 
                            PayoutEntity.STATUS_COMPLETED
                        ) ?: 0L
                        val totalPenalties = payoutDao.getTotalPenaltiesByRosca(roscaId) ?: 0L
                        _uiState.value = PayoutHistoryUiState.Success(payouts, totalPaid, totalPenalties)
                    }
            } catch (e: Exception) {
                _uiState.value = PayoutHistoryUiState.Error(e.message ?: "Failed to load payouts")
            }
        }
    }
    
    fun loadPayoutsByMember(memberId: String) {
        currentMemberId = memberId
        viewModelScope.launch {
            try {
                _uiState.value = PayoutHistoryUiState.Loading
                // Fix: Explicitly specify types in the Flow transformation
                payoutDao.observeByRecipientId(memberId)
                    .map { payoutEntities: List<PayoutEntity> ->
                        payoutEntities.map { it.toDomainModel() }
                    }
                    .collect { payouts: List<Payout> ->
                        val totalPaid = payouts.filter { it.isCompleted() }.sumOf { it.netAmount }
                        val totalPenalties = payouts.sumOf { it.penaltyAmount }
                        _uiState.value = PayoutHistoryUiState.Success(payouts, totalPaid, totalPenalties)
                    }
            } catch (e: Exception) {
                _uiState.value = PayoutHistoryUiState.Error(e.message ?: "Failed to load payouts")
            }
        }
    }
    
    fun refresh() {
        currentRoscaId?.let { loadPayoutsByRosca(it) }
        currentMemberId?.let { loadPayoutsByMember(it) }
    }
    
    // Extension function to convert PayoutEntity to domain Payout model
    private fun PayoutEntity.toDomainModel(): Payout {
        return Payout(
            id = id,
            roscaId = roscaId,
            recipientId = recipientId,
            roundId = roundId,
            payoutType = Payout.PayoutType.valueOf(payoutType.uppercase().replace("_", "_")),
            grossAmount = grossAmount,
            serviceFee = serviceFee,
            penaltyAmount = penaltyAmount,
            netAmount = netAmount,
            txHash = txHash,
            txId = txId,
            recipientAddress = recipientAddress,
            status = Payout.PayoutStatus.valueOf(status.uppercase()),
            initiatedAt = initiatedAt,
            completedAt = completedAt,
            failedAt = failedAt,
            errorMessage = errorMessage,
            confirmations = confirmations,
            verifiedAt = verifiedAt,
            notes = notes,
            createdAt = createdAt
            // Removed: updatedAt, ipfsHash, lastSyncedAt, isDirty - not in Payout domain model
        )
    }
}
