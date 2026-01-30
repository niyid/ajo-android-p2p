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
import com.techducat.ajo.data.local.entity.RoscaEntity;
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
public final class RoscaDao_Impl implements RoscaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RoscaEntity> __insertionAdapterOfRoscaEntity;

  private final EntityDeletionOrUpdateAdapter<RoscaEntity> __deletionAdapterOfRoscaEntity;

  private final EntityDeletionOrUpdateAdapter<RoscaEntity> __updateAdapterOfRoscaEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateCycle;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSyncStatus;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public RoscaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRoscaEntity = new EntityInsertionAdapter<RoscaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `roscas` (`id`,`name`,`description`,`creatorId`,`groupType`,`contributionAmount`,`contributionFrequency`,`frequencyDays`,`totalMembers`,`currentMembers`,`payoutOrder`,`distributionMethod`,`cycleNumber`,`currentRound`,`totalCycles`,`status`,`walletAddress`,`roscaWalletPath`,`multisigAddress`,`multisigInfo`,`ipfsHash`,`ipfsCid`,`ipnsKey`,`version`,`isDirty`,`lastSyncedAt`,`lastSyncTimestamp`,`startDate`,`startedAt`,`completedAt`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RoscaEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        if (entity.getCreatorId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCreatorId());
        }
        statement.bindString(5, entity.getGroupType());
        statement.bindLong(6, entity.getContributionAmount());
        statement.bindString(7, entity.getContributionFrequency());
        statement.bindLong(8, entity.getFrequencyDays());
        statement.bindLong(9, entity.getTotalMembers());
        statement.bindLong(10, entity.getCurrentMembers());
        statement.bindString(11, entity.getPayoutOrder());
        statement.bindString(12, entity.getDistributionMethod());
        statement.bindLong(13, entity.getCycleNumber());
        statement.bindLong(14, entity.getCurrentRound());
        statement.bindLong(15, entity.getTotalCycles());
        statement.bindString(16, entity.getStatus());
        if (entity.getWalletAddress() == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.getWalletAddress());
        }
        if (entity.getRoscaWalletPath() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getRoscaWalletPath());
        }
        if (entity.getMultisigAddress() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getMultisigAddress());
        }
        if (entity.getMultisigInfo() == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, entity.getMultisigInfo());
        }
        if (entity.getIpfsHash() == null) {
          statement.bindNull(21);
        } else {
          statement.bindString(21, entity.getIpfsHash());
        }
        if (entity.getIpfsCid() == null) {
          statement.bindNull(22);
        } else {
          statement.bindString(22, entity.getIpfsCid());
        }
        if (entity.getIpnsKey() == null) {
          statement.bindNull(23);
        } else {
          statement.bindString(23, entity.getIpnsKey());
        }
        statement.bindLong(24, entity.getVersion());
        final int _tmp = entity.isDirty() ? 1 : 0;
        statement.bindLong(25, _tmp);
        if (entity.getLastSyncedAt() == null) {
          statement.bindNull(26);
        } else {
          statement.bindLong(26, entity.getLastSyncedAt());
        }
        if (entity.getLastSyncTimestamp() == null) {
          statement.bindNull(27);
        } else {
          statement.bindLong(27, entity.getLastSyncTimestamp());
        }
        if (entity.getStartDate() == null) {
          statement.bindNull(28);
        } else {
          statement.bindLong(28, entity.getStartDate());
        }
        if (entity.getStartedAt() == null) {
          statement.bindNull(29);
        } else {
          statement.bindLong(29, entity.getStartedAt());
        }
        if (entity.getCompletedAt() == null) {
          statement.bindNull(30);
        } else {
          statement.bindLong(30, entity.getCompletedAt());
        }
        statement.bindLong(31, entity.getCreatedAt());
        if (entity.getUpdatedAt() == null) {
          statement.bindNull(32);
        } else {
          statement.bindLong(32, entity.getUpdatedAt());
        }
      }
    };
    this.__deletionAdapterOfRoscaEntity = new EntityDeletionOrUpdateAdapter<RoscaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `roscas` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RoscaEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfRoscaEntity = new EntityDeletionOrUpdateAdapter<RoscaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `roscas` SET `id` = ?,`name` = ?,`description` = ?,`creatorId` = ?,`groupType` = ?,`contributionAmount` = ?,`contributionFrequency` = ?,`frequencyDays` = ?,`totalMembers` = ?,`currentMembers` = ?,`payoutOrder` = ?,`distributionMethod` = ?,`cycleNumber` = ?,`currentRound` = ?,`totalCycles` = ?,`status` = ?,`walletAddress` = ?,`roscaWalletPath` = ?,`multisigAddress` = ?,`multisigInfo` = ?,`ipfsHash` = ?,`ipfsCid` = ?,`ipnsKey` = ?,`version` = ?,`isDirty` = ?,`lastSyncedAt` = ?,`lastSyncTimestamp` = ?,`startDate` = ?,`startedAt` = ?,`completedAt` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RoscaEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        if (entity.getCreatorId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCreatorId());
        }
        statement.bindString(5, entity.getGroupType());
        statement.bindLong(6, entity.getContributionAmount());
        statement.bindString(7, entity.getContributionFrequency());
        statement.bindLong(8, entity.getFrequencyDays());
        statement.bindLong(9, entity.getTotalMembers());
        statement.bindLong(10, entity.getCurrentMembers());
        statement.bindString(11, entity.getPayoutOrder());
        statement.bindString(12, entity.getDistributionMethod());
        statement.bindLong(13, entity.getCycleNumber());
        statement.bindLong(14, entity.getCurrentRound());
        statement.bindLong(15, entity.getTotalCycles());
        statement.bindString(16, entity.getStatus());
        if (entity.getWalletAddress() == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.getWalletAddress());
        }
        if (entity.getRoscaWalletPath() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getRoscaWalletPath());
        }
        if (entity.getMultisigAddress() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getMultisigAddress());
        }
        if (entity.getMultisigInfo() == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, entity.getMultisigInfo());
        }
        if (entity.getIpfsHash() == null) {
          statement.bindNull(21);
        } else {
          statement.bindString(21, entity.getIpfsHash());
        }
        if (entity.getIpfsCid() == null) {
          statement.bindNull(22);
        } else {
          statement.bindString(22, entity.getIpfsCid());
        }
        if (entity.getIpnsKey() == null) {
          statement.bindNull(23);
        } else {
          statement.bindString(23, entity.getIpnsKey());
        }
        statement.bindLong(24, entity.getVersion());
        final int _tmp = entity.isDirty() ? 1 : 0;
        statement.bindLong(25, _tmp);
        if (entity.getLastSyncedAt() == null) {
          statement.bindNull(26);
        } else {
          statement.bindLong(26, entity.getLastSyncedAt());
        }
        if (entity.getLastSyncTimestamp() == null) {
          statement.bindNull(27);
        } else {
          statement.bindLong(27, entity.getLastSyncTimestamp());
        }
        if (entity.getStartDate() == null) {
          statement.bindNull(28);
        } else {
          statement.bindLong(28, entity.getStartDate());
        }
        if (entity.getStartedAt() == null) {
          statement.bindNull(29);
        } else {
          statement.bindLong(29, entity.getStartedAt());
        }
        if (entity.getCompletedAt() == null) {
          statement.bindNull(30);
        } else {
          statement.bindLong(30, entity.getCompletedAt());
        }
        statement.bindLong(31, entity.getCreatedAt());
        if (entity.getUpdatedAt() == null) {
          statement.bindNull(32);
        } else {
          statement.bindLong(32, entity.getUpdatedAt());
        }
        statement.bindString(33, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE roscas SET status = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateCycle = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE roscas SET cycleNumber = ?, currentRound = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateSyncStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE roscas SET ipfsHash = ?, ipfsCid = ?, lastSyncedAt = ?, isDirty = 0 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM roscas WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final RoscaEntity rosca, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRoscaEntity.insert(rosca);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<RoscaEntity> roscas,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRoscaEntity.insert(roscas);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final RoscaEntity rosca, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRoscaEntity.handle(rosca);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final RoscaEntity rosca, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRoscaEntity.handle(rosca);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final String roscaId, final String status, final long updatedAt,
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
          __preparedStmtOfUpdateStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCycle(final String roscaId, final int cycle, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateCycle.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cycle);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, cycle);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 4;
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
          __preparedStmtOfUpdateCycle.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSyncStatus(final String roscaId, final String ipfsHash, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSyncStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, ipfsHash);
        _argIndex = 2;
        _stmt.bindString(_argIndex, ipfsHash);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 4;
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
          __preparedStmtOfUpdateSyncStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final String roscaId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
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
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final String id, final Continuation<? super RoscaEntity> $completion) {
    final String _sql = "SELECT * FROM roscas WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RoscaEntity>() {
      @Override
      @Nullable
      public RoscaEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final RoscaEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _result = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getGroupById(final String id, final Continuation<? super RoscaEntity> $completion) {
    final String _sql = "SELECT * FROM roscas WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RoscaEntity>() {
      @Override
      @Nullable
      public RoscaEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final RoscaEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _result = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getRoscaById(final String roscaId,
      final Continuation<? super RoscaEntity> $completion) {
    final String _sql = "SELECT * FROM roscas WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RoscaEntity>() {
      @Override
      @Nullable
      public RoscaEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final RoscaEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _result = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getAllGroups(final Continuation<? super List<RoscaEntity>> $completion) {
    final String _sql = "SELECT * FROM roscas ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getAllRoscas(final Continuation<? super List<RoscaEntity>> $completion) {
    final String _sql = "SELECT * FROM roscas ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<RoscaEntity>> observeAllRoscas() {
    final String _sql = "SELECT * FROM roscas ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"roscas"}, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getRoscasByStatus(final String status,
      final Continuation<? super List<RoscaEntity>> $completion) {
    final String _sql = "SELECT * FROM roscas WHERE status = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<RoscaEntity>> observeRoscasByStatus(final String status) {
    final String _sql = "SELECT * FROM roscas WHERE status = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"roscas"}, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getActiveRoscas(final Continuation<? super List<RoscaEntity>> $completion) {
    final String _sql = "SELECT * FROM roscas WHERE status = 'ACTIVE' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<RoscaEntity>> observeActiveRoscas() {
    final String _sql = "SELECT * FROM roscas WHERE status = 'ACTIVE' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"roscas"}, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getCompletedRoscas(final Continuation<? super List<RoscaEntity>> $completion) {
    final String _sql = "SELECT * FROM roscas WHERE status = 'COMPLETED' ORDER BY completedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getRoscasByCreator(final String creatorId,
      final Continuation<? super List<RoscaEntity>> $completion) {
    final String _sql = "SELECT * FROM roscas WHERE creatorId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, creatorId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<RoscaEntity>> observeRoscasByCreator(final String creatorId) {
    final String _sql = "SELECT * FROM roscas WHERE creatorId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, creatorId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"roscas"}, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getDirtyGroups(final Continuation<? super List<RoscaEntity>> $completion) {
    final String _sql = "SELECT * FROM roscas WHERE isDirty = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getDirtyRoscas(final Continuation<? super List<RoscaEntity>> $completion) {
    final String _sql = "SELECT * FROM roscas WHERE isDirty = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getUnsyncedRoscas(final long timestamp,
      final Continuation<? super List<RoscaEntity>> $completion) {
    final String _sql = "SELECT * FROM roscas WHERE lastSyncedAt IS NULL OR lastSyncedAt < ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timestamp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getRoscaCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM roscas";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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
  public Object getRoscaCountByStatus(final String status,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM roscas WHERE status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
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
  public Object getRoscaCountByCreator(final String creatorId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM roscas WHERE creatorId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, creatorId);
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
  public Object searchRoscas(final String query,
      final Continuation<? super List<RoscaEntity>> $completion) {
    final String _sql = "SELECT * FROM roscas WHERE name LIKE '%' || ? || '%' OR description LIKE '%' || ? || '%' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<RoscaEntity>> observeSearchRoscas(final String query) {
    final String _sql = "SELECT * FROM roscas WHERE name LIKE '%' || ? || '%' OR description LIKE '%' || ? || '%' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"roscas"}, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getRoscasByDateRange(final long startDate, final long endDate,
      final Continuation<? super List<RoscaEntity>> $completion) {
    final String _sql = "SELECT * FROM roscas WHERE createdAt BETWEEN ? AND ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getRoscasStartingInRange(final long startDate, final long endDate,
      final Continuation<? super List<RoscaEntity>> $completion) {
    final String _sql = "SELECT * FROM roscas WHERE startDate BETWEEN ? AND ? ORDER BY startDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getRoscasByUser(final String userId,
      final Continuation<? super List<RoscaEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT DISTINCT r.* FROM roscas r\n"
            + "        INNER JOIN members m ON r.id = m.roscaId\n"
            + "        WHERE m.userId = ? AND m.isActive = 1\n"
            + "        ORDER BY r.createdAt DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoscaEntity>>() {
      @Override
      @NonNull
      public List<RoscaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfGroupType = CursorUtil.getColumnIndexOrThrow(_cursor, "groupType");
          final int _cursorIndexOfContributionAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionAmount");
          final int _cursorIndexOfContributionFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "contributionFrequency");
          final int _cursorIndexOfFrequencyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyDays");
          final int _cursorIndexOfTotalMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMembers");
          final int _cursorIndexOfCurrentMembers = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMembers");
          final int _cursorIndexOfPayoutOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrder");
          final int _cursorIndexOfDistributionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionMethod");
          final int _cursorIndexOfCycleNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cycleNumber");
          final int _cursorIndexOfCurrentRound = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRound");
          final int _cursorIndexOfTotalCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCycles");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfRoscaWalletPath = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaWalletPath");
          final int _cursorIndexOfMultisigAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigAddress");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfIpnsKey = CursorUtil.getColumnIndexOrThrow(_cursor, "ipnsKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLastSyncTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncTimestamp");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<RoscaEntity> _result = new ArrayList<RoscaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoscaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCreatorId;
            if (_cursor.isNull(_cursorIndexOfCreatorId)) {
              _tmpCreatorId = null;
            } else {
              _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            }
            final String _tmpGroupType;
            _tmpGroupType = _cursor.getString(_cursorIndexOfGroupType);
            final long _tmpContributionAmount;
            _tmpContributionAmount = _cursor.getLong(_cursorIndexOfContributionAmount);
            final String _tmpContributionFrequency;
            _tmpContributionFrequency = _cursor.getString(_cursorIndexOfContributionFrequency);
            final int _tmpFrequencyDays;
            _tmpFrequencyDays = _cursor.getInt(_cursorIndexOfFrequencyDays);
            final int _tmpTotalMembers;
            _tmpTotalMembers = _cursor.getInt(_cursorIndexOfTotalMembers);
            final int _tmpCurrentMembers;
            _tmpCurrentMembers = _cursor.getInt(_cursorIndexOfCurrentMembers);
            final String _tmpPayoutOrder;
            _tmpPayoutOrder = _cursor.getString(_cursorIndexOfPayoutOrder);
            final String _tmpDistributionMethod;
            _tmpDistributionMethod = _cursor.getString(_cursorIndexOfDistributionMethod);
            final int _tmpCycleNumber;
            _tmpCycleNumber = _cursor.getInt(_cursorIndexOfCycleNumber);
            final int _tmpCurrentRound;
            _tmpCurrentRound = _cursor.getInt(_cursorIndexOfCurrentRound);
            final int _tmpTotalCycles;
            _tmpTotalCycles = _cursor.getInt(_cursorIndexOfTotalCycles);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final String _tmpRoscaWalletPath;
            if (_cursor.isNull(_cursorIndexOfRoscaWalletPath)) {
              _tmpRoscaWalletPath = null;
            } else {
              _tmpRoscaWalletPath = _cursor.getString(_cursorIndexOfRoscaWalletPath);
            }
            final String _tmpMultisigAddress;
            if (_cursor.isNull(_cursorIndexOfMultisigAddress)) {
              _tmpMultisigAddress = null;
            } else {
              _tmpMultisigAddress = _cursor.getString(_cursorIndexOfMultisigAddress);
            }
            final String _tmpMultisigInfo;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmpMultisigInfo = null;
            } else {
              _tmpMultisigInfo = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            final String _tmpIpfsHash;
            if (_cursor.isNull(_cursorIndexOfIpfsHash)) {
              _tmpIpfsHash = null;
            } else {
              _tmpIpfsHash = _cursor.getString(_cursorIndexOfIpfsHash);
            }
            final String _tmpIpfsCid;
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _tmpIpfsCid = null;
            } else {
              _tmpIpfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            final String _tmpIpnsKey;
            if (_cursor.isNull(_cursorIndexOfIpnsKey)) {
              _tmpIpnsKey = null;
            } else {
              _tmpIpnsKey = _cursor.getString(_cursorIndexOfIpnsKey);
            }
            final long _tmpVersion;
            _tmpVersion = _cursor.getLong(_cursorIndexOfVersion);
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
            final Long _tmpLastSyncTimestamp;
            if (_cursor.isNull(_cursorIndexOfLastSyncTimestamp)) {
              _tmpLastSyncTimestamp = null;
            } else {
              _tmpLastSyncTimestamp = _cursor.getLong(_cursorIndexOfLastSyncTimestamp);
            }
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _item = new RoscaEntity(_tmpId,_tmpName,_tmpDescription,_tmpCreatorId,_tmpGroupType,_tmpContributionAmount,_tmpContributionFrequency,_tmpFrequencyDays,_tmpTotalMembers,_tmpCurrentMembers,_tmpPayoutOrder,_tmpDistributionMethod,_tmpCycleNumber,_tmpCurrentRound,_tmpTotalCycles,_tmpStatus,_tmpWalletAddress,_tmpRoscaWalletPath,_tmpMultisigAddress,_tmpMultisigInfo,_tmpIpfsHash,_tmpIpfsCid,_tmpIpnsKey,_tmpVersion,_tmpIsDirty,_tmpLastSyncedAt,_tmpLastSyncTimestamp,_tmpStartDate,_tmpStartedAt,_tmpCompletedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
