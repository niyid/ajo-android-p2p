package com.techducat.ajo.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration to add service_fees table
 */
val MIGRATION_SERVICE_FEES = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS service_fees (
                id TEXT PRIMARY KEY NOT NULL,
                distribution_id TEXT NOT NULL,
                rosca_id TEXT NOT NULL,
                gross_amount INTEGER NOT NULL,
                fee_amount INTEGER NOT NULL,
                net_amount INTEGER NOT NULL,
                fee_percentage REAL NOT NULL,
                service_wallet TEXT NOT NULL,
                recipient_tx_hash TEXT,
                fee_tx_hash TEXT,
                status TEXT NOT NULL,
                error_message TEXT,
                created_at INTEGER NOT NULL,
                completed_at INTEGER
            )
        """.trimIndent())
        
        // Create indexes for better query performance
        database.execSQL("CREATE INDEX IF NOT EXISTS index_service_fees_rosca_id ON service_fees(rosca_id)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_service_fees_status ON service_fees(status)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_service_fees_service_wallet ON service_fees(service_wallet)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_service_fees_created_at ON service_fees(created_at)")
    }
}
