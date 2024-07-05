package ru.anydevprojects.simplepodcastapp.home.domain

import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeed

interface HomeRepository {

    suspend fun getPodcastFeedsByQuery(query: String): Result<List<PodcastFeed>>
}
