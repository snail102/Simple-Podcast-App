package ru.anydevprojects.simplepodcastapp.root.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

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
