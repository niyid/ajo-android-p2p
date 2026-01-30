package com.techducat.ajo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Dividend(
    val id: String = UUID.randomUUID().toString(),
    val roundId: String,
    val memberId: String,
    val amount: Long,
    val transactionHash: String,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable
