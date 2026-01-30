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
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.techducat.ajo.data.local.entity.ServiceFeeEntity;
import java.lang.Class;
import java.lang.Double;
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
public final class ServiceFeeDao_Impl implements ServiceFeeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ServiceFeeEntity> __insertionAdapterOfServiceFeeEntity;

  private final EntityDeletionOrUpdateAdapter<ServiceFeeEntity> __updateAdapterOfServiceFeeEntity;

  public ServiceFeeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfServiceFeeEntity = new EntityInsertionAdapter<ServiceFeeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `service_fees` (`id`,`distributionId`,`roscaId`,`grossAmount`,`feeAmount`,`netAmount`,`feePercentage`,`serviceWallet`,`recipientTxHash`,`feeTxHash`,`status`,`errorMessage`,`createdAt`,`completedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ServiceFeeEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDistributionId());
        statement.bindString(3, entity.getRoscaId());
        statement.bindLong(4, entity.getGrossAmount());
        statement.bindLong(5, entity.getFeeAmount());
        statement.bindLong(6, entity.getNetAmount());
        statement.bindDouble(7, entity.getFeePercentage());
        statement.bindString(8, entity.getServiceWallet());
        if (entity.getRecipientTxHash() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getRecipientTxHash());
        }
        if (entity.getFeeTxHash() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getFeeTxHash());
        }
        statement.bindString(11, entity.getStatus());
        if (entity.getErrorMessage() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getErrorMessage());
        }
        statement.bindLong(13, entity.getCreatedAt());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getCompletedAt());
        }
      }
    };
    this.__updateAdapterOfServiceFeeEntity = new EntityDeletionOrUpdateAdapter<ServiceFeeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `service_fees` SET `id` = ?,`distributionId` = ?,`roscaId` = ?,`grossAmount` = ?,`feeAmount` = ?,`netAmount` = ?,`feePercentage` = ?,`serviceWallet` = ?,`recipientTxHash` = ?,`feeTxHash` = ?,`status` = ?,`errorMessage` = ?,`createdAt` = ?,`completedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ServiceFeeEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDistributionId());
        statement.bindString(3, entity.getRoscaId());
        statement.bindLong(4, entity.getGrossAmount());
        statement.bindLong(5, entity.getFeeAmount());
        statement.bindLong(6, entity.getNetAmount());
        statement.bindDouble(7, entity.getFeePercentage());
        statement.bindString(8, entity.getServiceWallet());
        if (entity.getRecipientTxHash() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getRecipientTxHash());
        }
        if (entity.getFeeTxHash() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getFeeTxHash());
        }
        statement.bindString(11, entity.getStatus());
        if (entity.getErrorMessage() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getErrorMessage());
        }
        statement.bindLong(13, entity.getCreatedAt());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getCompletedAt());
        }
        statement.bindString(15, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final ServiceFeeEntity fee, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfServiceFeeEntity.insert(fee);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ServiceFeeEntity fee, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfServiceFeeEntity.handle(fee);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPendingFees(final Continuation<? super List<ServiceFeeEntity>> $completion) {
    final String _sql = "SELECT * FROM service_fees WHERE status = 'PENDING'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ServiceFeeEntity>>() {
      @Override
      @NonNull
      public List<ServiceFeeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDistributionId = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionId");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "grossAmount");
          final int _cursorIndexOfFeeAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "feeAmount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "netAmount");
          final int _cursorIndexOfFeePercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "feePercentage");
          final int _cursorIndexOfServiceWallet = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceWallet");
          final int _cursorIndexOfRecipientTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientTxHash");
          final int _cursorIndexOfFeeTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "feeTxHash");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final List<ServiceFeeEntity> _result = new ArrayList<ServiceFeeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ServiceFeeEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDistributionId;
            _tmpDistributionId = _cursor.getString(_cursorIndexOfDistributionId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpFeeAmount;
            _tmpFeeAmount = _cursor.getLong(_cursorIndexOfFeeAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final double _tmpFeePercentage;
            _tmpFeePercentage = _cursor.getDouble(_cursorIndexOfFeePercentage);
            final String _tmpServiceWallet;
            _tmpServiceWallet = _cursor.getString(_cursorIndexOfServiceWallet);
            final String _tmpRecipientTxHash;
            if (_cursor.isNull(_cursorIndexOfRecipientTxHash)) {
              _tmpRecipientTxHash = null;
            } else {
              _tmpRecipientTxHash = _cursor.getString(_cursorIndexOfRecipientTxHash);
            }
            final String _tmpFeeTxHash;
            if (_cursor.isNull(_cursorIndexOfFeeTxHash)) {
              _tmpFeeTxHash = null;
            } else {
              _tmpFeeTxHash = _cursor.getString(_cursorIndexOfFeeTxHash);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _item = new ServiceFeeEntity(_tmpId,_tmpDistributionId,_tmpRoscaId,_tmpGrossAmount,_tmpFeeAmount,_tmpNetAmount,_tmpFeePercentage,_tmpServiceWallet,_tmpRecipientTxHash,_tmpFeeTxHash,_tmpStatus,_tmpErrorMessage,_tmpCreatedAt,_tmpCompletedAt);
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
  public Object getTotalFeesCollected(final Continuation<? super Double> $completion) {
    final String _sql = "SELECT SUM(CAST(feeAmount AS REAL)) FROM service_fees WHERE status = 'PAID'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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
  public Object getById(final String id, final Continuation<? super ServiceFeeEntity> $completion) {
    final String _sql = "SELECT * FROM service_fees WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ServiceFeeEntity>() {
      @Override
      @Nullable
      public ServiceFeeEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDistributionId = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionId");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "grossAmount");
          final int _cursorIndexOfFeeAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "feeAmount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "netAmount");
          final int _cursorIndexOfFeePercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "feePercentage");
          final int _cursorIndexOfServiceWallet = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceWallet");
          final int _cursorIndexOfRecipientTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientTxHash");
          final int _cursorIndexOfFeeTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "feeTxHash");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final ServiceFeeEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDistributionId;
            _tmpDistributionId = _cursor.getString(_cursorIndexOfDistributionId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpFeeAmount;
            _tmpFeeAmount = _cursor.getLong(_cursorIndexOfFeeAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final double _tmpFeePercentage;
            _tmpFeePercentage = _cursor.getDouble(_cursorIndexOfFeePercentage);
            final String _tmpServiceWallet;
            _tmpServiceWallet = _cursor.getString(_cursorIndexOfServiceWallet);
            final String _tmpRecipientTxHash;
            if (_cursor.isNull(_cursorIndexOfRecipientTxHash)) {
              _tmpRecipientTxHash = null;
            } else {
              _tmpRecipientTxHash = _cursor.getString(_cursorIndexOfRecipientTxHash);
            }
            final String _tmpFeeTxHash;
            if (_cursor.isNull(_cursorIndexOfFeeTxHash)) {
              _tmpFeeTxHash = null;
            } else {
              _tmpFeeTxHash = _cursor.getString(_cursorIndexOfFeeTxHash);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _result = new ServiceFeeEntity(_tmpId,_tmpDistributionId,_tmpRoscaId,_tmpGrossAmount,_tmpFeeAmount,_tmpNetAmount,_tmpFeePercentage,_tmpServiceWallet,_tmpRecipientTxHash,_tmpFeeTxHash,_tmpStatus,_tmpErrorMessage,_tmpCreatedAt,_tmpCompletedAt);
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
  public Object getAllFeeRecords(final Continuation<? super List<ServiceFeeEntity>> $completion) {
    final String _sql = "SELECT * FROM service_fees ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ServiceFeeEntity>>() {
      @Override
      @NonNull
      public List<ServiceFeeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDistributionId = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionId");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "grossAmount");
          final int _cursorIndexOfFeeAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "feeAmount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "netAmount");
          final int _cursorIndexOfFeePercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "feePercentage");
          final int _cursorIndexOfServiceWallet = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceWallet");
          final int _cursorIndexOfRecipientTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientTxHash");
          final int _cursorIndexOfFeeTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "feeTxHash");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final List<ServiceFeeEntity> _result = new ArrayList<ServiceFeeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ServiceFeeEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDistributionId;
            _tmpDistributionId = _cursor.getString(_cursorIndexOfDistributionId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpFeeAmount;
            _tmpFeeAmount = _cursor.getLong(_cursorIndexOfFeeAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final double _tmpFeePercentage;
            _tmpFeePercentage = _cursor.getDouble(_cursorIndexOfFeePercentage);
            final String _tmpServiceWallet;
            _tmpServiceWallet = _cursor.getString(_cursorIndexOfServiceWallet);
            final String _tmpRecipientTxHash;
            if (_cursor.isNull(_cursorIndexOfRecipientTxHash)) {
              _tmpRecipientTxHash = null;
            } else {
              _tmpRecipientTxHash = _cursor.getString(_cursorIndexOfRecipientTxHash);
            }
            final String _tmpFeeTxHash;
            if (_cursor.isNull(_cursorIndexOfFeeTxHash)) {
              _tmpFeeTxHash = null;
            } else {
              _tmpFeeTxHash = _cursor.getString(_cursorIndexOfFeeTxHash);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _item = new ServiceFeeEntity(_tmpId,_tmpDistributionId,_tmpRoscaId,_tmpGrossAmount,_tmpFeeAmount,_tmpNetAmount,_tmpFeePercentage,_tmpServiceWallet,_tmpRecipientTxHash,_tmpFeeTxHash,_tmpStatus,_tmpErrorMessage,_tmpCreatedAt,_tmpCompletedAt);
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
  public Object getFeeHistory(final int limit, final int offset,
      final Continuation<? super List<ServiceFeeEntity>> $completion) {
    final String _sql = "SELECT * FROM service_fees ORDER BY createdAt DESC LIMIT ? OFFSET ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    _argIndex = 2;
    _statement.bindLong(_argIndex, offset);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ServiceFeeEntity>>() {
      @Override
      @NonNull
      public List<ServiceFeeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDistributionId = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionId");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "grossAmount");
          final int _cursorIndexOfFeeAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "feeAmount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "netAmount");
          final int _cursorIndexOfFeePercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "feePercentage");
          final int _cursorIndexOfServiceWallet = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceWallet");
          final int _cursorIndexOfRecipientTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientTxHash");
          final int _cursorIndexOfFeeTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "feeTxHash");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final List<ServiceFeeEntity> _result = new ArrayList<ServiceFeeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ServiceFeeEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDistributionId;
            _tmpDistributionId = _cursor.getString(_cursorIndexOfDistributionId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpFeeAmount;
            _tmpFeeAmount = _cursor.getLong(_cursorIndexOfFeeAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final double _tmpFeePercentage;
            _tmpFeePercentage = _cursor.getDouble(_cursorIndexOfFeePercentage);
            final String _tmpServiceWallet;
            _tmpServiceWallet = _cursor.getString(_cursorIndexOfServiceWallet);
            final String _tmpRecipientTxHash;
            if (_cursor.isNull(_cursorIndexOfRecipientTxHash)) {
              _tmpRecipientTxHash = null;
            } else {
              _tmpRecipientTxHash = _cursor.getString(_cursorIndexOfRecipientTxHash);
            }
            final String _tmpFeeTxHash;
            if (_cursor.isNull(_cursorIndexOfFeeTxHash)) {
              _tmpFeeTxHash = null;
            } else {
              _tmpFeeTxHash = _cursor.getString(_cursorIndexOfFeeTxHash);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _item = new ServiceFeeEntity(_tmpId,_tmpDistributionId,_tmpRoscaId,_tmpGrossAmount,_tmpFeeAmount,_tmpNetAmount,_tmpFeePercentage,_tmpServiceWallet,_tmpRecipientTxHash,_tmpFeeTxHash,_tmpStatus,_tmpErrorMessage,_tmpCreatedAt,_tmpCompletedAt);
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
  public Object getFeeRecordsByRosca(final String roscaId,
      final Continuation<? super List<ServiceFeeEntity>> $completion) {
    final String _sql = "SELECT * FROM service_fees WHERE roscaId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, roscaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ServiceFeeEntity>>() {
      @Override
      @NonNull
      public List<ServiceFeeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDistributionId = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionId");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "grossAmount");
          final int _cursorIndexOfFeeAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "feeAmount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "netAmount");
          final int _cursorIndexOfFeePercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "feePercentage");
          final int _cursorIndexOfServiceWallet = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceWallet");
          final int _cursorIndexOfRecipientTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientTxHash");
          final int _cursorIndexOfFeeTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "feeTxHash");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final List<ServiceFeeEntity> _result = new ArrayList<ServiceFeeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ServiceFeeEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDistributionId;
            _tmpDistributionId = _cursor.getString(_cursorIndexOfDistributionId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpFeeAmount;
            _tmpFeeAmount = _cursor.getLong(_cursorIndexOfFeeAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final double _tmpFeePercentage;
            _tmpFeePercentage = _cursor.getDouble(_cursorIndexOfFeePercentage);
            final String _tmpServiceWallet;
            _tmpServiceWallet = _cursor.getString(_cursorIndexOfServiceWallet);
            final String _tmpRecipientTxHash;
            if (_cursor.isNull(_cursorIndexOfRecipientTxHash)) {
              _tmpRecipientTxHash = null;
            } else {
              _tmpRecipientTxHash = _cursor.getString(_cursorIndexOfRecipientTxHash);
            }
            final String _tmpFeeTxHash;
            if (_cursor.isNull(_cursorIndexOfFeeTxHash)) {
              _tmpFeeTxHash = null;
            } else {
              _tmpFeeTxHash = _cursor.getString(_cursorIndexOfFeeTxHash);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _item = new ServiceFeeEntity(_tmpId,_tmpDistributionId,_tmpRoscaId,_tmpGrossAmount,_tmpFeeAmount,_tmpNetAmount,_tmpFeePercentage,_tmpServiceWallet,_tmpRecipientTxHash,_tmpFeeTxHash,_tmpStatus,_tmpErrorMessage,_tmpCreatedAt,_tmpCompletedAt);
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
  public Object getRecentTransactionsForWallet(final String walletAddress, final int limit,
      final Continuation<? super List<ServiceFeeEntity>> $completion) {
    final String _sql = "SELECT * FROM service_fees WHERE serviceWallet = ? ORDER BY createdAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, walletAddress);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ServiceFeeEntity>>() {
      @Override
      @NonNull
      public List<ServiceFeeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDistributionId = CursorUtil.getColumnIndexOrThrow(_cursor, "distributionId");
          final int _cursorIndexOfRoscaId = CursorUtil.getColumnIndexOrThrow(_cursor, "roscaId");
          final int _cursorIndexOfGrossAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "grossAmount");
          final int _cursorIndexOfFeeAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "feeAmount");
          final int _cursorIndexOfNetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "netAmount");
          final int _cursorIndexOfFeePercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "feePercentage");
          final int _cursorIndexOfServiceWallet = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceWallet");
          final int _cursorIndexOfRecipientTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientTxHash");
          final int _cursorIndexOfFeeTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "feeTxHash");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final List<ServiceFeeEntity> _result = new ArrayList<ServiceFeeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ServiceFeeEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDistributionId;
            _tmpDistributionId = _cursor.getString(_cursorIndexOfDistributionId);
            final String _tmpRoscaId;
            _tmpRoscaId = _cursor.getString(_cursorIndexOfRoscaId);
            final long _tmpGrossAmount;
            _tmpGrossAmount = _cursor.getLong(_cursorIndexOfGrossAmount);
            final long _tmpFeeAmount;
            _tmpFeeAmount = _cursor.getLong(_cursorIndexOfFeeAmount);
            final long _tmpNetAmount;
            _tmpNetAmount = _cursor.getLong(_cursorIndexOfNetAmount);
            final double _tmpFeePercentage;
            _tmpFeePercentage = _cursor.getDouble(_cursorIndexOfFeePercentage);
            final String _tmpServiceWallet;
            _tmpServiceWallet = _cursor.getString(_cursorIndexOfServiceWallet);
            final String _tmpRecipientTxHash;
            if (_cursor.isNull(_cursorIndexOfRecipientTxHash)) {
              _tmpRecipientTxHash = null;
            } else {
              _tmpRecipientTxHash = _cursor.getString(_cursorIndexOfRecipientTxHash);
            }
            final String _tmpFeeTxHash;
            if (_cursor.isNull(_cursorIndexOfFeeTxHash)) {
              _tmpFeeTxHash = null;
            } else {
              _tmpFeeTxHash = _cursor.getString(_cursorIndexOfFeeTxHash);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _item = new ServiceFeeEntity(_tmpId,_tmpDistributionId,_tmpRoscaId,_tmpGrossAmount,_tmpFeeAmount,_tmpNetAmount,_tmpFeePercentage,_tmpServiceWallet,_tmpRecipientTxHash,_tmpFeeTxHash,_tmpStatus,_tmpErrorMessage,_tmpCreatedAt,_tmpCompletedAt);
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
