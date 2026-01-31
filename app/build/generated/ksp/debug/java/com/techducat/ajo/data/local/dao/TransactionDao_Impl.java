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
import com.techducat.ajo.data.local.entity.TransactionEntity;
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
public final class TransactionDao_Impl implements TransactionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TransactionEntity> __insertionAdapterOfTransactionEntity;

  private final EntityDeletionOrUpdateAdapter<TransactionEntity> __deletionAdapterOfTransactionEntity;

  private final EntityDeletionOrUpdateAdapter<TransactionEntity> __updateAdapterOfTransactionEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldCompletedTransactions;

  public TransactionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTransactionEntity = new EntityInsertionAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `transactions` (`id`,`txHash`,`status`,`confirmations`,`confirmedAt`,`createdAt`,`roscaId`,`type`,`amount`,`fromAddress`,`toAddress`,`blockHeight`,`timestamp`,`requiredSignatures`,`currentSignatureCount`,`syncVersion`,`lastModifiedBy`,`lastModifiedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        statement.bindString(1, entity.getId());
        if (entity.getTxHash() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTxHash());
        }
        statement.bindString(3, entity.getStatus());
        statement.bindLong(4, entity.getConfirmations());
        if (entity.getConfirmedAt() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getConfirmedAt());
        }
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindString(7, entity.getRoscaId());
        statement.bindString(8, entity.getType());
        statement.bindLong(9, entity.getAmount());
        if (entity.getFromAddress() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getFromAddress());
        }
        if (entity.getToAddress() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getToAddress());
        }
        if (entity.getBlockHeight() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getBlockHeight());
        }
        statement.bindLong(13, entity.getTimestamp());
        statement.bindLong(14, entity.getRequiredSignatures());
        statement.bindLong(15, entity.getCurrentSignatureCount());
        statement.bindLong(16, entity.getSyncVersion());
        if (entity.getLastModifiedBy() == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.getLastModifiedBy());
        }
        statement.bindLong(18, entity.getLastModifiedAt());
      }
    };
    this.__deletionAdapterOfTransactionEntity = new EntityDeletionOrUpdateAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `transactions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfTransactionEntity = new EntityDeletionOrUpdateAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `transactions` SET `id` = ?,`txHash` = ?,`status` = ?,`confirmations` = ?,`confirmedAt` = ?,`createdAt` = ?,`roscaId` = ?,`type` = ?,`amount` = ?,`fromAddress` = ?,`toAddress` = ?,`blockHeight` = ?,`timestamp` = ?,`requiredSignatures` = ?,`currentSignatureCount` = ?,`syncVersion` = ?,`lastModifiedBy` = ?,`lastModifiedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        statement.bindString(1, entity.getId());
        if (entity.getTxHash() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTxHash());
        }
        statement.bindString(3, entity.getStatus());
        statement.bindLong(4, entity.getConfirmations());
        if (entity.getConfirmedAt() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getConfirmedAt());
        }
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindString(7, entity.getRoscaId());
        statement.bindString(8, entity.getType());
        statement.bindLong(9, entity.getAmount());
        if (entity.getFromAddress() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getFromAddress());
        }
        if (entity.getToAddress() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getToAddress());
        }
        if (entity.getBlockHeight() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getBlockHeight());
        }
        statement.bindLong(13, entity.getTimestamp());
        statement.bindLong(14, entity.getRequiredSignatures());
        statement.bindLong(15, entity.getCurrentSignatureCount());
        statement.bindLong(16, entity.getSyncVersion());
        if (entity.getLastModifiedBy() == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.getLastModifiedBy());
        }
        statement.bindLong(18, entity.getLastModifiedAt());
        statement.bindString(19, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteOldCompletedTransactions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM transactions WHERE status = 'confirmed' AND confirmedAt < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final TransactionEntity transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTransactionEntity.insert(transaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final TransactionEntity transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTransactionEntity.handle(transaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final TransactionEntity transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTransactionEntity.handle(transaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldCompletedTransactions(final long cutoffTime,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldCompletedTransactions.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoffTime);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteOldCompletedTransactions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object get(final String id, final Continuation<? super TransactionEntity> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TransactionEntity>() {
      @Override
      @Nullable
      public TransactionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfConfirmedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfFromAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "fromAddress");
          final int _cursorIndexOfToAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "toAddress");
          final int _cursorIndexOfBlockHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "blockHeight");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRequiredSignatures = CursorUtil.getColumnIndexOrThrow(_cursor, "requiredSignatures");
          final int _cursorIndexOfCurrentSignatureCount = CursorUtil.getColumnIndexOrThrow(_cursor, "currentSignatureCount");
          final int _cursorIndexOfSyncVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "syncVersion");
          final int _cursorIndexOfLastModifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedBy");
          final int _cursorIndexOfLastModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedAt");
          final TransactionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpConfirmedAt;
            if (_cursor.isNull(_cursorIndexOfConfirmedAt)) {
              _tmpConfirmedAt = null;
            } else {
              _tmpConfirmedAt = _cursor.getLong(_cursorIndexOfConfirmedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final String _tmpFromAddress;
            if (_cursor.isNull(_cursorIndexOfFromAddress)) {
              _tmpFromAddress = null;
            } else {
              _tmpFromAddress = _cursor.getString(_cursorIndexOfFromAddress);
            }
            final String _tmpToAddress;
            if (_cursor.isNull(_cursorIndexOfToAddress)) {
              _tmpToAddress = null;
            } else {
              _tmpToAddress = _cursor.getString(_cursorIndexOfToAddress);
            }
            final Long _tmpBlockHeight;
            if (_cursor.isNull(_cursorIndexOfBlockHeight)) {
              _tmpBlockHeight = null;
            } else {
              _tmpBlockHeight = _cursor.getLong(_cursorIndexOfBlockHeight);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpRequiredSignatures;
            _tmpRequiredSignatures = _cursor.getInt(_cursorIndexOfRequiredSignatures);
            final int _tmpCurrentSignatureCount;
            _tmpCurrentSignatureCount = _cursor.getInt(_cursorIndexOfCurrentSignatureCount);
            final long _tmpSyncVersion;
            _tmpSyncVersion = _cursor.getLong(_cursorIndexOfSyncVersion);
            final String _tmpLastModifiedBy;
            if (_cursor.isNull(_cursorIndexOfLastModifiedBy)) {
              _tmpLastModifiedBy = null;
            } else {
              _tmpLastModifiedBy = _cursor.getString(_cursorIndexOfLastModifiedBy);
            }
            final long _tmpLastModifiedAt;
            _tmpLastModifiedAt = _cursor.getLong(_cursorIndexOfLastModifiedAt);
            _result = new TransactionEntity(_tmpId,_tmpTxHash,_tmpStatus,_tmpConfirmations,_tmpConfirmedAt,_tmpCreatedAt,_tmpRoscaId,_tmpType,_tmpAmount,_tmpFromAddress,_tmpToAddress,_tmpBlockHeight,_tmpTimestamp,_tmpRequiredSignatures,_tmpCurrentSignatureCount,_tmpSyncVersion,_tmpLastModifiedBy,_tmpLastModifiedAt);
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
      final Continuation<? super TransactionEntity> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE txHash = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, txHash);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TransactionEntity>() {
      @Override
      @Nullable
      public TransactionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfConfirmedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfFromAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "fromAddress");
          final int _cursorIndexOfToAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "toAddress");
          final int _cursorIndexOfBlockHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "blockHeight");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRequiredSignatures = CursorUtil.getColumnIndexOrThrow(_cursor, "requiredSignatures");
          final int _cursorIndexOfCurrentSignatureCount = CursorUtil.getColumnIndexOrThrow(_cursor, "currentSignatureCount");
          final int _cursorIndexOfSyncVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "syncVersion");
          final int _cursorIndexOfLastModifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedBy");
          final int _cursorIndexOfLastModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedAt");
          final TransactionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpConfirmedAt;
            if (_cursor.isNull(_cursorIndexOfConfirmedAt)) {
              _tmpConfirmedAt = null;
            } else {
              _tmpConfirmedAt = _cursor.getLong(_cursorIndexOfConfirmedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final String _tmpFromAddress;
            if (_cursor.isNull(_cursorIndexOfFromAddress)) {
              _tmpFromAddress = null;
            } else {
              _tmpFromAddress = _cursor.getString(_cursorIndexOfFromAddress);
            }
            final String _tmpToAddress;
            if (_cursor.isNull(_cursorIndexOfToAddress)) {
              _tmpToAddress = null;
            } else {
              _tmpToAddress = _cursor.getString(_cursorIndexOfToAddress);
            }
            final Long _tmpBlockHeight;
            if (_cursor.isNull(_cursorIndexOfBlockHeight)) {
              _tmpBlockHeight = null;
            } else {
              _tmpBlockHeight = _cursor.getLong(_cursorIndexOfBlockHeight);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpRequiredSignatures;
            _tmpRequiredSignatures = _cursor.getInt(_cursorIndexOfRequiredSignatures);
            final int _tmpCurrentSignatureCount;
            _tmpCurrentSignatureCount = _cursor.getInt(_cursorIndexOfCurrentSignatureCount);
            final long _tmpSyncVersion;
            _tmpSyncVersion = _cursor.getLong(_cursorIndexOfSyncVersion);
            final String _tmpLastModifiedBy;
            if (_cursor.isNull(_cursorIndexOfLastModifiedBy)) {
              _tmpLastModifiedBy = null;
            } else {
              _tmpLastModifiedBy = _cursor.getString(_cursorIndexOfLastModifiedBy);
            }
            final long _tmpLastModifiedAt;
            _tmpLastModifiedAt = _cursor.getLong(_cursorIndexOfLastModifiedAt);
            _result = new TransactionEntity(_tmpId,_tmpTxHash,_tmpStatus,_tmpConfirmations,_tmpConfirmedAt,_tmpCreatedAt,_tmpRoscaId,_tmpType,_tmpAmount,_tmpFromAddress,_tmpToAddress,_tmpBlockHeight,_tmpTimestamp,_tmpRequiredSignatures,_tmpCurrentSignatureCount,_tmpSyncVersion,_tmpLastModifiedBy,_tmpLastModifiedAt);
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
  public Object getByRosca(final String roscaId,
      final Continuation<? super List<TransactionEntity>> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE roscaId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfConfirmedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfFromAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "fromAddress");
          final int _cursorIndexOfToAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "toAddress");
          final int _cursorIndexOfBlockHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "blockHeight");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRequiredSignatures = CursorUtil.getColumnIndexOrThrow(_cursor, "requiredSignatures");
          final int _cursorIndexOfCurrentSignatureCount = CursorUtil.getColumnIndexOrThrow(_cursor, "currentSignatureCount");
          final int _cursorIndexOfSyncVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "syncVersion");
          final int _cursorIndexOfLastModifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedBy");
          final int _cursorIndexOfLastModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedAt");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpConfirmedAt;
            if (_cursor.isNull(_cursorIndexOfConfirmedAt)) {
              _tmpConfirmedAt = null;
            } else {
              _tmpConfirmedAt = _cursor.getLong(_cursorIndexOfConfirmedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final String _tmpFromAddress;
            if (_cursor.isNull(_cursorIndexOfFromAddress)) {
              _tmpFromAddress = null;
            } else {
              _tmpFromAddress = _cursor.getString(_cursorIndexOfFromAddress);
            }
            final String _tmpToAddress;
            if (_cursor.isNull(_cursorIndexOfToAddress)) {
              _tmpToAddress = null;
            } else {
              _tmpToAddress = _cursor.getString(_cursorIndexOfToAddress);
            }
            final Long _tmpBlockHeight;
            if (_cursor.isNull(_cursorIndexOfBlockHeight)) {
              _tmpBlockHeight = null;
            } else {
              _tmpBlockHeight = _cursor.getLong(_cursorIndexOfBlockHeight);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpRequiredSignatures;
            _tmpRequiredSignatures = _cursor.getInt(_cursorIndexOfRequiredSignatures);
            final int _tmpCurrentSignatureCount;
            _tmpCurrentSignatureCount = _cursor.getInt(_cursorIndexOfCurrentSignatureCount);
            final long _tmpSyncVersion;
            _tmpSyncVersion = _cursor.getLong(_cursorIndexOfSyncVersion);
            final String _tmpLastModifiedBy;
            if (_cursor.isNull(_cursorIndexOfLastModifiedBy)) {
              _tmpLastModifiedBy = null;
            } else {
              _tmpLastModifiedBy = _cursor.getString(_cursorIndexOfLastModifiedBy);
            }
            final long _tmpLastModifiedAt;
            _tmpLastModifiedAt = _cursor.getLong(_cursorIndexOfLastModifiedAt);
            _item = new TransactionEntity(_tmpId,_tmpTxHash,_tmpStatus,_tmpConfirmations,_tmpConfirmedAt,_tmpCreatedAt,_tmpRoscaId,_tmpType,_tmpAmount,_tmpFromAddress,_tmpToAddress,_tmpBlockHeight,_tmpTimestamp,_tmpRequiredSignatures,_tmpCurrentSignatureCount,_tmpSyncVersion,_tmpLastModifiedBy,_tmpLastModifiedAt);
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
  public Flow<List<TransactionEntity>> getByRoscaFlow(final String roscaId) {
    final String _sql = "SELECT * FROM transactions WHERE roscaId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfConfirmedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfFromAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "fromAddress");
          final int _cursorIndexOfToAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "toAddress");
          final int _cursorIndexOfBlockHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "blockHeight");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRequiredSignatures = CursorUtil.getColumnIndexOrThrow(_cursor, "requiredSignatures");
          final int _cursorIndexOfCurrentSignatureCount = CursorUtil.getColumnIndexOrThrow(_cursor, "currentSignatureCount");
          final int _cursorIndexOfSyncVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "syncVersion");
          final int _cursorIndexOfLastModifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedBy");
          final int _cursorIndexOfLastModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedAt");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpConfirmedAt;
            if (_cursor.isNull(_cursorIndexOfConfirmedAt)) {
              _tmpConfirmedAt = null;
            } else {
              _tmpConfirmedAt = _cursor.getLong(_cursorIndexOfConfirmedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final String _tmpFromAddress;
            if (_cursor.isNull(_cursorIndexOfFromAddress)) {
              _tmpFromAddress = null;
            } else {
              _tmpFromAddress = _cursor.getString(_cursorIndexOfFromAddress);
            }
            final String _tmpToAddress;
            if (_cursor.isNull(_cursorIndexOfToAddress)) {
              _tmpToAddress = null;
            } else {
              _tmpToAddress = _cursor.getString(_cursorIndexOfToAddress);
            }
            final Long _tmpBlockHeight;
            if (_cursor.isNull(_cursorIndexOfBlockHeight)) {
              _tmpBlockHeight = null;
            } else {
              _tmpBlockHeight = _cursor.getLong(_cursorIndexOfBlockHeight);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpRequiredSignatures;
            _tmpRequiredSignatures = _cursor.getInt(_cursorIndexOfRequiredSignatures);
            final int _tmpCurrentSignatureCount;
            _tmpCurrentSignatureCount = _cursor.getInt(_cursorIndexOfCurrentSignatureCount);
            final long _tmpSyncVersion;
            _tmpSyncVersion = _cursor.getLong(_cursorIndexOfSyncVersion);
            final String _tmpLastModifiedBy;
            if (_cursor.isNull(_cursorIndexOfLastModifiedBy)) {
              _tmpLastModifiedBy = null;
            } else {
              _tmpLastModifiedBy = _cursor.getString(_cursorIndexOfLastModifiedBy);
            }
            final long _tmpLastModifiedAt;
            _tmpLastModifiedAt = _cursor.getLong(_cursorIndexOfLastModifiedAt);
            _item = new TransactionEntity(_tmpId,_tmpTxHash,_tmpStatus,_tmpConfirmations,_tmpConfirmedAt,_tmpCreatedAt,_tmpRoscaId,_tmpType,_tmpAmount,_tmpFromAddress,_tmpToAddress,_tmpBlockHeight,_tmpTimestamp,_tmpRequiredSignatures,_tmpCurrentSignatureCount,_tmpSyncVersion,_tmpLastModifiedBy,_tmpLastModifiedAt);
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
  public Object getPendingTransactions(
      final Continuation<? super List<TransactionEntity>> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE status = 'pending'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfConfirmedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfFromAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "fromAddress");
          final int _cursorIndexOfToAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "toAddress");
          final int _cursorIndexOfBlockHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "blockHeight");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRequiredSignatures = CursorUtil.getColumnIndexOrThrow(_cursor, "requiredSignatures");
          final int _cursorIndexOfCurrentSignatureCount = CursorUtil.getColumnIndexOrThrow(_cursor, "currentSignatureCount");
          final int _cursorIndexOfSyncVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "syncVersion");
          final int _cursorIndexOfLastModifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedBy");
          final int _cursorIndexOfLastModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedAt");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpConfirmedAt;
            if (_cursor.isNull(_cursorIndexOfConfirmedAt)) {
              _tmpConfirmedAt = null;
            } else {
              _tmpConfirmedAt = _cursor.getLong(_cursorIndexOfConfirmedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final String _tmpFromAddress;
            if (_cursor.isNull(_cursorIndexOfFromAddress)) {
              _tmpFromAddress = null;
            } else {
              _tmpFromAddress = _cursor.getString(_cursorIndexOfFromAddress);
            }
            final String _tmpToAddress;
            if (_cursor.isNull(_cursorIndexOfToAddress)) {
              _tmpToAddress = null;
            } else {
              _tmpToAddress = _cursor.getString(_cursorIndexOfToAddress);
            }
            final Long _tmpBlockHeight;
            if (_cursor.isNull(_cursorIndexOfBlockHeight)) {
              _tmpBlockHeight = null;
            } else {
              _tmpBlockHeight = _cursor.getLong(_cursorIndexOfBlockHeight);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpRequiredSignatures;
            _tmpRequiredSignatures = _cursor.getInt(_cursorIndexOfRequiredSignatures);
            final int _tmpCurrentSignatureCount;
            _tmpCurrentSignatureCount = _cursor.getInt(_cursorIndexOfCurrentSignatureCount);
            final long _tmpSyncVersion;
            _tmpSyncVersion = _cursor.getLong(_cursorIndexOfSyncVersion);
            final String _tmpLastModifiedBy;
            if (_cursor.isNull(_cursorIndexOfLastModifiedBy)) {
              _tmpLastModifiedBy = null;
            } else {
              _tmpLastModifiedBy = _cursor.getString(_cursorIndexOfLastModifiedBy);
            }
            final long _tmpLastModifiedAt;
            _tmpLastModifiedAt = _cursor.getLong(_cursorIndexOfLastModifiedAt);
            _item = new TransactionEntity(_tmpId,_tmpTxHash,_tmpStatus,_tmpConfirmations,_tmpConfirmedAt,_tmpCreatedAt,_tmpRoscaId,_tmpType,_tmpAmount,_tmpFromAddress,_tmpToAddress,_tmpBlockHeight,_tmpTimestamp,_tmpRequiredSignatures,_tmpCurrentSignatureCount,_tmpSyncVersion,_tmpLastModifiedBy,_tmpLastModifiedAt);
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
  public Object getPendingConfirmation(
      final Continuation<? super List<TransactionEntity>> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE status = 'PENDING_CONFIRMATION' OR status = 'BROADCAST'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfConfirmedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfFromAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "fromAddress");
          final int _cursorIndexOfToAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "toAddress");
          final int _cursorIndexOfBlockHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "blockHeight");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRequiredSignatures = CursorUtil.getColumnIndexOrThrow(_cursor, "requiredSignatures");
          final int _cursorIndexOfCurrentSignatureCount = CursorUtil.getColumnIndexOrThrow(_cursor, "currentSignatureCount");
          final int _cursorIndexOfSyncVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "syncVersion");
          final int _cursorIndexOfLastModifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedBy");
          final int _cursorIndexOfLastModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedAt");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpConfirmedAt;
            if (_cursor.isNull(_cursorIndexOfConfirmedAt)) {
              _tmpConfirmedAt = null;
            } else {
              _tmpConfirmedAt = _cursor.getLong(_cursorIndexOfConfirmedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final String _tmpFromAddress;
            if (_cursor.isNull(_cursorIndexOfFromAddress)) {
              _tmpFromAddress = null;
            } else {
              _tmpFromAddress = _cursor.getString(_cursorIndexOfFromAddress);
            }
            final String _tmpToAddress;
            if (_cursor.isNull(_cursorIndexOfToAddress)) {
              _tmpToAddress = null;
            } else {
              _tmpToAddress = _cursor.getString(_cursorIndexOfToAddress);
            }
            final Long _tmpBlockHeight;
            if (_cursor.isNull(_cursorIndexOfBlockHeight)) {
              _tmpBlockHeight = null;
            } else {
              _tmpBlockHeight = _cursor.getLong(_cursorIndexOfBlockHeight);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpRequiredSignatures;
            _tmpRequiredSignatures = _cursor.getInt(_cursorIndexOfRequiredSignatures);
            final int _tmpCurrentSignatureCount;
            _tmpCurrentSignatureCount = _cursor.getInt(_cursorIndexOfCurrentSignatureCount);
            final long _tmpSyncVersion;
            _tmpSyncVersion = _cursor.getLong(_cursorIndexOfSyncVersion);
            final String _tmpLastModifiedBy;
            if (_cursor.isNull(_cursorIndexOfLastModifiedBy)) {
              _tmpLastModifiedBy = null;
            } else {
              _tmpLastModifiedBy = _cursor.getString(_cursorIndexOfLastModifiedBy);
            }
            final long _tmpLastModifiedAt;
            _tmpLastModifiedAt = _cursor.getLong(_cursorIndexOfLastModifiedAt);
            _item = new TransactionEntity(_tmpId,_tmpTxHash,_tmpStatus,_tmpConfirmations,_tmpConfirmedAt,_tmpCreatedAt,_tmpRoscaId,_tmpType,_tmpAmount,_tmpFromAddress,_tmpToAddress,_tmpBlockHeight,_tmpTimestamp,_tmpRequiredSignatures,_tmpCurrentSignatureCount,_tmpSyncVersion,_tmpLastModifiedBy,_tmpLastModifiedAt);
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
  public Object getPendingSignatures(
      final Continuation<? super List<TransactionEntity>> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE status = 'PENDING_SIGNATURES' ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfConfirmations = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmations");
          final int _cursorIndexOfConfirmedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "confirmedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfFromAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "fromAddress");
          final int _cursorIndexOfToAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "toAddress");
          final int _cursorIndexOfBlockHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "blockHeight");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRequiredSignatures = CursorUtil.getColumnIndexOrThrow(_cursor, "requiredSignatures");
          final int _cursorIndexOfCurrentSignatureCount = CursorUtil.getColumnIndexOrThrow(_cursor, "currentSignatureCount");
          final int _cursorIndexOfSyncVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "syncVersion");
          final int _cursorIndexOfLastModifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedBy");
          final int _cursorIndexOfLastModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedAt");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTxHash;
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _tmpTxHash = null;
            } else {
              _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpConfirmations;
            _tmpConfirmations = _cursor.getInt(_cursorIndexOfConfirmations);
            final Long _tmpConfirmedAt;
            if (_cursor.isNull(_cursorIndexOfConfirmedAt)) {
              _tmpConfirmedAt = null;
            } else {
              _tmpConfirmedAt = _cursor.getLong(_cursorIndexOfConfirmedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final String _tmpFromAddress;
            if (_cursor.isNull(_cursorIndexOfFromAddress)) {
              _tmpFromAddress = null;
            } else {
              _tmpFromAddress = _cursor.getString(_cursorIndexOfFromAddress);
            }
            final String _tmpToAddress;
            if (_cursor.isNull(_cursorIndexOfToAddress)) {
              _tmpToAddress = null;
            } else {
              _tmpToAddress = _cursor.getString(_cursorIndexOfToAddress);
            }
            final Long _tmpBlockHeight;
            if (_cursor.isNull(_cursorIndexOfBlockHeight)) {
              _tmpBlockHeight = null;
            } else {
              _tmpBlockHeight = _cursor.getLong(_cursorIndexOfBlockHeight);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpRequiredSignatures;
            _tmpRequiredSignatures = _cursor.getInt(_cursorIndexOfRequiredSignatures);
            final int _tmpCurrentSignatureCount;
            _tmpCurrentSignatureCount = _cursor.getInt(_cursorIndexOfCurrentSignatureCount);
            final long _tmpSyncVersion;
            _tmpSyncVersion = _cursor.getLong(_cursorIndexOfSyncVersion);
            final String _tmpLastModifiedBy;
            if (_cursor.isNull(_cursorIndexOfLastModifiedBy)) {
              _tmpLastModifiedBy = null;
            } else {
              _tmpLastModifiedBy = _cursor.getString(_cursorIndexOfLastModifiedBy);
            }
            final long _tmpLastModifiedAt;
            _tmpLastModifiedAt = _cursor.getLong(_cursorIndexOfLastModifiedAt);
            _item = new TransactionEntity(_tmpId,_tmpTxHash,_tmpStatus,_tmpConfirmations,_tmpConfirmedAt,_tmpCreatedAt,_tmpRoscaId,_tmpType,_tmpAmount,_tmpFromAddress,_tmpToAddress,_tmpBlockHeight,_tmpTimestamp,_tmpRequiredSignatures,_tmpCurrentSignatureCount,_tmpSyncVersion,_tmpLastModifiedBy,_tmpLastModifiedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
