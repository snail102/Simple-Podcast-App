package ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.models

import ru.anydevprojects.simplepodcastapp.core.ui.ContentViewState
import ru.anydevprojects.simplepodcastapp.core.ui.ViewState

sealed interface PodcastFeedState : ViewState {
    data object Loading : PodcastFeedState
    data class Failed(val throwable: Throwable) : PodcastFeedState
    data class PodcastFeedContent(
        val podcastInfo: PodcastInfo = PodcastInfo()
    ) : PodcastFeedState, ContentViewState
}
