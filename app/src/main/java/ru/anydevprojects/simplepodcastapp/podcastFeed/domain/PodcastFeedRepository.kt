package ru.anydevprojects.simplepodcastapp.podcastFeed.domain

import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeed

interface PodcastFeedRepository {

    suspend fun getPodcastFeedById(id: Int): Result<PodcastFeed>
}
