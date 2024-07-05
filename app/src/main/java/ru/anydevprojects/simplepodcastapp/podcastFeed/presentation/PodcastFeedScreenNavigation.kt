package ru.anydevprojects.simplepodcastapp.podcastFeed.presentation

import kotlinx.serialization.Serializable

@Serializable
data class PodcastFeedScreenNavigation(
    val podcastId: Int
)
