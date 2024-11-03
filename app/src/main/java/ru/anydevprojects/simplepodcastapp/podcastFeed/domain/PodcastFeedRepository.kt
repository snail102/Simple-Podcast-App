package ru.anydevprojects.simplepodcastapp.podcastFeed.domain

import kotlinx.coroutines.flow.Flow
import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeed

interface PodcastFeedRepository {

    fun podcastFeedByIdFlow(id: Long): Flow<PodcastFeed?>

    fun getSubscriptionPodcasts(): Flow<List<PodcastFeed>>

    suspend fun fetchSubscriptionsPodcasts(): Result<Unit>

    suspend fun addSubscriptionsPodcasts(ids: List<Long>): Result<Unit>

    suspend fun getAllSubscriptionPodcasts(): List<PodcastFeed>

    suspend fun getPodcastFeedById(id: Long): Result<PodcastFeed>

    suspend fun getPodcastFeedByUrl(url: String): Result<PodcastFeed>

    suspend fun getLocalPodcastFeedByUrl(url: String): PodcastFeed?

    suspend fun subscribeOnPodcast(podcastId: Long): Result<Unit>

    suspend fun unsubscribeOnPodcast(podcastId: Long): Result<Unit>

    suspend fun importPodcasts(podcastUrls: List<String>): Result<Unit>

    suspend fun getPodcastNameByEpisodeIdFromLocal(id: Long): String
}
