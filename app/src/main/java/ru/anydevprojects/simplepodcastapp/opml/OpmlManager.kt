package ru.anydevprojects.simplepodcastapp.opml

import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.serialization.XML
import ru.anydevprojects.simplepodcastapp.home.domain.model.PodcastFeed
import ru.anydevprojects.simplepodcastapp.opml.models.PodcastOpml

object OpmlManager {

    private val xml = XML {
        autoPolymorphic = true
        indentString = "  "
        defaultPolicy {
            pedantic = false
            ignoreUnknownChildren()
        }
    }

    fun decode(contentOpml: String): Result<List<PodcastOpml>> {
        return kotlin.runCatching {
            val opml = xml.decodeFromString(serializer<Opml>(), contentOpml)
            val podcastOpmlList = mutableListOf<PodcastOpml>()

            fun flatten(outline: Outline) {
                if (outline.outlines.isNullOrEmpty() && !outline.xmlUrl.isNullOrBlank()) {
                    outline.convertToPodcastOpml()?.let {
                        podcastOpmlList.add(it)
                    }
                }

                outline.outlines?.forEach { nestedOutline -> flatten(nestedOutline) }
            }

            opml.body.outlines.forEach { outline -> flatten(outline) }
            podcastOpmlList
        }
    }

    fun encode(podcastFeeds: List<PodcastFeed>): Result<String> {
        return kotlin.runCatching {
            val opml = Opml(
                version = "2.0",
                head = Head("Simple Podcast App Subscriptions", dateCreated = null),
                body = Body(outlines = podcastFeeds.map { it.convertToOutline() })
            )

            val xmlString = xml.encodeToString(serializer<Opml>(), opml)

            StringBuilder(xmlString)
                .insert(0, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n")
                .appendLine()
                .toString()
        }
    }
}

private fun Outline.convertToPodcastOpml(): PodcastOpml? {
    return if (this.title != null && this.xmlUrl != null) {
        PodcastOpml(
            title = this.title,
            url = this.xmlUrl
        )
    } else {
        null
    }
}

private fun PodcastFeed.convertToOutline(): Outline {
    return Outline(
        title = this.title,
        text = this.title,
        type = "rss",
        xmlUrl = this.url,
        htmlUrl = "",
        outlines = null
    )
}
