package ru.anydevprojects.simplepodcastapp.home.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import ru.anydevprojects.simplepodcastapp.home.data.mappers.toPodcastFeedSearched
import ru.anydevprojects.simplepodcastapp.home.data.models.SearchPodcastFeedResponse
import ru.anydevprojects.simplepodcastapp.home.domain.HomeRepository
import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeedSearched

class HomeRepositoryImpl(
    private val httpClient: HttpClient
) : HomeRepository {
    override suspend fun getPodcastFeedsByQuery(query: String): Result<List<PodcastFeedSearched>> =
        kotlin.runCatching {
            val searchPodcastFeedResponse = httpClient.get("search") {
                parameter("q", query)
            }.body<SearchPodcastFeedResponse>()

            searchPodcastFeedResponse.feeds.map { it.toPodcastFeedSearched() }
        }
}
