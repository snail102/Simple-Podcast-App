package ru.anydevprojects.simplepodcastapp.core.user.domain

import kotlinx.coroutines.flow.StateFlow
import ru.anydevprojects.simplepodcastapp.core.user.domain.models.User

interface UserManager {

    val currentUser: StateFlow<User?>

    fun isAuthorized(): Boolean

    fun getUser(): User?
}
