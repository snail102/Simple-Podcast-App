package ru.anydevprojects.simplepodcastapp.core.database

import org.koin.dsl.module

val databaseModule = module {
    single<PodcastDatabase> { createDataBase(get()) }
}
