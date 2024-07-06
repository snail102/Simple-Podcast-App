package ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation.models

import ru.anydevprojects.simplepodcastapp.core.ui.ContentViewState
import ru.anydevprojects.simplepodcastapp.core.ui.ViewState

sealed interface PodcastEpisodeState : ViewState {

    data object Loading : PodcastEpisodeState

    data class Content(
        val title: String = "",
        val description: String = "",
        val dateTimestamp: Int = 0,
        val podcastImageUrl: String = ""
    ) : PodcastEpisodeState, ContentViewState

    data object Failed : PodcastEpisodeState
}
