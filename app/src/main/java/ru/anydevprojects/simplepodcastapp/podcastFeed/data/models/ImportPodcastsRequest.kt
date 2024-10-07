package ru.anydevprojects.simplepodcastapp.podcastFeed.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ImportPodcastsRequest(
    val podcastUrls: List<String>
)
