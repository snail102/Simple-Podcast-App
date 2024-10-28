package ru.anydevprojects.simplepodcastapp.home.presentation.models

import ru.anydevprojects.simplepodcastapp.core.ui.ContentViewState
import ru.anydevprojects.simplepodcastapp.core.ui.ViewState
import ru.anydevprojects.simplepodcastapp.core.ui.mediaPlayer.MediaPlayerContent
import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeedSearched

sealed interface HomeState : ViewState {

    data object Loading : HomeState

    data object ImportProcessing : HomeState

    data object ExportProcessing : HomeState

    data class Content(
        val podcastEpisodes: List<PodcastEpisodeUi> = emptyList(),
        val podcastsSubscriptions: List<PodcastSubscriptionUi> = emptyList(),
        val searchContent: SearchContent = SearchContent(),
        val mediaPlayerContent: MediaPlayerContent = MediaPlayerContent()
    ) : HomeState, ContentViewState
}

data class SearchContent(
    val isActivate: Boolean = false,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val podcastFeeds: List<PodcastFeedSearched> = emptyList(),
    val enabledClear: Boolean = false,
    val expandedMoreMenu: Boolean = false
)
