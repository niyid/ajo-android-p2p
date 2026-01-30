package com.techducat.ajo.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SyncQueueDao_Impl implements SyncQueueDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SyncQueueEntity> __insertionAdapterOfSyncQueueEntity;

  private final EntityDeletionOrUpdateAdapter<SyncQueueEntity> __deletionAdapterOfSyncQueueEntity;

  public SyncQueueDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSyncQueueEntity = new EntityInsertionAdapter<SyncQueueEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `sync_queue` (`id`,`entityType`,`entityId`,`operation`,`payload`,`attempts`,`maxAttempts`,`createdAt`,`lastAttemptAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncQueueEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getEntityType());
        statement.bindString(3, entity.getEntityId());
        statement.bindString(4, entity.getOperation());
        statement.bindString(5, entity.getPayload());
        statement.bindLong(6, entity.getAttempts());
        statement.bindLong(7, entity.getMaxAttempts());
        statement.bindLong(8, entity.getCreatedAt());
        if (entity.getLastAttemptAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getLastAttemptAt());
        }
      }
    };
    this.__deletionAdapterOfSyncQueueEntity = new EntityDeletionOrUpdateAdapter<SyncQueueEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `sync_queue` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncQueueEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final SyncQueueEntity syncItem,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSyncQueueEntity.insert(syncItem);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final SyncQueueEntity syncItem,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSyncQueueEntity.handle(syncItem);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPendingSyncs(final Continuation<? super List<SyncQueueEntity>> $completion) {
    final String _sql = "SELECT * FROM sync_queue WHERE attempts < maxAttempts ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SyncQueueEntity>>() {
      @Override
      @NonNull
      public List<SyncQueueEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEntityType = CursorUtil.getColumnIndexOrThrow(_cursor, "entityType");
          final int _cursorIndexOfEntityId = CursorUtil.getColumnIndexOrThrow(_cursor, "entityId");
          final int _cursorIndexOfOperation = CursorUtil.getColumnIndexOrThrow(_cursor, "operation");
          final int _cursorIndexOfPayload = CursorUtil.getColumnIndexOrThrow(_cursor, "payload");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfMaxAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "maxAttempts");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastAttemptAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastAttemptAt");
          final List<SyncQueueEntity> _result = new ArrayList<SyncQueueEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncQueueEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpEntityType;
            _tmpEntityType = _cursor.getString(_cursorIndexOfEntityType);
            final String _tmpEntityId;
            _tmpEntityId = _cursor.getString(_cursorIndexOfEntityId);
            final String _tmpOperation;
            _tmpOperation = _cursor.getString(_cursorIndexOfOperation);
            final String _tmpPayload;
            _tmpPayload = _cursor.getString(_cursorIndexOfPayload);
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final int _tmpMaxAttempts;
            _tmpMaxAttempts = _cursor.getInt(_cursorIndexOfMaxAttempts);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastAttemptAt;
            if (_cursor.isNull(_cursorIndexOfLastAttemptAt)) {
              _tmpLastAttemptAt = null;
            } else {
              _tmpLastAttemptAt = _cursor.getLong(_cursorIndexOfLastAttemptAt);
            }
            _item = new SyncQueueEntity(_tmpId,_tmpEntityType,_tmpEntityId,_tmpOperation,_tmpPayload,_tmpAttempts,_tmpMaxAttempts,_tmpCreatedAt,_tmpLastAttemptAt);
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
