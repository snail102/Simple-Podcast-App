package ru.anydevprojects.simplepodcastapp.core.user.domain

import kotlinx.coroutines.flow.Flow
import ru.anydevprojects.simplepodcastapp.core.user.domain.models.User

interface UserRepository {

    val currentUserFlow: Flow<User?>

    val isAuthorizedFlow: Flow<Boolean>

    suspend fun isAuthorized(): Boolean

    suspend fun getUser(): User?

    suspend fun updateUser(user: User)

    suspend fun removeUser()
}
