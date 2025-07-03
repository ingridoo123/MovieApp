package com.example.movieapp.presentation.components

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
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.data.remote.respond.EpisodeDto
import com.example.movieapp.data.remote.respond.SeriesDetailsDTO
import com.example.movieapp.presentation.screens.series_details.SeriesDetailsViewModel
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.util.Constants
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