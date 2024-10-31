package ru.anydevprojects.simplepodcastapp.playControl.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
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

class PlayControlViewModel(
    private val mediaPlayerControl: MediaPlayerControl
) :
    BaseViewModel<PlayControlState.Content, PlayControlState.Content, PlayControlIntent, PlayControlEvent>(
        initialStateAndDefaultContentState = {
            PlayControlState.Content() to PlayControlState.Content()
        }
    ) {

    private val _timePosition: MutableStateFlow<TimePosition> = MutableStateFlow(TimePosition())
    val timePosition = _timePosition.asStateFlow()

    private var durationTimeFormat: DurationTimeFormat = HOUR

    init {
        viewModelScope.launch {
            mediaPlayerControl.currentMedia.combine(
                mediaPlayerControl.playState
            ) { mediaData: MediaData, playState: PlayState ->
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
                _timePosition.update {
                    it.copy(
                        currentTime = formatMillisToDynamicTime(
                            currentDuration,
                            durationTimeFormat
                        ),
                        trackPosition = progress
                    )
                }
            }.collect()
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
        }
    }

    private fun movePlay(positionTrack: Float) {
    }

    private fun changePlayState() {
    }

    private fun maxDurationTimeFormat(millis: Int): DurationTimeFormat {
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
        millis: Int,
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
