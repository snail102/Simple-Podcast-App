package ru.anydevprojects.simplepodcastapp.home.presentation

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain.MediaData
import ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain.MediaPlayerControl
import ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain.PlayMediaData
import ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain.PlayState
import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel
import ru.anydevprojects.simplepodcastapp.exportPodcasts.domain.ExportPodcastsRepository
import ru.anydevprojects.simplepodcastapp.home.domain.HomeRepository
import ru.anydevprojects.simplepodcastapp.home.presentation.mappers.toPodcastSubscriptionUi
import ru.anydevprojects.simplepodcastapp.home.presentation.mappers.toUi
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeEvent
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeIntent
import ru.anydevprojects.simplepodcastapp.home.presentation.models.HomeState
import ru.anydevprojects.simplepodcastapp.home.presentation.models.PodcastEpisodeUi
import ru.anydevprojects.simplepodcastapp.home.presentation.models.SearchContent
import ru.anydevprojects.simplepodcastapp.importPodcasts.domain.ImportPodcastsRepository
import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.PodcastEpisodeRepository
import ru.anydevprojects.simplepodcastapp.podcastFeed.domain.PodcastFeedRepository

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val podcastEpisodeRepository: PodcastEpisodeRepository,
    private val podcastFeedRepository: PodcastFeedRepository,
    private val importPodcastsRepository: ImportPodcastsRepository,
    private val exportPodcastsRepository: ExportPodcastsRepository,
    private val mediaPlayerControl: MediaPlayerControl
) :
    BaseViewModel<HomeState, HomeState.Content, HomeIntent, HomeEvent>(
        initialStateAndDefaultContentState = {
            HomeState.Loading to HomeState.Content()
        }
    ) {

    override fun onStart() {
        super.onStart()
        fetchSubscriptionsPodcasts()
        loadEpisodesByIds()
    }

    init {
        collectSubscriptionPodcasts()
        collectEpisodes()
        collectCurrentMedia()
        collectPlayingState()
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.OnChangeSearchPodcastFeed -> changeSearchQuery(intent.query)
            is HomeIntent.OnSearchClick -> searchPodcast(intent.query)
            HomeIntent.OnClearSearchQueryClick -> clearSearchQuery()
            HomeIntent.OnBackFromSearchClick -> closeSearch()
            is HomeIntent.OnPlayEpisodeBtnClick -> playEpisode(intent.episodeUi)
            HomeIntent.OnChangePayingCurrentMediaBtnClick -> {
                mediaPlayerControl.changePlayStatus()
            }

            HomeIntent.OnDismissMore -> dismissMoreMenu()
            HomeIntent.OnMoreClick -> showMoreMenu()
            HomeIntent.OnSettingsClick -> openSettings()
            HomeIntent.OnExportOpmlClick -> exportOpml()
            HomeIntent.OnImportOpmlClick -> importOpml()
            is HomeIntent.SelectedImportFile -> startImportDataFromFile(intent.uri)
            is HomeIntent.SelectedFolderForExportedFile -> startExportDataToFile(intent.uri)
        }
    }

    private fun startExportDataToFile(uri: Uri?) {
        viewModelScope.launch {
            Log.d("folderForExportFile", uri.toString())
            updateState(
                lastContentState.copy(
                    searchContent = lastContentState.searchContent.copy(
                        expandedMoreMenu = false
                    )
                )
            )

            if (uri == null) {
                return@launch
            }

            exportPodcastsRepository.export(uri.toString())
        }
    }

    private fun startImportDataFromFile(uri: Uri?) {
        viewModelScope.launch {
            Log.d("importFile", uri.toString())
            updateState(
                lastContentState.copy(
                    searchContent = lastContentState.searchContent.copy(
                        expandedMoreMenu = false
                    )
                )
            )
            if (uri == null) {
                return@launch
            }
            updateState(
                HomeState.ImportProcessing
            )
            importPodcastsRepository.import(uri.toString())
            updateState(lastContentState)
        }
    }

    private fun exportOpml() {
        updateState(HomeState.ExportProcessing)
        emitEvent(HomeEvent.SelectFolderForExportFile)
    }

    private fun importOpml() {
        updateState(HomeState.ImportProcessing)
        emitEvent(HomeEvent.SelectImportFile)
    }

    private fun showMoreMenu() {
        updateState(
            lastContentState.copy(
                searchContent = lastContentState.searchContent.copy(
                    expandedMoreMenu = true
                )
            )
        )
    }

    private fun hideMoreMenu() {
        updateState(
            lastContentState.copy(
                searchContent = lastContentState.searchContent.copy(
                    expandedMoreMenu = false
                )
            )
        )
    }

    private fun openSettings() {
        hideMoreMenu()
        emitEvent(HomeEvent.OpenSettings)
    }

    private fun dismissMoreMenu() {
        updateState(
            lastContentState.copy(
                searchContent = lastContentState.searchContent.copy(
                    expandedMoreMenu = false
                )
            )
        )
    }

    @OptIn(UnstableApi::class)
    @SuppressLint("RestrictedApi")
    private fun playEpisode(episodeUi: PodcastEpisodeUi) {
        viewModelScope.launch {
            mediaPlayerControl.play(
                PlayMediaData(
                    id = episodeUi.id,
                    title = episodeUi.title,
                    imageUrl = episodeUi.imageUrl,
                    audioUrl = episodeUi.audioUrl
                )
            )
        }
    }

    private fun collectSubscriptionPodcasts() {
        podcastFeedRepository.getSubscriptionPodcasts().onEach {
            updateState(
                lastContentState.copy(
                    podcastsSubscriptions = it.map { it.toPodcastSubscriptionUi() }
                )
            )
        }.launchIn(viewModelScope)
    }

    private fun fetchSubscriptionsPodcasts() {
        viewModelScope.launch {
            podcastFeedRepository.fetchSubscriptionsPodcasts()
        }
    }

    private fun loadEpisodesByIds() {
        viewModelScope.launch {
            podcastEpisodeRepository.getEpisodesFromSubscriptions()
        }
    }

    private fun collectEpisodes() {
        podcastEpisodeRepository.getAllEpisodesSubscriptions().onEach {
            val currentPlayingId = mediaPlayerControl.currentMediaContent?.id
            updateState(
                lastContentState.copy(
                    podcastEpisodes = it.map { episode ->
                        episode.toUi(
                            isPlaying = episode.id == currentPlayingId && mediaPlayerControl.playState.value is PlayState.Playing
                        )
                    }
                )
            )
        }.launchIn(viewModelScope)
    }

    private fun collectCurrentMedia() {
        mediaPlayerControl.currentMedia.onEach { mediaData ->
            when (mediaData) {
                is MediaData.Content -> {
                    updateState(
                        lastContentState.copy(
                            podcastEpisodes = lastContentState.podcastEpisodes.map {
                                it.copy(
                                    isPlaying = it.id == mediaData.id && mediaPlayerControl.playState.value is PlayState.Playing
                                )
                            }
                        )
                    )
                }

                MediaData.Init -> {}
                MediaData.Loading -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun collectPlayingState() {
        mediaPlayerControl.playState.onEach { playState ->
            when (playState) {
                PlayState.Init -> {}
                PlayState.Loading -> {}
                PlayState.Pause -> {
                    updateState(
                        lastContentState.copy(
                            podcastEpisodes = lastContentState.podcastEpisodes.map {
                                it.copy(
                                    isPlaying = false
                                )
                            }
                        )
                    )
                }

                PlayState.Playing -> {
                    updateState(
                        lastContentState.copy(
                            podcastEpisodes = lastContentState.podcastEpisodes.map {
                                it.copy(
                                    isPlaying = it.id == mediaPlayerControl.currentMediaContent?.id
                                )
                            }
                        )
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun changeSearchQuery(newQuery: String) {
        updateState(
            lastContentState.copy(
                searchContent = lastContentState.searchContent.copy(
                    searchQuery = newQuery,
                    isActivate = true,
                    enabledClear = newQuery.isNotEmpty()
                )
            )
        )
    }

    private fun searchPodcast(query: String) {
        viewModelScope.launch {
            updateState(
                lastContentState.copy(
                    searchContent = lastContentState.searchContent.copy(
                        searchQuery = query,
                        isLoading = true
                    )
                )
            )
            homeRepository.getPodcastFeedsByQuery(query).onSuccess {
                updateState(
                    lastContentState.copy(
                        searchContent = lastContentState.searchContent.copy(
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
        updateState(
            lastContentState.copy(
                searchContent = SearchContent()
            )
        )
        emitEvent(HomeEvent.HideKeyboard)
        emitEvent(HomeEvent.ClearFocused)
    }
}
