package com.techducat.ajo.data.local

import com.techducat.ajo.data.local.entity.ContributionEntity
import com.techducat.ajo.data.local.entity.ServiceFeeEntity
import com.techducat.ajo.data.local.entity.MemberEntity
import com.techducat.ajo.data.local.entity.RoscaEntity
import com.techducat.ajo.data.local.entity.PayoutEntity
import com.techducat.ajo.data.local.entity.PenaltyEntity
import com.techducat.ajo.data.local.entity.TransactionEntity
import com.techducat.ajo.data.local.entity.UserProfileEntity
import com.techducat.ajo.data.local.entity.InviteEntity
import com.techducat.ajo.data.local.entity.BidEntity
import com.techducat.ajo.data.local.entity.DividendEntity
import com.techducat.ajo.data.local.entity.RoundEntity
import com.techducat.ajo.data.local.entity.DistributionEntity
import com.techducat.ajo.data.local.entity.MultisigSignatureEntity

// NEW P2P Entities
import com.techducat.ajo.data.local.entity.LocalNodeEntity
import com.techducat.ajo.data.local.entity.PeerEntity
import com.techducat.ajo.data.local.entity.SyncTargetEntity
import com.techducat.ajo.data.local.entity.LocalWalletEntity
import com.techducat.ajo.data.local.entity.SyncLogEntity
import com.techducat.ajo.data.local.entity.SyncConflictEntity

import com.techducat.ajo.data.local.dao.ContributionDao
import com.techducat.ajo.data.local.dao.ServiceFeeDao
import com.techducat.ajo.data.local.dao.MemberDao
import com.techducat.ajo.data.local.dao.RoscaDao
import com.techducat.ajo.data.local.dao.PayoutDao
import com.techducat.ajo.data.local.dao.PenaltyDao
import com.techducat.ajo.data.local.dao.TransactionDao
import com.techducat.ajo.data.local.dao.UserProfileDao
import com.techducat.ajo.data.local.dao.InviteDao
import com.techducat.ajo.data.local.dao.BidDao
import com.techducat.ajo.data.local.dao.DividendDao
import com.techducat.ajo.data.local.dao.RoundDao
import com.techducat.ajo.data.local.dao.DistributionDao
import com.techducat.ajo.data.local.dao.MultisigSignatureDao

// NEW P2P DAOs
import com.techducat.ajo.data.local.dao.LocalNodeDao
import com.techducat.ajo.data.local.dao.PeerDao
import com.techducat.ajo.data.local.dao.SyncTargetDao
import com.techducat.ajo.data.local.dao.LocalWalletDao
import com.techducat.ajo.data.local.dao.SyncLogDao
import com.techducat.ajo.data.local.dao.SyncConflictDao

import android.content.Context
import androidx.room.Room
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ============= Entities =============

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entityType: String,
    val entityId: String,
    val operation: String,
    val payload: String,
    val attempts: Int = 0,
    val maxAttempts: Int = 5,
    val createdAt: Long,
    val lastAttemptAt: Long?
)

// ============= DAOs =============
@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue WHERE attempts < maxAttempts ORDER BY createdAt ASC")
    suspend fun getPendingSyncs(): List<SyncQueueEntity>
    
    @Insert
    suspend fun insert(syncItem: SyncQueueEntity)
    
    @Delete
    suspend fun delete(syncItem: SyncQueueEntity)
}

// ============= Database =============

@Database(
    entities = [
        // Layer 1: Peer Topology (NEW)
        LocalNodeEntity::class,
        PeerEntity::class,
        SyncTargetEntity::class,
        
        // Layer 2: ROSCA State (synced)
        RoscaEntity::class,
        MemberEntity::class,
        ContributionEntity::class,
        RoundEntity::class,
        BidEntity::class,
        DividendEntity::class,
        DistributionEntity::class,
        ServiceFeeEntity::class,
        PayoutEntity::class,
        PenaltyEntity::class,
        TransactionEntity::class,
        MultisigSignatureEntity::class,
        
        // Layer 3: Local-only Data (NEW)
        LocalWalletEntity::class,
        UserProfileEntity::class,
        InviteEntity::class,
        
        // Layer 4: Sync Coordination (NEW)
        SyncQueueEntity::class,
        SyncLogEntity::class,
        SyncConflictEntity::class
    ],
    version = 10,  // Incremented for P2P
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class AjoDatabase : RoomDatabase() {
    // Layer 1 DAOs - Peer topology
    abstract fun localNodeDao(): LocalNodeDao
    abstract fun peerDao(): PeerDao
    abstract fun syncTargetDao(): SyncTargetDao
    
    // Layer 2 DAOs - ROSCA state (synced)
    abstract fun roscaDao(): RoscaDao
    abstract fun contributionDao(): ContributionDao
    abstract fun memberDao(): MemberDao
    abstract fun distributionDao(): DistributionDao
    abstract fun serviceFeeDao(): ServiceFeeDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun payoutDao(): PayoutDao
    abstract fun penaltyDao(): PenaltyDao
    abstract fun transactionDao(): TransactionDao
    abstract fun inviteDao(): InviteDao
    abstract fun roundDao(): RoundDao
    abstract fun bidDao(): BidDao
    abstract fun dividendDao(): DividendDao
    abstract fun multisigSignatureDao(): MultisigSignatureDao
    
    // Layer 3 DAOs - Local-only
    abstract fun localWalletDao(): LocalWalletDao
    
    // Layer 4 DAOs - Sync coordination
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun syncLogDao(): SyncLogDao
    abstract fun syncConflictDao(): SyncConflictDao
    
    companion object {
        @Volatile
        private var INSTANCE: AjoDatabase? = null
        
        fun getInstance(context: Context): AjoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AjoDatabase::class.java,
                    "ajo_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        fun clearDatabase(context: Context) {
            synchronized(this) {
                INSTANCE?.close()
                context.deleteDatabase("ajo_database")
                INSTANCE = null
            }
        }
        
        fun resetInstance() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
