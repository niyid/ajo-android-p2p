package com.techducat.ajo.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.techducat.ajo.model.Member
import com.techducat.ajo.model.Rosca
import com.techducat.ajo.model.Round
import com.techducat.ajo.ui.MainActivity

class NotificationService : Service() {
    
    companion object {
        private const val CHANNEL_ID = "ajo_notifications"
        private const val CHANNEL_NAME = "Àjọ Notifications"
    }
    
    private lateinit var notificationManager: NotificationManager
    
    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for ROSCA activities"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun notifyRoscaActivated(rosca: Rosca) {
        showNotification(
            "ROSCA Activated",
            "${rosca.name} is now active. First round has started!"
        )
    }
    
    fun notifyRoundStarted(rosca: Rosca, round: Round) {
        showNotification(
            "New Round Started",
            "Round ${round.roundNumber} of ${rosca.name} has started. Please contribute!"
        )
    }
    
    fun notifyRecipientSelected(rosca: Rosca, round: Round, recipientId: String) {
        val members = rosca.members
        val recipient = members.find { it.userId == recipientId }
        showNotification(
            "Recipient Selected",
            "${recipient?.displayName ?: "Member"} has been selected for round ${round.roundNumber}"
        )
    }
    
    fun notifyRoundCompleted(rosca: Rosca, round: Round) {
        showNotification(
            "Round Completed",
            "Round ${round.roundNumber} of ${rosca.name} has been completed!"
        )
    }
    
    fun notifyRoscaCompleted(rosca: Rosca) {
        showNotification(
            "ROSCA Completed",
            "${rosca.name} has completed all rounds!"
        )
    }
    
    fun notifyRoscaPaused(rosca: Rosca) {
        showNotification(
            "ROSCA Paused",
            "${rosca.name} has been paused"
        )
    }
    
    fun notifyRoscaResumed(rosca: Rosca) {
        showNotification(
            "ROSCA Resumed",
            "${rosca.name} has been resumed"
        )
    }
    
    fun notifyMemberJoined(rosca: Rosca, member: Member) {
        showNotification(
            "New Member",
            "${member.displayName} has joined ${rosca.name}"
        )
    }
    
    private fun showNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Use default icon for now
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
