package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import com.techducat.ajo.model.Bid
import com.techducat.ajo.model.BidStatus

@Entity(
    tableName = "bids",
    foreignKeys = [
        ForeignKey(
            entity = RoundEntity::class,
            parentColumns = ["id"],
            childColumns = ["roundId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["roundId"]),
        Index(value = ["memberId"]),
        Index(value = ["roscaId"]),
        Index(value = ["roundNumber"]),
        Index(value = ["roundId", "memberId"], unique = true)
    ]
)
data class BidEntity(
    @PrimaryKey val id: String,
    val roundId: String,
    val memberId: String,
    val bidAmount: Long,
    val timestamp: Long,
    val status: String,
    val roscaId: String,
    val roundNumber: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Extension functions for converting between Entity and Domain models
fun BidEntity.toDomain() = Bid(
    id = id,
    roundId = roundId,
    memberId = memberId,
    bidAmount = bidAmount,
    timestamp = timestamp,
    roscaId = roscaId,
    roundNumber = roundNumber,
    status = try {
        BidStatus.valueOf(status.uppercase())
    } catch (e: Exception) {
        BidStatus.PENDING
    }
)

fun Bid.toEntity() = BidEntity(
    id = id,
    roundId = roundId,
    memberId = memberId,
    bidAmount = bidAmount,
    timestamp = timestamp,
    status = status.name,
    roscaId = roscaId,
    roundNumber = roundNumber
)
