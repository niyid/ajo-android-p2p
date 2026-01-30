package com.techducat.ajo.ui.pin

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.techducat.ajo.databinding.FragmentPinVerificationBinding
import com.techducat.ajo.util.Logger
import com.techducat.ajo.util.SecureStorage
import org.koin.android.ext.android.inject
import java.util.concurrent.Executor

import com.techducat.ajo.R

class PinVerificationFragment : Fragment() {
    
    companion object {
        private const val TAG = "com.techducat.ajo.ui.pin.PinVerificationFragment"
        private const val PIN_LENGTH = 6
    }
    
    private var _binding: FragmentPinVerificationBinding? = null
    private val binding get() = _binding!!
    
    private val secureStorage: SecureStorage by inject()
    
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    
    private var lockoutTimer: CountDownTimer? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPinVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        checkLockoutStatus()
        setupBiometric()
        setupPinInput()
        setupButtons()
    }
    
    private fun checkLockoutStatus() {
        if (secureStorage.isPinLockedOut()) {
            showLockoutState()
        } else {
            showPinInputState()
        }
    }
    
    private fun showLockoutState() {
        binding.pinInput.isEnabled = false
        binding.btnClear.isEnabled = false
        
        val remainingTime = secureStorage.getRemainingLockoutTime()
        startLockoutTimer(remainingTime)
    }
    
    private fun showPinInputState() {
        binding.pinInput.isEnabled = true
        binding.btnClear.isEnabled = true
        binding.tvLockoutMessage.visibility = View.GONE
        
        val attempts = secureStorage.getPinAttempts()
        if (attempts > 0) {
            val remainingAttempts = 3 - attempts // Assuming max 3 attempts
            binding.tvSubtitle.text = getString(R.string.PinVerification_incorrect_pin_attempts_attempts, remainingAttempts) // Fixed: use remainingAttempts instead
            binding.tvSubtitle.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
            )
        } else {
            binding.tvSubtitle.text = getString(R.string.PinVerification_enter_your_pin_unlock)
            binding.tvSubtitle.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
            )
        }
    }
    
    private fun startLockoutTimer(durationMs: Long) {
        lockoutTimer?.cancel()
        
        binding.tvLockoutMessage.visibility = View.VISIBLE
        
        lockoutTimer = object : CountDownTimer(durationMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                binding.tvLockoutMessage.text = getString(R.string.PinVerification_account_locked_try_again, minutes, seconds)
            }
            
            override fun onFinish() {
                binding.tvLockoutMessage.visibility = View.GONE
                showPinInputState()
            }
        }.start()
    }
    
    private fun setupBiometric() {
        val prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val biometricEnabled = prefs.getBoolean("biometric_enabled", false)
        
        if (!biometricEnabled) {
            binding.btnBiometric.visibility = View.GONE
            return
        }
        
        val biometricManager = BiometricManager.from(requireContext())
        
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                binding.btnBiometric.visibility = View.VISIBLE
                setupBiometricPrompt()
            }
            else -> {
                binding.btnBiometric.visibility = View.GONE
            }
        }
    }
    
    private fun setupBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(requireContext())
        
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        requireContext(),
                        "Authentication error: $errString",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onPinVerified()
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        requireContext(),
                        "Authentication failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_auth))
            .setSubtitle("Unlock with your fingerprint or face")
            .setNegativeButtonText("Use PIN")
            .build()
    }
    
    private fun setupPinInput() {
        binding.pinInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatePinDots(s?.length ?: 0)
            }
            
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == PIN_LENGTH) {
                    verifyPin(s.toString())
                }
            }
        })
        
        // Auto-focus the input
        binding.pinInput.requestFocus()
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
    
    private fun setupButtons() {
        binding.btnClear.setOnClickListener {
            binding.pinInput.text?.clear()
        }
        
        binding.btnBiometric.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
        
        binding.btnForgotPin.setOnClickListener {
            showForgotPinDialog()
        }
    }
    
    private fun verifyPin(pin: String) {
        if (secureStorage.verifyPin(pin)) {
            onPinVerified()
        } else {
            // Wrong PIN
            binding.pinInput.text?.clear()
            
            if (secureStorage.isPinLockedOut()) {
                showLockoutState()
            } else {
                showPinInputState()
            }
        }
    }
    
    private fun onPinVerified() {
        Toast.makeText(requireContext(), getString(R.string.PinVerification_welcome_back), Toast.LENGTH_SHORT).show()
        
        // Navigate back or to main screen
        findNavController().navigateUp()
    }
    
    private fun showForgotPinDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.PinVerification_forgot_pin))
            .setMessage(getString(R.string.PinVerification_reset_your_pin_you))
            .setPositiveButton(getString(R.string.logout)) { _, _ ->
                // Navigate to logout or login screen
                Toast.makeText(requireContext(), getString(R.string.PinVerification_logout_functionality_needed_here), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        
        // Show biometric prompt automatically if enabled
        if (binding.btnBiometric.visibility == View.VISIBLE) {
            binding.root.postDelayed({
                try {
                    biometricPrompt.authenticate(promptInfo)
                } catch (e: Exception) {
                    Logger.e("$TAG: Error showing biometric prompt", e)
                }
            }, 300)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        lockoutTimer?.cancel()
        _binding = null
    }
}
