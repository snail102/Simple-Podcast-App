package ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.models

import ru.anydevprojects.simplepodcastapp.core.ui.ViewEvent

sealed interface SettingsEvent : ViewEvent {
    data object Test1: SettingsEvent
    data object Test2: SettingsEvent
    data object Test3: SettingsEvent
}
