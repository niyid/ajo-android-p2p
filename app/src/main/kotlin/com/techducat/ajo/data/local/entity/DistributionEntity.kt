package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import com.techducat.ajo.model.Distribution
import com.techducat.ajo.model.Distribution.DistributionStatus

@Entity(
    tableName = "distributions",
    foreignKeys = [
        ForeignKey(
            entity = RoscaEntity::class,
            parentColumns = ["id"],
            childColumns = ["roscaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoundEntity::class,
            parentColumns = ["id"],
            childColumns = ["roundId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["roscaId"]),
        Index(value = ["roundId"]),
        Index(value = ["roundNumber"]),
        Index(value = ["recipientId"]),
        Index(value = ["status"]),
        Index(value = ["txHash"])
    ]
)
data class DistributionEntity(
    @PrimaryKey
    val id: String,
    val roscaId: String,
    val roundId: String,
    val roundNumber: Int,
    val recipientId: String,
    val recipientAddress: String,
    val amount: Long,
    val txHash: String? = null,
    val txId: String? = null,
    val status: String = "pending",
    val createdAt: Long = System.currentTimeMillis(),
    val confirmedAt: Long? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

// Extension functions for converting between Entity and Domain models
fun DistributionEntity.toDomain() = Distribution(
    id = id,
    roscaId = roscaId,
    roundId = roundId,
    roundNumber = roundNumber,
    recipientId = recipientId,
    recipientAddress = recipientAddress,
    amount = amount,
    txHash = txHash ?: txId,
    status = try {
        DistributionStatus.valueOf(status.uppercase())
    } catch (e: Exception) {
        DistributionStatus.PENDING
    },
    createdAt = createdAt,
    confirmedAt = confirmedAt
)

fun Distribution.toEntity() = DistributionEntity(
    id = id,
    roscaId = roscaId,
    roundId = roundId,
    roundNumber = roundNumber,
    recipientId = recipientId,
    recipientAddress = recipientAddress,
    amount = amount,
    txHash = txHash,
    txId = txHash,
    status = status.name.lowercase(),
    createdAt = createdAt,
    confirmedAt = confirmedAt,
    updatedAt = System.currentTimeMillis()
)
