package ru.anydevprojects.simplepodcastapp.podcastEpisode.domain

import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.models.PodcastEpisode

interface PodcastEpisodeRepository {

    suspend fun getEpisodesByPodcastId(podcastId: Long): Result<List<PodcastEpisode>>

    suspend fun getEpisodesByPodcastIds(podcastIds: List<Int>): Result<List<PodcastEpisode>>

    suspend fun getEpisodeById(id: Long): Result<PodcastEpisode>
}
