package ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation.models.PodcastEpisodeState

@Composable
fun PodcastEpisodeScreen(
    podcastName: String,
    episodeId: Long,
    onBackClick: () -> Unit,
    viewModel: PodcastEpisodeViewModel = koinViewModel(
        parameters = {
            parametersOf(podcastName)
            parametersOf(episodeId)
        }
    )
) {
    val state by viewModel.stateFlow.collectAsState()

    Scaffold(
        topBar = {
            EpisodeTopBar(
                title = podcastName,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        val modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)

        when (val localState = state) {
            is PodcastEpisodeState.Content -> EpisodeContent(
                state = localState,
                modifier = modifier
            )

            PodcastEpisodeState.Failed -> FailedContent(modifier = modifier)
            PodcastEpisodeState.Loading -> LoadingContent(modifier = modifier)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EpisodeTopBar(title: String, onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = title
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick
            ) {
                Icons.AutoMirrored.Default.ArrowBack
            }
        }
    )
}

@Composable
private fun EpisodeContent(state: PodcastEpisodeState.Content, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AsyncImage(
                modifier = Modifier.size(48.dp),
                model = state.podcastImageUrl,
                contentDescription = null
            )

            Card(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = state.dateTimestamp.toString()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = state.title
                    )
                }
            }
        }

        Text(
            text = "Описание",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = state.description
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
}

@Composable
private fun FailedContent(modifier: Modifier = Modifier) {
}
