package com.kynv1.aiinsectidentifierpro.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kynv1.aiinsectidentifierpro.MainActivity
import com.kynv1.aiinsectidentifierpro.R
import timber.log.Timber
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("FCM Registration Token refreshed: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.d("Message received from: ${remoteMessage.from}")

        val (title, body) = extractContent(remoteMessage)
        val imageUrl = remoteMessage.notification?.imageUrl?.toString()
            ?: remoteMessage.data[KEY_IMAGE]

        showNotification(title, body, imageUrl)
    }

    private fun extractContent(remoteMessage: RemoteMessage): Pair<String, String> {
        val title = remoteMessage.notification?.title 
            ?: remoteMessage.data[KEY_TITLE] 
            ?: DEFAULT_TITLE
        val body = remoteMessage.notification?.body 
            ?: remoteMessage.data[KEY_BODY] 
            ?: DEFAULT_BODY
        return Pair(title, body)
    }

    private fun showNotification(title: String, messageBody: String, imageUrl: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureNotificationChannel(notificationManager)

        val pendingIntent = createLaunchPendingIntent()
        val appLargeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(appLargeIcon)
            .setColor(ContextCompat.getColor(this, R.color.notification_color))
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))

        // Nếu có ảnh minh họa đính kèm ➔ Tải và hiển thị dạng Banner lớn (BigPictureStyle)
        if (!imageUrl.isNullOrBlank()) {
            val bitmap = getBitmapFromUrl(imageUrl)
            if (bitmap != null) {
                notificationBuilder.setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null as Bitmap?)
                )
            }
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun getBitmapFromUrl(imageUrl: String?): Bitmap? {
        if (imageUrl.isNullOrBlank()) return null
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Timber.e(e, "Error downloading notification image: $imageUrl")
            null
        }
    }

    private fun ensureNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createLaunchPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private const val CHANNEL_ID = "ai_insect_high_priority_channel"
        private const val CHANNEL_NAME = "Insect Notifications"
        private const val CHANNEL_DESCRIPTION = "High priority channel for app notifications & tips"
        private const val KEY_TITLE = "title"
        private const val KEY_BODY = "body"
        private const val KEY_IMAGE = "image"
        private const val DEFAULT_TITLE = "AI Insect Identifier"
        private const val DEFAULT_BODY = "Check out new insect identification insights!"
    }
}
