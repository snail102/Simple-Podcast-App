package ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation.models.PodcastEpisodeState
import ru.anydevprojects.simplepodcastapp.ui.theme.SimplePodcastAppTheme

@Composable
fun PodcastEpisodeScreen(
    episodeId: Long,
    onBackClick: () -> Unit,
    viewModel: PodcastEpisodeViewModel = koinViewModel(
        parameters = {
            parametersOf(episodeId)
        }
    )
) {
    val state by viewModel.stateFlow.collectAsState()

    Scaffold(
        topBar = {
            EpisodeTopBar(
                title = "title",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        val modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)

        when (val localState = state) {
            is PodcastEpisodeState.Content -> EpisodeContent(
                state = localState,
                modifier = modifier
            )

            PodcastEpisodeState.Failed -> FailedContent(modifier = modifier)
            PodcastEpisodeState.Loading -> LoadingContent(modifier = modifier)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EpisodeTopBar(title: String, onBackClick: () -> Unit) {
    TopAppBar(
        title = {
//            Text(
//                text = title
//            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun EpisodeContent(state: PodcastEpisodeState.Content, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val annotatedText = remember(state.description) {
        val spanned = HtmlCompat.fromHtml(state.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
        val text = spanned.toString()
        buildAnnotatedString {
            append(text)
        }
    }


    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Gray)
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(color = Color.Red)
                .padding(16.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp)),
                model = state.podcastImageUrl,
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                modifier = Modifier,
                text = state.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.W500
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(color = Color.Gray)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            IconButton(
                modifier = Modifier.size(32.dp),
                onClick = {
                }
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null
                )
            }
            IconButton(
                modifier = Modifier.size(32.dp),
                onClick = {
                }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null
                )
            }
            IconButton(
                modifier = Modifier.size(32.dp),
                onClick = {
                }
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Описание",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors().copy(containerColor = Color.White)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = annotatedText,
                fontSize = 15.sp,
                fontWeight = FontWeight.W400
            )
        }
    }
}

@Preview
@Composable
private fun EpisodeContentPreview() {
    SimplePodcastAppTheme {
        EpisodeContent(
            state = PodcastEpisodeState.Content(
                title = "title",
                description = "huashabsdausbd sabd asdba sdab diabsdjab dadbn",
                dateTimestamp = 0,
                podcastImageUrl = ""
            )
        )
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
}

@Composable
private fun FailedContent(modifier: Modifier = Modifier) {
}
