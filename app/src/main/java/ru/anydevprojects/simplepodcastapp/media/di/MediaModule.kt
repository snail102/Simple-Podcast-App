package ru.anydevprojects.simplepodcastapp.media.di

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.media.JetAudioServiceHandler
import ru.anydevprojects.simplepodcastapp.root.presentation.MainActivity

@SuppressLint("UnsafeOptInUsageError")
val mediaModule = module {
    single {
        AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .setUsage(C.USAGE_MEDIA)
            .build()
    }
    single {
        ExoPlayer.Builder(get()).build()
    }
    single {
        MediaSession.Builder(get(), get())
            .also { builder ->
                // Set the session activity to the PendingIntent returned by getSingleTopActivity() if it's not null
                getSingleTopActivity(get())?.let { builder.setSessionActivity(it) }
            }
            .build() // Build the MediaSession instance
    }
    single { JetAudioServiceHandler(get(), get()) }
}

private fun getSingleTopActivity(context: Context): PendingIntent? = PendingIntent.getActivity(
    context,
    0,
    Intent(context, MainActivity::class.java),
    immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT
)

private val immutableFlag = PendingIntent.FLAG_IMMUTABLE
