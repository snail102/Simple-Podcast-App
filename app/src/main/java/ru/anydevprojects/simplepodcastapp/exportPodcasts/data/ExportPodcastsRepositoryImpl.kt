package ru.anydevprojects.simplepodcastapp.exportPodcasts.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import java.io.OutputStreamWriter
import ru.anydevprojects.simplepodcastapp.exportPodcasts.domain.ExportPodcastsRepository
import ru.anydevprojects.simplepodcastapp.opml.OpmlManager
import ru.anydevprojects.simplepodcastapp.podcastFeed.domain.PodcastFeedRepository

class ExportPodcastsRepositoryImpl(
    private val applicationContext: Context,
    private val podcastFeedRepository: PodcastFeedRepository,
    private val opmlManager: OpmlManager = OpmlManager
) : ExportPodcastsRepository {
    override suspend fun export(folderPath: String): Result<Unit> = kotlin.runCatching {
        val fileName: String = StringBuilder().run {
            append(PREFIX_FILE_NAME)
            append("_")
            append(System.currentTimeMillis())
        }.toString()

        val uri: Uri = folderPath.toUri()

        val podcastFeeds = podcastFeedRepository.getAllSubscriptionPodcasts()

        val exportData = opmlManager.encode(podcastFeeds)

        exportData.map { exportDataContent ->
            saveTextFileToFolder(
                context = applicationContext,
                folderUri = uri,
                fileName = fileName,
                fileContent = exportDataContent
            )
        }
    }

    private fun saveTextFileToFolder(
        context: Context,
        folderUri: Uri,
        fileName: String,
        fileContent: String
    ) {
        val pickedDir = DocumentFile.fromTreeUri(context, folderUri)

        if (pickedDir != null && pickedDir.isDirectory) {
            val newFile = pickedDir.createFile("text/xml", fileName)

            newFile?.uri?.let { uri ->
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(fileContent)
                        writer.flush()
                    }
                }
            } ?: run {
                println("Ошибка создания файла")
            }
        } else {
            println("Ошибка доступа к папке")
        }
    }

    companion object {
        private const val PREFIX_FILE_NAME = "export_podcasts"
    }
}
