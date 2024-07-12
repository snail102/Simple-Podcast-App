package ru.anydevprojects.simplepodcastapp.home.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel
import ru.anydevprojects.simplepodcastapp.home.domain.HomeRepository
import ru.anydevprojects.simplepodcastapp.home.presentation.mappers.toPodcastSubscriptionUi
import ru.anydevprojects.simplepodcastapp.home.presentation.mappers.toUi
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeEvent
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeIntent
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeState
import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.PodcastEpisodeRepository
import ru.anydevprojects.simplepodcastapp.podcastFeed.domain.PodcastFeedRepository

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val podcastEpisodeRepository: PodcastEpisodeRepository,
    private val podcastFeedRepository: PodcastFeedRepository
) :
    BaseViewModel<HomeState, HomeState.Content, HomeIntent, HomeEvent>(
        initialStateAndDefaultContentState = {
            HomeState.Loading to HomeState.Content()
        }
    ) {

    init {
        collectSubscriptionPodcasts()

        viewModelScope.launch {
            podcastEpisodeRepository.getEpisodesByPodcastIds(
                listOf(1065162)
            ).onSuccess {
                updateState(
                    lastContentState.copy(
                        episodes = it.map { episode -> episode.toUi(false) }
                    )
                )
            }
        }
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.OnChangeSearchPodcastFeed -> changeSearchQuery(intent.query)
            is HomeIntent.OnSearchClick -> searchPodcast(intent.query)
            HomeIntent.OnClearSearchQueryClick -> clearSearchQuery()
            HomeIntent.OnBackFromSearchClick -> closeSearch()
        }
    }

    private fun collectSubscriptionPodcasts() {
        podcastFeedRepository.getSubscriptionPodcasts().onEach {
            updateState(
                lastContentState.copy(
                    podcastsSubscriptions = lastContentState.podcastsSubscriptions.copy(
                        podcasts = it.map { podcastFeed -> podcastFeed.toPodcastSubscriptionUi() }
                    )
                )
            )
        }.launchIn(viewModelScope)
    }

    private fun changeSearchQuery(newQuery: String) {
        updateState(
            HomeState.SearchContent(
                searchQuery = newQuery,
                enabledClear = newQuery.isNotEmpty()
            )
        )
    }

    private fun searchPodcast(query: String) {
        viewModelScope.launch {
            updateState(HomeState.SearchContent(searchQuery = query, isLoading = true))
            homeRepository.getPodcastFeedsByQuery(query).onSuccess {
                updateState(
                    HomeState.SearchContent(
                        searchQuery = query,
                        podcastFeeds = it,
                        isLoading = false
                    )
                )
            }.onFailure {
            }
        }
    }

    private fun clearSearchQuery() {
        changeSearchQuery("")
    }

    private fun closeSearch() {
        updateState(lastContentState)
        emitEvent(HomeEvent.HideKeyboard)
        emitEvent(HomeEvent.ClearFocused)
    }
}
