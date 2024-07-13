package ru.anydevprojects.simplepodcastapp.home.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class PodcastsSubscriptions(
    val podcasts: List<PodcastSubscriptionUi> = emptyList()
) : HomeScreenItem
