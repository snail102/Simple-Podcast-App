package ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation

import kotlinx.serialization.Serializable

@Serializable
data class PodcastEpisodeScreenNavigation(
    val episodeId: Long
)
