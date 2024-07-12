package ru.anydevprojects.simplepodcastapp.home.presentation.models

import ru.anydevprojects.simplepodcastapp.core.ui.ContentViewState
import ru.anydevprojects.simplepodcastapp.core.ui.ViewState
import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeedSearched

sealed interface HomeState : ViewState {

    data object Loading : HomeState

    data class Content(
        val podcastsSubscriptions: PodcastsSubscriptions = PodcastsSubscriptions(),
        val episodes: List<PodcastEpisodeUi> = emptyList()
    ) :
        HomeState,
        ContentViewState

    data class SearchContent(
        val isLoading: Boolean = false,
        val searchQuery: String = "",
        val podcastFeeds: List<PodcastFeedSearched> = emptyList(),
        val enabledClear: Boolean = false
    ) : HomeState
}
