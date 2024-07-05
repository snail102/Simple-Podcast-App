package ru.anydevprojects.simplepodcastapp.di

import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.core.network.networkModule
import ru.anydevprojects.simplepodcastapp.home.di.homeModule
import ru.anydevprojects.simplepodcastapp.podcastFeed.di.podcastFeedModule

val appModule = module {
    includes(
        networkModule,
        // databaseModule,
        homeModule,
        podcastFeedModule
    )
}