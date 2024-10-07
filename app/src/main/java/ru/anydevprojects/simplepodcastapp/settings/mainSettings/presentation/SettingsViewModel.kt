package ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.core.token.domain.TokenRepository
import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel
import ru.anydevprojects.simplepodcastapp.core.user.domain.UserRepository
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.models.SettingsEvent
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.models.SettingsIntent
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.models.SettingsState

class SettingsViewModel(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) : BaseViewModel<SettingsState, SettingsState.Content, SettingsIntent, SettingsEvent>(
    initialStateAndDefaultContentState = {
        SettingsState.Loading to SettingsState.Content()
    }
) {

    override fun onStart() {
        super.onStart()
        Log.d("settings", "onStartBlock")

        viewModelScope.launch {
            val user = userRepository.getUser()
            if (user == null) {
                updateState(
                    lastContentState.copy(
                        isAuthorized = false
                    )
                )
            } else {
                updateState(
                    lastContentState.copy(
                        userName = user.name,
                        isAuthorized = true
                    )
                )
            }
        }
    }

    override fun onIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.OnChangeAuthorizedClick -> changeAuthorizationStatus()
        }
    }

    private fun changeAuthorizationStatus() {
        viewModelScope.launch {
            updateState(
                lastContentState.copy(changingAuthStatus = true)
            )

            val user = userRepository.getUser()

            if (user != null) {
                userRepository.removeUser()
                tokenRepository.removeTokens()
            }
        }
    }
}
