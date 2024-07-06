package ru.anydevprojects.simplepodcastapp.podcastFeed.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscription_podcast_feed")
data class SubscriptionPodcastFeedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "podcast_id")
    val podcastId: Int
)
