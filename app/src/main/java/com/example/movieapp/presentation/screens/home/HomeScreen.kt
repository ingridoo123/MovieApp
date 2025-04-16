package com.example.movieapp.presentation.screens.home

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
import com.example.movieapp.presentation.components.NoInternetScreen
import com.example.movieapp.presentation.components.ShimmerDarkGray
import com.example.movieapp.ui.theme.background
import com.example.movieapp.util.Constants.netflixFamily
import com.example.movieapp.util.MovieState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.delay

@Composable
fun SimpleHomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val popularMoviesLazy = viewModel.popularAllListState.collectAsLazyPagingItems()
    val popularMovies by viewModel.popularMovieResponse.collectAsState()
    val refreshTrigger by viewModel.refreshTrigger.collectAsState()
    val topRatedMovies by viewModel.topRatedMovieResponse.collectAsState()
    val movieDetailsMap by viewModel.movieDetailsMap.collectAsState()
    val allDetailsLoaded by viewModel.allDetailsLoaded.collectAsState()
    val genresMovieState by viewModel.genresMovieResponse.collectAsState()

    var preparedMovies by remember {
        mutableStateOf<List<Movie>>(emptyList())
    }
    var isLoadingDetails by remember {
        mutableStateOf(true)
    }

    // Force refresh when refreshTrigger changes
    LaunchedEffect(refreshTrigger) {
        popularMoviesLazy.refresh()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = background)

    ) {
        when(topRatedMovies) {
            is MovieState.Success -> {
                val movies = (topRatedMovies as MovieState.Success<MovieResponse?>).data?.results.orEmpty()

                // Przygotuj filtrowane filmy tylko raz przy zmianie danych
                LaunchedEffect(movies) {
                    isLoadingDetails = true
                    val filtered = movies.filter { it.originalLanguage == "en" }.shuffled().take(5)
                    preparedMovies = filtered

                    // Resetuj i pobierz wszystkie szczegóły
                    viewModel.resetDetailsLoaded()
                    viewModel.fetchAllMovieDetails(filtered.map { it.id.toString() })
                }

                // Obserwuj zmiany stanu załadowania
                LaunchedEffect(allDetailsLoaded) {
                    if (allDetailsLoaded) {
                        // Upewnij się, że mamy wszystkie szczegóły dla wyświetlanych filmów
                        val allDetailsAvailable = preparedMovies.all { movieDetailsMap.containsKey(it.id) }
                        isLoadingDetails = !allDetailsAvailable
                    }
                }

                if (!isLoadingDetails && preparedMovies.isNotEmpty()) {
                    // Pokazuj slider tylko gdy wszystkie szczegóły są załadowane
                    HomeSlider(
                        moviesList = preparedMovies,
                        movieDetailsMap = movieDetailsMap,
                        navController
                    )
                } else {
                    // Pokaż ekran ładowania
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(590.dp)
                            .background(ShimmerDarkGray),
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
            is MovieState.Error -> {
                val errorMessage = (topRatedMovies as MovieState.Error).message
                Text(text = errorMessage, fontSize = 30.sp, color = Color.Red)
            }
            is MovieState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(590.dp)
                        .background(ShimmerDarkGray),
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
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp)
        ) {
            when(genresMovieState) {
                is MovieState.Success -> {
                    val genres = (genresMovieState as MovieState.Success<GenreResponse?>).data?.genres
                    Text(
                        text = "Genres",
                        fontFamily = netflixFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(genres?.size ?: 0) { index ->
                            genres?.get(index)?.name?.let { GenreBlock(name = it) {navController.navigate(Screen.GenreWise.route + "/${genres[index].id}" + "/${genres[index].name}")} }
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
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
        }

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
            .height(590.dp)
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
                    .height(575.dp)
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
                .padding(bottom = 19.dp),
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


