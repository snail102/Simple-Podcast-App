package ru.anydevprojects.simplepodcastapp.home.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SearchPodcastFeedResponse(
    val status: Boolean,
    val feeds: List<PodcastFeedDto>,
    val count: Long
)
