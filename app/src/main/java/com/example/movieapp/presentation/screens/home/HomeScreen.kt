package com.example.movieapp.presentation.screens.home

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.movieapp.data.remote.respond.GenreResponse
import com.example.movieapp.data.remote.respond.MovieDetailsDTO
import com.example.movieapp.data.remote.respond.MovieResponse
import com.example.movieapp.domain.model.Genre
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.navigation.Screen
import com.example.movieapp.presentation.components.AnimatedShimmerItem
import com.example.movieapp.presentation.components.AutoSlidingCarousel
import com.example.movieapp.presentation.components.GenreBlock
import com.example.movieapp.presentation.components.MovieItemLoadingPlaceholder
import com.example.movieapp.presentation.components.MovieItemSmallSimilar
import com.example.movieapp.presentation.components.NoInternetScreen
import com.example.movieapp.presentation.components.SeriesItemSmallSimilar
import com.example.movieapp.presentation.components.ShimmerDarkGray
import com.example.movieapp.presentation.components.TopRatedSeriesItem
import com.example.movieapp.presentation.components.TopRatedSeriesItemShimmer
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.util.Constants.netflixFamily
import com.example.movieapp.util.MovieState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.delay

@Composable
fun SimpleHomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val popularMoviesLazy = viewModel.popularAllListState.collectAsLazyPagingItems()
    val refreshTrigger by viewModel.refreshTrigger.collectAsState()
    val topRatedMovies by viewModel.topRatedMovieResponse.collectAsState()
    val movieDetailsMap by viewModel.movieDetailsMap.collectAsState()
    val allDetailsLoaded by viewModel.allDetailsLoaded.collectAsState()
    val recommendedMovies by viewModel.recommendedMovies.collectAsState()
    val recommendedSeries by viewModel.recommendedSeries.collectAsState()
    val topRatedSeries by viewModel.topRatedSeries.collectAsState()

    var preparedMovies by remember { mutableStateOf(viewModel.cachedFilteredMovies.value) }

    var isLoadingDetails by remember {
        mutableStateOf(true)
    }

    var selectedMediaType by remember { mutableStateOf<String?>(null)}


    LaunchedEffect(refreshTrigger) {
        popularMoviesLazy.refresh()
    }
    Log.d("HOMEMOVIES", preparedMovies.map{ it.title }.toString())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = background),
        contentPadding = PaddingValues(bottom = 100.dp)

    ) {
        item {
            when(topRatedMovies) {
                is MovieState.Success -> {
                    val movies = (topRatedMovies as MovieState.Success<MovieResponse?>).data?.results.orEmpty()

                    LaunchedEffect(movies) {
                        if(viewModel.cachedFilteredMovies.value.isEmpty()) {
                            isLoadingDetails = true
                            val filtered = movies.filter { it.originalLanguage == "en" && !it.title.contains("Lucy Shimmers and the") &&!it.title.contains("Gabriel") && !it.title.contains("Primal: Tales") }.shuffled().take(5)
                            preparedMovies = filtered
                            viewModel.cacheFilteredMovies(filtered)

                            viewModel.resetDetailsLoaded()
                            viewModel.fetchAllMovieDetails(filtered.map { it.id.toString() })
                        }
                    }

                    LaunchedEffect(allDetailsLoaded) {
                        if (allDetailsLoaded && preparedMovies.isNotEmpty()) {

                            val allDetailsAvailable = preparedMovies.all { movieDetailsMap.containsKey(it.id) }
                            isLoadingDetails = !allDetailsAvailable
                        }
                    }

                    if (!isLoadingDetails && preparedMovies.isNotEmpty()) {
                        HomeSlider(
                            moviesList = preparedMovies,
                            movieDetailsMap = movieDetailsMap,
                            navController
                        )
                    } else {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(590.dp)
                                .background(background),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .clip(
                                        RoundedCornerShape(
                                            bottomEnd = 15.dp,
                                            bottomStart = 15.dp
                                        )
                                    )
                                    .height(575.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AnimatedShimmerItem()
                            }

                        }
                    }
                }
                is MovieState.Error -> {
                    val errorMessage = (topRatedMovies as MovieState.Error).message
                    Text(text = errorMessage, fontSize = 30.sp, color = Color.Red)
                }
                is MovieState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(590.dp)
                            .background(background),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .clip(RoundedCornerShape(bottomEnd = 15.dp, bottomStart = 15.dp))
                                .height(575.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AnimatedShimmerItem()
                        }

                    }
                }
            }
        }

//        item {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 16.dp, bottom = 10.dp),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                MediaTypeChip(
//                    text = "Movies",
//                    isSelected = selectedMediaType == "Movies",
//                    onClick = {
//                        selectedMediaType = if (selectedMediaType == "Movies") null else "Movies"
//                    }
//                )
//                Spacer(modifier = Modifier.width(10.dp))
//                MediaTypeChip(
//                    text = "Series",
//                    isSelected = selectedMediaType == "Series",
//                    onClick = {
//                        selectedMediaType = if (selectedMediaType == "Series") null else "Series"
//                    }
//                )
//            }
//        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Recommended Movies",
                    fontFamily = netflixFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                when(val state = recommendedMovies) {
                    is MovieState.Success -> {
                        val movies = state.data?.results
                        if(!movies.isNullOrEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(movies) {movie ->
                                    MovieItemSmallSimilar(movie = movie, navController = navController)
                                }

                            }
                        }
                    }
                    is MovieState.Loading -> {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(5) {
                                Column(
                                    modifier = Modifier
                                        .height(260.dp)
                                        .width(140.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .height(210.dp)
                                            .width(140.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    ) {
                                        AnimatedShimmerItem()
                                    }
                                }
                            }
                        }
                    }
                    is MovieState.Error -> {
                        
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Recommended Series",
                    fontFamily = netflixFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                when (val state = recommendedSeries) {
                    is MovieState.Success -> {
                        val series = state.data?.results
                        if (!series.isNullOrEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(series) { seriesItem ->
                                    SeriesItemSmallSimilar(
                                        series = seriesItem,
                                        navController = navController
                                    )
                                }
                            }
                        }
                    }

                    is MovieState.Loading -> {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(5) {
                                Column(
                                    modifier = Modifier
                                        .width(140.dp)
                                        .height(260.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .height(210.dp)
                                            .width(140.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    ) {
                                        AnimatedShimmerItem()
                                    }
                                }
                            }
                        }
                    }

                    is MovieState.Error -> {
                        Text(
                            text = state.message,
                            color = Color.Red,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Top Rated Series",
                    fontFamily = netflixFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                when (val state = topRatedSeries) {
                    is MovieState.Success -> {
                        val series = state.data?.results
                        if (!series.isNullOrEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(series) { seriesItem ->
                                    TopRatedSeriesItem(
                                        series = seriesItem,
                                        navController = navController
                                    )
                                }
                            }
                        }
                    }
                    is MovieState.Loading -> {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(4) {
                                TopRatedSeriesItemShimmer()
                            }
                        }
                    }
                    is MovieState.Error -> {
                        Text(
                            text = state.message,
                            color = Color.Red,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MediaTypeChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .border(width = 1.dp, color = component, shape = RoundedCornerShape(15.dp))
            .background(if (isSelected) component else background)
            .clickable { onClick() }
            .padding(vertical = 6.dp, horizontal = 20.dp)
    ) {
        Text(
            text = text,
            fontFamily = netflixFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}


@Composable
fun HomeSlider(
    moviesList: List<Movie>,
    movieDetailsMap: Map<Int, MovieDetailsDTO>,
    navController: NavController
) {
    val hazeState = remember {
        HazeState()
    }
    var currentIndex by remember {
        mutableStateOf(0)
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(560.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(bottomEnd = 10.dp, bottomStart = 10.dp))
                .haze(
                    state = hazeState,
                    backgroundColor = Color.Black.copy(0.5f),
                    tint = Color.Transparent,
                    blurRadius = 50.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            AutoSlidingCarousel(
                images = moviesList,
                currentIndex = currentIndex,
                onIndexChanged = { currentIndex = it},
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(546.dp)
                    .clip(RoundedCornerShape(bottomEnd = 15.dp, bottomStart = 15.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Transparent
                            ), startY = 100f
                        )
                    ),
                navController = navController


            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 17.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterHorizontally)
            ) {
                // Genre Panel
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(75.dp)
                        .hazeChild(hazeState, shape = RoundedCornerShape(15.dp))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(vertical = 6.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = movieDetailsMap[moviesList[currentIndex].id]?.genres?.firstOrNull()?.name ?: "N/A",
                        color = Color.White.copy(alpha = 0.8f),
                        fontFamily = netflixFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp
                    )
                }

                // Adult Rating Panel
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(45.dp)
                        .hazeChild(hazeState, shape = RoundedCornerShape(15.dp))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(

                        text = if (moviesList.isNotEmpty() && moviesList[currentIndex].adult) "18+" else "<18",
                        color = Color.White.copy(alpha = 0.8f),
                        fontFamily = netflixFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp
                    )
                }

                // Rating Panel
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(45.dp)
                        .hazeChild(hazeState, shape = RoundedCornerShape(15.dp))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = moviesList[currentIndex].voteAverage?.toString()?.take(3) ?: "N/A",
                        color = Color.White.copy(alpha = 0.9f),
                        fontFamily = netflixFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp
                    )
                }

                // Release Year Panel
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(50.dp)
                        .hazeChild(hazeState, shape = RoundedCornerShape(15.dp))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = moviesList[currentIndex].releaseDate?.take(4) ?: "N/A",
                        color = Color.White.copy(alpha = 0.8f),
                        fontFamily = netflixFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp
                    )
                }

            }

        }
    }
}


@Composable
fun DisplayGenreList(genre: List<Genre>?) {
    LazyRow {
        items(genre!!.size) { index ->
            genre[index].name?.let { GenreBlock(name = it) {} }
        }
    }
}


