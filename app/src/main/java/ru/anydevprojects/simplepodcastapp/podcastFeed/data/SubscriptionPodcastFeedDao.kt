package ru.anydevprojects.simplepodcastapp.podcastFeed.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.SubscriptionPodcastFeedEntity

@Dao
interface SubscriptionPodcastFeedDao {

    @Upsert
    suspend fun insert(subscriptionPodcastFeed: SubscriptionPodcastFeedEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(subscriptionPodcastFeeds: List<SubscriptionPodcastFeedEntity>)

    @Query("SELECT * FROM subscription_podcast_feed")
    suspend fun getAllSubscription(): List<SubscriptionPodcastFeedEntity>

    @Query("DELETE FROM subscription_podcast_feed WHERE podcast_id = :podcastId")
    suspend fun deleteByPodcastId(podcastId: Long)

    @Query("SELECT * FROM subscription_podcast_feed WHERE podcast_id=:podcastId ")
    suspend fun getByPodcastId(podcastId: Long): SubscriptionPodcastFeedEntity?

    @Delete
    suspend fun delete(subscriptionPodcastFeed: SubscriptionPodcastFeedEntity)
}
