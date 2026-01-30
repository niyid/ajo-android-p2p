package com.techducat.ajo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Rosca(
    val id: String = UUID.randomUUID().toString(),
    val roscaId: String = id,  // Alias for compatibility
    var name: String = "",
    var description: String = "",
    var creatorId: String = "",
    var totalMembers: Int = 0,
    val currentMembers: Int = 0,
    var contributionAmount: Long = 0L,
    var frequencyDays: Int = 7,
    var distributionMethod: DistributionMethod = DistributionMethod.PREDETERMINED,
    var multisigAddress: String? = null,
    val roscaWalletPath: String?,
    var status: RoscaState = RoscaState.SETUP,
    var currentRound: Int = 0,
    var createdAt: Long = System.currentTimeMillis(),
    var startedAt: Long? = null,
    var completedAt: Long? = null,
    var members: List<Member> = emptyList(),  // For in-memory operations
    var rounds: List<Round> = emptyList()  // For in-memory operations
) : Parcelable {
    
    enum class DistributionMethod {
        PREDETERMINED,
        LOTTERY,
        BIDDING
    }
    
    enum class RoscaState {
        SETUP,
        ACTIVE,
        PAUSED,
        COMPLETED,
        CANCELLED
    }
    
    /**
     * Calculate progress (0.0 to 1.0)
     */
    fun getProgress(): Float {
        if (totalMembers == 0) return 0f
        return currentRound.toFloat() / totalMembers.toFloat()
    }
}
