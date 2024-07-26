package ru.anydevprojects.simplepodcastapp.media

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import ru.anydevprojects.simplepodcastapp.R
import ru.anydevprojects.simplepodcastapp.root.presentation.MainActivity

@UnstableApi
class PlaybackService : MediaSessionService(), KoinComponent {

    val player: ExoPlayer by inject()

    private var _mediaSession: MediaSession? = null
    private val mediaSession get() = _mediaSession!!

    private var controller: MediaController? = null

    override fun onCreate() {
        super.onCreate() // Call the superclass method

        // Create an ExoPlayer instance
        // val player = ExoPlayer.Builder(this).build()

        // Create a MediaSession instance
        _mediaSession = MediaSession.Builder(this, player)
            .also { builder ->
                // Set the session activity to the PendingIntent returned by getSingleTopActivity() if it's not null
                getSingleTopActivity()?.let { builder.setSessionActivity(it) }
            }
            .build() // Build the MediaSession instance

        // Set the listener for the MediaSessionService
        setListener(MediaSessionServiceListener())
        MediaController.Builder(
            this,
            SessionToken(this, ComponentName(this, PlaybackService::class.java))
        ).buildAsync().run {
            addListener(
                { controller = this.let { if (it.isDone) it.get() else null } },
                MoreExecutors.directExecutor()
            )
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Get the player from the media session
        val player = mediaSession.player

        // Check if the player is not ready to play or there are no items in the media queue
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            // Stop the service
            stopSelf()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return _mediaSession
    }

    override fun onDestroy() {
        // If _mediaSession is not null, run the following block
        _mediaSession?.run {
            // If getBackStackedActivity() returns a non-null value, set it as the session activity
            getBackStackedActivity()?.let { setSessionActivity(it) }
            // Release the player
            player.release()
            // Release the MediaSession instance
            release()
            // Set _mediaSession to null
            _mediaSession = null
        }
        // Clear the listener
        clearListener()
        // Call the superclass method
        super.onDestroy()
    }

    private fun getSingleTopActivity(): PendingIntent? {
        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getBackStackedActivity(): PendingIntent? {
        return TaskStackBuilder.create(this).run {
            addNextIntent(Intent(this@PlaybackService, MainActivity::class.java))
            getPendingIntent(0, immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    @OptIn(UnstableApi::class) // MediaSessionService.Listener
    private inner class MediaSessionServiceListener : Listener {

        override fun onForegroundServiceStartNotAllowedException() {
            if (
                Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                // Notification permission is required but not granted
                return
            }
            val notificationManagerCompat = NotificationManagerCompat.from(this@PlaybackService)
            ensureNotificationChannel(notificationManagerCompat)
            val builder =
                NotificationCompat.Builder(this@PlaybackService, CHANNEL_ID)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("getString(R.string.notification_content_title)")
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText("getString(R.string.notification_content_text)")
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .also { builder ->
                        getBackStackedActivity()?.let {
                            builder.setContentIntent(
                                it
                            )
                        }
                    }
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun ensureNotificationChannel(notificationManagerCompat: NotificationManagerCompat) {
        if (
            Build.VERSION.SDK_INT < 26 ||
            notificationManagerCompat.getNotificationChannel(CHANNEL_ID) != null
        ) {
            return
        }

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        notificationManagerCompat.createNotificationChannel(channel)
    }

    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "session_notification_channel_id"
        private val immutableFlag = PendingIntent.FLAG_IMMUTABLE
    }
}
