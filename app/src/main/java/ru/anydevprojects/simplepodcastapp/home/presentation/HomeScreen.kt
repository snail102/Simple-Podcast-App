package ru.anydevprojects.simplepodcastapp.home.presentation

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import org.koin.androidx.compose.koinViewModel
import ru.anydevprojects.simplepodcastapp.R
import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeedSearched
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeEvent
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeIntent
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeState
import ru.anydevprojects.simplepodcastapp.home.presentation.models.PodcastEpisodeUi
import ru.anydevprojects.simplepodcastapp.home.presentation.models.PodcastSubscriptionUi
import ru.anydevprojects.simplepodcastapp.home.presentation.models.SearchContent
import ru.anydevprojects.simplepodcastapp.ui.components.BottomMediaPlayer
import ru.anydevprojects.simplepodcastapp.ui.components.PlayControlIconBtn
import ru.anydevprojects.simplepodcastapp.ui.theme.AppTheme
import ru.anydevprojects.simplepodcastapp.utils.rememberFlowWithLifecycle

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    onPodcastClick: (Long) -> Unit,
    onEpisodeClick: (Long) -> Unit,
    onPlaybackQueueClick: () -> Unit,
    openSettings: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            viewModel.onIntent(HomeIntent.SelectedImportFile(uri))
        }

    val contentResolver = LocalContext.current.contentResolver

    val folderPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        viewModel.onIntent(HomeIntent.SelectedFolderForExportedFile(uri))
//        uri?.let {
//            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//            val gg = contentResolver.takePersistableUriPermission(uri, takeFlags)
//        }
    }

    val state by viewModel.stateFlow.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val event = rememberFlowWithLifecycle(viewModel.event)

    LaunchedEffect(event) {
        event.collect { event ->
            when (event) {
                HomeEvent.ClearFocused -> focusManager.clearFocus(true)
                HomeEvent.HideKeyboard -> keyboardController?.hide()
                is HomeEvent.PlayEpisode -> {
                }

                HomeEvent.SelectImportFile -> launcher.launch("text/xml")
                HomeEvent.SelectFolderForExportFile -> folderPickerLauncher.launch(null)
                HomeEvent.OpenSettings -> {
                    openSettings()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceBright)
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
                    viewModel = viewModel,
                    onPlaybackQueueClick = onPlaybackQueueClick
                )
            }

            HomeState.Loading -> {
            }

            HomeState.ExportProcessing -> ExportProcessingScreen(
                modifier = Modifier.padding(
                    paddingValues
                )
            )

            HomeState.ImportProcessing -> ImportProcessingScreen(
                modifier = Modifier.padding(
                    paddingValues
                )
            )
        }
    }
}

@Composable
private fun ImportProcessingScreen(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_import))

    Box(modifier = modifier) {
        LottieAnimation(composition, iterations = LottieConstants.IterateForever, speed = 1.5f)
    }
}

@Composable
private fun ExportProcessingScreen(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_export))

    Box(modifier = modifier) {
        LottieAnimation(composition, iterations = LottieConstants.IterateForever, speed = 1.5f)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentHomeScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    homeState: HomeState.Content,
    onPodcastClick: (Long) -> Unit,
    onEpisodeClick: (Long) -> Unit,
    onPlaybackQueueClick: () -> Unit,
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
            },
            onMoreClick = {
                viewModel.onIntent(HomeIntent.OnMoreClick)
            },
            onDismissMore = {
                viewModel.onIntent(HomeIntent.OnDismissMore)
            },
            onSettingsClick = {
                viewModel.onIntent(HomeIntent.OnSettingsClick)
            },
            onImportOpmlClick = {
                viewModel.onIntent(HomeIntent.OnImportOpmlClick)
            },
            onExportOpmlClick = {
                viewModel.onIntent(HomeIntent.OnExportOpmlClick)
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp + contentPadding.calculateBottomPadding())
        ) {
            item {
                PodcastsSubscriptionsHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = firstItemVisibility
                            translationY = firstItemTranslationY
                        },
                    podcastsSubscriptions = homeState.podcastsSubscriptions,
                    topBarPadding = contentPadding.calculateTopPadding(),
                    onClick = {
                        onPodcastClick(it.id)
                    }
                )
            }

            items(
                items = homeState.podcastEpisodes,
                key = { item ->
                    item.hashCode()
                }
            ) { homeScreenItem ->
                PodcastEpisodeItem(
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
                    availablePlaybackQueue = homeState.mediaPlayerContent.enabledPlaybackQueue,
                    onPlaybackQueueBtnClick = {
                        onPlaybackQueueClick()
                    }
                )
            }
        }
    }
}

@Composable
private fun PodcastsSubscriptionsHeader(
    topBarPadding: Dp,
    podcastsSubscriptions: List<PodcastSubscriptionUi>,
    onClick: (PodcastSubscriptionUi) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .clip(
                RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)
            )
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .padding(top = topBarPadding + 88.dp, bottom = 32.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = podcastsSubscriptions,
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
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
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
                .padding(start = 16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = podcastEpisodeUi.title,
                fontSize = 15.sp,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp,
                fontWeight = FontWeight.W500,
                maxLines = 3,
                minLines = 3
            )

            PlayControlIconBtn(
                modifier = Modifier,
                isPlaying = podcastEpisodeUi.isPlaying,
                onClick = {
                    onPlayBtnClick()
                },
                tint = MaterialTheme.colorScheme.inverseSurface
            )
        }
    }
}

@Preview
@Composable
private fun PodcastEpisodeItemPreview() {
    AppTheme {
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
    onMoreClick: () -> Unit,
    onDismissMore: () -> Unit,
    onItemClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    onImportOpmlClick: () -> Unit,
    onExportOpmlClick: () -> Unit,
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
            } else {
                if (!isActivate) {
                    IconButton(
                        onClick = {
                            onMoreClick()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_more_vert),
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = homeState.expandedMoreMenu,
                onDismissRequest = {
                    onDismissMore()
                }
            ) {
                DropdownMenuItem(
                    onClick = {
                        onSettingsClick()
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = null,
                            tint = Color.Black
                        )
                    },
                    text = {
                        Text("Настройки")
                    }
                )

                DropdownMenuItem(
                    onClick = {
                        onImportOpmlClick()
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_down),
                            contentDescription = null,
                            tint = Color.Black
                        )
                    },
                    text = {
                        Text("Импорт Opml")
                    }
                )
                DropdownMenuItem(
                    onClick = {
                        onExportOpmlClick()
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_up),
                            contentDescription = null,
                            tint = Color.Black
                        )
                    },
                    text = {
                        Text("Экспорт Opml")
                    }
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
    AppTheme {
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
