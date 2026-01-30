package com.techducat.ajo.data.local.entity

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.techducat.ajo.model.Member

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey val id: String,
    val roscaId: String,
    var userId: String,
    var name: String,
    var moneroAddress: String?,
    var joinedAt: Long,
    var position: Int,
    var leftAt: Long,
    var leftReason: String,
    var isActive: Boolean,
    val walletAddress: String? = null,
    val payoutOrderPosition: Int? = null,
    val hasReceivedPayout: Boolean = false,
    val totalContributed: Long = 0,
    val missedPayments: Int = 0,
    val lastContributionAt: Long? = null,
    val exitedAt: Long? = null,
    val updatedAt: Long? = null,
    val ipfsHash: String? = null,
    val lastSyncedAt: Long? = null,
    val isDirty: Boolean = false,
    val status: String? = "active", // Make nullable
    
    // ⚠️ ADDED: The critical missing field for multisig functionality
    val multisigInfo: Member.MultisigInfo? = null,
    val hasReceived: Boolean = false
) {
    companion object {
        private const val TAG = "MemberEntity"
        
        const val INVITE_ACCEPTED = "accepted"
        const val INVITE_PENDING = "pending"
        const val INVITE_REJECTED = "rejected"
        
        const val INVITE_TYPE_BLANKET = "blanket"
        const val INVITE_TYPE_DIRECT = "direct"
        const val INVITE_TYPE_TARGETED = "targeted"
        const val INVITE_TYPE_GROUP = "rosca"
    }
}

// Extension functions for converting between Entity and Domain models
fun MemberEntity.toDomain(): Member {
    Log.d("MemberEntity", "toDomain conversion:")
    Log.d("MemberEntity", "  ID: $id")
    Log.d("MemberEntity", "  UserId: $userId")
    Log.d("MemberEntity", "  Name: $name")
    Log.d("MemberEntity", "  MultisigInfo before conversion: ${multisigInfo != null}")
    
    // ✅ FIX: Determine correct wallet address (now returns non-null String)
    val resolvedWalletAddress: String = when {
        // Priority 1: Use multisig address if available (most reliable)
        multisigInfo?.address?.isNotBlank() == true -> multisigInfo.address!!
        // Priority 2: Use stored wallet address
        walletAddress?.isNotBlank() == true -> walletAddress!!
        // Priority 3: Fall back to monero address
        moneroAddress?.isNotBlank() == true -> moneroAddress!!
        // Priority 4: Use userId as temporary placeholder (for display purposes only)
        else -> userId
    }
    
    // ✅ FIX 1: Handle nullable status safely (FIXED - added ?. operator)
    val memberStatus = when (status?.lowercase()) {
        "active" -> Member.MemberStatus.ACTIVE
        "pending" -> Member.MemberStatus.PENDING
        "inactive" -> Member.MemberStatus.INACTIVE
        "banned" -> Member.MemberStatus.BANNED
        else -> Member.MemberStatus.ACTIVE
    }
    
    val member = Member(
        id = id,
        roscaId = roscaId,
        userId = userId,
        walletAddress = resolvedWalletAddress,
        name = name,
        multisigInfo = multisigInfo,
        position = position,
        joinedAt = joinedAt,
        isActive = isActive,
        hasReceived = hasReceivedPayout || hasReceived,
        status = memberStatus,
        lastContributionAt = lastContributionAt,
        totalContributed = totalContributed
    )
    
    Log.d("MemberEntity", "  MultisigInfo after conversion: ${member.multisigInfo != null}")
    if (member.multisigInfo != null) {
        Log.d("MemberEntity", "    Address: ${member.multisigInfo?.address?.take(20)}...")
        Log.d("MemberEntity", "    IsReady: ${member.multisigInfo?.isReady}")
    }
    Log.d("MemberEntity", "  Resolved walletAddress: ${resolvedWalletAddress.take(20)}...")
    
    return member
}

fun Member.toEntity(): MemberEntity {
    Log.d("Member", "toEntity conversion:")
    Log.d("Member", "  ID: $id")
    Log.d("Member", "  UserId: $userId")
    Log.d("Member", "  Name: $name")
    Log.d("Member", "  MultisigInfo before conversion: ${multisigInfo != null}")
    if (multisigInfo != null) {
        Log.d("Member", "    Address: ${multisigInfo?.address?.take(20)}...")
        Log.d("Member", "    IsReady: ${multisigInfo?.isReady}")
    }
    
    // ✅ FIX 2: Handle nullable walletAddress safely with safe call operator
    val fallbackMoneroAddress = if (!walletAddress.isNullOrEmpty()) walletAddress else null
    
    val entity = MemberEntity(
        id = id,
        roscaId = roscaId,
        userId = userId,
        name = name,
        moneroAddress = fallbackMoneroAddress,
        walletAddress = walletAddress, // ✅ FIX 3: This is correctly nullable
        joinedAt = joinedAt,
        position = position,
        leftAt = 0L,
        leftReason = "",
        isActive = isActive,
        multisigInfo = multisigInfo,  // TypeConverter handles JSON serialization automatically
        hasReceivedPayout = hasReceived,
        hasReceived = hasReceived,
        totalContributed = totalContributed,
        lastContributionAt = lastContributionAt,
        status = status.name.lowercase(),
        updatedAt = System.currentTimeMillis(),
        isDirty = false
    )
    
    Log.d("Member", "  MultisigInfo after conversion: ${entity.multisigInfo != null}")
    
    return entity
}
