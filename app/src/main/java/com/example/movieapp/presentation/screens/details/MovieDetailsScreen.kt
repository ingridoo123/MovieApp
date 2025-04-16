package com.example.movieapp.presentation.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.movieapp.data.remote.respond.MovieDetailsDTO
import com.example.movieapp.presentation.components.MovieDataItem
import com.example.movieapp.presentation.components.MovieDataItemEmpty
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.component
import com.example.movieapp.util.Constants.netflixFamily
import com.example.movieapp.util.MovieState

@Composable
fun MovieDetailsScreen(navController: NavController, movieId: String, viewModel: MovieDetailsViewModel = hiltViewModel()) {

    val detailsMovieState by viewModel.detailsMovieResponse.collectAsState()
    val movieCastState by viewModel.movieCastResponse.collectAsState()
    val similarMovieState by viewModel.similarMoviesResponse.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchMovieDetails(movieId)
        viewModel.fetchCastOfMovie(movieId)
        viewModel.fetchSimilarMovies(movieId)
    }

   Column(
       modifier = Modifier
           .fillMaxSize()
           .background(background)
           .verticalScroll(rememberScrollState())
   ) {
       when (detailsMovieState) {
           is MovieState.Success -> {
               val moviesInfo = (detailsMovieState as MovieState.Success<MovieDetailsDTO?>).data
               Column(modifier = Modifier
                   .fillMaxSize()
               ) {
                   MovieDataItem(movieInfo = moviesInfo, navController = navController)
                   Row(
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(horizontal = 20.dp),
                       horizontalArrangement = Arrangement.Center
                   ) {
                       Text(
                           text = moviesInfo?.title ?: "",
                           fontSize = 22.sp,
                           color = Color.White.copy(alpha = 0.8f),
                           fontFamily = netflixFamily,
                           fontWeight = FontWeight.Medium,
                           textAlign = TextAlign.Center,
                           modifier = Modifier.fillMaxWidth()
                       )
                   }
                   Spacer(modifier = Modifier.height(15.dp))
                   Row(
                       modifier = Modifier.fillMaxWidth()
                   ) {

                   }

               }

           }

           is MovieState.Error -> {
               val errorMessage = (detailsMovieState as MovieState.Error).message
               Text(text = errorMessage, fontSize = 30.sp, color = Color.Red)
           }

           is MovieState.Loading -> {
               Box(
                   modifier = Modifier
                       .fillMaxSize()
                       .padding(30.dp),
                   contentAlignment = Alignment.Center
               ) {
                   MovieDataItemEmpty(navController = navController)
               }
           }
       }
   }


}

