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
import androidx.sqlite.db.SupportSQLiteStatement;
import com.techducat.ajo.data.local.entity.DistributionEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
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
public final class DistributionDao_Impl implements DistributionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DistributionEntity> __insertionAdapterOfDistributionEntity;

  private final EntityDeletionOrUpdateAdapter<DistributionEntity> __deletionAdapterOfDistributionEntity;

  private final EntityDeletionOrUpdateAdapter<DistributionEntity> __updateAdapterOfDistributionEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  private final SharedSQLiteStatement __preparedStmtOfCompleteDistribution;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByRoscaId;

  public DistributionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDistributionEntity = new EntityInsertionAdapter<DistributionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `distributions` (`id`,`roscaId`,`roundId`,`roundNumber`,`recipientId`,`recipientAddress`,`amount`,`txHash`,`txId`,`status`,`createdAt`,`confirmedAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DistributionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindString(3, entity.getRoundId());
        statement.bindLong(4, entity.getRoundNumber());
        statement.bindString(5, entity.getRecipientId());
        statement.bindString(6, entity.getRecipientAddress());
        statement.bindLong(7, entity.getAmount());
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
        statement.bindString(10, entity.getStatus());
        statement.bindLong(11, entity.getCreatedAt());
        if (entity.getConfirmedAt() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getConfirmedAt());
        }
        statement.bindLong(13, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfDistributionEntity = new EntityDeletionOrUpdateAdapter<DistributionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `distributions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DistributionEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfDistributionEntity = new EntityDeletionOrUpdateAdapter<DistributionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `distributions` SET `id` = ?,`roscaId` = ?,`roundId` = ?,`roundNumber` = ?,`recipientId` = ?,`recipientAddress` = ?,`amount` = ?,`txHash` = ?,`txId` = ?,`status` = ?,`createdAt` = ?,`confirmedAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DistributionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindString(3, entity.getRoundId());
        statement.bindLong(4, entity.getRoundNumber());
        statement.bindString(5, entity.getRecipientId());
        statement.bindString(6, entity.getRecipientAddress());
        statement.bindLong(7, entity.getAmount());
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
        statement.bindString(10, entity.getStatus());
        statement.bindLong(11, entity.getCreatedAt());
        if (entity.getConfirmedAt() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getConfirmedAt());
        }
        statement.bindLong(13, entity.getUpdatedAt());
        statement.bindString(14, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE distributions SET status = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfCompleteDistribution = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE distributions \n"
                + "        SET txHash = ?, txId = ?, status = ?, confirmedAt = ?, updatedAt = ?\n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM distributions WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByRoscaId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM distributions WHERE roscaId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final DistributionEntity distribution,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDistributionEntity.insert(distribution);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<DistributionEntity> distributions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDistributionEntity.insert(distributions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final DistributionEntity distribution,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDistributionEntity.handle(distribution);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final DistributionEntity distribution,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDistributionEntity.handle(distribution);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final String distributionId, final String status, final long updatedAt,
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
        _stmt.bindString(_argIndex, distributionId);
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
  public Object completeDistribution(final String distributionId, final String txHash,
      final String txId, final String status, final long confirmedAt, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfCompleteDistribution.acquire();
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
        _stmt.bindLong(_argIndex, confirmedAt);
        _argIndex = 5;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 6;
        _stmt.bindString(_argIndex, distributionId);
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
          __preparedStmtOfCompleteDistribution.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final String distributionId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, distributionId);
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
  public Object getById(final String id,
      final Continuation<? super DistributionEntity> $completion) {
    final String _sql = "SELECT * FROM distributions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DistributionEntity>() {
      @Override
      @Nullable
      public DistributionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "roundId");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "roundNumber");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientId");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientAddress");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfConfirmedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmedAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DistributionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRoundId;
            _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
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
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpConfirmedAt;
            if (_cursor.isNull(_cursorIndexOfConfirmedAt)) {
              _tmpConfirmedAt = null;
            } else {
              _tmpConfirmedAt = _cursor.getLong(_cursorIndexOfConfirmedAt);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DistributionEntity(_tmpId,_tmpRoscaId,_tmpRoundId,_tmpRoundNumber,_tmpRecipientId,_tmpRecipientAddress,_tmpAmount,_tmpTxHash,_tmpTxId,_tmpStatus,_tmpCreatedAt,_tmpConfirmedAt,_tmpUpdatedAt);
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
  public Flow<DistributionEntity> observeById(final String id) {
    final String _sql = "SELECT * FROM distributions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"distributions"}, new Callable<DistributionEntity>() {
      @Override
      @Nullable
      public DistributionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "roundId");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "roundNumber");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientId");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientAddress");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfConfirmedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmedAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DistributionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRoundId;
            _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
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
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpConfirmedAt;
            if (_cursor.isNull(_cursorIndexOfConfirmedAt)) {
              _tmpConfirmedAt = null;
            } else {
              _tmpConfirmedAt = _cursor.getLong(_cursorIndexOfConfirmedAt);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DistributionEntity(_tmpId,_tmpRoscaId,_tmpRoundId,_tmpRoundNumber,_tmpRecipientId,_tmpRecipientAddress,_tmpAmount,_tmpTxHash,_tmpTxId,_tmpStatus,_tmpCreatedAt,_tmpConfirmedAt,_tmpUpdatedAt);
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
      final Continuation<? super List<DistributionEntity>> $completion) {
    final String _sql = "SELECT * FROM distributions WHERE roscaId = ? ORDER BY roundNumber DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DistributionEntity>>() {
      @Override
      @NonNull
      public List<DistributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "roundId");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "roundNumber");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientId");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientAddress");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfConfirmedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmedAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DistributionEntity> _result = new ArrayList<DistributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DistributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRoundId;
            _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
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
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpConfirmedAt;
            if (_cursor.isNull(_cursorIndexOfConfirmedAt)) {
              _tmpConfirmedAt = null;
            } else {
              _tmpConfirmedAt = _cursor.getLong(_cursorIndexOfConfirmedAt);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DistributionEntity(_tmpId,_tmpRoscaId,_tmpRoundId,_tmpRoundNumber,_tmpRecipientId,_tmpRecipientAddress,_tmpAmount,_tmpTxHash,_tmpTxId,_tmpStatus,_tmpCreatedAt,_tmpConfirmedAt,_tmpUpdatedAt);
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
  public Flow<List<DistributionEntity>> observeByRoscaId(final String roscaId) {
    final String _sql = "SELECT * FROM distributions WHERE roscaId = ? ORDER BY roundNumber DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"distributions"}, new Callable<List<DistributionEntity>>() {
      @Override
      @NonNull
      public List<DistributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "roundId");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "roundNumber");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientId");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientAddress");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfConfirmedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmedAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DistributionEntity> _result = new ArrayList<DistributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DistributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRoundId;
            _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
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
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpConfirmedAt;
            if (_cursor.isNull(_cursorIndexOfConfirmedAt)) {
              _tmpConfirmedAt = null;
            } else {
              _tmpConfirmedAt = _cursor.getLong(_cursorIndexOfConfirmedAt);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DistributionEntity(_tmpId,_tmpRoscaId,_tmpRoundId,_tmpRoundNumber,_tmpRecipientId,_tmpRecipientAddress,_tmpAmount,_tmpTxHash,_tmpTxId,_tmpStatus,_tmpCreatedAt,_tmpConfirmedAt,_tmpUpdatedAt);
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
  public Object getByRoundId(final String roundId,
      final Continuation<? super DistributionEntity> $completion) {
    final String _sql = "SELECT * FROM distributions WHERE roundId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roundId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DistributionEntity>() {
      @Override
      @Nullable
      public DistributionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "roundId");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "roundNumber");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientId");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientAddress");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfConfirmedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmedAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DistributionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRoundId;
            _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
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
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpConfirmedAt;
            if (_cursor.isNull(_cursorIndexOfConfirmedAt)) {
              _tmpConfirmedAt = null;
            } else {
              _tmpConfirmedAt = _cursor.getLong(_cursorIndexOfConfirmedAt);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DistributionEntity(_tmpId,_tmpRoscaId,_tmpRoundId,_tmpRoundNumber,_tmpRecipientId,_tmpRecipientAddress,_tmpAmount,_tmpTxHash,_tmpTxId,_tmpStatus,_tmpCreatedAt,_tmpConfirmedAt,_tmpUpdatedAt);
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
  public Object getByRound(final String roscaId, final int roundNumber,
      final Continuation<? super DistributionEntity> $completion) {
    final String _sql = "SELECT * FROM distributions WHERE roscaId = ? AND roundNumber = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, roundNumber);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DistributionEntity>() {
      @Override
      @Nullable
      public DistributionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "roundId");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "roundNumber");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientId");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientAddress");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfConfirmedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmedAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DistributionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRoundId;
            _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
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
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpConfirmedAt;
            if (_cursor.isNull(_cursorIndexOfConfirmedAt)) {
              _tmpConfirmedAt = null;
            } else {
              _tmpConfirmedAt = _cursor.getLong(_cursorIndexOfConfirmedAt);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DistributionEntity(_tmpId,_tmpRoscaId,_tmpRoundId,_tmpRoundNumber,_tmpRecipientId,_tmpRecipientAddress,_tmpAmount,_tmpTxHash,_tmpTxId,_tmpStatus,_tmpCreatedAt,_tmpConfirmedAt,_tmpUpdatedAt);
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
  public Object getByRecipient(final String recipientId,
      final Continuation<? super List<DistributionEntity>> $completion) {
    final String _sql = "SELECT * FROM distributions WHERE recipientId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, recipientId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DistributionEntity>>() {
      @Override
      @NonNull
      public List<DistributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "roundId");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "roundNumber");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientId");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientAddress");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfConfirmedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmedAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DistributionEntity> _result = new ArrayList<DistributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DistributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRoundId;
            _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
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
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpConfirmedAt;
            if (_cursor.isNull(_cursorIndexOfConfirmedAt)) {
              _tmpConfirmedAt = null;
            } else {
              _tmpConfirmedAt = _cursor.getLong(_cursorIndexOfConfirmedAt);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DistributionEntity(_tmpId,_tmpRoscaId,_tmpRoundId,_tmpRoundNumber,_tmpRecipientId,_tmpRecipientAddress,_tmpAmount,_tmpTxHash,_tmpTxId,_tmpStatus,_tmpCreatedAt,_tmpConfirmedAt,_tmpUpdatedAt);
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
  public Object getByStatus(final String status,
      final Continuation<? super List<DistributionEntity>> $completion) {
    final String _sql = "SELECT * FROM distributions WHERE status = ? ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DistributionEntity>>() {
      @Override
      @NonNull
      public List<DistributionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "roundId");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "roundNumber");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientId");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientAddress");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfConfirmedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmedAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DistributionEntity> _result = new ArrayList<DistributionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DistributionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRoundId;
            _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
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
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpConfirmedAt;
            if (_cursor.isNull(_cursorIndexOfConfirmedAt)) {
              _tmpConfirmedAt = null;
            } else {
              _tmpConfirmedAt = _cursor.getLong(_cursorIndexOfConfirmedAt);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DistributionEntity(_tmpId,_tmpRoscaId,_tmpRoundId,_tmpRoundNumber,_tmpRecipientId,_tmpRecipientAddress,_tmpAmount,_tmpTxHash,_tmpTxId,_tmpStatus,_tmpCreatedAt,_tmpConfirmedAt,_tmpUpdatedAt);
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
      final Continuation<? super DistributionEntity> $completion) {
    final String _sql = "SELECT * FROM distributions WHERE txHash = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, txHash);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DistributionEntity>() {
      @Override
      @Nullable
      public DistributionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "roundId");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "roundNumber");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientId");
          final int _cursorIndexOfRecipientAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientAddress");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfTxId = CursorUtil.getColumnIndexOrThrow(_cursor, "txId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfConfirmedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmedAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DistributionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpRoundId;
            _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpRecipientAddress;
            _tmpRecipientAddress = _cursor.getString(_cursorIndexOfRecipientAddress);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
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
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpConfirmedAt;
            if (_cursor.isNull(_cursorIndexOfConfirmedAt)) {
              _tmpConfirmedAt = null;
            } else {
              _tmpConfirmedAt = _cursor.getLong(_cursorIndexOfConfirmedAt);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DistributionEntity(_tmpId,_tmpRoscaId,_tmpRoundId,_tmpRoundNumber,_tmpRecipientId,_tmpRecipientAddress,_tmpAmount,_tmpTxHash,_tmpTxId,_tmpStatus,_tmpCreatedAt,_tmpConfirmedAt,_tmpUpdatedAt);
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
  public Object getTotalDistributed(final String roscaId,
      final Continuation<? super Long> $completion) {
    final String _sql = "SELECT SUM(amount) FROM distributions WHERE roscaId = ? AND status = 'completed'";
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
  public Object getDistributionCount(final String roscaId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM distributions WHERE roscaId = ?";
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
