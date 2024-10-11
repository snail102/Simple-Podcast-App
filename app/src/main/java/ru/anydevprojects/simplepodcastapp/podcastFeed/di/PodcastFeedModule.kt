package ru.anydevprojects.simplepodcastapp.podcastFeed.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.core.database.PodcastDatabase
import ru.anydevprojects.simplepodcastapp.core.network.networkClientPodcastIndexQualifier
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.PodcastFeedDao
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.PodcastFeedRepositoryImpl
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.SubscriptionPodcastFeedDao
import ru.anydevprojects.simplepodcastapp.podcastFeed.domain.PodcastFeedRepository
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.PodcastFeedViewModel

val podcastFeedModule = module {

    factory<PodcastFeedRepository> {
        PodcastFeedRepositoryImpl(
            httpClientLogic = get(),
            httpClient = get(qualifier = networkClientPodcastIndexQualifier),
            subscriptionPodcastFeedDao = get(),
            podcastFeedDao = get()
        )
    }
    viewModel { PodcastFeedViewModel(get(), get(), get()) }
    single<SubscriptionPodcastFeedDao> { get<PodcastDatabase>().getSubscriptionPodcastFeedDao() }
    single<PodcastFeedDao> { get<PodcastDatabase>().getPodcastFeedDao() }
}
