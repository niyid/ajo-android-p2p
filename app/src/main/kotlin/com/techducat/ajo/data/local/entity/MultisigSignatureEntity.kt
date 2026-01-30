package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "multisig_signatures",
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
        Index(value = ["roundNumber"]),
        Index(value = ["memberId"]),
        Index(value = ["roscaId", "roundNumber", "memberId"], unique = true)
    ]
)
data class MultisigSignatureEntity(
    @PrimaryKey val id: String,
    val roscaId: String,
    val roundNumber: Int,
    val txHash: String,
    val memberId: String,
    val hasSigned: Boolean,
    val signature: String?,
    val timestamp: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Extension functions
fun MultisigSignatureEntity.toDomain() = com.techducat.ajo.model.MultisigSignature(
    id = id,
    roscaId = roscaId,
    roundNumber = roundNumber,
    txHash = txHash,
    memberId = memberId,
    hasSigned = hasSigned,
    signature = signature,
    timestamp = timestamp
)

fun com.techducat.ajo.model.MultisigSignature.toEntity() = MultisigSignatureEntity(
    id = id,
    roscaId = roscaId,
    roundNumber = roundNumber,
    txHash = txHash,
    memberId = memberId,
    hasSigned = hasSigned,
    signature = signature,
    timestamp = timestamp
)
