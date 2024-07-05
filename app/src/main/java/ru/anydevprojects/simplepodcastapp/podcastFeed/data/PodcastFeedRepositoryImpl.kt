package ru.anydevprojects.simplepodcastapp.podcastFeed.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import ru.anydevprojects.simplepodcastapp.home.data.mappers.toDomain
import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeed
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.PodcastFeedResponse
import ru.anydevprojects.simplepodcastapp.podcastFeed.domain.PodcastFeedRepository

class PodcastFeedRepositoryImpl(
    private val httpClient: HttpClient
) : PodcastFeedRepository {
    override suspend fun getPodcastFeedById(id: Int): Result<PodcastFeed> = kotlin.runCatching {
        val podcastFeedResponse = httpClient.get("podcasts/byfeedid") {
            parameter("id", id)
        }.body<PodcastFeedResponse>()

        podcastFeedResponse.feed.toDomain()
    }
}
