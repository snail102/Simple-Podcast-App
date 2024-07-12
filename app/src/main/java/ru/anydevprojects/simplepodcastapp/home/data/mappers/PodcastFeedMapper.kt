package ru.anydevprojects.simplepodcastapp.home.data.mappers

import ru.anydevprojects.simplepodcastapp.home.data.models.PodcastFeedDto
import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeed
import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeedSearched

fun PodcastFeedDto.toDomain(subscribed: Boolean): PodcastFeed = PodcastFeed(
    id = this.id,
    title = this.title,
    url = this.url,
    link = this.link,
    description = this.description,
    image = this.image,
    author = this.author,
    episodeCount = this.episodeCount,
    subscribed = subscribed
)

fun PodcastFeedDto.toPodcastFeedSearched(): PodcastFeedSearched = PodcastFeedSearched(
    id = this.id,
    title = this.title,
    description = this.description,
    image = this.image

)
