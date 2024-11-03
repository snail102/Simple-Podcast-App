package ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain

import kotlinx.coroutines.flow.StateFlow

interface MediaPlayerControl {

    val currentMedia: StateFlow<MediaData>

    val progress: StateFlow<Float>

    val currentDuration: StateFlow<Long>

    val currentMediaContent: MediaData.Content?

    val playState: StateFlow<PlayState>

    suspend fun moveTo(timeMs: Long)

    fun play(playMediaData: PlayMediaData)

    fun pause()

    fun changePlayStatus()

    fun reset()
}
