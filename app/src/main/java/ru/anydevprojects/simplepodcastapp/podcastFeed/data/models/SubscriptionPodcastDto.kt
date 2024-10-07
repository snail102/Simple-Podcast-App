package ru.anydevprojects.simplepodcastapp.podcastFeed.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionPodcastDto(
    val id: Long,
    val url: String,
    val title: String,
    val description: String,
    val image: String,
    val author: String
)

fun SubscriptionPodcastDto.toEntity(): PodcastFeedEntity = PodcastFeedEntity(
    id = this.id,
    url = this.url,
    title = this.title,
    description = this.description,
    image = this.image,
    author = this.author
)
