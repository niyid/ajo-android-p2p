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
import com.techducat.ajo.data.local.DatabaseConverters;
import com.techducat.ajo.data.local.entity.MemberEntity;
import com.techducat.ajo.model.Member;
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
public final class MemberDao_Impl implements MemberDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MemberEntity> __insertionAdapterOfMemberEntity;

  private final DatabaseConverters __databaseConverters = new DatabaseConverters();

  private final EntityDeletionOrUpdateAdapter<MemberEntity> __updateAdapterOfMemberEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  public MemberDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMemberEntity = new EntityInsertionAdapter<MemberEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `members` (`id`,`roscaId`,`userId`,`name`,`moneroAddress`,`joinedAt`,`position`,`leftAt`,`leftReason`,`isActive`,`walletAddress`,`payoutOrderPosition`,`hasReceivedPayout`,`totalContributed`,`missedPayments`,`lastContributionAt`,`exitedAt`,`updatedAt`,`ipfsHash`,`lastSyncedAt`,`isDirty`,`status`,`multisigInfo`,`hasReceived`,`nodeId`,`publicWalletAddress`,`signingOrder`,`syncVersion`,`lastModifiedBy`,`lastModifiedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MemberEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindString(3, entity.getUserId());
        statement.bindString(4, entity.getName());
        if (entity.getMoneroAddress() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMoneroAddress());
        }
        statement.bindLong(6, entity.getJoinedAt());
        statement.bindLong(7, entity.getPosition());
        statement.bindLong(8, entity.getLeftAt());
        statement.bindString(9, entity.getLeftReason());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(10, _tmp);
        if (entity.getWalletAddress() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getWalletAddress());
        }
        if (entity.getPayoutOrderPosition() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getPayoutOrderPosition());
        }
        final int _tmp_1 = entity.getHasReceivedPayout() ? 1 : 0;
        statement.bindLong(13, _tmp_1);
        statement.bindLong(14, entity.getTotalContributed());
        statement.bindLong(15, entity.getMissedPayments());
        if (entity.getLastContributionAt() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getLastContributionAt());
        }
        if (entity.getExitedAt() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getExitedAt());
        }
        if (entity.getUpdatedAt() == null) {
          statement.bindNull(18);
        } else {
          statement.bindLong(18, entity.getUpdatedAt());
        }
        if (entity.getIpfsHash() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getIpfsHash());
        }
        if (entity.getLastSyncedAt() == null) {
          statement.bindNull(20);
        } else {
          statement.bindLong(20, entity.getLastSyncedAt());
        }
        final int _tmp_2 = entity.isDirty() ? 1 : 0;
        statement.bindLong(21, _tmp_2);
        if (entity.getStatus() == null) {
          statement.bindNull(22);
        } else {
          statement.bindString(22, entity.getStatus());
        }
        final String _tmp_3 = __databaseConverters.fromMultisigInfo(entity.getMultisigInfo());
        if (_tmp_3 == null) {
          statement.bindNull(23);
        } else {
          statement.bindString(23, _tmp_3);
        }
        final int _tmp_4 = entity.getHasReceived() ? 1 : 0;
        statement.bindLong(24, _tmp_4);
        if (entity.getNodeId() == null) {
          statement.bindNull(25);
        } else {
          statement.bindString(25, entity.getNodeId());
        }
        if (entity.getPublicWalletAddress() == null) {
          statement.bindNull(26);
        } else {
          statement.bindString(26, entity.getPublicWalletAddress());
        }
        statement.bindLong(27, entity.getSigningOrder());
        statement.bindLong(28, entity.getSyncVersion());
        if (entity.getLastModifiedBy() == null) {
          statement.bindNull(29);
        } else {
          statement.bindString(29, entity.getLastModifiedBy());
        }
        statement.bindLong(30, entity.getLastModifiedAt());
      }
    };
    this.__updateAdapterOfMemberEntity = new EntityDeletionOrUpdateAdapter<MemberEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `members` SET `id` = ?,`roscaId` = ?,`userId` = ?,`name` = ?,`moneroAddress` = ?,`joinedAt` = ?,`position` = ?,`leftAt` = ?,`leftReason` = ?,`isActive` = ?,`walletAddress` = ?,`payoutOrderPosition` = ?,`hasReceivedPayout` = ?,`totalContributed` = ?,`missedPayments` = ?,`lastContributionAt` = ?,`exitedAt` = ?,`updatedAt` = ?,`ipfsHash` = ?,`lastSyncedAt` = ?,`isDirty` = ?,`status` = ?,`multisigInfo` = ?,`hasReceived` = ?,`nodeId` = ?,`publicWalletAddress` = ?,`signingOrder` = ?,`syncVersion` = ?,`lastModifiedBy` = ?,`lastModifiedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MemberEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindString(3, entity.getUserId());
        statement.bindString(4, entity.getName());
        if (entity.getMoneroAddress() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMoneroAddress());
        }
        statement.bindLong(6, entity.getJoinedAt());
        statement.bindLong(7, entity.getPosition());
        statement.bindLong(8, entity.getLeftAt());
        statement.bindString(9, entity.getLeftReason());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(10, _tmp);
        if (entity.getWalletAddress() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getWalletAddress());
        }
        if (entity.getPayoutOrderPosition() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getPayoutOrderPosition());
        }
        final int _tmp_1 = entity.getHasReceivedPayout() ? 1 : 0;
        statement.bindLong(13, _tmp_1);
        statement.bindLong(14, entity.getTotalContributed());
        statement.bindLong(15, entity.getMissedPayments());
        if (entity.getLastContributionAt() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getLastContributionAt());
        }
        if (entity.getExitedAt() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getExitedAt());
        }
        if (entity.getUpdatedAt() == null) {
          statement.bindNull(18);
        } else {
          statement.bindLong(18, entity.getUpdatedAt());
        }
        if (entity.getIpfsHash() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getIpfsHash());
        }
        if (entity.getLastSyncedAt() == null) {
          statement.bindNull(20);
        } else {
          statement.bindLong(20, entity.getLastSyncedAt());
        }
        final int _tmp_2 = entity.isDirty() ? 1 : 0;
        statement.bindLong(21, _tmp_2);
        if (entity.getStatus() == null) {
          statement.bindNull(22);
        } else {
          statement.bindString(22, entity.getStatus());
        }
        final String _tmp_3 = __databaseConverters.fromMultisigInfo(entity.getMultisigInfo());
        if (_tmp_3 == null) {
          statement.bindNull(23);
        } else {
          statement.bindString(23, _tmp_3);
        }
        final int _tmp_4 = entity.getHasReceived() ? 1 : 0;
        statement.bindLong(24, _tmp_4);
        if (entity.getNodeId() == null) {
          statement.bindNull(25);
        } else {
          statement.bindString(25, entity.getNodeId());
        }
        if (entity.getPublicWalletAddress() == null) {
          statement.bindNull(26);
        } else {
          statement.bindString(26, entity.getPublicWalletAddress());
        }
        statement.bindLong(27, entity.getSigningOrder());
        statement.bindLong(28, entity.getSyncVersion());
        if (entity.getLastModifiedBy() == null) {
          statement.bindNull(29);
        } else {
          statement.bindString(29, entity.getLastModifiedBy());
        }
        statement.bindLong(30, entity.getLastModifiedAt());
        statement.bindString(31, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE members SET status = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final MemberEntity member, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMemberEntity.insert(member);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MemberEntity member, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMemberEntity.handle(member);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final String memberId, final String status, final long updatedAt,
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
        _stmt.bindString(_argIndex, memberId);
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
  public Object getMemberById(final String id,
      final Continuation<? super MemberEntity> $completion) {
    final String _sql = "SELECT * FROM members WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MemberEntity>() {
      @Override
      @Nullable
      public MemberEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMoneroAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "moneroAddress");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
          final int _cursorIndexOfLeftAt = CursorUtil.getColumnIndexOrThrow(_cursor, "leftAt");
          final int _cursorIndexOfLeftReason = CursorUtil.getColumnIndexOrThrow(_cursor, "leftReason");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfPayoutOrderPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrderPosition");
          final int _cursorIndexOfHasReceivedPayout = CursorUtil.getColumnIndexOrThrow(_cursor, "hasReceivedPayout");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "totalContributed");
          final int _cursorIndexOfMissedPayments = CursorUtil.getColumnIndexOrThrow(_cursor, "missedPayments");
          final int _cursorIndexOfLastContributionAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastContributionAt");
          final int _cursorIndexOfExitedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "exitedAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfHasReceived = CursorUtil.getColumnIndexOrThrow(_cursor, "hasReceived");
          final int _cursorIndexOfNodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "nodeId");
          final int _cursorIndexOfPublicWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "publicWalletAddress");
          final int _cursorIndexOfSigningOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "signingOrder");
          final int _cursorIndexOfSyncVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "syncVersion");
          final int _cursorIndexOfLastModifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedBy");
          final int _cursorIndexOfLastModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedAt");
          final MemberEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpMoneroAddress;
            if (_cursor.isNull(_cursorIndexOfMoneroAddress)) {
              _tmpMoneroAddress = null;
            } else {
              _tmpMoneroAddress = _cursor.getString(_cursorIndexOfMoneroAddress);
            }
            final long _tmpJoinedAt;
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            final int _tmpPosition;
            _tmpPosition = _cursor.getInt(_cursorIndexOfPosition);
            final long _tmpLeftAt;
            _tmpLeftAt = _cursor.getLong(_cursorIndexOfLeftAt);
            final String _tmpLeftReason;
            _tmpLeftReason = _cursor.getString(_cursorIndexOfLeftReason);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final Integer _tmpPayoutOrderPosition;
            if (_cursor.isNull(_cursorIndexOfPayoutOrderPosition)) {
              _tmpPayoutOrderPosition = null;
            } else {
              _tmpPayoutOrderPosition = _cursor.getInt(_cursorIndexOfPayoutOrderPosition);
            }
            final boolean _tmpHasReceivedPayout;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfHasReceivedPayout);
            _tmpHasReceivedPayout = _tmp_1 != 0;
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpMissedPayments;
            _tmpMissedPayments = _cursor.getInt(_cursorIndexOfMissedPayments);
            final Long _tmpLastContributionAt;
            if (_cursor.isNull(_cursorIndexOfLastContributionAt)) {
              _tmpLastContributionAt = null;
            } else {
              _tmpLastContributionAt = _cursor.getLong(_cursorIndexOfLastContributionAt);
            }
            final Long _tmpExitedAt;
            if (_cursor.isNull(_cursorIndexOfExitedAt)) {
              _tmpExitedAt = null;
            } else {
              _tmpExitedAt = _cursor.getLong(_cursorIndexOfExitedAt);
            }
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
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp_2 != 0;
            final String _tmpStatus;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmpStatus = null;
            } else {
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            }
            final Member.MultisigInfo _tmpMultisigInfo;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            _tmpMultisigInfo = __databaseConverters.toMultisigInfo(_tmp_3);
            final boolean _tmpHasReceived;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfHasReceived);
            _tmpHasReceived = _tmp_4 != 0;
            final String _tmpNodeId;
            if (_cursor.isNull(_cursorIndexOfNodeId)) {
              _tmpNodeId = null;
            } else {
              _tmpNodeId = _cursor.getString(_cursorIndexOfNodeId);
            }
            final String _tmpPublicWalletAddress;
            if (_cursor.isNull(_cursorIndexOfPublicWalletAddress)) {
              _tmpPublicWalletAddress = null;
            } else {
              _tmpPublicWalletAddress = _cursor.getString(_cursorIndexOfPublicWalletAddress);
            }
            final int _tmpSigningOrder;
            _tmpSigningOrder = _cursor.getInt(_cursorIndexOfSigningOrder);
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
            _result = new MemberEntity(_tmpId,_tmpRoscaId,_tmpUserId,_tmpName,_tmpMoneroAddress,_tmpJoinedAt,_tmpPosition,_tmpLeftAt,_tmpLeftReason,_tmpIsActive,_tmpWalletAddress,_tmpPayoutOrderPosition,_tmpHasReceivedPayout,_tmpTotalContributed,_tmpMissedPayments,_tmpLastContributionAt,_tmpExitedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty,_tmpStatus,_tmpMultisigInfo,_tmpHasReceived,_tmpNodeId,_tmpPublicWalletAddress,_tmpSigningOrder,_tmpSyncVersion,_tmpLastModifiedBy,_tmpLastModifiedAt);
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
  public Object getById(final String id, final Continuation<? super MemberEntity> $completion) {
    final String _sql = "SELECT * FROM members WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MemberEntity>() {
      @Override
      @Nullable
      public MemberEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMoneroAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "moneroAddress");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
          final int _cursorIndexOfLeftAt = CursorUtil.getColumnIndexOrThrow(_cursor, "leftAt");
          final int _cursorIndexOfLeftReason = CursorUtil.getColumnIndexOrThrow(_cursor, "leftReason");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfPayoutOrderPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrderPosition");
          final int _cursorIndexOfHasReceivedPayout = CursorUtil.getColumnIndexOrThrow(_cursor, "hasReceivedPayout");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "totalContributed");
          final int _cursorIndexOfMissedPayments = CursorUtil.getColumnIndexOrThrow(_cursor, "missedPayments");
          final int _cursorIndexOfLastContributionAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastContributionAt");
          final int _cursorIndexOfExitedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "exitedAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfHasReceived = CursorUtil.getColumnIndexOrThrow(_cursor, "hasReceived");
          final int _cursorIndexOfNodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "nodeId");
          final int _cursorIndexOfPublicWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "publicWalletAddress");
          final int _cursorIndexOfSigningOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "signingOrder");
          final int _cursorIndexOfSyncVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "syncVersion");
          final int _cursorIndexOfLastModifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedBy");
          final int _cursorIndexOfLastModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedAt");
          final MemberEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpMoneroAddress;
            if (_cursor.isNull(_cursorIndexOfMoneroAddress)) {
              _tmpMoneroAddress = null;
            } else {
              _tmpMoneroAddress = _cursor.getString(_cursorIndexOfMoneroAddress);
            }
            final long _tmpJoinedAt;
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            final int _tmpPosition;
            _tmpPosition = _cursor.getInt(_cursorIndexOfPosition);
            final long _tmpLeftAt;
            _tmpLeftAt = _cursor.getLong(_cursorIndexOfLeftAt);
            final String _tmpLeftReason;
            _tmpLeftReason = _cursor.getString(_cursorIndexOfLeftReason);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final Integer _tmpPayoutOrderPosition;
            if (_cursor.isNull(_cursorIndexOfPayoutOrderPosition)) {
              _tmpPayoutOrderPosition = null;
            } else {
              _tmpPayoutOrderPosition = _cursor.getInt(_cursorIndexOfPayoutOrderPosition);
            }
            final boolean _tmpHasReceivedPayout;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfHasReceivedPayout);
            _tmpHasReceivedPayout = _tmp_1 != 0;
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpMissedPayments;
            _tmpMissedPayments = _cursor.getInt(_cursorIndexOfMissedPayments);
            final Long _tmpLastContributionAt;
            if (_cursor.isNull(_cursorIndexOfLastContributionAt)) {
              _tmpLastContributionAt = null;
            } else {
              _tmpLastContributionAt = _cursor.getLong(_cursorIndexOfLastContributionAt);
            }
            final Long _tmpExitedAt;
            if (_cursor.isNull(_cursorIndexOfExitedAt)) {
              _tmpExitedAt = null;
            } else {
              _tmpExitedAt = _cursor.getLong(_cursorIndexOfExitedAt);
            }
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
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp_2 != 0;
            final String _tmpStatus;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmpStatus = null;
            } else {
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            }
            final Member.MultisigInfo _tmpMultisigInfo;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            _tmpMultisigInfo = __databaseConverters.toMultisigInfo(_tmp_3);
            final boolean _tmpHasReceived;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfHasReceived);
            _tmpHasReceived = _tmp_4 != 0;
            final String _tmpNodeId;
            if (_cursor.isNull(_cursorIndexOfNodeId)) {
              _tmpNodeId = null;
            } else {
              _tmpNodeId = _cursor.getString(_cursorIndexOfNodeId);
            }
            final String _tmpPublicWalletAddress;
            if (_cursor.isNull(_cursorIndexOfPublicWalletAddress)) {
              _tmpPublicWalletAddress = null;
            } else {
              _tmpPublicWalletAddress = _cursor.getString(_cursorIndexOfPublicWalletAddress);
            }
            final int _tmpSigningOrder;
            _tmpSigningOrder = _cursor.getInt(_cursorIndexOfSigningOrder);
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
            _result = new MemberEntity(_tmpId,_tmpRoscaId,_tmpUserId,_tmpName,_tmpMoneroAddress,_tmpJoinedAt,_tmpPosition,_tmpLeftAt,_tmpLeftReason,_tmpIsActive,_tmpWalletAddress,_tmpPayoutOrderPosition,_tmpHasReceivedPayout,_tmpTotalContributed,_tmpMissedPayments,_tmpLastContributionAt,_tmpExitedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty,_tmpStatus,_tmpMultisigInfo,_tmpHasReceived,_tmpNodeId,_tmpPublicWalletAddress,_tmpSigningOrder,_tmpSyncVersion,_tmpLastModifiedBy,_tmpLastModifiedAt);
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
  public List<MemberEntity> getMembersByGroup(final String roscaId) {
    final String _sql = "SELECT * FROM members WHERE roscaId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfMoneroAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "moneroAddress");
      final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
      final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
      final int _cursorIndexOfLeftAt = CursorUtil.getColumnIndexOrThrow(_cursor, "leftAt");
      final int _cursorIndexOfLeftReason = CursorUtil.getColumnIndexOrThrow(_cursor, "leftReason");
      final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
      final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
      final int _cursorIndexOfPayoutOrderPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrderPosition");
      final int _cursorIndexOfHasReceivedPayout = CursorUtil.getColumnIndexOrThrow(_cursor, "hasReceivedPayout");
      final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "totalContributed");
      final int _cursorIndexOfMissedPayments = CursorUtil.getColumnIndexOrThrow(_cursor, "missedPayments");
      final int _cursorIndexOfLastContributionAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastContributionAt");
      final int _cursorIndexOfExitedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "exitedAt");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
      final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
      final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
      final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
      final int _cursorIndexOfHasReceived = CursorUtil.getColumnIndexOrThrow(_cursor, "hasReceived");
      final int _cursorIndexOfNodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "nodeId");
      final int _cursorIndexOfPublicWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "publicWalletAddress");
      final int _cursorIndexOfSigningOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "signingOrder");
      final int _cursorIndexOfSyncVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "syncVersion");
      final int _cursorIndexOfLastModifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedBy");
      final int _cursorIndexOfLastModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedAt");
      final List<MemberEntity> _result = new ArrayList<MemberEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final MemberEntity _item;
        final String _tmpId;
        _tmpId = _cursor.getString(_cursorIndexOfId);
        final String _tmpRoscaId;
        _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
        final String _tmpUserId;
        _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
        final String _tmpName;
        _tmpName = _cursor.getString(_cursorIndexOfName);
        final String _tmpMoneroAddress;
        if (_cursor.isNull(_cursorIndexOfMoneroAddress)) {
          _tmpMoneroAddress = null;
        } else {
          _tmpMoneroAddress = _cursor.getString(_cursorIndexOfMoneroAddress);
        }
        final long _tmpJoinedAt;
        _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
        final int _tmpPosition;
        _tmpPosition = _cursor.getInt(_cursorIndexOfPosition);
        final long _tmpLeftAt;
        _tmpLeftAt = _cursor.getLong(_cursorIndexOfLeftAt);
        final String _tmpLeftReason;
        _tmpLeftReason = _cursor.getString(_cursorIndexOfLeftReason);
        final boolean _tmpIsActive;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsActive);
        _tmpIsActive = _tmp != 0;
        final String _tmpWalletAddress;
        if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
          _tmpWalletAddress = null;
        } else {
          _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
        }
        final Integer _tmpPayoutOrderPosition;
        if (_cursor.isNull(_cursorIndexOfPayoutOrderPosition)) {
          _tmpPayoutOrderPosition = null;
        } else {
          _tmpPayoutOrderPosition = _cursor.getInt(_cursorIndexOfPayoutOrderPosition);
        }
        final boolean _tmpHasReceivedPayout;
        final int _tmp_1;
        _tmp_1 = _cursor.getInt(_cursorIndexOfHasReceivedPayout);
        _tmpHasReceivedPayout = _tmp_1 != 0;
        final long _tmpTotalContributed;
        _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
        final int _tmpMissedPayments;
        _tmpMissedPayments = _cursor.getInt(_cursorIndexOfMissedPayments);
        final Long _tmpLastContributionAt;
        if (_cursor.isNull(_cursorIndexOfLastContributionAt)) {
          _tmpLastContributionAt = null;
        } else {
          _tmpLastContributionAt = _cursor.getLong(_cursorIndexOfLastContributionAt);
        }
        final Long _tmpExitedAt;
        if (_cursor.isNull(_cursorIndexOfExitedAt)) {
          _tmpExitedAt = null;
        } else {
          _tmpExitedAt = _cursor.getLong(_cursorIndexOfExitedAt);
        }
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
        final int _tmp_2;
        _tmp_2 = _cursor.getInt(_cursorIndexOfIsDirty);
        _tmpIsDirty = _tmp_2 != 0;
        final String _tmpStatus;
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _tmpStatus = null;
        } else {
          _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
        }
        final Member.MultisigInfo _tmpMultisigInfo;
        final String _tmp_3;
        if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getString(_cursorIndexOfMultisigInfo);
        }
        _tmpMultisigInfo = __databaseConverters.toMultisigInfo(_tmp_3);
        final boolean _tmpHasReceived;
        final int _tmp_4;
        _tmp_4 = _cursor.getInt(_cursorIndexOfHasReceived);
        _tmpHasReceived = _tmp_4 != 0;
        final String _tmpNodeId;
        if (_cursor.isNull(_cursorIndexOfNodeId)) {
          _tmpNodeId = null;
        } else {
          _tmpNodeId = _cursor.getString(_cursorIndexOfNodeId);
        }
        final String _tmpPublicWalletAddress;
        if (_cursor.isNull(_cursorIndexOfPublicWalletAddress)) {
          _tmpPublicWalletAddress = null;
        } else {
          _tmpPublicWalletAddress = _cursor.getString(_cursorIndexOfPublicWalletAddress);
        }
        final int _tmpSigningOrder;
        _tmpSigningOrder = _cursor.getInt(_cursorIndexOfSigningOrder);
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
        _item = new MemberEntity(_tmpId,_tmpRoscaId,_tmpUserId,_tmpName,_tmpMoneroAddress,_tmpJoinedAt,_tmpPosition,_tmpLeftAt,_tmpLeftReason,_tmpIsActive,_tmpWalletAddress,_tmpPayoutOrderPosition,_tmpHasReceivedPayout,_tmpTotalContributed,_tmpMissedPayments,_tmpLastContributionAt,_tmpExitedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty,_tmpStatus,_tmpMultisigInfo,_tmpHasReceived,_tmpNodeId,_tmpPublicWalletAddress,_tmpSigningOrder,_tmpSyncVersion,_tmpLastModifiedBy,_tmpLastModifiedAt);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Flow<List<MemberEntity>> getMembersByRosca(final String roscaId) {
    final String _sql = "SELECT * FROM members WHERE roscaId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"members"}, new Callable<List<MemberEntity>>() {
      @Override
      @NonNull
      public List<MemberEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMoneroAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "moneroAddress");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
          final int _cursorIndexOfLeftAt = CursorUtil.getColumnIndexOrThrow(_cursor, "leftAt");
          final int _cursorIndexOfLeftReason = CursorUtil.getColumnIndexOrThrow(_cursor, "leftReason");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfPayoutOrderPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrderPosition");
          final int _cursorIndexOfHasReceivedPayout = CursorUtil.getColumnIndexOrThrow(_cursor, "hasReceivedPayout");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "totalContributed");
          final int _cursorIndexOfMissedPayments = CursorUtil.getColumnIndexOrThrow(_cursor, "missedPayments");
          final int _cursorIndexOfLastContributionAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastContributionAt");
          final int _cursorIndexOfExitedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "exitedAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfHasReceived = CursorUtil.getColumnIndexOrThrow(_cursor, "hasReceived");
          final int _cursorIndexOfNodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "nodeId");
          final int _cursorIndexOfPublicWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "publicWalletAddress");
          final int _cursorIndexOfSigningOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "signingOrder");
          final int _cursorIndexOfSyncVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "syncVersion");
          final int _cursorIndexOfLastModifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedBy");
          final int _cursorIndexOfLastModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedAt");
          final List<MemberEntity> _result = new ArrayList<MemberEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemberEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpMoneroAddress;
            if (_cursor.isNull(_cursorIndexOfMoneroAddress)) {
              _tmpMoneroAddress = null;
            } else {
              _tmpMoneroAddress = _cursor.getString(_cursorIndexOfMoneroAddress);
            }
            final long _tmpJoinedAt;
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            final int _tmpPosition;
            _tmpPosition = _cursor.getInt(_cursorIndexOfPosition);
            final long _tmpLeftAt;
            _tmpLeftAt = _cursor.getLong(_cursorIndexOfLeftAt);
            final String _tmpLeftReason;
            _tmpLeftReason = _cursor.getString(_cursorIndexOfLeftReason);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final Integer _tmpPayoutOrderPosition;
            if (_cursor.isNull(_cursorIndexOfPayoutOrderPosition)) {
              _tmpPayoutOrderPosition = null;
            } else {
              _tmpPayoutOrderPosition = _cursor.getInt(_cursorIndexOfPayoutOrderPosition);
            }
            final boolean _tmpHasReceivedPayout;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfHasReceivedPayout);
            _tmpHasReceivedPayout = _tmp_1 != 0;
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpMissedPayments;
            _tmpMissedPayments = _cursor.getInt(_cursorIndexOfMissedPayments);
            final Long _tmpLastContributionAt;
            if (_cursor.isNull(_cursorIndexOfLastContributionAt)) {
              _tmpLastContributionAt = null;
            } else {
              _tmpLastContributionAt = _cursor.getLong(_cursorIndexOfLastContributionAt);
            }
            final Long _tmpExitedAt;
            if (_cursor.isNull(_cursorIndexOfExitedAt)) {
              _tmpExitedAt = null;
            } else {
              _tmpExitedAt = _cursor.getLong(_cursorIndexOfExitedAt);
            }
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
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp_2 != 0;
            final String _tmpStatus;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmpStatus = null;
            } else {
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            }
            final Member.MultisigInfo _tmpMultisigInfo;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            _tmpMultisigInfo = __databaseConverters.toMultisigInfo(_tmp_3);
            final boolean _tmpHasReceived;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfHasReceived);
            _tmpHasReceived = _tmp_4 != 0;
            final String _tmpNodeId;
            if (_cursor.isNull(_cursorIndexOfNodeId)) {
              _tmpNodeId = null;
            } else {
              _tmpNodeId = _cursor.getString(_cursorIndexOfNodeId);
            }
            final String _tmpPublicWalletAddress;
            if (_cursor.isNull(_cursorIndexOfPublicWalletAddress)) {
              _tmpPublicWalletAddress = null;
            } else {
              _tmpPublicWalletAddress = _cursor.getString(_cursorIndexOfPublicWalletAddress);
            }
            final int _tmpSigningOrder;
            _tmpSigningOrder = _cursor.getInt(_cursorIndexOfSigningOrder);
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
            _item = new MemberEntity(_tmpId,_tmpRoscaId,_tmpUserId,_tmpName,_tmpMoneroAddress,_tmpJoinedAt,_tmpPosition,_tmpLeftAt,_tmpLeftReason,_tmpIsActive,_tmpWalletAddress,_tmpPayoutOrderPosition,_tmpHasReceivedPayout,_tmpTotalContributed,_tmpMissedPayments,_tmpLastContributionAt,_tmpExitedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty,_tmpStatus,_tmpMultisigInfo,_tmpHasReceived,_tmpNodeId,_tmpPublicWalletAddress,_tmpSigningOrder,_tmpSyncVersion,_tmpLastModifiedBy,_tmpLastModifiedAt);
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
  public List<MemberEntity> getMembersByGroupSync(final String roscaId) {
    final String _sql = "SELECT * FROM members WHERE roscaId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfMoneroAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "moneroAddress");
      final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
      final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
      final int _cursorIndexOfLeftAt = CursorUtil.getColumnIndexOrThrow(_cursor, "leftAt");
      final int _cursorIndexOfLeftReason = CursorUtil.getColumnIndexOrThrow(_cursor, "leftReason");
      final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
      final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
      final int _cursorIndexOfPayoutOrderPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrderPosition");
      final int _cursorIndexOfHasReceivedPayout = CursorUtil.getColumnIndexOrThrow(_cursor, "hasReceivedPayout");
      final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "totalContributed");
      final int _cursorIndexOfMissedPayments = CursorUtil.getColumnIndexOrThrow(_cursor, "missedPayments");
      final int _cursorIndexOfLastContributionAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastContributionAt");
      final int _cursorIndexOfExitedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "exitedAt");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
      final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
      final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
      final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
      final int _cursorIndexOfHasReceived = CursorUtil.getColumnIndexOrThrow(_cursor, "hasReceived");
      final int _cursorIndexOfNodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "nodeId");
      final int _cursorIndexOfPublicWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "publicWalletAddress");
      final int _cursorIndexOfSigningOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "signingOrder");
      final int _cursorIndexOfSyncVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "syncVersion");
      final int _cursorIndexOfLastModifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedBy");
      final int _cursorIndexOfLastModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedAt");
      final List<MemberEntity> _result = new ArrayList<MemberEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final MemberEntity _item;
        final String _tmpId;
        _tmpId = _cursor.getString(_cursorIndexOfId);
        final String _tmpRoscaId;
        _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
        final String _tmpUserId;
        _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
        final String _tmpName;
        _tmpName = _cursor.getString(_cursorIndexOfName);
        final String _tmpMoneroAddress;
        if (_cursor.isNull(_cursorIndexOfMoneroAddress)) {
          _tmpMoneroAddress = null;
        } else {
          _tmpMoneroAddress = _cursor.getString(_cursorIndexOfMoneroAddress);
        }
        final long _tmpJoinedAt;
        _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
        final int _tmpPosition;
        _tmpPosition = _cursor.getInt(_cursorIndexOfPosition);
        final long _tmpLeftAt;
        _tmpLeftAt = _cursor.getLong(_cursorIndexOfLeftAt);
        final String _tmpLeftReason;
        _tmpLeftReason = _cursor.getString(_cursorIndexOfLeftReason);
        final boolean _tmpIsActive;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsActive);
        _tmpIsActive = _tmp != 0;
        final String _tmpWalletAddress;
        if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
          _tmpWalletAddress = null;
        } else {
          _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
        }
        final Integer _tmpPayoutOrderPosition;
        if (_cursor.isNull(_cursorIndexOfPayoutOrderPosition)) {
          _tmpPayoutOrderPosition = null;
        } else {
          _tmpPayoutOrderPosition = _cursor.getInt(_cursorIndexOfPayoutOrderPosition);
        }
        final boolean _tmpHasReceivedPayout;
        final int _tmp_1;
        _tmp_1 = _cursor.getInt(_cursorIndexOfHasReceivedPayout);
        _tmpHasReceivedPayout = _tmp_1 != 0;
        final long _tmpTotalContributed;
        _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
        final int _tmpMissedPayments;
        _tmpMissedPayments = _cursor.getInt(_cursorIndexOfMissedPayments);
        final Long _tmpLastContributionAt;
        if (_cursor.isNull(_cursorIndexOfLastContributionAt)) {
          _tmpLastContributionAt = null;
        } else {
          _tmpLastContributionAt = _cursor.getLong(_cursorIndexOfLastContributionAt);
        }
        final Long _tmpExitedAt;
        if (_cursor.isNull(_cursorIndexOfExitedAt)) {
          _tmpExitedAt = null;
        } else {
          _tmpExitedAt = _cursor.getLong(_cursorIndexOfExitedAt);
        }
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
        final int _tmp_2;
        _tmp_2 = _cursor.getInt(_cursorIndexOfIsDirty);
        _tmpIsDirty = _tmp_2 != 0;
        final String _tmpStatus;
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _tmpStatus = null;
        } else {
          _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
        }
        final Member.MultisigInfo _tmpMultisigInfo;
        final String _tmp_3;
        if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getString(_cursorIndexOfMultisigInfo);
        }
        _tmpMultisigInfo = __databaseConverters.toMultisigInfo(_tmp_3);
        final boolean _tmpHasReceived;
        final int _tmp_4;
        _tmp_4 = _cursor.getInt(_cursorIndexOfHasReceived);
        _tmpHasReceived = _tmp_4 != 0;
        final String _tmpNodeId;
        if (_cursor.isNull(_cursorIndexOfNodeId)) {
          _tmpNodeId = null;
        } else {
          _tmpNodeId = _cursor.getString(_cursorIndexOfNodeId);
        }
        final String _tmpPublicWalletAddress;
        if (_cursor.isNull(_cursorIndexOfPublicWalletAddress)) {
          _tmpPublicWalletAddress = null;
        } else {
          _tmpPublicWalletAddress = _cursor.getString(_cursorIndexOfPublicWalletAddress);
        }
        final int _tmpSigningOrder;
        _tmpSigningOrder = _cursor.getInt(_cursorIndexOfSigningOrder);
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
        _item = new MemberEntity(_tmpId,_tmpRoscaId,_tmpUserId,_tmpName,_tmpMoneroAddress,_tmpJoinedAt,_tmpPosition,_tmpLeftAt,_tmpLeftReason,_tmpIsActive,_tmpWalletAddress,_tmpPayoutOrderPosition,_tmpHasReceivedPayout,_tmpTotalContributed,_tmpMissedPayments,_tmpLastContributionAt,_tmpExitedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty,_tmpStatus,_tmpMultisigInfo,_tmpHasReceived,_tmpNodeId,_tmpPublicWalletAddress,_tmpSigningOrder,_tmpSyncVersion,_tmpLastModifiedBy,_tmpLastModifiedAt);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Object getAllMembers(final Continuation<? super List<MemberEntity>> $completion) {
    final String _sql = "SELECT * FROM members";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MemberEntity>>() {
      @Override
      @NonNull
      public List<MemberEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMoneroAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "moneroAddress");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
          final int _cursorIndexOfLeftAt = CursorUtil.getColumnIndexOrThrow(_cursor, "leftAt");
          final int _cursorIndexOfLeftReason = CursorUtil.getColumnIndexOrThrow(_cursor, "leftReason");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfPayoutOrderPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrderPosition");
          final int _cursorIndexOfHasReceivedPayout = CursorUtil.getColumnIndexOrThrow(_cursor, "hasReceivedPayout");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "totalContributed");
          final int _cursorIndexOfMissedPayments = CursorUtil.getColumnIndexOrThrow(_cursor, "missedPayments");
          final int _cursorIndexOfLastContributionAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastContributionAt");
          final int _cursorIndexOfExitedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "exitedAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfHasReceived = CursorUtil.getColumnIndexOrThrow(_cursor, "hasReceived");
          final int _cursorIndexOfNodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "nodeId");
          final int _cursorIndexOfPublicWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "publicWalletAddress");
          final int _cursorIndexOfSigningOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "signingOrder");
          final int _cursorIndexOfSyncVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "syncVersion");
          final int _cursorIndexOfLastModifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedBy");
          final int _cursorIndexOfLastModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedAt");
          final List<MemberEntity> _result = new ArrayList<MemberEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemberEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpMoneroAddress;
            if (_cursor.isNull(_cursorIndexOfMoneroAddress)) {
              _tmpMoneroAddress = null;
            } else {
              _tmpMoneroAddress = _cursor.getString(_cursorIndexOfMoneroAddress);
            }
            final long _tmpJoinedAt;
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            final int _tmpPosition;
            _tmpPosition = _cursor.getInt(_cursorIndexOfPosition);
            final long _tmpLeftAt;
            _tmpLeftAt = _cursor.getLong(_cursorIndexOfLeftAt);
            final String _tmpLeftReason;
            _tmpLeftReason = _cursor.getString(_cursorIndexOfLeftReason);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final Integer _tmpPayoutOrderPosition;
            if (_cursor.isNull(_cursorIndexOfPayoutOrderPosition)) {
              _tmpPayoutOrderPosition = null;
            } else {
              _tmpPayoutOrderPosition = _cursor.getInt(_cursorIndexOfPayoutOrderPosition);
            }
            final boolean _tmpHasReceivedPayout;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfHasReceivedPayout);
            _tmpHasReceivedPayout = _tmp_1 != 0;
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpMissedPayments;
            _tmpMissedPayments = _cursor.getInt(_cursorIndexOfMissedPayments);
            final Long _tmpLastContributionAt;
            if (_cursor.isNull(_cursorIndexOfLastContributionAt)) {
              _tmpLastContributionAt = null;
            } else {
              _tmpLastContributionAt = _cursor.getLong(_cursorIndexOfLastContributionAt);
            }
            final Long _tmpExitedAt;
            if (_cursor.isNull(_cursorIndexOfExitedAt)) {
              _tmpExitedAt = null;
            } else {
              _tmpExitedAt = _cursor.getLong(_cursorIndexOfExitedAt);
            }
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
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp_2 != 0;
            final String _tmpStatus;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmpStatus = null;
            } else {
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            }
            final Member.MultisigInfo _tmpMultisigInfo;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            _tmpMultisigInfo = __databaseConverters.toMultisigInfo(_tmp_3);
            final boolean _tmpHasReceived;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfHasReceived);
            _tmpHasReceived = _tmp_4 != 0;
            final String _tmpNodeId;
            if (_cursor.isNull(_cursorIndexOfNodeId)) {
              _tmpNodeId = null;
            } else {
              _tmpNodeId = _cursor.getString(_cursorIndexOfNodeId);
            }
            final String _tmpPublicWalletAddress;
            if (_cursor.isNull(_cursorIndexOfPublicWalletAddress)) {
              _tmpPublicWalletAddress = null;
            } else {
              _tmpPublicWalletAddress = _cursor.getString(_cursorIndexOfPublicWalletAddress);
            }
            final int _tmpSigningOrder;
            _tmpSigningOrder = _cursor.getInt(_cursorIndexOfSigningOrder);
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
            _item = new MemberEntity(_tmpId,_tmpRoscaId,_tmpUserId,_tmpName,_tmpMoneroAddress,_tmpJoinedAt,_tmpPosition,_tmpLeftAt,_tmpLeftReason,_tmpIsActive,_tmpWalletAddress,_tmpPayoutOrderPosition,_tmpHasReceivedPayout,_tmpTotalContributed,_tmpMissedPayments,_tmpLastContributionAt,_tmpExitedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty,_tmpStatus,_tmpMultisigInfo,_tmpHasReceived,_tmpNodeId,_tmpPublicWalletAddress,_tmpSigningOrder,_tmpSyncVersion,_tmpLastModifiedBy,_tmpLastModifiedAt);
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
  public Object getByNodeId(final String nodeId, final String roscaId,
      final Continuation<? super MemberEntity> $completion) {
    final String _sql = "SELECT * FROM members WHERE nodeId = ? AND roscaId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, nodeId);
    _argIndex = 2;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MemberEntity>() {
      @Override
      @Nullable
      public MemberEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMoneroAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "moneroAddress");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
          final int _cursorIndexOfLeftAt = CursorUtil.getColumnIndexOrThrow(_cursor, "leftAt");
          final int _cursorIndexOfLeftReason = CursorUtil.getColumnIndexOrThrow(_cursor, "leftReason");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "walletAddress");
          final int _cursorIndexOfPayoutOrderPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutOrderPosition");
          final int _cursorIndexOfHasReceivedPayout = CursorUtil.getColumnIndexOrThrow(_cursor, "hasReceivedPayout");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "totalContributed");
          final int _cursorIndexOfMissedPayments = CursorUtil.getColumnIndexOrThrow(_cursor, "missedPayments");
          final int _cursorIndexOfLastContributionAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastContributionAt");
          final int _cursorIndexOfExitedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "exitedAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfIpfsHash = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsHash");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfIsDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isDirty");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfMultisigInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "multisigInfo");
          final int _cursorIndexOfHasReceived = CursorUtil.getColumnIndexOrThrow(_cursor, "hasReceived");
          final int _cursorIndexOfNodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "nodeId");
          final int _cursorIndexOfPublicWalletAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "publicWalletAddress");
          final int _cursorIndexOfSigningOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "signingOrder");
          final int _cursorIndexOfSyncVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "syncVersion");
          final int _cursorIndexOfLastModifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedBy");
          final int _cursorIndexOfLastModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifiedAt");
          final MemberEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpMoneroAddress;
            if (_cursor.isNull(_cursorIndexOfMoneroAddress)) {
              _tmpMoneroAddress = null;
            } else {
              _tmpMoneroAddress = _cursor.getString(_cursorIndexOfMoneroAddress);
            }
            final long _tmpJoinedAt;
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            final int _tmpPosition;
            _tmpPosition = _cursor.getInt(_cursorIndexOfPosition);
            final long _tmpLeftAt;
            _tmpLeftAt = _cursor.getLong(_cursorIndexOfLeftAt);
            final String _tmpLeftReason;
            _tmpLeftReason = _cursor.getString(_cursorIndexOfLeftReason);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpWalletAddress;
            if (_cursor.isNull(_cursorIndexOfWalletAddress)) {
              _tmpWalletAddress = null;
            } else {
              _tmpWalletAddress = _cursor.getString(_cursorIndexOfWalletAddress);
            }
            final Integer _tmpPayoutOrderPosition;
            if (_cursor.isNull(_cursorIndexOfPayoutOrderPosition)) {
              _tmpPayoutOrderPosition = null;
            } else {
              _tmpPayoutOrderPosition = _cursor.getInt(_cursorIndexOfPayoutOrderPosition);
            }
            final boolean _tmpHasReceivedPayout;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfHasReceivedPayout);
            _tmpHasReceivedPayout = _tmp_1 != 0;
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpMissedPayments;
            _tmpMissedPayments = _cursor.getInt(_cursorIndexOfMissedPayments);
            final Long _tmpLastContributionAt;
            if (_cursor.isNull(_cursorIndexOfLastContributionAt)) {
              _tmpLastContributionAt = null;
            } else {
              _tmpLastContributionAt = _cursor.getLong(_cursorIndexOfLastContributionAt);
            }
            final Long _tmpExitedAt;
            if (_cursor.isNull(_cursorIndexOfExitedAt)) {
              _tmpExitedAt = null;
            } else {
              _tmpExitedAt = _cursor.getLong(_cursorIndexOfExitedAt);
            }
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
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsDirty);
            _tmpIsDirty = _tmp_2 != 0;
            final String _tmpStatus;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmpStatus = null;
            } else {
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            }
            final Member.MultisigInfo _tmpMultisigInfo;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfMultisigInfo)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfMultisigInfo);
            }
            _tmpMultisigInfo = __databaseConverters.toMultisigInfo(_tmp_3);
            final boolean _tmpHasReceived;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfHasReceived);
            _tmpHasReceived = _tmp_4 != 0;
            final String _tmpNodeId;
            if (_cursor.isNull(_cursorIndexOfNodeId)) {
              _tmpNodeId = null;
            } else {
              _tmpNodeId = _cursor.getString(_cursorIndexOfNodeId);
            }
            final String _tmpPublicWalletAddress;
            if (_cursor.isNull(_cursorIndexOfPublicWalletAddress)) {
              _tmpPublicWalletAddress = null;
            } else {
              _tmpPublicWalletAddress = _cursor.getString(_cursorIndexOfPublicWalletAddress);
            }
            final int _tmpSigningOrder;
            _tmpSigningOrder = _cursor.getInt(_cursorIndexOfSigningOrder);
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
            _result = new MemberEntity(_tmpId,_tmpRoscaId,_tmpUserId,_tmpName,_tmpMoneroAddress,_tmpJoinedAt,_tmpPosition,_tmpLeftAt,_tmpLeftReason,_tmpIsActive,_tmpWalletAddress,_tmpPayoutOrderPosition,_tmpHasReceivedPayout,_tmpTotalContributed,_tmpMissedPayments,_tmpLastContributionAt,_tmpExitedAt,_tmpUpdatedAt,_tmpIpfsHash,_tmpLastSyncedAt,_tmpIsDirty,_tmpStatus,_tmpMultisigInfo,_tmpHasReceived,_tmpNodeId,_tmpPublicWalletAddress,_tmpSigningOrder,_tmpSyncVersion,_tmpLastModifiedBy,_tmpLastModifiedAt);
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
