package ru.anydevprojects.simplepodcastapp.settings.mainSettings.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.SettingsViewModel

val settingsModule = module {
    viewModel { SettingsViewModel(get(), get()) }
}
