package ru.anydevprojects.simplepodcastapp.authorization.presentaion.models

import android.content.IntentSender
import ru.anydevprojects.simplepodcastapp.core.ui.ViewEvent

sealed interface AuthorizationEvent : ViewEvent {
    data class StartSignIn(
        val intentSender: IntentSender?
    ) : AuthorizationEvent
}
