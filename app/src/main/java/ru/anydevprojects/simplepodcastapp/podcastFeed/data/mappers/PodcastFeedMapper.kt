package ru.anydevprojects.simplepodcastapp.podcastFeed.data.mappers

import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeed
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.PodcastFeedEntity
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.PodcastFeedWithSubscription

fun PodcastFeed.toEntity(): PodcastFeedEntity {
    return PodcastFeedEntity(
        id = this.id,
        url = this.url,
        title = this.title,
        description = this.description,
        image = this.image,
        author = this.author
    )
}

fun PodcastFeedEntity.toDomain(subscribed: Boolean): PodcastFeed {
    return PodcastFeed(
        id = this.id,
        url = this.url,
        title = this.title,
        description = this.description,
        image = this.image,
        author = this.author,
        subscribed = subscribed
    )
}

fun PodcastFeedWithSubscription.toDomain(): PodcastFeed {
    return PodcastFeed(
        id = this.podcastFeedEntity.id,
        url = this.podcastFeedEntity.url,
        title = this.podcastFeedEntity.title,
        description = this.podcastFeedEntity.description,
        author = this.podcastFeedEntity.author,
        image = this.podcastFeedEntity.image,
        subscribed = this.isSubscribed
    )
}
