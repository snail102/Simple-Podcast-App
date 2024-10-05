package ru.anydevprojects.simplepodcastapp.core.user.di

import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.core.user.data.UserRepositoryImpl
import ru.anydevprojects.simplepodcastapp.core.user.domain.UserRepository

val userModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
}
