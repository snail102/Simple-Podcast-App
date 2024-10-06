package ru.anydevprojects.simplepodcastapp.authorization.data.models

import kotlinx.serialization.Serializable
import ru.anydevprojects.simplepodcastapp.core.user.domain.models.User

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val familyName: String,
    val email: String,
    val pictureUrl: String
)

fun UserDto.toDomain(): User = User(
    id = this.id,
    name = this.name,
    familyName = this.familyName,
    email = this.email,
    pictureUrl = this.pictureUrl
)
