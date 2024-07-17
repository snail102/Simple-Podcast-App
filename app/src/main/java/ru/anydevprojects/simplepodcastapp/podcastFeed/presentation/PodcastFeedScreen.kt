package ru.anydevprojects.simplepodcastapp.podcastFeed.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.anydevprojects.simplepodcastapp.home.presentation.models.PodcastEpisodeUi
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.models.PodcastFeedIntent
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.models.PodcastFeedState
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.models.PodcastInfo
import ru.anydevprojects.simplepodcastapp.ui.theme.SimplePodcastAppTheme

@Composable
fun PodcastFeedScreen(
    podcastId: Long,
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

            is PodcastFeedState.PodcastFeedContent -> ContentHomeScreen(
                modifier = Modifier
                    .fillMaxSize(),
                podcastInfo = localState.podcastInfo,
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                ),
                onChangeSubscribeBtnClick = {
                    viewModel.onIntent(PodcastFeedIntent.ChangeSubscriptionStatus)
                }
            )
        }
    }
}

@Composable
private fun ContentHomeScreen(
    podcastInfo: PodcastInfo,
    onChangeSubscribeBtnClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 220.dp + contentPadding.calculateTopPadding())
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(color = Color.Red)
                .padding(
                    top = contentPadding.calculateTopPadding() + 16.dp,
                    start = 32.dp,
                    end = 32.dp,
                    bottom = 32.dp
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(100.dp),
                    model = podcastInfo.imageUrl,
                    contentDescription = null
                )

                Button(
                    modifier = Modifier,
                    onClick = {
                        onChangeSubscribeBtnClick()
                    }
                ) {
                    val text = if (podcastInfo.subscribed) {
                        "Отписаться"
                    } else {
                        "Подписаться"
                    }
                    Text(text)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier,
                text = podcastInfo.title,
                minLines = 2,
                fontSize = 18.sp,
                fontWeight = FontWeight.W500
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "Описание",
            fontSize = 18.sp,
            fontWeight = FontWeight.W500
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color = Color.White)
                .padding(horizontal = 16.dp),
            text = podcastInfo.description,
            overflow = TextOverflow.Ellipsis,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Эпизоды",
                fontSize = 18.sp,
                fontWeight = FontWeight.W500
            )

            Text(
                text = podcastInfo.episodeCount.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.W500
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

//        LazyColumn(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            itemsIndexed(
//                items = podcastInfo.episodes,
//                key = { _, item: PodcastEpisodeUi ->
//                    item.id
//                }
//            ) {index: Int, item: PodcastEpisodeUi ->
//                EpisodeItem(
//                    episodeUi = item
//                )
//                if (index!=podcastInfo.episodes.lastIndex) {
//                    HorizontalDivider(
//                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
//                    )
//                }
//            }
//        }
    }
}

@Preview
@Composable
private fun ContentPreview() {
    SimplePodcastAppTheme {
        ContentHomeScreen(
            podcastInfo = PodcastInfo(
                title = "Названиnjasd asdsadsa dba dbsa dabs dsab dnbajds nsa djad ajd s adajd е",
                description = "Описание",
                author = "Автор",
                imageUrl = "",
                episodeCount = 123,
                link = "",
                subscribed = false
            ),
            contentPadding = PaddingValues(),
            onChangeSubscribeBtnClick = {}
        )
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

@Composable
private fun EpisodeItem(episodeUi: PodcastEpisodeUi, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = episodeUi.title,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            fontSize = 15.sp,
            fontWeight = FontWeight.W500
        )
        Text(
            text = episodeUi.description,
            overflow = TextOverflow.Ellipsis,
            maxLines = 3,
            fontSize = 13.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)

        ) {
            IconButton(
                modifier = Modifier.size(32.dp),
                onClick = {
                }
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null
                )
            }
            IconButton(
                modifier = Modifier.size(32.dp),
                onClick = {
                }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null
                )
            }
            IconButton(
                modifier = Modifier.size(32.dp),
                onClick = {
                }
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null
                )
            }
        }
    }
}

@Preview
@Composable
private fun EpisodeItemPreview() {
    SimplePodcastAppTheme {
        EpisodeItem(
            episodeUi = PodcastEpisodeUi(
                id = 0,
                title = "title",
                description = "description",
                isPlaying = false,
                imageUrl = "",
                audioUrl = ""
            )
        )
    }
}
