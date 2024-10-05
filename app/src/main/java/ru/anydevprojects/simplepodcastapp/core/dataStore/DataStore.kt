package ru.anydevprojects.simplepodcastapp.core.dataStore

import kotlinx.coroutines.flow.Flow
import ru.anydevprojects.simplepodcastapp.core.user.domain.models.User

interface DataStore {

    val userFlow: Flow<User?>

    val accessToken: String

    val refreshToken: String

    suspend fun getUser(): User?

    suspend fun updateTokens(accessToken: String, refreshToken: String)
    suspend fun removeTokens()
    suspend fun removeUser()
}
