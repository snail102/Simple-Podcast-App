package ru.anydevprojects.simplepodcastapp.playControl.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain.MediaData
import ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain.MediaPlayerControl
import ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain.PlayState
import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel
import ru.anydevprojects.simplepodcastapp.playControl.presentation.models.PlayControlEvent
import ru.anydevprojects.simplepodcastapp.playControl.presentation.models.PlayControlIntent
import ru.anydevprojects.simplepodcastapp.playControl.presentation.models.PlayControlState

class PlayControlViewModel(
    private val mediaPlayerControl: MediaPlayerControl
) :
    BaseViewModel<PlayControlState.Content, PlayControlState.Content, PlayControlIntent, PlayControlEvent>(
        initialStateAndDefaultContentState = {
            PlayControlState.Content() to PlayControlState.Content()
        }
    ) {

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
                    updateState(
                        lastContentState.copy(
                            episodeName = content.title,
                            imageUrl = content.imageUrl,
                            isPlaying = isPlaying
                        )
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
}
