package ru.anydevprojects.simplepodcastapp.home.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PodcastFeedDto(
    @SerialName("id")
    val id: Long,

    @SerialName("title")
    val title: String,

    @SerialName("url")
    val url: String,

    @SerialName("link")
    val link: String,

    @SerialName("description")
    val description: String,

    @SerialName("image")
    val image: String,

    @SerialName("author")
    val author: String,

    @SerialName("episodeCount")
    val episodeCount: Int,

    @SerialName("isSubscribed")
    val isSubscribed: Boolean
)
