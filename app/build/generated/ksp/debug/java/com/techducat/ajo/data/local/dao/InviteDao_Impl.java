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
import com.techducat.ajo.data.local.entity.InviteEntity;
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
public final class InviteDao_Impl implements InviteDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<InviteEntity> __insertionAdapterOfInviteEntity;

  private final EntityDeletionOrUpdateAdapter<InviteEntity> __deletionAdapterOfInviteEntity;

  private final EntityDeletionOrUpdateAdapter<InviteEntity> __updateAdapterOfInviteEntity;

  private final SharedSQLiteStatement __preparedStmtOfExpireOldInvites;

  public InviteDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfInviteEntity = new EntityInsertionAdapter<InviteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `invites` (`id`,`roscaId`,`inviterUserId`,`inviteeEmail`,`referralCode`,`status`,`createdAt`,`acceptedAt`,`expiresAt`,`acceptedByUserId`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InviteEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindString(3, entity.getInviterUserId());
        statement.bindString(4, entity.getInviteeEmail());
        statement.bindString(5, entity.getReferralCode());
        statement.bindString(6, entity.getStatus());
        statement.bindLong(7, entity.getCreatedAt());
        if (entity.getAcceptedAt() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getAcceptedAt());
        }
        statement.bindLong(9, entity.getExpiresAt());
        if (entity.getAcceptedByUserId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getAcceptedByUserId());
        }
      }
    };
    this.__deletionAdapterOfInviteEntity = new EntityDeletionOrUpdateAdapter<InviteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `invites` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InviteEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfInviteEntity = new EntityDeletionOrUpdateAdapter<InviteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `invites` SET `id` = ?,`roscaId` = ?,`inviterUserId` = ?,`inviteeEmail` = ?,`referralCode` = ?,`status` = ?,`createdAt` = ?,`acceptedAt` = ?,`expiresAt` = ?,`acceptedByUserId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InviteEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindString(3, entity.getInviterUserId());
        statement.bindString(4, entity.getInviteeEmail());
        statement.bindString(5, entity.getReferralCode());
        statement.bindString(6, entity.getStatus());
        statement.bindLong(7, entity.getCreatedAt());
        if (entity.getAcceptedAt() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getAcceptedAt());
        }
        statement.bindLong(9, entity.getExpiresAt());
        if (entity.getAcceptedByUserId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getAcceptedByUserId());
        }
        statement.bindString(11, entity.getId());
      }
    };
    this.__preparedStmtOfExpireOldInvites = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE invites SET status = ? WHERE expiresAt < ? AND status = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertInvite(final InviteEntity invite,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfInviteEntity.insert(invite);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteInvite(final InviteEntity invite,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfInviteEntity.handle(invite);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateInvite(final InviteEntity invite,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfInviteEntity.handle(invite);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object expireOldInvites(final String newStatus, final String oldStatus,
      final long currentTime, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfExpireOldInvites.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, newStatus);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, currentTime);
        _argIndex = 3;
        _stmt.bindString(_argIndex, oldStatus);
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
          __preparedStmtOfExpireOldInvites.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getInviteByReferralCode(final String referralCode,
      final Continuation<? super InviteEntity> $completion) {
    final String _sql = "SELECT * FROM invites WHERE referralCode = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, referralCode);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<InviteEntity>() {
      @Override
      @Nullable
      public InviteEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfInviterUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "inviterUserId");
          final int _cursorIndexOfInviteeEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "inviteeEmail");
          final int _cursorIndexOfReferralCode = CursorUtil.getColumnIndexOrThrow(_cursor, "referralCode");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfAcceptedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "acceptedAt");
          final int _cursorIndexOfExpiresAt = CursorUtil.getColumnIndexOrThrow(_cursor, "expiresAt");
          final int _cursorIndexOfAcceptedByUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "acceptedByUserId");
          final InviteEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpInviterUserId;
            _tmpInviterUserId = _cursor.getString(_cursorIndexOfInviterUserId);
            final String _tmpInviteeEmail;
            _tmpInviteeEmail = _cursor.getString(_cursorIndexOfInviteeEmail);
            final String _tmpReferralCode;
            _tmpReferralCode = _cursor.getString(_cursorIndexOfReferralCode);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpAcceptedAt;
            if (_cursor.isNull(_cursorIndexOfAcceptedAt)) {
              _tmpAcceptedAt = null;
            } else {
              _tmpAcceptedAt = _cursor.getLong(_cursorIndexOfAcceptedAt);
            }
            final long _tmpExpiresAt;
            _tmpExpiresAt = _cursor.getLong(_cursorIndexOfExpiresAt);
            final String _tmpAcceptedByUserId;
            if (_cursor.isNull(_cursorIndexOfAcceptedByUserId)) {
              _tmpAcceptedByUserId = null;
            } else {
              _tmpAcceptedByUserId = _cursor.getString(_cursorIndexOfAcceptedByUserId);
            }
            _result = new InviteEntity(_tmpId,_tmpRoscaId,_tmpInviterUserId,_tmpInviteeEmail,_tmpReferralCode,_tmpStatus,_tmpCreatedAt,_tmpAcceptedAt,_tmpExpiresAt,_tmpAcceptedByUserId);
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
  public Flow<List<InviteEntity>> getInvitesByRosca(final String roscaId) {
    final String _sql = "SELECT * FROM invites WHERE roscaId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"invites"}, new Callable<List<InviteEntity>>() {
      @Override
      @NonNull
      public List<InviteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfInviterUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "inviterUserId");
          final int _cursorIndexOfInviteeEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "inviteeEmail");
          final int _cursorIndexOfReferralCode = CursorUtil.getColumnIndexOrThrow(_cursor, "referralCode");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfAcceptedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "acceptedAt");
          final int _cursorIndexOfExpiresAt = CursorUtil.getColumnIndexOrThrow(_cursor, "expiresAt");
          final int _cursorIndexOfAcceptedByUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "acceptedByUserId");
          final List<InviteEntity> _result = new ArrayList<InviteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InviteEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpInviterUserId;
            _tmpInviterUserId = _cursor.getString(_cursorIndexOfInviterUserId);
            final String _tmpInviteeEmail;
            _tmpInviteeEmail = _cursor.getString(_cursorIndexOfInviteeEmail);
            final String _tmpReferralCode;
            _tmpReferralCode = _cursor.getString(_cursorIndexOfReferralCode);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpAcceptedAt;
            if (_cursor.isNull(_cursorIndexOfAcceptedAt)) {
              _tmpAcceptedAt = null;
            } else {
              _tmpAcceptedAt = _cursor.getLong(_cursorIndexOfAcceptedAt);
            }
            final long _tmpExpiresAt;
            _tmpExpiresAt = _cursor.getLong(_cursorIndexOfExpiresAt);
            final String _tmpAcceptedByUserId;
            if (_cursor.isNull(_cursorIndexOfAcceptedByUserId)) {
              _tmpAcceptedByUserId = null;
            } else {
              _tmpAcceptedByUserId = _cursor.getString(_cursorIndexOfAcceptedByUserId);
            }
            _item = new InviteEntity(_tmpId,_tmpRoscaId,_tmpInviterUserId,_tmpInviteeEmail,_tmpReferralCode,_tmpStatus,_tmpCreatedAt,_tmpAcceptedAt,_tmpExpiresAt,_tmpAcceptedByUserId);
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
  public Flow<List<InviteEntity>> getInvitesByInviter(final String userId) {
    final String _sql = "SELECT * FROM invites WHERE inviterUserId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"invites"}, new Callable<List<InviteEntity>>() {
      @Override
      @NonNull
      public List<InviteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfInviterUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "inviterUserId");
          final int _cursorIndexOfInviteeEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "inviteeEmail");
          final int _cursorIndexOfReferralCode = CursorUtil.getColumnIndexOrThrow(_cursor, "referralCode");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfAcceptedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "acceptedAt");
          final int _cursorIndexOfExpiresAt = CursorUtil.getColumnIndexOrThrow(_cursor, "expiresAt");
          final int _cursorIndexOfAcceptedByUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "acceptedByUserId");
          final List<InviteEntity> _result = new ArrayList<InviteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InviteEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpInviterUserId;
            _tmpInviterUserId = _cursor.getString(_cursorIndexOfInviterUserId);
            final String _tmpInviteeEmail;
            _tmpInviteeEmail = _cursor.getString(_cursorIndexOfInviteeEmail);
            final String _tmpReferralCode;
            _tmpReferralCode = _cursor.getString(_cursorIndexOfReferralCode);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpAcceptedAt;
            if (_cursor.isNull(_cursorIndexOfAcceptedAt)) {
              _tmpAcceptedAt = null;
            } else {
              _tmpAcceptedAt = _cursor.getLong(_cursorIndexOfAcceptedAt);
            }
            final long _tmpExpiresAt;
            _tmpExpiresAt = _cursor.getLong(_cursorIndexOfExpiresAt);
            final String _tmpAcceptedByUserId;
            if (_cursor.isNull(_cursorIndexOfAcceptedByUserId)) {
              _tmpAcceptedByUserId = null;
            } else {
              _tmpAcceptedByUserId = _cursor.getString(_cursorIndexOfAcceptedByUserId);
            }
            _item = new InviteEntity(_tmpId,_tmpRoscaId,_tmpInviterUserId,_tmpInviteeEmail,_tmpReferralCode,_tmpStatus,_tmpCreatedAt,_tmpAcceptedAt,_tmpExpiresAt,_tmpAcceptedByUserId);
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
  public Object getPendingInvitesByEmail(final String email, final String status,
      final Continuation<? super List<InviteEntity>> $completion) {
    final String _sql = "SELECT * FROM invites WHERE inviteeEmail = ? AND status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, email);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InviteEntity>>() {
      @Override
      @NonNull
      public List<InviteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfInviterUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "inviterUserId");
          final int _cursorIndexOfInviteeEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "inviteeEmail");
          final int _cursorIndexOfReferralCode = CursorUtil.getColumnIndexOrThrow(_cursor, "referralCode");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfAcceptedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "acceptedAt");
          final int _cursorIndexOfExpiresAt = CursorUtil.getColumnIndexOrThrow(_cursor, "expiresAt");
          final int _cursorIndexOfAcceptedByUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "acceptedByUserId");
          final List<InviteEntity> _result = new ArrayList<InviteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InviteEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpInviterUserId;
            _tmpInviterUserId = _cursor.getString(_cursorIndexOfInviterUserId);
            final String _tmpInviteeEmail;
            _tmpInviteeEmail = _cursor.getString(_cursorIndexOfInviteeEmail);
            final String _tmpReferralCode;
            _tmpReferralCode = _cursor.getString(_cursorIndexOfReferralCode);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpAcceptedAt;
            if (_cursor.isNull(_cursorIndexOfAcceptedAt)) {
              _tmpAcceptedAt = null;
            } else {
              _tmpAcceptedAt = _cursor.getLong(_cursorIndexOfAcceptedAt);
            }
            final long _tmpExpiresAt;
            _tmpExpiresAt = _cursor.getLong(_cursorIndexOfExpiresAt);
            final String _tmpAcceptedByUserId;
            if (_cursor.isNull(_cursorIndexOfAcceptedByUserId)) {
              _tmpAcceptedByUserId = null;
            } else {
              _tmpAcceptedByUserId = _cursor.getString(_cursorIndexOfAcceptedByUserId);
            }
            _item = new InviteEntity(_tmpId,_tmpRoscaId,_tmpInviterUserId,_tmpInviteeEmail,_tmpReferralCode,_tmpStatus,_tmpCreatedAt,_tmpAcceptedAt,_tmpExpiresAt,_tmpAcceptedByUserId);
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
  public Object getActiveInvites(final String status, final long currentTime,
      final Continuation<? super List<InviteEntity>> $completion) {
    final String _sql = "SELECT * FROM invites WHERE status = ? AND expiresAt > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    _argIndex = 2;
    _statement.bindLong(_argIndex, currentTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InviteEntity>>() {
      @Override
      @NonNull
      public List<InviteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfInviterUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "inviterUserId");
          final int _cursorIndexOfInviteeEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "inviteeEmail");
          final int _cursorIndexOfReferralCode = CursorUtil.getColumnIndexOrThrow(_cursor, "referralCode");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfAcceptedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "acceptedAt");
          final int _cursorIndexOfExpiresAt = CursorUtil.getColumnIndexOrThrow(_cursor, "expiresAt");
          final int _cursorIndexOfAcceptedByUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "acceptedByUserId");
          final List<InviteEntity> _result = new ArrayList<InviteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InviteEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpInviterUserId;
            _tmpInviterUserId = _cursor.getString(_cursorIndexOfInviterUserId);
            final String _tmpInviteeEmail;
            _tmpInviteeEmail = _cursor.getString(_cursorIndexOfInviteeEmail);
            final String _tmpReferralCode;
            _tmpReferralCode = _cursor.getString(_cursorIndexOfReferralCode);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpAcceptedAt;
            if (_cursor.isNull(_cursorIndexOfAcceptedAt)) {
              _tmpAcceptedAt = null;
            } else {
              _tmpAcceptedAt = _cursor.getLong(_cursorIndexOfAcceptedAt);
            }
            final long _tmpExpiresAt;
            _tmpExpiresAt = _cursor.getLong(_cursorIndexOfExpiresAt);
            final String _tmpAcceptedByUserId;
            if (_cursor.isNull(_cursorIndexOfAcceptedByUserId)) {
              _tmpAcceptedByUserId = null;
            } else {
              _tmpAcceptedByUserId = _cursor.getString(_cursorIndexOfAcceptedByUserId);
            }
            _item = new InviteEntity(_tmpId,_tmpRoscaId,_tmpInviterUserId,_tmpInviteeEmail,_tmpReferralCode,_tmpStatus,_tmpCreatedAt,_tmpAcceptedAt,_tmpExpiresAt,_tmpAcceptedByUserId);
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
  public Object getInviteCountByRosca(final String roscaId, final String status,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM invites WHERE roscaId = ? AND status = ?";
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
