package ru.anydevprojects.simplepodcastapp.exportPodcasts.domain

interface ExportPodcastsRepository {

    suspend fun export(folderPath: String): Result<Unit>
}
