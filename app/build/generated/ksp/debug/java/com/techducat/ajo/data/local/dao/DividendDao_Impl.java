package com.techducat.ajo.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.techducat.ajo.data.local.entity.DividendEntity;
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
public final class DividendDao_Impl implements DividendDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DividendEntity> __insertionAdapterOfDividendEntity;

  public DividendDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDividendEntity = new EntityInsertionAdapter<DividendEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `dividends` (`id`,`roundId`,`memberId`,`amount`,`transactionHash`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DividendEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoundId());
        statement.bindString(3, entity.getMemberId());
        statement.bindLong(4, entity.getAmount());
        statement.bindString(5, entity.getTransactionHash());
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindLong(7, entity.getUpdatedAt());
      }
    };
  }

  @Override
  public Object insert(final DividendEntity dividend,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDividendEntity.insert(dividend);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getDividendsByRoundId(final String roundId,
      final Continuation<? super List<DividendEntity>> $completion) {
    final String _sql = "SELECT * FROM dividends WHERE roundId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roundId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DividendEntity>>() {
      @Override
      @NonNull
      public List<DividendEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "roundId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTransactionHash = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionHash");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DividendEntity> _result = new ArrayList<DividendEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DividendEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoundId;
            _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final String _tmpTransactionHash;
            _tmpTransactionHash = _cursor.getString(_cursorIndexOfTransactionHash);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DividendEntity(_tmpId,_tmpRoundId,_tmpMemberId,_tmpAmount,_tmpTransactionHash,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getDividendsByMember(final String memberId,
      final Continuation<? super List<DividendEntity>> $completion) {
    final String _sql = "SELECT * FROM dividends WHERE memberId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, memberId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DividendEntity>>() {
      @Override
      @NonNull
      public List<DividendEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoundId = CursorUtil.getColumnIndexOrThrow(_cursor, "roundId");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "memberId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTransactionHash = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionHash");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DividendEntity> _result = new ArrayList<DividendEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DividendEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoundId;
            _tmpRoundId = _cursor.getString(_cursorIndexOfRoundId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final String _tmpTransactionHash;
            _tmpTransactionHash = _cursor.getString(_cursorIndexOfTransactionHash);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DividendEntity(_tmpId,_tmpRoundId,_tmpMemberId,_tmpAmount,_tmpTransactionHash,_tmpCreatedAt,_tmpUpdatedAt);
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
