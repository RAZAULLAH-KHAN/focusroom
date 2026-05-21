package com.focusroom.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.focusroom.app.core.network.WebSocketClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShameForegroundService : Service() {

    @Inject
    lateinit var webSocketClient: WebSocketClient

    private val binder = ShameBinder()
    private val notificationId = 8820
    private val channelId = "shame_engine_channel"

    inner class ShameBinder : Binder() {
        fun getService(): ShameForegroundService = this@ShameForegroundService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification("FocusRoom is keeping you locked in! ⚡")
        startForeground(notificationId, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    // Triggered when MainActivity informs the service that the user minimized the app
    fun onAppMinimized() {
        Log.d("ShameForegroundService", "App minimized! Triggering Shame Engine WebSocket alert.")
        
        // 1. Update status to Sleeping / Slacking via WebSocket
        webSocketClient.sendState("current_user_raza", "Raza", "sleeping")

        // 2. Change notification alert to Gen Z slang "Shame" callout
        val notification = createNotification("🚨 RAZA WENT TO SLEEP! Your buddies are focusing without you! 😴")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    // Triggered when user opens the app back up
    fun onAppRestored() {
        Log.d("ShameForegroundService", "App restored! Restoring Lock-in focus state.")
        
        // 1. Restore status to Focused via WebSocket
        webSocketClient.sendState("current_user_raza", "Raza", "focused")

        // 2. Update notification back to focusing state
        val notification = createNotification("Lock in mode active! ⚡ Work alongside your circle.")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    private fun createNotification(content: String): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("FocusRoom: Shame Engine")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES,O) {
            val channel = NotificationChannel(
                channelId,
                "FocusRoom Shame Tracking Services",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors focus room session states and tracks user distractions"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ShameForegroundService", "Service destroyed.")
    }
}
