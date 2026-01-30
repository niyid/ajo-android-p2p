package com.techducat.ajo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Invite(
    val id: String = UUID.randomUUID().toString(),
    val roscaId: String,
    val inviterUserId: String,
    val inviteeEmail: String = "", // Empty for link invites
    val referralCode: String,
    val status: InviteStatus,
    val createdAt: Long = System.currentTimeMillis(),
    val acceptedAt: Long? = null,
    val expiresAt: Long,
    val acceptedByUserId: String? = null
) : Parcelable {
    
    enum class InviteStatus {
        PENDING,
        ACCEPTED,
        EXPIRED,
        DECLINED
    }
    
    val isExpired: Boolean
        get() = expiresAt < System.currentTimeMillis()
    
    val isValid: Boolean
        get() = status == InviteStatus.PENDING && !isExpired
}
