package ru.anydevprojects.simplepodcastapp.root.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ru.anydevprojects.simplepodcastapp.home.presentation.HomeScreen
import ru.anydevprojects.simplepodcastapp.home.presentation.HomeScreenNavigation
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.PodcastFeedScreen
import ru.anydevprojects.simplepodcastapp.podcastFeed.presentation.PodcastFeedScreenNavigation
import ru.anydevprojects.simplepodcastapp.ui.theme.SimplePodcastAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            SimplePodcastAppTheme {
                NavHost(
                    navController = navController,
                    startDestination = HomeScreenNavigation
                ) {
                    composable<HomeScreenNavigation> {
                        HomeScreen(
                            onPodcastClick = { id ->
                                navController.navigate(PodcastFeedScreenNavigation(podcastId = id))
                            }
                        )
                    }
                    composable<PodcastFeedScreenNavigation> {
                        val args = it.toRoute<PodcastFeedScreenNavigation>()
                        PodcastFeedScreen(
                            podcastId = args.podcastId
                        )
                    }
                }
            }
        }
    }
}
