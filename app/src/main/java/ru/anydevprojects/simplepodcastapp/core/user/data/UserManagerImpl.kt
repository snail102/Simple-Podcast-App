package ru.anydevprojects.simplepodcastapp.core.user.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.anydevprojects.simplepodcastapp.core.user.domain.UserManager
import ru.anydevprojects.simplepodcastapp.core.user.domain.models.User

class UserManagerImpl : UserManager {

    private val _currentUser: MutableStateFlow<User?> = MutableStateFlow(null)

    override val currentUser: StateFlow<User?>
        get() = _currentUser.asStateFlow()

    override fun isAuthorized(): Boolean = _currentUser.value != null

    override fun getUser(): User? = _currentUser.value
}
