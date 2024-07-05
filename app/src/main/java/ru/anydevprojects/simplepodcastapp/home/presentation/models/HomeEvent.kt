package ru.anydevprojects.simplepodcastapp.home.presentation.models

import ru.anydevprojects.simplepodcastapp.core.ui.ViewEvent

sealed interface HomeEvent : ViewEvent {

    data object ClearFocused : HomeEvent

    data object HideKeyboard : HomeEvent
}
