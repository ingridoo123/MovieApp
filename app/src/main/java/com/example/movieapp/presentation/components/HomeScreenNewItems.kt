package com.example.movieapp.presentation.components

import android.widget.Toast
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.movieapp.R
import com.example.movieapp.data.local.media.MediaEntity
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.data.remote.MediaAPI.Companion.BASE_BACKDROP_IMAGE_URL
import com.example.movieapp.data.remote.respond.SeriesDetailsDTO
import com.example.movieapp.domain.model.Series
import com.example.movieapp.navigation.Screen
import com.example.movieapp.presentation.screens.favourite.FavouriteViewModel
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.util.Constants.netflixFamily
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import okhttp3.internal.notify

@Composable
fun TopRatedSeriesItem(series: Series, navController: NavController) {
    val name = series.name
    val imageUrl = "${MediaAPI.BASE_BACKDROP_IMAGE_URL}${series.backdropPath}"
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
            .width(240.dp)
            .height(170.dp)
            .clickable { navController.navigate(Screen.SeriesDetails.route + "/${series.id}")}
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .background(color = component, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (imageState is AsyncImagePainter.State.Success) {
                Image(
                    painter = imagePainter,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (imageState is AsyncImagePainter.State.Error) {
                Icon(
                    imageVector = Icons.Default.ImageNotSupported,
                    contentDescription = "error",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    tint = componentLighter
                )
            } else {
                AnimatedShimmerItem()
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = name,
                fontFamily = netflixFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = if(series.firstAirDate?.take(4)?.isNotEmpty() == true) series.firstAirDate.take(4) else "N/A",
//                    fontFamily = netflixFamily,
//                    color = componentLighter,
//                    fontSize = 12.sp,
//                    modifier = Modifier.padding(end = 4.dp),
//                )
//                Icon(
//                    imageVector = Icons.Default.Circle,
//                    modifier = Modifier
//                        .size(10.dp)
//                        .padding(end = 4.dp),
//                    tint = componentLighter,
//                    contentDescription = "circle"
//                )
//                Text(
//                    text = genre,
//                    fontFamily = netflixFamily,
//                    color = componentLighter,
//                    fontSize = 12.sp,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//            }
        }
    }
}

@Composable
fun TopRatedSeriesItemShimmer() {
    Column(
        modifier = Modifier
            .width(240.dp)
            .height(170.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            AnimatedShimmerItem()
        }
    }
}

@Composable
fun SeriesItem(seriesInfo: SeriesDetailsDTO, navController: NavController, viewModel: FavouriteViewModel, bookmarkImageUrl: String) {
    val hazeState = remember {HazeState()}
    viewModel.isFavourite(seriesInfo.id)
    val isFavourite by viewModel.isFavourite
    val context = LocalContext.current

    val mySeriesInfo = MediaEntity(
        mediaId = seriesInfo.id,
        imagePath = bookmarkImageUrl,
        title = seriesInfo.name,
        releaseDate = seriesInfo.firstAirDate,
        rating = seriesInfo.voteAverage,
        addedOn = System.currentTimeMillis()
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(background)
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
            Image(
                painter = rememberAsyncImagePainter(model = BASE_BACKDROP_IMAGE_URL + seriesInfo.backdropPath),
                contentDescription = seriesInfo.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                background.copy(0.5f),
                                background
                            ), startY = 100f
                        )
                    )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .hazeChild(hazeState, shape = CircleShape)
                        .clip(shape = CircleShape)
                        .clickable { navController.popBackStack() }
                        .background(Color.Transparent, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(25.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(shape = CircleShape)
                        .clickable {
                            if (isFavourite != 0) {
                                viewModel.removeFromFavourites(seriesInfo.id)
                                Toast
                                    .makeText(
                                        context,
                                        "Removed from your Favourites",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            } else {
                                viewModel.addToFavourites(mySeriesInfo)
                                Toast
                                    .makeText(
                                        context,
                                        "Added to your Favourites",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        }
                        .hazeChild(hazeState, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavourite != 0) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-20).dp, y = (-6).dp)
                .width(50.dp)
                .height(19.dp)
                .background(
                    color = Color.White.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(20.dp)
                )
                .clip(RoundedCornerShape(20.dp))
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_star),
                contentDescription = "Rating star",
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = seriesInfo.voteAverage.toString().take(3),
                fontFamily = netflixFamily,
                color = background,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 1.dp)
            )
        }
    }









}