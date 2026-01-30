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
import com.techducat.ajo.data.local.entity.PenaltyEntity;
import java.lang.Class;
import java.lang.Double;
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
public final class PenaltyDao_Impl implements PenaltyDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PenaltyEntity> __insertionAdapterOfPenaltyEntity;

  private final EntityDeletionOrUpdateAdapter<PenaltyEntity> __deletionAdapterOfPenaltyEntity;

  private final EntityDeletionOrUpdateAdapter<PenaltyEntity> __updateAdapterOfPenaltyEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsApplied;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsWaived;

  private final SharedSQLiteStatement __preparedStmtOfLinkToPayout;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByRoscaId;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByMemberId;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldCalculatedPenalties;

  public PenaltyDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPenaltyEntity = new EntityInsertionAdapter<PenaltyEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `penalties` (`id`,`rosca_id`,`member_id`,`payout_id`,`penalty_type`,`total_contributed`,`cycles_participated`,`cycles_remaining`,`penalty_percentage`,`penalty_amount`,`reimbursement_amount`,`calculation_method`,`reason`,`exit_reason`,`status`,`applied_at`,`waived_at`,`waived_by`,`waiver_reason`,`notes`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PenaltyEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindString(3, entity.getMemberId());
        if (entity.getPayoutId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPayoutId());
        }
        statement.bindString(5, entity.getPenaltyType());
        statement.bindLong(6, entity.getTotalContributed());
        statement.bindLong(7, entity.getCyclesParticipated());
        statement.bindLong(8, entity.getCyclesRemaining());
        statement.bindDouble(9, entity.getPenaltyPercentage());
        statement.bindLong(10, entity.getPenaltyAmount());
        statement.bindLong(11, entity.getReimbursementAmount());
        statement.bindString(12, entity.getCalculationMethod());
        statement.bindString(13, entity.getReason());
        if (entity.getExitReason() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getExitReason());
        }
        statement.bindString(15, entity.getStatus());
        if (entity.getAppliedAt() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getAppliedAt());
        }
        if (entity.getWaivedAt() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getWaivedAt());
        }
        if (entity.getWaivedBy() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getWaivedBy());
        }
        if (entity.getWaiverReason() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getWaiverReason());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, entity.getNotes());
        }
        statement.bindLong(21, entity.getCreatedAt());
        if (entity.getUpdatedAt() == null) {
          statement.bindNull(22);
        } else {
          statement.bindLong(22, entity.getUpdatedAt());
        }
      }
    };
    this.__deletionAdapterOfPenaltyEntity = new EntityDeletionOrUpdateAdapter<PenaltyEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `penalties` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PenaltyEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfPenaltyEntity = new EntityDeletionOrUpdateAdapter<PenaltyEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `penalties` SET `id` = ?,`rosca_id` = ?,`member_id` = ?,`payout_id` = ?,`penalty_type` = ?,`total_contributed` = ?,`cycles_participated` = ?,`cycles_remaining` = ?,`penalty_percentage` = ?,`penalty_amount` = ?,`reimbursement_amount` = ?,`calculation_method` = ?,`reason` = ?,`exit_reason` = ?,`status` = ?,`applied_at` = ?,`waived_at` = ?,`waived_by` = ?,`waiver_reason` = ?,`notes` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PenaltyEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoscaId());
        statement.bindString(3, entity.getMemberId());
        if (entity.getPayoutId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPayoutId());
        }
        statement.bindString(5, entity.getPenaltyType());
        statement.bindLong(6, entity.getTotalContributed());
        statement.bindLong(7, entity.getCyclesParticipated());
        statement.bindLong(8, entity.getCyclesRemaining());
        statement.bindDouble(9, entity.getPenaltyPercentage());
        statement.bindLong(10, entity.getPenaltyAmount());
        statement.bindLong(11, entity.getReimbursementAmount());
        statement.bindString(12, entity.getCalculationMethod());
        statement.bindString(13, entity.getReason());
        if (entity.getExitReason() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getExitReason());
        }
        statement.bindString(15, entity.getStatus());
        if (entity.getAppliedAt() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getAppliedAt());
        }
        if (entity.getWaivedAt() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getWaivedAt());
        }
        if (entity.getWaivedBy() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getWaivedBy());
        }
        if (entity.getWaiverReason() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getWaiverReason());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, entity.getNotes());
        }
        statement.bindLong(21, entity.getCreatedAt());
        if (entity.getUpdatedAt() == null) {
          statement.bindNull(22);
        } else {
          statement.bindLong(22, entity.getUpdatedAt());
        }
        statement.bindString(23, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE penalties SET status = ?, updated_at = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsApplied = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE penalties SET status = ?, applied_at = ?, updated_at = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsWaived = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE penalties SET status = ?, waived_at = ?, waived_by = ?, waiver_reason = ?, updated_at = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfLinkToPayout = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE penalties SET payout_id = ?, updated_at = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM penalties WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByRoscaId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM penalties WHERE rosca_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByMemberId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM penalties WHERE member_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldCalculatedPenalties = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM penalties WHERE status = ? AND created_at < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final PenaltyEntity penalty, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPenaltyEntity.insert(penalty);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<PenaltyEntity> penalties,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPenaltyEntity.insert(penalties);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final PenaltyEntity penalty, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfPenaltyEntity.handle(penalty);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final PenaltyEntity penalty, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPenaltyEntity.handle(penalty);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final String penaltyId, final String status, final long updatedAt,
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
        _stmt.bindString(_argIndex, penaltyId);
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
  public Object markAsApplied(final String penaltyId, final String status, final long appliedAt,
      final long updatedAt, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsApplied.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, appliedAt);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 4;
        _stmt.bindString(_argIndex, penaltyId);
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
          __preparedStmtOfMarkAsApplied.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsWaived(final String penaltyId, final String status, final long waivedAt,
      final String waivedBy, final String waiverReason, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsWaived.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, waivedAt);
        _argIndex = 3;
        _stmt.bindString(_argIndex, waivedBy);
        _argIndex = 4;
        if (waiverReason == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, waiverReason);
        }
        _argIndex = 5;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 6;
        _stmt.bindString(_argIndex, penaltyId);
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
          __preparedStmtOfMarkAsWaived.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object linkToPayout(final String penaltyId, final String payoutId, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfLinkToPayout.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, payoutId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 3;
        _stmt.bindString(_argIndex, penaltyId);
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
          __preparedStmtOfLinkToPayout.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final String penaltyId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, penaltyId);
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
  public Object deleteByMemberId(final String memberId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByMemberId.acquire();
        int _argIndex = 1;
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
          __preparedStmtOfDeleteByMemberId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldCalculatedPenalties(final String status, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldCalculatedPenalties.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
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
          __preparedStmtOfDeleteOldCalculatedPenalties.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final String penaltyId,
      final Continuation<? super PenaltyEntity> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, penaltyId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PenaltyEntity>() {
      @Override
      @Nullable
      public PenaltyEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final PenaltyEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _result = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<PenaltyEntity> observeById(final String penaltyId) {
    final String _sql = "SELECT * FROM penalties WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, penaltyId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"penalties"}, new Callable<PenaltyEntity>() {
      @Override
      @Nullable
      public PenaltyEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final PenaltyEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _result = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<PenaltyEntity>> observeByRoscaId(final String roscaId) {
    final String _sql = "SELECT * FROM penalties WHERE rosca_id = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"penalties"}, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getByRoscaId(final String roscaId,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE rosca_id = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<PenaltyEntity>> observeByMemberId(final String memberId) {
    final String _sql = "SELECT * FROM penalties WHERE member_id = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, memberId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"penalties"}, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getByMemberId(final String memberId,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE member_id = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, memberId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getByRoscaAndMember(final String roscaId, final String memberId,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE rosca_id = ? AND member_id = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, memberId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<PenaltyEntity>> observeByRoscaAndMember(final String roscaId,
      final String memberId) {
    final String _sql = "SELECT * FROM penalties WHERE rosca_id = ? AND member_id = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, memberId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"penalties"}, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getByPayoutId(final String payoutId,
      final Continuation<? super PenaltyEntity> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE payout_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, payoutId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PenaltyEntity>() {
      @Override
      @Nullable
      public PenaltyEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final PenaltyEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _result = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<PenaltyEntity>> observeByStatus(final String status) {
    final String _sql = "SELECT * FROM penalties WHERE status = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"penalties"}, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getByStatus(final String status,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE status = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getByPenaltyType(final String penaltyType,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE penalty_type = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, penaltyType);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getByRoscaAndStatus(final String roscaId, final String status,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE rosca_id = ? AND status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getByMemberAndStatus(final String memberId, final String status,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE member_id = ? AND status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, memberId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getTotalPenaltiesByRosca(final String roscaId, final String status,
      final Continuation<? super Long> $completion) {
    final String _sql = "SELECT SUM(penalty_amount) FROM penalties WHERE rosca_id = ? AND status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
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
  public Object getTotalPenaltiesByMember(final String memberId, final String status,
      final Continuation<? super Long> $completion) {
    final String _sql = "SELECT SUM(penalty_amount) FROM penalties WHERE member_id = ? AND status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, memberId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
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
  public Object getTotalReimbursementsByRosca(final String roscaId, final String status,
      final Continuation<? super Long> $completion) {
    final String _sql = "SELECT SUM(reimbursement_amount) FROM penalties WHERE rosca_id = ? AND status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
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
  public Object getPenaltyCountByRosca(final String roscaId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM penalties WHERE rosca_id = ?";
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

  @Override
  public Object getPenaltyCountByMember(final String memberId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM penalties WHERE member_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, memberId);
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
  public Object getPenaltyCountByRoscaAndStatus(final String roscaId, final String status,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM penalties WHERE rosca_id = ? AND status = ?";
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

  @Override
  public Object getAveragePenaltyPercentage(final String roscaId, final String penaltyType,
      final Continuation<? super Double> $completion) {
    final String _sql = "SELECT AVG(penalty_percentage) FROM penalties WHERE rosca_id = ? AND penalty_type = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, penaltyType);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
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
  public Object getCalculatedPenalties(final String status,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE status = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getAppliedPenalties(final String status,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE status = ? AND applied_at IS NOT NULL ORDER BY applied_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getWaivedPenalties(final String status,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE status = ? AND waived_at IS NOT NULL ORDER BY waived_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getDisputedPenalties(final String status,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE status = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getWaivedPenaltiesByRosca(final String roscaId, final String status,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE rosca_id = ? AND status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getEarlyExitPenalties(final String roscaId, final String penaltyType,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE rosca_id = ? AND penalty_type = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, penaltyType);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getNonPaymentPenalties(final String memberId, final String penaltyType,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE member_id = ? AND penalty_type = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, memberId);
    _argIndex = 2;
    _statement.bindString(_argIndex, penaltyType);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getViolationPenalties(final String penaltyType, final String excludeStatus,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE penalty_type = ? AND status != ? ORDER BY penalty_amount DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, penaltyType);
    _argIndex = 2;
    _statement.bindString(_argIndex, excludeStatus);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getPenaltiesByDateRange(final String roscaId, final long startTime,
      final long endTime, final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE rosca_id = ? AND created_at BETWEEN ? AND ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<PenaltyEntity>> observePenaltiesByDateRange(final long startTime,
      final long endTime) {
    final String _sql = "SELECT * FROM penalties WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"penalties"}, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getAppliedPenaltiesByDateRange(final long startTime, final long endTime,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE applied_at BETWEEN ? AND ? ORDER BY applied_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getPenaltiesWaivedBy(final String waivedBy,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE waived_by = ? ORDER BY waived_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, waivedBy);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getWaiverCountByRosca(final String roscaId, final String status,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM penalties WHERE rosca_id = ? AND status = ?";
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

  @Override
  public Object getByCalculationMethod(final String method,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE calculation_method = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, method);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getByRoscaAndCalculationMethod(final String roscaId, final String method,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE rosca_id = ? AND calculation_method = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, method);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getPendingPenalties(final String status,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE status = ? AND payout_id IS NULL ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getPendingPenaltiesByRosca(final String roscaId, final String status,
      final Continuation<? super List<PenaltyEntity>> $completion) {
    final String _sql = "SELECT * FROM penalties WHERE rosca_id = ? AND status = ? AND payout_id IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyEntity>>() {
      @Override
      @NonNull
      public List<PenaltyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "rosca_id");
          final int _cursorIndexOfMemberId = CursorUtil.getColumnIndexOrThrow(_cursor, "member_id");
          final int _cursorIndexOfPayoutId = CursorUtil.getColumnIndexOrThrow(_cursor, "payout_id");
          final int _cursorIndexOfPenaltyType = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_type");
          final int _cursorIndexOfTotalContributed = CursorUtil.getColumnIndexOrThrow(_cursor, "total_contributed");
          final int _cursorIndexOfCyclesParticipated = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_participated");
          final int _cursorIndexOfCyclesRemaining = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles_remaining");
          final int _cursorIndexOfPenaltyPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_percentage");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penalty_amount");
          final int _cursorIndexOfReimbursementAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "reimbursement_amount");
          final int _cursorIndexOfCalculationMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "calculation_method");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfExitReason = CursorUtil.getColumnIndexOrThrow(_cursor, "exit_reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAppliedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "applied_at");
          final int _cursorIndexOfWaivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_at");
          final int _cursorIndexOfWaivedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "waived_by");
          final int _cursorIndexOfWaiverReason = CursorUtil.getColumnIndexOrThrow(_cursor, "waiver_reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<PenaltyEntity> _result = new ArrayList<PenaltyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final String _tmpPayoutId;
            if (_cursor.isNull(_cursorIndexOfPayoutId)) {
              _tmpPayoutId = null;
            } else {
              _tmpPayoutId = _cursor.getString(_cursorIndexOfPayoutId);
            }
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final long _tmpTotalContributed;
            _tmpTotalContributed = _cursor.getLong(_cursorIndexOfTotalContributed);
            final int _tmpCyclesParticipated;
            _tmpCyclesParticipated = _cursor.getInt(_cursorIndexOfCyclesParticipated);
            final int _tmpCyclesRemaining;
            _tmpCyclesRemaining = _cursor.getInt(_cursorIndexOfCyclesRemaining);
            final double _tmpPenaltyPercentage;
            _tmpPenaltyPercentage = _cursor.getDouble(_cursorIndexOfPenaltyPercentage);
            final long _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getLong(_cursorIndexOfPenaltyAmount);
            final long _tmpReimbursementAmount;
            _tmpReimbursementAmount = _cursor.getLong(_cursorIndexOfReimbursementAmount);
            final String _tmpCalculationMethod;
            _tmpCalculationMethod = _cursor.getString(_cursorIndexOfCalculationMethod);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpExitReason;
            if (_cursor.isNull(_cursorIndexOfExitReason)) {
              _tmpExitReason = null;
            } else {
              _tmpExitReason = _cursor.getString(_cursorIndexOfExitReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpAppliedAt;
            if (_cursor.isNull(_cursorIndexOfAppliedAt)) {
              _tmpAppliedAt = null;
            } else {
              _tmpAppliedAt = _cursor.getLong(_cursorIndexOfAppliedAt);
            }
            final Long _tmpWaivedAt;
            if (_cursor.isNull(_cursorIndexOfWaivedAt)) {
              _tmpWaivedAt = null;
            } else {
              _tmpWaivedAt = _cursor.getLong(_cursorIndexOfWaivedAt);
            }
            final String _tmpWaivedBy;
            if (_cursor.isNull(_cursorIndexOfWaivedBy)) {
              _tmpWaivedBy = null;
            } else {
              _tmpWaivedBy = _cursor.getString(_cursorIndexOfWaivedBy);
            }
            final String _tmpWaiverReason;
            if (_cursor.isNull(_cursorIndexOfWaiverReason)) {
              _tmpWaiverReason = null;
            } else {
              _tmpWaiverReason = _cursor.getString(_cursorIndexOfWaiverReason);
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
            _item = new PenaltyEntity(_tmpId,_tmpRoscaId,_tmpMemberId,_tmpPayoutId,_tmpPenaltyType,_tmpTotalContributed,_tmpCyclesParticipated,_tmpCyclesRemaining,_tmpPenaltyPercentage,_tmpPenaltyAmount,_tmpReimbursementAmount,_tmpCalculationMethod,_tmpReason,_tmpExitReason,_tmpStatus,_tmpAppliedAt,_tmpWaivedAt,_tmpWaivedBy,_tmpWaiverReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getPenaltyStatsByType(final String roscaId,
      final Continuation<? super List<PenaltyStatistic>> $completion) {
    final String _sql = "SELECT penalty_type, COUNT(*) as count, SUM(penalty_amount) as total FROM penalties WHERE rosca_id = ? GROUP BY penalty_type";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PenaltyStatistic>>() {
      @Override
      @NonNull
      public List<PenaltyStatistic> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPenaltyType = 0;
          final int _cursorIndexOfCount = 1;
          final int _cursorIndexOfTotal = 2;
          final List<PenaltyStatistic> _result = new ArrayList<PenaltyStatistic>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PenaltyStatistic _item;
            final String _tmpPenaltyType;
            _tmpPenaltyType = _cursor.getString(_cursorIndexOfPenaltyType);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            final long _tmpTotal;
            _tmpTotal = _cursor.getLong(_cursorIndexOfTotal);
            _item = new PenaltyStatistic(_tmpPenaltyType,_tmpCount,_tmpTotal);
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
  public Object getPenaltyStatsByMember(final String roscaId,
      final Continuation<? super List<MemberPenaltyStatistic>> $completion) {
    final String _sql = "SELECT member_id, COUNT(*) as count, SUM(penalty_amount) as total FROM penalties WHERE rosca_id = ? GROUP BY member_id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MemberPenaltyStatistic>>() {
      @Override
      @NonNull
      public List<MemberPenaltyStatistic> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMemberId = 0;
          final int _cursorIndexOfCount = 1;
          final int _cursorIndexOfTotal = 2;
          final List<MemberPenaltyStatistic> _result = new ArrayList<MemberPenaltyStatistic>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemberPenaltyStatistic _item;
            final String _tmpMemberId;
            _tmpMemberId = _cursor.getString(_cursorIndexOfMemberId);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            final long _tmpTotal;
            _tmpTotal = _cursor.getLong(_cursorIndexOfTotal);
            _item = new MemberPenaltyStatistic(_tmpMemberId,_tmpCount,_tmpTotal);
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
