package com.techducat.ajo.service

import android.util.Log
import com.techducat.ajo.model.Bid
import com.techducat.ajo.model.BidStatus
import com.techducat.ajo.model.Member
import com.techducat.ajo.model.Rosca
import com.techducat.ajo.model.Round
import com.techducat.ajo.repository.RoscaRepository
import com.techducat.ajo.data.local.entity.RoundEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.SecureRandom

class DistributionSelector(
    private val repository: RoscaRepository
) {
    companion object {
        private const val TAG = "com.techducat.ajo.service.DistributionSelector"
    }
    
    private val secureRandom = SecureRandom()
    private val scope = CoroutineScope(Dispatchers.IO)
    
    suspend fun selectRecipient(rosca: Rosca, roundNumber: Int): Member? {
        val members = repository.getMembersByRoscaId(rosca.id)
        val previousRounds = repository.getRoundsByRoscaId(rosca.id)
        
        return when (rosca.distributionMethod) {
            Rosca.DistributionMethod.PREDETERMINED -> selectPredetermined(members, previousRounds)
            Rosca.DistributionMethod.LOTTERY -> selectByLottery(members, previousRounds)
            Rosca.DistributionMethod.BIDDING -> selectByBidding(rosca, roundNumber)
        }
    }
    
    fun selectPredetermined(members: List<Member>, previousRounds: List<Round>): Member? {
        return try {
            val eligibleMembers = getEligibleMembers(members)
            if (eligibleMembers.isEmpty()) return null
            
            val sortedMembers = eligibleMembers.sortedBy { it.position }
            val index = previousRounds.size % sortedMembers.size
            val selected = sortedMembers[index]
            
            Log.d(TAG, "Selected (predetermined): ${selected.displayName}")
            selected
        } catch (e: Exception) {
            Log.e(TAG, "Error in predetermined selection", e)
            null
        }
    }
    
    fun selectByLottery(members: List<Member>, previousRounds: List<Round>): Member? {
        return try {
            val eligibleMembers = getEligibleMembers(members)
            if (eligibleMembers.isEmpty()) return null
            
            val randomIndex = secureRandom.nextInt(eligibleMembers.size)
            val selected = eligibleMembers[randomIndex]
            
            Log.d(TAG, "Selected (lottery): ${selected.displayName}")
            selected
        } catch (e: Exception) {
            Log.e(TAG, "Error in lottery selection", e)
            null
        }
    }
    
    suspend fun selectByBidding(rosca: Rosca, roundNumber: Int): Member? {
        return try {
            val round = repository.getRoundByNumber(rosca.id, roundNumber) ?: return null
            val bids = repository.getBidsByRoundId(round.id)
            
            if (bids.isEmpty()) {
                Log.d(TAG, "No bids submitted, using lottery")
                val members = repository.getMembersByRoscaId(rosca.id)
                val previousRounds = repository.getRoundsByRoscaId(rosca.id)
                return selectByLottery(members, previousRounds)
            }
            
            val members = repository.getMembersByRoscaId(rosca.id)
            val eligibleMembers = getEligibleMembers(members)
            val eligibleIds = eligibleMembers.map { it.id }.toSet()
            
            // Fix: Use BidStatus enum correctly
            val eligibleBids = bids.filter { 
                eligibleIds.contains(it.memberId) && 
                it.status == BidStatus.PENDING  // Changed from Bid.BidStatus.SUBMITTED
            }
            
            if (eligibleBids.isEmpty()) {
                val previousRounds = repository.getRoundsByRoscaId(rosca.id)
                return selectByLottery(members, previousRounds)
            }
            
            val winningBid = eligibleBids.maxByOrNull { it.bidAmount } ?: return null
            
            // Fix: Create new bid objects instead of mutating val properties
            scope.launch {
                // Update winning bid
                val updatedWinningBid = winningBid.copy(status = BidStatus.WINNING)
                repository.updateBid(updatedWinningBid)
                
                // Update losing bids
                eligibleBids.filter { it.id != winningBid.id }.forEach { bid ->
                    val updatedBid = bid.copy(status = BidStatus.LOSING)
                    repository.updateBid(updatedBid)
                }
            }
            
            Log.d(TAG, "Selected (bidding): ${winningBid.memberId} with bid: ${winningBid.bidAmount}")
            members.find { it.id == winningBid.memberId }
        } catch (e: Exception) {
            Log.e(TAG, "Error in bidding selection", e)
            null
        }
    }
    
    private fun getEligibleMembers(members: List<Member>): List<Member> {
        return members.filter { member ->
            !member.hasReceived && member.status == Member.MemberStatus.ACTIVE
        }
    }
    
    suspend fun submitBid(roscaId: String, userId: String, bidAmount: Long): Boolean {
        return try {
            val rosca = repository.getRoscaById(roscaId) ?: return false
            val members = repository.getMembersByRoscaId(roscaId)
            val member = members.find { it.userId == userId } ?: return false
            
            if (member.hasReceived) return false
            
            // Get the current round to get its ID
            val nextRoundNumber = rosca.currentRound + 1  // Define the round number
            val round = repository.getRoundByNumber(rosca.id, nextRoundNumber) 
                ?: return false
            
            // Fix: Use correct parameter names and include all required fields
            val bid = Bid(
                roscaId = roscaId,          // Primary identifier
                roundNumber = nextRoundNumber,  // FIXED: Use the defined variable
                roundId = round.id,         // For backward compatibility
                memberId = userId,
                bidAmount = bidAmount,
                status = BidStatus.PENDING
            )
            
            repository.insertBid(bid)
            Log.d(TAG, "Bid submitted: $userId = $bidAmount")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error submitting bid", e)
            false
        }
    }
}
