package com.example.movieapp.presentation.screens.details

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableInferredTarget
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.movieapp.data.remote.respond.MovieDetailsDTO
import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.Trailer
import com.example.movieapp.presentation.components.MovieCastComponent
import com.example.movieapp.presentation.components.MovieDataItem
import com.example.movieapp.presentation.components.MovieDataItemEmpty
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.util.Constants.netflixFamily
import com.example.movieapp.util.MovieState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dev.chrisbanes.haze.hazeChild

@Composable
fun MovieDetailsScreen(navController: NavController, movieId: String, viewModel: MovieDetailsViewModel = hiltViewModel()) {

    val detailsMovieState by viewModel.detailsMovieResponse.collectAsState()
    val movieCastState by viewModel.movieCastResponse.collectAsState()
    val similarMovieState by viewModel.similarMoviesResponse.collectAsState()
    val movieTrailerState by viewModel.movieTrailerResponse.collectAsState()

    var playTrailer by remember { mutableStateOf(false)}

    LaunchedEffect(Unit) {
        viewModel.fetchMovieDetails(movieId)
        viewModel.fetchMovieTrailer(movieId)
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
               moviesInfo?.let {
                   Column(modifier = Modifier
                       .fillMaxSize()
                   ) {
                       MovieDataItem(movieInfo = it, navController = navController)
                       Row(
                           modifier = Modifier
                               .fillMaxWidth()
                               .padding(horizontal = 20.dp),
                           horizontalArrangement = Arrangement.Center
                       ) {
                           Text(
                               text = it.title ,
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
                           modifier = Modifier.fillMaxWidth(),
                           horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterHorizontally)
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
                                   color = Color.White.copy(alpha = 0.8f)
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
                                   text = it.releaseDate.take(4),
                                   fontFamily = netflixFamily,
                                   fontSize = 11.sp,
                                   fontWeight = FontWeight.Normal,
                                   color = Color.White.copy(alpha = 0.8f),
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
                                   color = Color.White.copy(alpha = 0.8f)
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
                                   text = formatRuntime(it.runtime),
                                   fontFamily = netflixFamily,
                                   fontSize = 11.sp,
                                   fontWeight = FontWeight.Normal,
                                   color = Color.White.copy(alpha = 0.8f)
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
                               fontSize = 18.sp,
                               fontWeight = FontWeight.Medium,
                               color = Color.White.copy(alpha = 0.8f),
                               modifier = Modifier.padding(bottom = 7.dp)
                           )
                           Text(
                               text = it.overview ?: "No description, we are sorry :c",
                               fontFamily = netflixFamily,
                               fontWeight = FontWeight.Normal,
                               fontSize = 12.sp,
                               color = componentLighter,
                               lineHeight = 17.sp
                           )

                           Spacer(modifier = Modifier.height(20.dp))

                           when (movieTrailerState) {
                               is MovieState.Success -> {
                                   val trailerList = (movieTrailerState as MovieState.Success<List<Trailer>?>).data ?: emptyList()
                                   val trailer = trailerList.firstOrNull { it.site == "YouTube" && it.type == "Trailer" }
                                   trailer?.let {
                                       if(playTrailer) {
                                           YoutubePlayer(youtubeVideoId = it.key, lifecycleOwner = LocalLifecycleOwner.current)
                                       } else {
                                           PlayTrailerBox {
                                               playTrailer = true
                                           }
                                       }
                                   }
                               }
                               is MovieState.Loading -> {
                                   // możesz dodać jakiś shimmer
                               }
                               is MovieState.Error -> {
                                   Text("Trailer unavailable", color = Color.Gray, modifier = Modifier.padding(horizontal = 16.dp))
                               }
                           }
                       }

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
                       .fillMaxSize(),
                   contentAlignment = Alignment.Center
               ) {
                   MovieDataItemEmpty(navController = navController)
               }
           }
       }
       Spacer(modifier = Modifier.height(20.dp))

       when(movieCastState) {
           is MovieState.Success -> {
               val castList = ((movieCastState as MovieState.Success<List<Cast>?>).data as? List<Cast>) ?: emptyList()
               Log.d("CastScreen_test","test1")
               MovieCastComponent(castList = castList)

           }

           is MovieState.Loading -> {

           }

           is MovieState.Error -> {

           }
       }
   }


}

fun formatRuntime(runtime: Int?): String {
    return if(runtime != null && runtime > 0) {
        val hours = runtime/60
        val minutes = runtime % 60
        "${hours}h ${minutes}min"
    } else {
        "Nieznana"
    }
}



@Composable
fun PlayTrailerBox(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(360.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(top_bar_component)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play Trailer",
            tint = Color.White,
            modifier = Modifier.size(50.dp)
        )
    }
}

@Composable
fun YoutubePlayer(
    youtubeVideoId: String,
    lifecycleOwner: LifecycleOwner
) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(8.dp)
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
        })
}






