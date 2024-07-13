package ru.anydevprojects.simplepodcastapp.home.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeedSearched
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeEvent
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeIntent
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeState
import ru.anydevprojects.simplepodcastapp.home.presentation.models.PodcastEpisodeUi
import ru.anydevprojects.simplepodcastapp.home.presentation.models.PodcastSubscriptionUi
import ru.anydevprojects.simplepodcastapp.home.presentation.models.PodcastsSubscriptions
import ru.anydevprojects.simplepodcastapp.home.presentation.models.SearchContent
import ru.anydevprojects.simplepodcastapp.media.rememberManagedMediaController
import ru.anydevprojects.simplepodcastapp.ui.theme.SimplePodcastAppTheme
import ru.anydevprojects.simplepodcastapp.utils.playMediaAt
import ru.anydevprojects.simplepodcastapp.utils.updatePlaylist

@Composable
fun HomeScreen(
    onPodcastClick: (Long) -> Unit,
    onEpisodeClick: (Long) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsState()

    val isPlayerSetUp by viewModel.isPlayerSetUp.collectAsStateWithLifecycle()

    val mediaController by rememberManagedMediaController()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.events.getFlow().collect { event ->
            when (event) {
                HomeEvent.ClearFocused -> focusManager.clearFocus(true)
                HomeEvent.HideKeyboard -> keyboardController?.hide()
                is HomeEvent.PlayEpisode -> {
                    mediaController?.clearMediaItems()
                    val metadata = MediaMetadata.Builder()
                        .setDisplayTitle(event.title)
                        .setArtworkUri(event.imageUri)
                        // .setGenre(genres)
                        .build()

                    mediaController?.updatePlaylist(
                        listOf(
                            MediaItem.Builder()
                                .setUri(event.uri)
                                .setMediaId(event.id)
                                .setMediaMetadata(metadata)
                                .build()
                        )
                    )
                    mediaController?.playMediaAt(0)
                }
            }
        }
    }

    LaunchedEffect(key1 = isPlayerSetUp) {
        if (isPlayerSetUp) {
            mediaController?.run {
                if (mediaItemCount > 0) {
                    prepare()
                    play()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
//        topBar = {
//            when (val localState = state) {
//                is HomeState.Content ->
//
//                HomeState.Loading -> {}
//            }
//        }
    ) { paddingValues ->

        when (val localState = state) {
            is HomeState.Content -> {
                ContentHomeScreen(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = paddingValues.calculateTopPadding() + 88.dp,
                        bottom = paddingValues.calculateBottomPadding()
                    ),
                    homeState = localState,
                    onPodcastClick = {
                        onPodcastClick(it)
                    },
                    onEpisodeClick = {
                        onEpisodeClick(it)
                    },
                    viewModel = viewModel
                )
            }

            HomeState.Loading -> {
            }
        }
    }
}

@Composable
private fun ContentHomeScreen(
    homeState: HomeState.Content,
    onPodcastClick: (Long) -> Unit,
    onEpisodeClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    viewModel: HomeViewModel
) {
    Box(modifier = modifier) {
        SearchBarPodcastFeeds(
            modifier = Modifier.fillMaxWidth(),
            homeState = homeState.searchContent,
            onQueryChange = {
                viewModel.onIntent(HomeIntent.OnChangeSearchPodcastFeed(it))
            },
            onSearch = {
                viewModel.onIntent(HomeIntent.OnSearchClick(it))
            },
            onBackClick = {
                viewModel.onIntent(HomeIntent.OnBackFromSearchClick)
            },
            onClearClick = {
                viewModel.onIntent(HomeIntent.OnClearSearchQueryClick)
            },
            onItemClick = { id ->
                onPodcastClick(id)
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = contentPadding
        ) {
            items(
                items = homeState.homeScreenItems,
                key = { item ->
                    item.hashCode()
                }
            ) { homeScreenItem ->
                when (homeScreenItem) {
                    is PodcastEpisodeUi -> PodcastEpisodeItem(
                        modifier = Modifier.fillMaxWidth(),
                        podcastEpisodeUi = homeScreenItem,
                        onClick = {
                            onEpisodeClick(homeScreenItem.id)
                        },
                        onPlayBtnClick = {
                            viewModel.onIntent(HomeIntent.OnPlayEpisodeBtnClick(homeScreenItem))
                        }
                    )

                    is PodcastsSubscriptions -> PodcastsSubscriptionsHeader(
                        modifier = Modifier.fillMaxWidth(),
                        podcastsSubscriptions = homeScreenItem,
                        onClick = {
                            onPodcastClick(it.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PodcastsSubscriptionsHeader(
    podcastsSubscriptions: PodcastsSubscriptions,
    onClick: (PodcastSubscriptionUi) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
    ) {
        items(
            items = podcastsSubscriptions.podcasts,
            key = {
                it.id
            }
        ) {
            PodcastSubscriptionItem(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .clickable { onClick(it) },
                podcastSubscriptionUi = it
            )
        }
    }
}

@Composable
private fun PodcastSubscriptionItem(
    podcastSubscriptionUi: PodcastSubscriptionUi,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        modifier = modifier,
        model = podcastSubscriptionUi.imageUrl,
        contentDescription = null
    )
}

@Composable
private fun PodcastEpisodeItem(
    podcastEpisodeUi: PodcastEpisodeUi,
    onClick: () -> Unit,
    onPlayBtnClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier
            .padding(16.dp)
            .clickable { onClick() },
        headlineContent = {
            Text(podcastEpisodeUi.title)
        },
        supportingContent = {
            Text(podcastEpisodeUi.description, maxLines = 2)
        },
        leadingContent = {
            AsyncImage(
                modifier = Modifier.size(64.dp),
                model = podcastEpisodeUi.imageUrl,
                contentDescription = null
            )
        },
        trailingContent = {
            IconButton(modifier = Modifier.size(48.dp), onClick = { onPlayBtnClick() }) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null
                )
            }
        }
    )
}

@Preview
@Composable
private fun PodcastEpisodeItemPreview() {
    SimplePodcastAppTheme {
        PodcastEpisodeItem(
            podcastEpisodeUi = PodcastEpisodeUi(
                id = 0,
                title = "title",
                description = "",
                isPlaying = false,
                audioUrl = "",
                imageUrl = "https://img.transistor.fm/IQ-91nRR0Lx3sJv6RaaVAbKEc2Lzp2ttHJqC-vsbj1w/rs:fill:3000:3000:1/q:60/aHR0cHM6Ly9pbWct/dXBsb2FkLXByb2R1/Y3Rpb24udHJhbnNp/c3Rvci5mbS8yZGE2/ZTU4MWU1Y2NmOTBm/ODI1NmJhNjIzYzY2/YzAxYi5qcGc.jpg"
            ),
            onClick = {},
            onPlayBtnClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarPodcastFeeds(
    homeState: SearchContent,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading: Boolean = homeState.isLoading
    val query: String = homeState.searchQuery
    val response: List<PodcastFeedSearched> = homeState.podcastFeeds
    val isActivate: Boolean = query.isNotEmpty()
    val enabledClear: Boolean = homeState.enabledClear

    SearchBar(
        modifier = if (isActivate) {
            modifier
                .animateContentSize(spring(stiffness = Spring.StiffnessHigh))
        } else {
            modifier
                .padding(start = 12.dp, top = 2.dp, end = 12.dp, bottom = 12.dp)
                .fillMaxWidth()
                .animateContentSize(spring(stiffness = Spring.StiffnessHigh))
        },
        query = query,
        onQueryChange = {
            onQueryChange(it)
        },
        onSearch = {
            onSearch(it)
        },
        active = isActivate,
        onActiveChange = {
        },
        leadingIcon = {
            if (isActivate) {
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
            } else {
                IconButton(
                    onClick = {
                        onBackClick()
                    }
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                }
            }
        },
        trailingIcon = {
            if (enabledClear) {
                ClearButton(
                    onClick = onClearClick
                )
            }
        }
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = response,
                    key = {
                        it.id
                    }
                ) {
                    PodcastFeedsItem(
                        modifier = Modifier.fillMaxWidth(),
                        podcastFeed = it,
                        onClick = {
                            onItemClick(it.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PodcastFeedsItem(
    podcastFeed: PodcastFeedSearched,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier
            .padding(16.dp)
            .clickable {
                onClick()
            },
        headlineContent = {
            Text(
                podcastFeed.title
            )
        },
        supportingContent = {
            Text(
                podcastFeed.description,
                maxLines = 3
            )
        },
        leadingContent = {
            AsyncImage(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                model = podcastFeed.image,
                contentDescription = null
            )
        }
    )
}

@Composable
private fun ClearButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        modifier = modifier,
        onClick = {
            onClick()
        }
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = null
        )
    }
}
