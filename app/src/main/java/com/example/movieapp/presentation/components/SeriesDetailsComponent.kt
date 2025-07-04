package com.example.movieapp.presentation.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.data.remote.respond.EpisodeDto
import com.example.movieapp.data.remote.respond.PersonSeriesCreditsResponse
import com.example.movieapp.data.remote.respond.SeriesDetailsDTO
import com.example.movieapp.data.remote.respond.SeriesResponse
import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.DisplayableSeriesCredit
import com.example.movieapp.domain.model.Series
import com.example.movieapp.navigation.Screen
import com.example.movieapp.presentation.screens.series_details.SeriesDetailsViewModel
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.util.Constants
import com.example.movieapp.util.Constants.netflixFamily
import com.example.movieapp.util.MovieState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SeasonsAndEpisodesComponent(
    seriesInfo: SeriesDetailsDTO,
    viewModel: SeriesDetailsViewModel
) {
    var selectedSeason by remember { mutableStateOf(1) }
    val seasonDetailsState by viewModel.seasonDetailsResponse.collectAsState()

    LaunchedEffect(selectedSeason) {
        viewModel.fetchSeasonDetails(seriesInfo.id.toString(), selectedSeason)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Seasons & Episodes",
            fontFamily = Constants.netflixFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(seriesInfo.numberOfSeasons) { seasonNumber ->
                SeasonChip(
                    seasonNumber = seasonNumber + 1,
                    isSelected = selectedSeason == seasonNumber + 1,
                    onClick = { selectedSeason = seasonNumber + 1 }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = seasonDetailsState) {
            is MovieState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = componentLighter)
                }
            }
            is MovieState.Success -> {
                val episodes = state.data?.episodes ?: emptyList()
                if (episodes.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                        items(episodes) {episode ->
                            EpisodeItem(episode = episode)
                        }
                    }
                } else {
                    Text("No episodes found for this season.", color = componentLighter)
                }
            }
            is MovieState.Error -> {
                Text(
                    text = state.message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun SeasonChip(seasonNumber: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .background(top_bar_component)
            .border(width = 1.dp, color = if (isSelected) Color.White.copy(0.8f) else component, shape = RoundedCornerShape(15.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Season $seasonNumber",
            color = Color.White.copy(alpha = 0.8f),
            fontFamily = Constants.netflixFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EpisodeItem(episode: EpisodeDto) {
    val imageUrl = MediaAPI.BASE_BACKDROP_IMAGE_URL + episode.stillPath
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )
    val imageState = imagePainter.state

    Column(
        modifier = Modifier
            .width(192.dp)
            .clickable { }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(108.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(top_bar_component),
            contentAlignment = Alignment.Center
        ) {
            when (imageState) {
                is AsyncImagePainter.State.Success -> {
                    Image(
                        painter = imagePainter,
                        contentDescription = episode.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is AsyncImagePainter.State.Error -> {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(component), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ImageNotSupported,
                            contentDescription = "No Image",
                            tint = componentLighter,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                else -> {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(component))
                }
            }
        }
        Spacer(modifier = Modifier.height(3.dp))

        Column {
            Text(
                text = "E${episode.episodeNumber} • ${episode.name}",
                fontFamily = Constants.netflixFamily,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatEpisodeAirDate(episode.airDate),
                fontFamily = Constants.netflixFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = componentLighter,
            )
        }
    }

}


@Composable
fun SeriesCastComponent(castList: List<Cast>, navController: NavController, seriesId: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cast & Crew",
                fontFamily = netflixFamily,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )

            IconButton(onClick = {
                navController.navigate(Screen.SeriesCastAndCrew.route + "/$seriesId") {
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "See More",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val groupedList = castList.chunked(3)

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            items(groupedList) { columnItems ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    columnItems.forEach { cast ->
                        CastItem(cast = cast, navController)
                    }
                }

            }
        }
    }
}

@Composable
fun PersonSeriesComponent(
    navController: NavController,
    seriesCreditsResponse: PersonSeriesCreditsResponse?,
    personDepartment: String?,
    componentTitle: String = "Filmography - Series"
) {
    if (seriesCreditsResponse == null || personDepartment == null) {
        return
    }

    var displayableCredits by remember { mutableStateOf<List<DisplayableSeriesCredit>>(emptyList()) }

    LaunchedEffect(seriesCreditsResponse, personDepartment) {
        val popularityThreshold = 10.00
        val voteAverageThreshold = 7.30
        val secondaryPopularityThreshold = 4.50
        val secondaryVoteAverageThreshold = 0.0

        val filteredSeries = mutableListOf<DisplayableSeriesCredit>()

        when (personDepartment) {
            "Acting" -> {
                seriesCreditsResponse.cast
                    .filter {
                        (it.popularity >= popularityThreshold && it.voteAverage > 4.00)||
                                (it.voteAverage >= voteAverageThreshold && it.popularity >= secondaryPopularityThreshold)
                    }
                    .sortedByDescending { it.popularity }
                    .forEach { castCredit ->
                        filteredSeries.add(
                            DisplayableSeriesCredit(
                                id = castCredit.id,
                                name = castCredit.name,
                                posterPath = castCredit.posterPath,
                                firstAirDate = castCredit.firstAirDate,
                                voteAverage = castCredit.voteAverage,
                                popularity = castCredit.popularity,
                                genreIds = castCredit.genreIds,
                            )
                        )
                    }
            }
            "Directing", "Production", "Writing" -> {
                seriesCreditsResponse.crew
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
                        filteredSeries.add(
                            DisplayableSeriesCredit(
                                id = crewCredit.id,
                                name = crewCredit.name,
                                posterPath = crewCredit.posterPath,
                                firstAirDate = crewCredit.firstAirDate,
                                voteAverage = crewCredit.voteAverage,
                                popularity = crewCredit.popularity,
                                genreIds = crewCredit.genreIds
                            )
                        )
                    }
            }
            else -> {  }
        }
        displayableCredits = filteredSeries.distinctBy { it.id }
        displayableCredits.forEach { series ->
            Log.d("PersonScreenSeries", "Name: ${series.name}, Popularity: ${series.popularity}, Vote Average: ${series.voteAverage})")
        }
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
                items(displayableCredits, key = { it.id }) { seriesCredit ->
                    SeriesItemSmallPerson(
                        series = seriesCredit,
                        navController = navController
                    )
                }
            }
        }
    }
}



@Composable
fun SeriesItemSmallPerson(series: DisplayableSeriesCredit, navController: NavController) {

    val title = series.name
    val imageUrl = "${MediaAPI.BASE_BACKDROP_IMAGE_URL}${series.posterPath}"
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )
    val imageState = imagePainter.state

    val genreMap = mapOf(
        10759 to "Action", 16 to "Animation", 35 to "Comedy", 80 to "Crime",
        99 to "Documentary", 18 to "Drama", 10751 to "Family", 10762 to "Kids",
        9648 to "Mystery", 10763 to "News", 10764 to "Reality", 10765 to "Sci-Fi & Fantasy",
        10766 to "Soap", 10767 to "Talk", 10768 to "War & Politics", 37 to "Western"
    )


    val genre = series.genreIds?.firstOrNull()?.let { genreMap[it] } ?: "N/A"

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
                        .clickable { navController.navigate(Screen.SeriesDetails.route + "/${series.id}") }
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
                    text = if(series.firstAirDate.take(4).isNotEmpty()) series.firstAirDate.take(4) else "N/A",
                    fontFamily = netflixFamily,
                    color = componentLighter,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(end = 4.dp),
                )
                Icon(
                    imageVector = Icons.Default.Circle,
                    modifier = Modifier
                        .size(10.dp)
                        .padding(end = 4.dp),
                    tint = componentLighter,
                    contentDescription = "circle"
                )
                Text(
                    text = genre ?: "N/A",
                    fontFamily = netflixFamily,
                    color = componentLighter,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(end = 2.dp)
                )
            }
        }
    }
}

@Composable
fun SimilarSeriesComponent(navController: NavController, series: SeriesResponse, seriesId: String) {
    val seriesList = series.results
    val filteredSeriesList = seriesList.filter { it.originalLanguage != "zh" && it.originalLanguage != "kn" && it.originalLanguage != "hi" && it.voteAverage != 0.0}

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "You May Also Like",
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = netflixFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            IconButton(onClick = {
                navController.navigate(Screen.SimilarSeries.route + "/$seriesId") {
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "See More",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredSeriesList.size) {
                SeriesItemSmallSimilar(series = filteredSeriesList[it],navController = navController)
            }
        }
    }
}

@Composable
fun SeriesItemBig(series: Series, navController: NavController) {

    val title = series.name
    val imageUrl = "${MediaAPI.BASE_BACKDROP_IMAGE_URL}${series.posterPath}"
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )
    val imageState = imagePainter.state

    val genreMap = mapOf(
        10759 to "Action", 16 to "Animation", 35 to "Comedy", 80 to "Crime",
        99 to "Documentary", 18 to "Drama", 10751 to "Family", 10762 to "Kids",
        9648 to "Mystery", 10763 to "News", 10764 to "Reality", 10765 to "Sci-Fi & Fantasy",
        10766 to "Soap", 10767 to "Talk", 10768 to "War & Politics", 37 to "Western"
    )


    val genre = series.genreIds?.firstOrNull()?.let { genreMap[it] } ?: "N/A"

    Column(
        modifier = Modifier
            .width(171.dp)
            .height(299.dp)
    ) {
        Box(
            modifier = Modifier
                .height(256.dp)
                .width(171.dp)
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
                        .clickable { navController.navigate(Screen.SeriesDetails.route + "/${series.id}") }
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
                    text = if(series.firstAirDate?.take(4)?.isNotEmpty() == true) series.firstAirDate.take(4) else "N/A",
                    fontFamily = netflixFamily,
                    color = componentLighter,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(end = 4.dp),
                )
                Icon(
                    imageVector = Icons.Default.Circle,
                    modifier = Modifier
                        .size(10.dp)
                        .padding(end = 4.dp),
                    tint = componentLighter,
                    contentDescription = "circle"
                )
                Text(
                    text = genre ?: "N/A",
                    fontFamily = netflixFamily,
                    color = componentLighter,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(end = 2.dp)
                )
            }
        }
    }
}




private fun formatEpisodeAirDate(dateString: String?): String {
    if (dateString.isNullOrBlank()) return "N/A"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM d • yyyy", Locale.ENGLISH)
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}