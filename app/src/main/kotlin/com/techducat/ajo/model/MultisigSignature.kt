package com.techducat.ajo.model

data class MultisigSignature(
    val id: String,
    val roscaId: String,
    val roundNumber: Int,
    val txHash: String,
    val memberId: String,
    val hasSigned: Boolean,
    val signature: String?,
    val timestamp: Long
)
