package ru.anydevprojects.simplepodcastapp.playControl.presentation.models

import ru.anydevprojects.simplepodcastapp.core.ui.ContentViewState
import ru.anydevprojects.simplepodcastapp.core.ui.ViewState

sealed interface PlayControlState : ViewState {

    data object Loading : PlayControlState

    data class Content(
        val episodeName: String = "",
        val imageUrl: String = "",
        val isPlaying: Boolean = false
    ) : PlayControlState, ContentViewState
}
