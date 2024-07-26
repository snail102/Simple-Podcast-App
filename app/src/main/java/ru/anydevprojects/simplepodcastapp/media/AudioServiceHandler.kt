package ru.anydevprojects.simplepodcastapp.media

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
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

@OptIn(UnstableApi::class)
class JetAudioServiceHandler(
    private val applicationContext: Context,
    private val exoPlayer: ExoPlayer
) : Player.Listener {
    private val _audioState: MutableStateFlow<JetAudioState> =
        MutableStateFlow(JetAudioState.Initial)
    val audioState: StateFlow<JetAudioState> = _audioState.asStateFlow()

    private var controller: MediaController? = null

    private var job: Job? = null

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

    suspend fun setPlayMediaItem(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        playOrPause()
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
                        _audioState.value = JetAudioState.Playing(
                            isPlaying = true,
                            id = exoPlayer.currentMediaItem?.mediaId.orEmpty(),
                            title = exoPlayer.mediaMetadata.title.toString(),
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

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _audioState.value = JetAudioState.Playing(
            isPlaying = isPlaying,
            id = exoPlayer.currentMediaItem?.mediaId.orEmpty(),
            title = exoPlayer.mediaMetadata.title.toString(),
            imageUri = exoPlayer.mediaMetadata.artworkUri
        )
        _audioState.value = JetAudioState.CurrentPlaying(
            exoPlayer.currentMediaItemIndex
        )
        if (isPlaying) {
            GlobalScope.launch(Dispatchers.Main) {
                startProgressUpdate()
            }
        } else {
            stopProgressUpdate()
        }
    }

    private suspend fun playOrPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            stopProgressUpdate()
        } else {
            exoPlayer.play()
            _audioState.value = JetAudioState.Playing(
                isPlaying = true,
                id = exoPlayer.currentMediaItem?.mediaId.orEmpty(),
                title = exoPlayer.mediaMetadata.title?.toString().orEmpty(),
                imageUri = exoPlayer.mediaMetadata.artworkUri
            )
            startProgressUpdate()
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
        _audioState.value = JetAudioState.Playing(
            isPlaying = false,
            id = exoPlayer.currentMediaItem?.mediaId.orEmpty(),
            title = exoPlayer.mediaMetadata.title?.toString().orEmpty(),
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
    data class Playing(
        val isPlaying: Boolean,
        val id: String,
        val title: String,
        val imageUri: Uri?
    ) : JetAudioState()

    data class CurrentPlaying(val mediaItemIndex: Int) : JetAudioState()
}
