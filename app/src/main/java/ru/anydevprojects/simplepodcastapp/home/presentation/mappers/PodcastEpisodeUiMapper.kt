package ru.anydevprojects.simplepodcastapp.home.presentation.mappers

import ru.anydevprojects.simplepodcastapp.home.presentation.models.PodcastEpisodeUi
import ru.anydevprojects.simplepodcastapp.podcastEpisode.domain.models.PodcastEpisode

fun PodcastEpisode.toUi(isPlaying: Boolean): PodcastEpisodeUi {
    return PodcastEpisodeUi(
        id = this.id,
        title = this.title,
        description = this.description,
        isPlaying = isPlaying,
        imageUrl = this.feedImage,
        audioUrl = this.enclosureUrl
    )
}
