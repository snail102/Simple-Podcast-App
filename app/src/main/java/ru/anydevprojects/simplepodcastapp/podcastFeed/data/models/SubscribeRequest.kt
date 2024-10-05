package ru.anydevprojects.simplepodcastapp.podcastFeed.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscribeRequest(
    @SerialName("podcastId")
    val podcastId: Long
)
