package kv.kvchat.data.notification

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build

class OreoAboveNotification(base: Context) : ContextWrapper(base) {
    companion object {
        val ID = "com.kv.kvchat"
        val NAME = "KVChat"
    }

    private var notificationManager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val notificationChannel =
            NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
    }

    fun getManager(): NotificationManager? {
        if (notificationManager == null) {
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationManager
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun getONotifications(
        title: String,
        body: String,
        pIntent: PendingIntent,
        soundUri: Uri,
        icon: String
    ): Notification.Builder {
        return Notification.Builder(applicationContext, ID)
            .setContentIntent(pIntent)
            .setContentTitle(title)
            .setContentText(body)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setSmallIcon(Integer.parseInt(icon))
    }
}