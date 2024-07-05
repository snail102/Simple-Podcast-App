package ru.anydevprojects.simplepodcastapp.podcastFeed.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.models.PodcastFeedState
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.models.PodcastInfo

@Composable
fun PodcastFeedScreen(
    podcastId: Int,
    viewModel: PodcastFeedViewModel = koinViewModel(
        parameters = {
            parametersOf(podcastId)
        }
    )
) {
    val state by viewModel.stateFlow.collectAsState()

    Scaffold { paddingValues ->
        when (val localState = state) {
            is PodcastFeedState.Failed -> FailedContent(
                failedState = localState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )

            PodcastFeedState.Loading -> LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )

            is PodcastFeedState.PodcastFeedContent -> Content(
                podcastInfo = localState.podcastInfo,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
}

@Composable
private fun Content(podcastInfo: PodcastInfo, modifier: Modifier = Modifier) {
    Column {
        Text(podcastInfo.title)
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun FailedContent(failedState: PodcastFeedState.Failed, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(text = failedState.throwable.message.orEmpty())
    }
}
