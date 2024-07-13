package ru.anydevprojects.simplepodcastapp.podcastEpisode.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PodcastEpisodeDto(
    @SerialName("id")
    val id: Long,

    @SerialName("title")
    val title: String,

    @SerialName("description")
    val description: String,

    @SerialName("datePublished")
    val dateTimestamp: Long,

    @SerialName("enclosureUrl")
    val enclosureUrl: String,

    @SerialName("enclosureType")
    val enclosureType: String,

    @SerialName("enclosureLength")
    val enclosureLength: Int,

    @SerialName("duration")
    val duration: Int?,

    @SerialName("feedImage")
    val feedImage: String,

    @SerialName("feedId")
    val feedId: Long
)
