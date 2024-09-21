package ru.anydevprojects.simplepodcastapp.home.presentation.models

import android.net.Uri
import ru.anydevprojects.simplepodcastapp.core.ui.ViewIntent

sealed interface HomeIntent : ViewIntent {
    data class OnChangeSearchPodcastFeed(val query: String) : HomeIntent
    data class OnSearchClick(val query: String) : HomeIntent
    data object OnClearSearchQueryClick : HomeIntent
    data object OnBackFromSearchClick : HomeIntent
    data class OnPlayEpisodeBtnClick(val episodeUi: PodcastEpisodeUi) : HomeIntent
    data object OnChangePayingCurrentMediaBtnClick : HomeIntent
    data object OnMoreClick : HomeIntent
    data object OnDismissMore : HomeIntent
    data object OnSettingsClick : HomeIntent
    data object OnImportOpmlClick : HomeIntent
    data object OnExportOpmlClick : HomeIntent

    data class SelectedImportFile(val uri: Uri?) : HomeIntent
    data class SelectedFolderForExportedFile(val uri: Uri?) : HomeIntent
}
