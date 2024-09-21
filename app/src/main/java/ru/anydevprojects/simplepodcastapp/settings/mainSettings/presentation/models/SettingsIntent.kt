package ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.models

import ru.anydevprojects.simplepodcastapp.core.ui.ViewIntent

sealed interface SettingsIntent : ViewIntent {
    data object OnChangeAuthorizedClick : SettingsIntent
}
