package ru.anydevprojects.simplepodcastapp.home.presentation

import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import ru.anydevprojects.simplepodcastapp.ui.components.BottomMediaPlayer
import ru.anydevprojects.simplepodcastapp.ui.components.EpisodeControlPanel
import ru.anydevprojects.simplepodcastapp.ui.theme.SimplePodcastAppTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    onPodcastClick: (Long) -> Unit,
    onEpisodeClick: (Long) -> Unit,
    onPlaybackQueueBtnClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.events.getFlow().collect { event ->
            when (event) {
                HomeEvent.ClearFocused -> focusManager.clearFocus(true)
                HomeEvent.HideKeyboard -> keyboardController?.hide()
                is HomeEvent.PlayEpisode -> {
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
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = paddingValues.calculateTopPadding(),
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
    animatedVisibilityScope: AnimatedVisibilityScope,
    homeState: HomeState.Content,
    onPodcastClick: (Long) -> Unit,
    onEpisodeClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    viewModel: HomeViewModel
) {
    val lazyListState: LazyListState = rememberLazyListState()
    val firstItemTranslationY by remember {
        derivedStateOf {
            if (lazyListState.layoutInfo.visibleItemsInfo.isNotEmpty() && lazyListState.firstVisibleItemIndex == 0) {
                lazyListState.firstVisibleItemScrollOffset * 0.6f
            } else {
                0f
            }
        }
    }

    val firstItemVisibility by remember {
        derivedStateOf {
            if (lazyListState.layoutInfo.visibleItemsInfo.isNotEmpty() && lazyListState.firstVisibleItemIndex == 0) {
                Log.d(
                    "test",
                    "${lazyListState.firstVisibleItemScrollOffset.toFloat()} ${lazyListState.layoutInfo.visibleItemsInfo[0].size}"
                )
                1f - 1.6f * lazyListState.firstVisibleItemScrollOffset.toFloat() / lazyListState.layoutInfo.visibleItemsInfo[0].size
            } else {
                1f
            }
        }
    }

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
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = homeState.homeScreenItems,
                key = { item ->
                    item.hashCode()
                }
            ) { homeScreenItem ->
                when (homeScreenItem) {
                    is PodcastsSubscriptions -> PodcastsSubscriptionsHeader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = firstItemVisibility
                                translationY = firstItemTranslationY
                            },
                        podcastsSubscriptions = homeScreenItem,
                        topBarPadding = contentPadding.calculateTopPadding(),
                        onClick = {
                            onPodcastClick(it.id)
                        }
                    )

                    is PodcastEpisodeUi -> PodcastEpisodeItem(
                        // animatedVisibilityScope = animatedVisibilityScope,
                        modifier = Modifier.fillMaxWidth(),
                        podcastEpisodeUi = homeScreenItem,
                        onClick = {
                            onEpisodeClick(homeScreenItem.id)
                        },
                        onPlayBtnClick = {
                            viewModel.onIntent(HomeIntent.OnPlayEpisodeBtnClick(homeScreenItem))
                        }
                    )
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (homeState.mediaPlayerContent.enabled) {
                BottomMediaPlayer(
                    imageUrl = homeState.mediaPlayerContent.imageUrl,
                    title = homeState.mediaPlayerContent.title,
                    isPlaying = homeState.mediaPlayerContent.isPlaying,
                    onChangePlayState = {
                        viewModel.onIntent(HomeIntent.OnChangePayingCurrentMediaBtnClick)
                    },
                    bottomPadding = contentPadding.calculateBottomPadding(),
                    availablePlaybackQueue = false,
                    onPlaybackQueueBtnClick = {
                        onPlaybackQueueBtnClick()
                    }
                )
            }
        }
    }
}

@Composable
private fun PodcastsSubscriptionsHeader(
    topBarPadding: Dp,
    podcastsSubscriptions: PodcastsSubscriptions,
    onClick: (PodcastSubscriptionUi) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .clip(
                RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)
            )
            .background(color = Color.Red)
            .padding(top = topBarPadding + 88.dp, bottom = 32.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                    .clip(RoundedCornerShape(16.dp))
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PodcastEpisodeItem(
    // animatedVisibilityScope: AnimatedVisibilityScope,
    podcastEpisodeUi: PodcastEpisodeUi,
    onClick: () -> Unit,
    onPlayBtnClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(color = Color.Cyan)
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp)),
//                .sharedElement(
//                    state = rememberSharedContentState(
//                        key = podcastEpisodeUi.imageUrl
//                    ),
//                    animatedVisibilityScope = animatedVisibilityScope
//                ),
            model = podcastEpisodeUi.imageUrl,
            contentDescription = null
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                podcastEpisodeUi.title,
                fontSize = 15.sp,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp,
                fontWeight = FontWeight.W500,
                maxLines = 3,
                minLines = 3
            )
            EpisodeControlPanel(
                modifier = Modifier.fillMaxWidth(),
                isDownloaded = false,
                isAddedPlaylist = false,
                isPlaying = podcastEpisodeUi.isPlaying,
                onDownloadControlClick = {},
                onAddPlaylistControlClick = {},
                onPlayControlClick = {
                    onPlayBtnClick()
                },
                tint = Color.Black
            )
        }
    }
}

@Preview
@Composable
private fun PodcastEpisodeItemPreview() {
    SimplePodcastAppTheme {
        PodcastEpisodeItem(
            podcastEpisodeUi = PodcastEpisodeUi(
                id = 0,
                title = "title sjdbs dbas dsaibd sajidb ahsdb adbasidb asdba daobd ad asbd  bsahd advas d",
                description = "dsasd",
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
    val isActivate: Boolean = homeState.isActivate
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
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(
                    items = response,
                    key = { _, item: PodcastFeedSearched ->
                        item.id
                    }
                ) { index: Int, item: PodcastFeedSearched ->
                    PodcastFeedsItem(
                        modifier = Modifier.fillMaxWidth(),
                        podcastFeed = item,
                        onClick = {
                            onItemClick(item.id)
                        }
                    )
                    if (index < response.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            color = Color.Black,
                            thickness = 1.dp
                        )
                    }
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
    Row(
        modifier = modifier
            .clickable {
                onClick()
            }
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp)),
            model = podcastFeed.image,
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                podcastFeed.title,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Text(
                podcastFeed.description,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
                fontSize = 12.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Preview
@Composable
private fun PodcastFeedsItemPreview() {
    SimplePodcastAppTheme {
        PodcastFeedsItem(
            podcastFeed = PodcastFeedSearched(
                id = 0,
                title = "title",
                description = "Description 123 213123 12321 12321 321 3sdaduhuasduashdihasidh ashdas dhd sadba dahd adbaasdsabd asbd asdb assdhba asd abd  21 3",
                image = ""
            ),
            onClick = {}
        )
    }
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
