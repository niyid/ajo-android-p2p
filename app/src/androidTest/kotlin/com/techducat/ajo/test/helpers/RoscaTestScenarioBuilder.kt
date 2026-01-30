// TODO: Update RoscaMetadata constructor to match new schema
package com.techducat.ajo.dlt


import kotlinx.serialization.Serializable
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith

@RunWith(RobolectricTestRunner::class)
class RoscaTestScenarioBuilder {
    
    private var roscaId: String = "rosca_test"
    private var memberCount: Int = 5
    private var contributionAmount: Long = 100000L
    private var rounds: Int = 5
    
    fun withRoscaId(id: String) = apply { this.roscaId = id }
    fun withMembers(count: Int) = apply { this.memberCount = count }
    fun withContributionAmount(amount: Long) = apply { this.contributionAmount = amount }
    fun withRounds(count: Int) = apply { this.rounds = count }
    
    fun build(): RoscaTestScenario {
        val rosca = DLTTestUtils.createTestRoscaMetadata(
            roscaId = roscaId,
            memberCount = memberCount,
            contributionAmount = contributionAmount
        )
        
        val contributions = mutableListOf<ContributionRecord>()
        val distributions = mutableListOf<DistributionRecord>()
        
        // Generate contributions and distributions for each round
        for (round in 1..rounds) {
            // Each member contributes
            rosca.memberIds.forEachIndexed { index, memberId ->
                contributions.add(
                    DLTTestUtils.createTestContribution(
                        roscaId = roscaId,
                        memberId = memberId,
                        amount = contributionAmount,
                        txHash = "contribution_r${round}_m${index}"
                    )
                )
            }
            
            // One member receives distribution
            val recipientIndex = (round - 1) % memberCount
            distributions.add(
                DLTTestUtils.createTestDistribution(
                    roscaId = roscaId,
                    recipientId = rosca.memberIds[recipientIndex],
                    amount = contributionAmount * memberCount,
                    txHash = "distribution_r${round}"
                )
            )
        }
        
        return RoscaTestScenario(rosca, contributions, distributions)
    }
}
