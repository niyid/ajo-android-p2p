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
import com.techducat.ajo.data.local.entity.UserProfileEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserProfileDao_Impl implements UserProfileDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserProfileEntity> __insertionAdapterOfUserProfileEntity;

  private final EntityDeletionOrUpdateAdapter<UserProfileEntity> __deletionAdapterOfUserProfileEntity;

  private final EntityDeletionOrUpdateAdapter<UserProfileEntity> __updateAdapterOfUserProfileEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLastLogin;

  private final SharedSQLiteStatement __preparedStmtOfDeactivateAllProfiles;

  private final SharedSQLiteStatement __preparedStmtOfSetActiveProfile;

  private final SharedSQLiteStatement __preparedStmtOfDeleteUserProfileById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllUserProfiles;

  public UserProfileDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserProfileEntity = new EntityInsertionAdapter<UserProfileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_profiles` (`id`,`email`,`displayName`,`photoUrl`,`idToken`,`isActive`,`createdAt`,`lastLoginAt`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserProfileEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getEmail());
        statement.bindString(3, entity.getDisplayName());
        if (entity.getPhotoUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPhotoUrl());
        }
        if (entity.getIdToken() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getIdToken());
        }
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getLastLoginAt());
      }
    };
    this.__deletionAdapterOfUserProfileEntity = new EntityDeletionOrUpdateAdapter<UserProfileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `user_profiles` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserProfileEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfUserProfileEntity = new EntityDeletionOrUpdateAdapter<UserProfileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `user_profiles` SET `id` = ?,`email` = ?,`displayName` = ?,`photoUrl` = ?,`idToken` = ?,`isActive` = ?,`createdAt` = ?,`lastLoginAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserProfileEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getEmail());
        statement.bindString(3, entity.getDisplayName());
        if (entity.getPhotoUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPhotoUrl());
        }
        if (entity.getIdToken() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getIdToken());
        }
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getLastLoginAt());
        statement.bindString(9, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateLastLogin = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profiles SET lastLoginAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeactivateAllProfiles = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profiles SET isActive = 0";
        return _query;
      }
    };
    this.__preparedStmtOfSetActiveProfile = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profiles SET isActive = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteUserProfileById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM user_profiles WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllUserProfiles = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM user_profiles";
        return _query;
      }
    };
  }

  @Override
  public Object insertUserProfile(final UserProfileEntity userProfile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserProfileEntity.insert(userProfile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteUserProfile(final UserProfileEntity userProfile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfUserProfileEntity.handle(userProfile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateUserProfile(final UserProfileEntity userProfile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUserProfileEntity.handle(userProfile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLastLogin(final String userId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLastLogin.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, userId);
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
          __preparedStmtOfUpdateLastLogin.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deactivateAllProfiles(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeactivateAllProfiles.acquire();
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
          __preparedStmtOfDeactivateAllProfiles.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setActiveProfile(final String userId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetActiveProfile.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, userId);
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
          __preparedStmtOfSetActiveProfile.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteUserProfileById(final String userId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteUserProfileById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, userId);
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
          __preparedStmtOfDeleteUserProfileById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllUserProfiles(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllUserProfiles.acquire();
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
          __preparedStmtOfDeleteAllUserProfiles.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getUserProfile(final String userId,
      final Continuation<? super UserProfileEntity> $completion) {
    final String _sql = "SELECT * FROM user_profiles WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserProfileEntity>() {
      @Override
      @Nullable
      public UserProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUrl");
          final int _cursorIndexOfIdToken = CursorUtil.getColumnIndexOrThrow(_cursor, "idToken");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastLoginAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLoginAt");
          final UserProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfPhotoUrl)) {
              _tmpPhotoUrl = null;
            } else {
              _tmpPhotoUrl = _cursor.getString(_cursorIndexOfPhotoUrl);
            }
            final String _tmpIdToken;
            if (_cursor.isNull(_cursorIndexOfIdToken)) {
              _tmpIdToken = null;
            } else {
              _tmpIdToken = _cursor.getString(_cursorIndexOfIdToken);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpLastLoginAt;
            _tmpLastLoginAt = _cursor.getLong(_cursorIndexOfLastLoginAt);
            _result = new UserProfileEntity(_tmpId,_tmpEmail,_tmpDisplayName,_tmpPhotoUrl,_tmpIdToken,_tmpIsActive,_tmpCreatedAt,_tmpLastLoginAt);
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
  public Flow<UserProfileEntity> getUserProfileFlow(final String userId) {
    final String _sql = "SELECT * FROM user_profiles WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_profiles"}, new Callable<UserProfileEntity>() {
      @Override
      @Nullable
      public UserProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUrl");
          final int _cursorIndexOfIdToken = CursorUtil.getColumnIndexOrThrow(_cursor, "idToken");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastLoginAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLoginAt");
          final UserProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfPhotoUrl)) {
              _tmpPhotoUrl = null;
            } else {
              _tmpPhotoUrl = _cursor.getString(_cursorIndexOfPhotoUrl);
            }
            final String _tmpIdToken;
            if (_cursor.isNull(_cursorIndexOfIdToken)) {
              _tmpIdToken = null;
            } else {
              _tmpIdToken = _cursor.getString(_cursorIndexOfIdToken);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpLastLoginAt;
            _tmpLastLoginAt = _cursor.getLong(_cursorIndexOfLastLoginAt);
            _result = new UserProfileEntity(_tmpId,_tmpEmail,_tmpDisplayName,_tmpPhotoUrl,_tmpIdToken,_tmpIsActive,_tmpCreatedAt,_tmpLastLoginAt);
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
  public Object getActiveUserProfile(final Continuation<? super UserProfileEntity> $completion) {
    final String _sql = "SELECT * FROM user_profiles WHERE isActive = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserProfileEntity>() {
      @Override
      @Nullable
      public UserProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUrl");
          final int _cursorIndexOfIdToken = CursorUtil.getColumnIndexOrThrow(_cursor, "idToken");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastLoginAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLoginAt");
          final UserProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfPhotoUrl)) {
              _tmpPhotoUrl = null;
            } else {
              _tmpPhotoUrl = _cursor.getString(_cursorIndexOfPhotoUrl);
            }
            final String _tmpIdToken;
            if (_cursor.isNull(_cursorIndexOfIdToken)) {
              _tmpIdToken = null;
            } else {
              _tmpIdToken = _cursor.getString(_cursorIndexOfIdToken);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpLastLoginAt;
            _tmpLastLoginAt = _cursor.getLong(_cursorIndexOfLastLoginAt);
            _result = new UserProfileEntity(_tmpId,_tmpEmail,_tmpDisplayName,_tmpPhotoUrl,_tmpIdToken,_tmpIsActive,_tmpCreatedAt,_tmpLastLoginAt);
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
  public Flow<UserProfileEntity> getActiveUserProfileFlow() {
    final String _sql = "SELECT * FROM user_profiles WHERE isActive = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_profiles"}, new Callable<UserProfileEntity>() {
      @Override
      @Nullable
      public UserProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUrl");
          final int _cursorIndexOfIdToken = CursorUtil.getColumnIndexOrThrow(_cursor, "idToken");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastLoginAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLoginAt");
          final UserProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfPhotoUrl)) {
              _tmpPhotoUrl = null;
            } else {
              _tmpPhotoUrl = _cursor.getString(_cursorIndexOfPhotoUrl);
            }
            final String _tmpIdToken;
            if (_cursor.isNull(_cursorIndexOfIdToken)) {
              _tmpIdToken = null;
            } else {
              _tmpIdToken = _cursor.getString(_cursorIndexOfIdToken);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpLastLoginAt;
            _tmpLastLoginAt = _cursor.getLong(_cursorIndexOfLastLoginAt);
            _result = new UserProfileEntity(_tmpId,_tmpEmail,_tmpDisplayName,_tmpPhotoUrl,_tmpIdToken,_tmpIsActive,_tmpCreatedAt,_tmpLastLoginAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
