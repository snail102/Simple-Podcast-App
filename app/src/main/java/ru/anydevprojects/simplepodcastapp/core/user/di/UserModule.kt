package ru.anydevprojects.simplepodcastapp.core.user.di

import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.core.user.data.UserManagerImpl
import ru.anydevprojects.simplepodcastapp.core.user.domain.UserManager

val userModule = module {
    single<UserManager> { UserManagerImpl() }
}
