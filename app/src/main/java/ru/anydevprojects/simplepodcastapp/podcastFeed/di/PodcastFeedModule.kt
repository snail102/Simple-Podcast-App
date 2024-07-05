package ru.anydevprojects.simplepodcastapp.podcastFeed.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.PodcastFeedRepositoryImpl
import ru.anydevprojects.simplepodcastapp.podcastFeed.domain.PodcastFeedRepository
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.PodcastFeedViewModel

val podcastFeedModule = module {

    factory<PodcastFeedRepository> { PodcastFeedRepositoryImpl(get()) }
    viewModel { PodcastFeedViewModel(get(), get()) }
}
