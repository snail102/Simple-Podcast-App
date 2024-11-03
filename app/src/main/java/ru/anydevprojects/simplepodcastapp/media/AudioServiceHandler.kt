package ru.anydevprojects.simplepodcastapp.media

import android.content.ComponentName
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.anydevprojects.simplepodcastapp.utils.currentMediaItems

@OptIn(UnstableApi::class)
class JetAudioServiceHandler(
    private val applicationContext: Context,
    private val exoPlayer: ExoPlayer
) : Player.Listener {
    private val _audioState: MutableStateFlow<JetAudioState> =
        MutableStateFlow(JetAudioState.Initial)
    val audioState: StateFlow<JetAudioState> = _audioState.asStateFlow()

    private val _audioItemState: MutableStateFlow<AudioItemState> =
        MutableStateFlow(AudioItemState.Empty)
    val audioItemState: StateFlow<AudioItemState> = _audioItemState.asStateFlow()

    private val _isPlaybackQueue: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isPlaybackQueue = _isPlaybackQueue.asStateFlow()

    private val _playbackQueueList: MutableStateFlow<List<MediaItem>> =
        MutableStateFlow(emptyList())
    val playbackQueueList = _playbackQueueList.asStateFlow()

    val currentPlayingId: String?
        get() {
            val currentItem = audioItemState.value
            return if (currentItem is AudioItemState.Current) {
                currentItem.id
            } else {
                null
            }
        }

    private var controller: MediaController? = null

    private var job: Job? = null

    private val audioFocusListener = AudioManager.OnAudioFocusChangeListener { focusChange ->

        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> {
                exoPlayer.play()
            }

            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                exoPlayer.pause()
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // audio focus loss, but maybe just set lower volume
            }
        }
    }

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    @RequiresApi(Build.VERSION_CODES.O)
    private val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
        .setAudioAttributes(audioAttributes)
        .setOnAudioFocusChangeListener(audioFocusListener)
        .build()

    private val audioManager = applicationContext.getSystemService(AudioManager::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    private val audioFocus = audioManager?.requestAudioFocus(audioFocusRequest)

    init {

        MediaController.Builder(
            applicationContext,
            SessionToken(
                applicationContext,
                ComponentName(applicationContext, PlaybackService::class.java)
            )
        ).buildAsync().run {
            addListener(
                { controller = this.let { if (it.isDone) it.get() else null } },
                MoreExecutors.directExecutor()
            )
        }

        exoPlayer.addListener(this)
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        Log.d("test", "updated media data ${mediaMetadata.displayTitle}")
    }

    suspend fun setPlayMediaItem(mediaItem: MediaItem) {
        withContext(Dispatchers.Main) {
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            playOrPause()
        }
    }

    fun addMediaItem(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    fun setMediaItemList(mediaItems: List<MediaItem>) {
        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.prepare()
    }

    suspend fun onPlayerEvents(
        playerEvent: PlayerEvent,
        selectedAudioIndex: Int = -1,
        seekPosition: Long = 0
    ) {
        when (playerEvent) {
            PlayerEvent.Backward -> exoPlayer.seekBack()
            PlayerEvent.Forward -> exoPlayer.seekForward()
            PlayerEvent.SeekToNext -> exoPlayer.seekToNext()
            PlayerEvent.PlayPause -> playOrPause()
            PlayerEvent.SeekTo -> exoPlayer.seekTo(seekPosition)
            PlayerEvent.SelectedAudioChange -> {
                when (selectedAudioIndex) {
                    exoPlayer.currentMediaItemIndex -> {
                        playOrPause()
                    }

                    else -> {
                        exoPlayer.seekToDefaultPosition(selectedAudioIndex)
                        _audioState.value = JetAudioState.Playing(isPlaying = true)
                        _audioItemState.value = AudioItemState.Current(
                            id = exoPlayer.currentMediaItem?.mediaId.orEmpty(),
                            title = exoPlayer.mediaMetadata.displayTitle.toString(),
                            imageUri = exoPlayer.mediaMetadata.artworkUri
                        )
                        exoPlayer.playWhenReady = true
                        startProgressUpdate()
                    }
                }
            }

            PlayerEvent.Stop -> stopProgressUpdate()
            is PlayerEvent.UpdateProgress -> {
                exoPlayer.seekTo(
                    (exoPlayer.duration * playerEvent.newProgress).toLong()
                )
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING ->
                _audioState.value =
                    JetAudioState.Buffering(exoPlayer.currentPosition)

            ExoPlayer.STATE_READY ->
                _audioState.value =
                    JetAudioState.Ready(exoPlayer.duration)
        }
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        super.onTimelineChanged(timeline, reason)
        _isPlaybackQueue.value = exoPlayer.getMediaItemCount() > 1
        _playbackQueueList.value = exoPlayer.currentMediaItems
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _audioState.value = JetAudioState.Playing(isPlaying = isPlaying)

        _audioItemState.value = AudioItemState.Current(
            id = exoPlayer.currentMediaItem?.mediaId.orEmpty(),
            title = exoPlayer.mediaMetadata.displayTitle.toString(),
            imageUri = exoPlayer.mediaMetadata.artworkUri
        )

//        _audioState.value = JetAudioState.CurrentPlaying(
//            exoPlayer.currentMediaItemIndex
//        )
        if (isPlaying) {
            GlobalScope.launch(Dispatchers.Main) {
                startProgressUpdate()
            }
        } else {
            stopProgressUpdate()
        }
    }

    private suspend fun playOrPause() {
        withContext(Dispatchers.Main) {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
                stopProgressUpdate()
            } else {
                exoPlayer.play()
                _audioState.value = JetAudioState.Playing(isPlaying = true)

                _audioItemState.value = AudioItemState.Current(
                    id = exoPlayer.currentMediaItem?.mediaId.orEmpty(),
                    title = exoPlayer.mediaMetadata.displayTitle.toString(),
                    imageUri = exoPlayer.mediaMetadata.artworkUri
                )

                startProgressUpdate()
            }
        }
    }

    private suspend fun startProgressUpdate() = job.run {
        while (true) {
            delay(500)
            _audioState.value = JetAudioState.Progress(exoPlayer.currentPosition)
        }
    }

    private fun stopProgressUpdate() {
        job?.cancel()
        _audioState.value = JetAudioState.Playing(isPlaying = false)

        _audioItemState.value = AudioItemState.Current(
            id = exoPlayer.currentMediaItem?.mediaId.orEmpty(),
            title = exoPlayer.mediaMetadata.displayTitle.toString(),
            imageUri = exoPlayer.mediaMetadata.artworkUri
        )
    }
}

sealed class PlayerEvent {
    object PlayPause : PlayerEvent()
    object SelectedAudioChange : PlayerEvent()
    object Backward : PlayerEvent()
    object SeekToNext : PlayerEvent()
    object Forward : PlayerEvent()
    object SeekTo : PlayerEvent()
    object Stop : PlayerEvent()
    data class UpdateProgress(val newProgress: Float) : PlayerEvent()
}

sealed class JetAudioState {
    object Initial : JetAudioState()
    data class Ready(val duration: Long) : JetAudioState()
    data class Progress(val progress: Long) : JetAudioState()
    data class Buffering(val progress: Long) : JetAudioState()
    data class Playing(val isPlaying: Boolean) : JetAudioState()

    // data class CurrentPlaying(val mediaItemIndex: Int) : JetAudioState()
}

sealed class AudioItemState {
    data object Empty : AudioItemState()

    data class Current(
        val id: String,
        val title: String,
        val imageUri: Uri?
    ) : AudioItemState()
}
