package ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import ru.anydevprojects.simplepodcastapp.R
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.models.SettingsEvent
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.models.SettingsIntent
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.models.SettingsState
import ru.anydevprojects.simplepodcastapp.ui.components.TitleAppBar
import ru.anydevprojects.simplepodcastapp.utils.rememberFlowWithLifecycle

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel(), onBackClick: () -> Unit) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    val event = rememberFlowWithLifecycle(viewModel.event)

    LaunchedEffect(event) {
        event.collect { event ->
            Log.d("settingsScreen",event.toString())
        }
    }

    Scaffold(
        topBar = {
            TitleAppBar(
                title = "Настройки",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

        when (val currentState = state) {
            SettingsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is SettingsState.Content -> {
                Content(
                    userName = currentState.userName,
                    isAuthorized = currentState.isAuthorized,
                    onChangeAuthorized = {
                        viewModel.onIntent(SettingsIntent.OnChangeAuthorizedClick)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)

                )
            }
        }
    }
}

@Composable
private fun Content(
    userName: String,
    isAuthorized: Boolean,
    onChangeAuthorized: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        UserCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            userName = userName,
            isAuthorized = isAuthorized,
            onChangeAuthorized = onChangeAuthorized

        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = Color.Black
        )

        Spacer(
            modifier = Modifier.height(64.dp)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
                .padding(horizontal = 16.dp),
            onClick = {
            }
        ) {
            Text(text = "Уведомление о новых эпизодах")
        }
    }
}

@Preview
@Composable
private fun ContentPreview() {
    Content(
        userName = "1234",
        isAuthorized = true,
        onChangeAuthorized = {}
    )
}

@Composable
private fun UserCard(
    userName: String,
    isAuthorized: Boolean,
    onChangeAuthorized: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            Icon(
                modifier = Modifier.defaultMinSize(minHeight = 128.dp, minWidth = 128.dp),
                painter = painterResource(R.drawable.ic_person),
                contentDescription = null
            )
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                if (isAuthorized) {
                    Text(
                        text = userName
                    )
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onChangeAuthorized
                ) {
                    val authBtnText = if (isAuthorized) {
                        "Выйти"
                    } else {
                        "Авторизоваться"
                    }
                    Text(text = authBtnText)
                }
            }
        }
    }
}

@Preview
@Composable
private fun UserCardPreview() {
    UserCard("name", false, {})
}
