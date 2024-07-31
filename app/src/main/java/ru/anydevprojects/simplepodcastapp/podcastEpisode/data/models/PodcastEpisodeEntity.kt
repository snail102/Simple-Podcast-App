package ru.anydevprojects.simplepodcastapp.podcastEpisode.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "podcast_episode")
data class PodcastEpisodeEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val description: String,
    @ColumnInfo(name = "date_timestamp")
    val dateTimestamp: Long,
    @ColumnInfo(name = "enclosure_url")
    val enclosureUrl: String,
    @ColumnInfo(name = "enclosure_type")
    val enclosureType: String,
    @ColumnInfo(name = "enclosure_length")
    val enclosureLength: Int,
    val duration: Int?,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "feed_id")
    val feedId: Long
)
