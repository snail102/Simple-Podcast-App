package ru.anydevprojects.simplepodcastapp.playbackQueue.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import ru.anydevprojects.simplepodcastapp.R
import ru.anydevprojects.simplepodcastapp.playbackQueue.domain.models.MediaQueueItem
import ru.anydevprojects.simplepodcastapp.playbackQueue.presentation.models.PlaybackQueueState
import ru.anydevprojects.simplepodcastapp.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackQueueScreen(
    onBackClick: () -> Unit,
    viewModel: PlaybackQueueViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MediumTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text("Очередь воспроизведения")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackClick()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }

            )
        }
    ) { paddingValues ->
        val modifier = Modifier.fillMaxSize().padding(paddingValues)

        when (val localState = state) {
            is PlaybackQueueState.Content -> PlaybackQueueContent(
                modifier = modifier,
                items = localState.mediaQueueItems
            )

            PlaybackQueueState.Failure -> PlaybackQueueFailure(modifier = modifier)
            PlaybackQueueState.Loading -> PlaybackQueueLoading(modifier = modifier)
        }
    }
}

@Composable
private fun PlaybackQueueContent(items: List<MediaQueueItem>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
    ) {
    }
}

@Composable
private fun PlaybackQueueFailure(modifier: Modifier = Modifier) {
}

@Composable
private fun PlaybackQueueLoading(modifier: Modifier = Modifier) {
}

@Composable
private fun PlaybackQueueItem(
    item: MediaQueueItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(color = Color.White)
            .clickable {
                onClick()
            }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp)),
            model = item.url,
            contentDescription = null
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                item.episodeName,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                item.podcastName,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(R.drawable.ic_drag_handle),
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Preview
@Composable
private fun PlaybackQueueItemPreview() {
    AppTheme {
        PlaybackQueueItem(
            onClick = {},
            item = MediaQueueItem(
                id = 0,
                episodeName = "episodeName",
                podcastName = "podcastName",
                url = ""
            )
        )
    }
}
