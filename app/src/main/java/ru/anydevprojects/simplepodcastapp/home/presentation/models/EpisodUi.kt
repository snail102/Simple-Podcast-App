package ru.anydevprojects.simplepodcastapp.home.presentation.models

data class PodcastEpisodeUi(
    val id: Long,
    val title: String,
    val description: String,
    val isPlaying: Boolean,
    val imageUrl: String,
    val audioUrl: String
) : HomeScreenItem
