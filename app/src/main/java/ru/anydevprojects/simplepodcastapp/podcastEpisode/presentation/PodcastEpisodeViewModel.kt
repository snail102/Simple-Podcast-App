package ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel
import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.PodcastEpisodeRepository
import ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation.models.PodcastEpisodeEvent
import ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation.models.PodcastEpisodeIntent
import ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation.models.PodcastEpisodeState

class PodcastEpisodeViewModel(
    private val episodeId: Long,
    private val podcastEpisodeRepository: PodcastEpisodeRepository
) : BaseViewModel<
    PodcastEpisodeState,
    PodcastEpisodeState.Content,
    PodcastEpisodeIntent,
    PodcastEpisodeEvent
    >(
    {
        PodcastEpisodeState.Loading to PodcastEpisodeState.Content()
    }
) {

    init {
        collectEpisode()
        loadEpisode()
    }

    override fun onIntent(intent: PodcastEpisodeIntent) {
    }

    private fun collectEpisode() {
        podcastEpisodeRepository.getPodcastEpisodeFlow(episodeId = episodeId)
            .filterNotNull()
            .onEach {
                updateState(
                    PodcastEpisodeState.Content(
                        title = it.title,
                        description = it.description,
                        podcastImageUrl = it.image
                    )
                )
            }.launchIn(viewModelScope)
    }

    private fun loadEpisode() {
        viewModelScope.launch {
            podcastEpisodeRepository.getEpisodeById(id = episodeId).onFailure {
                updateState(PodcastEpisodeState.Failed)
            }
        }
    }
}
