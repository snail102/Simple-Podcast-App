package ru.anydevprojects.simplepodcastapp.playControl.presentation

import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel
import ru.anydevprojects.simplepodcastapp.playControl.presentation.models.PlayControlEvent
import ru.anydevprojects.simplepodcastapp.playControl.presentation.models.PlayControlIntent
import ru.anydevprojects.simplepodcastapp.playControl.presentation.models.PlayControlState

class PlayControlViewModel(
    private val
) :
    BaseViewModel<PlayControlState, PlayControlState.Content, PlayControlIntent, PlayControlEvent>(
        initialStateAndDefaultContentState = {
            PlayControlState.Loading to PlayControlState.Content()
        }
    ) {
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
