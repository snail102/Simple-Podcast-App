package ru.anydevprojects.simplepodcastapp.podcastFeed.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.anydevprojects.simplepodcastapp.home.data.mappers.toDomain
import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeed
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.mappers.toDomain
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.mappers.toEntity
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.PodcastFeedResponse
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.SubscriptionPodcastFeedEntity
import ru.anydevprojects.simplepodcastapp.podcastFeed.domain.PodcastFeedRepository

class PodcastFeedRepositoryImpl(
    private val httpClient: HttpClient,
    private val subscriptionPodcastFeedDao: SubscriptionPodcastFeedDao,
    private val podcastFeedDao: PodcastFeedDao
) : PodcastFeedRepository {

    override fun podcastFeedByIdFlow(id: Long): Flow<PodcastFeed?> =
        podcastFeedDao.getPodcastWithSubscription(id).map { it?.toDomain() }

    override fun getSubscriptionPodcasts(): Flow<List<PodcastFeed>> =
        podcastFeedDao.getSubscriptionPodcasts().map { it.map { it.toDomain(true) } }

    override suspend fun getAllSubscriptionPodcasts(): List<PodcastFeed> {
        return podcastFeedDao.getAllSubscriptionPodcasts().map { it.toDomain(subscribed = true) }
    }

    override suspend fun getPodcastFeedById(id: Long): Result<PodcastFeed> = kotlin.runCatching {
        val podcastFeedResponse = httpClient.get("podcasts/byfeedid") {
            parameter("id", id)
        }.body<PodcastFeedResponse>()

        val remotePodcastFeed =
            podcastFeedResponse.feed.toDomain(false)

        podcastFeedDao.insert(remotePodcastFeed.toEntity())
        remotePodcastFeed
    }

    override suspend fun getPodcastFeedByUrl(url: String): Result<PodcastFeed> =
        kotlin.runCatching {
            val podcastFeedResponse = httpClient.get("podcasts/byfeedurl") {
                parameter("url", url)
            }.body<PodcastFeedResponse>()

            val remotePodcastFeed =
                podcastFeedResponse.feed.toDomain(false)

            podcastFeedDao.insert(remotePodcastFeed.toEntity())
            remotePodcastFeed
        }

    override suspend fun getLocalPodcastFeedByUrl(url: String): PodcastFeed? {
        return podcastFeedDao.getPodcastWithSubscriptionByUrl(podcastUrl = url)?.toDomain()
    }

    override suspend fun subscribeOnPodcast(podcastId: Long) {
        subscriptionPodcastFeedDao.insert(SubscriptionPodcastFeedEntity(podcastId = podcastId))
    }

    override suspend fun unsubscribeOnPodcast(podcastId: Long) {
        subscriptionPodcastFeedDao.deleteByPodcastId(podcastId = podcastId)
    }
}
