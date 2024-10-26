package ru.anydevprojects.simplepodcastapp.playControl.presentation.models

import ru.anydevprojects.simplepodcastapp.core.ui.ViewIntent

sealed interface PlayControlIntent : ViewIntent {
    data object OnChangePlayState : PlayControlIntent
    data class OnChangeCurrentPlayPosition(
        val tackPosition: Float
    ) : PlayControlIntent
}
