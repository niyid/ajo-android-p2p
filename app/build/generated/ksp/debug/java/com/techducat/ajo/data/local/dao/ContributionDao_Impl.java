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
import com.techducat.ajo.data.local.entity.ContributionEntity;
import java.lang.Boolean;
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
public final class ContributionDao_Impl implements ContributionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ContributionEntity> __insertionAdapterOfContributionEntity;

  private final EntityDeletionOrUpdateAdapter<ContributionEntity> __deletionAdapterOfContributionEntity;

  private final EntityDeletionOrUpdateAdapter<ContributionEntity> __updateAdapterOfContributionEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  private final SharedSQLiteStatement __preparedStmtOfCompleteContribution;

  private final SharedSQLiteStatement __preparedStmtOfUpdateConfirmations;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSyncStatus;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByRoscaId;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldCancelledContributions;

  public ContributionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfContributionEntity = new EntityInsertionAdapter<ContributionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `contributions` (`id`,`roscaId`,`memberId`,`amount`,`cycleNumber`,`status`,`dueDate`,`txHash`,`txId`,`proofOfPayment`,`paidAt`,`confirmations`,`verifiedAt`,`notes`,`createdAt`,`updated_at`,`isDirty`,`lastSyncedAt`,`ipfsHash`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ContributionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindString(3, entity.getMemberId());
        statement.bindLong(4, entity.getAmount());
        statement.bindLong(5, entity.getCycleNumber());
        statement.bindString(6, entity.getStatus());
        statement.bindLong(7, entity.getDueDate());
        if (entity.getTxHash() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getTxHash());
        }
        if (entity.getTxId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getTxId());
        }
        if (entity.getProofOfPayment() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getProofOfPayment());
        }
        if (entity.getPaidAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getPaidAt());
        }
        statement.bindLong(12, entity.getConfirmations());
        if (entity.getVerifiedAt() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getVerifiedAt());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getNotes());
        }
        statement.bindLong(15, entity.getCreatedAt());
        if (entity.getUpdatedAt() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getUpdatedAt());
        }
        final int _tmp = entity.isDirty() ? 1 : 0;
        statement.bindLong(17, _tmp);
        if (entity.getLastSyncedAt() == null) {
          statement.bindNull(18);
        } else {
          statement.bindLong(18, entity.getLastSyncedAt());
        }
        if (entity.getIpfsHash() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getIpfsHash());
        }
      }
    };
    this.__deletionAdapterOfContributionEntity = new EntityDeletionOrUpdateAdapter<ContributionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `contributions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ContributionEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfContributionEntity = new EntityDeletionOrUpdateAdapter<ContributionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `contributions` SET `id` = ?,`roscaId` = ?,`memberId` = ?,`amount` = ?,`cycleNumber` = ?,`status` = ?,`dueDate` = ?,`txHash` = ?,`txId` = ?,`proofOfPayment` = ?,`paidAt` = ?,`confirmations` = ?,`verifiedAt` = ?,`notes` = ?,`createdAt` = ?,`updated_at` = ?,`isDirty` = ?,`lastSyncedAt` = ?,`ipfsHash` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ContributionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindString(3, entity.getMemberId());
        statement.bindLong(4, entity.getAmount());
        statement.bindLong(5, entity.getCycleNumber());
        statement.bindString(6, entity.getStatus());
        statement.bindLong(7, entity.getDueDate());
        if (entity.getTxHash() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getTxHash());
        }
        if (entity.getTxId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getTxId());
        }
        if (entity.getProofOfPayment() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getProofOfPayment());
        }
        if (entity.getPaidAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getPaidAt());
        }
        statement.bindLong(12, entity.getConfirmations());
        if (entity.getVerifiedAt() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getVerifiedAt());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getNotes());
        }
        statement.bindLong(15, entity.getCreatedAt());
        if (entity.getUpdatedAt() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getUpdatedAt());
        }
        final int _tmp = entity.isDirty() ? 1 : 0;
        statement.bindLong(17, _tmp);
        if (entity.getLastSyncedAt() == null) {
          statement.bindNull(18);
        } else {
          statement.bindLong(18, entity.getLastSyncedAt());
        }
        if (entity.getIpfsHash() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getIpfsHash());
        }
        statement.bindString(20, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE contributions \n"
                + "        SET status = ?, updated_at = ? \n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfCompleteContribution = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE contributions \n"
                + "        SET \n"
                + "            txHash = ?,\n"
                + "            txId = ?,\n"
                + "            status = ?,\n"
                + "            paidAt = ?,\n"
                + "            updated_at = ?\n"
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
                + "        UPDATE contributions \n"
                + "        SET \n"
                + "            confirmations = ?,\n"
                + "            verifiedAt = ?,\n"
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
                + "        UPDATE contributions \n"
                + "        SET \n"
                + "            ipfsHash = ?,\n"
                + "            lastSyncedAt = ?,\n"
                + "            isDirty = ?,\n"
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
        final String _query = "DELETE FROM contributions WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByRoscaId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM contributions WHERE roscaId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldCancelledContributions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        DELETE FROM contributions \n"
                + "        WHERE status = 'cancelled' \n"
                + "        AND updated_at < ?\n"
                + "    ";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ContributionEntity contribution,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfContributionEntity.insert(contribution);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<ContributionEntity> contributions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfContributionEntity.insert(contributions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final ContributionEntity contribution,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfContributionEntity.handle(contribution);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ContributionEntity contribution,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfContributionEntity.handle(contribution);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final String contributionId, final String status, final long updatedAt,
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
        _stmt.bindString(_argIndex, contributionId);
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
  public Object completeContribution(final String contributionId, final String txHash,
      final String txId, final String status, final long paidAt, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfCompleteContribution.acquire();
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
        _stmt.bindLong(_argIndex, paidAt);
        _argIndex = 5;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 6;
        _stmt.bindString(_argIndex, contributionId);
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
          __preparedStmtOfCompleteContribution.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateConfirmations(final String contributionId, final int confirmations,
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
        _stmt.bindString(_argIndex, contributionId);
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
  public Object updateSyncStatus(final String contributionId, final String ipfsHash,
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
        _stmt.bindString(_argIndex, contributionId);
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
  public Object deleteById(final String contributionId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, contributionId);
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
  public Object deleteOldCancelledContributions(final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldCancelledContributions.acquire();
        int _argIndex = 1;
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
          __preparedStmtOfDeleteOldCancelledContributions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getContributionById(final String id,
      final Continuation<? super ContributionEntity> $completion) {
    final String _sql = "SELECT * FROM contributions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ContributionEntity>() {
      @Override
      @Nullable
      public ContributionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final ContributionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _result = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Flow<ContributionEntity> observeContributionById(final String id) {
    final String _sql = "SELECT * FROM contributions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"contributions"}, new Callable<ContributionEntity>() {
      @Override
      @Nullable
      public ContributionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final ContributionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _result = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getContributionByMemberAndCycle(final String memberId, final String roscaId,
      final int cycleNumber, final Continuation<? super ContributionEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM contributions \n"
            + "        WHERE memberId = ? \n"
            + "        AND roscaId = ? \n"
            + "        AND cycleNumber = ?\n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, memberId);
    _argIndex = 2;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 3;
    _statement.bindLong(_argIndex, cycleNumber);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ContributionEntity>() {
      @Override
      @Nullable
      public ContributionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final ContributionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _result = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getContributionsByRoscaSync(final String roscaId,
      final Continuation<? super List<ContributionEntity>> $completion) {
    final String _sql = "SELECT * FROM contributions WHERE roscaId = ? ORDER BY dueDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Flow<List<ContributionEntity>> getContributionsByRosca(final String roscaId) {
    final String _sql = "SELECT * FROM contributions WHERE roscaId = ? ORDER BY dueDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"contributions"}, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getContributionsByRound(final String roscaId, final int cycleNumber,
      final Continuation<? super List<ContributionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM contributions \n"
            + "        WHERE roscaId = ? \n"
            + "        AND cycleNumber = ?\n"
            + "        ORDER BY memberId ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, cycleNumber);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Flow<List<ContributionEntity>> observeContributionsByRound(final String roscaId,
      final int cycleNumber) {
    final String _sql = "\n"
            + "        SELECT * FROM contributions \n"
            + "        WHERE roscaId = ? \n"
            + "        AND cycleNumber = ?\n"
            + "        ORDER BY memberId ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, cycleNumber);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"contributions"}, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getByMemberAndRosca(final String memberId, final String roscaId,
      final Continuation<? super List<ContributionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM contributions \n"
            + "        WHERE memberId = ? \n"
            + "        AND roscaId = ?\n"
            + "        ORDER BY cycleNumber DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, memberId);
    _argIndex = 2;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Flow<List<ContributionEntity>> observeByMemberAndRosca(final String memberId,
      final String roscaId) {
    final String _sql = "\n"
            + "        SELECT * FROM contributions \n"
            + "        WHERE memberId = ? \n"
            + "        AND roscaId = ?\n"
            + "        ORDER BY cycleNumber DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, memberId);
    _argIndex = 2;
    _statement.bindString(_argIndex, roscaId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"contributions"}, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getPendingContributions(
      final Continuation<? super List<ContributionEntity>> $completion) {
    final String _sql = "SELECT * FROM contributions WHERE status = 'pending' ORDER BY dueDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Flow<List<ContributionEntity>> observePendingContributions() {
    final String _sql = "SELECT * FROM contributions WHERE status = 'pending' ORDER BY dueDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"contributions"}, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getContributionsByStatus(final String roscaId, final String status,
      final Continuation<? super List<ContributionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM contributions \n"
            + "        WHERE roscaId = ? \n"
            + "        AND status = ?\n"
            + "        ORDER BY dueDate ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getContributionsByRoundAndStatus(final String roscaId, final int cycleNumber,
      final String status, final Continuation<? super List<ContributionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM contributions \n"
            + "        WHERE roscaId = ? \n"
            + "        AND cycleNumber = ? \n"
            + "        AND status = ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, cycleNumber);
    _argIndex = 3;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getByTxId(final String txId,
      final Continuation<? super ContributionEntity> $completion) {
    final String _sql = "SELECT * FROM contributions WHERE txId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, txId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ContributionEntity>() {
      @Override
      @Nullable
      public ContributionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final ContributionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _result = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getByTxHash(final String txHash,
      final Continuation<? super ContributionEntity> $completion) {
    final String _sql = "SELECT * FROM contributions WHERE txHash = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, txHash);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ContributionEntity>() {
      @Override
      @Nullable
      public ContributionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final ContributionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _result = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getUnverifiedContributions(
      final Continuation<? super List<ContributionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM contributions \n"
            + "        WHERE txHash IS NOT NULL \n"
            + "        AND verifiedAt IS NULL\n"
            + "        AND status = 'paid'\n"
            + "        ORDER BY paidAt ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getPendingVerification(final int requiredConfirmations,
      final Continuation<? super List<ContributionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM contributions \n"
            + "        WHERE status = 'paid' \n"
            + "        AND confirmations < ?\n"
            + "        AND txHash IS NOT NULL\n"
            + "        ORDER BY paidAt ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, requiredConfirmations);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getDirtyContributions(
      final Continuation<? super List<ContributionEntity>> $completion) {
    final String _sql = "SELECT * FROM contributions WHERE isDirty = 1 ORDER BY updated_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getUnsyncedContributions(final long timestamp,
      final Continuation<? super List<ContributionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM contributions \n"
            + "        WHERE lastSyncedAt IS NULL \n"
            + "        OR lastSyncedAt < ?\n"
            + "        ORDER BY updated_at ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timestamp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getTotalContributedAmount(final String roscaId,
      final Continuation<? super Long> $completion) {
    final String _sql = "\n"
            + "        SELECT SUM(amount) \n"
            + "        FROM contributions \n"
            + "        WHERE roscaId = ? \n"
            + "        AND status = 'paid'\n"
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
  public Object getTotalForRound(final String roscaId, final int cycleNumber,
      final Continuation<? super Long> $completion) {
    final String _sql = "\n"
            + "        SELECT SUM(amount) \n"
            + "        FROM contributions \n"
            + "        WHERE roscaId = ? \n"
            + "        AND cycleNumber = ? \n"
            + "        AND status = 'paid'\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, cycleNumber);
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
  public Object getPaidContributionCountForRound(final String roscaId, final int cycleNumber,
      final Continuation<? super Integer> $completion) {
    final String _sql = "\n"
            + "        SELECT COUNT(*) \n"
            + "        FROM contributions \n"
            + "        WHERE roscaId = ? \n"
            + "        AND cycleNumber = ? \n"
            + "        AND status = 'paid'\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, cycleNumber);
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
  public Object getTotalContributionCountForRound(final String roscaId, final int cycleNumber,
      final Continuation<? super Integer> $completion) {
    final String _sql = "\n"
            + "        SELECT COUNT(*) \n"
            + "        FROM contributions \n"
            + "        WHERE roscaId = ? \n"
            + "        AND cycleNumber = ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, cycleNumber);
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
  public Object isRoundComplete(final String roscaId, final int cycleNumber,
      final Continuation<? super Boolean> $completion) {
    final String _sql = "\n"
            + "        SELECT COUNT(*) = 0\n"
            + "        FROM contributions \n"
            + "        WHERE roscaId = ? \n"
            + "        AND cycleNumber = ? \n"
            + "        AND status != 'paid'\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, cycleNumber);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Boolean>() {
      @Override
      @NonNull
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp != 0;
          } else {
            _result = false;
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
  public Object getMemberPaidContributionCount(final String memberId, final String roscaId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "\n"
            + "        SELECT COUNT(*) \n"
            + "        FROM contributions \n"
            + "        WHERE memberId = ? \n"
            + "        AND roscaId = ? \n"
            + "        AND status = 'paid'\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, memberId);
    _argIndex = 2;
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
  public Object getMemberTotalContributed(final String memberId, final String roscaId,
      final Continuation<? super Long> $completion) {
    final String _sql = "\n"
            + "        SELECT SUM(amount) \n"
            + "        FROM contributions \n"
            + "        WHERE memberId = ? \n"
            + "        AND roscaId = ? \n"
            + "        AND status = 'paid'\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, memberId);
    _argIndex = 2;
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
  public Object getOverdueContributions(final long currentTime,
      final Continuation<? super List<ContributionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM contributions \n"
            + "        WHERE status = 'pending' \n"
            + "        AND dueDate < ?\n"
            + "        ORDER BY dueDate ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, currentTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getOverdueContributionsForRosca(final String roscaId, final long currentTime,
      final Continuation<? super List<ContributionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM contributions \n"
            + "        WHERE roscaId = ? \n"
            + "        AND status = 'pending' \n"
            + "        AND dueDate < ?\n"
            + "        ORDER BY dueDate ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, currentTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getOverdueContributionsForRound(final String roscaId, final int cycleNumber,
      final long currentTime, final Continuation<? super List<ContributionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM contributions \n"
            + "        WHERE roscaId = ? \n"
            + "        AND cycleNumber = ? \n"
            + "        AND status = 'pending' \n"
            + "        AND dueDate < ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, cycleNumber);
    _argIndex = 3;
    _statement.bindLong(_argIndex, currentTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object getContributionsByDateRange(final String roscaId, final long startTime,
      final long endTime, final Continuation<? super List<ContributionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM contributions \n"
            + "        WHERE roscaId = ? \n"
            + "        AND dueDate BETWEEN ? AND ?\n"
            + "        ORDER BY dueDate ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContributionEntity>>() {
      @Override
      @NonNull
      public List<ContributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfProofOfPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "proofOfPayment");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfVerifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final List<ContributionEntity> _result = new ArrayList<ContributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
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
            final String _tmpProofOfPayment;
            if (_cursor.isNull(_cursorIndexOfProofOfPayment)) {
              _tmpProofOfPayment = null;
            } else {
              _tmpProofOfPayment = _cursor.getString(_cursorIndexOfProofOfPayment);
            }
            final Long _tmpPaidAt;
            if (_cursor.isNull(_cursorIndexOfPaidAt)) {
              _tmpPaidAt = null;
            } else {
              _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
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
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            _item = new ContributionEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpAmount,_tmpCycleNumber,_tmpStatus,_tmpDueDate,_tmpTxHash,_tmpTxId,_tmpProofOfPayment,_tmpPaidAt,_tmpConfirmations,_tmpVerifiedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDirty,_tmpLastSyncedAt,_tmpIpfsHash);
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
  public Object markAsSynced(final List<String> contributionIds, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("\n");
        _stringBuilder.append("        UPDATE contributions ");
        _stringBuilder.append("\n");
        _stringBuilder.append("        SET isDirty = 0, lastSyncedAt = ");
        _stringBuilder.append("?");
        _stringBuilder.append(", updated_at = ");
        _stringBuilder.append("?");
        _stringBuilder.append("\n");
        _stringBuilder.append("        WHERE id IN (");
        final int _inputSize = contributionIds.size();
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
        for (String _item : contributionIds) {
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
