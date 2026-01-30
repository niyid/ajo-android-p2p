package com.techducat.ajo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_fees")
data class ServiceFeeEntity(
    @PrimaryKey val id: String,
    val distributionId: String,
    val roscaId: String,
    val grossAmount: Long,
    val feeAmount: Long,
    val netAmount: Long,
    val feePercentage: Double,
    val serviceWallet: String,
    var recipientTxHash: String?,
    var feeTxHash: String?,
    var status: String,
    var errorMessage: String?,
    val createdAt: Long,
    var completedAt: Long?
)
