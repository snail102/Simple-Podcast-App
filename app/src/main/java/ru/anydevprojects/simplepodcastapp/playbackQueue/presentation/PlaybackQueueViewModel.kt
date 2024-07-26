package ru.anydevprojects.simplepodcastapp.playbackQueue.presentation

import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel
import ru.anydevprojects.simplepodcastapp.playbackQueue.presentation.models.PlaybackQueueEvent
import ru.anydevprojects.simplepodcastapp.playbackQueue.presentation.models.PlaybackQueueIntent
import ru.anydevprojects.simplepodcastapp.playbackQueue.presentation.models.PlaybackQueueState

class PlaybackQueueViewModel : BaseViewModel<
    PlaybackQueueState,
    PlaybackQueueState.Content,
    PlaybackQueueIntent,
    PlaybackQueueEvent
    >(
    {
        PlaybackQueueState.Loading to PlaybackQueueState.Content()
    }
) {
    override fun onIntent(intent: PlaybackQueueIntent) {
    }
}
