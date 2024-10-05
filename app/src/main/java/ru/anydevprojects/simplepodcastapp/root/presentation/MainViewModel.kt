package ru.anydevprojects.simplepodcastapp.root.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.authorization.presentaion.AuthorizationScreenNavigation
import ru.anydevprojects.simplepodcastapp.core.navigation.Screen
import ru.anydevprojects.simplepodcastapp.core.user.domain.UserRepository
import ru.anydevprojects.simplepodcastapp.home.presentation.HomeScreenNavigation

class MainViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private var startDestination: Screen = AuthorizationScreenNavigation

    private val _stateInitialApp: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val stateInitialApp = _stateInitialApp
        .onStart {
            val isAuthUser = userRepository.isAuthorized()
            if (isAuthUser) {
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
}
