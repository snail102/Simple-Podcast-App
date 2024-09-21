package ru.anydevprojects.simplepodcastapp.root.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.SettingsScreen
import ru.anydevprojects.simplepodcastapp.settings.mainSettings.presentation.SettingsScreenNavigation
import ru.anydevprojects.simplepodcastapp.ui.theme.SimplePodcastAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // startService(Intent(this, JetAudioService::class.java))
        // viewModel.getFCMToken()
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            SimplePodcastAppTheme {
                SharedTransitionLayout(
                    modifier = Modifier
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = AuthorizationScreenNavigation
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
