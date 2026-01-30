package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "dividends",
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
        Index(value = ["memberId"])
    ]
)
data class DividendEntity(
    @PrimaryKey val id: String,
    val roundId: String,
    val memberId: String,
    val amount: Long,
    val transactionHash: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
