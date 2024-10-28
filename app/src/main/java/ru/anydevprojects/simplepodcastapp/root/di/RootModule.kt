package ru.anydevprojects.simplepodcastapp.root.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.root.presentation.MainViewModel

val rootModule = module {
    viewModel { MainViewModel(get(), get(), get(), get()) }
}
