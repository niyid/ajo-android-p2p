package com.techducat.ajo.ui.profile

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.techducat.ajo.databinding.FragmentNotificationSettingsBinding
import com.techducat.ajo.util.Logger

class NotificationSettingsFragment : Fragment() {
    
    companion object {
        private const val TAG = "com.techducat.ajo.ui.profile.NotificationSettingsFragment"
        
        // Notification channels
        const val CHANNEL_ROSCA_UPDATES = "rosca_updates"
        const val CHANNEL_PAYMENTS = "payments"
        const val CHANNEL_INVITATIONS = "invitations"
        const val CHANNEL_SECURITY = "security"
    }
    
    private var _binding: FragmentNotificationSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefs: android.content.SharedPreferences
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        prefs = requireContext().getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        
        setupViews()
        createNotificationChannels()
        loadSettings()
    }
    
    private fun setupViews() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.btnSystemSettings.setOnClickListener {
            openSystemNotificationSettings()
        }
        
        // ROSCA Updates
        binding.switchRoscaUpdates.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notify_rosca_updates", isChecked).apply()
            Logger.d("$TAG: ROSCA updates notifications: $isChecked")
        }
        
        // Payment Reminders
        binding.switchPaymentReminders.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notify_payment_reminders", isChecked).apply()
            Logger.d("$TAG: Payment reminders: $isChecked")
        }
        
        // Payout Notifications
        binding.switchPayoutNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notify_payouts", isChecked).apply()
            Logger.d("$TAG: Payout notifications: $isChecked")
        }
        
        // Invitations
        binding.switchInvitations.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notify_invitations", isChecked).apply()
            Logger.d("$TAG: Invitation notifications: $isChecked")
        }
        
        // Security Alerts
        binding.switchSecurityAlerts.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notify_security", isChecked).apply()
            Logger.d("$TAG: Security alerts: $isChecked")
        }
        
        // Sound
        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notification_sound", isChecked).apply()
        }
        
        // Vibration
        binding.switchVibration.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notification_vibration", isChecked).apply()
        }
    }
    
    private fun loadSettings() {
        binding.switchRoscaUpdates.isChecked = prefs.getBoolean("notify_rosca_updates", true)
        binding.switchPaymentReminders.isChecked = prefs.getBoolean("notify_payment_reminders", true)
        binding.switchPayoutNotifications.isChecked = prefs.getBoolean("notify_payouts", true)
        binding.switchInvitations.isChecked = prefs.getBoolean("notify_invitations", true)
        binding.switchSecurityAlerts.isChecked = prefs.getBoolean("notify_security", true)
        binding.switchSound.isChecked = prefs.getBoolean("notification_sound", true)
        binding.switchVibration.isChecked = prefs.getBoolean("notification_vibration", true)
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // ROSCA Updates Channel
            val roscaChannel = NotificationChannel(
                CHANNEL_ROSCA_UPDATES,
                "ROSCA Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about ROSCA cycle updates and member activities"
            }
            
            // Payment Channel
            val paymentChannel = NotificationChannel(
                CHANNEL_PAYMENTS,
                "Payment Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for upcoming ROSCA contributions"
            }
            
            // Invitations Channel
            val invitationChannel = NotificationChannel(
                CHANNEL_INVITATIONS,
                "Invitations",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "ROSCA invitation notifications"
            }
            
            // Security Channel
            val securityChannel = NotificationChannel(
                CHANNEL_SECURITY,
                "Security Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Important security notifications"
            }
            
            notificationManager.createNotificationChannels(
                listOf(roscaChannel, paymentChannel, invitationChannel, securityChannel)
            )
            
            Logger.d("$TAG: Notification channels created")
        }
    }
    
    private fun openSystemNotificationSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.parse("package:${requireContext().packageName}")
            }
        }
        startActivity(intent)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Extension function to check if notifications are enabled for a category
fun Context.isNotificationEnabled(category: String): Boolean {
    val prefs = getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
    return prefs.getBoolean(category, true)
}
