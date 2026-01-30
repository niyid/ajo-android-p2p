package com.techducat.ajo.ui.pin

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.techducat.ajo.R
import com.techducat.ajo.databinding.FragmentPinSetupBinding
import com.techducat.ajo.util.Logger
import com.techducat.ajo.util.SecureStorage
import org.koin.android.ext.android.inject

class PinSetupFragment : Fragment() {
    
    companion object {
        private const val TAG = "com.techducat.ajo.ui.pin.PinSetupFragment"
        private const val PIN_LENGTH = 6
        private const val MAX_PIN_ATTEMPTS = 3
        private const val LOCKOUT_DURATION_MS = 30000L // 30 seconds
    }
    
    private var _binding: FragmentPinSetupBinding? = null
    private val binding get() = _binding!!
    
    private val secureStorage: SecureStorage by inject()
    
    private var currentStep = PinSetupStep.CREATE_PIN
    private var firstPin: String? = null
    
    private enum class PinSetupStep {
        CREATE_PIN,
        CONFIRM_PIN,
        COMPLETED
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPinSetupBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        checkExistingPin()
        setupViews()
        setupPinInput()
    }
    
    private fun checkExistingPin() {
        val hasExistingPin = secureStorage.hasPin()
        
        if (hasExistingPin) {
            // User already has a PIN - show change/remove options
            showPinManagementDialog()
        } else {
            // New PIN setup
            updateStepUI()
        }
    }
    
    private fun showPinManagementDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.PinSetup_pin_security))
            .setMessage(getString(R.string.PinSetup_you_already_have_pin))
            .setPositiveButton(getString(R.string.PinSetup_change_pin)) { _, _ ->
                // First verify current PIN, then allow change
                currentStep = PinSetupStep.CREATE_PIN
                updateStepUI()
                Toast.makeText(requireContext(), getString(R.string.PinSetup_enter_your_current_pin), Toast.LENGTH_SHORT).show()
                verifyCurrentPinFirst = true
            }
            .setNegativeButton(getString(R.string.PinSetup_remove_pin)) { _, _ ->
                confirmRemovePin()
            }
            .setNeutralButton(getString(R.string.PinSetup_back)) { _, _ ->
                findNavController().navigateUp()
            }
            .setCancelable(false)
            .show()
    }
    
    private var verifyCurrentPinFirst = false
    
    private fun confirmRemovePin() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.PinSetup_remove_pin_1))
            .setMessage(getString(R.string.PinSetup_are_you_sure_you))
            .setPositiveButton(getString(R.string.PinSetup_remove)) { _, _ ->
                secureStorage.removePin()
                Toast.makeText(requireContext(), getString(R.string.PinSetup_pin_removed), Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun setupViews() {
        binding.btnBack.setOnClickListener {
            when (currentStep) {
                PinSetupStep.CREATE_PIN -> findNavController().navigateUp()
                PinSetupStep.CONFIRM_PIN -> {
                    currentStep = PinSetupStep.CREATE_PIN
                    firstPin = null
                    updateStepUI()
                }
                PinSetupStep.COMPLETED -> findNavController().navigateUp()
            }
        }
        
        binding.btnClear.setOnClickListener {
            binding.pinInput.text?.clear()
        }
    }
    
    private fun setupPinInput() {
        binding.pinInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatePinDots(s?.length ?: 0)
            }
            
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == PIN_LENGTH) {
                    handlePinComplete(s.toString())
                }
            }
        })
    }
    
    private fun updatePinDots(filledCount: Int) {
        val dots = listOf(
            binding.pinDot1,
            binding.pinDot2,
            binding.pinDot3,
            binding.pinDot4,
            binding.pinDot5,
            binding.pinDot6
        )
        
        dots.forEachIndexed { index, view ->
            view.isActivated = index < filledCount
        }
    }
    
    private fun handlePinComplete(pin: String) {
        when {
            verifyCurrentPinFirst -> {
                verifyCurrentPin(pin)
            }
            currentStep == PinSetupStep.CREATE_PIN -> {
                handleCreatePin(pin)
            }
            currentStep == PinSetupStep.CONFIRM_PIN -> {
                handleConfirmPin(pin)
            }
        }
    }
    
    private fun verifyCurrentPin(pin: String) {
        if (secureStorage.verifyPin(pin)) {
            verifyCurrentPinFirst = false
            Toast.makeText(requireContext(), getString(R.string.PinSetup_current_pin_verified_enter), Toast.LENGTH_SHORT).show()
            binding.pinInput.text?.clear()
            currentStep = PinSetupStep.CREATE_PIN
            updateStepUI()
        } else {
            binding.pinInput.text?.clear()
            Toast.makeText(requireContext(), getString(R.string.PinSetup_incorrect_pin_try_again), Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun handleCreatePin(pin: String) {
        if (!isValidPin(pin)) {
            binding.pinInput.text?.clear()
            Toast.makeText(
                requireContext(),
                "PIN must be 6 digits and not all the same",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        
        firstPin = pin
        currentStep = PinSetupStep.CONFIRM_PIN
        updateStepUI()
        binding.pinInput.text?.clear()
    }
    
    private fun handleConfirmPin(pin: String) {
        if (pin == firstPin) {
            // PINs match - save it
            savePin(pin)
        } else {
            // PINs don't match - reset
            Toast.makeText(
                requireContext(),
                "PINs don't match. Please try again",
                Toast.LENGTH_LONG
            ).show()
            currentStep = PinSetupStep.CREATE_PIN
            firstPin = null
            updateStepUI()
            binding.pinInput.text?.clear()
        }
    }
    
    private fun savePin(pin: String) {
        try {
            secureStorage.savePin(pin)
            
            currentStep = PinSetupStep.COMPLETED
            updateStepUI()
            
            // Ask about biometric
            checkBiometricAvailability()
            
            // Navigate back after delay
            binding.root.postDelayed({
                findNavController().navigateUp()
            }, 2000)
            
        } catch (e: Exception) {
            Logger.e("$TAG: Error saving PIN", e)
            Toast.makeText(
                requireContext(),
                "Failed to save PIN: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private fun checkBiometricAvailability() {
        val biometricManager = BiometricManager.from(requireContext())
        
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Suggest enabling biometric
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.PinSetup_enable_biometric_authentication))
                    .setMessage(getString(R.string.PinSetup_you_can_use_fingerprint))
                    .setPositiveButton(getString(R.string.PinSetup_enable)) { _, _ ->
                        saveBiometricPreference(true)
                    }
                    .setNegativeButton(getString(R.string.PinSetup_not_now)) { _, _ ->
                        saveBiometricPreference(false)
                    }
                    .show()
            }
        }
    }
    
    private fun saveBiometricPreference(enabled: Boolean) {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("biometric_enabled", enabled).apply()
    }
    
    private fun isValidPin(pin: String): Boolean {
        // Check if all digits are the same
        if (pin.all { it == pin[0] }) {
            return false
        }
        
        // Check if it's a simple sequence
        val sequences = listOf("012345", "123456", "234567", "345678", "456789", "987654", "876543", "765432", "654321", "543210")
        if (sequences.contains(pin)) {
            return false
        }
        
        return true
    }
    
    private fun updateStepUI() {
        when (currentStep) {
            PinSetupStep.CREATE_PIN -> {
                binding.tvTitle.text = if (verifyCurrentPinFirst) getString(R.string.PinSetup_enter_current_pin) else getString(R.string.PinSetup_create_security_pin)
                binding.tvSubtitle.text = getString(R.string.PinSetup_enter_digit_pin_secure)
                binding.btnClear.visibility = View.VISIBLE
            }
            PinSetupStep.CONFIRM_PIN -> {
                binding.tvTitle.text = getString(R.string.PinSetup_confirm_pin)
                binding.tvSubtitle.text = getString(R.string.PinSetup_enter_your_pin_confirm)
                binding.btnClear.visibility = View.VISIBLE
            }
            PinSetupStep.COMPLETED -> {
                binding.tvTitle.text = getString(R.string.PinSetup_pin_set_successfully)
                binding.tvSubtitle.text = getString(R.string.PinSetup_your_account_now_more)
                binding.pinInput.isEnabled = false
                binding.btnClear.visibility = View.GONE
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
