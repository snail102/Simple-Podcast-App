package ru.anydevprojects.simplepodcastapp.core.intentHandler

sealed interface ResolvedIntentData {

    data class OpenEpisodeScreen(val episodeId: Long) : ResolvedIntentData
}
