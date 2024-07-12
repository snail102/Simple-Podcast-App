package ru.anydevprojects.simplepodcastapp.home.domain.model

data class PodcastFeed(
    val id: Long,
    val title: String,
    val url: String,
    val link: String,
    val description: String,
    val author: String,
    val image: String,
    val episodeCount: Int,
    val subscribed: Boolean
)
