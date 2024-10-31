package ru.anydevprojects.simplepodcastapp.root.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.Flow
import nl.adaptivity.xmlutil.core.impl.multiplatform.name
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.anydevprojects.simplepodcastapp.authorization.presentaion.AuthorizationScreen
import ru.anydevprojects.simplepodcastapp.authorization.presentaion.AuthorizationScreenNavigation
import ru.anydevprojects.simplepodcastapp.core.intentHandler.IntentHandler
import ru.anydevprojects.simplepodcastapp.core.navigation.Screen
import ru.anydevprojects.simplepodcastapp.home.presentation.HomeScreen
import ru.anydevprojects.simplepodcastapp.home.presentation.HomeScreenNavigation
import ru.anydevprojects.simplepodcastapp.playControl.presentation.MiniPlayer
import ru.anydevprojects.simplepodcastapp.playControl.presentation.PlayControlScreen
import ru.anydevprojects.simplepodcastapp.playControl.presentation.models.TimePosition
import ru.anydevprojects.simplepodcastapp.playbackQueue.presentation.PlaybackQueueScreen
import ru.anydevprojects.simplepodcastapp.playbackQueue.presentation.PlaybackQueueScreenNavigation
import ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation.PodcastEpisodeScreen
import ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation.PodcastEpisodeScreenNavigation
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.PodcastFeedScreen
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.PodcastFeedScreenNavigation
import ru.anydevprojects.simplepodcastapp.root.presentation.models.EventMain
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.SettingsScreen
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.SettingsScreenNavigation
import ru.anydevprojects.simplepodcastapp.ui.components.bottomsheet.SheetCollapsed
import ru.anydevprojects.simplepodcastapp.ui.components.bottomsheet.SheetContent
import ru.anydevprojects.simplepodcastapp.ui.components.bottomsheet.SheetExpanded
import ru.anydevprojects.simplepodcastapp.ui.components.bottomsheet.currentFraction
import ru.anydevprojects.simplepodcastapp.ui.theme.AppTheme
import ru.anydevprojects.simplepodcastapp.utils.pxToDp
import ru.anydevprojects.simplepodcastapp.utils.rememberFlowWithLifecycle

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()

    @OptIn(
        ExperimentalSharedTransitionApi::class,
        ExperimentalMaterial3Api::class,
        ExperimentalMaterialApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.stateInitialApp.value
            }
        }

        super.onCreate(savedInstanceState)

        IntentHandler.handleOnCreate(intent)?.let {
            viewModel.handleIntentData(it)
        }

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            val event = rememberFlowWithLifecycle(viewModel.event)

            val stateStartDestination by viewModel.startDestination.collectAsStateWithLifecycle()

            val density = LocalDensity.current

            val playerControlState by viewModel.playerControlState.collectAsStateWithLifecycle()

            val navigationBars = WindowInsets.navigationBars.getBottom(density).pxToDp()

            val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = rememberBottomSheetState(
                    initialValue = BottomSheetValue.Collapsed
                )
            )

            var radiusBottomSheet by remember {
                mutableStateOf(32.dp)
            }

            LaunchedEffect(bottomSheetScaffoldState.bottomSheetState.progress) {
                snapshotFlow { bottomSheetScaffoldState.bottomSheetState }.collect {
                    Log.d("bottomSheet", "${bottomSheetScaffoldState.currentFraction}")
                    kotlin.runCatching {
                        if (bottomSheetScaffoldState.currentFraction > 0.99) {
                            radiusBottomSheet = 0.dp
                        } else {
                            radiusBottomSheet = 32.dp
                        }
                    }
                }
            }

            AppTheme {
                SharedTransitionLayout(
                    modifier = Modifier
                ) {
                    stateStartDestination?.let { startDestination ->
                        BottomSheetScaffold(
                            scaffoldState = bottomSheetScaffoldState,
                            sheetShape = RoundedCornerShape(
                                topStart = radiusBottomSheet,
                                topEnd = radiusBottomSheet
                            ),
                            sheetPeekHeight = if (playerControlState.isEnabled) {
                                100.dp
                            } else {
                                0.dp
                            },
                            sheetContent = {
                                if (playerControlState.isEnabled) {
                                    SheetContent(
                                        heightFraction = 1f
                                    ) {
                                        SheetExpanded(
                                            currentFraction = bottomSheetScaffoldState.currentFraction
                                        ) {
                                            PlayControlScreen()
                                        }
                                        SheetCollapsed(
                                            isCollapsed = bottomSheetScaffoldState.bottomSheetState.isCollapsed,
                                            currentFraction = bottomSheetScaffoldState.currentFraction,
                                            onSheetClick = {}
                                        ) {
                                            MiniPlayer(
                                                modifier = Modifier
                                                    .clip(
                                                        RoundedCornerShape(
                                                            topStart = 32.dp,
                                                            topEnd = 32.dp
                                                        )
                                                    )
                                                    .height(100.dp),
                                                paddingValues = PaddingValues(
                                                    bottom = navigationBars
                                                ),
                                                isPlaying = playerControlState.isPlaying,
                                                onClick = {},
                                                nameEpisode = playerControlState.title,
                                                timePosition = mutableStateOf(
                                                    TimePosition(
                                                        currentTime = "",
                                                        trackPosition = 0.01F
                                                    )
                                                ),
                                                coverUrl = playerControlState.imageUrl
                                            )
                                        }
                                    }
                                }
                            }
                        ) { paddingValues ->
                            AppHost(
                                navController = navController,
                                stateStartDestination = startDestination,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        IntentHandler.handleIntent(intent)?.let {
            viewModel.handleIntentData(it)
        }
    }
}

@Composable
private fun AppHost(
    navController: NavHostController,
    stateStartDestination: Screen,
    viewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = stateStartDestination
    ) {
        viewModel.startDestinationInitialized()

        composable<AuthorizationScreenNavigation> {
            AuthorizationScreen()
        }

        composable<HomeScreenNavigation> {
            HomeScreen(
                animatedVisibilityScope = this@composable,
                onPodcastClick = { id ->
                    navController.navigate(
                        PodcastFeedScreenNavigation(podcastId = id)
                    )
                },
                onEpisodeClick = { episodeId ->
                    navController.navigate(
                        PodcastEpisodeScreenNavigation(
                            episodeId = episodeId
                        )
                    )
                },
                onPlaybackQueueClick = {
                    navController.navigate(
                        PlaybackQueueScreenNavigation
                    )
                },
                openSettings = {
                    navController.navigate(
                        SettingsScreenNavigation
                    )
                }
            )
        }
        composable<PodcastFeedScreenNavigation> {
            val args = it.toRoute<PodcastFeedScreenNavigation>()
            PodcastFeedScreen(
                podcastId = args.podcastId,
                onNavigateToEpisodeDetail = {
                    navController.navigate(
                        PodcastEpisodeScreenNavigation(episodeId = it)
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<PodcastEpisodeScreenNavigation> {
            val args = it.toRoute<PodcastEpisodeScreenNavigation>()
            PodcastEpisodeScreen(
                episodeId = args.episodeId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<PlaybackQueueScreenNavigation> {
            PlaybackQueueScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<SettingsScreenNavigation> {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun CollectEvent(event: Flow<EventMain>, navController: NavHostController) {
    LaunchedEffect(event) {
        event.collect { event ->
            when (event) {
                EventMain.NavigateToAuthorization -> {
                    if (navController.currentBackStackEntry?.destination?.route != AuthorizationScreenNavigation::class.name) {
                        navController.navigate(AuthorizationScreenNavigation) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }

                EventMain.NavigateToHome -> {
                    if (navController.currentBackStackEntry?.destination?.route == AuthorizationScreenNavigation::class.name) {
                        navController.navigate(
                            HomeScreenNavigation
                        ) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }

                is EventMain.NavigateToEpisode -> {
                    Log.d("NavigateToEpisode", "navigateToEpisode ${event.id}")
                    navController.navigate(
                        PodcastEpisodeScreenNavigation(episodeId = event.id)
                    ) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}
