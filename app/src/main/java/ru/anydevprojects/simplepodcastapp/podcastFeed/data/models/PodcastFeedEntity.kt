package ru.anydevprojects.simplepodcastapp.podcastFeed.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "podcast_feed")
data class PodcastFeedEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val url: String,
    val link: String,
    val description: String,
    val image: String,
    val author: String,
    val episodeCount: Int
)
