package ru.anydevprojects.simplepodcastapp.podcastFeed.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel
import ru.anydevprojects.simplepodcastapp.podcastFeed.domain.PodcastFeedRepository
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.mappers.toPodcastInfo
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.models.PodcastFeedEvent
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.models.PodcastFeedIntent
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.models.PodcastFeedState

class PodcastFeedViewModel(
    private val id: Int,
    private val podcastFeedRepository: PodcastFeedRepository
) : BaseViewModel<PodcastFeedState, PodcastFeedState.PodcastFeedContent, PodcastFeedIntent, PodcastFeedEvent>(
    {
        PodcastFeedState.Loading to PodcastFeedState.PodcastFeedContent()
    }
) {

    init {
        loadPodcastFeed()
    }

    override fun onIntent(intent: PodcastFeedIntent) {
        when (intent) {
            PodcastFeedIntent.ChangeSubscriptionStatus -> changeSubscriptionStatus()
        }
    }

    private fun changeSubscriptionStatus() {
        viewModelScope.launch {
            if (lastContentState.podcastInfo.subscribed) {
                podcastFeedRepository.unsubscribeOnPodcast(podcastId = id).onSuccess {
                    updateState(
                        lastContentState.copy(
                            podcastInfo = lastContentState.podcastInfo.copy(subscribed = false)
                        )
                    )
                }
            } else {
                podcastFeedRepository.subscribeOnPodcast(podcastId = id).onSuccess {
                    updateState(
                        lastContentState.copy(
                            podcastInfo = lastContentState.podcastInfo.copy(subscribed = true)
                        )
                    )
                }
            }
        }
    }

    private fun loadPodcastFeed() {
        viewModelScope.launch {
            podcastFeedRepository.getPodcastFeedById(id = id)
                .onSuccess { podcastFeed ->
                    updateState(
                        PodcastFeedState.PodcastFeedContent(
                            podcastInfo = podcastFeed.toPodcastInfo()
                        )
                    )
                }.onFailure {
                    updateState(PodcastFeedState.Failed(it))
                }
        }
    }
}
