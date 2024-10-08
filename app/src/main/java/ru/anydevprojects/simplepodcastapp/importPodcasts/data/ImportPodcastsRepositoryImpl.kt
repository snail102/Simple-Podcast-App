package ru.anydevprojects.simplepodcastapp.importPodcasts.data

import android.content.Context
import android.net.Uri
import ru.anydevprojects.simplepodcastapp.importPodcasts.domain.ImportPodcastsRepository
import ru.anydevprojects.simplepodcastapp.opml.OpmlManager
import ru.anydevprojects.simplepodcastapp.podcastFeed.domain.PodcastFeedRepository

class ImportPodcastsRepositoryImpl(
    private val applicationContext: Context,
    private val podcastFeedRepository: PodcastFeedRepository,
    private val opmlManager: OpmlManager = OpmlManager
) : ImportPodcastsRepository {
    override suspend fun import(filePath: String): Result<Unit> {
        return getUrlsFromImportFile(filePath = filePath).mapCatching {
            podcastFeedRepository.importPodcasts(it)
        }
    }

    private fun getUrlsFromImportFile(filePath: String): Result<List<String>> {
        return runCatching {
            val inputStream = applicationContext.contentResolver.openInputStream(
                Uri.parse(filePath)
            )
            val urls = mutableListOf<String>()
            inputStream?.use {
                val content = it.bufferedReader().readText()
                opmlManager.decode(content).map { podcasts ->
                    podcasts.map { podcast ->
                        urls.add(podcast.url)
                    }
                }
            }
            urls
        }
    }
}
