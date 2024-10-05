package ru.anydevprojects.simplepodcastapp.authorization.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignIn(
    @SerialName("token")
    val token: String
)
