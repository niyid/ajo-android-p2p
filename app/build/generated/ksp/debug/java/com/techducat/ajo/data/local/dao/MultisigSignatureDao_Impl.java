package com.techducat.ajo.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.techducat.ajo.data.local.entity.MultisigSignatureEntity;
import java.lang.Class;
import java.lang.Exception;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MultisigSignatureDao_Impl implements MultisigSignatureDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MultisigSignatureEntity> __insertionAdapterOfMultisigSignatureEntity;

  private final EntityDeletionOrUpdateAdapter<MultisigSignatureEntity> __updateAdapterOfMultisigSignatureEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByRoscaId;

  private final EntityUpsertionAdapter<MultisigSignatureEntity> __upsertionAdapterOfMultisigSignatureEntity;

  public MultisigSignatureDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMultisigSignatureEntity = new EntityInsertionAdapter<MultisigSignatureEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `multisig_signatures` (`id`,`roscaId`,`roundNumber`,`txHash`,`memberId`,`hasSigned`,`signature`,`timestamp`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MultisigSignatureEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindLong(3, entity.getRoundNumber());
        statement.bindString(4, entity.getTxHash());
        statement.bindString(5, entity.getMemberId());
        final int _tmp = entity.getHasSigned() ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.getSignature() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getSignature());
        }
        statement.bindLong(8, entity.getTimestamp());
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getUpdatedAt());
      }
    };
    this.__updateAdapterOfMultisigSignatureEntity = new EntityDeletionOrUpdateAdapter<MultisigSignatureEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `multisig_signatures` SET `id` = ?,`roscaId` = ?,`roundNumber` = ?,`txHash` = ?,`memberId` = ?,`hasSigned` = ?,`signature` = ?,`timestamp` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MultisigSignatureEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindLong(3, entity.getRoundNumber());
        statement.bindString(4, entity.getTxHash());
        statement.bindString(5, entity.getMemberId());
        final int _tmp = entity.getHasSigned() ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.getSignature() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getSignature());
        }
        statement.bindLong(8, entity.getTimestamp());
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getUpdatedAt());
        statement.bindString(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteByRoscaId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM multisig_signatures WHERE roscaId = ?";
        return _query;
      }
    };
    this.__upsertionAdapterOfMultisigSignatureEntity = new EntityUpsertionAdapter<MultisigSignatureEntity>(new EntityInsertionAdapter<MultisigSignatureEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `multisig_signatures` (`id`,`roscaId`,`roundNumber`,`txHash`,`memberId`,`hasSigned`,`signature`,`timestamp`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MultisigSignatureEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindLong(3, entity.getRoundNumber());
        statement.bindString(4, entity.getTxHash());
        statement.bindString(5, entity.getMemberId());
        final int _tmp = entity.getHasSigned() ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.getSignature() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getSignature());
        }
        statement.bindLong(8, entity.getTimestamp());
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getUpdatedAt());
      }
    }, new EntityDeletionOrUpdateAdapter<MultisigSignatureEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `multisig_signatures` SET `id` = ?,`roscaId` = ?,`roundNumber` = ?,`txHash` = ?,`memberId` = ?,`hasSigned` = ?,`signature` = ?,`timestamp` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MultisigSignatureEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindLong(3, entity.getRoundNumber());
        statement.bindString(4, entity.getTxHash());
        statement.bindString(5, entity.getMemberId());
        final int _tmp = entity.getHasSigned() ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.getSignature() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getSignature());
        }
        statement.bindLong(8, entity.getTimestamp());
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getUpdatedAt());
        statement.bindString(11, entity.getId());
      }
    });
  }

  @Override
  public Object insert(final MultisigSignatureEntity signature,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMultisigSignatureEntity.insert(signature);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MultisigSignatureEntity signature,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMultisigSignatureEntity.handle(signature);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
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
  public Object upsert(final MultisigSignatureEntity signature,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfMultisigSignatureEntity.upsert(signature);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getMultisigSignatures(final String roscaId, final int roundNumber,
      final Continuation<? super List<MultisigSignatureEntity>> $completion) {
    final String _sql = "SELECT * FROM multisig_signatures WHERE roscaId = ? AND roundNumber = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, roundNumber);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MultisigSignatureEntity>>() {
      @Override
      @NonNull
      public List<MultisigSignatureEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "roundNumber");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfHasSigned = CursorUtil.getColumnIndexOrThrow(_cursor, "hasSigned");
          final int _cursorIndexOfSignature = CursorUtil.getColumnIndexOrThrow(_cursor, "signature");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<MultisigSignatureEntity> _result = new ArrayList<MultisigSignatureEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MultisigSignatureEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpTxHash;
            _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final boolean _tmpHasSigned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfHasSigned);
            _tmpHasSigned = _tmp != 0;
            final String _tmpSignature;
            if (_cursor.isNull(_cursorIndexOfSignature)) {
              _tmpSignature = null;
            } else {
              _tmpSignature = _cursor.getString(_cursorIndexOfSignature);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MultisigSignatureEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpTxHash,_tmpMemberId,_tmpHasSigned,_tmpSignature,_tmpTimestamp,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getMultisigSignature(final String roscaId, final int roundNumber,
      final String memberId, final Continuation<? super MultisigSignatureEntity> $completion) {
    final String _sql = "SELECT * FROM multisig_signatures WHERE roscaId = ? AND roundNumber = ? AND memberId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, roundNumber);
    _argIndex = 3;
    _statement.bindString(_argIndex, memberId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MultisigSignatureEntity>() {
      @Override
      @Nullable
      public MultisigSignatureEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "roundNumber");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfHasSigned = CursorUtil.getColumnIndexOrThrow(_cursor, "hasSigned");
          final int _cursorIndexOfSignature = CursorUtil.getColumnIndexOrThrow(_cursor, "signature");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final MultisigSignatureEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final String _tmpTxHash;
            _tmpTxHash = _cursor.getString(_cursorIndexOfTxHash);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final boolean _tmpHasSigned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfHasSigned);
            _tmpHasSigned = _tmp != 0;
            final String _tmpSignature;
            if (_cursor.isNull(_cursorIndexOfSignature)) {
              _tmpSignature = null;
            } else {
              _tmpSignature = _cursor.getString(_cursorIndexOfSignature);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new MultisigSignatureEntity(_tmpId,_tmpRoscaId,_tmpRoundNumber,_tmpTxHash,_tmpMemberId,_tmpHasSigned,_tmpSignature,_tmpTimestamp,_tmpCreatedAt,_tmpUpdatedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
