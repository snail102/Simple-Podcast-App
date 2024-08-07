package ru.anydevprojects.simplepodcastapp.importPodcasts.di

import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.importPodcasts.data.ImportPodcastsRepositoryImpl
import ru.anydevprojects.simplepodcastapp.importPodcasts.domain.ImportPodcastsRepository

val importPodcastsModule = module {
    factory<ImportPodcastsRepository> { ImportPodcastsRepositoryImpl(get(), get()) }
}
