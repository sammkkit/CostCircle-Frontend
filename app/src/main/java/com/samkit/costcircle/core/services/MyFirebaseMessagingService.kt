package com.samkit.costcircle.core.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.samkit.costcircle.R
import com.samkit.costcircle.app.MainActivity
import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.data.group.remote.GroupApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val sessionManager: SessionManager by inject()
    private val api: GroupApiService by inject()

    // Safe lifecycle-aware scope for service work
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New FCM Token: $token")

        // Send token immediately if user is already logged in
        if (sessionManager.getToken() != null) {
            sendTokenToBackend(token)
        }
    }

    private fun sendTokenToBackend(token: String) {
        serviceScope.launch {
            try {
                api.updateFcmToken(
                    mapOf("token" to token)
                )
                Log.d("FCM", "FCM token sent to backend successfully")
            } catch (e: Exception) {
                Log.e("FCM", "Failed to send FCM token", e)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "RAW MESSAGE RECEIVED from Server!")
        // Handle BOTH notification & data payloads
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
        Log.d("FCM", "${title},${body}")

        if (!title.isNullOrEmpty() && !body.isNullOrEmpty()) {
            showNotification(title, body, remoteMessage.data)
        }
    }

    private fun showNotification(
        title: String,
        body: String,
        data: Map<String, String>
    ) {
        val channelId = "cost_circle_notifications_v2"
        val notificationId = System.currentTimeMillis().toInt()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            // Example deep-link support (future-ready)
            data["groupId"]?.let {
                putExtra("groupId", it)
            }
        }
        Log.d("FCM","in notificatino fucntion")
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.cost_circle_logo) // WHITE vector icon only
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android O+ notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "CostCircle Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for group activity and updates"
            }
            manager.createNotificationChannel(channel)
        }

        manager.notify(notificationId, builder.build())
    }
}
