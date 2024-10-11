package ru.anydevprojects.simplepodcastapp.core.network

import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> { getNetworkClientForLogicServer(get()) }

    single<HttpClient>(
        qualifier = networkClientPodcastIndexQualifier
    ) { getNetworkClient() }
}
