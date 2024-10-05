package ru.anydevprojects.simplepodcastapp.core.dataStore.di

import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.core.dataStore.DataStore
import ru.anydevprojects.simplepodcastapp.core.dataStore.DataStoreImpl

val datastoreModule = module {
    single<DataStore> { DataStoreImpl(get()) }
}
