package ru.anydevprojects.simplepodcastapp.home.data.mappers

import ru.anydevprojects.simplepodcastapp.home.data.models.PodcastFeedDto
import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeed

fun PodcastFeedDto.toDomain(): PodcastFeed = PodcastFeed(
    id = this.id,
    podcastGuid = this.podcastGuid,
    title = this.title,
    url = this.url,
    originalUrl = this.originalUrl,
    link = this.link,
    description = this.description,
    image = this.image,
    author = this.author,
    episodeCount = this.episodeCount
)
