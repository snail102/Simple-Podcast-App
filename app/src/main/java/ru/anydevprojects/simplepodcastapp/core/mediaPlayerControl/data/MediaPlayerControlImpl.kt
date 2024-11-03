package ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.data

import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain.MediaData
import ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain.MediaPlayerControl
import ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain.PlayMediaData
import ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain.PlayState
import ru.anydevprojects.simplepodcastapp.media.AudioItemState
import ru.anydevprojects.simplepodcastapp.media.JetAudioServiceHandler
import ru.anydevprojects.simplepodcastapp.media.JetAudioState
import ru.anydevprojects.simplepodcastapp.media.PlayerEvent

class MediaPlayerControlImpl(
    private val jetAudioServiceHandler: JetAudioServiceHandler
) : MediaPlayerControl {

    private val _currentMedia: MutableStateFlow<MediaData> = MutableStateFlow(MediaData.Init)
    private val _playState: MutableStateFlow<PlayState> = MutableStateFlow(PlayState.Init)
    private val _progress: MutableStateFlow<Float> = MutableStateFlow(0.0f)
    private val _currentDuration: MutableStateFlow<Long> = MutableStateFlow(0)

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val currentMedia: StateFlow<MediaData>
        get() = _currentMedia.asStateFlow()

    override val progress: StateFlow<Float>
        get() = _progress.asStateFlow()

    override val currentDuration: StateFlow<Long>
        get() = _currentDuration.asStateFlow()

    override val currentMediaContent: MediaData.Content?
        get() {
            val mediaData = _currentMedia.value
            return if (mediaData is MediaData.Content) {
                mediaData
            } else {
                null
            }
        }

    override val playState: StateFlow<PlayState>
        get() = _playState.asStateFlow()

    override suspend fun moveTo(timeMs: Long) {
        jetAudioServiceHandler.onPlayerEvents(
            playerEvent = PlayerEvent.SeekTo,
            seekPosition = timeMs
        )
    }

    private var duration: Long = 0

    init {
        scope.launch {
            jetAudioServiceHandler.audioState.collectLatest { mediaState ->
                when (mediaState) {
                    JetAudioState.Initial -> {
                        Log.d("AudioService", "Initial")
                    }

                    is JetAudioState.Buffering -> {
                        Log.d("AudioService", "Buffering")
                    }

                    is JetAudioState.Playing -> {
                        val id = jetAudioServiceHandler.currentPlayingId?.toLongOrNull()
                            ?: throw Exception("id current media is null")

                        _playState.update {
                            if (mediaState.isPlaying) {
                                PlayState.Playing
                            } else {
                                PlayState.Pause
                            }
                        }
                    }

                    is JetAudioState.Progress -> {

                        _progress.update {
                            if (duration == 0L) {
                                0F
                            } else {
                                mediaState.progress.toFloat() / duration
                            }
                        }

                        _currentDuration.update {
                            mediaState.progress
                        }
                        Log.d("AudioService", "Progress ${mediaState.progress} ${_progress.value}")
                    }

//                    is JetAudioState.CurrentPlaying -> {
//                        Log.d("AudioService", "CurrentPlaying ${mediaState.mediaItemIndex}")
//                    }

                    is JetAudioState.Ready -> {
                        Log.d("AudioService", "Ready ${mediaState.duration}")
                    }
                }
            }
        }

        jetAudioServiceHandler.audioItemState.onEach { value: AudioItemState ->

            when (value) {
                is AudioItemState.Current -> {
                    _currentMedia.update {
                        MediaData.Content(
                            id = value.id.toLongOrNull()
                                ?: throw Exception("id current media not long"),
                            title = value.title,
                            imageUrl = value.imageUri?.toString().orEmpty(),
                            totalDurationMs = duration
                        )
                    }
                }

                AudioItemState.Empty -> {
                    _currentMedia.update {
                        MediaData.Init
                    }
                }
            }
        }.launchIn(scope)

//        jetAudioServiceHandler.isPlaybackQueue.onEach {
//            updateState(
//                lastContentState.copy(
//                    mediaPlayerContent = lastContentState.mediaPlayerContent.copy(
//                        enabledPlaybackQueue = it
//                    )
//                )
//            )
//        }.launchIn(scope)
    }

    override fun play(playMediaData: PlayMediaData) {
        scope.launch {
            val state = jetAudioServiceHandler.audioItemState.value
            duration = playMediaData.duration * 1000
            if (state is AudioItemState.Current && state.id == playMediaData.id.toString()) {
                jetAudioServiceHandler.onPlayerEvents(
                    PlayerEvent.PlayPause
                )
            } else {
                val metadata = MediaMetadata.Builder()
                    .setDisplayTitle(playMediaData.title)
                    .setArtworkUri(playMediaData.imageUrl.toUri())
                    // .setGenre(genres)
                    .build()

                val item = MediaItem.Builder()
                    .setUri(playMediaData.audioUrl.toUri())
                    .setMediaId(playMediaData.id.toString())
                    .setMediaMetadata(metadata)
                    .build()

                jetAudioServiceHandler.setPlayMediaItem(item)
            }
        }
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun changePlayStatus() {
        scope.launch {
            jetAudioServiceHandler.onPlayerEvents(
                PlayerEvent.PlayPause
            )
        }
    }

    override fun reset() {
        TODO("Not yet implemented")
    }
}
