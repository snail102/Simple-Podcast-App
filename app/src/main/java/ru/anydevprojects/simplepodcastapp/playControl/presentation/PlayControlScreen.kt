package ru.anydevprojects.simplepodcastapp.playControl.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.anydevprojects.simplepodcastapp.playControl.presentation.models.TimePosition
import ru.anydevprojects.simplepodcastapp.ui.components.PlayControlIconBtn
import ru.anydevprojects.simplepodcastapp.ui.theme.SimplePodcastAppTheme

@Composable
fun PlayControlScreen() {
    PlayControlScreenContent(
        isPlaying = false,
        onClick = {},
        nameEpisode = "name",
        timePosition = mutableStateOf(
            TimePosition(
                currentTime = "1:1",
                totalTime = "2:34",
                trackPosition = 0.3F
            )
        ),
        coverUrl = "",
        onChangeCurrentPositionMedia = {}
    )
}

@Composable
private fun PlayControlScreenContent(
    nameEpisode: String,
    timePosition: State<TimePosition>,
    isPlaying: Boolean,
    coverUrl: String,
    onClick: () -> Unit,
    onChangeCurrentPositionMedia: (Float) -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .background(color = Color.LightGray)
                .padding(start = 16.dp, bottom = 16.dp, end = 16.dp, top = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                model = coverUrl,
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = nameEpisode,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(32.dp))

            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = timePosition.value.trackPosition,
                onValueChange = onChangeCurrentPositionMedia
            )

            Spacer(modifier = Modifier.height(64.dp))

            PlayControlIconBtn(
                modifier = Modifier.size(80.dp),
                sizeIcon = 64.dp,
                isPlaying = isPlaying,
                onClick = onClick
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun MiniPlayer(
    nameEpisode: String,
    timePosition: State<TimePosition>,
    isPlaying: Boolean,
    coverUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableFloatStateOf(timePosition.value.trackPosition) }
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = ""
    ).value

    Column(
        modifier = modifier
    ) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            progress = { animatedProgress },
            color = Color.Red
        )

        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(32.dp)),
                model = coverUrl,
                contentDescription = null
            )
            Text(
                modifier = Modifier.weight(1f),
                text = nameEpisode,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2
            )

            PlayControlIconBtn(
                isPlaying = isPlaying,
                onClick = onClick
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun MiniPlayerPreview() {
    SimplePodcastAppTheme {
        MiniPlayer(
            isPlaying = false,
            onClick = {},
            nameEpisode = "name",
            timePosition = mutableStateOf(
                TimePosition(
                    currentTime = "",
                    totalTime = "",
                    trackPosition = 0.01F
                )
            ),
            coverUrl = ""
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun PlayControlScreenContentPreview() {
    SimplePodcastAppTheme {
        PlayControlScreenContent(
            isPlaying = false,
            onClick = {},
            nameEpisode = "name",
            timePosition = mutableStateOf(
                TimePosition(
                    currentTime = "",
                    totalTime = "",
                    trackPosition = 0F
                )
            ),
            coverUrl = "",
            onChangeCurrentPositionMedia = {}
        )
    }
}
