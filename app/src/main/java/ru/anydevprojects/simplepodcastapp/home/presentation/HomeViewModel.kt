package ru.anydevprojects.simplepodcastapp.home.presentation

import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel
import ru.anydevprojects.simplepodcastapp.home.domain.HomeRepository
import ru.anydevprojects.simplepodcastapp.home.presentation.mappers.toPodcastSubscriptionUi
import ru.anydevprojects.simplepodcastapp.home.presentation.mappers.toUi
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeEvent
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeIntent
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeScreenItem
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeState
import ru.anydevprojects.simplepodcastapp.home.presentation.models.PodcastEpisodeUi
import ru.anydevprojects.simplepodcastapp.home.presentation.models.PodcastsSubscriptions
import ru.anydevprojects.simplepodcastapp.home.presentation.models.SearchContent
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

    private var episodes: List<PodcastEpisodeUi> = emptyList()
    private var podcastsSubscriptions: PodcastsSubscriptions = PodcastsSubscriptions()

    private val _isPlayerSetUp = MutableStateFlow(false)
    val isPlayerSetUp = _isPlayerSetUp.asStateFlow()

    fun setupPlayer() {
        _isPlayerSetUp.update {
            true
        }
    }

    init {
        collectSubscriptionPodcasts()
        collectEpisodes()
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.OnChangeSearchPodcastFeed -> changeSearchQuery(intent.query)
            is HomeIntent.OnSearchClick -> searchPodcast(intent.query)
            HomeIntent.OnClearSearchQueryClick -> clearSearchQuery()
            HomeIntent.OnBackFromSearchClick -> closeSearch()
            is HomeIntent.OnPlayEpisodeBtnClick -> playEpisode(intent.episodeUi)
        }
    }

    private fun playEpisode(episodeUi: PodcastEpisodeUi) {
        viewModelScope.launch {
            setupPlayer()
            emitEvent(
                HomeEvent.PlayEpisode(
                    imageUri = episodeUi.imageUrl.toUri(),
                    title = episodeUi.title,
                    uri = episodeUi.audioUrl.toUri(),
                    id = episodeUi.id.toString()
                )
            )
        }
    }

    private fun collectSubscriptionPodcasts() {
        podcastFeedRepository.getSubscriptionPodcasts().onEach {
            podcastsSubscriptions = podcastsSubscriptions.copy(
                podcasts = it.map { podcastFeed -> podcastFeed.toPodcastSubscriptionUi() }
            )
            updateState()
            loadEpisodesByIds(it.map { podcastFeed -> podcastFeed.id })
        }.launchIn(viewModelScope)
    }

    private fun loadEpisodesByIds(ids: List<Long>) {
        viewModelScope.launch {
            podcastEpisodeRepository.getEpisodesByPodcastIds(ids)
        }
    }

    private fun collectEpisodes() {
        podcastEpisodeRepository.getAllEpisodesSubscriptions().onEach {
            episodes = it.map { episode -> episode.toUi(false) }

            updateState()
        }.launchIn(viewModelScope)
    }

    private fun updateState() {
        val podcastsSubscriptionsHomeScreenItem = podcastsSubscriptions as? HomeScreenItem
        val episodesHomeScreenItem = episodes as? List<HomeScreenItem>

        updateState(
            lastContentState.copy(
                homeScreenItems = buildList {
                    podcastsSubscriptionsHomeScreenItem?.let { it1 -> add(it1) }
                    episodesHomeScreenItem?.let { it1 -> addAll(it1) }
                }
            )
        )
    }

    private fun changeSearchQuery(newQuery: String) {
        updateState(
            lastContentState.copy(
                searchContent = SearchContent(
                    searchQuery = newQuery,
                    enabledClear = newQuery.isNotEmpty()
                )
            )
        )
    }

    private fun searchPodcast(query: String) {
        viewModelScope.launch {
            updateState(
                lastContentState.copy(
                    searchContent = SearchContent(searchQuery = query, isLoading = true)
                )
            )
            homeRepository.getPodcastFeedsByQuery(query).onSuccess {
                updateState(
                    lastContentState.copy(
                        searchContent = SearchContent(
                            searchQuery = query,
                            podcastFeeds = it,
                            isLoading = false
                        )
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
