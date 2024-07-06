package ru.anydevprojects.simplepodcastapp.podcastEpisode.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PodcastEpisodeByIdResponse(
    val status: String,
    val id: Int,
    val episode: PodcastEpisodeDto
)
