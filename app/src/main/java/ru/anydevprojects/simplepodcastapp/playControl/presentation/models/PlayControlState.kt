package ru.anydevprojects.simplepodcastapp.playControl.presentation.models

import ru.anydevprojects.simplepodcastapp.core.ui.ContentViewState
import ru.anydevprojects.simplepodcastapp.core.ui.ViewState

sealed interface PlayControlState : ViewState {

    data object Loading : PlayControlState

    data class Content(
        val podcastName: String = "",
        val episodeName: String = "",
        val imageUrl: String = "",
        val isPlaying: Boolean = false,
        val totalDuration: String = ""
    ) : PlayControlState, ContentViewState
}
