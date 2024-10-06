package ru.anydevprojects.simplepodcastapp.core.token.domain

import kotlinx.coroutines.flow.Flow
import ru.anydevprojects.simplepodcastapp.core.token.domain.models.Token

interface TokenRepository {

    val tokenFlow: Flow<Token?>

    val accessToken: String

    val refreshToken: String

    suspend fun hasToken(): Boolean

    suspend fun updateTokens(token: Token)

    suspend fun removeTokens()
}
