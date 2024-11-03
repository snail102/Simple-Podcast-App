package ru.anydevprojects.simplepodcastapp.playControl.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain.MediaData
import ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain.MediaPlayerControl
import ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain.PlayState
import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel
import ru.anydevprojects.simplepodcastapp.playControl.presentation.models.DurationTimeFormat
import ru.anydevprojects.simplepodcastapp.playControl.presentation.models.DurationTimeFormat.*
import ru.anydevprojects.simplepodcastapp.playControl.presentation.models.PlayControlEvent
import ru.anydevprojects.simplepodcastapp.playControl.presentation.models.PlayControlIntent
import ru.anydevprojects.simplepodcastapp.playControl.presentation.models.PlayControlState
import ru.anydevprojects.simplepodcastapp.playControl.presentation.models.TimePosition
import ru.anydevprojects.simplepodcastapp.podcastFeed.domain.PodcastFeedRepository

class PlayControlViewModel(
    private val mediaPlayerControl: MediaPlayerControl,
    private val podcastFeedRepository: PodcastFeedRepository
) :
    BaseViewModel<PlayControlState.Content, PlayControlState.Content, PlayControlIntent, PlayControlEvent>(
        initialStateAndDefaultContentState = {
            PlayControlState.Content() to PlayControlState.Content()
        }
    ) {

    private val _timePosition: MutableStateFlow<TimePosition> = MutableStateFlow(TimePosition())
    val timePosition = _timePosition.asStateFlow()

    private var durationTimeFormat: DurationTimeFormat = HOUR

    private var isProcessingChangeProgress: Boolean = false
    private var timeBeforeStartMoveTrack: Long = 0

    init {
        viewModelScope.launch {
            mediaPlayerControl.currentMedia.combine(
                mediaPlayerControl.playState
            ) { mediaData: MediaData, playState: PlayState ->
                Log.d("playState", playState.toString())
                val content = when (mediaData) {
                    is MediaData.Content -> mediaData
                    MediaData.Init -> null
                    MediaData.Loading -> null
                }

                if (content != null) {
                    val isPlaying = when (playState) {
                        PlayState.Init -> false
                        PlayState.Loading -> false
                        PlayState.Pause -> false
                        PlayState.Playing -> true
                    }
                    durationTimeFormat = maxDurationTimeFormat(content.totalDurationMs)
                    updateState(
                        lastContentState.copy(
                            episodeName = content.title,
                            imageUrl = content.imageUrl,
                            totalDuration = formatMillisToDynamicTime(
                                millis = content.totalDurationMs,
                                durationTimeFormat = durationTimeFormat
                            ),
                            isPlaying = isPlaying
                        )
                    )
                }
            }.collect()
        }

        viewModelScope.launch {
            mediaPlayerControl.progress.combine(
                mediaPlayerControl.currentDuration
            ) { progress, currentDuration ->
                Log.d("progressPlaying", progress.toString())
                _timePosition.update {
                    it.copy(
                        currentTimeMs = currentDuration,
                        currentTime = formatMillisToDynamicTime(
                            currentDuration,
                            durationTimeFormat
                        ),
                        trackPosition = if (!isProcessingChangeProgress) {
                            progress
                        } else {
                            it.trackPosition
                        }
                    )
                }
            }.collect()
        }

        viewModelScope.launch {
            mediaPlayerControl.currentMedia.filterIsInstance<MediaData.Content>()
                .map { it.id }
                .distinctUntilChanged()
                .collect { id ->
                    val podcastName = podcastFeedRepository.getPodcastNameByEpisodeIdFromLocal(
                        id = id
                    )
                    updateState(
                        lastContentState.copy(
                            podcastName = podcastName
                        )
                    )
                }
        }
    }

    override fun onIntent(intent: PlayControlIntent) {
        when (intent) {
            is PlayControlIntent.OnChangeCurrentPlayPosition -> {
                movePlay(intent.tackPosition)
            }

            PlayControlIntent.OnChangePlayState -> {
                changePlayState()
            }

            PlayControlIntent.OnChangeFinishedCurrentPositionMedia -> {
                onFinishChangeTrackPosition()
            }
        }
    }

    private fun movePlay(positionTrack: Float) {
        isProcessingChangeProgress = true
        val totalDuration = mediaPlayerControl.currentMediaContent?.totalDurationMs ?: 0
        timeBeforeStartMoveTrack = (totalDuration * positionTrack).toLong()

        _timePosition.update {
            it.copy(
                trackPosition = positionTrack
            )
        }
    }

    private fun onFinishChangeTrackPosition() {
        viewModelScope.launch {
            mediaPlayerControl.moveTo(timeBeforeStartMoveTrack)
            isProcessingChangeProgress = false
        }
    }

    private fun changePlayState() {
        mediaPlayerControl.changePlayStatus()
    }

    private fun maxDurationTimeFormat(millis: Long): DurationTimeFormat {
        val hours = millis / 1000 / 3600
        val minutes = (millis / 1000 % 3600) / 60
        val seconds = millis / 1000 % 60

        return when {
            hours > 0 -> HOUR
            minutes > 0 -> MINUTE
            else -> SECOND
        }
    }

    private fun formatMillisToDynamicTime(
        millis: Long,
        durationTimeFormat: DurationTimeFormat = HOUR
    ): String {
        val hours = millis / 1000 / 3600
        val minutes = (millis / 1000 % 3600) / 60
        val seconds = millis / 1000 % 60

        val formattedDuration = String.format("%d:%02d:%02d", hours, minutes, seconds)

        return when (durationTimeFormat) {
            HOUR -> formattedDuration
            MINUTE -> formattedDuration.substringAfter(":")
            SECOND -> formattedDuration.substringAfterLast(":")
        }
    }
}
