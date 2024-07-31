package ru.anydevprojects.simplepodcastapp.podcastEpisode.data.mappers

import ru.anydevprojects.simplepodcastapp.podcastEpisode.data.models.PodcastEpisodeDto
import ru.anydevprojects.simplepodcastapp.podcastEpisode.data.models.PodcastEpisodeEntity
import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.models.PodcastEpisode

fun PodcastEpisodeDto.toDomain(): PodcastEpisode = PodcastEpisode(
    id = this.id,
    title = this.title,
    description = this.description,
    dateTimestamp = this.dateTimestamp,
    enclosureUrl = this.enclosureUrl,
    enclosureType = this.enclosureType,
    enclosureLength = this.enclosureLength,
    duration = this.duration,
    image = this.image.ifEmpty { this.feedImage },
    feedId = this.feedId
)

fun PodcastEpisodeEntity.toDomain(): PodcastEpisode = PodcastEpisode(
    id = this.id,
    title = this.title,
    description = this.description,
    dateTimestamp = this.dateTimestamp,
    enclosureUrl = this.enclosureUrl,
    enclosureType = this.enclosureType,
    enclosureLength = this.enclosureLength,
    duration = this.duration,
    image = this.image,
    feedId = this.feedId
)

fun PodcastEpisode.toEntity(): PodcastEpisodeEntity = PodcastEpisodeEntity(
    id = this.id,
    title = this.title,
    description = this.description,
    dateTimestamp = this.dateTimestamp,
    enclosureUrl = this.enclosureUrl,
    enclosureType = this.enclosureType,
    enclosureLength = this.enclosureLength,
    duration = this.duration,
    image = this.image,
    feedId = this.feedId
)
