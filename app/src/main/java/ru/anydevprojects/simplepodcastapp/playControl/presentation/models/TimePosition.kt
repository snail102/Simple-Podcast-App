package ru.anydevprojects.simplepodcastapp.playControl.presentation.models

data class TimePosition(
    val currentTimeMs: Long = 0,
    val currentTime: String = "",
    val trackPosition: Float = 0f
)
