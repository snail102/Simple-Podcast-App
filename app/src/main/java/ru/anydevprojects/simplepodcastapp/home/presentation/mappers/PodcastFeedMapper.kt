package ru.anydevprojects.simplepodcastapp.home.presentation.mappers

import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeed
import ru.anydevprojects.simplepodcastapp.home.presentation.models.PodcastSubscriptionUi

fun PodcastFeed.toPodcastSubscriptionUi(): PodcastSubscriptionUi {
    return PodcastSubscriptionUi(id = this.id, imageUrl = this.image)
}
