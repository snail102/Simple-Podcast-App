package ru.anydevprojects.simplepodcastapp.home.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel
import ru.anydevprojects.simplepodcastapp.exportPodcasts.domain.ExportPodcastsRepository
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
import ru.anydevprojects.simplepodcastapp.importPodcasts.domain.ImportPodcastsRepository
import ru.anydevprojects.simplepodcastapp.media.AudioItemState
import ru.anydevprojects.simplepodcastapp.media.JetAudioServiceHandler
import ru.anydevprojects.simplepodcastapp.media.JetAudioState
import ru.anydevprojects.simplepodcastapp.media.PlayerEvent
import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.PodcastEpisodeRepository
import ru.anydevprojects.simplepodcastapp.podcastFeed.domain.PodcastFeedRepository

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val podcastEpisodeRepository: PodcastEpisodeRepository,
    private val podcastFeedRepository: PodcastFeedRepository,
    private val applicationContext: Context,
    private val jetAudioServiceHandler: JetAudioServiceHandler,
    private val importPodcastsRepository: ImportPodcastsRepository,
    private val exportPodcastsRepository: ExportPodcastsRepository
) :
    BaseViewModel<HomeState, HomeState.Content, HomeIntent, HomeEvent>(
        initialStateAndDefaultContentState = {
            HomeState.Loading to HomeState.Content()
        }
    ) {

    private var episodes: List<PodcastEpisodeUi> = emptyList()
    private var podcastsSubscriptions: PodcastsSubscriptions = PodcastsSubscriptions()

    override fun onStart() {
        super.onStart()
        fetchSubscriptionsPodcasts()
        loadEpisodesByIds()
    }

    init {
        viewModelScope.launch {
            jetAudioServiceHandler.audioState.collectLatest { mediaState ->
                when (mediaState) {
                    JetAudioState.Initial -> {
                        Log.d("AudioService", "Initial")
                    }

                    is JetAudioState.Buffering -> {
                        Log.d("AudioService", "Buffering")
                    }

                    is JetAudioState.Playing -> {
                        val id = jetAudioServiceHandler.currentPlayingId?.toLongOrNull()
                        updateState(
                            lastContentState.copy(
                                mediaPlayerContent = lastContentState.mediaPlayerContent.copy(
                                    enabled = true,
                                    isPlaying = mediaState.isPlaying
                                ),
                                homeScreenItems = lastContentState.homeScreenItems.map {
                                    when (it) {
                                        is PodcastEpisodeUi -> if (
                                            id != null &&
                                            it.id == id &&
                                            mediaState.isPlaying
                                        ) {
                                            it.copy(
                                                isPlaying = true
                                            )
                                        } else {
                                            it.copy(
                                                isPlaying = false
                                            )
                                        }

                                        is PodcastsSubscriptions -> it
                                    }
                                }
                            )
                        )
                    }

                    is JetAudioState.Progress -> {
                        Log.d("AudioService", "Progress ${mediaState.progress}")
                    }

                    is JetAudioState.CurrentPlaying -> {
                        Log.d("AudioService", "CurrentPlaying ${mediaState.mediaItemIndex}")
                    }

                    is JetAudioState.Ready -> {
                        Log.d("AudioService", "Ready ${mediaState.duration}")
                    }
                }
            }
        }

        jetAudioServiceHandler.audioItemState.onEach { value: AudioItemState ->

            when (value) {
                is AudioItemState.Current -> {
                    updateState(
                        lastContentState.copy(
                            mediaPlayerContent = lastContentState.mediaPlayerContent.copy(
                                enabled = true,
                                title = value.title,
                                imageUrl = value.imageUri?.toString().orEmpty()
                            )
                        )
                    )
                }

                AudioItemState.Empty -> {
                    updateState(
                        lastContentState.copy(
                            mediaPlayerContent = lastContentState.mediaPlayerContent.copy(
                                enabled = false,
                                title = "",
                                imageUrl = ""
                            )
                        )
                    )
                }
            }
        }.launchIn(viewModelScope)

        jetAudioServiceHandler.isPlaybackQueue.onEach {
            updateState(
                lastContentState.copy(
                    mediaPlayerContent = lastContentState.mediaPlayerContent.copy(
                        enabledPlaybackQueue = it
                    )
                )
            )
        }.launchIn(viewModelScope)
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
            HomeIntent.OnChangePayingCurrentMediaBtnClick -> {
                viewModelScope.launch {
                    jetAudioServiceHandler.onPlayerEvents(
                        PlayerEvent.PlayPause
                    )
                }
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
            val state = jetAudioServiceHandler.audioItemState.value
            if (state is AudioItemState.Current && state.id == episodeUi.id.toString()) {
                jetAudioServiceHandler.onPlayerEvents(
                    PlayerEvent.PlayPause
                )
            } else {
                val metadata = MediaMetadata.Builder()
                    .setDisplayTitle(episodeUi.title)
                    .setArtworkUri(episodeUi.imageUrl.toUri())
                    // .setGenre(genres)
                    .build()

                val item = MediaItem.Builder()
                    .setUri(episodeUi.audioUrl.toUri())
                    .setMediaId(episodeUi.id.toString())
                    .setMediaMetadata(metadata)
                    .build()

                jetAudioServiceHandler.setPlayMediaItem(item)
            }
        }
    }

    private fun collectSubscriptionPodcasts() {
        podcastFeedRepository.getSubscriptionPodcasts().onEach {
            podcastsSubscriptions = podcastsSubscriptions.copy(
                podcasts = it.map { podcastFeed -> podcastFeed.toPodcastSubscriptionUi() }
            )
            updateState()
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
