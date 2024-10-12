package ru.anydevprojects.simplepodcastapp.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.anydevprojects.simplepodcastapp.R
import ru.anydevprojects.simplepodcastapp.root.presentation.MainActivity

class FcmService : FirebaseMessagingService() {
    private val CHANNEL_ID = "example_channel_id"
    private val NOTIFICATION_ID = 1
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FcmService", message.data.toString())
        createNotificationChannel()
        val intent: Intent = Intent(applicationContext, MainActivity::class.java).apply {
            message.data["episodeId"]?.let {
                putExtra("episodeId", it)
            }
        }

        showNotification(
            title = message.data["title"].orEmpty(),
            body = message.data["body"].orEmpty(),
            intent = intent
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Example Channel"
            val descriptionText = "This is an example channel for notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Регистрируем канал уведомлений в системе
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, body: String, intent: Intent) {
        val contentIntent = PendingIntent.getActivity(
            applicationContext,
            1234,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )
        // Создаем уведомление
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon) // Иконка уведомления
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Показать уведомление
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }
}
