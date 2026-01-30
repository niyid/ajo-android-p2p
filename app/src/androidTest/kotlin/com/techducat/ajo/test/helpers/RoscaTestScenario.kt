package com.techducat.ajo.dlt


import kotlinx.serialization.Serializable
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith

@RunWith(RobolectricTestRunner::class)
data class RoscaTestScenario(
    val rosca: RoscaMetadata,
    val contributions: List<ContributionRecord>,
    val distributions: List<DistributionRecord>
) {
    fun getTotalContributed(): Long = contributions.sumOf { it.amount }
    fun getTotalDistributed(): Long = distributions.sumOf { it.amount }
    fun getRoundCount(): Int = distributions.size
    fun getMemberCount(): Int = rosca.memberIds.size
}
