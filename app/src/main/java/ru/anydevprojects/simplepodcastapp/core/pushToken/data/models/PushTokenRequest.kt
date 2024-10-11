package ru.anydevprojects.simplepodcastapp.core.pushToken.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PushTokenRequest(
    val fcmToken: String
)
