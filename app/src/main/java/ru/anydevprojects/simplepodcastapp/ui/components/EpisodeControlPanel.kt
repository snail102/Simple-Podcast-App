package ru.anydevprojects.simplepodcastapp.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.anydevprojects.simplepodcastapp.R
import ru.anydevprojects.simplepodcastapp.ui.theme.AppTheme

@Composable
fun EpisodeControlPanel(
    isDownloaded: Boolean,
    isAddedPlaylist: Boolean,
    isPlaying: Boolean,
    onDownloadControlClick: () -> Unit,
    onAddPlaylistControlClick: () -> Unit,
    onPlayControlClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
    ) {
        DownloadControlIconBtn(
            modifier = Modifier.size(32.dp),
            isDownloaded = isDownloaded,
            tint = tint,
            onClick = {
                onDownloadControlClick()
            }
        )

        AddPlaybackListControlIconBtn(
            modifier = Modifier.size(32.dp),
            isAdded = isAddedPlaylist,
            tint = tint,
            onClick = {
                onAddPlaylistControlClick()
            }
        )

        PlayControlIconBtn(
            modifier = Modifier.size(32.dp),
            isPlaying = isPlaying,
            tint = tint,
            onClick = {
                onPlayControlClick()
            }
        )
    }
}

@Composable
private fun IconControlBtn(
    isActivate: Boolean,
    @DrawableRes activateIconResId: Int,
    @DrawableRes deactivateIconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    sizeIcon: Dp = 24.dp
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.size(sizeIcon),
            painter = painterResource(if (isActivate) activateIconResId else deactivateIconResId),
            contentDescription = null,
            tint = tint
        )
    }
}

@Composable
fun PlayControlIconBtn(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    sizeIcon: Dp = 24.dp
) {
    IconControlBtn(
        modifier = modifier,
        isActivate = isPlaying,
        activateIconResId = R.drawable.ic_pause,
        deactivateIconResId = R.drawable.ic_play,
        onClick = onClick,
        tint = tint,
        sizeIcon = sizeIcon
    )
}

@Composable
fun DownloadControlIconBtn(
    isDownloaded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    IconControlBtn(
        modifier = modifier,
        isActivate = isDownloaded,
        activateIconResId = R.drawable.ic_downloaded_done,
        deactivateIconResId = R.drawable.ic_download,
        onClick = onClick,
        tint = tint
    )
}

@Composable
fun AddPlaybackListControlIconBtn(
    isAdded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    IconControlBtn(
        modifier = modifier,
        isActivate = isAdded,
        activateIconResId = R.drawable.ic_add_list,
        deactivateIconResId = R.drawable.ic_added_list,
        onClick = onClick,
        tint = tint
    )
}

@Preview
@Composable
private fun EpisodeControlPanelPreview(
    @PreviewParameter(EpisodeControlPanelProvider::class) data: EpisodeControlPanelData
) {
    AppTheme {
        EpisodeControlPanel(
            isDownloaded = data.isDownloaded,
            isAddedPlaylist = data.isAddedPlaylist,
            isPlaying = data.isPlaying,
            onDownloadControlClick = {},
            onAddPlaylistControlClick = {},
            onPlayControlClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private class EpisodeControlPanelProvider :
    PreviewParameterProvider<EpisodeControlPanelData> {
    override val values: Sequence<EpisodeControlPanelData> = sequenceOf(
        EpisodeControlPanelData(
            isDownloaded = false,
            isAddedPlaylist = false,
            isPlaying = false
        ),
        EpisodeControlPanelData(
            isDownloaded = true,
            isAddedPlaylist = true,
            isPlaying = true
        ),
        EpisodeControlPanelData(
            isDownloaded = false,
            isAddedPlaylist = false,
            isPlaying = true
        )
    )
}

private data class EpisodeControlPanelData(
    val isDownloaded: Boolean,
    val isAddedPlaylist: Boolean,
    val isPlaying: Boolean
)
