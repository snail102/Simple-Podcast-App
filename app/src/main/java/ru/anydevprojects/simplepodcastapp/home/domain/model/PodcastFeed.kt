package ru.anydevprojects.simplepodcastapp.home.domain.model

data class PodcastFeed(
    val id: Long,
    val url: String,
    val title: String,
    val description: String,
    val author: String,
    val image: String,
    val subscribed: Boolean
)
