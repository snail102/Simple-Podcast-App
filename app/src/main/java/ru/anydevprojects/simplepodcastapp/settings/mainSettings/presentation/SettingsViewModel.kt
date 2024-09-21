package ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.models.SettingsEvent
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.models.SettingsIntent
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.models.SettingsState

class SettingsViewModel() : BaseViewModel<SettingsState, SettingsState.Content, SettingsIntent, SettingsEvent>(
    initialStateAndDefaultContentState = {
        SettingsState.Loading to SettingsState.Content()
    }
) {

    override fun onStart() {
        super.onStart()
        Log.d("settings", "onStartBlock")

        viewModelScope.launch {
            Log.d("settings", "send Test1")
            emitEvent(SettingsEvent.Test1)

            delay(3000)
            Log.d("settings", "send Test2")
            emitEvent(SettingsEvent.Test2)

            delay(3000)
            Log.d("settings", "send Test3")
            emitEvent(SettingsEvent.Test3)
        }
    }

    override fun onIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.OnChangeAuthorizedClick -> changeAuthorizationStatus()
        }
    }

    private fun changeAuthorizationStatus() {
    }
}
