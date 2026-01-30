// InviteEntity.kt
package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "invites",
    foreignKeys = [
        ForeignKey(
            entity = RoscaEntity::class,
            parentColumns = ["id"],
            childColumns = ["roscaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["roscaId"]),
        Index(value = ["referralCode"], unique = true),
        Index(value = ["inviteeEmail"]),
        Index(value = ["status"])
    ]
)
data class InviteEntity(
    @PrimaryKey
    val id: String,
    
    val roscaId: String,
    
    val inviterUserId: String, // Who sent the invite
    
    val inviteeEmail: String, // Who was invited (empty for link invites)
    
    val referralCode: String, // Unique referral code (e.g. "ABC12345")
    
    val status: String, // pending, accepted, expired, declined
    
    val createdAt: Long,
    
    val acceptedAt: Long? = null,
    
    val expiresAt: Long,
    
    val acceptedByUserId: String? = null // User who accepted (for link invites)
) {
    companion object {
        const val STATUS_PENDING = "pending"
        const val STATUS_ACCEPTED = "accepted"
        const val STATUS_EXPIRED = "expired"
        const val STATUS_DECLINED = "declined"
    }
}

