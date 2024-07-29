package ru.anydevprojects.simplepodcastapp.playbackQueue.domain.models

data class MediaQueueItem(
    val id: Long,
    val episodeName: String,
    val podcastName: String,
    val url: String
)
