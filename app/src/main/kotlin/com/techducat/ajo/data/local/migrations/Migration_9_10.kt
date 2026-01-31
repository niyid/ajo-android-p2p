package com.techducat.ajo.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        
        // ========== CREATE NEW P2P TABLES ==========
        
        // Local Node
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS local_node (
                nodeId TEXT PRIMARY KEY NOT NULL,
                publicKey TEXT NOT NULL,
                privateKeyEncrypted TEXT NOT NULL,
                createdAt INTEGER NOT NULL,
                lastSyncAt INTEGER,
                displayName TEXT,
                deviceInfo TEXT
            )
        """)
        
        // Peers
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS peers (
                id TEXT PRIMARY KEY NOT NULL,
                nodeId TEXT NOT NULL UNIQUE,
                roscaId TEXT NOT NULL,
                publicKey TEXT NOT NULL,
                role TEXT NOT NULL,
                endpoint TEXT,
                status TEXT NOT NULL,
                addedAt INTEGER NOT NULL,
                lastSeenAt INTEGER,
                displayName TEXT
            )
        """)
        
        database.execSQL("CREATE INDEX IF NOT EXISTS index_peers_nodeId ON peers(nodeId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_peers_roscaId ON peers(roscaId)")
        
        // Sync Targets
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS sync_targets (
                id TEXT PRIMARY KEY NOT NULL,
                roscaId TEXT NOT NULL,
                targetPeerId TEXT NOT NULL,
                syncEnabled INTEGER NOT NULL DEFAULT 1,
                lastSyncAttempt INTEGER,
                lastSyncSuccess INTEGER,
                consecutiveFailures INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(targetPeerId) REFERENCES peers(id) ON DELETE CASCADE
            )
        """)
        
        database.execSQL("CREATE INDEX IF NOT EXISTS index_sync_targets_roscaId ON sync_targets(roscaId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_sync_targets_targetPeerId ON sync_targets(targetPeerId)")
        
        // Local Wallets
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS local_wallets (
                id TEXT PRIMARY KEY NOT NULL,
                roscaId TEXT NOT NULL,
                nodeId TEXT NOT NULL,
                walletPath TEXT NOT NULL,
                cacheFilePath TEXT,
                passwordEncrypted TEXT NOT NULL,
                createdAt INTEGER NOT NULL,
                lastAccessedAt INTEGER,
                isMultisig INTEGER NOT NULL DEFAULT 0,
                multisigInfo TEXT,
                label TEXT,
                FOREIGN KEY(roscaId) REFERENCES roscas(id) ON DELETE CASCADE
            )
        """)
        
        database.execSQL("CREATE INDEX IF NOT EXISTS index_local_wallets_roscaId ON local_wallets(roscaId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_local_wallets_nodeId ON local_wallets(nodeId)")
        
        // Sync Log
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS sync_log (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                roscaId TEXT NOT NULL,
                direction TEXT NOT NULL,
                peerNodeId TEXT NOT NULL,
                entityType TEXT NOT NULL,
                entityId TEXT NOT NULL,
                operation TEXT NOT NULL,
                status TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                errorMessage TEXT,
                durationMs INTEGER,
                payloadSize INTEGER
            )
        """)
        
        database.execSQL("CREATE INDEX IF NOT EXISTS index_sync_log_roscaId ON sync_log(roscaId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_sync_log_timestamp ON sync_log(timestamp)")
        
        // Sync Conflicts
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS sync_conflicts (
                id TEXT PRIMARY KEY NOT NULL,
                roscaId TEXT NOT NULL,
                entityType TEXT NOT NULL,
                entityId TEXT NOT NULL,
                localVersion INTEGER NOT NULL,
                remoteVersion INTEGER NOT NULL,
                localPayload TEXT NOT NULL,
                remotePayload TEXT NOT NULL,
                detectedAt INTEGER NOT NULL,
                resolvedAt INTEGER,
                resolution TEXT
            )
        """)
        
        // ========== ADD SYNC METADATA TO EXISTING TABLES ==========
        
        // ROSCAs
        database.execSQL("ALTER TABLE roscas ADD COLUMN creatorNodeId TEXT")
        database.execSQL("ALTER TABLE roscas ADD COLUMN syncVersion INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE roscas ADD COLUMN lastModifiedBy TEXT")
        database.execSQL("ALTER TABLE roscas ADD COLUMN lastModifiedAt INTEGER NOT NULL DEFAULT 0")
        
        // Members
        database.execSQL("ALTER TABLE members ADD COLUMN nodeId TEXT")
        database.execSQL("ALTER TABLE members ADD COLUMN publicWalletAddress TEXT")
        database.execSQL("ALTER TABLE members ADD COLUMN signingOrder INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE members ADD COLUMN syncVersion INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE members ADD COLUMN lastModifiedBy TEXT")
        database.execSQL("ALTER TABLE members ADD COLUMN lastModifiedAt INTEGER NOT NULL DEFAULT 0")
        
        // Contributions
        database.execSQL("ALTER TABLE contributions ADD COLUMN syncVersion INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE contributions ADD COLUMN lastModifiedBy TEXT")
        database.execSQL("ALTER TABLE contributions ADD COLUMN lastModifiedAt INTEGER NOT NULL DEFAULT 0")
        
        // Rounds
        database.execSQL("ALTER TABLE rounds ADD COLUMN syncVersion INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE rounds ADD COLUMN lastModifiedBy TEXT")
        database.execSQL("ALTER TABLE rounds ADD COLUMN lastModifiedAt INTEGER NOT NULL DEFAULT 0")
        
        // Transactions
        database.execSQL("ALTER TABLE transactions ADD COLUMN requiredSignatures INTEGER NOT NULL DEFAULT 3")
        database.execSQL("ALTER TABLE transactions ADD COLUMN syncVersion INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE transactions ADD COLUMN lastModifiedBy TEXT")
        database.execSQL("ALTER TABLE transactions ADD COLUMN lastModifiedAt INTEGER NOT NULL DEFAULT 0")
        
        // MultisigSignatures
        database.execSQL("ALTER TABLE multisig_signatures ADD COLUMN syncVersion INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE multisig_signatures ADD COLUMN lastModifiedBy TEXT")
        database.execSQL("ALTER TABLE multisig_signatures ADD COLUMN lastModifiedAt INTEGER NOT NULL DEFAULT 0")
        
        // Distributions
        database.execSQL("ALTER TABLE distributions ADD COLUMN syncVersion INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE distributions ADD COLUMN lastModifiedBy TEXT")
        database.execSQL("ALTER TABLE distributions ADD COLUMN lastModifiedAt INTEGER NOT NULL DEFAULT 0")
        
        // ========== CREATE SYNC TRIGGERS ==========
        
        // Trigger for MultisigSignature INSERT
        database.execSQL("""
            CREATE TRIGGER multisig_signature_insert_sync
            AFTER INSERT ON multisig_signatures
            FOR EACH ROW
            BEGIN
                INSERT INTO sync_queue (
                    entityType,
                    entityId,
                    roscaId,
                    operation,
                    payload,
                    createdAt
                ) VALUES (
                    'multisig_signatures',
                    NEW.id,
                    NEW.roscaId,
                    'INSERT',
                    json_object(
                        'id', NEW.id,
                        'roscaId', NEW.roscaId,
                        'transactionId', NEW.transactionId,
                        'signerId', NEW.signerId,
                        'signature', NEW.signature,
                        'timestamp', NEW.timestamp,
                        'status', NEW.status,
                        'syncVersion', NEW.syncVersion,
                        'lastModifiedBy', NEW.lastModifiedBy,
                        'lastModifiedAt', NEW.lastModifiedAt
                    ),
                    CAST(strftime('%s', 'now') AS INTEGER) * 1000
                );
            END
        """)
        
        // Trigger for MultisigSignature UPDATE
        database.execSQL("""
            CREATE TRIGGER multisig_signature_update_sync
            AFTER UPDATE ON multisig_signatures
            FOR EACH ROW
            WHEN NEW.syncVersion > OLD.syncVersion
            BEGIN
                INSERT INTO sync_queue (
                    entityType,
                    entityId,
                    roscaId,
                    operation,
                    payload,
                    createdAt
                ) VALUES (
                    'multisig_signatures',
                    NEW.id,
                    NEW.roscaId,
                    'UPDATE',
                    json_object(
                        'id', NEW.id,
                        'roscaId', NEW.roscaId,
                        'transactionId', NEW.transactionId,
                        'signerId', NEW.signerId,
                        'signature', NEW.signature,
                        'timestamp', NEW.timestamp,
                        'status', NEW.status,
                        'syncVersion', NEW.syncVersion,
                        'lastModifiedBy', NEW.lastModifiedBy,
                        'lastModifiedAt', NEW.lastModifiedAt
                    ),
                    CAST(strftime('%s', 'now') AS INTEGER) * 1000
                );
            END
        """)
        
        // Trigger for Transaction UPDATE
        database.execSQL("""
            CREATE TRIGGER transaction_update_sync
            AFTER UPDATE ON transactions
            FOR EACH ROW
            WHEN NEW.syncVersion > OLD.syncVersion
            BEGIN
                INSERT INTO sync_queue (
                    entityType,
                    entityId,
                    roscaId,
                    operation,
                    payload,
                    createdAt
                ) VALUES (
                    'transactions',
                    NEW.id,
                    NEW.roscaId,
                    'UPDATE',
                    json_object(
                        'id', NEW.id,
                        'roscaId', NEW.roscaId,
                        'txHash', NEW.txHash,
                        'type', NEW.type,
                        'amount', NEW.amount,
                        'status', NEW.status,
                        'requiredSignatures', NEW.requiredSignatures,
                        'syncVersion', NEW.syncVersion,
                        'lastModifiedBy', NEW.lastModifiedBy,
                        'lastModifiedAt', NEW.lastModifiedAt
                    ),
                    CAST(strftime('%s', 'now') AS INTEGER) * 1000
                );
            END
        """)
        
        // Trigger for Contribution INSERT
        database.execSQL("""
            CREATE TRIGGER contribution_insert_sync
            AFTER INSERT ON contributions
            FOR EACH ROW
            BEGIN
                INSERT INTO sync_queue (
                    entityType,
                    entityId,
                    roscaId,
                    operation,
                    payload,
                    createdAt
                ) VALUES (
                    'contributions',
                    NEW.id,
                    NEW.roscaId,
                    'INSERT',
                    json_object(
                        'id', NEW.id,
                        'roscaId', NEW.roscaId,
                        'memberId', NEW.memberId,
                        'roundId', NEW.roundId,
                        'amount', NEW.amount,
                        'timestamp', NEW.timestamp,
                        'status', NEW.status,
                        'txHash', NEW.txHash,
                        'syncVersion', NEW.syncVersion,
                        'lastModifiedBy', NEW.lastModifiedBy,
                        'lastModifiedAt', NEW.lastModifiedAt
                    ),
                    CAST(strftime('%s', 'now') AS INTEGER) * 1000
                );
            END
        """)
        
        // Trigger for Member INSERT
        database.execSQL("""
            CREATE TRIGGER member_insert_sync
            AFTER INSERT ON members
            FOR EACH ROW
            BEGIN
                INSERT INTO sync_queue (
                    entityType,
                    entityId,
                    roscaId,
                    operation,
                    payload,
                    createdAt
                ) VALUES (
                    'members',
                    NEW.id,
                    NEW.roscaId,
                    'INSERT',
                    json_object(
                        'id', NEW.id,
                        'roscaId', NEW.roscaId,
                        'userId', NEW.userId,
                        'nodeId', NEW.nodeId,
                        'name', NEW.name,
                        'publicWalletAddress', NEW.publicWalletAddress,
                        'signingOrder', NEW.signingOrder,
                        'joinedAt', NEW.joinedAt,
                        'position', NEW.position,
                        'isActive', NEW.isActive,
                        'status', NEW.status,
                        'syncVersion', NEW.syncVersion,
                        'lastModifiedBy', NEW.lastModifiedBy,
                        'lastModifiedAt', NEW.lastModifiedAt
                    ),
                    CAST(strftime('%s', 'now') AS INTEGER) * 1000
                );
            END
        """)
    }
}
