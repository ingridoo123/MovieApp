package com.example.movieapp.presentation.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.movieapp.R
import com.example.movieapp.navigation.Screen
import com.example.movieapp.presentation.screens.home.HomeViewModel
import com.example.movieapp.util.MovieState
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val popularMoviesState by viewModel.popularMovieResponse.collectAsState()
    val trendingMoviesState by viewModel.trendingMovieResponse.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )


            CircularProgressIndicator(
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }


    LaunchedEffect(popularMoviesState, trendingMoviesState) {

        delay(1250)


            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Launch.route) { inclusive = true }
            }

    }
} 