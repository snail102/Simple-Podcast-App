package ru.anydevprojects.simplepodcastapp.home.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeedSearched
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeEvent
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeIntent
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeState
import ru.anydevprojects.simplepodcastapp.home.presentation.models.PodcastEpisodeUi

@Composable
fun HomeScreen(
    onPodcastClick: (Int) -> Unit,
    onEpisodeClick: (String, Int) -> Unit,
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
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SearchBarPodcastFeeds(
                modifier = Modifier.fillMaxWidth(),
                homeState = state,
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
        }
    ) { paddingValues ->
        Column {
            ContentHomeScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                homeState = state,
                onClick = {
                    onEpisodeClick("name podcase", it)
                }
            )
        }
    }
}

@Composable
private fun ContentHomeScreen(
    homeState: HomeState,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading = homeState is HomeState.Loading

    val podcastEpisodes: List<PodcastEpisodeUi> = if (homeState is HomeState.Content) {
        homeState.episodes
    } else {
        emptyList()
    }

    if (isLoading) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = podcastEpisodes,
                key = {
                    it.id
                }
            ) {
                PodcastEpisodeItem(
                    modifier = Modifier.fillMaxWidth(),
                    podcastEpisodeUi = it,
                    onClick = {
                        onClick(it.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun PodcastEpisodeItem(
    podcastEpisodeUi: PodcastEpisodeUi,
    onClick: () -> Unit,
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
                model = podcastEpisodeUi.imageUrl,
                contentDescription = null
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarPodcastFeeds(
    homeState: HomeState,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading: Boolean
    val query: String
    val response: List<PodcastFeedSearched>
    val isActivate: Boolean
    val enabledClear: Boolean
    if (homeState is HomeState.SearchContent) {
        query = homeState.searchQuery
        response = homeState.podcastFeeds
        isActivate = true
        enabledClear = homeState.enabledClear
        isLoading = homeState.isLoading
    } else {
        query = ""
        response = emptyList()
        isActivate = false
        enabledClear = false
        isLoading = false
    }
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
