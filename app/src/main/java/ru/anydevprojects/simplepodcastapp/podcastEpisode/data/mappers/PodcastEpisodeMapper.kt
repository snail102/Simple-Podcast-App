package ru.anydevprojects.simplepodcastapp.podcastEpisode.data.mappers

import ru.anydevprojects.simplepodcastapp.podcastEpisode.data.models.PodcastEpisodeDto
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
    feedImage = this.feedImage,
    feedId = this.feedId

)
