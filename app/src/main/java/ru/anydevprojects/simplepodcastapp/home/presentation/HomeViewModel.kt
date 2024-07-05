package ru.anydevprojects.simplepodcastapp.home.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel
import ru.anydevprojects.simplepodcastapp.home.domain.HomeRepository
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeEvent
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeIntent
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeState

class HomeViewModel(private val homeRepository: HomeRepository) :
    BaseViewModel<HomeState, HomeState.Content, HomeIntent, HomeEvent>(
        initialStateAndDefaultContentState = {
            HomeState.Loading to HomeState.Content()
        }
    ) {
    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.OnChangeSearchPodcastFeed -> changeSearchQuery(intent.query)
            is HomeIntent.OnSearchClick -> searchPodcast(intent.query)
            HomeIntent.OnClearSearchQueryClick -> clearSearchQuery()
            HomeIntent.OnBackFromSearchClick -> closeSearch()
        }
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
