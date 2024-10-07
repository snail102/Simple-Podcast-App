package ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.models

import ru.anydevprojects.simplepodcastapp.core.ui.ContentViewState
import ru.anydevprojects.simplepodcastapp.core.ui.ViewState

sealed interface SettingsState : ViewState {

    data object Loading : SettingsState

    data class Content(
        val userName: String = "",
        val isAuthorized: Boolean = false,
        val isAvailablePodcastUpdates: Boolean = true,
        val changingAuthStatus: Boolean = false
    ) : SettingsState, ContentViewState
}
