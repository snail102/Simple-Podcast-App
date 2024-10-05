package ru.anydevprojects.simplepodcastapp.core.user.domain.models

data class User(
    val id: String,
    val name: String,
    val familyName: String,
    val email: String,
    val pictureUrl: String
)
