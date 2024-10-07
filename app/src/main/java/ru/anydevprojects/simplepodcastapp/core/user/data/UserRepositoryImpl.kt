package ru.anydevprojects.simplepodcastapp.core.user.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.anydevprojects.simplepodcastapp.core.dataStore.DataStore
import ru.anydevprojects.simplepodcastapp.core.user.domain.UserRepository
import ru.anydevprojects.simplepodcastapp.core.user.domain.models.User

class UserRepositoryImpl(
    private val dataStore: DataStore
) : UserRepository {

    override val currentUserFlow: Flow<User?>
        get() = dataStore.userFlow

    override val isAuthorizedFlow: Flow<Boolean>
        get() = dataStore.userFlow.map { it != null && it.id.isNotEmpty() }

    override suspend fun isAuthorized(): Boolean = dataStore.getUser() != null

    override suspend fun getUser(): User? = dataStore.getUser()
    override suspend fun updateUser(user: User) {
        dataStore.updateUser(user = user)
    }

    override suspend fun removeUser() {
        dataStore.removeUser()
    }
}
