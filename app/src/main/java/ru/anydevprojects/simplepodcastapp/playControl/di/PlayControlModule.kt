package ru.anydevprojects.simplepodcastapp.playControl.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.playControl.presentation.PlayControlViewModel

val playControlModule = module {
    viewModel { PlayControlViewModel(get()) }
}
