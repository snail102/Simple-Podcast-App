package ru.anydevprojects.simplepodcastapp.playbackQueue.presentation.models

import ru.anydevprojects.simplepodcastapp.core.ui.ContentViewState
import ru.anydevprojects.simplepodcastapp.core.ui.ViewState
import ru.anydevprojects.simplepodcastapp.playbackQueue.domain.models.MediaQueueItem

sealed interface PlaybackQueueState : ViewState {

    data object Loading : PlaybackQueueState

    data object Failure : PlaybackQueueState

    data class Content(
        val mediaQueueItems: List<MediaQueueItem> = emptyList()
    ) : PlaybackQueueState, ContentViewState
}
