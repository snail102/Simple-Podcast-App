package ru.anydevprojects.simplepodcastapp.root.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.authorization.presentaion.AuthorizationScreenNavigation
import ru.anydevprojects.simplepodcastapp.core.navigation.Screen
import ru.anydevprojects.simplepodcastapp.core.token.domain.TokenRepository
import ru.anydevprojects.simplepodcastapp.core.user.domain.UserRepository
import ru.anydevprojects.simplepodcastapp.home.presentation.HomeScreenNavigation
import ru.anydevprojects.simplepodcastapp.root.presentation.models.EventMain

class MainViewModel(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private val _startDestination: MutableStateFlow<Screen?> = MutableStateFlow(null)
    val startDestination = _startDestination.asStateFlow()

    private val _stateInitialApp: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val stateInitialApp = _stateInitialApp.asStateFlow()

    init {
        viewModelScope.launch {
            val isAuthUser = userRepository.isAuthorized()
            val hasToken = tokenRepository.hasToken()
            if (isAuthUser && hasToken) {
                _startDestination.value = HomeScreenNavigation
                Log.d("test1", startDestination.toString())
            } else {
                _startDestination.value = AuthorizationScreenNavigation
            }
            _stateAuth.value = isAuthUser
            _stateInitialApp.value = false
        }
    }

    private val _stateAuth: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val stateAuth = _stateAuth.asStateFlow()

    private val _event = Channel<EventMain>(capacity = Channel.CONFLATED)
    val event = _event.receiveAsFlow()

    init {
        authorizationStatusObserver()
    }

    fun getFCMToken() {
        viewModelScope.launch {
            val token = suspendCoroutine<String> { continuation ->
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        continuation.resume("")
                    } else if (task.result != null) {
                        continuation.resume(task.result!!)
                    }
                }
            }
            Log.d("MainViewModel", token)
        }
    }

    private fun authorizationStatusObserver() {
        viewModelScope.launch {
            val isAuthorized = userRepository.isAuthorized()
            userRepository.isAuthorizedFlow.scan(
                isAuthorized to isAuthorized
            ) { previousPair, current ->
                val previousValue = previousPair.second
                previousPair.second to current
            }.collect { (previous, current) ->
                if (previous != current) {
                    if (current) {
                        _event.send(EventMain.NavigateToHome)
                    } else {
                        _event.send(EventMain.NavigateToAuthorization)
                    }
                }
            }
        }
    }
}
