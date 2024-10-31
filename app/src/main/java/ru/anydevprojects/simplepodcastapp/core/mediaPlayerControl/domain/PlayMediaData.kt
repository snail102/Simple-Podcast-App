package ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain

data class PlayMediaData(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val audioUrl: String,
    val duration: Int
)
