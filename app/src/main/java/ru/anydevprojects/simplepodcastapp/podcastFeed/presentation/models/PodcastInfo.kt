package ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.models

data class PodcastInfo(
    val title: String = "",
    val description: String = "",
    val author: String = "",
    val imageUrl: String = "",
    val episodeCount: Int = 0,
    val link: String = "",
    val subscribed: Boolean = false
)
