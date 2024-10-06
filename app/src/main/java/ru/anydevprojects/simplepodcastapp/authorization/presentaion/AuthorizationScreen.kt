package ru.anydevprojects.simplepodcastapp.authorization.presentaion

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.anydevprojects.simplepodcastapp.authorization.presentaion.models.AuthorizationEvent
import ru.anydevprojects.simplepodcastapp.authorization.presentaion.models.AuthorizationIntent
import ru.anydevprojects.simplepodcastapp.utils.rememberFlowWithLifecycle

@Composable
fun AuthorizationScreen(viewModel: AuthorizationViewModel = koinViewModel()) {
    val coroutineScope = rememberCoroutineScope()

    val scopeSnackbar = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                coroutineScope.launch {
                    viewModel.onIntent(
                        AuthorizationIntent.OnSignInResult(
                            intent = result.data ?: return@launch
                        )
                    )
                }
            }
        }
    )

    val event = rememberFlowWithLifecycle(viewModel.event)
    LaunchedEffect(event) {
        event.collect { event ->
            when (event) {
                is AuthorizationEvent.StartSignIn -> launcher.launch(
                    IntentSenderRequest.Builder(
                        event.intentSender ?: return@collect
                    ).build()
                )

                AuthorizationEvent.ErrorAuth -> {
                    scopeSnackbar.launch {
                        snackbarHostState.showSnackbar("Не удалось авторизоваться")
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    viewModel.onIntent(AuthorizationIntent.OnSignInThroughGoogleClick)
                }
            ) {
                Text(
                    text = "Авторизоваться"
                )
            }
        }
    }
}
