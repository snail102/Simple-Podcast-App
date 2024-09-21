package ru.anydevprojects.simplepodcastapp.authorization.presentaion.models

import ru.anydevprojects.simplepodcastapp.core.ui.ContentViewState
import ru.anydevprojects.simplepodcastapp.core.ui.ViewState

sealed interface AuthorizationState : ViewState {

    data object Loading : AuthorizationState

    data object Content : AuthorizationState, ContentViewState
}
