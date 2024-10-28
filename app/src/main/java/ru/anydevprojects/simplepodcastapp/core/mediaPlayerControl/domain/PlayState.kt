package ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain

sealed interface PlayState {

    data object Init : PlayState

    data object Loading : PlayState

    data object Playing : PlayState

    data object Pause : PlayState
}
