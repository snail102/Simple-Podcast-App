package ru.anydevprojects.simplepodcastapp.di

import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.authorization.di.authorizationModule
import ru.anydevprojects.simplepodcastapp.core.database.databaseModule
import ru.anydevprojects.simplepodcastapp.core.network.networkModule
import ru.anydevprojects.simplepodcastapp.exportPodcasts.di.exportPodcastsModule
import ru.anydevprojects.simplepodcastapp.home.di.homeModule
import ru.anydevprojects.simplepodcastapp.importPodcasts.di.importPodcastsModule
import ru.anydevprojects.simplepodcastapp.media.di.mediaModule
import ru.anydevprojects.simplepodcastapp.playbackQueue.di.playbackQueueModule
import ru.anydevprojects.simplepodcastapp.podcastEpisode.di.podcastEpisodeModule
import ru.anydevprojects.simplepodcastapp.podcastFeed.di.podcastFeedModule
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.di.settingsModule

val appModule = module {
    includes(
        networkModule,
        databaseModule,
        homeModule,
        podcastFeedModule,
        podcastEpisodeModule,
        mediaModule,
        playbackQueueModule,
        importPodcastsModule,
        exportPodcastsModule,
        authorizationModule,
        settingsModule
    )
}
