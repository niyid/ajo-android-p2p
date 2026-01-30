package com.techducat.ajo.dlt

import kotlinx.serialization.Serializable

@Serializable
data class RoscaMetadata(
    val roscaId: String,
    val name: String,
    val description: String,
    val creatorId: String,
    val totalMembers: Int,
    val contributionAmount: Long,
    val contributionFrequency: String,
    val payoutOrder: String,
    val startDate: Long,
    val memberIds: List<String>,
    val multisigAddress: String?,
    val status: String,
    val createdAt: Long,
    val customMetadata: Map<String, String> = emptyMap()
)

@Serializable
data class ContributionRecord(
    val id: String,
    val roscaId: String,
    val memberId: String,
    val amount: Long,
    val roundNumber: Int,
    val dueDate: Long,
    val txHash: String
) {
    val contributionId: String get() = id
    val cycle: Int get() = roundNumber
}

@Serializable
data class DistributionRecord(
    val id: String,
    val roscaId: String,
    val recipientId: String,
    val recipientAddress: String,
    val amount: Long,
    val roundNumber: Int,
    val dueDate: Long,
    val txHash: String
) {
    val distributionId: String get() = id
    val cycle: Int get() = roundNumber
}

sealed class DLTUpdate {
    data class RoscaStored(val rosca: RoscaMetadata) : DLTUpdate()
    data class RoscaUpdated(val roscaId: String, val txHash: String) : DLTUpdate()
    data class ContributionAdded(val contribution: ContributionRecord) : DLTUpdate()
    data class ContributionReceived(val contribution: ContributionRecord) : DLTUpdate()
    data class DistributionCompleted(val distribution: DistributionRecord) : DLTUpdate()
    data class MemberAdded(val roscaId: String, val memberId: String) : DLTUpdate()
    data class MemberRemoved(val roscaId: String, val memberId: String) : DLTUpdate()
}
