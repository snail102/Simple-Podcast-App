package ru.anydevprojects.simplepodcastapp.home.presentation.models

data class PodcastEpisodeUi(
    val id: Int,
    val title: String,
    val description: String,
    val isPlaying: Boolean,
    val imageUrl: String
)
