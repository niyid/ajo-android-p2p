package com.techducat.ajo.ui.leave

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.techducat.ajo.R
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.wallet.WalletSuite
import kotlinx.coroutines.launch
import com.techducat.ajo.data.local.entity.ContributionEntity

class LeaveRoscaDialog(
    private val roscaId: String,
    private val onSuccess: () -> Unit
) : DialogFragment() {
    
    private lateinit var database: AjoDatabase
    private lateinit var walletSuite: WalletSuite
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AjoDatabase.getInstance(requireContext())
        walletSuite = WalletSuite.getInstance(requireContext())
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val reasons = arrayOf(
            "Financial constraints",
            "Found better alternative",
            "Rosca issues",
            "Personal reasons",
            "Other"
        )
        
        var selectedReason = reasons[0]
        
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.LeaveRosca_leave_rosca))
            .setMessage(getString(R.string.LeaveRosca_warning) +
                "• You will forfeit your position\n" +
                "• Cannot rejoin this cycle\n" +
                "• Pending contributions must be completed\n\n" +
                "Are you sure you want to leave?")
            .setSingleChoiceItems(reasons, 0) { _, which ->
                selectedReason = reasons[which]
            }
            .setPositiveButton(getString(R.string.LeaveRosca_leave)) { _, _ ->
                performLeave(selectedReason)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
    }
    
    private fun performLeave(reason: String) {
        lifecycleScope.launch {
            try {
                // Validate can leave
                val userId = walletSuite.getUserId()
                val members = database.memberDao().getMembersByGroupSync(roscaId)
                val currentMember = members.find { it.userId == userId }
                
                if (currentMember == null) {
                    showError("Member not found")
                    return@launch
                }
                
                // Check pending contributions
                val contributions = database.contributionDao()
                    .getContributionsByRoscaSync(roscaId)
                val pendingContributions = contributions.filter { contribution ->
                    contribution.memberId == currentMember.id && contribution.status == "pending" 
                }
                
                if (pendingContributions.isNotEmpty()) {
                    showError("Cannot leave with ${pendingContributions.size} pending contribution(s)")
                    return@launch
                }
                
                // Check if current payout recipient
                val rosca = database.roscaDao().getRoscaById(roscaId)
                if (rosca != null && currentMember.position == rosca.currentRound) {
                    showError("Cannot leave as current payout recipient")
                    return@launch
                }
                
                // Update member (soft delete with audit trail)
                currentMember.isActive = false
                currentMember.leftAt = System.currentTimeMillis()
                currentMember.leftReason = reason
                
                database.memberDao().update(currentMember)
                
                // Notify success
                activity?.runOnUiThread {
                    onSuccess()
                }
                
            } catch (e: Exception) {
                showError("Failed to leave: ${e.message}")
            }
        }
    }
    
    private fun showError(message: String) {
        activity?.runOnUiThread {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.LeaveRosca_cannot_leave))
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
