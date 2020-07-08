package com.flixxo.apps.flixxoapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.view.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class NotificationService : FirebaseMessagingService() {
    private val CHANNEL_ID = "admin_channel"
    private val notificationManager: NotificationManager? = null
    private var uuid: String = ""
    private var contentType: String = ""


    override fun onNewToken(token: String?) {
        Timber.e("Refreshed token: $token")
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.e("data: ${remoteMessage.data.size}")
        Timber.e("from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body
        uuid = data["uuid"].toString()
        contentType = data["contentType"].toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels()
        }

        if (remoteMessage.notification != null) {
            showNotification(title, body)
        }
    }

    private fun showNotification(title: String?, body: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra("CONTENT_TYPE", contentType)
        intent.putExtra("UUID", uuid)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = "default_notification"
        val notificationID = 100
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationID, notificationBuilder.build())

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels() {
        val adminChannelName: CharSequence = "global channel"
        val adminChannelDescription = "notifications from the app admin"
        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager?.createNotificationChannel(adminChannel)
    }
}