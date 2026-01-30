package com.techducat.ajo.data.local.dao

import androidx.room.*
import com.techducat.ajo.data.local.entity.DividendEntity

@Dao
interface DividendDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dividend: DividendEntity)
    
    @Query("SELECT * FROM dividends WHERE roundId = :roundId ORDER BY createdAt DESC")
    suspend fun getDividendsByRoundId(roundId: String): List<DividendEntity>
    
    @Query("SELECT * FROM dividends WHERE memberId = :memberId ORDER BY createdAt DESC")
    suspend fun getDividendsByMember(memberId: String): List<DividendEntity>
}
