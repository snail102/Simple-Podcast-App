package ru.anydevprojects.simplepodcastapp.podcastFeed.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.PodcastFeedEntity
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.PodcastFeedWithSubscription

@Dao
interface PodcastFeedDao {

    @Upsert
    suspend fun insert(podcastFeedEntity: PodcastFeedEntity)

    @Query("SELECT * FROM podcast_feed WHERE id = :id")
    suspend fun getPodcastFeedById(id: Long): PodcastFeedEntity?

    @Query("SELECT * FROM podcast_feed WHERE url = :url")
    suspend fun getPodcastFeedByUrl(url: String): PodcastFeedEntity?

    @Query("SELECT * FROM podcast_feed WHERE id = :id")
    fun getPodcastFeedFlowById(id: Long): Flow<PodcastFeedEntity?>

    @Query(
        """
        SELECT p.*, 
               CASE 
                   WHEN s.podcast_id IS NOT NULL THEN 1 
                   ELSE 0 
               END AS isSubscribed
        FROM podcast_feed p
        LEFT JOIN subscription_podcast_feed s ON p.id = s.podcast_id
        WHERE p.id = :podcastId
    """
    )
    fun getPodcastWithSubscription(podcastId: Long): Flow<PodcastFeedWithSubscription?>

    @Query(
        """
        SELECT p.*, 
               CASE 
                   WHEN s.podcast_id IS NOT NULL THEN 1 
                   ELSE 0 
               END AS isSubscribed
        FROM podcast_feed p
        LEFT JOIN subscription_podcast_feed s ON p.id = s.podcast_id
        WHERE p.url = :podcastUrl
    """
    )
    suspend fun getPodcastWithSubscriptionByUrl(podcastUrl: String): PodcastFeedWithSubscription?

    @Query(
        """
        SELECT p.*
        FROM podcast_feed p
        JOIN subscription_podcast_feed s ON p.id = s.podcast_id;
    """
    )
    fun getSubscriptionPodcasts(): Flow<List<PodcastFeedEntity>>

    @Query(
        """
        SELECT p.*
        FROM podcast_feed p
        JOIN subscription_podcast_feed s ON p.id = s.podcast_id;
    """
    )
    suspend fun getAllSubscriptionPodcasts(): List<PodcastFeedEntity>

    @Query(
        """
        SELECT * FROM podcast_feed
        WHERE id = (
            SELECT feed_id FROM podcast_episode WHERE id = :id
        )
    """
    )
    suspend fun getPodcastNameByEpisodeId(id: Long): PodcastFeedEntity?

    @Delete
    suspend fun delete(podcastFeedEntity: PodcastFeedEntity)
}
