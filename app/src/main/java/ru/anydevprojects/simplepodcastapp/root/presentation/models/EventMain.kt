package ru.anydevprojects.simplepodcastapp.root.presentation.models

sealed interface EventMain {

    data object NavigateToAuthorization : EventMain
}
