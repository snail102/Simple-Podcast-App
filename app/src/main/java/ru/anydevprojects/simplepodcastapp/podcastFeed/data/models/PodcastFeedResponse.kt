package ru.anydevprojects.simplepodcastapp.podcastFeed.data.models

import kotlinx.serialization.Serializable
import ru.anydevprojects.simplepodcastapp.home.data.models.PodcastFeedDto

@Serializable
data class PodcastFeedResponse(
    val status: String,
    val feed: PodcastFeedDto
)
