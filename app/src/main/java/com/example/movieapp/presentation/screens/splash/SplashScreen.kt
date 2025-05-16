package com.example.movieapp.presentation.screens.splash

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.movieapp.R
import com.example.movieapp.navigation.Screen
import com.example.movieapp.presentation.screens.home.HomeViewModel
import com.example.movieapp.ui.theme.background
import com.example.movieapp.util.MovieState
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val topRatedState by viewModel.topRatedMovieResponse.collectAsState()
    val genresState by viewModel.genresMovieResponse.collectAsState()

    val rotation = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = tween(
                durationMillis = 1500,
                delayMillis = 400
            )
        )
    }


    LaunchedEffect(Unit) {

        delay(2500)
        navController.navigate(Screen.Main.route) {
            popUpTo(Screen.Launch.route) { inclusive = true }

        }
    }


    Box(
        modifier = Modifier.fillMaxSize().background(background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.clapperboard),
                contentDescription = "App Logo",
                modifier = Modifier.size(175.dp).rotate(rotation.value)
            )
        }
    }

} 