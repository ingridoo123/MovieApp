package com.example.movieapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.domain.model.Series
import com.example.movieapp.navigation.Screen
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.util.Constants.netflixFamily

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