package ru.anydevprojects.simplepodcastapp.authorization.data.models

import kotlinx.serialization.Serializable

@Serializable
data class TokenDto(
    val access: String,
    val refresh: String
)
