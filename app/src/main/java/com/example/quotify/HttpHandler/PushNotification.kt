package com.example.quotify.HttpHandler

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.quotify.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val CHANNEL_ID = "Quotify_Notification"

class PushNotification : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("TOKEN", token)
        val tokenData = mapOf("token" to token)
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("DeviceToken").document().set(tokenData)
    }

    @SuppressLint("MissingPermission")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title
        val body = message.notification?.body

        val channel =
            NotificationChannel(CHANNEL_ID, "MyNotification", NotificationManager.IMPORTANCE_HIGH)

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        val notification = android.app.Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.icon_quotify)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(1, notification.build())
    }
}