package ru.anydevprojects.simplepodcastapp.exportPodcasts.di

import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.exportPodcasts.data.ExportPodcastsRepositoryImpl
import ru.anydevprojects.simplepodcastapp.exportPodcasts.domain.ExportPodcastsRepository

val exportPodcastsModule = module {
    factory<ExportPodcastsRepository> { ExportPodcastsRepositoryImpl(get(), get()) }
}
