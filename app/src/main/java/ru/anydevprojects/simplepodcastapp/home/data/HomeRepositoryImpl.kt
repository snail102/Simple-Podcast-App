package ru.anydevprojects.simplepodcastapp.home.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import ru.anydevprojects.simplepodcastapp.home.data.mappers.toDomain
import ru.anydevprojects.simplepodcastapp.home.data.models.SearchPodcastFeedResponse
import ru.anydevprojects.simplepodcastapp.home.domain.HomeRepository
import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeed

class HomeRepositoryImpl(private val httpClient: HttpClient) : HomeRepository {
    override suspend fun getPodcastFeedsByQuery(query: String): Result<List<PodcastFeed>> =
        kotlin.runCatching {
            val searchPodcastFeedResponse = httpClient.get("search/byterm") {
                parameter("q", query)
            }.body<SearchPodcastFeedResponse>()

            searchPodcastFeedResponse.feeds.map { it.toDomain() }
        }
}
