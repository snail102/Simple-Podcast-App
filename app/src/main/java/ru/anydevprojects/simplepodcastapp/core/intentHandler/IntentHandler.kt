package ru.anydevprojects.simplepodcastapp.core.intentHandler

import android.content.Intent

object IntentHandler {

    private var isHandledFromOnCreate: Boolean = false

    fun handleOnCreate(intent: Intent): ResolvedIntentData? {
        if (isHandledFromOnCreate) return null
        isHandledFromOnCreate = true
        return handleIntent(intent)
    }

    fun handleIntent(intent: Intent): ResolvedIntentData? {
        intent.extras?.getString("episodeId")?.toLongOrNull()?.let { episodeId ->
            return ResolvedIntentData.OpenEpisodeScreen(episodeId = episodeId)
        }

        return null
    }
}
