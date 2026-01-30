package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    var txHash: String?,
    var status: String,
    var confirmations: Int = 0,
    var confirmedAt: Long? = null,
    val createdAt: Long
)
