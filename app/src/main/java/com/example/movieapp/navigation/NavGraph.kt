package com.example.movieapp.navigation

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.core.graphics.rotationMatrix
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.movieapp.presentation.screens.all_screen.AllGenresScreen
import com.example.movieapp.presentation.screens.details.MovieDetailsScreen

import com.example.movieapp.presentation.screens.home.SimpleHomeScreen
import com.example.movieapp.presentation.screens.splash.SplashScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Launch.route
    ) {
        composable(route = Screen.Launch.route) {
            SplashScreen(navController = navController)
        }
        composable(route = Screen.Welcome.route) {

        }
        composable(route = Screen.Home.route) {
            //HomeScreen(navController = navController)
            SimpleHomeScreen(navController = navController)

        }
        composable(
            route = Screen.Details.route + "/{movieId}",
            arguments = listOf(navArgument("movieId") {type = NavType.StringType}),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 300)
                )
            }
        ) {
            Log.d("PASSED_KEY", "RootNavigation: " + it.arguments?.getString("movieId"))
            MovieDetailsScreen(navController = navController, movieId = it.arguments?.getString("movieId") ?: "1")

        }
        composable(
            route = Screen.AllMovies.route + "/{seeAllTags}",
            arguments = listOf(navArgument("seeAllTags") {type = NavType.StringType})
        ) {
            Log.d("PASSED_KEY_All", "RootNavigation: " + it.arguments?.getString("seeAllTags"))
        }
        composable(
            route = Screen.GenreWise.route + "/{genId}" + "/{genName}",
            arguments = listOf(
                navArgument("genId") {type = NavType.StringType},
                navArgument("genName") {type = NavType.StringType}
            )
        ) {
            AllGenresScreen(
                navController = navController ,
                genId = it.arguments?.getString("genId") ?: "1",
                genName = it.arguments?.getString("genName") ?: ""
            )

        }
        composable(
            route = Screen.Search.route
        ) {

        }
        composable(
            route = Screen.WatchList.route
        ) {

        }
        composable(
            route = Screen.About.route
        ) {

        }
    }

}