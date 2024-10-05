package ru.anydevprojects.simplepodcastapp.core.network

import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient>(
        qualifier = networkClientPodcastIndexQualifier
    ) { getNetworkClient() }
    single<HttpClient>(
        qualifier = networkClientLogicServerQualifier
    ) { getNetworkClientForLogicServer(get()) }
}
