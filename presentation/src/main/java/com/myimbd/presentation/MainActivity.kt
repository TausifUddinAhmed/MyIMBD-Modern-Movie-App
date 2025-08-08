package com.myimbd.presentation

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.myimbd.presentation.customcomposables.sharedelements.SharedElementsRoot
import com.myimbd.presentation.models.*
import com.myimbd.presentation.theme.MyIMDBTheme
import com.myimbd.presentation.theme.StocksDarkPrimaryText
import com.myimbd.presentation.theme.StocksDarkSelectedCard
import com.myimbd.presentation.theme.StocksDarkTopAppBarCollapsed
import com.myimbd.presentation.ui.movie_details.MovieDetailsScreen
import com.myimbd.presentation.ui.movielist.MovieListScreen
import com.myimbd.presentation.ui.splash.SplashScreen
import com.myimbd.presentation.ui.wishlist.WishlistScreen
import dagger.hilt.android.AndroidEntryPoint
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.popAll
import dev.olshevski.navigation.reimagined.rememberNavController

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Platform splash
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            MyIMDBTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(
                        onSuccess = { showSplash = false } // enter app only when API succeeded
                    )
                } else {
                    MainApp(
                    )
                }
            }
        }


    }
}


@Composable
fun MainApp(
) {

    val context = LocalContext.current
    val activity = context as? Activity
    val navController = rememberNavController<Screen>(startDestination = Screen.MovieList)
    NavBackHandler(navController)

    val isRoot by remember { androidx.compose.runtime.derivedStateOf { navController.backstack.entries.size == 1 } }


    BackHandler(enabled = isRoot) {
        activity?.finish()
    }

    val currentDestination by remember { androidx.compose.runtime.derivedStateOf { navController.backstack.entries.first().destination } }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = StocksDarkTopAppBarCollapsed) {
                BottomNavigationItem.entries.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = stringResource(id = item.title)) },
                        label = { Text(stringResource(id = item.title)) },
                        selected = item.route == currentDestination,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = StocksDarkPrimaryText,
                            indicatorColor = StocksDarkSelectedCard
                        ),
                        onClick = {
                            if (item.route != currentDestination) {
                                navController.popAll()
                                navController.navigate(item.route)
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        Surface(Modifier.padding(padding)) {
            SharedElementsRoot {
                NavHost(controller = navController) { route ->
                    when (route) {
                        is Screen.MovieList -> MovieListScreen(
                            viewModel = hiltViewModel(),
                            onMovieClick = { movie ->
                                navController.navigate(Screen.MovieDetail(movie.id))
                            }
                        )
                        is Screen.WishList -> WishlistScreen(hiltViewModel())
                        is Screen.MovieDetail -> MovieDetailsScreen(
                            movieId = route.movieId,
                            viewModel = hiltViewModel(),
                            onBack = { navController.pop() }
                        )
                    }
                }
            }
        }    }

}
