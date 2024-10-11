package ru.anydevprojects.simplepodcastapp.core.pushToken.domain

interface PushTokenRepository {

    suspend fun sendToken(token: String): Result<Unit>
}
