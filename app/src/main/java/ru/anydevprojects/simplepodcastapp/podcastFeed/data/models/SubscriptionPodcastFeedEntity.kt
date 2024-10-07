package ru.anydevprojects.simplepodcastapp.podcastFeed.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subscription_podcast_feed",
    indices = [Index(value = ["podcast_id"], unique = true)]
)
data class SubscriptionPodcastFeedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "podcast_id")
    val podcastId: Long
)
