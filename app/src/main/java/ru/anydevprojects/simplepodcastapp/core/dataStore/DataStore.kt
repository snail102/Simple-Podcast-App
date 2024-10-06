package ru.anydevprojects.simplepodcastapp.core.dataStore

import kotlinx.coroutines.flow.Flow
import ru.anydevprojects.simplepodcastapp.core.token.domain.models.Token
import ru.anydevprojects.simplepodcastapp.core.user.domain.models.User

interface DataStore {

    val userFlow: Flow<User?>

    val tokenFlow: Flow<Token?>

    suspend fun getUser(): User?

    suspend fun getToken(): Token?

    suspend fun updateTokens(accessToken: String, refreshToken: String)
    suspend fun updateUser(user: User)
    suspend fun removeTokens()
    suspend fun removeUser()
}
