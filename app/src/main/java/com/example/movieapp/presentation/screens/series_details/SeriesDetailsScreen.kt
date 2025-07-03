package com.example.movieapp.presentation.screens.series_details

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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.movieapp.data.remote.MediaAPI.Companion.BASE_BACKDROP_IMAGE_URL
import com.example.movieapp.data.remote.respond.SeriesDetailsDTO
import com.example.movieapp.domain.model.Trailer
import com.example.movieapp.presentation.components.AnimatedShimmerItem
import com.example.movieapp.presentation.components.MovieDataItemEmpty
import com.example.movieapp.presentation.components.SeasonsAndEpisodesComponent
import com.example.movieapp.presentation.components.SeriesItem
import com.example.movieapp.presentation.screens.details.MovieDetailsViewModel
import com.example.movieapp.presentation.screens.details.PlayTrailerBox
import com.example.movieapp.presentation.screens.details.YoutubePlayer
import com.example.movieapp.presentation.screens.details.formatRuntime
import com.example.movieapp.presentation.screens.favourite.FavouriteViewModel
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.util.Constants.netflixFamily
import com.example.movieapp.util.MovieState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import java.text.SimpleDateFormat
import java.util.Locale


private fun formatTrailerDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: ""
    } catch (e: Exception) {
        ""
    }
}

@Composable
fun SeriesDetailsScreen(
    navController: NavController,
    seriesId: String,
    viewModel: SeriesDetailsViewModel = hiltViewModel(),
    viewModel2: FavouriteViewModel = hiltViewModel()
) {
    val seriesTrailerState by viewModel.seriesTrailerResponse.collectAsState()
    val detailsSeriesState by viewModel.detailsSeriesResponse.collectAsState()
    

    var playedTrailerKey by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchSeriesDetails(seriesId)
        viewModel.fetchSeriesTrailer(seriesId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .verticalScroll(rememberScrollState())
    ) {
        when(detailsSeriesState) {
            is MovieState.Success -> {
                val seriesInfo = (detailsSeriesState as MovieState.Success<SeriesDetailsDTO?>).data
                seriesInfo?.let {
                    Column(modifier = Modifier.fillMaxSize()) {

                        SeriesItem(
                            seriesInfo = it,
                            navController = navController,
                            viewModel = viewModel2,
                            bookmarkImageUrl = it.backdropPath ?: ""
                        )
                        Log.d("DetailsScreen", seriesInfo.originalLanguage)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = it.name,
                                fontSize = 22.sp,
                                color = Color.White.copy(0.8f),
                                fontFamily = netflixFamily,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                3.dp,
                                Alignment.CenterHorizontally
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(28.dp)
                                    .wrapContentWidth()
                                    .background(
                                        color = top_bar_component,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = it.genres.firstOrNull()?.name ?: "N/A",
                                    fontFamily = netflixFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(0.8f)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .height(28.dp)
                                    .wrapContentWidth()
                                    .background(
                                        color = top_bar_component,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (it.firstAirDate.isNotBlank()) it.firstAirDate.take(4) else "N/A",
                                    fontFamily = netflixFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(0.8f)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .height(28.dp)
                                    .wrapContentWidth()
                                    .background(
                                        color = top_bar_component,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = it.productionCompanies.firstOrNull()?.name ?: "N/A",
                                    fontFamily = netflixFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(0.8f)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .height(28.dp)
                                    .wrapContentWidth()
                                    .background(
                                        color = top_bar_component,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val season = if (it.numberOfSeasons == 1) "Season" else "Seasons"
                                Text(
                                    text = "${it.numberOfSeasons} $season",
                                    fontFamily = netflixFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(0.8f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(22.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(horizontal = 15.dp)
                        ) {
                            Text(
                                text = "Description",
                                fontFamily = netflixFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp,
                                color = Color.White.copy(0.8f),
                                modifier = Modifier.padding(bottom = 7.dp)
                            )
                            Text(
                                text = it.overview ?: "No description, we are sorry :c",
                                fontFamily = netflixFamily,
                                fontSize = 12.sp,
                                color = componentLighter,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Trailers",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(0.8f),
                                    fontFamily = netflixFamily
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            when (seriesTrailerState) {
                                is MovieState.Success -> {
                                    val trailerList = (seriesTrailerState as MovieState.Success<List<Trailer>?>).data
                                        ?.filter { it.site == "YouTube" && (it.type == "Trailer" || it.type == "Teaser") }?.take(3)
                                        ?: emptyList()

                                    Log.d("SeriesD", "${trailerList.size}")

                                    if (trailerList.isNotEmpty()) {
                                        if(trailerList.size == 1) {
                                            val trailer = trailerList.first()
                                            if (playedTrailerKey == trailer.key) {
                                                YoutubePlayer(
                                                    youtubeVideoId = trailer.key,
                                                    lifecycleOwner = LocalLifecycleOwner.current,
                                                )
                                            } else {
                                                PlayTrailerBox(
                                                    onClick = { playedTrailerKey = trailer.key },
                                                    imageUrl = seriesInfo.backdropPath,
                                                )
                                            }
                                        } else {
                                            LazyRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                            ) {
                                                items(trailerList) { trailer ->
                                                    TrailerItem(
                                                        trailer = trailer,
                                                        backdropUrl = seriesInfo.backdropPath,
                                                        isPlaying = playedTrailerKey == trailer.key,
                                                        onPlayClick = {playedTrailerKey = trailer.key}
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                is MovieState.Loading -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(top_bar_component),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AnimatedShimmerItem()
                                    }
                                }

                                is MovieState.Error -> {
                                    Text(
                                        "Trailer unavailable",
                                        color = Color.Gray,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp) )
                            
                            if(it.numberOfSeasons > 0) {
                                SeasonsAndEpisodesComponent(seriesInfo = it, viewModel = viewModel)
                            }
                            
                            Spacer(modifier = Modifier.height(150.dp))
                        }

                    }
                }
            }

            is MovieState.Error -> {
                val errorMessage = (detailsSeriesState as MovieState.Error).message
                Text(text = errorMessage, fontSize = 30.sp, color = Color.Red)
            }

            is MovieState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    MovieDataItemEmpty(navController = navController)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(22.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(component)
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            3.dp,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        repeat(4) {
                            Box(
                                modifier = Modifier
                                    .height(28.dp)
                                    .width(80.dp)
                                    .background(
                                        color = top_bar_component,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {

                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 15.dp)
                    ) {
                        Text(
                            text = "Description",
                            fontFamily = netflixFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 7.dp)
                        )

                        Column {
                            repeat(5) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(14.dp)
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(componentLighter)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Trailer",
                                fontFamily = netflixFamily,
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(top_bar_component),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedShimmerItem()
                        }
                    }
                }
            }

        }

    }














}

@Composable
fun YouTubePlayerSeries(
    youtubeVideoId: String,
    lifecycleOwner: LifecycleOwner,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)),
        factory = { context ->
            YouTubePlayerView(context = context).apply {
                lifecycleOwner.lifecycle.addObserver(this)
                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(youtubeVideoId, 0f)
                    }
                })
            }
        }
    )
}
@Composable
fun TrailerItem(
    trailer: Trailer,
    backdropUrl: String?,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.width(300.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(169.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(top_bar_component)
        ) {
            if (isPlaying) {
                YouTubePlayerSeries(
                    youtubeVideoId = trailer.key,
                    lifecycleOwner = LocalLifecycleOwner.current,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                PlayTrailerBoxSeries(
                    onClick = onPlayClick,
                    imageUrl = backdropUrl,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = trailer.name,
            fontFamily = netflixFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.9f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = formatTrailerDate(trailer.publishedAt),
            fontFamily = netflixFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = componentLighter
        )
    }
}

@Composable
fun PlayTrailerBoxSeries(onClick: () -> Unit, imageUrl: String?, modifier: Modifier = Modifier) {
    val hazeState = remember {
        HazeState()
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(top_bar_component)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .haze(
                    state = hazeState,
                    backgroundColor = top_bar_component.copy(alpha = 0.7f),
                    tint = top_bar_component.copy(alpha = 0.2f),
                    blurRadius = 15.dp
                )
        ) {
            if (imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = BASE_BACKDROP_IMAGE_URL + imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxSize()
                )
            }
        }
        Box(
            modifier = Modifier
                .size(50.dp)
                .hazeChild(hazeState, shape = CircleShape)
                .clip(shape = CircleShape)
                .background(Color.Transparent, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "PlayArrow",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(50.dp)
                    .hazeChild(hazeState, shape = RoundedCornerShape(10.dp))
            )
        }
    }
}













