package com.techducat.ajo.model

/**
 * Domain model representing a ROSCA transaction
 * 
 * This represents financial transactions within a ROSCA, particularly
 * multisig transactions that require multiple signatures before execution.
 */
data class Transaction(
    val id: String,
    val roscaId: String,
    val roundNumber: Int,
    val txHash: String?,
    val amount: Long,
    val toAddress: String?,
    val fromAddress: String?,
    val status: TransactionStatus,
    val requiredSignatures: Int,
    val currentSignatureCount: Int,
    val confirmations: Int,
    val createdAt: Long,
    val broadcastAt: Long?,
    val confirmedAt: Long?
) {
    enum class TransactionStatus {
        PENDING_SIGNATURES,
        BROADCAST,
        CONFIRMED,
        FAILED
    }
}
