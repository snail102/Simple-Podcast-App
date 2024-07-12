package ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel
import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.PodcastEpisodeRepository
import ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation.models.PodcastEpisodeEvent
import ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation.models.PodcastEpisodeIntent
import ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation.models.PodcastEpisodeState

class PodcastEpisodeViewModel(
    private val podcastName: String,
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
        loadEpisode()
    }

    override fun onIntent(intent: PodcastEpisodeIntent) {
    }

    private fun loadEpisode() {
        viewModelScope.launch {
            podcastEpisodeRepository.getEpisodeById(id = episodeId).onSuccess { episode ->
                updateState(
                    PodcastEpisodeState.Content(
                        title = episode.title
                    )
                )
            }.onFailure {
                updateState(PodcastEpisodeState.Failed)
            }
        }
    }
}
