package ru.anydevprojects.simplepodcastapp.podcastEpisode.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.anydevprojects.simplepodcastapp.podcastEpisode.data.mappers.toDomain
import ru.anydevprojects.simplepodcastapp.podcastEpisode.data.mappers.toEntity
import ru.anydevprojects.simplepodcastapp.podcastEpisode.data.models.PodcastEpisodeByIdResponse
import ru.anydevprojects.simplepodcastapp.podcastEpisode.data.models.PodcastEpisodesResponse
import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.PodcastEpisodeRepository
import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.models.PodcastEpisode

class PodcastEpisodeRepositoryImpl(
    private val httpClient: HttpClient,
    private val podcastEpisodeDao: PodcastEpisodeDao
) : PodcastEpisodeRepository {

    override fun getPodcastEpisodeFlow(episodeId: Long): Flow<PodcastEpisode?> =
        podcastEpisodeDao.getEpisodeFlow(episodeId).map { it?.toDomain() }

    override fun getPodcastEpisodesFlow(podcastId: Long): Flow<List<PodcastEpisode>> =
        podcastEpisodeDao.getEpisodesFlow(podcastId).map { list -> list.map { it.toDomain() } }

    override fun getAllEpisodesSubscriptions(): Flow<List<PodcastEpisode>> =
        podcastEpisodeDao.getAllEpisodesSubscriptions()
            .map { it.map { podcastEpisode -> podcastEpisode.toDomain() } }

    override suspend fun getEpisodesByPodcastId(podcastId: Long): Result<List<PodcastEpisode>> {
        return getEpisodes(requestId = podcastId.toString())
    }

    override suspend fun getEpisodesByPodcastIds(
        podcastIds: List<Long>
    ): Result<List<PodcastEpisode>> {
        return getEpisodes(requestId = podcastIds.joinToString(separator = ","))
    }

    override suspend fun getEpisodeById(id: Long): Result<PodcastEpisode> {
        return kotlin.runCatching {
            val podcastEpisodeByIdResponse = httpClient.get("episodes/byid") {
                parameter("id", id)
            }.body<PodcastEpisodeByIdResponse>()

            val episode = podcastEpisodeByIdResponse.episode.toDomain()

            podcastEpisodeDao.insert(episode.toEntity())

            episode
        }
    }

    private suspend fun getEpisodes(requestId: String): Result<List<PodcastEpisode>> {
        return kotlin.runCatching {
            val podcastEpisodesResponse = httpClient.get("episodes/byfeedid") {
                parameter("id", requestId)
            }.body<PodcastEpisodesResponse>()

            val episodes = podcastEpisodesResponse.items.map {
                it.toDomain()
            }

            podcastEpisodeDao.insert(episodes.map { it.toEntity() })

            episodes
        }
    }
}
