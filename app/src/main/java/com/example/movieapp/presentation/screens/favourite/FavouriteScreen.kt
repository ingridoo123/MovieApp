package com.example.movieapp.presentation.screens.favourite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.data.remote.MediaAPI.Companion.BASE_BACKDROP_IMAGE_URL
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.navigation.Screen
import com.example.movieapp.presentation.screens.details.MovieDetailsViewModel
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.util.Constants
import com.example.movieapp.util.Constants.netflixFamily

@Composable
fun FavouriteScreen(navController: NavController, viewModel: FavouriteViewModel = hiltViewModel(), viewModel2: MovieDetailsViewModel = hiltViewModel()) {

    val myMoviesDataFlow = viewModel.myMovieData.value
    val myMoviesData by myMoviesDataFlow.collectAsState(initial = null)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color = top_bar_component),
            ) {
            Text(
                text = "My List",
                fontSize = 19.sp,
                fontFamily = netflixFamily,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center),
                color = Color.White.copy(alpha = 0.8f)
            )

            IconButton(
                onClick = {navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "IosBack",
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }

            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit ,
                    contentDescription = "Edit",
                    tint = Color.White.copy(0.8f)
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            Text(
                text = "Sort by",
                fontSize = 14.sp,
                fontFamily = netflixFamily,
                fontWeight = FontWeight.Normal,
                color = componentLighter
            )
            Text(
                text = "Date added to the list:",
                fontSize = 15.sp,
                fontFamily = netflixFamily,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(5.dp))

        if(myMoviesData == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Color.White.copy(alpha = 0.8f))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(myMoviesData ?: emptyList(), key = {it.mediaId}) {
                    val movie = it
                    val imageUrl = movie.imagePath.let { BASE_BACKDROP_IMAGE_URL + it}
                    FavouriteItem(navController = navController, imageUrl = imageUrl ?: "", title = movie.title) {
                        navController.navigate(Screen.Details.route + "/${movie.mediaId}")
                    }

                }
            }
        }




    }
}

@Composable
fun FavouriteItem(navController: NavController, imageUrl: String, title: String, onClick: () -> Unit) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(77.dp)
            .clickable { onClick() }
            .clip(RoundedCornerShape(10.dp))
            .background(color = top_bar_component, shape = RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(5.dp))
                .width(150.dp),
            painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current).data(data = imageUrl)
                .size(Size.ORIGINAL)
                .build()

            ),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            fontFamily = netflixFamily,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.8f),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }





}