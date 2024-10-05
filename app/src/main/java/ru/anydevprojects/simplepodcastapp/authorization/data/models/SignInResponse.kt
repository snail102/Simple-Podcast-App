package ru.anydevprojects.simplepodcastapp.authorization.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInResponse(
    @SerialName("tokens")
    val tokens: TokenDto,

    @SerialName("user")
    val user: UserDto
)
