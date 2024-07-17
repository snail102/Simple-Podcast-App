package ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.models

import androidx.compose.runtime.Immutable
import ru.anydevprojects.simplepodcastapp.home.presentation.models.PodcastEpisodeUi

@Immutable
data class PodcastInfo(
    val title: String = "",
    val description: String = "",
    val author: String = "",
    val imageUrl: String = "",
    val episodeCount: Int = 0,
    val link: String = "",
    val subscribed: Boolean = false,
    val episodes: List<PodcastEpisodeUi> = emptyList()
)
