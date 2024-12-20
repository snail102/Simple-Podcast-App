package ru.anydevprojects.simplepodcastapp.podcastFeed.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.anydevprojects.simplepodcastapp.home.data.mappers.toDomain
import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeed
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.mappers.toDomain
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.mappers.toEntity
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.ImportPodcastsRequest
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.PodcastFeedResponse
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.SubscribeRequest
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.SubscriptionPodcastDto
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.SubscriptionPodcastFeedEntity
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.UnsubscribeRequest
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.toEntity
import ru.anydevprojects.simplepodcastapp.podcastFeed.domain.PodcastFeedRepository

class PodcastFeedRepositoryImpl(
    private val httpClientLogic: HttpClient,
    private val httpClient: HttpClient,
    private val subscriptionPodcastFeedDao: SubscriptionPodcastFeedDao,
    private val podcastFeedDao: PodcastFeedDao
) : PodcastFeedRepository {

    override fun podcastFeedByIdFlow(id: Long): Flow<PodcastFeed?> =
        podcastFeedDao.getPodcastWithSubscription(id).map { it?.toDomain() }

    override fun getSubscriptionPodcasts(): Flow<List<PodcastFeed>> =
        podcastFeedDao.getSubscriptionPodcasts().map { it.map { it.toDomain(true) } }

    override suspend fun fetchSubscriptionsPodcasts(): Result<Unit> {
        return kotlin.runCatching {
            httpClientLogic.get("subscriptions") {
            }.body<List<SubscriptionPodcastDto>>()
        }.mapCatching { subscriptionsPodcasts ->
            subscriptionsPodcasts.forEach { podcast ->
                podcastFeedDao.insert(podcast.toEntity())
            }
            addSubscriptionsPodcasts(subscriptionsPodcasts.map { it.id })
        }
    }

    override suspend fun addSubscriptionsPodcasts(ids: List<Long>): Result<Unit> {
        return kotlin.runCatching {
            val subscriptions = ids.map { SubscriptionPodcastFeedEntity(podcastId = it) }
            subscriptionPodcastFeedDao.insertAll(subscriptions)
        }
    }

    override suspend fun getAllSubscriptionPodcasts(): List<PodcastFeed> {
        return podcastFeedDao.getAllSubscriptionPodcasts().map { it.toDomain(subscribed = true) }
    }

    override suspend fun getPodcastFeedById(id: Long): Result<PodcastFeed> = kotlin.runCatching {
        val podcastFeedResponse = httpClientLogic.get("podcast_by_id") {
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

    override suspend fun subscribeOnPodcast(podcastId: Long): Result<Unit> {
        return kotlin.runCatching {
            httpClientLogic.post("subscribe") {
                setBody(
                    SubscribeRequest(
                        podcastId = podcastId
                    )
                )
            }
        }.mapCatching {
            if (it.status == HttpStatusCode.OK) {
                subscriptionPodcastFeedDao.insert(
                    SubscriptionPodcastFeedEntity(podcastId = podcastId)
                )
            }
            Unit
        }
    }

    override suspend fun unsubscribeOnPodcast(podcastId: Long): Result<Unit> {
        return kotlin.runCatching {
            httpClientLogic.post("unsubscribe") {
                setBody(
                    UnsubscribeRequest(
                        podcastId = podcastId
                    )
                )
            }
        }.mapCatching {
            if (it.status == HttpStatusCode.OK) {
                subscriptionPodcastFeedDao.deleteByPodcastId(podcastId = podcastId)
            }
            Unit
        }
    }

    override suspend fun importPodcasts(podcastUrls: List<String>): Result<Unit> {
        return kotlin.runCatching {
            httpClientLogic.post("import") {
                setBody(
                    ImportPodcastsRequest(
                        podcastUrls = podcastUrls
                    )
                )
            }
        }.mapCatching {
            fetchSubscriptionsPodcasts()
        }
    }

    override suspend fun getPodcastNameByEpisodeIdFromLocal(id: Long): String {
        return kotlin.runCatching {
            podcastFeedDao.getPodcastNameByEpisodeId(id = id)
        }.getOrNull()?.title.orEmpty()
    }
}
