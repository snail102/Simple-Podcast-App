package ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain

sealed interface MediaData {

    data object Init : MediaData

    data object Loading : MediaData

    data class Content(
        val id: Long,
        val title: String,
        val imageUrl: String,
        val totalDurationMs: Long
    ) : MediaData
}
