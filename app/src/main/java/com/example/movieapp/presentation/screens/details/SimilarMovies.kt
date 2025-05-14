package com.example.movieapp.presentation.screens.details

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.movieapp.data.remote.respond.MovieResponse
import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.Crew
import com.example.movieapp.presentation.components.MovieItemAllGenreScreen
import com.example.movieapp.ui.theme.background
import com.example.movieapp.util.Constants
import com.example.movieapp.util.MovieState

@Composable
fun SimilarMoviesScreen(navController: NavController, movieId: String, viewModel: MovieDetailsViewModel = hiltViewModel()) {

    Log.d("SimilarMovies", "$movieId 1")
    LaunchedEffect(movieId) {
        viewModel.fetchSimilarMovies(movieId)
    }
    Log.d("SimilarMovies", "$movieId 2")

    val similarMoviesState by viewModel.similarMoviesResponse.collectAsState()
    Log.d("SimilarMovies", "$movieId 3")

    Log.d("SimilarMovies", "$movieId 4")

    val genreMap = mapOf(
        28 to "Action", 12 to "Adventure", 16 to "Animation", 35 to "Comedy", 80 to "Crime",
        99 to "Documentary", 18 to "Drama", 10751 to "Family", 14 to "Fantasy", 36 to "History",
        27 to "Horror", 10402 to "Music", 9648 to "Mystery", 10749 to "Romance", 878 to "Sci-Fi",
        10770 to "TV Movie", 53 to "Thriller", 10752 to "War", 37 to "Western"
    )



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(background),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.Transparent)
        ) {
            Text(
                text = "Similar Movies",
                fontWeight = FontWeight.Medium,
                fontFamily = Constants.netflixFamily,
                fontSize = 22.sp,
                modifier = Modifier.align(Alignment.Center),
                color = Color.White.copy(alpha = 0.8f)
            )

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "IosBack",
                    tint = Color.White.copy(alpha = 0.8f)
                )

            }
        }
        Spacer(modifier = Modifier.height(15.dp))

        Log.d("SimilarMovies", "$movieId 5")

        when (similarMoviesState) {
            is MovieState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

            }

            is MovieState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = (similarMoviesState as MovieState.Error).message,
                        color = Color.Red,
                    )
                }

            }

            is MovieState.Success -> {
                val similarList = (similarMoviesState as MovieState.Success<MovieResponse?>).data
                val listState = rememberLazyGridState()
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if(similarList != null) {
                        val filteredSimilarList = similarList.results.filter { it.originalLanguage != "zh" && it.originalLanguage != "kn" && it.originalLanguage != "hi"}
                        items(similarList.results.size) { index ->
                            similarList.results[index].let {
                                val genre = it.genreIds?.firstOrNull()?.let { genreMap[it] } ?: "N/A"
                                MovieItemAllGenreScreen(movie = it, navController = navController, genre = genre)
                            }

                        }
                    }
                }
            }

            else -> {}
        }

    }



}
