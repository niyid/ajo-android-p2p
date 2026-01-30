package com.techducat.ajo.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.techducat.ajo.data.local.entity.RoundEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RoundDao_Impl implements RoundDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RoundEntity> __insertionAdapterOfRoundEntity;

  private final EntityDeletionOrUpdateAdapter<RoundEntity> __deletionAdapterOfRoundEntity;

  private final EntityDeletionOrUpdateAdapter<RoundEntity> __updateAdapterOfRoundEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateContributionProgress;

  private final SharedSQLiteStatement __preparedStmtOfInitiatePayout;

  private final SharedSQLiteStatement __preparedStmtOfCompletePayout;

  private final SharedSQLiteStatement __preparedStmtOfUpdatePayoutConfirmations;

  private final SharedSQLiteStatement __preparedStmtOfUpdateFees;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSyncStatus;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByRoscaId;

  public RoundDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRoundEntity = new EntityInsertionAdapter<RoundEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `rounds` (`id`,`rosca_id`,`round_number`,`recipient_member_id`,`recipient_address`,`status`,`target_amount`,`collected_amount`,`expected_contributors`,`actual_contributors`,`payout_amount`,`service_fee`,`penalty_amount`,`started_at`,`due_date`,`payout_initiated_at`,`completed_at`,`payout_tx_hash`,`payout_tx_id`,`payout_confirmations`,`notes`,`ipfs_hash`,`is_dirty`,`last_synced_at`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RoundEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindLong(3, entity.getRoundNumber());
        statement.bindString(4, entity.getRecipientMemberId());
        statement.bindString(5, entity.getRecipientAddress());
        statement.bindString(6, entity.getStatus());
        statement.bindLong(7, entity.getTargetAmount());
        statement.bindLong(8, entity.getCollectedAmount());
        statement.bindLong(9, entity.getExpectedContributors());
        statement.bindLong(10, entity.getActualContributors());
        if (entity.getPayoutAmount() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getPayoutAmount());
        }
        statement.bindLong(12, entity.getServiceFee());
        statement.bindLong(13, entity.getPenaltyAmount());
        statement.bindLong(14, entity.getStartedAt());
        statement.bindLong(15, entity.getDueDate());
        if (entity.getPayoutInitiatedAt() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getPayoutInitiatedAt());
        }
        if (entity.getCompletedAt() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getCompletedAt());
        }
        if (entity.getPayoutTxHash() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getPayoutTxHash());
        }
        if (entity.getPayoutTxId() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getPayoutTxId());
        }
        statement.bindLong(20, entity.getPayoutConfirmations());
        if (entity.getNotes() == null) {
          statement.bindNull(21);
        } else {
          statement.bindString(21, entity.getNotes());
        }
        if (entity.getIpfsHash() == null) {
          statement.bindNull(22);
        } else {
          statement.bindString(22, entity.getIpfsHash());
        }
        final int _tmp = entity.isDirty() ? 1 : 0;
        statement.bindLong(23, _tmp);
        if (entity.getLastSyncedAt() == null) {
          statement.bindNull(24);
        } else {
          statement.bindLong(24, entity.getLastSyncedAt());
        }
        statement.bindLong(25, entity.getCreatedAt());
        statement.bindLong(26, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfRoundEntity = new EntityDeletionOrUpdateAdapter<RoundEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `rounds` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RoundEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfRoundEntity = new EntityDeletionOrUpdateAdapter<RoundEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `rounds` SET `id` = ?,`rosca_id` = ?,`round_number` = ?,`recipient_member_id` = ?,`recipient_address` = ?,`status` = ?,`target_amount` = ?,`collected_amount` = ?,`expected_contributors` = ?,`actual_contributors` = ?,`payout_amount` = ?,`service_fee` = ?,`penalty_amount` = ?,`started_at` = ?,`due_date` = ?,`payout_initiated_at` = ?,`completed_at` = ?,`payout_tx_hash` = ?,`payout_tx_id` = ?,`payout_confirmations` = ?,`notes` = ?,`ipfs_hash` = ?,`is_dirty` = ?,`last_synced_at` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RoundEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindLong(3, entity.getRoundNumber());
        statement.bindString(4, entity.getRecipientMemberId());
        statement.bindString(5, entity.getRecipientAddress());
        statement.bindString(6, entity.getStatus());
        statement.bindLong(7, entity.getTargetAmount());
        statement.bindLong(8, entity.getCollectedAmount());
        statement.bindLong(9, entity.getExpectedContributors());
        statement.bindLong(10, entity.getActualContributors());
        if (entity.getPayoutAmount() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getPayoutAmount());
        }
        statement.bindLong(12, entity.getServiceFee());
        statement.bindLong(13, entity.getPenaltyAmount());
        statement.bindLong(14, entity.getStartedAt());
        statement.bindLong(15, entity.getDueDate());
        if (entity.getPayoutInitiatedAt() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getPayoutInitiatedAt());
        }
        if (entity.getCompletedAt() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getCompletedAt());
        }
        if (entity.getPayoutTxHash() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getPayoutTxHash());
        }
        if (entity.getPayoutTxId() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getPayoutTxId());
        }
        statement.bindLong(20, entity.getPayoutConfirmations());
        if (entity.getNotes() == null) {
          statement.bindNull(21);
        } else {
          statement.bindString(21, entity.getNotes());
        }
        if (entity.getIpfsHash() == null) {
          statement.bindNull(22);
        } else {
          statement.bindString(22, entity.getIpfsHash());
        }
        final int _tmp = entity.isDirty() ? 1 : 0;
        statement.bindLong(23, _tmp);
        if (entity.getLastSyncedAt() == null) {
          statement.bindNull(24);
        } else {
          statement.bindLong(24, entity.getLastSyncedAt());
        }
        statement.bindLong(25, entity.getCreatedAt());
        statement.bindLong(26, entity.getUpdatedAt());
        statement.bindString(27, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE rounds SET status = ?, updated_at = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateContributionProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE rounds SET \n"
                + "            collected_amount = ?,\n"
                + "            actual_contributors = ?,\n"
                + "            updated_at = ?\n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfInitiatePayout = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE rounds SET \n"
                + "            status = ?,\n"
                + "            payout_initiated_at = ?,\n"
                + "            updated_at = ?\n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfCompletePayout = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE rounds SET \n"
                + "            status = ?,\n"
                + "            payout_tx_hash = ?,\n"
                + "            payout_tx_id = ?,\n"
                + "            payout_amount = ?,\n"
                + "            completed_at = ?,\n"
                + "            updated_at = ?\n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePayoutConfirmations = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE rounds SET \n"
                + "            payout_confirmations = ?,\n"
                + "            updated_at = ?\n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateFees = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE rounds SET \n"
                + "            service_fee = ?,\n"
                + "            penalty_amount = ?,\n"
                + "            updated_at = ?\n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateSyncStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE rounds SET \n"
                + "            ipfs_hash = ?,\n"
                + "            last_synced_at = ?,\n"
                + "            is_dirty = ?,\n"
                + "            updated_at = ?\n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM rounds WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByRoscaId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM rounds WHERE rosca_id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final RoundEntity round, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRoundEntity.insert(round);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<RoundEntity> rounds,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRoundEntity.insert(rounds);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final RoundEntity round, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRoundEntity.handle(round);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final RoundEntity round, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRoundEntity.handle(round);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final String roundId, final String status, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 3;
        _stmt.bindString(_argIndex, roundId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateContributionProgress(final String roundId, final long collectedAmount,
      final int actualContributors, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateContributionProgress.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, collectedAmount);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, actualContributors);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 4;
        _stmt.bindString(_argIndex, roundId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateContributionProgress.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object initiatePayout(final String roundId, final String status,
      final long payoutInitiatedAt, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfInitiatePayout.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, payoutInitiatedAt);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 4;
        _stmt.bindString(_argIndex, roundId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfInitiatePayout.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object completePayout(final String roundId, final String status, final String txHash,
      final String txId, final long payoutAmount, final long completedAt, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfCompletePayout.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindString(_argIndex, txHash);
        _argIndex = 3;
        if (txId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, txId);
        }
        _argIndex = 4;
        _stmt.bindLong(_argIndex, payoutAmount);
        _argIndex = 5;
        _stmt.bindLong(_argIndex, completedAt);
        _argIndex = 6;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 7;
        _stmt.bindString(_argIndex, roundId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfCompletePayout.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePayoutConfirmations(final String roundId, final int confirmations,
      final long updatedAt, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePayoutConfirmations.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, confirmations);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 3;
        _stmt.bindString(_argIndex, roundId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdatePayoutConfirmations.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateFees(final String roundId, final long serviceFee, final long penaltyAmount,
      final long updatedAt, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateFees.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, serviceFee);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, penaltyAmount);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 4;
        _stmt.bindString(_argIndex, roundId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateFees.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSyncStatus(final String roundId, final String ipfsHash,
      final long lastSyncedAt, final boolean isDirty, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSyncStatus.acquire();
        int _argIndex = 1;
        if (ipfsHash == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, ipfsHash);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, lastSyncedAt);
        _argIndex = 3;
        final int _tmp = isDirty ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 5;
        _stmt.bindString(_argIndex, roundId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateSyncStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final String roundId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, roundId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByRoscaId(final String roscaId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByRoscaId.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, roscaId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteByRoscaId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final String roundId, final Continuation<? super RoundEntity> $completion) {
    final String _sql = "SELECT * FROM rounds WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roundId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RoundEntity>() {
      @Override
      @Nullable
      public RoundEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final RoundEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<RoundEntity> observeById(final String roundId) {
    final String _sql = "SELECT * FROM rounds WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roundId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rounds"}, new Callable<RoundEntity>() {
      @Override
      @Nullable
      public RoundEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final RoundEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<RoundEntity>> observeByRoscaId(final String roscaId) {
    final String _sql = "SELECT * FROM rounds WHERE rosca_id = ? ORDER BY round_number ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rounds"}, new Callable<List<RoundEntity>>() {
      @Override
      @NonNull
      public List<RoundEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<RoundEntity> _result = new ArrayList<RoundEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoundEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getByRoscaId(final String roscaId,
      final Continuation<? super List<RoundEntity>> $completion) {
    final String _sql = "SELECT * FROM rounds WHERE rosca_id = ? ORDER BY round_number ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoundEntity>>() {
      @Override
      @NonNull
      public List<RoundEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<RoundEntity> _result = new ArrayList<RoundEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoundEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRoundByNumber(final String roscaId, final int roundNumber,
      final Continuation<? super RoundEntity> $completion) {
    final String _sql = "SELECT * FROM rounds WHERE rosca_id = ? AND round_number = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, roundNumber);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RoundEntity>() {
      @Override
      @Nullable
      public RoundEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final RoundEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<RoundEntity> observeRoundByNumber(final String roscaId, final int roundNumber) {
    final String _sql = "SELECT * FROM rounds WHERE rosca_id = ? AND round_number = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, roundNumber);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rounds"}, new Callable<RoundEntity>() {
      @Override
      @Nullable
      public RoundEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final RoundEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getRoundsByStatus(final String roscaId, final String status,
      final Continuation<? super List<RoundEntity>> $completion) {
    final String _sql = "SELECT * FROM rounds WHERE rosca_id = ? AND status = ? ORDER BY round_number ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoundEntity>>() {
      @Override
      @NonNull
      public List<RoundEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<RoundEntity> _result = new ArrayList<RoundEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoundEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RoundEntity>> observeRoundsByStatus(final String roscaId, final String status) {
    final String _sql = "SELECT * FROM rounds WHERE rosca_id = ? AND status = ? ORDER BY round_number ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rounds"}, new Callable<List<RoundEntity>>() {
      @Override
      @NonNull
      public List<RoundEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<RoundEntity> _result = new ArrayList<RoundEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoundEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getRoundsByRecipient(final String memberId,
      final Continuation<? super List<RoundEntity>> $completion) {
    final String _sql = "SELECT * FROM rounds WHERE recipient_member_id = ? ORDER BY round_number ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, memberId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoundEntity>>() {
      @Override
      @NonNull
      public List<RoundEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<RoundEntity> _result = new ArrayList<RoundEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoundEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllByStatus(final String status,
      final Continuation<? super List<RoundEntity>> $completion) {
    final String _sql = "SELECT * FROM rounds WHERE status = ? ORDER BY due_date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoundEntity>>() {
      @Override
      @NonNull
      public List<RoundEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<RoundEntity> _result = new ArrayList<RoundEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoundEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getCurrentRound(final String roscaId,
      final Continuation<? super RoundEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM rounds \n"
            + "        WHERE rosca_id = ? \n"
            + "        AND status IN ('ACTIVE', 'PAYOUT')\n"
            + "        ORDER BY round_number DESC \n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RoundEntity>() {
      @Override
      @Nullable
      public RoundEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final RoundEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<RoundEntity> observeCurrentRound(final String roscaId) {
    final String _sql = "\n"
            + "        SELECT * FROM rounds \n"
            + "        WHERE rosca_id = ? \n"
            + "        AND status IN ('ACTIVE', 'PAYOUT')\n"
            + "        ORDER BY round_number DESC \n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rounds"}, new Callable<RoundEntity>() {
      @Override
      @Nullable
      public RoundEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final RoundEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getActiveRound(final String roscaId,
      final Continuation<? super RoundEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM rounds \n"
            + "        WHERE rosca_id = ? \n"
            + "        AND status = 'ACTIVE'\n"
            + "        ORDER BY round_number DESC \n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RoundEntity>() {
      @Override
      @Nullable
      public RoundEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final RoundEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRoundCount(final String roscaId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM rounds WHERE rosca_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRoundCountByStatus(final String roscaId, final String status,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM rounds WHERE rosca_id = ? AND status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTotalCollected(final String roscaId,
      final Continuation<? super Long> $completion) {
    final String _sql = "SELECT SUM(collected_amount) FROM rounds WHERE rosca_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTotalPaidOut(final String roscaId,
      final Continuation<? super Long> $completion) {
    final String _sql = "SELECT SUM(payout_amount) FROM rounds WHERE rosca_id = ? AND status = 'COMPLETED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTotalServiceFees(final String roscaId,
      final Continuation<? super Long> $completion) {
    final String _sql = "SELECT SUM(service_fee) FROM rounds WHERE rosca_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTotalPenalties(final String roscaId,
      final Continuation<? super Long> $completion) {
    final String _sql = "SELECT SUM(penalty_amount) FROM rounds WHERE rosca_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getOverdueRounds(final long currentTime,
      final Continuation<? super List<RoundEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM rounds \n"
            + "        WHERE status = 'ACTIVE' \n"
            + "        AND due_date < ?\n"
            + "        ORDER BY due_date ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, currentTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoundEntity>>() {
      @Override
      @NonNull
      public List<RoundEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<RoundEntity> _result = new ArrayList<RoundEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoundEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getOverdueRoundsForRosca(final String roscaId, final long currentTime,
      final Continuation<? super List<RoundEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM rounds \n"
            + "        WHERE rosca_id = ? \n"
            + "        AND status = 'ACTIVE' \n"
            + "        AND due_date < ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, currentTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoundEntity>>() {
      @Override
      @NonNull
      public List<RoundEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<RoundEntity> _result = new ArrayList<RoundEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoundEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPayoutReadyRounds(final Continuation<? super List<RoundEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM rounds \n"
            + "        WHERE status = 'PAYOUT'\n"
            + "        AND payout_initiated_at IS NULL\n"
            + "        ORDER BY due_date ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoundEntity>>() {
      @Override
      @NonNull
      public List<RoundEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<RoundEntity> _result = new ArrayList<RoundEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoundEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPayoutReadyRoundsForRosca(final String roscaId,
      final Continuation<? super List<RoundEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM rounds \n"
            + "        WHERE rosca_id = ? \n"
            + "        AND status = 'PAYOUT'\n"
            + "        AND payout_initiated_at IS NULL\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoundEntity>>() {
      @Override
      @NonNull
      public List<RoundEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<RoundEntity> _result = new ArrayList<RoundEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoundEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getDirtyRounds(final Continuation<? super List<RoundEntity>> $completion) {
    final String _sql = "SELECT * FROM rounds WHERE is_dirty = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoundEntity>>() {
      @Override
      @NonNull
      public List<RoundEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<RoundEntity> _result = new ArrayList<RoundEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoundEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getUnsyncedRounds(final long timestamp,
      final Continuation<? super List<RoundEntity>> $completion) {
    final String _sql = "SELECT * FROM rounds WHERE last_synced_at IS NULL OR last_synced_at < ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timestamp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoundEntity>>() {
      @Override
      @NonNull
      public List<RoundEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<RoundEntity> _result = new ArrayList<RoundEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoundEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRoundsByDateRange(final String roscaId, final long startTime, final long endTime,
      final Continuation<? super List<RoundEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM rounds \n"
            + "        WHERE rosca_id = ? \n"
            + "        AND started_at BETWEEN ? AND ?\n"
            + "        ORDER BY round_number ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoundEntity>>() {
      @Override
      @NonNull
      public List<RoundEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "round_number");
          final int _cursorIndexOfRecipientMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_member_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "target_amount");
          final int _cursorIndexOfCollectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "collected_amount");
          final int _cursorIndexOfExpectedContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_contributors");
          final int _cursorIndexOfActualContributors = CursorUtil.getColumnIndexOrThrow(_cursor, "actual_contributors");
          final int _cursorIndexOfPayoutAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "started_at");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfPayoutInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfPayoutTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_hash");
          final int _cursorIndexOfPayoutTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_tx_id");
          final int _cursorIndexOfPayoutConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_confirmations");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<RoundEntity> _result = new ArrayList<RoundEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoundEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientMemberId;
            _tmpRecipientMemberId = _cursor.getString(_cursorIndexOfRecipientMemberId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getLong(_cursorIndexOfTargetAmount);
            final long _tmpCollectedAmount;
            _tmpCollectedAmount = _cursor.getLong(_cursorIndexOfCollectedAmount);
            final int _tmpExpectedContributors;
            _tmpExpectedContributors = _cursor.getInt(_cursorIndexOfExpectedContributors);
            final int _tmpActualContributors;
            _tmpActualContributors = _cursor.getInt(_cursorIndexOfActualContributors);
            final Long _tmpPayoutAmount;
            if (_cursor.isNull(_cursorIndexOfPayoutAmount)) {
              _tmpPayoutAmount = null;
            } else {
              _tmpPayoutAmount = _cursor.getLong(_cursorIndexOfPayoutAmount);
            }
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Long _tmpPayoutInitiatedAt;
            if (_cursor.isNull(_cursorIndexOfPayoutInitiatedAt)) {
              _tmpPayoutInitiatedAt = null;
            } else {
              _tmpPayoutInitiatedAt = _cursor.getLong(_cursorIndexOfPayoutInitiatedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpPayoutTxHash;
            if (_cursor.isNull(_cursorIndexOfPayoutTxHash)) {
              _tmpPayoutTxHash = null;
            } else {
              _tmpPayoutTxHash = _cursor.getString(_cursorIndexOfPayoutTxHash);
            }
            final String _tmpPayoutTxId;
            if (_cursor.isNull(_cursorIndexOfPayoutTxId)) {
              _tmpPayoutTxId = null;
            } else {
              _tmpPayoutTxId = _cursor.getString(_cursorIndexOfPayoutTxId);
            }
            final int _tmpPayoutConfirmations;
            _tmpPayoutConfirmations = _cursor.getInt(_cursorIndexOfPayoutConfirmations);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new RoundEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpRecipientMemberId,_tmpRecipientAddress,_tmpStatus,_tmpTargetAmount,_tmpCollectedAmount,_tmpExpectedContributors,_tmpActualContributors,_tmpPayoutAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpStartedAt,_tmpDueDate,_tmpPayoutInitiatedAt,_tmpCompletedAt,_tmpPayoutTxHash,_tmpPayoutTxId,_tmpPayoutConfirmations,_tmpNotes,_tmpIpfsHash,_tmpIsDirty,_tmpLastSyncedAt,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAverageRoundDuration(final String roscaId,
      final Continuation<? super Long> $completion) {
    final String _sql = "\n"
            + "        SELECT AVG(completed_at - started_at) \n"
            + "        FROM rounds \n"
            + "        WHERE rosca_id = ? \n"
            + "        AND status = 'COMPLETED'\n"
            + "        AND completed_at IS NOT NULL\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAverageCollectedAmount(final String roscaId,
      final Continuation<? super Long> $completion) {
    final String _sql = "\n"
            + "        SELECT AVG(collected_amount) \n"
            + "        FROM rounds \n"
            + "        WHERE rosca_id = ? \n"
            + "        AND status = 'COMPLETED'\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsSynced(final List<String> roundIds, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE rounds SET is_dirty = 0, last_synced_at = ");
        _stringBuilder.append("?");
        _stringBuilder.append(" WHERE id IN (");
        final int _inputSize = roundIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        for (String _item : roundIds) {
          _stmt.bindString(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
