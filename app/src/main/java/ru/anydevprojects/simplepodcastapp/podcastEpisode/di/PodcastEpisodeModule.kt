package ru.anydevprojects.simplepodcastapp.podcastEpisode.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.core.database.PodcastDatabase
import ru.anydevprojects.simplepodcastapp.core.network.networkClientLogicServerQualifier
import ru.anydevprojects.simplepodcastapp.core.network.networkClientPodcastIndexQualifier
import ru.anydevprojects.simplepodcastapp.podcastEpisode.data.PodcastEpisodeDao
import ru.anydevprojects.simplepodcastapp.podcastEpisode.data.PodcastEpisodeRepositoryImpl
import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.PodcastEpisodeRepository
import ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation.PodcastEpisodeViewModel

val podcastEpisodeModule = module {
    factory<PodcastEpisodeRepository> {
        PodcastEpisodeRepositoryImpl(
            httpClientLogic = get(qualifier = networkClientLogicServerQualifier),
            httpClient = get(qualifier = networkClientPodcastIndexQualifier),
            podcastEpisodeDao = get()
        )
    }
    viewModel { PodcastEpisodeViewModel(get(), get()) }
    single<PodcastEpisodeDao> { get<PodcastDatabase>().getPodcastEpisodeDao() }
}
