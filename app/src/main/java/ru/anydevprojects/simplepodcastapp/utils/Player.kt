package ru.anydevprojects.simplepodcastapp.utils

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player

internal val Player.currentMediaItems: List<MediaItem> get() {
    return List(mediaItemCount, ::getMediaItemAt)
}

fun Player.playMediaAt(index: Int) {
    if (currentMediaItemIndex == index) {
        return
    }
    seekToDefaultPosition(index)
    playWhenReady = true
    // Recover from any errors that may have happened at previous media positions
    prepare()
}

fun Player.updatePlaylist(incoming: List<MediaItem>) {
    val oldMediaIds = currentMediaItems.map { it.mediaId }.toSet()
    val itemsToAdd = incoming.filterNot { item -> item.mediaId in oldMediaIds }
    Log.d("PlayerExt", "updatePlaylist: itemsToAdd: $itemsToAdd")
    addMediaItems(itemsToAdd)
}
