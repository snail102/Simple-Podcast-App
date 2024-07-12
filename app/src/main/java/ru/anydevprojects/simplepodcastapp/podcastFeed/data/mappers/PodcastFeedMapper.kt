package ru.anydevprojects.simplepodcastapp.podcastFeed.data.mappers

import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeed
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.PodcastFeedEntity
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.PodcastFeedWithSubscription

fun PodcastFeed.toEntity(): PodcastFeedEntity {
    return PodcastFeedEntity(
        id = this.id,
        title = this.title,
        url = this.url,
        link = this.link,
        description = this.description,
        image = this.image,
        author = this.author,
        episodeCount = this.episodeCount
    )
}

fun PodcastFeedEntity.toDomain(subscribed: Boolean): PodcastFeed {
    return PodcastFeed(
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
}

fun PodcastFeedWithSubscription.toDomain(): PodcastFeed {
    return PodcastFeed(
        id = this.podcastFeedEntity.id,
        title = this.podcastFeedEntity.title,
        url = this.podcastFeedEntity.url,
        link = this.podcastFeedEntity.link,
        description = this.podcastFeedEntity.description,
        author = this.podcastFeedEntity.author,
        image = this.podcastFeedEntity.image,
        episodeCount = this.podcastFeedEntity.episodeCount,
        subscribed = this.isSubscribed
    )
}
