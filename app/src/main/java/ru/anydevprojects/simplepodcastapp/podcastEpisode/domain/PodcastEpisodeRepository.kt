package ru.anydevprojects.simplepodcastapp.podcastEpisode.domain

import kotlinx.coroutines.flow.Flow
import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.models.PodcastEpisode

interface PodcastEpisodeRepository {

    fun getPodcastEpisodeFlow(episodeId: Long): Flow<PodcastEpisode?>

    fun getPodcastEpisodesFlow(podcastId: Long): Flow<List<PodcastEpisode>>

    fun getAllEpisodesSubscriptions(): Flow<List<PodcastEpisode>>

    suspend fun getEpisodesByPodcastId(podcastId: Long): Result<List<PodcastEpisode>>

    suspend fun getEpisodesFromSubscriptions(): Result<List<PodcastEpisode>>

    suspend fun getEpisodeById(id: Long): Result<PodcastEpisode>
}
