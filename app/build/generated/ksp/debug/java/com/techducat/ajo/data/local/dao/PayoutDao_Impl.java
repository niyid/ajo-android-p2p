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
import com.techducat.ajo.data.local.entity.PayoutEntity;
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
public final class PayoutDao_Impl implements PayoutDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PayoutEntity> __insertionAdapterOfPayoutEntity;

  private final EntityDeletionOrUpdateAdapter<PayoutEntity> __deletionAdapterOfPayoutEntity;

  private final EntityDeletionOrUpdateAdapter<PayoutEntity> __updateAdapterOfPayoutEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  private final SharedSQLiteStatement __preparedStmtOfCompleteTransaction;

  private final SharedSQLiteStatement __preparedStmtOfUpdateConfirmations;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsFailed;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSyncStatus;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByRoscaId;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByRoundId;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldFailedPayouts;

  public PayoutDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPayoutEntity = new EntityInsertionAdapter<PayoutEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `payouts` (`id`,`rosca_id`,`recipient_id`,`round_id`,`payout_type`,`gross_amount`,`service_fee`,`penalty_amount`,`net_amount`,`tx_hash`,`tx_id`,`recipient_address`,`status`,`initiated_at`,`completed_at`,`failed_at`,`error_message`,`confirmations`,`verified_at`,`notes`,`created_at`,`updated_at`,`ipfs_hash`,`last_synced_at`,`is_dirty`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PayoutEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindString(3, entity.getRecipientId());
        if (entity.getRoundId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getRoundId());
        }
        statement.bindString(5, entity.getPayoutType());
        statement.bindLong(6, entity.getGrossAmount());
        statement.bindLong(7, entity.getServiceFee());
        statement.bindLong(8, entity.getPenaltyAmount());
        statement.bindLong(9, entity.getNetAmount());
        if (entity.getTxHash() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getTxHash());
        }
        if (entity.getTxId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getTxId());
        }
        statement.bindString(12, entity.getRecipientAddress());
        statement.bindString(13, entity.getStatus());
        statement.bindLong(14, entity.getInitiatedAt());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getCompletedAt());
        }
        if (entity.getFailedAt() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getFailedAt());
        }
        if (entity.getErrorMessage() == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.getErrorMessage());
        }
        statement.bindLong(18, entity.getConfirmations());
        if (entity.getVerifiedAt() == null) {
          statement.bindNull(19);
        } else {
          statement.bindLong(19, entity.getVerifiedAt());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, entity.getNotes());
        }
        statement.bindLong(21, entity.getCreatedAt());
        if (entity.getUpdatedAt() == null) {
          statement.bindNull(22);
        } else {
          statement.bindLong(22, entity.getUpdatedAt());
        }
        if (entity.getIpfsHash() == null) {
          statement.bindNull(23);
        } else {
          statement.bindString(23, entity.getIpfsHash());
        }
        if (entity.getLastSyncedAt() == null) {
          statement.bindNull(24);
        } else {
          statement.bindLong(24, entity.getLastSyncedAt());
        }
        final int _tmp = entity.isDirty() ? 1 : 0;
        statement.bindLong(25, _tmp);
      }
    };
    this.__deletionAdapterOfPayoutEntity = new EntityDeletionOrUpdateAdapter<PayoutEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `payouts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PayoutEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfPayoutEntity = new EntityDeletionOrUpdateAdapter<PayoutEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `payouts` SET `id` = ?,`rosca_id` = ?,`recipient_id` = ?,`round_id` = ?,`payout_type` = ?,`gross_amount` = ?,`service_fee` = ?,`penalty_amount` = ?,`net_amount` = ?,`tx_hash` = ?,`tx_id` = ?,`recipient_address` = ?,`status` = ?,`initiated_at` = ?,`completed_at` = ?,`failed_at` = ?,`error_message` = ?,`confirmations` = ?,`verified_at` = ?,`notes` = ?,`created_at` = ?,`updated_at` = ?,`ipfs_hash` = ?,`last_synced_at` = ?,`is_dirty` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PayoutEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindString(3, entity.getRecipientId());
        if (entity.getRoundId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getRoundId());
        }
        statement.bindString(5, entity.getPayoutType());
        statement.bindLong(6, entity.getGrossAmount());
        statement.bindLong(7, entity.getServiceFee());
        statement.bindLong(8, entity.getPenaltyAmount());
        statement.bindLong(9, entity.getNetAmount());
        if (entity.getTxHash() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getTxHash());
        }
        if (entity.getTxId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getTxId());
        }
        statement.bindString(12, entity.getRecipientAddress());
        statement.bindString(13, entity.getStatus());
        statement.bindLong(14, entity.getInitiatedAt());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getCompletedAt());
        }
        if (entity.getFailedAt() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getFailedAt());
        }
        if (entity.getErrorMessage() == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.getErrorMessage());
        }
        statement.bindLong(18, entity.getConfirmations());
        if (entity.getVerifiedAt() == null) {
          statement.bindNull(19);
        } else {
          statement.bindLong(19, entity.getVerifiedAt());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, entity.getNotes());
        }
        statement.bindLong(21, entity.getCreatedAt());
        if (entity.getUpdatedAt() == null) {
          statement.bindNull(22);
        } else {
          statement.bindLong(22, entity.getUpdatedAt());
        }
        if (entity.getIpfsHash() == null) {
          statement.bindNull(23);
        } else {
          statement.bindString(23, entity.getIpfsHash());
        }
        if (entity.getLastSyncedAt() == null) {
          statement.bindNull(24);
        } else {
          statement.bindLong(24, entity.getLastSyncedAt());
        }
        final int _tmp = entity.isDirty() ? 1 : 0;
        statement.bindLong(25, _tmp);
        statement.bindString(26, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE payouts SET \n"
                + "            status = ?, \n"
                + "            updated_at = ? \n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfCompleteTransaction = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE payouts SET \n"
                + "            tx_hash = ?, \n"
                + "            tx_id = ?, \n"
                + "            status = ?, \n"
                + "            completed_at = ?, \n"
                + "            updated_at = ? \n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateConfirmations = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE payouts SET \n"
                + "            confirmations = ?, \n"
                + "            verified_at = ?, \n"
                + "            updated_at = ? \n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsFailed = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE payouts SET \n"
                + "            status = ?, \n"
                + "            failed_at = ?, \n"
                + "            error_message = ?, \n"
                + "            updated_at = ? \n"
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
                + "        UPDATE payouts SET \n"
                + "            ipfs_hash = ?, \n"
                + "            last_synced_at = ?, \n"
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
        final String _query = "DELETE FROM payouts WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByRoscaId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM payouts WHERE rosca_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByRoundId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM payouts WHERE round_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldFailedPayouts = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        DELETE FROM payouts \n"
                + "        WHERE status = ? \n"
                + "        AND failed_at < ?\n"
                + "    ";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final PayoutEntity payout, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPayoutEntity.insert(payout);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<PayoutEntity> payouts,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPayoutEntity.insert(payouts);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final PayoutEntity payout, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfPayoutEntity.handle(payout);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final PayoutEntity payout, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPayoutEntity.handle(payout);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final String payoutId, final String status, final long updatedAt,
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
        _stmt.bindString(_argIndex, payoutId);
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
  public Object completeTransaction(final String payoutId, final String txHash, final String txId,
      final String status, final long completedAt, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfCompleteTransaction.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, txHash);
        _argIndex = 2;
        if (txId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, txId);
        }
        _argIndex = 3;
        _stmt.bindString(_argIndex, status);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, completedAt);
        _argIndex = 5;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 6;
        _stmt.bindString(_argIndex, payoutId);
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
          __preparedStmtOfCompleteTransaction.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateConfirmations(final String payoutId, final int confirmations,
      final Long verifiedAt, final long updatedAt, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateConfirmations.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, confirmations);
        _argIndex = 2;
        if (verifiedAt == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, verifiedAt);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 4;
        _stmt.bindString(_argIndex, payoutId);
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
          __preparedStmtOfUpdateConfirmations.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsFailed(final String payoutId, final String status, final long failedAt,
      final String errorMessage, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsFailed.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, failedAt);
        _argIndex = 3;
        if (errorMessage == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, errorMessage);
        }
        _argIndex = 4;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 5;
        _stmt.bindString(_argIndex, payoutId);
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
          __preparedStmtOfMarkAsFailed.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSyncStatus(final String payoutId, final String ipfsHash,
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
        _stmt.bindString(_argIndex, payoutId);
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
  public Object deleteById(final String payoutId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, payoutId);
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
  public Object deleteByRoundId(final String roundId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByRoundId.acquire();
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
          __preparedStmtOfDeleteByRoundId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldFailedPayouts(final String status, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldFailedPayouts.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
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
          __preparedStmtOfDeleteOldFailedPayouts.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final String payoutId,
      final Continuation<? super PayoutEntity> $completion) {
    final String _sql = "SELECT * FROM payouts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, payoutId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PayoutEntity>() {
      @Override
      @Nullable
      public PayoutEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final PayoutEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _result = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Flow<PayoutEntity> observeById(final String payoutId) {
    final String _sql = "SELECT * FROM payouts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, payoutId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"payouts"}, new Callable<PayoutEntity>() {
      @Override
      @Nullable
      public PayoutEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final PayoutEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _result = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object getByRoundId(final String roundId,
      final Continuation<? super PayoutEntity> $completion) {
    final String _sql = "SELECT * FROM payouts WHERE round_id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roundId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PayoutEntity>() {
      @Override
      @Nullable
      public PayoutEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final PayoutEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _result = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Flow<PayoutEntity> observeByRoundId(final String roundId) {
    final String _sql = "SELECT * FROM payouts WHERE round_id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roundId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"payouts"}, new Callable<PayoutEntity>() {
      @Override
      @Nullable
      public PayoutEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final PayoutEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _result = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object getByRoscaId(final String roscaId,
      final Continuation<? super List<PayoutEntity>> $completion) {
    final String _sql = "SELECT * FROM payouts WHERE rosca_id = ? ORDER BY initiated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _item = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Flow<List<PayoutEntity>> observeByRoscaId(final String roscaId) {
    final String _sql = "SELECT * FROM payouts WHERE rosca_id = ? ORDER BY initiated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"payouts"}, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _item = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object getByRecipientId(final String recipientId,
      final Continuation<? super List<PayoutEntity>> $completion) {
    final String _sql = "SELECT * FROM payouts WHERE recipient_id = ? ORDER BY initiated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, recipientId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _item = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Flow<List<PayoutEntity>> observeByRecipientId(final String recipientId) {
    final String _sql = "SELECT * FROM payouts WHERE recipient_id = ? ORDER BY initiated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, recipientId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"payouts"}, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _item = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object getByStatus(final String status,
      final Continuation<? super List<PayoutEntity>> $completion) {
    final String _sql = "SELECT * FROM payouts WHERE status = ? ORDER BY initiated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _item = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Flow<List<PayoutEntity>> observeByStatus(final String status) {
    final String _sql = "SELECT * FROM payouts WHERE status = ? ORDER BY initiated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"payouts"}, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _item = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object getByRoscaAndStatus(final String roscaId, final String status,
      final Continuation<? super List<PayoutEntity>> $completion) {
    final String _sql = "SELECT * FROM payouts WHERE rosca_id = ? AND status = ? ORDER BY initiated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _item = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object getByPayoutType(final String payoutType,
      final Continuation<? super List<PayoutEntity>> $completion) {
    final String _sql = "SELECT * FROM payouts WHERE payout_type = ? ORDER BY initiated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, payoutType);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _item = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object getByTxHash(final String txHash,
      final Continuation<? super PayoutEntity> $completion) {
    final String _sql = "SELECT * FROM payouts WHERE tx_hash = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, txHash);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PayoutEntity>() {
      @Override
      @Nullable
      public PayoutEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final PayoutEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _result = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object getByTxId(final String txId, final Continuation<? super PayoutEntity> $completion) {
    final String _sql = "SELECT * FROM payouts WHERE tx_id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, txId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PayoutEntity>() {
      @Override
      @Nullable
      public PayoutEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final PayoutEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _result = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object getPendingVerification(final String status, final int requiredConfirmations,
      final Continuation<? super List<PayoutEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM payouts \n"
            + "        WHERE status = ? \n"
            + "        AND confirmations < ?\n"
            + "        AND tx_hash IS NOT NULL\n"
            + "        ORDER BY initiated_at ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    _argIndex = 2;
    _statement.bindLong(_argIndex, requiredConfirmations);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _item = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object getUnverifiedTransactions(
      final Continuation<? super List<PayoutEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM payouts \n"
            + "        WHERE tx_hash IS NOT NULL \n"
            + "        AND verified_at IS NULL\n"
            + "        ORDER BY initiated_at ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _item = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object getTotalPayoutsByRosca(final String roscaId, final String status,
      final Continuation<? super Long> $completion) {
    final String _sql = "\n"
            + "        SELECT SUM(net_amount) \n"
            + "        FROM payouts \n"
            + "        WHERE rosca_id = ? \n"
            + "        AND status = ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
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
  public Object getTotalPenaltiesByRosca(final String roscaId,
      final Continuation<? super Long> $completion) {
    final String _sql = "SELECT SUM(penalty_amount) FROM payouts WHERE rosca_id = ?";
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
  public Object getTotalServiceFeesByRosca(final String roscaId, final String status,
      final Continuation<? super Long> $completion) {
    final String _sql = "\n"
            + "        SELECT SUM(service_fee) \n"
            + "        FROM payouts \n"
            + "        WHERE rosca_id = ? \n"
            + "        AND status = ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
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
  public Object getTotalPayoutsByRecipient(final String recipientId, final String status,
      final Continuation<? super Long> $completion) {
    final String _sql = "\n"
            + "        SELECT SUM(net_amount) \n"
            + "        FROM payouts \n"
            + "        WHERE recipient_id = ? \n"
            + "        AND status = ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, recipientId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
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
  public Object getPayoutCountByRosca(final String roscaId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM payouts WHERE rosca_id = ?";
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
  public Object getPayoutCountByRoscaAndStatus(final String roscaId, final String status,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM payouts WHERE rosca_id = ? AND status = ?";
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
  public Object getFailedPayouts(final String status,
      final Continuation<? super List<PayoutEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM payouts \n"
            + "        WHERE status = ? \n"
            + "        AND failed_at IS NOT NULL \n"
            + "        ORDER BY failed_at DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _item = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object getFailedPayoutsByRosca(final String roscaId, final String status,
      final Continuation<? super List<PayoutEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM payouts \n"
            + "        WHERE rosca_id = ? \n"
            + "        AND status = ?\n"
            + "        ORDER BY failed_at DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _item = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object getDirtyPayouts(final Continuation<? super List<PayoutEntity>> $completion) {
    final String _sql = "SELECT * FROM payouts WHERE is_dirty = 1 ORDER BY updated_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _item = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object getUnsyncedPayouts(final long timestamp,
      final Continuation<? super List<PayoutEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM payouts \n"
            + "        WHERE last_synced_at IS NULL \n"
            + "        OR last_synced_at < ?\n"
            + "        ORDER BY updated_at ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timestamp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _item = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object getPayoutsByDateRange(final String roscaId, final long startTime,
      final long endTime, final Continuation<? super List<PayoutEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM payouts \n"
            + "        WHERE rosca_id = ? \n"
            + "        AND initiated_at BETWEEN ? AND ? \n"
            + "        ORDER BY initiated_at DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _item = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Flow<List<PayoutEntity>> observePayoutsByDateRange(final long startTime,
      final long endTime) {
    final String _sql = "\n"
            + "        SELECT * FROM payouts \n"
            + "        WHERE initiated_at BETWEEN ? AND ? \n"
            + "        ORDER BY initiated_at DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"payouts"}, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _item = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object getLatestPayout(final String roscaId,
      final Continuation<? super PayoutEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM payouts \n"
            + "        WHERE rosca_id = ? \n"
            + "        ORDER BY initiated_at DESC \n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PayoutEntity>() {
      @Override
      @Nullable
      public PayoutEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "round_id");
          final int _cursorIndexOfPayoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_type");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "gross_amount");
          final int _cursorIndexOfServiceFee = CursorUtil.getColumnIndexOrThrow(_cursor, "service_fee");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "net_amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_hash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "tx_id");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipient_address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInitiatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "initiated_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfFailedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "failed_at");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verified_at");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfs_hash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "is_dirty");
          final PayoutEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRoundId;
            if (_cursor.isNull(_cursorIndexOfRoundId)) {
              _tmpRoundId = null;
            } else {
              _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            }
            final String _tmpPayoutType;
            _tmpPayoutType = _cursor.getString(_cursorIndexOfPayoutType);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpServiceFee;
            _tmpServiceFee = _cursor.getLong(_cursorIndexOfServiceFee);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpTxId;
            if (_cursor.isNull(_cursorIndexOfTxId)) {
              _tmpTxId = null;
            } else {
              _tmpTxId = _cursor.getString(_cursorIndexOfTxId);
            }
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInitiatedAt;
            _tmpInitiatedAt = _cursor.getLong(_cursorIndexOfInitiatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpFailedAt;
            if (_cursor.isNull(_cursorIndexOfFailedAt)) {
              _tmpFailedAt = null;
            } else {
              _tmpFailedAt = _cursor.getLong(_cursorIndexOfFailedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpVerifiedAt;
            if (_cursor.isNull(_cursorIndexOfVerifiedAt)) {
              _tmpVerifiedAt = null;
            } else {
              _tmpVerifiedAt = _cursor.getLong(_cursorIndexOfVerifiedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final Long _tmpLastSyncedAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncedAt)) {
              _tmpLastSyncedAt = null;
            } else {
              _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            }
            final boolean _tmpIsDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp != 0;
            _result = new PayoutEntity(_tmpId,_tmpRoscaId,_tmpRecipientId,_tmpRoundId,_tmpPayoutType,_tmpGrossAmount,_tmpServiceFee,_tmpPenaltyAmount,_tmpNetAmount,_tmpTxHash,_tmpTxId,_tmpRecipientAddress,_tmpStatus,_tmpInitiatedAt,_tmpCompletedAt,_tmpFailedAt,_tmpErrorMessage,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty);
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
  public Object hasPayoutForRound(final String roundId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM payouts WHERE round_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roundId);
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
  public Object getSuccessfulPayoutCount(final String roscaId, final String status,
      final Continuation<? super Integer> $completion) {
    final String _sql = "\n"
            + "        SELECT COUNT(*) FROM payouts \n"
            + "        WHERE rosca_id = ? \n"
            + "        AND status = ?\n"
            + "    ";
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
  public Object markAsSynced(final List<String> payoutIds, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("\n");
        _stringBuilder.append("        UPDATE payouts ");
        _stringBuilder.append("\n");
        _stringBuilder.append("        SET is_dirty = 0, last_synced_at = ");
        _stringBuilder.append("?");
        _stringBuilder.append(", updated_at = ");
        _stringBuilder.append("?");
        _stringBuilder.append("\n");
        _stringBuilder.append("        WHERE id IN (");
        final int _inputSize = payoutIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        _stringBuilder.append("\n");
        _stringBuilder.append("    ");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        for (String _item : payoutIds) {
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
