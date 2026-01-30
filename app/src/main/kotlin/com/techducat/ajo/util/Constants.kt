package com.techducat.ajo.util

object Constants {
    // Timeouts
    const val SIGNATURE_COLLECTION_TIMEOUT_MS = 180000L  // 3 minutes
    const val MULTISIG_SETUP_TIMEOUT_MS = 300000L  // 5 minutes
    const val TRANSACTION_CONFIRMATION_TIMEOUT_MS = 600000L  // 10 minutes
    
    // Network
    const val DEFAULT_NODE_URL = "node.moneroworld.com:18089"
    const val FALLBACK_NODE_URL = "node.xmr.to:18081"
    
    // Wallet
    const val WALLET_PASSWORD = "ajo-secure-wallet"  // Should be user-provided in production
    const val MIN_CONFIRMATIONS = 10
    
    // ROSCA
    const val MIN_MEMBERS = 3
    const val MAX_MEMBERS = 20
    const val MIN_CONTRIBUTION_AMOUNT = 100000000L  // 0.0001 XMR in atomic units
    const val DEFAULT_FREQUENCY_DAYS = 7
    
    // DLT
    const val IPFS_GATEWAY = "https://ipfs.io/ipfs/"
    const val MAX_TX_EXTRA_SIZE = 1024
    const val MAGIC_BYTES = "AJO:"
    
    // Fees
    const val SERVICE_FEE_PERCENTAGE = 0.05  // 5%
    const val NETWORK_FEE_PRIORITY = 1

    const val MONERO_ATOMIC_UNITS = 1e12
    
}
