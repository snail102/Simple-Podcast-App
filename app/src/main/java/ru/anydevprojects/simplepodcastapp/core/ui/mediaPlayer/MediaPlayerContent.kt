package ru.anydevprojects.simplepodcastapp.core.ui.mediaPlayer

data class MediaPlayerContent(
    val enabled: Boolean = false,
    val imageUrl: String = "",
    val title: String = "",
    val isPlaying: Boolean = false,
    val enabledPlaybackQueue: Boolean = false
)
