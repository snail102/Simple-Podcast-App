package ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import ru.anydevprojects.simplepodcastapp.ui.components.EpisodeControlPanel
import ru.anydevprojects.simplepodcastapp.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
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

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            EpisodeTopBar(
                title = "title",
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        val modifier = Modifier
            .fillMaxSize()

        when (val localState = state) {
            is PodcastEpisodeState.Content -> EpisodeContent(
                state = localState,
                modifier = modifier,
                paddingValues = paddingValues
            )

            PodcastEpisodeState.Failed -> FailedContent(modifier = modifier)
            PodcastEpisodeState.Loading -> LoadingContent(modifier = modifier)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EpisodeTopBar(
    title: String,
    onBackClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = {
//            Text(
//                text = title
//            ),
        },
        scrollBehavior = scrollBehavior,
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
private fun EpisodeContent(
    state: PodcastEpisodeState.Content,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues()
) {
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
                .padding(top = paddingValues.calculateTopPadding())
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

        EpisodeControlPanel(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(color = Color.Gray)
                .padding(16.dp),
            isDownloaded = false,
            isAddedPlaylist = false,
            isPlaying = false,
            onDownloadControlClick = {},
            onAddPlaylistControlClick = {},
            onPlayControlClick = {}
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Описание",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))

        Card(
            modifier = Modifier
                .padding(bottom = paddingValues.calculateBottomPadding())
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
    AppTheme {
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
