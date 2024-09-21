package ru.anydevprojects.simplepodcastapp.home.presentation.models

import android.net.Uri
import ru.anydevprojects.simplepodcastapp.core.ui.ViewEvent

sealed interface HomeEvent : ViewEvent {

    data object ClearFocused : HomeEvent

    data object HideKeyboard : HomeEvent

    data class PlayEpisode(
        val imageUri: Uri,
        val title: String,
        val uri: Uri,
        val id: String
    ) : HomeEvent

    data object SelectImportFile : HomeEvent
    data object SelectFolderForExportFile : HomeEvent
    data object OpenSettings : HomeEvent
}
