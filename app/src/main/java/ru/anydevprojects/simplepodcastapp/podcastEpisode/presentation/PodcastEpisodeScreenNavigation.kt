package ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation

import kotlinx.serialization.Serializable

@Serializable
data class PodcastEpisodeScreenNavigation(
    val podcastName: String,
    val episodeId: Long
)
