package ru.anydevprojects.simplepodcastapp.podcastEpisode.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import ru.anydevprojects.simplepodcastapp.podcastEpisode.data.mappers.toDomain
import ru.anydevprojects.simplepodcastapp.podcastEpisode.data.models.PodcastEpisodeByIdResponse
import ru.anydevprojects.simplepodcastapp.podcastEpisode.data.models.PodcastEpisodesResponse
import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.PodcastEpisodeRepository
import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.models.PodcastEpisode

class PodcastEpisodeRepositoryImpl(
    private val httpClient: HttpClient
) : PodcastEpisodeRepository {
    override suspend fun getEpisodesByPodcastId(podcastId: Long): Result<List<PodcastEpisode>> {
        return getEpisodes(requestId = podcastId.toString())
    }

    override suspend fun getEpisodesByPodcastIds(
        podcastIds: List<Int>
    ): Result<List<PodcastEpisode>> {
        return getEpisodes(requestId = podcastIds.joinToString(separator = ","))
    }

    override suspend fun getEpisodeById(id: Long): Result<PodcastEpisode> {
        return kotlin.runCatching {
            val podcastEpisodeByIdResponse = httpClient.get("episodes/byid") {
                parameter("id", id)
            }.body<PodcastEpisodeByIdResponse>()

            podcastEpisodeByIdResponse.episode.toDomain()
        }
    }

    private suspend fun getEpisodes(requestId: String): Result<List<PodcastEpisode>> {
        return kotlin.runCatching {
            val podcastEpisodesResponse = httpClient.get("episodes/byfeedid") {
                parameter("id", requestId)
            }.body<PodcastEpisodesResponse>()

            podcastEpisodesResponse.items.map {
                it.toDomain()
            }
        }
    }
}
