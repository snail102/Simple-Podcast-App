package ru.anydevprojects.simplepodcastapp.authorization.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.anydevprojects.simplepodcastapp.core.token.data.TokenDto

@Serializable
data class SignInResponse(
    @SerialName("tokens")
    val tokens: TokenDto,

    @SerialName("user")
    val user: UserDto
)
