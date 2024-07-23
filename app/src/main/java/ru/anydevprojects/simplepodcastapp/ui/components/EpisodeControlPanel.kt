package ru.anydevprojects.simplepodcastapp.ui.components

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
import androidx.compose.ui.unit.dp
import ru.anydevprojects.simplepodcastapp.R
import ru.anydevprojects.simplepodcastapp.ui.theme.SimplePodcastAppTheme

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
        IconButton(
            modifier = Modifier.size(32.dp),
            onClick = {
                onDownloadControlClick()
            }
        ) {
            Icon(
                if (isDownloaded) {
                    painterResource(R.drawable.ic_downloaded_done)
                } else {
                    painterResource(R.drawable.ic_download)
                },
                contentDescription = null,
                tint = tint
            )
        }

        IconButton(
            modifier = Modifier.size(32.dp),
            onClick = {
                onAddPlaylistControlClick()
            }
        ) {
            Icon(
                if (isAddedPlaylist) {
                    painterResource(R.drawable.ic_added_list)
                } else {
                    painterResource(R.drawable.ic_add_list)
                },
                contentDescription = null,
                tint = tint
            )
        }

        IconButton(
            modifier = Modifier.size(32.dp),
            onClick = {
                onPlayControlClick()
            }
        ) {
            Icon(
                if (isPlaying) {
                    painterResource(R.drawable.ic_pause)
                } else {
                    painterResource(R.drawable.ic_play)
                },
                contentDescription = null,
                tint = tint
            )
        }
    }
}

@Preview
@Composable
private fun EpisodeControlPanelPreview(
    @PreviewParameter(EpisodeControlPanelProvider::class) data: EpisodeControlPanelData
) {
    SimplePodcastAppTheme {
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
