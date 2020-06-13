package kv.kvchat.data.firebase

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kv.kvchat.ChatApplication
import kv.kvchat.ui.chat.ChatActivity

class FirebaseMessaging(val firebaseSource: FirebaseSource) : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val sent = p0.data["sent"]
        val currUser = ChatApplication.getUser().username

        if (sent.equals(currUser)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                sendOAndAboveNotification(p0)
            } else {
                sendNormalNotification(p0)
            }
        }
    }

    private fun sendNormalNotification(remoteMessage: RemoteMessage) {
        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]?: ""
        val body = remoteMessage.data["body"]
        val title = remoteMessage.data["title"]

        val notification = remoteMessage.notification

        var j = 0
        user?.let {
            j = Integer.parseInt(user.replace("[\\D]", ""))
        }

        val intent = Intent(this, ChatActivity::class.java)

        val bundle = Bundle()
        bundle.putString("username", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this)
            .setSmallIcon(Integer.parseInt(icon))
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var i = 0
        if (j > 0) {
            i = j
        }

        notificationManager.notify(i, builder.build())
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        if (firebaseSource.currentUser() != null) {
            firebaseSource.updateToken(p0)
        }
    }
}
