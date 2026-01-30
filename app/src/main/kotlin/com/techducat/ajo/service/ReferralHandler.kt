package com.techducat.ajo.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.InviteEntity
import com.techducat.ajo.service.RoscaManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Handles referral codes when users first install and login to the app
 */
class ReferralHandler(
    private val context: Context,
    private val database: AjoDatabase,
    private val roscaManager: RoscaManager  // ✅ ADDED: Inject RoscaManager
) {
    
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val TAG = "com.techducat.ajo.service.ReferralHandler"
        private const val PREF_REFERRAL_CODE = "pending_referral_code"
        private const val PREF_ROSCA_ID = "pending_rosca_id"
        private const val PREF_REFERRAL_PROCESSED = "referral_processed"
    }
    
    /**
     * Call this when app starts to check for deep link referral
     * Example deep link: ajo://join?ref=ABC12345&rosca=xyz123
     * Or web link: https://ajo.app/join?ref=ABC12345&rosca=xyz123
     */
    fun handleDeepLink(intent: Intent) {
        val data: Uri? = intent.data
        
        if (data != null) {
            Log.d(TAG, "Deep link received: $data")
            
            val referralCode = data.getQueryParameter("ref")
            val roscaId = data.getQueryParameter("rosca")
            
            if (!referralCode.isNullOrEmpty() && !roscaId.isNullOrEmpty()) {
                // Store for later processing after login
                savePendingReferral(referralCode, roscaId)
                Log.d(TAG, "Saved pending referral: $referralCode for ROSCA: $roscaId")
            }
        }
    }
    
    /**
     * Call this immediately after successful first-time login
     */
    suspend fun processReferralAfterLogin(userId: String, userEmail: String?): ReferralResult {
        return withContext(Dispatchers.IO) {
            try {
                // Check if already processed
                if (prefs.getBoolean(PREF_REFERRAL_PROCESSED, false)) {
                    Log.d(TAG, "Referral already processed for this user")
                    return@withContext ReferralResult.AlreadyProcessed
                }
                
                // Get pending referral from deep link or manual entry
                val referralCode = prefs.getString(PREF_REFERRAL_CODE, null)
                val roscaId = prefs.getString(PREF_ROSCA_ID, null)
                
                if (referralCode.isNullOrEmpty() || roscaId.isNullOrEmpty()) {
                    Log.d(TAG, "No pending referral found")
                    return@withContext ReferralResult.NoReferral
                }
                
                Log.d(TAG, "Processing referral: $referralCode for user: $userId")
                
                // Look up the invite
                val invite = database.inviteDao().getInviteByReferralCode(referralCode)
                
                if (invite == null) {
                    Log.e(TAG, "Invalid referral code: $referralCode")
                    clearPendingReferral()
                    return@withContext ReferralResult.InvalidCode
                }
                
                // Check if invite is for correct ROSCA
                if (invite.roscaId != roscaId) {
                    Log.e(TAG, "ROSCA ID mismatch: ${invite.roscaId} != $roscaId")
                    clearPendingReferral()
                    return@withContext ReferralResult.InvalidCode
                }
                
                // Check if expired
                if (invite.expiresAt < System.currentTimeMillis()) {
                    Log.e(TAG, "Invite expired: ${invite.referralCode}")
                    
                    // Mark as expired in database
                    database.inviteDao().updateInvite(
                        invite.copy(status = InviteEntity.STATUS_EXPIRED)
                    )
                    
                    clearPendingReferral()
                    return@withContext ReferralResult.Expired
                }
                
                // Check if already accepted
                if (invite.status == InviteEntity.STATUS_ACCEPTED) {
                    Log.e(TAG, "Invite already accepted: ${invite.referralCode}")
                    clearPendingReferral()
                    return@withContext ReferralResult.AlreadyUsed
                }
                
                // Validate email if invite was sent to specific email
                if (invite.inviteeEmail.isNotEmpty() && 
                    invite.inviteeEmail != userEmail) {
                    Log.e(TAG, "Email mismatch: ${invite.inviteeEmail} != $userEmail")
                    clearPendingReferral()
                    return@withContext ReferralResult.EmailMismatch(invite.inviteeEmail)
                }
                
                // Check if ROSCA exists
                val rosca = database.roscaDao().getRoscaById(invite.roscaId)
                if (rosca == null) {
                    Log.e(TAG, "ROSCA not found: ${invite.roscaId}")
                    clearPendingReferral()
                    return@withContext ReferralResult.RoscaNotFound
                }
                
                val existingMembers = database.memberDao().getMembersByGroupSync(invite.roscaId)
                val activeCount = existingMembers.count { it.isActive }
                
                // Check if ROSCA is full
                if (activeCount >= rosca.totalMembers) {
                    Log.e(TAG, "ROSCA is full: ${rosca.name}")
                    clearPendingReferral()
                    return@withContext ReferralResult.RoscaFull
                }
                
                // Check if user is already a member
                val existingMembership = existingMembers.find { 
                    it.userId == userId || it.walletAddress == userId 
                }
                
                if (existingMembership != null) {
                    Log.w(TAG, "User already a member of this ROSCA")
                    // Still mark invite as accepted
                    database.inviteDao().updateInvite(
                        invite.copy(
                            status = InviteEntity.STATUS_ACCEPTED,
                            acceptedAt = System.currentTimeMillis(),
                            acceptedByUserId = userId
                        )
                    )
                    markReferralProcessed()
                    clearPendingReferral()
                    return@withContext ReferralResult.AlreadyMember(rosca.name)
                }
                
                // ✅ FIX: Call RoscaManager.joinRosca() to properly create wallet and multisig info
                Log.d(TAG, "Calling RoscaManager.joinRosca() for user: $userId")
                
                val joinResult = roscaManager.joinRosca(
                    roscaId = invite.roscaId,
                    setupInfo = "", // Empty for referral flow
                    context = context
                )
                
                if (joinResult.isFailure) {
                    val error = joinResult.exceptionOrNull()?.message ?: "Failed to join ROSCA"
                    Log.e(TAG, "Failed to join ROSCA: $error", joinResult.exceptionOrNull())
                    clearPendingReferral()
                    return@withContext ReferralResult.Error(error)
                }
                
                val member = joinResult.getOrThrow()
                Log.d(TAG, "✅ Successfully joined ROSCA via RoscaManager")
                Log.d(TAG, "  Member ID: ${member.id}")
                Log.d(TAG, "  User ID: ${member.userId}")
                Log.d(TAG, "  Wallet: ${member.walletAddress.take(20)}...")
                Log.d(TAG, "  Has MultisigInfo: ${member.multisigInfo != null}")
                Log.d(TAG, "  Exchange State: ${member.multisigInfo?.exchangeState?.take(50) ?: "NULL"}")
                
                // ✅ Mark invite as accepted
                database.inviteDao().updateInvite(
                    invite.copy(
                        status = InviteEntity.STATUS_ACCEPTED,
                        acceptedAt = System.currentTimeMillis(),
                        acceptedByUserId = userId
                    )
                )
                
                // ✅ Mark as processed
                markReferralProcessed()
                clearPendingReferral()
                
                Log.d(TAG, "✓ Successfully added user to ROSCA: ${rosca.name}")
                
                // ✅ Check if ROSCA can be finalized (happens automatically in joinRosca)
                val updatedRosca = database.roscaDao().getRoscaById(invite.roscaId)
                if (updatedRosca != null && 
                    updatedRosca.currentMembers >= updatedRosca.totalMembers) {
                    Log.d(TAG, "🎯 ROSCA is full! Auto-finalization should have been triggered")
                }
                
                return@withContext ReferralResult.Success(rosca.name, rosca.id)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error processing referral", e)
                clearPendingReferral()
                return@withContext ReferralResult.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Save referral code for processing after login
     */
    fun savePendingReferral(referralCode: String, roscaId: String) {
        prefs.edit().apply {
            putString(PREF_REFERRAL_CODE, referralCode)
            putString(PREF_ROSCA_ID, roscaId)
            apply()
        }
    }
    
    /**
     * Clear pending referral
     */
    fun clearPendingReferral() {
        prefs.edit().apply {
            remove(PREF_REFERRAL_CODE)
            remove(PREF_ROSCA_ID)
            apply()
        }
        Log.d(TAG, "Cleared pending referral")
    }
    
    /**
     * Mark that referral has been processed for this user
     */
    private fun markReferralProcessed() {
        prefs.edit().putBoolean(PREF_REFERRAL_PROCESSED, true).apply()
    }
    
    /**
     * Check if there's a pending referral waiting to be processed
     */
    fun hasPendingReferral(): Boolean {
        val referralCode = prefs.getString(PREF_REFERRAL_CODE, null)
        val roscaId = prefs.getString(PREF_ROSCA_ID, null)
        return !referralCode.isNullOrEmpty() && !roscaId.isNullOrEmpty()
    }
    
    /**
     * Get pending referral info
     */
    fun getPendingReferralInfo(): Pair<String, String>? {
        val referralCode = prefs.getString(PREF_REFERRAL_CODE, null)
        val roscaId = prefs.getString(PREF_ROSCA_ID, null)
        
        return if (!referralCode.isNullOrEmpty() && !roscaId.isNullOrEmpty()) {
            Pair(referralCode, roscaId)
        } else {
            null
        }
    }
    
    /**
     * Reset referral processing state (for testing or re-login)
     */
    fun resetReferralState() {
        prefs.edit().apply {
            remove(PREF_REFERRAL_PROCESSED)
            remove(PREF_REFERRAL_CODE)
            remove(PREF_ROSCA_ID)
            apply()
        }
    }
}

/**
 * Result of referral processing
 */
sealed class ReferralResult {
    data class Success(val roscaName: String, val roscaId: String) : ReferralResult()
    data class AlreadyMember(val roscaName: String) : ReferralResult()
    data class EmailMismatch(val expectedEmail: String) : ReferralResult()
    data class Error(val message: String) : ReferralResult()
    object NoReferral : ReferralResult()
    object InvalidCode : ReferralResult()
    object Expired : ReferralResult()
    object AlreadyUsed : ReferralResult()
    object AlreadyProcessed : ReferralResult()
    object RoscaNotFound : ReferralResult()
    object RoscaFull : ReferralResult()
}
