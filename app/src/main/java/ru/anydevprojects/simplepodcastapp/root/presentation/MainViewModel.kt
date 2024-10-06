package ru.anydevprojects.simplepodcastapp.root.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
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

    private var startDestination: Screen = AuthorizationScreenNavigation

    private val _stateInitialApp: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val stateInitialApp = _stateInitialApp
        .onStart {
            val isAuthUser = userRepository.isAuthorized()
            val hasToken = tokenRepository.hasToken()
            if (isAuthUser && hasToken) {
                startDestination = HomeScreenNavigation
            }
            _stateAuth.value = isAuthUser
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = false
        )

    private val _stateAuth: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val stateAuth = _stateAuth.asStateFlow()

    private val _event = Channel<EventMain>(capacity = Channel.CONFLATED)
    val event = _event.receiveAsFlow()

    init {
        authorizationStatusObserver()
    }

    fun getStartDestination(): Screen {
        return startDestination
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
        userRepository.currentUserFlow.combine(tokenRepository.tokenFlow) { user, token ->
            if (user == null && token == null) {
                _event.send(EventMain.NavigateToAuthorization)
            }
        }
    }
}
