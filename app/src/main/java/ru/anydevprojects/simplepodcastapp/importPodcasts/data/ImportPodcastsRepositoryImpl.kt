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
        return kotlin.runCatching {
            val inputStream = applicationContext.contentResolver.openInputStream(
                Uri.parse(filePath)
            )
            inputStream?.use {
                val content = it.bufferedReader().readText()
                opmlManager.decode(content).map { podcasts ->
                    podcasts.map { podcast ->
                        if (podcastFeedRepository.getLocalPodcastFeedByUrl(
                                podcast.url
                            )?.subscribed != true
                        ) {
                            podcastFeedRepository.getPodcastFeedByUrl(podcast.url)
                                .onSuccess { podcastFeed ->
                                    podcastFeedRepository.subscribeOnPodcast(podcastFeed.id)
                                }
                        }
                    }
                    Unit
                }
            }
        }
    }
}
