package ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.models

data class PodcastEpisode(
    val id: Int,
    val title: String,
    val description: String,
    val dateTimestamp: Int,
    val enclosureUrl: String,
    val enclosureType: String,
    val enclosureLength: Int,
    val duration: Int?,
    val feedImage: String,
    val feedId: Int
)
