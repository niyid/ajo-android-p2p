package com.techducat.ajo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Member(
    val id: String = UUID.randomUUID().toString(),
    val memberId: String = id,  // Alias for compatibility
    var roscaId: String = "",
    var userId: String = "",
    var walletAddress: String = "",
    var name: String = "",
    var displayName: String = name,  // Alias for compatibility
    var multisigInfo: MultisigInfo? = null,  // ⚠️ Changed to MultisigInfo? type
    var position: Int = -1,
    var joinedAt: Long = System.currentTimeMillis(),
    var isActive: Boolean = true,
    var hasReceived: Boolean = false,
    var status: MemberStatus = MemberStatus.ACTIVE,
    var lastContributionAt: Long? = null,
    var totalContributed: Long = 0L
) : Parcelable {
    
    enum class MemberStatus {
        PENDING,
        ACTIVE,
        INACTIVE,
        BANNED
    }
    
    @Parcelize
    data class MultisigInfo(
        val address: String,
        val viewKey: String,
        val isReady: Boolean,
        val exchangeState: String
    ) : Parcelable
}
