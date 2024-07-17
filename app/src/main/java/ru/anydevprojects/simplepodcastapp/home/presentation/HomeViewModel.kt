package ru.anydevprojects.simplepodcastapp.home.presentation

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.DeviceInfo
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.work.await
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
import ru.anydevprojects.simplepodcastapp.media.PlaybackService
import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.PodcastEpisodeRepository
import ru.anydevprojects.simplepodcastapp.podcastFeed.domain.PodcastFeedRepository
import ru.anydevprojects.simplepodcastapp.utils.playMediaAt
import ru.anydevprojects.simplepodcastapp.utils.updatePlaylist

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val podcastEpisodeRepository: PodcastEpisodeRepository,
    private val podcastFeedRepository: PodcastFeedRepository,
    private val applicationContext: Context
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

    private var mediaController: MediaController? = null

    private val listener = object : Player.Listener {
        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            Log.d("onTimelineChanged", timeline.periodCount.toString())
        }

        override fun onTracksChanged(tracks: Tracks) {
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        }

        override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
        }

        override fun onAvailableCommandsChanged(availableCommands: Player.Commands) {
        }

        override fun onTrackSelectionParametersChanged(parameters: TrackSelectionParameters) {
        }

        override fun onPlaybackStateChanged(@Player.State playbackState: Int) {
        }

        override fun onPlayWhenReadyChanged(
            playWhenReady: Boolean,
            @Player.PlayWhenReadyChangeReason reason: Int
        ) {
        }

        override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            Log.d("isPlaying", isPlaying.toString())
            updateState(
                lastContentState.copy(
                    mediaPlayerContent = lastContentState.mediaPlayerContent.copy(
                        isPlaying = isPlaying
                    )
                )
            )
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        }

        override fun onPlayerErrorChanged(error: PlaybackException?) {
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
        }

        override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) {
        }

        override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
        }

        override fun onMaxSeekToPreviousPositionChanged(maxSeekToPreviousPositionMs: Long) {
        }

        override fun onAudioAttributesChanged(audioAttributes: AudioAttributes) {
        }

        override fun onVolumeChanged(volume: Float) {
        }

        override fun onDeviceInfoChanged(deviceInfo: DeviceInfo) {
        }

        override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
        }

        override fun onCues(cues: CueGroup) {
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
            HomeIntent.OnChangePayingCurrentMediaBtnClick -> {
                if (mediaController?.isPlaying == true) {
                    mediaController?.pause()
                } else {
                    mediaController?.play()
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun playEpisode(episodeUi: PodcastEpisodeUi) {
        viewModelScope.launch {
            setupPlayer()

            if (!isPlayerSetUp.value) {
                mediaController = MediaController.Builder(
                    applicationContext,
                    SessionToken(
                        applicationContext,
                        ComponentName(applicationContext, PlaybackService::class.java)
                    )
                ).buildAsync().await()

                mediaController?.run {
                    if (mediaItemCount > 0) {
                        prepare()
                        play()
                        addListener(listener)
                    }
                }
            }

            mediaController?.clearMediaItems()
            val metadata = MediaMetadata.Builder()
                .setDisplayTitle(episodeUi.title)
                .setArtworkUri(episodeUi.imageUrl.toUri())
                // .setGenre(genres)
                .build()

            mediaController?.updatePlaylist(
                listOf(
                    MediaItem.Builder()
                        .setUri(episodeUi.audioUrl.toUri())
                        .setMediaId(episodeUi.id.toString())
                        .setMediaMetadata(metadata)
                        .build()
                )
            )
            mediaController?.playMediaAt(0)

            updateState(
                lastContentState.copy(
                    mediaPlayerContent = lastContentState.mediaPlayerContent.copy(
                        enabled = true,
                        title = episodeUi.title,
                        imageUrl = episodeUi.imageUrl
                    )
                )
            )

//            emitEvent(
//                HomeEvent.PlayEpisode(
//                    imageUri = episodeUi.imageUrl.toUri(),
//                    title = episodeUi.title,
//                    uri = episodeUi.audioUrl.toUri(),
//                    id = episodeUi.id.toString()
//                )
//            )
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
