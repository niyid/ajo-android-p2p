package com.techducat.ajo.ui.exit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.MemberEntity
import com.techducat.ajo.data.local.entity.PenaltyEntity
import com.techducat.ajo.di.ExitService
import com.techducat.ajo.model.Member
import com.techducat.ajo.model.Penalty
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ExitConfirmationUiState {
    object Loading : ExitConfirmationUiState()
    data class Calculated(val member: Member, val penalty: Penalty) : ExitConfirmationUiState()
    object Processing : ExitConfirmationUiState()
    object Success : ExitConfirmationUiState()
    data class Error(val message: String) : ExitConfirmationUiState()
}

class ExitConfirmationViewModel(
    private val database: AjoDatabase,
    private val exitService: ExitService
) : ViewModel() {
    
    private val memberDao = database.memberDao()
    private val contributionDao = database.contributionDao()
    private val roscaDao = database.roscaDao()
    
    private val _uiState = MutableStateFlow<ExitConfirmationUiState>(ExitConfirmationUiState.Loading)
    val uiState: StateFlow<ExitConfirmationUiState> = _uiState.asStateFlow()
    
    fun calculatePenalty(memberId: String, roscaId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ExitConfirmationUiState.Loading
                
                // Get member entity
                val memberEntity = memberDao.getById(memberId)
                    ?: throw Exception("Member not found")
                
                // Get contribution history
                val contributions = contributionDao.getByMemberAndRosca(memberId, roscaId)
                val totalContributed = contributions.sumOf { it.amount }
                
                // Get ROSCA info
                val rosca = roscaDao.getById(roscaId)
                    ?: throw Exception("ROSCA not found")
                
                val cyclesParticipated = contributions.size
                val cyclesRemaining = rosca.totalCycles - cyclesParticipated
                
                // Calculate penalty (10% default)
                val penaltyPercentage = 0.10
                val penaltyAmount = (totalContributed * penaltyPercentage).toLong()
                val reimbursementAmount = totalContributed - penaltyAmount
                
                // Create penalty entity
                val penaltyEntity = PenaltyEntity(
                    id = "penalty_${System.currentTimeMillis()}_${memberId}",
                    roscaId = roscaId,
                    memberId = memberId,
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
                    exitReason = null,
                    status = PenaltyEntity.STATUS_CALCULATED,
                    appliedAt = null,
                    waivedAt = null,
                    waivedBy = null,
                    waiverReason = null,
                    notes = null,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = null
                )
                
                // Convert to domain models
                val member = memberEntity.toDomainModel()
                val penalty = penaltyEntity.toDomainModel()
                
                _uiState.value = ExitConfirmationUiState.Calculated(member, penalty)
                
            } catch (e: Exception) {
                _uiState.value = ExitConfirmationUiState.Error(
                    e.message ?: "Failed to calculate penalty"
                )
            }
        }
    }
    
    fun confirmExit(memberId: String, roscaId: String, exitReason: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ExitConfirmationUiState.Processing
                
                val result = exitService.processEarlyExit(memberId, roscaId, exitReason)
                
                if (result.isSuccess) {
                    _uiState.value = ExitConfirmationUiState.Success
                } else {
                    _uiState.value = ExitConfirmationUiState.Error(
                        result.exceptionOrNull()?.message ?: "Failed to process exit"
                    )
                }
                
            } catch (e: Exception) {
                _uiState.value = ExitConfirmationUiState.Error(
                    e.message ?: "Failed to process exit"
                )
            }
        }
    }
    
    // Extension functions to convert entities to domain models
    private fun MemberEntity.toDomainModel(): Member {
        return Member(
            id = id,
            roscaId = roscaId,
            userId = userId,
            walletAddress = walletAddress ?: "",
            name = name ?: "Unknown",
            position = payoutOrderPosition ?: -1,
            joinedAt = joinedAt,
            isActive = status == "active",
            hasReceived = hasReceivedPayout ?: false,
            status = try {
                // ✅ FIX: Handle nullable status safely with ?. operator
                Member.MemberStatus.valueOf(status?.uppercase() ?: "ACTIVE")
            } catch (e: Exception) {
                Member.MemberStatus.ACTIVE  // Default status
            },
            lastContributionAt = lastContributionAt,
            totalContributed = totalContributed ?: 0L
        )
    }

    private fun PenaltyEntity.toDomainModel(): Penalty {
        return Penalty(
            id = id,
            roscaId = roscaId,
            memberId = memberId,
            payoutId = payoutId,
            penaltyType = try {
                Penalty.PenaltyType.valueOf(penaltyType.uppercase().replace("-", "_"))
            } catch (e: Exception) {
                Penalty.PenaltyType.EARLY_EXIT  // Default type
            },
            totalContributed = totalContributed,
            cyclesParticipated = cyclesParticipated,
            cyclesRemaining = cyclesRemaining,
            penaltyPercentage = penaltyPercentage,
            penaltyAmount = penaltyAmount,
            reimbursementAmount = reimbursementAmount,
            calculationMethod = try {
                Penalty.CalculationMethod.valueOf(calculationMethod.uppercase())
            } catch (e: Exception) {
                Penalty.CalculationMethod.PERCENTAGE  // Default method
            },
            reason = reason,
            exitReason = exitReason,
            status = try {
                Penalty.PenaltyStatus.valueOf(status.uppercase())
            } catch (e: Exception) {
                Penalty.PenaltyStatus.CALCULATED  // Default status
            },
            appliedAt = appliedAt,
            waivedAt = waivedAt,
            waivedBy = waivedBy,
            waiverReason = waiverReason,
            notes = notes,
            createdAt = createdAt
        )
    }
}
