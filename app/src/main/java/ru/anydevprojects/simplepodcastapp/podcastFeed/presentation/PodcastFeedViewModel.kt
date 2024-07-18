package ru.anydevprojects.simplepodcastapp.podcastFeed.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel
import ru.anydevprojects.simplepodcastapp.home.presentation.mappers.toUi
import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.PodcastEpisodeRepository
import ru.anydevprojects.simplepodcastapp.podcastFeed.domain.PodcastFeedRepository
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.mappers.toPodcastInfo
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.models.PodcastFeedEvent
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.models.PodcastFeedIntent
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.models.PodcastFeedState

class PodcastFeedViewModel(
    private val id: Long,
    private val podcastFeedRepository: PodcastFeedRepository,
    private val podcastEpisodeRepository: PodcastEpisodeRepository
) : BaseViewModel<PodcastFeedState, PodcastFeedState.PodcastFeedContent, PodcastFeedIntent, PodcastFeedEvent>(
    {
        PodcastFeedState.Loading to PodcastFeedState.PodcastFeedContent()
    }
) {

    init {
        collectPodcast()
        collectEpisodes()
        loadPodcastFeed()
        loadEpisodes()
    }

    override fun onIntent(intent: PodcastFeedIntent) {
        when (intent) {
            PodcastFeedIntent.ChangeSubscriptionStatus -> changeSubscriptionStatus()
        }
    }

    private fun changeSubscriptionStatus() {
        viewModelScope.launch {
            if (lastContentState.podcastInfo.subscribed) {
                podcastFeedRepository.unsubscribeOnPodcast(podcastId = id)
            } else {
                podcastFeedRepository.subscribeOnPodcast(podcastId = id)
            }
        }
    }

    private fun collectPodcast() {
        podcastFeedRepository.podcastFeedByIdFlow(id = id).filterNotNull().onEach { podcastFeed ->
            updateState(
                PodcastFeedState.PodcastFeedContent(
                    podcastInfo = podcastFeed.toPodcastInfo()
                )
            )
        }.launchIn(viewModelScope)
    }

    private fun collectEpisodes() {
        podcastEpisodeRepository.getPodcastEpisodesFlow(id).onEach {
            updateState(
                PodcastFeedState.PodcastFeedContent(
                    podcastInfo = lastContentState.podcastInfo.copy(
                        episodes = it.map {
                            it.toUi(
                                false
                            )
                        }
                    )
                )
            )
        }.launchIn(viewModelScope)
    }

    private fun loadPodcastFeed() {
        viewModelScope.launch {
            podcastFeedRepository.getPodcastFeedById(id = id).onFailure {
                updateState(PodcastFeedState.Failed(it))
            }
        }
    }

    private fun loadEpisodes() {
        viewModelScope.launch {
            podcastEpisodeRepository.getEpisodesByPodcastId(podcastId = id).onFailure {
                updateState(PodcastFeedState.Failed(it))
            }
        }
    }
}
