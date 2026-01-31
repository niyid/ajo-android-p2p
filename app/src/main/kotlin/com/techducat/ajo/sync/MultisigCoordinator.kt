package com.techducat.ajo.sync

import android.content.Context
import android.util.Log
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.MultisigSignatureEntity
import com.techducat.ajo.data.local.entity.TransactionEntity
import com.techducat.ajo.core.crypto.KeyManagerImpl
import com.techducat.ajo.wallet.WalletSuite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MultisigCoordinator(
    private val context: Context,
    private val db: AjoDatabase
) {
    
    private val walletSuite: WalletSuite by lazy {
        WalletSuite.getInstance(context)
    }
    
    companion object {
        private const val TAG = "MultisigCoordinator"
    }
    
    fun observePendingSignatures(roscaId: String): Flow<List<PendingSignature>> {
        return db.multisigSignatureDao().getByRoscaFlow(roscaId).map { allSignatures ->
            val pending = mutableListOf<PendingSignature>()
            
            val localNode = db.localNodeDao().getLocalNode()
            val myMember = localNode?.let { node ->
                db.memberDao().getByNodeId(node.nodeId, roscaId)
            }
            
            if (myMember == null) {
                Log.w(TAG, "Member not found for node")
                return@map emptyList()
            }
            
            val pendingTxs = db.transactionDao().getPendingSignatures()
            
            pendingTxs.forEach { tx ->
                if (tx.roscaId != roscaId) return@forEach
                
                val txSignatures = allSignatures.filter { 
                    it.txHash == (tx.txHash ?: tx.id) 
                }
                
                val iHaveSigned = txSignatures.any { sig ->
                    sig.memberId == myMember.id
                }
                
                if (!iHaveSigned) {
                    val currentSigCount = txSignatures.size
                    val mySigningOrder = myMember.signingOrder
                    
                    val isMyTurn = if (mySigningOrder > 0) {
                        currentSigCount + 1 == mySigningOrder
                    } else {
                        true
                    }
                    
                    if (isMyTurn) {
                        pending.add(PendingSignature(
                            transactionId = tx.id,
                            roscaId = tx.roscaId,
                            currentSignatures = currentSigCount,
                            requiredSignatures = tx.requiredSignatures,
                            amount = tx.amount,
                            recipient = tx.toAddress ?: ""
                        ))
                    }
                }
            }
            
            pending
        }
    }
    
    suspend fun signTransaction(txId: String): SigningResult = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Signing transaction: $txId")
            
            val tx = db.transactionDao().getByTxHash(txId)
                ?: return@withContext SigningResult.Error("Transaction not found: $txId")
            
            if (tx.status != TransactionEntity.STATUS_PENDING_SIGNATURES) {
                return@withContext SigningResult.Error("Transaction not pending signatures: ${tx.status}")
            }
            
            val txHashOrId = tx.txHash ?: tx.id
            val existingSigs = db.multisigSignatureDao().getByTransaction(txHashOrId)
            Log.i(TAG, "Found ${existingSigs.size} existing signatures")
            
            val localNode = KeyManagerImpl.getOrCreateLocalNode(context)
            val myMember = db.memberDao().getByNodeId(localNode.nodeId, tx.roscaId)
                ?: return@withContext SigningResult.Error("Member not found")
            
            val alreadySigned = existingSigs.any { sig ->
                sig.memberId == myMember.id
            }
            
            if (alreadySigned) {
                return@withContext SigningResult.Error("Already signed")
            }
            
            val localWallet = db.localWalletDao().getWalletForNode(tx.roscaId, localNode.nodeId)
                ?: return@withContext SigningResult.Error("Wallet not found")
            
            // Get round number from ROSCA entity
            val rosca = db.roscaDao().getById(tx.roscaId)
            val roundNumber = rosca?.currentRound ?: 0
            
            // Use WalletSuite's multisig functions
            val signature = if (existingSigs.isEmpty()) {
                // First signer: create the multisig transaction
                createMultisigTransaction(tx, localWallet.walletPath)
            } else {
                // Subsequent signers: sign existing transaction
                signExistingMultisigTransaction(tx, existingSigs, localWallet.walletPath)
            }
            
            // Store signature
            val sigEntity = MultisigSignatureEntity(
                id = "sig_${System.currentTimeMillis()}_${myMember.id}",
                roscaId = tx.roscaId,
                roundNumber = roundNumber,
                txHash = txHashOrId,
                memberId = myMember.id,
                hasSigned = true,
                signature = signature,
                timestamp = System.currentTimeMillis()
            )
            
            db.multisigSignatureDao().insert(sigEntity)
            Log.i(TAG, "Signature stored")
            
            val newCount = existingSigs.size + 1
            val updatedTx = tx.copy(
                currentSignatureCount = newCount,
                syncVersion = tx.syncVersion + 1,
                lastModifiedBy = localNode.nodeId,
                lastModifiedAt = System.currentTimeMillis()
            )
            
            db.transactionDao().update(updatedTx)
            
            if (newCount >= tx.requiredSignatures) {
                Log.i(TAG, "All signatures collected! Broadcasting...")
                broadcastTransaction(txId, signature)
            }
            
            SigningResult.Success(newCount, tx.requiredSignatures)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error signing transaction", e)
            SigningResult.Error(e.message ?: "Unknown error")
        }
    }
    
    private suspend fun createMultisigTransaction(
        tx: TransactionEntity,
        walletPath: String
    ): String = suspendCancellableCoroutine { continuation ->
        try {
            Log.i(TAG, "First signer - creating multisig transaction")
            
            // Use WalletSuite's createMultisigTransaction function
            walletSuite.createMultisigTransaction(
                tx.roscaId,
                tx.toAddress ?: throw IllegalArgumentException("No destination address"),
                tx.amount,
                object : WalletSuite.MultisigTxCallback {
                    override fun onSuccess(txData: String) {
                        Log.i(TAG, "Multisig transaction created, size: ${txData.length}")
                        continuation.resume(txData)
                    }
                    
                    override fun onError(error: String) {
                        Log.e(TAG, "Failed to create multisig transaction: $error")
                        continuation.resumeWithException(IllegalStateException("Failed to create tx: $error"))
                    }
                }
            )
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }
    
    private suspend fun signExistingMultisigTransaction(
        tx: TransactionEntity,
        existingSignatures: List<MultisigSignatureEntity>,
        walletPath: String
    ): String = suspendCancellableCoroutine { continuation ->
        try {
            Log.i(TAG, "Signing existing partial transaction")
            
            val lastSignature = existingSignatures.last().signature
                ?: throw IllegalStateException("Last signature is null")
            
            // Use WalletSuite's signMultisigTransaction function
            walletSuite.signMultisigTransaction(
                tx.roscaId,
                lastSignature,
                object : WalletSuite.MultisigSignCallback {
                    override fun onSuccess(signedData: String) {
                        Log.i(TAG, "Multisig transaction signed, size: ${signedData.length}")
                        continuation.resume(signedData)
                    }
                    
                    override fun onError(error: String) {
                        Log.e(TAG, "Failed to sign multisig transaction: $error")
                        continuation.resumeWithException(IllegalStateException("Failed to sign tx: $error"))
                    }
                }
            )
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }
    
    private suspend fun broadcastTransaction(txId: String, finalSignedTxHex: String) {
        try {
            val tx = db.transactionDao().getByTxHash(txId) ?: return
            
            Log.i(TAG, "Broadcasting transaction: $txId")
            
            val localNode = db.localNodeDao().getLocalNode()
                ?: throw IllegalStateException("Local node not found")
            
            // Use WalletSuite's submitMultisigTransaction function
            suspendCancellableCoroutine<String> { continuation ->
                walletSuite.submitMultisigTransaction(
                    tx.roscaId,
                    finalSignedTxHex,
                    object : WalletSuite.MultisigSubmitCallback {
                        override fun onSuccess(txHash: String) {
                            Log.i(TAG, "Transaction broadcast successfully: $txHash")
                            continuation.resume(txHash)
                        }
                        
                        override fun onError(error: String) {
                            Log.e(TAG, "Failed to broadcast transaction: $error")
                            continuation.resumeWithException(IllegalStateException(error))
                        }
                    }
                )
            }.let { txHash ->
                // Update transaction status
                val updatedTx = tx.copy(
                    txHash = txHash,
                    status = TransactionEntity.STATUS_BROADCAST,
                    confirmations = 0,
                    syncVersion = tx.syncVersion + 1,
                    lastModifiedBy = localNode.nodeId,
                    lastModifiedAt = System.currentTimeMillis()
                )
                
                db.transactionDao().update(updatedTx)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error broadcasting transaction", e)
        }
    }
}

data class PendingSignature(
    val transactionId: String,
    val roscaId: String,
    val currentSignatures: Int,
    val requiredSignatures: Int,
    val amount: Long,
    val recipient: String
)

sealed class SigningResult {
    data class Success(val currentSigs: Int, val requiredSigs: Int) : SigningResult()
    data class Error(val message: String) : SigningResult()
}
