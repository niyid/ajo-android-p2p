package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.BidEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BidDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bid: BidEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bids: List<BidEntity>)
    
    @Update
    suspend fun update(bid: BidEntity)
    
    @Delete
    suspend fun delete(bid: BidEntity)
    
    @Query("SELECT * FROM bids WHERE id = :bidId")
    suspend fun getById(bidId: String): BidEntity?
    
    @Query("SELECT * FROM bids WHERE roundId = :roundId ORDER BY bidAmount DESC")
    suspend fun getBidsByRoundId(roundId: String): List<BidEntity>
    
    // ADDED: Method needed by RoscaManager for evaluating bids by ROSCA and round number
    @Query("""
        SELECT b.* FROM bids b
        INNER JOIN rounds r ON b.roundId = r.id
        WHERE r.rosca_id = :roscaId AND r.round_number = :roundNumber
        ORDER BY b.bidAmount DESC
    """)
    suspend fun getBidsByRound(roscaId: String, roundNumber: Int): List<BidEntity>
    
    @Query("SELECT * FROM bids WHERE roundId = :roundId AND memberId = :memberId LIMIT 1")
    suspend fun getBidByMemberAndRound(roundId: String, memberId: String): BidEntity?
    
    @Query("SELECT * FROM bids WHERE roundId = :roundId ORDER BY bidAmount DESC LIMIT 1")
    suspend fun getHighestBid(roundId: String): BidEntity?
    
    @Query("SELECT * FROM bids WHERE memberId = :memberId ORDER BY timestamp DESC")
    suspend fun getBidsByMember(memberId: String): List<BidEntity>
    
    @Query("SELECT COUNT(*) FROM bids WHERE roundId = :roundId")
    suspend fun getBidCountForRound(roundId: String): Int
}
