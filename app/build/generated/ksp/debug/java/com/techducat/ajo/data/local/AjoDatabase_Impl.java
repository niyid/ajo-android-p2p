package com.techducat.ajo.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.techducat.ajo.data.local.dao.BidDao;
import com.techducat.ajo.data.local.dao.BidDao_Impl;
import com.techducat.ajo.data.local.dao.ContributionDao;
import com.techducat.ajo.data.local.dao.ContributionDao_Impl;
import com.techducat.ajo.data.local.dao.DistributionDao;
import com.techducat.ajo.data.local.dao.DistributionDao_Impl;
import com.techducat.ajo.data.local.dao.DividendDao;
import com.techducat.ajo.data.local.dao.DividendDao_Impl;
import com.techducat.ajo.data.local.dao.InviteDao;
import com.techducat.ajo.data.local.dao.InviteDao_Impl;
import com.techducat.ajo.data.local.dao.LocalNodeDao;
import com.techducat.ajo.data.local.dao.LocalNodeDao_Impl;
import com.techducat.ajo.data.local.dao.LocalWalletDao;
import com.techducat.ajo.data.local.dao.LocalWalletDao_Impl;
import com.techducat.ajo.data.local.dao.MemberDao;
import com.techducat.ajo.data.local.dao.MemberDao_Impl;
import com.techducat.ajo.data.local.dao.MultisigSignatureDao;
import com.techducat.ajo.data.local.dao.MultisigSignatureDao_Impl;
import com.techducat.ajo.data.local.dao.PayoutDao;
import com.techducat.ajo.data.local.dao.PayoutDao_Impl;
import com.techducat.ajo.data.local.dao.PeerDao;
import com.techducat.ajo.data.local.dao.PeerDao_Impl;
import com.techducat.ajo.data.local.dao.PenaltyDao;
import com.techducat.ajo.data.local.dao.PenaltyDao_Impl;
import com.techducat.ajo.data.local.dao.RoscaDao;
import com.techducat.ajo.data.local.dao.RoscaDao_Impl;
import com.techducat.ajo.data.local.dao.RoundDao;
import com.techducat.ajo.data.local.dao.RoundDao_Impl;
import com.techducat.ajo.data.local.dao.ServiceFeeDao;
import com.techducat.ajo.data.local.dao.ServiceFeeDao_Impl;
import com.techducat.ajo.data.local.dao.SyncConflictDao;
import com.techducat.ajo.data.local.dao.SyncConflictDao_Impl;
import com.techducat.ajo.data.local.dao.SyncLogDao;
import com.techducat.ajo.data.local.dao.SyncLogDao_Impl;
import com.techducat.ajo.data.local.dao.SyncTargetDao;
import com.techducat.ajo.data.local.dao.SyncTargetDao_Impl;
import com.techducat.ajo.data.local.dao.TransactionDao;
import com.techducat.ajo.data.local.dao.TransactionDao_Impl;
import com.techducat.ajo.data.local.dao.UserProfileDao;
import com.techducat.ajo.data.local.dao.UserProfileDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AjoDatabase_Impl extends AjoDatabase {
  private volatile LocalNodeDao _localNodeDao;

  private volatile PeerDao _peerDao;

  private volatile SyncTargetDao _syncTargetDao;

  private volatile RoscaDao _roscaDao;

  private volatile ContributionDao _contributionDao;

  private volatile MemberDao _memberDao;

  private volatile DistributionDao _distributionDao;

  private volatile ServiceFeeDao _serviceFeeDao;

  private volatile UserProfileDao _userProfileDao;

  private volatile PayoutDao _payoutDao;

  private volatile PenaltyDao _penaltyDao;

  private volatile TransactionDao _transactionDao;

  private volatile InviteDao _inviteDao;

  private volatile RoundDao _roundDao;

  private volatile BidDao _bidDao;

  private volatile DividendDao _dividendDao;

  private volatile MultisigSignatureDao _multisigSignatureDao;

  private volatile LocalWalletDao _localWalletDao;

  private volatile SyncQueueDao _syncQueueDao;

  private volatile SyncLogDao _syncLogDao;

  private volatile SyncConflictDao _syncConflictDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(10) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `local_node` (`nodeId` TEXT NOT NULL, `publicKey` TEXT NOT NULL, `privateKeyEncrypted` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `lastSyncAt` INTEGER, `displayName` TEXT, `deviceInfo` TEXT, PRIMARY KEY(`nodeId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `peers` (`id` TEXT NOT NULL, `nodeId` TEXT NOT NULL, `roscaId` TEXT NOT NULL, `publicKey` TEXT NOT NULL, `role` TEXT NOT NULL, `endpoint` TEXT, `status` TEXT NOT NULL, `addedAt` INTEGER NOT NULL, `lastSeenAt` INTEGER, `displayName` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_peers_nodeId` ON `peers` (`nodeId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_peers_roscaId` ON `peers` (`roscaId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sync_targets` (`id` TEXT NOT NULL, `roscaId` TEXT NOT NULL, `targetPeerId` TEXT NOT NULL, `syncEnabled` INTEGER NOT NULL, `lastSyncAttempt` INTEGER, `lastSyncSuccess` INTEGER, `consecutiveFailures` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`targetPeerId`) REFERENCES `peers`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sync_targets_roscaId` ON `sync_targets` (`roscaId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sync_targets_targetPeerId` ON `sync_targets` (`targetPeerId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `roscas` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `creatorId` TEXT, `groupType` TEXT NOT NULL, `contributionAmount` INTEGER NOT NULL, `contributionFrequency` TEXT NOT NULL, `frequencyDays` INTEGER NOT NULL, `totalMembers` INTEGER NOT NULL, `currentMembers` INTEGER NOT NULL, `payoutOrder` TEXT NOT NULL, `distributionMethod` TEXT NOT NULL, `cycleNumber` INTEGER NOT NULL, `currentRound` INTEGER NOT NULL, `totalCycles` INTEGER NOT NULL, `status` TEXT NOT NULL, `walletAddress` TEXT, `roscaWalletPath` TEXT, `multisigAddress` TEXT, `multisigInfo` TEXT, `ipfsHash` TEXT, `ipfsCid` TEXT, `ipnsKey` TEXT, `version` INTEGER NOT NULL, `isDirty` INTEGER NOT NULL, `lastSyncedAt` INTEGER, `lastSyncTimestamp` INTEGER, `startDate` INTEGER, `startedAt` INTEGER, `completedAt` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `members` (`id` TEXT NOT NULL, `roscaId` TEXT NOT NULL, `userId` TEXT NOT NULL, `name` TEXT NOT NULL, `moneroAddress` TEXT, `joinedAt` INTEGER NOT NULL, `position` INTEGER NOT NULL, `leftAt` INTEGER NOT NULL, `leftReason` TEXT NOT NULL, `isActive` INTEGER NOT NULL, `walletAddress` TEXT, `payoutOrderPosition` INTEGER, `hasReceivedPayout` INTEGER NOT NULL, `totalContributed` INTEGER NOT NULL, `missedPayments` INTEGER NOT NULL, `lastContributionAt` INTEGER, `exitedAt` INTEGER, `updatedAt` INTEGER, `ipfsHash` TEXT, `lastSyncedAt` INTEGER, `isDirty` INTEGER NOT NULL, `status` TEXT NOT NULL, `multisigInfo` TEXT, `hasReceived` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `contributions` (`id` TEXT NOT NULL, `roscaId` TEXT NOT NULL, `memberId` TEXT NOT NULL, `amount` INTEGER NOT NULL, `cycleNumber` INTEGER NOT NULL, `status` TEXT NOT NULL, `dueDate` INTEGER NOT NULL, `txHash` TEXT, `txId` TEXT, `proofOfPayment` TEXT, `paidAt` INTEGER, `confirmations` INTEGER NOT NULL, `verifiedAt` INTEGER, `notes` TEXT, `createdAt` INTEGER NOT NULL, `updated_at` INTEGER, `isDirty` INTEGER NOT NULL, `lastSyncedAt` INTEGER, `ipfsHash` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `rounds` (`id` TEXT NOT NULL, `rosca_id` TEXT NOT NULL, `round_number` INTEGER NOT NULL, `recipient_member_id` TEXT NOT NULL, `recipient_address` TEXT NOT NULL, `status` TEXT NOT NULL, `target_amount` INTEGER NOT NULL, `collected_amount` INTEGER NOT NULL, `expected_contributors` INTEGER NOT NULL, `actual_contributors` INTEGER NOT NULL, `payout_amount` INTEGER, `service_fee` INTEGER NOT NULL, `penalty_amount` INTEGER NOT NULL, `started_at` INTEGER NOT NULL, `due_date` INTEGER NOT NULL, `payout_initiated_at` INTEGER, `completed_at` INTEGER, `payout_tx_hash` TEXT, `payout_tx_id` TEXT, `payout_confirmations` INTEGER NOT NULL, `notes` TEXT, `ipfs_hash` TEXT, `is_dirty` INTEGER NOT NULL, `last_synced_at` INTEGER, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`rosca_id`) REFERENCES `roscas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_rounds_rosca_id` ON `rounds` (`rosca_id`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_rounds_rosca_id_round_number` ON `rounds` (`rosca_id`, `round_number`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_rounds_status` ON `rounds` (`status`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_rounds_recipient_member_id` ON `rounds` (`recipient_member_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bids` (`id` TEXT NOT NULL, `roundId` TEXT NOT NULL, `memberId` TEXT NOT NULL, `bidAmount` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `status` TEXT NOT NULL, `roscaId` TEXT NOT NULL, `roundNumber` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`roundId`) REFERENCES `rounds`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_bids_roundId` ON `bids` (`roundId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_bids_memberId` ON `bids` (`memberId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_bids_roscaId` ON `bids` (`roscaId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_bids_roundNumber` ON `bids` (`roundNumber`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_bids_roundId_memberId` ON `bids` (`roundId`, `memberId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `dividends` (`id` TEXT NOT NULL, `roundId` TEXT NOT NULL, `memberId` TEXT NOT NULL, `amount` INTEGER NOT NULL, `transactionHash` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`roundId`) REFERENCES `rounds`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_dividends_roundId` ON `dividends` (`roundId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_dividends_memberId` ON `dividends` (`memberId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `distributions` (`id` TEXT NOT NULL, `roscaId` TEXT NOT NULL, `roundId` TEXT NOT NULL, `roundNumber` INTEGER NOT NULL, `recipientId` TEXT NOT NULL, `recipientAddress` TEXT NOT NULL, `amount` INTEGER NOT NULL, `txHash` TEXT, `txId` TEXT, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `confirmedAt` INTEGER, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`roscaId`) REFERENCES `roscas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`roundId`) REFERENCES `rounds`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_distributions_roscaId` ON `distributions` (`roscaId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_distributions_roundId` ON `distributions` (`roundId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_distributions_roundNumber` ON `distributions` (`roundNumber`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_distributions_recipientId` ON `distributions` (`recipientId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_distributions_status` ON `distributions` (`status`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_distributions_txHash` ON `distributions` (`txHash`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `service_fees` (`id` TEXT NOT NULL, `distributionId` TEXT NOT NULL, `roscaId` TEXT NOT NULL, `grossAmount` INTEGER NOT NULL, `feeAmount` INTEGER NOT NULL, `netAmount` INTEGER NOT NULL, `feePercentage` REAL NOT NULL, `serviceWallet` TEXT NOT NULL, `recipientTxHash` TEXT, `feeTxHash` TEXT, `status` TEXT NOT NULL, `errorMessage` TEXT, `createdAt` INTEGER NOT NULL, `completedAt` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `payouts` (`id` TEXT NOT NULL, `rosca_id` TEXT NOT NULL, `recipient_id` TEXT NOT NULL, `round_id` TEXT, `payout_type` TEXT NOT NULL, `gross_amount` INTEGER NOT NULL, `service_fee` INTEGER NOT NULL, `penalty_amount` INTEGER NOT NULL, `net_amount` INTEGER NOT NULL, `tx_hash` TEXT, `tx_id` TEXT, `recipient_address` TEXT NOT NULL, `status` TEXT NOT NULL, `initiated_at` INTEGER NOT NULL, `completed_at` INTEGER, `failed_at` INTEGER, `error_message` TEXT, `confirmations` INTEGER NOT NULL, `verified_at` INTEGER, `notes` TEXT, `created_at` INTEGER NOT NULL, `updated_at` INTEGER, `ipfs_hash` TEXT, `last_synced_at` INTEGER, `is_dirty` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `penalties` (`id` TEXT NOT NULL, `rosca_id` TEXT NOT NULL, `member_id` TEXT NOT NULL, `payout_id` TEXT, `penalty_type` TEXT NOT NULL, `total_contributed` INTEGER NOT NULL, `cycles_participated` INTEGER NOT NULL, `cycles_remaining` INTEGER NOT NULL, `penalty_percentage` REAL NOT NULL, `penalty_amount` INTEGER NOT NULL, `reimbursement_amount` INTEGER NOT NULL, `calculation_method` TEXT NOT NULL, `reason` TEXT NOT NULL, `exit_reason` TEXT, `status` TEXT NOT NULL, `applied_at` INTEGER, `waived_at` INTEGER, `waived_by` TEXT, `waiver_reason` TEXT, `notes` TEXT, `created_at` INTEGER NOT NULL, `updated_at` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (`id` TEXT NOT NULL, `roscaId` TEXT NOT NULL, `roundNumber` INTEGER NOT NULL, `txHash` TEXT, `amount` INTEGER NOT NULL, `toAddress` TEXT, `fromAddress` TEXT, `status` TEXT NOT NULL, `requiredSignatures` INTEGER NOT NULL, `currentSignatureCount` INTEGER NOT NULL, `confirmations` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `broadcastAt` INTEGER, `confirmedAt` INTEGER, `syncVersion` INTEGER NOT NULL, `lastModifiedBy` TEXT NOT NULL, `lastModifiedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `multisig_signatures` (`id` TEXT NOT NULL, `roscaId` TEXT NOT NULL, `roundNumber` INTEGER NOT NULL, `txHash` TEXT NOT NULL, `memberId` TEXT NOT NULL, `hasSigned` INTEGER NOT NULL, `signature` TEXT, `timestamp` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`roscaId`) REFERENCES `roscas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_multisig_signatures_roscaId` ON `multisig_signatures` (`roscaId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_multisig_signatures_roundNumber` ON `multisig_signatures` (`roundNumber`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_multisig_signatures_memberId` ON `multisig_signatures` (`memberId`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_multisig_signatures_roscaId_roundNumber_memberId` ON `multisig_signatures` (`roscaId`, `roundNumber`, `memberId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `local_wallets` (`id` TEXT NOT NULL, `roscaId` TEXT NOT NULL, `nodeId` TEXT NOT NULL, `walletPath` TEXT NOT NULL, `cacheFilePath` TEXT, `passwordEncrypted` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `lastAccessedAt` INTEGER, `isMultisig` INTEGER NOT NULL, `multisigInfo` TEXT, `label` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`roscaId`) REFERENCES `roscas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_local_wallets_roscaId` ON `local_wallets` (`roscaId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_local_wallets_nodeId` ON `local_wallets` (`nodeId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_profiles` (`id` TEXT NOT NULL, `email` TEXT NOT NULL, `displayName` TEXT NOT NULL, `photoUrl` TEXT, `idToken` TEXT, `isActive` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `lastLoginAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `invites` (`id` TEXT NOT NULL, `roscaId` TEXT NOT NULL, `inviterUserId` TEXT NOT NULL, `inviteeEmail` TEXT NOT NULL, `referralCode` TEXT NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `acceptedAt` INTEGER, `expiresAt` INTEGER NOT NULL, `acceptedByUserId` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`roscaId`) REFERENCES `roscas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_invites_roscaId` ON `invites` (`roscaId`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_invites_referralCode` ON `invites` (`referralCode`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_invites_inviteeEmail` ON `invites` (`inviteeEmail`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_invites_status` ON `invites` (`status`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sync_queue` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `entityType` TEXT NOT NULL, `entityId` TEXT NOT NULL, `operation` TEXT NOT NULL, `payload` TEXT NOT NULL, `attempts` INTEGER NOT NULL, `maxAttempts` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `lastAttemptAt` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sync_log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `roscaId` TEXT NOT NULL, `direction` TEXT NOT NULL, `peerNodeId` TEXT NOT NULL, `entityType` TEXT NOT NULL, `entityId` TEXT NOT NULL, `operation` TEXT NOT NULL, `status` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `errorMessage` TEXT, `durationMs` INTEGER, `payloadSize` INTEGER)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sync_log_roscaId` ON `sync_log` (`roscaId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sync_log_timestamp` ON `sync_log` (`timestamp`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sync_conflicts` (`id` TEXT NOT NULL, `roscaId` TEXT NOT NULL, `entityType` TEXT NOT NULL, `entityId` TEXT NOT NULL, `localVersion` INTEGER NOT NULL, `remoteVersion` INTEGER NOT NULL, `localPayload` TEXT NOT NULL, `remotePayload` TEXT NOT NULL, `detectedAt` INTEGER NOT NULL, `resolvedAt` INTEGER, `resolution` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '093f5fe289436947f015d949bf97fbe7')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `local_node`");
        db.execSQL("DROP TABLE IF EXISTS `peers`");
        db.execSQL("DROP TABLE IF EXISTS `sync_targets`");
        db.execSQL("DROP TABLE IF EXISTS `roscas`");
        db.execSQL("DROP TABLE IF EXISTS `members`");
        db.execSQL("DROP TABLE IF EXISTS `contributions`");
        db.execSQL("DROP TABLE IF EXISTS `rounds`");
        db.execSQL("DROP TABLE IF EXISTS `bids`");
        db.execSQL("DROP TABLE IF EXISTS `dividends`");
        db.execSQL("DROP TABLE IF EXISTS `distributions`");
        db.execSQL("DROP TABLE IF EXISTS `service_fees`");
        db.execSQL("DROP TABLE IF EXISTS `payouts`");
        db.execSQL("DROP TABLE IF EXISTS `penalties`");
        db.execSQL("DROP TABLE IF EXISTS `transactions`");
        db.execSQL("DROP TABLE IF EXISTS `multisig_signatures`");
        db.execSQL("DROP TABLE IF EXISTS `local_wallets`");
        db.execSQL("DROP TABLE IF EXISTS `user_profiles`");
        db.execSQL("DROP TABLE IF EXISTS `invites`");
        db.execSQL("DROP TABLE IF EXISTS `sync_queue`");
        db.execSQL("DROP TABLE IF EXISTS `sync_log`");
        db.execSQL("DROP TABLE IF EXISTS `sync_conflicts`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsLocalNode = new HashMap<String, TableInfo.Column>(7);
        _columnsLocalNode.put("nodeId", new TableInfo.Column("nodeId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalNode.put("publicKey", new TableInfo.Column("publicKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalNode.put("privateKeyEncrypted", new TableInfo.Column("privateKeyEncrypted", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalNode.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalNode.put("lastSyncAt", new TableInfo.Column("lastSyncAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalNode.put("displayName", new TableInfo.Column("displayName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalNode.put("deviceInfo", new TableInfo.Column("deviceInfo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLocalNode = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLocalNode = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLocalNode = new TableInfo("local_node", _columnsLocalNode, _foreignKeysLocalNode, _indicesLocalNode);
        final TableInfo _existingLocalNode = TableInfo.read(db, "local_node");
        if (!_infoLocalNode.equals(_existingLocalNode)) {
          return new RoomOpenHelper.ValidationResult(false, "local_node(com.techducat.ajo.data.local.entity.LocalNodeEntity).\n"
                  + " Expected:\n" + _infoLocalNode + "\n"
                  + " Found:\n" + _existingLocalNode);
        }
        final HashMap<String, TableInfo.Column> _columnsPeers = new HashMap<String, TableInfo.Column>(10);
        _columnsPeers.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPeers.put("nodeId", new TableInfo.Column("nodeId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPeers.put("roscaId", new TableInfo.Column("roscaId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPeers.put("publicKey", new TableInfo.Column("publicKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPeers.put("role", new TableInfo.Column("role", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPeers.put("endpoint", new TableInfo.Column("endpoint", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPeers.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPeers.put("addedAt", new TableInfo.Column("addedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPeers.put("lastSeenAt", new TableInfo.Column("lastSeenAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPeers.put("displayName", new TableInfo.Column("displayName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPeers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPeers = new HashSet<TableInfo.Index>(2);
        _indicesPeers.add(new TableInfo.Index("index_peers_nodeId", true, Arrays.asList("nodeId"), Arrays.asList("ASC")));
        _indicesPeers.add(new TableInfo.Index("index_peers_roscaId", false, Arrays.asList("roscaId"), Arrays.asList("ASC")));
        final TableInfo _infoPeers = new TableInfo("peers", _columnsPeers, _foreignKeysPeers, _indicesPeers);
        final TableInfo _existingPeers = TableInfo.read(db, "peers");
        if (!_infoPeers.equals(_existingPeers)) {
          return new RoomOpenHelper.ValidationResult(false, "peers(com.techducat.ajo.data.local.entity.PeerEntity).\n"
                  + " Expected:\n" + _infoPeers + "\n"
                  + " Found:\n" + _existingPeers);
        }
        final HashMap<String, TableInfo.Column> _columnsSyncTargets = new HashMap<String, TableInfo.Column>(7);
        _columnsSyncTargets.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncTargets.put("roscaId", new TableInfo.Column("roscaId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncTargets.put("targetPeerId", new TableInfo.Column("targetPeerId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncTargets.put("syncEnabled", new TableInfo.Column("syncEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncTargets.put("lastSyncAttempt", new TableInfo.Column("lastSyncAttempt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncTargets.put("lastSyncSuccess", new TableInfo.Column("lastSyncSuccess", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncTargets.put("consecutiveFailures", new TableInfo.Column("consecutiveFailures", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSyncTargets = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSyncTargets.add(new TableInfo.ForeignKey("peers", "CASCADE", "NO ACTION", Arrays.asList("targetPeerId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSyncTargets = new HashSet<TableInfo.Index>(2);
        _indicesSyncTargets.add(new TableInfo.Index("index_sync_targets_roscaId", false, Arrays.asList("roscaId"), Arrays.asList("ASC")));
        _indicesSyncTargets.add(new TableInfo.Index("index_sync_targets_targetPeerId", false, Arrays.asList("targetPeerId"), Arrays.asList("ASC")));
        final TableInfo _infoSyncTargets = new TableInfo("sync_targets", _columnsSyncTargets, _foreignKeysSyncTargets, _indicesSyncTargets);
        final TableInfo _existingSyncTargets = TableInfo.read(db, "sync_targets");
        if (!_infoSyncTargets.equals(_existingSyncTargets)) {
          return new RoomOpenHelper.ValidationResult(false, "sync_targets(com.techducat.ajo.data.local.entity.SyncTargetEntity).\n"
                  + " Expected:\n" + _infoSyncTargets + "\n"
                  + " Found:\n" + _existingSyncTargets);
        }
        final HashMap<String, TableInfo.Column> _columnsRoscas = new HashMap<String, TableInfo.Column>(32);
        _columnsRoscas.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("creatorId", new TableInfo.Column("creatorId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("groupType", new TableInfo.Column("groupType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("contributionAmount", new TableInfo.Column("contributionAmount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("contributionFrequency", new TableInfo.Column("contributionFrequency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("frequencyDays", new TableInfo.Column("frequencyDays", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("totalMembers", new TableInfo.Column("totalMembers", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("currentMembers", new TableInfo.Column("currentMembers", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("payoutOrder", new TableInfo.Column("payoutOrder", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("distributionMethod", new TableInfo.Column("distributionMethod", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("cycleNumber", new TableInfo.Column("cycleNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("currentRound", new TableInfo.Column("currentRound", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("totalCycles", new TableInfo.Column("totalCycles", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("walletAddress", new TableInfo.Column("walletAddress", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("roscaWalletPath", new TableInfo.Column("roscaWalletPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("multisigAddress", new TableInfo.Column("multisigAddress", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("multisigInfo", new TableInfo.Column("multisigInfo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("ipfsHash", new TableInfo.Column("ipfsHash", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("ipfsCid", new TableInfo.Column("ipfsCid", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("ipnsKey", new TableInfo.Column("ipnsKey", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("version", new TableInfo.Column("version", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("isDirty", new TableInfo.Column("isDirty", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("lastSyncedAt", new TableInfo.Column("lastSyncedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("lastSyncTimestamp", new TableInfo.Column("lastSyncTimestamp", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("startDate", new TableInfo.Column("startDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("startedAt", new TableInfo.Column("startedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoscas.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRoscas = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRoscas = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRoscas = new TableInfo("roscas", _columnsRoscas, _foreignKeysRoscas, _indicesRoscas);
        final TableInfo _existingRoscas = TableInfo.read(db, "roscas");
        if (!_infoRoscas.equals(_existingRoscas)) {
          return new RoomOpenHelper.ValidationResult(false, "roscas(com.techducat.ajo.data.local.entity.RoscaEntity).\n"
                  + " Expected:\n" + _infoRoscas + "\n"
                  + " Found:\n" + _existingRoscas);
        }
        final HashMap<String, TableInfo.Column> _columnsMembers = new HashMap<String, TableInfo.Column>(24);
        _columnsMembers.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("roscaId", new TableInfo.Column("roscaId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("moneroAddress", new TableInfo.Column("moneroAddress", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("joinedAt", new TableInfo.Column("joinedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("position", new TableInfo.Column("position", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("leftAt", new TableInfo.Column("leftAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("leftReason", new TableInfo.Column("leftReason", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("walletAddress", new TableInfo.Column("walletAddress", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("payoutOrderPosition", new TableInfo.Column("payoutOrderPosition", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("hasReceivedPayout", new TableInfo.Column("hasReceivedPayout", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("totalContributed", new TableInfo.Column("totalContributed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("missedPayments", new TableInfo.Column("missedPayments", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("lastContributionAt", new TableInfo.Column("lastContributionAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("exitedAt", new TableInfo.Column("exitedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("ipfsHash", new TableInfo.Column("ipfsHash", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("lastSyncedAt", new TableInfo.Column("lastSyncedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("isDirty", new TableInfo.Column("isDirty", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("multisigInfo", new TableInfo.Column("multisigInfo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMembers.put("hasReceived", new TableInfo.Column("hasReceived", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMembers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMembers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMembers = new TableInfo("members", _columnsMembers, _foreignKeysMembers, _indicesMembers);
        final TableInfo _existingMembers = TableInfo.read(db, "members");
        if (!_infoMembers.equals(_existingMembers)) {
          return new RoomOpenHelper.ValidationResult(false, "members(com.techducat.ajo.data.local.entity.MemberEntity).\n"
                  + " Expected:\n" + _infoMembers + "\n"
                  + " Found:\n" + _existingMembers);
        }
        final HashMap<String, TableInfo.Column> _columnsContributions = new HashMap<String, TableInfo.Column>(19);
        _columnsContributions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("roscaId", new TableInfo.Column("roscaId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("memberId", new TableInfo.Column("memberId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("amount", new TableInfo.Column("amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("cycleNumber", new TableInfo.Column("cycleNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("dueDate", new TableInfo.Column("dueDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("txHash", new TableInfo.Column("txHash", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("txId", new TableInfo.Column("txId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("proofOfPayment", new TableInfo.Column("proofOfPayment", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("paidAt", new TableInfo.Column("paidAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("confirmations", new TableInfo.Column("confirmations", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("verifiedAt", new TableInfo.Column("verifiedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("isDirty", new TableInfo.Column("isDirty", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("lastSyncedAt", new TableInfo.Column("lastSyncedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContributions.put("ipfsHash", new TableInfo.Column("ipfsHash", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysContributions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesContributions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoContributions = new TableInfo("contributions", _columnsContributions, _foreignKeysContributions, _indicesContributions);
        final TableInfo _existingContributions = TableInfo.read(db, "contributions");
        if (!_infoContributions.equals(_existingContributions)) {
          return new RoomOpenHelper.ValidationResult(false, "contributions(com.techducat.ajo.data.local.entity.ContributionEntity).\n"
                  + " Expected:\n" + _infoContributions + "\n"
                  + " Found:\n" + _existingContributions);
        }
        final HashMap<String, TableInfo.Column> _columnsRounds = new HashMap<String, TableInfo.Column>(26);
        _columnsRounds.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("rosca_id", new TableInfo.Column("rosca_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("round_number", new TableInfo.Column("round_number", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("recipient_member_id", new TableInfo.Column("recipient_member_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("recipient_address", new TableInfo.Column("recipient_address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("target_amount", new TableInfo.Column("target_amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("collected_amount", new TableInfo.Column("collected_amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("expected_contributors", new TableInfo.Column("expected_contributors", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("actual_contributors", new TableInfo.Column("actual_contributors", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("payout_amount", new TableInfo.Column("payout_amount", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("service_fee", new TableInfo.Column("service_fee", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("penalty_amount", new TableInfo.Column("penalty_amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("started_at", new TableInfo.Column("started_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("due_date", new TableInfo.Column("due_date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("payout_initiated_at", new TableInfo.Column("payout_initiated_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("completed_at", new TableInfo.Column("completed_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("payout_tx_hash", new TableInfo.Column("payout_tx_hash", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("payout_tx_id", new TableInfo.Column("payout_tx_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("payout_confirmations", new TableInfo.Column("payout_confirmations", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("ipfs_hash", new TableInfo.Column("ipfs_hash", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("is_dirty", new TableInfo.Column("is_dirty", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("last_synced_at", new TableInfo.Column("last_synced_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRounds.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRounds = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysRounds.add(new TableInfo.ForeignKey("roscas", "CASCADE", "NO ACTION", Arrays.asList("rosca_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesRounds = new HashSet<TableInfo.Index>(4);
        _indicesRounds.add(new TableInfo.Index("index_rounds_rosca_id", false, Arrays.asList("rosca_id"), Arrays.asList("ASC")));
        _indicesRounds.add(new TableInfo.Index("index_rounds_rosca_id_round_number", true, Arrays.asList("rosca_id", "round_number"), Arrays.asList("ASC", "ASC")));
        _indicesRounds.add(new TableInfo.Index("index_rounds_status", false, Arrays.asList("status"), Arrays.asList("ASC")));
        _indicesRounds.add(new TableInfo.Index("index_rounds_recipient_member_id", false, Arrays.asList("recipient_member_id"), Arrays.asList("ASC")));
        final TableInfo _infoRounds = new TableInfo("rounds", _columnsRounds, _foreignKeysRounds, _indicesRounds);
        final TableInfo _existingRounds = TableInfo.read(db, "rounds");
        if (!_infoRounds.equals(_existingRounds)) {
          return new RoomOpenHelper.ValidationResult(false, "rounds(com.techducat.ajo.data.local.entity.RoundEntity).\n"
                  + " Expected:\n" + _infoRounds + "\n"
                  + " Found:\n" + _existingRounds);
        }
        final HashMap<String, TableInfo.Column> _columnsBids = new HashMap<String, TableInfo.Column>(10);
        _columnsBids.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBids.put("roundId", new TableInfo.Column("roundId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBids.put("memberId", new TableInfo.Column("memberId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBids.put("bidAmount", new TableInfo.Column("bidAmount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBids.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBids.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBids.put("roscaId", new TableInfo.Column("roscaId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBids.put("roundNumber", new TableInfo.Column("roundNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBids.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBids.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBids = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysBids.add(new TableInfo.ForeignKey("rounds", "CASCADE", "NO ACTION", Arrays.asList("roundId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesBids = new HashSet<TableInfo.Index>(5);
        _indicesBids.add(new TableInfo.Index("index_bids_roundId", false, Arrays.asList("roundId"), Arrays.asList("ASC")));
        _indicesBids.add(new TableInfo.Index("index_bids_memberId", false, Arrays.asList("memberId"), Arrays.asList("ASC")));
        _indicesBids.add(new TableInfo.Index("index_bids_roscaId", false, Arrays.asList("roscaId"), Arrays.asList("ASC")));
        _indicesBids.add(new TableInfo.Index("index_bids_roundNumber", false, Arrays.asList("roundNumber"), Arrays.asList("ASC")));
        _indicesBids.add(new TableInfo.Index("index_bids_roundId_memberId", true, Arrays.asList("roundId", "memberId"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoBids = new TableInfo("bids", _columnsBids, _foreignKeysBids, _indicesBids);
        final TableInfo _existingBids = TableInfo.read(db, "bids");
        if (!_infoBids.equals(_existingBids)) {
          return new RoomOpenHelper.ValidationResult(false, "bids(com.techducat.ajo.data.local.entity.BidEntity).\n"
                  + " Expected:\n" + _infoBids + "\n"
                  + " Found:\n" + _existingBids);
        }
        final HashMap<String, TableInfo.Column> _columnsDividends = new HashMap<String, TableInfo.Column>(7);
        _columnsDividends.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDividends.put("roundId", new TableInfo.Column("roundId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDividends.put("memberId", new TableInfo.Column("memberId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDividends.put("amount", new TableInfo.Column("amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDividends.put("transactionHash", new TableInfo.Column("transactionHash", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDividends.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDividends.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDividends = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysDividends.add(new TableInfo.ForeignKey("rounds", "CASCADE", "NO ACTION", Arrays.asList("roundId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesDividends = new HashSet<TableInfo.Index>(2);
        _indicesDividends.add(new TableInfo.Index("index_dividends_roundId", false, Arrays.asList("roundId"), Arrays.asList("ASC")));
        _indicesDividends.add(new TableInfo.Index("index_dividends_memberId", false, Arrays.asList("memberId"), Arrays.asList("ASC")));
        final TableInfo _infoDividends = new TableInfo("dividends", _columnsDividends, _foreignKeysDividends, _indicesDividends);
        final TableInfo _existingDividends = TableInfo.read(db, "dividends");
        if (!_infoDividends.equals(_existingDividends)) {
          return new RoomOpenHelper.ValidationResult(false, "dividends(com.techducat.ajo.data.local.entity.DividendEntity).\n"
                  + " Expected:\n" + _infoDividends + "\n"
                  + " Found:\n" + _existingDividends);
        }
        final HashMap<String, TableInfo.Column> _columnsDistributions = new HashMap<String, TableInfo.Column>(13);
        _columnsDistributions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributions.put("roscaId", new TableInfo.Column("roscaId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributions.put("roundId", new TableInfo.Column("roundId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributions.put("roundNumber", new TableInfo.Column("roundNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributions.put("recipientId", new TableInfo.Column("recipientId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributions.put("recipientAddress", new TableInfo.Column("recipientAddress", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributions.put("amount", new TableInfo.Column("amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributions.put("txHash", new TableInfo.Column("txHash", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributions.put("txId", new TableInfo.Column("txId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributions.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributions.put("confirmedAt", new TableInfo.Column("confirmedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistributions.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDistributions = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysDistributions.add(new TableInfo.ForeignKey("roscas", "CASCADE", "NO ACTION", Arrays.asList("roscaId"), Arrays.asList("id")));
        _foreignKeysDistributions.add(new TableInfo.ForeignKey("rounds", "CASCADE", "NO ACTION", Arrays.asList("roundId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesDistributions = new HashSet<TableInfo.Index>(6);
        _indicesDistributions.add(new TableInfo.Index("index_distributions_roscaId", false, Arrays.asList("roscaId"), Arrays.asList("ASC")));
        _indicesDistributions.add(new TableInfo.Index("index_distributions_roundId", false, Arrays.asList("roundId"), Arrays.asList("ASC")));
        _indicesDistributions.add(new TableInfo.Index("index_distributions_roundNumber", false, Arrays.asList("roundNumber"), Arrays.asList("ASC")));
        _indicesDistributions.add(new TableInfo.Index("index_distributions_recipientId", false, Arrays.asList("recipientId"), Arrays.asList("ASC")));
        _indicesDistributions.add(new TableInfo.Index("index_distributions_status", false, Arrays.asList("status"), Arrays.asList("ASC")));
        _indicesDistributions.add(new TableInfo.Index("index_distributions_txHash", false, Arrays.asList("txHash"), Arrays.asList("ASC")));
        final TableInfo _infoDistributions = new TableInfo("distributions", _columnsDistributions, _foreignKeysDistributions, _indicesDistributions);
        final TableInfo _existingDistributions = TableInfo.read(db, "distributions");
        if (!_infoDistributions.equals(_existingDistributions)) {
          return new RoomOpenHelper.ValidationResult(false, "distributions(com.techducat.ajo.data.local.entity.DistributionEntity).\n"
                  + " Expected:\n" + _infoDistributions + "\n"
                  + " Found:\n" + _existingDistributions);
        }
        final HashMap<String, TableInfo.Column> _columnsServiceFees = new HashMap<String, TableInfo.Column>(14);
        _columnsServiceFees.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceFees.put("distributionId", new TableInfo.Column("distributionId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceFees.put("roscaId", new TableInfo.Column("roscaId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceFees.put("grossAmount", new TableInfo.Column("grossAmount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceFees.put("feeAmount", new TableInfo.Column("feeAmount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceFees.put("netAmount", new TableInfo.Column("netAmount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceFees.put("feePercentage", new TableInfo.Column("feePercentage", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceFees.put("serviceWallet", new TableInfo.Column("serviceWallet", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceFees.put("recipientTxHash", new TableInfo.Column("recipientTxHash", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceFees.put("feeTxHash", new TableInfo.Column("feeTxHash", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceFees.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceFees.put("errorMessage", new TableInfo.Column("errorMessage", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceFees.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceFees.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysServiceFees = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesServiceFees = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoServiceFees = new TableInfo("service_fees", _columnsServiceFees, _foreignKeysServiceFees, _indicesServiceFees);
        final TableInfo _existingServiceFees = TableInfo.read(db, "service_fees");
        if (!_infoServiceFees.equals(_existingServiceFees)) {
          return new RoomOpenHelper.ValidationResult(false, "service_fees(com.techducat.ajo.data.local.entity.ServiceFeeEntity).\n"
                  + " Expected:\n" + _infoServiceFees + "\n"
                  + " Found:\n" + _existingServiceFees);
        }
        final HashMap<String, TableInfo.Column> _columnsPayouts = new HashMap<String, TableInfo.Column>(25);
        _columnsPayouts.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("rosca_id", new TableInfo.Column("rosca_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("recipient_id", new TableInfo.Column("recipient_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("round_id", new TableInfo.Column("round_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("payout_type", new TableInfo.Column("payout_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("gross_amount", new TableInfo.Column("gross_amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("service_fee", new TableInfo.Column("service_fee", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("penalty_amount", new TableInfo.Column("penalty_amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("net_amount", new TableInfo.Column("net_amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("tx_hash", new TableInfo.Column("tx_hash", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("tx_id", new TableInfo.Column("tx_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("recipient_address", new TableInfo.Column("recipient_address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("initiated_at", new TableInfo.Column("initiated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("completed_at", new TableInfo.Column("completed_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("failed_at", new TableInfo.Column("failed_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("error_message", new TableInfo.Column("error_message", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("confirmations", new TableInfo.Column("confirmations", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("verified_at", new TableInfo.Column("verified_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("ipfs_hash", new TableInfo.Column("ipfs_hash", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("last_synced_at", new TableInfo.Column("last_synced_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("is_dirty", new TableInfo.Column("is_dirty", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPayouts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPayouts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPayouts = new TableInfo("payouts", _columnsPayouts, _foreignKeysPayouts, _indicesPayouts);
        final TableInfo _existingPayouts = TableInfo.read(db, "payouts");
        if (!_infoPayouts.equals(_existingPayouts)) {
          return new RoomOpenHelper.ValidationResult(false, "payouts(com.techducat.ajo.data.local.entity.PayoutEntity).\n"
                  + " Expected:\n" + _infoPayouts + "\n"
                  + " Found:\n" + _existingPayouts);
        }
        final HashMap<String, TableInfo.Column> _columnsPenalties = new HashMap<String, TableInfo.Column>(22);
        _columnsPenalties.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("rosca_id", new TableInfo.Column("rosca_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("member_id", new TableInfo.Column("member_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("payout_id", new TableInfo.Column("payout_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("penalty_type", new TableInfo.Column("penalty_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("total_contributed", new TableInfo.Column("total_contributed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("cycles_participated", new TableInfo.Column("cycles_participated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("cycles_remaining", new TableInfo.Column("cycles_remaining", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("penalty_percentage", new TableInfo.Column("penalty_percentage", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("penalty_amount", new TableInfo.Column("penalty_amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("reimbursement_amount", new TableInfo.Column("reimbursement_amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("calculation_method", new TableInfo.Column("calculation_method", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("reason", new TableInfo.Column("reason", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("exit_reason", new TableInfo.Column("exit_reason", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("applied_at", new TableInfo.Column("applied_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("waived_at", new TableInfo.Column("waived_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("waived_by", new TableInfo.Column("waived_by", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("waiver_reason", new TableInfo.Column("waiver_reason", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPenalties.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPenalties = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPenalties = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPenalties = new TableInfo("penalties", _columnsPenalties, _foreignKeysPenalties, _indicesPenalties);
        final TableInfo _existingPenalties = TableInfo.read(db, "penalties");
        if (!_infoPenalties.equals(_existingPenalties)) {
          return new RoomOpenHelper.ValidationResult(false, "penalties(com.techducat.ajo.data.local.entity.PenaltyEntity).\n"
                  + " Expected:\n" + _infoPenalties + "\n"
                  + " Found:\n" + _existingPenalties);
        }
        final HashMap<String, TableInfo.Column> _columnsTransactions = new HashMap<String, TableInfo.Column>(17);
        _columnsTransactions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("roscaId", new TableInfo.Column("roscaId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("roundNumber", new TableInfo.Column("roundNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("txHash", new TableInfo.Column("txHash", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("amount", new TableInfo.Column("amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("toAddress", new TableInfo.Column("toAddress", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("fromAddress", new TableInfo.Column("fromAddress", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("requiredSignatures", new TableInfo.Column("requiredSignatures", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("currentSignatureCount", new TableInfo.Column("currentSignatureCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("confirmations", new TableInfo.Column("confirmations", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("broadcastAt", new TableInfo.Column("broadcastAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("confirmedAt", new TableInfo.Column("confirmedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("syncVersion", new TableInfo.Column("syncVersion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("lastModifiedBy", new TableInfo.Column("lastModifiedBy", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("lastModifiedAt", new TableInfo.Column("lastModifiedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTransactions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTransactions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTransactions = new TableInfo("transactions", _columnsTransactions, _foreignKeysTransactions, _indicesTransactions);
        final TableInfo _existingTransactions = TableInfo.read(db, "transactions");
        if (!_infoTransactions.equals(_existingTransactions)) {
          return new RoomOpenHelper.ValidationResult(false, "transactions(com.techducat.ajo.data.local.entity.TransactionEntity).\n"
                  + " Expected:\n" + _infoTransactions + "\n"
                  + " Found:\n" + _existingTransactions);
        }
        final HashMap<String, TableInfo.Column> _columnsMultisigSignatures = new HashMap<String, TableInfo.Column>(10);
        _columnsMultisigSignatures.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMultisigSignatures.put("roscaId", new TableInfo.Column("roscaId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMultisigSignatures.put("roundNumber", new TableInfo.Column("roundNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMultisigSignatures.put("txHash", new TableInfo.Column("txHash", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMultisigSignatures.put("memberId", new TableInfo.Column("memberId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMultisigSignatures.put("hasSigned", new TableInfo.Column("hasSigned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMultisigSignatures.put("signature", new TableInfo.Column("signature", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMultisigSignatures.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMultisigSignatures.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMultisigSignatures.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMultisigSignatures = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysMultisigSignatures.add(new TableInfo.ForeignKey("roscas", "CASCADE", "NO ACTION", Arrays.asList("roscaId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMultisigSignatures = new HashSet<TableInfo.Index>(4);
        _indicesMultisigSignatures.add(new TableInfo.Index("index_multisig_signatures_roscaId", false, Arrays.asList("roscaId"), Arrays.asList("ASC")));
        _indicesMultisigSignatures.add(new TableInfo.Index("index_multisig_signatures_roundNumber", false, Arrays.asList("roundNumber"), Arrays.asList("ASC")));
        _indicesMultisigSignatures.add(new TableInfo.Index("index_multisig_signatures_memberId", false, Arrays.asList("memberId"), Arrays.asList("ASC")));
        _indicesMultisigSignatures.add(new TableInfo.Index("index_multisig_signatures_roscaId_roundNumber_memberId", true, Arrays.asList("roscaId", "roundNumber", "memberId"), Arrays.asList("ASC", "ASC", "ASC")));
        final TableInfo _infoMultisigSignatures = new TableInfo("multisig_signatures", _columnsMultisigSignatures, _foreignKeysMultisigSignatures, _indicesMultisigSignatures);
        final TableInfo _existingMultisigSignatures = TableInfo.read(db, "multisig_signatures");
        if (!_infoMultisigSignatures.equals(_existingMultisigSignatures)) {
          return new RoomOpenHelper.ValidationResult(false, "multisig_signatures(com.techducat.ajo.data.local.entity.MultisigSignatureEntity).\n"
                  + " Expected:\n" + _infoMultisigSignatures + "\n"
                  + " Found:\n" + _existingMultisigSignatures);
        }
        final HashMap<String, TableInfo.Column> _columnsLocalWallets = new HashMap<String, TableInfo.Column>(11);
        _columnsLocalWallets.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalWallets.put("roscaId", new TableInfo.Column("roscaId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalWallets.put("nodeId", new TableInfo.Column("nodeId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalWallets.put("walletPath", new TableInfo.Column("walletPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalWallets.put("cacheFilePath", new TableInfo.Column("cacheFilePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalWallets.put("passwordEncrypted", new TableInfo.Column("passwordEncrypted", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalWallets.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalWallets.put("lastAccessedAt", new TableInfo.Column("lastAccessedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalWallets.put("isMultisig", new TableInfo.Column("isMultisig", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalWallets.put("multisigInfo", new TableInfo.Column("multisigInfo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalWallets.put("label", new TableInfo.Column("label", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLocalWallets = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysLocalWallets.add(new TableInfo.ForeignKey("roscas", "CASCADE", "NO ACTION", Arrays.asList("roscaId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesLocalWallets = new HashSet<TableInfo.Index>(2);
        _indicesLocalWallets.add(new TableInfo.Index("index_local_wallets_roscaId", false, Arrays.asList("roscaId"), Arrays.asList("ASC")));
        _indicesLocalWallets.add(new TableInfo.Index("index_local_wallets_nodeId", false, Arrays.asList("nodeId"), Arrays.asList("ASC")));
        final TableInfo _infoLocalWallets = new TableInfo("local_wallets", _columnsLocalWallets, _foreignKeysLocalWallets, _indicesLocalWallets);
        final TableInfo _existingLocalWallets = TableInfo.read(db, "local_wallets");
        if (!_infoLocalWallets.equals(_existingLocalWallets)) {
          return new RoomOpenHelper.ValidationResult(false, "local_wallets(com.techducat.ajo.data.local.entity.LocalWalletEntity).\n"
                  + " Expected:\n" + _infoLocalWallets + "\n"
                  + " Found:\n" + _existingLocalWallets);
        }
        final HashMap<String, TableInfo.Column> _columnsUserProfiles = new HashMap<String, TableInfo.Column>(8);
        _columnsUserProfiles.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("email", new TableInfo.Column("email", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("displayName", new TableInfo.Column("displayName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("photoUrl", new TableInfo.Column("photoUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("idToken", new TableInfo.Column("idToken", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("lastLoginAt", new TableInfo.Column("lastLoginAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserProfiles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserProfiles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserProfiles = new TableInfo("user_profiles", _columnsUserProfiles, _foreignKeysUserProfiles, _indicesUserProfiles);
        final TableInfo _existingUserProfiles = TableInfo.read(db, "user_profiles");
        if (!_infoUserProfiles.equals(_existingUserProfiles)) {
          return new RoomOpenHelper.ValidationResult(false, "user_profiles(com.techducat.ajo.data.local.entity.UserProfileEntity).\n"
                  + " Expected:\n" + _infoUserProfiles + "\n"
                  + " Found:\n" + _existingUserProfiles);
        }
        final HashMap<String, TableInfo.Column> _columnsInvites = new HashMap<String, TableInfo.Column>(10);
        _columnsInvites.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInvites.put("roscaId", new TableInfo.Column("roscaId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInvites.put("inviterUserId", new TableInfo.Column("inviterUserId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInvites.put("inviteeEmail", new TableInfo.Column("inviteeEmail", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInvites.put("referralCode", new TableInfo.Column("referralCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInvites.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInvites.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInvites.put("acceptedAt", new TableInfo.Column("acceptedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInvites.put("expiresAt", new TableInfo.Column("expiresAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInvites.put("acceptedByUserId", new TableInfo.Column("acceptedByUserId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysInvites = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysInvites.add(new TableInfo.ForeignKey("roscas", "CASCADE", "NO ACTION", Arrays.asList("roscaId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesInvites = new HashSet<TableInfo.Index>(4);
        _indicesInvites.add(new TableInfo.Index("index_invites_roscaId", false, Arrays.asList("roscaId"), Arrays.asList("ASC")));
        _indicesInvites.add(new TableInfo.Index("index_invites_referralCode", true, Arrays.asList("referralCode"), Arrays.asList("ASC")));
        _indicesInvites.add(new TableInfo.Index("index_invites_inviteeEmail", false, Arrays.asList("inviteeEmail"), Arrays.asList("ASC")));
        _indicesInvites.add(new TableInfo.Index("index_invites_status", false, Arrays.asList("status"), Arrays.asList("ASC")));
        final TableInfo _infoInvites = new TableInfo("invites", _columnsInvites, _foreignKeysInvites, _indicesInvites);
        final TableInfo _existingInvites = TableInfo.read(db, "invites");
        if (!_infoInvites.equals(_existingInvites)) {
          return new RoomOpenHelper.ValidationResult(false, "invites(com.techducat.ajo.data.local.entity.InviteEntity).\n"
                  + " Expected:\n" + _infoInvites + "\n"
                  + " Found:\n" + _existingInvites);
        }
        final HashMap<String, TableInfo.Column> _columnsSyncQueue = new HashMap<String, TableInfo.Column>(9);
        _columnsSyncQueue.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("entityType", new TableInfo.Column("entityType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("entityId", new TableInfo.Column("entityId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("operation", new TableInfo.Column("operation", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("payload", new TableInfo.Column("payload", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("attempts", new TableInfo.Column("attempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("maxAttempts", new TableInfo.Column("maxAttempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("lastAttemptAt", new TableInfo.Column("lastAttemptAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSyncQueue = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSyncQueue = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSyncQueue = new TableInfo("sync_queue", _columnsSyncQueue, _foreignKeysSyncQueue, _indicesSyncQueue);
        final TableInfo _existingSyncQueue = TableInfo.read(db, "sync_queue");
        if (!_infoSyncQueue.equals(_existingSyncQueue)) {
          return new RoomOpenHelper.ValidationResult(false, "sync_queue(com.techducat.ajo.data.local.SyncQueueEntity).\n"
                  + " Expected:\n" + _infoSyncQueue + "\n"
                  + " Found:\n" + _existingSyncQueue);
        }
        final HashMap<String, TableInfo.Column> _columnsSyncLog = new HashMap<String, TableInfo.Column>(12);
        _columnsSyncLog.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLog.put("roscaId", new TableInfo.Column("roscaId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLog.put("direction", new TableInfo.Column("direction", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLog.put("peerNodeId", new TableInfo.Column("peerNodeId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLog.put("entityType", new TableInfo.Column("entityType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLog.put("entityId", new TableInfo.Column("entityId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLog.put("operation", new TableInfo.Column("operation", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLog.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLog.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLog.put("errorMessage", new TableInfo.Column("errorMessage", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLog.put("durationMs", new TableInfo.Column("durationMs", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLog.put("payloadSize", new TableInfo.Column("payloadSize", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSyncLog = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSyncLog = new HashSet<TableInfo.Index>(2);
        _indicesSyncLog.add(new TableInfo.Index("index_sync_log_roscaId", false, Arrays.asList("roscaId"), Arrays.asList("ASC")));
        _indicesSyncLog.add(new TableInfo.Index("index_sync_log_timestamp", false, Arrays.asList("timestamp"), Arrays.asList("ASC")));
        final TableInfo _infoSyncLog = new TableInfo("sync_log", _columnsSyncLog, _foreignKeysSyncLog, _indicesSyncLog);
        final TableInfo _existingSyncLog = TableInfo.read(db, "sync_log");
        if (!_infoSyncLog.equals(_existingSyncLog)) {
          return new RoomOpenHelper.ValidationResult(false, "sync_log(com.techducat.ajo.data.local.entity.SyncLogEntity).\n"
                  + " Expected:\n" + _infoSyncLog + "\n"
                  + " Found:\n" + _existingSyncLog);
        }
        final HashMap<String, TableInfo.Column> _columnsSyncConflicts = new HashMap<String, TableInfo.Column>(11);
        _columnsSyncConflicts.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConflicts.put("roscaId", new TableInfo.Column("roscaId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConflicts.put("entityType", new TableInfo.Column("entityType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConflicts.put("entityId", new TableInfo.Column("entityId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConflicts.put("localVersion", new TableInfo.Column("localVersion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConflicts.put("remoteVersion", new TableInfo.Column("remoteVersion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConflicts.put("localPayload", new TableInfo.Column("localPayload", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConflicts.put("remotePayload", new TableInfo.Column("remotePayload", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConflicts.put("detectedAt", new TableInfo.Column("detectedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConflicts.put("resolvedAt", new TableInfo.Column("resolvedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConflicts.put("resolution", new TableInfo.Column("resolution", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSyncConflicts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSyncConflicts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSyncConflicts = new TableInfo("sync_conflicts", _columnsSyncConflicts, _foreignKeysSyncConflicts, _indicesSyncConflicts);
        final TableInfo _existingSyncConflicts = TableInfo.read(db, "sync_conflicts");
        if (!_infoSyncConflicts.equals(_existingSyncConflicts)) {
          return new RoomOpenHelper.ValidationResult(false, "sync_conflicts(com.techducat.ajo.data.local.entity.SyncConflictEntity).\n"
                  + " Expected:\n" + _infoSyncConflicts + "\n"
                  + " Found:\n" + _existingSyncConflicts);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "093f5fe289436947f015d949bf97fbe7", "8814eddb60a9c04532bd21e9ea75de4f");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "local_node","peers","sync_targets","roscas","members","contributions","rounds","bids","dividends","distributions","service_fees","payouts","penalties","transactions","multisig_signatures","local_wallets","user_profiles","invites","sync_queue","sync_log","sync_conflicts");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `local_node`");
      _db.execSQL("DELETE FROM `peers`");
      _db.execSQL("DELETE FROM `sync_targets`");
      _db.execSQL("DELETE FROM `roscas`");
      _db.execSQL("DELETE FROM `members`");
      _db.execSQL("DELETE FROM `contributions`");
      _db.execSQL("DELETE FROM `rounds`");
      _db.execSQL("DELETE FROM `bids`");
      _db.execSQL("DELETE FROM `dividends`");
      _db.execSQL("DELETE FROM `distributions`");
      _db.execSQL("DELETE FROM `service_fees`");
      _db.execSQL("DELETE FROM `payouts`");
      _db.execSQL("DELETE FROM `penalties`");
      _db.execSQL("DELETE FROM `transactions`");
      _db.execSQL("DELETE FROM `multisig_signatures`");
      _db.execSQL("DELETE FROM `local_wallets`");
      _db.execSQL("DELETE FROM `user_profiles`");
      _db.execSQL("DELETE FROM `invites`");
      _db.execSQL("DELETE FROM `sync_queue`");
      _db.execSQL("DELETE FROM `sync_log`");
      _db.execSQL("DELETE FROM `sync_conflicts`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(LocalNodeDao.class, LocalNodeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PeerDao.class, PeerDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SyncTargetDao.class, SyncTargetDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RoscaDao.class, RoscaDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ContributionDao.class, ContributionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MemberDao.class, MemberDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DistributionDao.class, DistributionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ServiceFeeDao.class, ServiceFeeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UserProfileDao.class, UserProfileDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PayoutDao.class, PayoutDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PenaltyDao.class, PenaltyDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TransactionDao.class, TransactionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(InviteDao.class, InviteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RoundDao.class, RoundDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BidDao.class, BidDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DividendDao.class, DividendDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MultisigSignatureDao.class, MultisigSignatureDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LocalWalletDao.class, LocalWalletDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SyncQueueDao.class, SyncQueueDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SyncLogDao.class, SyncLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SyncConflictDao.class, SyncConflictDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public LocalNodeDao localNodeDao() {
    if (_localNodeDao != null) {
      return _localNodeDao;
    } else {
      synchronized(this) {
        if(_localNodeDao == null) {
          _localNodeDao = new LocalNodeDao_Impl(this);
        }
        return _localNodeDao;
      }
    }
  }

  @Override
  public PeerDao peerDao() {
    if (_peerDao != null) {
      return _peerDao;
    } else {
      synchronized(this) {
        if(_peerDao == null) {
          _peerDao = new PeerDao_Impl(this);
        }
        return _peerDao;
      }
    }
  }

  @Override
  public SyncTargetDao syncTargetDao() {
    if (_syncTargetDao != null) {
      return _syncTargetDao;
    } else {
      synchronized(this) {
        if(_syncTargetDao == null) {
          _syncTargetDao = new SyncTargetDao_Impl(this);
        }
        return _syncTargetDao;
      }
    }
  }

  @Override
  public RoscaDao roscaDao() {
    if (_roscaDao != null) {
      return _roscaDao;
    } else {
      synchronized(this) {
        if(_roscaDao == null) {
          _roscaDao = new RoscaDao_Impl(this);
        }
        return _roscaDao;
      }
    }
  }

  @Override
  public ContributionDao contributionDao() {
    if (_contributionDao != null) {
      return _contributionDao;
    } else {
      synchronized(this) {
        if(_contributionDao == null) {
          _contributionDao = new ContributionDao_Impl(this);
        }
        return _contributionDao;
      }
    }
  }

  @Override
  public MemberDao memberDao() {
    if (_memberDao != null) {
      return _memberDao;
    } else {
      synchronized(this) {
        if(_memberDao == null) {
          _memberDao = new MemberDao_Impl(this);
        }
        return _memberDao;
      }
    }
  }

  @Override
  public DistributionDao distributionDao() {
    if (_distributionDao != null) {
      return _distributionDao;
    } else {
      synchronized(this) {
        if(_distributionDao == null) {
          _distributionDao = new DistributionDao_Impl(this);
        }
        return _distributionDao;
      }
    }
  }

  @Override
  public ServiceFeeDao serviceFeeDao() {
    if (_serviceFeeDao != null) {
      return _serviceFeeDao;
    } else {
      synchronized(this) {
        if(_serviceFeeDao == null) {
          _serviceFeeDao = new ServiceFeeDao_Impl(this);
        }
        return _serviceFeeDao;
      }
    }
  }

  @Override
  public UserProfileDao userProfileDao() {
    if (_userProfileDao != null) {
      return _userProfileDao;
    } else {
      synchronized(this) {
        if(_userProfileDao == null) {
          _userProfileDao = new UserProfileDao_Impl(this);
        }
        return _userProfileDao;
      }
    }
  }

  @Override
  public PayoutDao payoutDao() {
    if (_payoutDao != null) {
      return _payoutDao;
    } else {
      synchronized(this) {
        if(_payoutDao == null) {
          _payoutDao = new PayoutDao_Impl(this);
        }
        return _payoutDao;
      }
    }
  }

  @Override
  public PenaltyDao penaltyDao() {
    if (_penaltyDao != null) {
      return _penaltyDao;
    } else {
      synchronized(this) {
        if(_penaltyDao == null) {
          _penaltyDao = new PenaltyDao_Impl(this);
        }
        return _penaltyDao;
      }
    }
  }

  @Override
  public TransactionDao transactionDao() {
    if (_transactionDao != null) {
      return _transactionDao;
    } else {
      synchronized(this) {
        if(_transactionDao == null) {
          _transactionDao = new TransactionDao_Impl(this);
        }
        return _transactionDao;
      }
    }
  }

  @Override
  public InviteDao inviteDao() {
    if (_inviteDao != null) {
      return _inviteDao;
    } else {
      synchronized(this) {
        if(_inviteDao == null) {
          _inviteDao = new InviteDao_Impl(this);
        }
        return _inviteDao;
      }
    }
  }

  @Override
  public RoundDao roundDao() {
    if (_roundDao != null) {
      return _roundDao;
    } else {
      synchronized(this) {
        if(_roundDao == null) {
          _roundDao = new RoundDao_Impl(this);
        }
        return _roundDao;
      }
    }
  }

  @Override
  public BidDao bidDao() {
    if (_bidDao != null) {
      return _bidDao;
    } else {
      synchronized(this) {
        if(_bidDao == null) {
          _bidDao = new BidDao_Impl(this);
        }
        return _bidDao;
      }
    }
  }

  @Override
  public DividendDao dividendDao() {
    if (_dividendDao != null) {
      return _dividendDao;
    } else {
      synchronized(this) {
        if(_dividendDao == null) {
          _dividendDao = new DividendDao_Impl(this);
        }
        return _dividendDao;
      }
    }
  }

  @Override
  public MultisigSignatureDao multisigSignatureDao() {
    if (_multisigSignatureDao != null) {
      return _multisigSignatureDao;
    } else {
      synchronized(this) {
        if(_multisigSignatureDao == null) {
          _multisigSignatureDao = new MultisigSignatureDao_Impl(this);
        }
        return _multisigSignatureDao;
      }
    }
  }

  @Override
  public LocalWalletDao localWalletDao() {
    if (_localWalletDao != null) {
      return _localWalletDao;
    } else {
      synchronized(this) {
        if(_localWalletDao == null) {
          _localWalletDao = new LocalWalletDao_Impl(this);
        }
        return _localWalletDao;
      }
    }
  }

  @Override
  public SyncQueueDao syncQueueDao() {
    if (_syncQueueDao != null) {
      return _syncQueueDao;
    } else {
      synchronized(this) {
        if(_syncQueueDao == null) {
          _syncQueueDao = new SyncQueueDao_Impl(this);
        }
        return _syncQueueDao;
      }
    }
  }

  @Override
  public SyncLogDao syncLogDao() {
    if (_syncLogDao != null) {
      return _syncLogDao;
    } else {
      synchronized(this) {
        if(_syncLogDao == null) {
          _syncLogDao = new SyncLogDao_Impl(this);
        }
        return _syncLogDao;
      }
    }
  }

  @Override
  public SyncConflictDao syncConflictDao() {
    if (_syncConflictDao != null) {
      return _syncConflictDao;
    } else {
      synchronized(this) {
        if(_syncConflictDao == null) {
          _syncConflictDao = new SyncConflictDao_Impl(this);
        }
        return _syncConflictDao;
      }
    }
  }
}
