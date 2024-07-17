package ru.anydevprojects.simplepodcastapp.home.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.home.data.HomeRepositoryImpl
import ru.anydevprojects.simplepodcastapp.home.domain.HomeRepository
import ru.anydevprojects.simplepodcastapp.home.presentation.HomeViewModel

val homeModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    factory<HomeRepository> { HomeRepositoryImpl(get()) }
}
