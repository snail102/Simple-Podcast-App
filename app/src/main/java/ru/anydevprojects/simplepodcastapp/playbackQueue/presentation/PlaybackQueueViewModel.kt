package ru.anydevprojects.simplepodcastapp.playbackQueue.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.anydevprojects.simplepodcastapp.core.ui.BaseViewModel
import ru.anydevprojects.simplepodcastapp.media.JetAudioServiceHandler
import ru.anydevprojects.simplepodcastapp.playbackQueue.presentation.models.PlaybackQueueEvent
import ru.anydevprojects.simplepodcastapp.playbackQueue.presentation.models.PlaybackQueueIntent
import ru.anydevprojects.simplepodcastapp.playbackQueue.presentation.models.PlaybackQueueState

class PlaybackQueueViewModel(
    private val jetAudioServiceHandler: JetAudioServiceHandler
) : BaseViewModel<
    PlaybackQueueState,
    PlaybackQueueState.Content,
    PlaybackQueueIntent,
    PlaybackQueueEvent
    >(
    {
        PlaybackQueueState.Loading to PlaybackQueueState.Content()
    }
) {

    init {
        jetAudioServiceHandler.playbackQueueList.onEach {
            Log.d("test", it.toString())
        }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: PlaybackQueueIntent) {
    }
}
