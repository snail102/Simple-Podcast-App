package ru.anydevprojects.simplepodcastapp.authorization.presentaion.models

import android.content.Intent
import ru.anydevprojects.simplepodcastapp.core.ui.ViewIntent

sealed interface AuthorizationIntent : ViewIntent {

    data object OnSignInThroughGoogleClick : AuthorizationIntent
    data class OnSignInResult(
        val intent: Intent
    ) : AuthorizationIntent
}
