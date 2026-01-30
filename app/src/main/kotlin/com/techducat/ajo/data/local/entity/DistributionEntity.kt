package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

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
