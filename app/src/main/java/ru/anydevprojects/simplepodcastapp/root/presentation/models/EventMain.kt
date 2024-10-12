package ru.anydevprojects.simplepodcastapp.root.presentation.models

sealed interface EventMain {

    data object NavigateToAuthorization : EventMain
    data object NavigateToHome : EventMain
    data class NavigateToEpisode(val id: Long) : EventMain
}
