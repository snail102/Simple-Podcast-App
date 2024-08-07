package ru.anydevprojects.simplepodcastapp.importPodcasts.domain

interface ImportPodcastsRepository {

    suspend fun import(filePath: String): Result<Unit>
}
