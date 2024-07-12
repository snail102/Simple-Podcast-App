package ru.anydevprojects.simplepodcastapp.podcastEpisode.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.podcastEpisode.data.PodcastEpisodeRepositoryImpl
import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.PodcastEpisodeRepository
import ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation.PodcastEpisodeViewModel

val podcastEpisodeModule = module {
    factory<PodcastEpisodeRepository> { PodcastEpisodeRepositoryImpl(get()) }
    viewModel { PodcastEpisodeViewModel(get(), get(), get()) }
}