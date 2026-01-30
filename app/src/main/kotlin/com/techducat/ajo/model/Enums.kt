package com.techducat.ajo.model

/**
 * Shared enums for the Ajo application
 */

// Transaction status
enum class TransactionStatus {
    PENDING,
    CONFIRMED,
    FAILED
}

// Wallet sync status
enum class SyncStatus {
    SYNCED,
    SYNCING,
    OUT_OF_SYNC,
    ERROR
}

// Notification types
enum class NotificationType {
    ROSCA_CREATED,
    ROSCA_ACTIVATED,
    MEMBER_JOINED,
    ROUND_STARTED,
    CONTRIBUTION_RECEIVED,
    RECIPIENT_SELECTED,
    PAYOUT_COMPLETED,
    ROUND_COMPLETED,
    ROSCA_COMPLETED,
    ROSCA_PAUSED,
    ROSCA_RESUMED
}

// User roles
enum class UserRole {
    CREATOR,
    MEMBER,
    ADMIN
}
