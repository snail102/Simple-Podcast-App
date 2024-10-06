package ru.anydevprojects.simplepodcastapp.authorization.presentaion

import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.authorization.domain.AuthorizationRepository
import ru.anydevprojects.simplepodcastapp.authorization.presentaion.models.AuthorizationEvent
import ru.anydevprojects.simplepodcastapp.authorization.presentaion.models.AuthorizationIntent
import ru.anydevprojects.simplepodcastapp.authorization.presentaion.models.AuthorizationState
import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel

class AuthorizationViewModel(
    private val applicationContext: Context,
    private val authorizationRepository: AuthorizationRepository
) :
    BaseViewModel<AuthorizationState, AuthorizationState.Content, AuthorizationIntent, AuthorizationEvent>(
        initialStateAndDefaultContentState = {
            AuthorizationState.Loading to AuthorizationState.Content
        }
    ) {

    override fun onIntent(intent: AuthorizationIntent) {
        when (intent) {
            is AuthorizationIntent.OnSignInResult -> processSignInResult(intent.intent)
            AuthorizationIntent.OnSignInThroughGoogleClick -> signIn()
        }
    }

    private fun signIn() {
        viewModelScope.launch {
            authorizationRepository.signInByGoogle().onFailure {
                emitEvent(AuthorizationEvent.ErrorAuth)
            }

            // emitEvent(AuthorizationEvent.StartSignIn(signInIntentSender))
        }
    }

    private fun processSignInResult(intent: Intent) {
        viewModelScope.launch {
            // googleAuthUiClient.signInWithIntent(intent)
        }
    }
}
