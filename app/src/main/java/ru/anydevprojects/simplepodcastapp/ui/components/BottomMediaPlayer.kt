package ru.anydevprojects.simplepodcastapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.anydevprojects.simplepodcastapp.R
import ru.anydevprojects.simplepodcastapp.ui.theme.SimplePodcastAppTheme

@Composable
fun BottomMediaPlayer(
    imageUrl: String,
    title: String,
    isPlaying: Boolean,
    availablePlaybackQueue: Boolean,
    modifier: Modifier = Modifier,
    onChangePlayState: () -> Unit,
    onPlaybackQueueBtnClick: () -> Unit,
    bottomPadding: Dp = 0.dp
) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(color = Color.Gray)
                .padding(16.dp)
                .padding(bottom = bottomPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp)),
                model = imageUrl,
                contentDescription = null
            )
            Text(
                text = title,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                overflow = TextOverflow.Ellipsis,
                minLines = 2,
                maxLines = 2
            )
            if (availablePlaybackQueue) {
                IconButton(
                    modifier = Modifier
                        .padding(end = 16.dp),
                    onClick = {
                        onPlaybackQueueBtnClick()
                    }
                ) {
                    Icon(
                        modifier = Modifier,
                        painter = painterResource(R.drawable.ic_queue_music),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            PlayControlIconBtn(
                modifier = Modifier,
                isPlaying = isPlaying,
                onClick = onChangePlayState,
                sizeIcon = 48.dp
            )
        }

}

@Preview
@Composable
private fun BottomMediaPlayerPreview() {
    SimplePodcastAppTheme {
        BottomMediaPlayer(
            imageUrl = "",
            title = "title",
            isPlaying = true,
            onChangePlayState = {},
            onPlaybackQueueBtnClick = {},
            bottomPadding = 0.dp,
            availablePlaybackQueue = true
        )
    }
}
