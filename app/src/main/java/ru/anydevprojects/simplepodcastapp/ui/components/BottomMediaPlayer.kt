package ru.anydevprojects.simplepodcastapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.anydevprojects.simplepodcastapp.ui.theme.SimplePodcastAppTheme

@Composable
fun BottomMediaPlayer(
    imageUrl: String,
    title: String,
    isPlaying: Boolean,
    onChangePlayState: () -> Unit,
    bottomPadding: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(80.dp + bottomPadding)
            .fillMaxWidth()
            .background(color = Color.Gray)
            .padding(16.dp)
            .padding(bottom = bottomPadding)
    ) {
        AsyncImage(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp)),
            model = imageUrl,
            contentDescription = null
        )
        Text(text = title, modifier = Modifier.weight(1f))
        IconButton(
            onClick = onChangePlayState
        ) {
            Icon(
                if (isPlaying) {
                    Icons.Filled.Edit
                } else {
                    Icons.Default.PlayArrow
                },
                contentDescription = null
            )
        }
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
            onChangePlayState = {}
        )
    }
}
