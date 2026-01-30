package com.techducat.ajo.ui.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.techducat.ajo.databinding.FragmentBackupRestoreBinding
import com.techducat.ajo.util.Logger
import com.techducat.ajo.wallet.WalletSuite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

import com.techducat.ajo.R

class BackupRestoreFragment : Fragment() {
    
    companion object {
        private const val TAG = "com.techducat.ajo.ui.profile.BackupRestoreFragment"
    }
    
    private var _binding: FragmentBackupRestoreBinding? = null
    private val binding get() = _binding!!
    
    private val walletSuite: WalletSuite by inject()
    
    private var seedPhrase: String? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBackupRestoreBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        checkBackupStatus()
    }
    
    private fun setupViews() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.btnViewSeed.setOnClickListener {
            showSeedPhraseWarning()
        }
        
        binding.btnCopySeed.setOnClickListener {
            copySeedToClipboard()
        }
        
        binding.btnBackupComplete.setOnClickListener {
            markBackupAsComplete()
        }
        
        binding.btnRestoreWallet.setOnClickListener {
            showRestoreDialog()
        }
    }
    
    private fun checkBackupStatus() {
        val prefs = requireContext().getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)
        val hasBackedUp = prefs.getBoolean("wallet_backed_up", false)
        val backupDate = prefs.getLong("backup_date", 0L)
        
        if (hasBackedUp && backupDate > 0) {
            binding.tvBackupStatus.text = getString(R.string.BackupRestore_backed)
            binding.tvBackupStatus.setTextColor(
                resources.getColor(android.R.color.holo_green_dark, null)
            )
            binding.tvBackupDate.visibility = View.VISIBLE
            binding.tvBackupDate.text = getString(R.string.BackupRestore_last_backup_formatdate_backupdate, binding.tvBackupDate.visibility)
            binding.btnBackupComplete.text = getString(R.string.BackupRestore_mark_backed_again)
        } else {
            binding.tvBackupStatus.text = getString(R.string.BackupRestore_not_backed)
            binding.tvBackupStatus.setTextColor(
                resources.getColor(android.R.color.holo_orange_dark, null)
            )
            binding.tvBackupDate.visibility = View.GONE
            binding.btnBackupComplete.text = getString(R.string.BackupRestore_mark_backed)
        }
    }
    
    private fun showSeedPhraseWarning() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.BackupRestore_security_warning))
            .setMessage(
                "Your seed phrase is the ONLY way to recover your wallet.\n\n" +
                "• Never share it with anyone\n" +
                "• Never enter it on websites\n" +
                "• Store it in a safe place offline\n" +
                "• Anyone with your seed can access your funds\n\n" +
                "Are you in a private location?"
            )
            .setPositiveButton(getString(R.string.BackupRestore_yes_show_seed)) { _, _ ->
                revealSeedPhrase()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun revealSeedPhrase() {
        binding.progressBar.visibility = View.VISIBLE
        
        walletSuite.getSeedPhrase(object : WalletSuite.SeedPhraseCallback {
            override fun onSuccess(seed: String) {
                seedPhrase = seed
                binding.progressBar.visibility = View.GONE
                binding.seedContainer.visibility = View.VISIBLE
                binding.tvSeedPhrase.text = seed
                binding.btnCopySeed.isEnabled = true
                binding.btnBackupComplete.isEnabled = true
                Logger.d("$TAG: Seed phrase revealed")
            }
            
            override fun onError(error: String) {
                binding.progressBar.visibility = View.GONE
                showError("Failed to get seed phrase: $error")
                Logger.e("$TAG: $error")
            }
        })
    }
    
    private fun copySeedToClipboard() {
        if (seedPhrase.isNullOrEmpty()) {
            showError("No seed phrase available")
            return
        }
        
        try {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Wallet Seed", seedPhrase)
            clipboard.setPrimaryClip(clip)
            
            Toast.makeText(
                requireContext(),
                "⚠️ Seed copied! Clear clipboard after saving securely",
                Toast.LENGTH_LONG
            ).show()
            
            // Auto-clear clipboard after 2 minutes for security
            binding.root.postDelayed({
                try {
                    val emptyClip = ClipData.newPlainText("", "")
                    clipboard.setPrimaryClip(emptyClip)
                    Logger.d("$TAG: Clipboard cleared for security")
                } catch (e: Exception) {
                    Logger.e("$TAG: Error clearing clipboard", e)
                }
            }, 120000) // 2 minutes
            
        } catch (e: Exception) {
            Logger.e("$TAG: Error copying to clipboard", e)
            showError("Failed to copy seed")
        }
    }
    
    private fun markBackupAsComplete() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.BackupRestore_confirm_backup))
            .setMessage(
                "Have you safely written down or stored your seed phrase?\n\n" +
                "Without it, you CANNOT recover your wallet if you lose your phone."
            )
            .setPositiveButton(getString(R.string.BackupRestore_yes_backed)) { _, _ ->
                saveBackupStatus()
            }
            .setNegativeButton(getString(R.string.BackupRestore_not_yet), null)
            .show()
    }
    
    private fun saveBackupStatus() {
        val prefs = requireContext().getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean("wallet_backed_up", true)
            putLong("backup_date", System.currentTimeMillis())
            apply()
        }
        
        Toast.makeText(requireContext(), getString(R.string.BackupRestore_backup_status_saved), Toast.LENGTH_SHORT).show()
        checkBackupStatus()
    }
    
    private fun showRestoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.BackupRestore_restore_wallet))
            .setMessage(
                "Wallet restore functionality:\n\n" +
                "• This will replace your current wallet\n" +
                "• You'll need your 25-word seed phrase\n" +
                "• The process may take several minutes\n\n" +
                "For security, wallet restore should be done during initial setup.\n\n" +
                "To restore a wallet, please reinstall the app and choose 'Restore Wallet' on first launch."
            )
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun formatDate(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
    
    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    
    override fun onPause() {
        super.onPause()
        
        // Hide seed phrase when leaving screen for security
        binding.seedContainer.visibility = View.GONE
        binding.tvSeedPhrase.text = ""
        seedPhrase = null
        binding.btnCopySeed.isEnabled = false
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        
        // Clear seed phrase from memory
        seedPhrase = null
        
        _binding = null
    }
}
