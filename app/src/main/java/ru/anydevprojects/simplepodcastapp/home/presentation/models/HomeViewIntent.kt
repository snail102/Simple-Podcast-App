package ru.anydevprojects.simplepodcastapp.home.presentation.models

import ru.anydevprojects.simplepodcastapp.core.ui.ViewIntent

sealed interface HomeIntent : ViewIntent {
    data class OnChangeSearchPodcastFeed(val query: String) : HomeIntent
    data class OnSearchClick(val query: String) : HomeIntent
    data object OnClearSearchQueryClick : HomeIntent
    data object OnBackFromSearchClick : HomeIntent
}
