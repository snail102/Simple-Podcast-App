package ru.anydevprojects.simplepodcastapp.home.domain.model

data class PodcastFeed(
    val id: Int,
    val podcastGuid: String,
    val title: String,
    val url: String,
    val originalUrl: String,
    val link: String,
    val description: String,
    val author: String,
    val image: String,
    val episodeCount: Int
)
