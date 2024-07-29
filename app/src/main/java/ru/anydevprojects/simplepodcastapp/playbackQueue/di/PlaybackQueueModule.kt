package ru.anydevprojects.simplepodcastapp.playbackQueue.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.playbackQueue.presentation.PlaybackQueueViewModel

val playbackQueueModule = module {
    viewModel { PlaybackQueueViewModel() }
}