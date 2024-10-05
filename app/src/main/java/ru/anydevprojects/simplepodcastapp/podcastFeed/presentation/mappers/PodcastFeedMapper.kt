package ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.mappers

import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeed
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.models.PodcastInfo

fun PodcastFeed.toPodcastInfo() = PodcastInfo(
    title = this.title,
    description = this.description,
    author = this.author,
    imageUrl = this.image,
    subscribed = this.subscribed
)
