package ru.anydevprojects.simplepodcastapp.podcastFeed.data.models

import androidx.room.Embedded

data class PodcastFeedWithSubscription(
    @Embedded val podcastFeedEntity: PodcastFeedEntity,
    val isSubscribed: Boolean
)
