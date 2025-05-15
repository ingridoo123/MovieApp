package com.example.movieapp.presentation.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.data.remote.respond.MovieResponse
import com.example.movieapp.data.remote.respond.PersonMovieCreditsResponse
import com.example.movieapp.domain.model.DisplayableMovieCredit
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.navigation.Screen
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.util.Constants
import com.example.movieapp.util.Constants.netflixFamily
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun PersonMoviesComponent(
    navController: NavController,
    movieCreditsResponse: PersonMovieCreditsResponse?,
    personDepartment: String?,
    componentTitle: String = "Filmography"
) {
    if (movieCreditsResponse == null || personDepartment == null) {
        return
    }

    var displayableCredits by remember { mutableStateOf<List<DisplayableMovieCredit>>(emptyList()) }

    LaunchedEffect(movieCreditsResponse, personDepartment) {
        val popularityThreshold = 10.00
        val voteAverageThreshold = 7.30
        val secondaryPopularityThreshold = 4.50

        val filteredMovies = mutableListOf<DisplayableMovieCredit>()

        when (personDepartment) {
            "Acting" -> {
                movieCreditsResponse.cast
                    .filter {
                        it.popularity >= popularityThreshold ||
                                (it.voteAverage >= voteAverageThreshold && it.popularity >= secondaryPopularityThreshold)
                    }
                    .sortedByDescending { it.popularity }
                    .forEach { castCredit ->
                        filteredMovies.add(
                            DisplayableMovieCredit(
                                id = castCredit.id,
                                title = castCredit.title,
                                posterPath = castCredit.posterPath,
                                releaseDate = castCredit.releaseDate,
                                voteAverage = castCredit.voteAverage,
                                genreIds = castCredit.genreIds,
                            )
                        )
                    }
            }
            "Directing", "Production", "Writing" -> {
                movieCreditsResponse.crew
                    .filter {
                        val isCorrectJob = when (personDepartment) {
                            "Directing" -> it.job == "Director"
                            "Production" -> it.job == "Producer"
                            "Writing" -> it.job == "Writer"
                            else -> false
                        }
                        isCorrectJob && (it.popularity >= popularityThreshold ||
                                (it.voteAverage >= voteAverageThreshold && it.popularity >= secondaryPopularityThreshold))
                    }
                    .sortedByDescending { it.popularity }
                    .forEach { crewCredit ->
                        filteredMovies.add(
                            DisplayableMovieCredit(
                                id = crewCredit.id,
                                title = crewCredit.title,
                                posterPath = crewCredit.posterPath,
                                releaseDate = crewCredit.releaseDate,
                                voteAverage = crewCredit.voteAverage,
                                genreIds = crewCredit.genreIds
                            )
                        )
                    }
            }
            else -> {  }
        }
        displayableCredits = filteredMovies.distinctBy { it.id }
        Log.d("PersonScreen", "${displayableCredits}")
    }

    if (displayableCredits.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
        ) {
            Text(
                text = componentTitle,
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = netflixFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayableCredits, key = { it.id }) { movieCredit ->
                    MovieItemSmallPerson(
                        movie = movieCredit,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun MovieItemSmallPerson(movie: DisplayableMovieCredit, navController: NavController) {

    val title = movie.title
    val imageUrl = "${MediaAPI.BASE_BACKDROP_IMAGE_URL}${movie.posterPath}"
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )
    val imageState = imagePainter.state

    val genreMap = mapOf(
        28 to "Action", 12 to "Adventure", 16 to "Animation", 35 to "Comedy", 80 to "Crime",
        99 to "Documentary", 18 to "Drama", 10751 to "Family", 14 to "Fantasy", 36 to "History",
        27 to "Horror", 10402 to "Music", 9648 to "Mystery", 10749 to "Romance", 878 to "Sci-Fi",
        10770 to "TV Movie", 53 to "Thriller", 10752 to "War", 37 to "Western"
    )


    val genre = movie.genreIds?.firstOrNull()?.let { genreMap[it] } ?: "N/A"

    Column(
        modifier = Modifier
            .width(140.dp)
            .height(260.dp)
    ) {
        Box(
            modifier = Modifier
                .height(210.dp)
                .width(140.dp)
                .background(color = component, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (imageState is AsyncImagePainter.State.Success) {
                val imageBitmap = imageState.result.drawable.toBitmap()

                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { navController.navigate(Screen.Details.route + "/${movie.id}") }
                )
            }

            if (imageState is AsyncImagePainter.State.Error) {
                Icon(
                    imageVector = Icons.Default.ImageNotSupported,
                    contentDescription = "error",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    tint = componentLighter
                )
            }

            if (imageState is AsyncImagePainter.State.Loading) {
                AnimatedShimmerItem()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                fontFamily = netflixFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 5.dp, bottom = 2.dp),
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 1,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if(movie.releaseDate.take(4).isNotEmpty()) movie.releaseDate.take(4) else "N/A",
                    fontFamily = netflixFamily,
                    color = componentLighter,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(end = 4.dp),
                )
                Icon(
                    imageVector = Icons.Default.Circle,
                    modifier = Modifier
                        .size(10.dp) // bez zmian
                        .padding(end = 4.dp),
                    tint = componentLighter,
                    contentDescription = "circle"
                )
                Text(
                    text = genre ?: "N/A",
                    fontFamily = netflixFamily,
                    color = componentLighter,
                    fontSize = 11.sp, // bez zmian
                    modifier = Modifier.padding(end = 2.dp)
                )
            }
        }
    }

}