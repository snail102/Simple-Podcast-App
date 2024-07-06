package ru.anydevprojects.simplepodcastapp.podcastFeed.domain

import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeed

interface PodcastFeedRepository {

    suspend fun getPodcastFeedById(id: Int): Result<PodcastFeed>

    suspend fun subscribeOnPodcast(podcastId: Int): Result<Unit>

    suspend fun unsubscribeOnPodcast(podcastId: Int): Result<Unit>
}
