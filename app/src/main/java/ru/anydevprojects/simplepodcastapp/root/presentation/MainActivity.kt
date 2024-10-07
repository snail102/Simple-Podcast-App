package ru.anydevprojects.simplepodcastapp.root.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import nl.adaptivity.xmlutil.core.impl.multiplatform.name
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.anydevprojects.simplepodcastapp.authorization.presentaion.AuthorizationScreen
import ru.anydevprojects.simplepodcastapp.authorization.presentaion.AuthorizationScreenNavigation
import ru.anydevprojects.simplepodcastapp.home.presentation.HomeScreen
import ru.anydevprojects.simplepodcastapp.home.presentation.HomeScreenNavigation
import ru.anydevprojects.simplepodcastapp.playbackQueue.presentation.PlaybackQueueScreen
import ru.anydevprojects.simplepodcastapp.playbackQueue.presentation.PlaybackQueueScreenNavigation
import ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation.PodcastEpisodeScreen
import ru.anydevprojects.simplepodcastapp.podcastEpisode.presentation.PodcastEpisodeScreenNavigation
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.PodcastFeedScreen
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.PodcastFeedScreenNavigation
import ru.anydevprojects.simplepodcastapp.root.presentation.models.EventMain
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.SettingsScreen
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.SettingsScreenNavigation
import ru.anydevprojects.simplepodcastapp.ui.theme.SimplePodcastAppTheme
import ru.anydevprojects.simplepodcastapp.utils.rememberFlowWithLifecycle

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.stateInitialApp.value
            }
        }

        super.onCreate(savedInstanceState)

        Log.d("test", "onCreate")

        // startService(Intent(this, JetAudioService::class.java))
        // viewModel.getFCMToken()
        enableEdgeToEdge()
        Log.d("test", "before setContent")
        setContent {
            val navController = rememberNavController()

            val event = rememberFlowWithLifecycle(viewModel.event)

            val stateStartDestination by viewModel.startDestination.collectAsStateWithLifecycle()

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
                    }
                }
            }

            SimplePodcastAppTheme {
                SharedTransitionLayout(
                    modifier = Modifier
                ) {
                    stateStartDestination?.let {
                        NavHost(
                            navController = navController,
                            startDestination = it
                        ) {
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
                }
            }
        }
    }
}
