package com.techducat.ajo.dlt

import kotlinx.serialization.Serializable

object DLTTestUtils {
    
    fun createTestRoscaMetadata(
        roscaId: String = "rosca_test",
        name: String = "Test ROSCA",
        memberCount: Int = 5,
        contributionAmount: Long = 100000L
    ) = RoscaMetadata(
        roscaId = roscaId,
        name = name,
        description = "Test ROSCA",
        creatorId = "creator_1",
        totalMembers = memberCount,
        contributionAmount = contributionAmount,
        contributionFrequency = "MONTHLY",
        payoutOrder = "SEQUENTIAL",
        startDate = System.currentTimeMillis(),
        memberIds = List(memberCount) { "member_$it" },
        multisigAddress = "test_multisig",
        status = "ACTIVE",
        createdAt = System.currentTimeMillis()
    )
    
    fun createTestContribution(
        roscaId: String = "rosca_test",
        memberId: String = "member_1",
        amount: Long = 100000L,
        txHash: String = "tx_test"
    ) = ContributionRecord(
        id = "contrib_test",
        roscaId = roscaId,
        memberId = memberId,
        amount = amount,
        roundNumber = 1,
        dueDate = System.currentTimeMillis(),
        txHash = txHash
    )
    
    fun createTestDistribution(
        roscaId: String = "rosca_test",
        recipientId: String = "member_1",
        amount: Long = 500000L,
        txHash: String = "tx_test"
    ) = DistributionRecord(
        id = "dist_test",
        roscaId = roscaId,
        recipientId = recipientId,
        recipientAddress = recipientId,
        amount = amount,
        roundNumber = 1,
        dueDate = System.currentTimeMillis(),
        txHash = txHash
    )
    
    fun generateMockIPFSHash(): String {
        val timestamp = System.currentTimeMillis()
        val random = (1..20).map { ('a'..'z').random() }.joinToString("")
        return "Qm$timestamp$random"
    }
    
    fun generateMockTxHash(): String {
        return (1..64).map { "0123456789abcdef".random() }.joinToString("")
    }
    
    fun xmrToAtomic(xmr: Double): Long {
        return (xmr * 1e12).toLong()
    }
    
    fun atomicToXmr(atomic: Long): Double {
        return atomic / 1e12
    }
    
    fun isValidIPFSHash(hash: String): Boolean {
        return hash.startsWith("Qm") && hash.length == 46
    }
    
    fun isValidMoneroTxHash(hash: String): Boolean {
        return hash.matches(Regex("^[0-9a-f]{64}$"))
    }
    
    fun createTestJsonData(
        key: String = "test",
        value: String = "data"
    ): String {
        val timestamp = System.currentTimeMillis()
        return "{\"$key\": \"$value\", \"timestamp\": $timestamp}"
    }
    
    fun calculateExpectedFee(amount: Long, priority: Int = 1): Long {
        val baseFee = 10000000L
        return baseFee * priority
    }
}
