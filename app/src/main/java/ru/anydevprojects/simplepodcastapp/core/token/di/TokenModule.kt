package ru.anydevprojects.simplepodcastapp.core.token.di

import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.core.token.data.TokenRepositoryImpl
import ru.anydevprojects.simplepodcastapp.core.token.domain.TokenRepository

val tokenModule = module {
    single<TokenRepository> { TokenRepositoryImpl(get()) }
}
