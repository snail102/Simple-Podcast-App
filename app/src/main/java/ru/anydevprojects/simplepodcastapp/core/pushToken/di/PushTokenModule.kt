package ru.anydevprojects.simplepodcastapp.core.pushToken.di

import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.core.pushToken.data.PushTokenRepositoryImpl
import ru.anydevprojects.simplepodcastapp.core.pushToken.domain.PushTokenRepository

val pushTokenModule = module {
    single<PushTokenRepository> { PushTokenRepositoryImpl(get()) }
}
