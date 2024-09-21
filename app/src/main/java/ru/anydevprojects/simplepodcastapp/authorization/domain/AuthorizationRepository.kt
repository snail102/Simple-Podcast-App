package ru.anydevprojects.simplepodcastapp.authorization.domain

interface AuthorizationRepository {
    suspend fun signInByGoogle()
}
