package ru.anydevprojects.simplepodcastapp.core.token.data

import kotlinx.serialization.Serializable
import ru.anydevprojects.simplepodcastapp.core.token.domain.models.Token

@Serializable
data class TokenDto(
    val access: String,
    val refresh: String
)

fun TokenDto.toDomain(): Token = Token(
    access = this.access,
    refresh = this.refresh
)
