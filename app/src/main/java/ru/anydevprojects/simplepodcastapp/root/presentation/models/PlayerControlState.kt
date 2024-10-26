package ru.anydevprojects.simplepodcastapp.root.presentation.models

data class PlayerControlState(
    val imageUrl: String = "",
    val title: String = "",
    val isPlaying: Boolean = false
)
