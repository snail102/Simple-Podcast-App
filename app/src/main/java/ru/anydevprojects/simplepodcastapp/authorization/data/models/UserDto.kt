package ru.anydevprojects.simplepodcastapp.authorization.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val familyName: String,
    val email: String,
    val pictureUrl: String
)
