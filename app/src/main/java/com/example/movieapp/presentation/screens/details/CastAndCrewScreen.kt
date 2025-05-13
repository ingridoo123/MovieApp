package com.example.movieapp.presentation.screens.details

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.Crew
import com.example.movieapp.navigation.Screen
import com.example.movieapp.presentation.components.AnimatedShimmerItem
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.util.Constants.netflixFamily
import com.example.movieapp.util.MovieState
import kotlinx.coroutines.flow.collect

@Composable
fun CastAndCrewScreen(navController: NavController, viewModel: MovieDetailsViewModel = hiltViewModel(), movieId: String) {

    LaunchedEffect(movieId) {
        viewModel.fetchCastOfMovie(movieId)
    }

    val castCrewState by viewModel.crewCastResponse.collectAsState()

    val castList = (castCrewState as? MovieState.Success)?.data?.first ?: emptyList()
    val crewList = (castCrewState as? MovieState.Success)?.data?.second ?: emptyList()

    var selectedFilter by remember { mutableStateOf("Actors") }

    val filteredList = remember(selectedFilter, castList, crewList) {
        when (selectedFilter) {
            "Actors" -> castList.filter { it.department == "Acting" }
            "Directors" -> crewList.filter { it.job == "Director" }
            "Producers" -> crewList.filter { it.job == "Producer" }
            else -> emptyList()
        }
    }

    Log.d("CastResponse", "CC Screen - ${crewList}")


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(background),
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.Transparent)
        ) {
            Text(
                text = "Cast & Crew",
                fontWeight = FontWeight.Medium,
                fontFamily = netflixFamily,
                fontSize = 22.sp,
                modifier = Modifier.align(Alignment.Center),
                color = Color.White.copy(alpha = 0.8f)
            )
            
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "IosBack",
                    tint = Color.White.copy(alpha = 0.8f)
                )

            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            listOf("Actors", "Directors", "Producers").forEach {
                FilterChip(
                    text = it,
                    isSelected = selectedFilter == it,
                    onClick = { selectedFilter = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Log.d("CrewAndCast", "$castList ORAZ $crewList")

        when (castCrewState) {
            is MovieState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            is MovieState.Error -> {
                Text(
                    text = (castCrewState as MovieState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            is MovieState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (selectedFilter) {
                        "Actors" -> items(filteredList as List<Cast>) { CastItemBigger(it, navController) }
                        else -> items(filteredList as List<Crew>) { CrewItemBigger(it, navController) }
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(top_bar_component, shape = RoundedCornerShape(25.dp))
            .clickable { onClick() }
            .height(35.dp)
            .wrapContentWidth()
            .border(
                1.dp,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Transparent,
                shape = RoundedCornerShape(25.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
            color = if(isSelected) Color.White.copy(alpha = 0.8f) else componentLighter,
            fontFamily = netflixFamily,
            fontSize = 13.sp,

        )
    }
}

@Composable
fun CastItemBigger(cast: Cast, navController: NavController) {

    val imageUrl = "${MediaAPI.BASE_BACKDROP_IMAGE_URL}${cast.profilePath}"
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )
    val imageState = imagePainter.state


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(top_bar_component, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(color = component, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (imageState is AsyncImagePainter.State.Success) {
                val imageBitmap = imageState.result.drawable.toBitmap()

                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = "castImage",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            if (imageState is AsyncImagePainter.State.Error) {
                Icon(
                    imageVector = Icons.Default.ImageNotSupported,
                    contentDescription = "error",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .padding(5.dp),
                    tint = componentLighter
                )
            }

            if (imageState is AsyncImagePainter.State.Loading) {
                AnimatedShimmerItem()
            }
        }

        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
        ) {

            Text(
                text = cast.name,
                fontFamily = netflixFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .padding(bottom = 5.dp),
                maxLines = 1
            )

            Text(
                text = "Actor",
                fontFamily = netflixFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = componentLighter
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {
                navController.navigate(Screen.Person.route + "/${cast.name}") {
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "personScreen",
                    tint = componentLighter,
                    modifier = Modifier.size(25.dp)
                )
            }
        }



    }
}

@Composable
fun CrewItemBigger(crew: Crew, navController: NavController) {

    val imageUrl = "${MediaAPI.BASE_BACKDROP_IMAGE_URL}${crew.profilePath}"
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )
    val imageState = imagePainter.state


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(top_bar_component, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(color = component, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (imageState is AsyncImagePainter.State.Success) {
                val imageBitmap = imageState.result.drawable.toBitmap()

                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = "castImage",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            if (imageState is AsyncImagePainter.State.Error) {
                Icon(
                    imageVector = Icons.Default.ImageNotSupported,
                    contentDescription = "error",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .padding(5.dp),
                    tint = componentLighter
                )
            }

            if (imageState is AsyncImagePainter.State.Loading) {
                AnimatedShimmerItem()
            }
        }

        Spacer(modifier = Modifier.width(10.dp))
        Column {

            Text(
                text = crew.name,
                fontFamily = netflixFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .padding(bottom = 5.dp),
                maxLines = 1
            )

            Text(
                text = crew.job,
                fontFamily = netflixFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = componentLighter
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {
                navController.navigate(Screen.Person.route + "/${crew.name}") {
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "personScreen",
                    tint = componentLighter,
                    modifier = Modifier.size(25.dp)
                )
            }
        }

        
    }
}


val castTest = Cast(job = "Actor", department = "elo", profilePath = "error" ?: "esa", name = "Piotr Adamczyk")


@Composable
@Preview(showBackground = true)
fun CastItemBiggerPreview(cast: Cast = castTest) {

    val imageUrl = "${MediaAPI.BASE_BACKDROP_IMAGE_URL}${cast.profilePath}"
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )
    val imageState = imagePainter.state


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(top_bar_component, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(color = component, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (imageState is AsyncImagePainter.State.Success) {
                val imageBitmap = imageState.result.drawable.toBitmap()

                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = "castImage",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            if (imageState is AsyncImagePainter.State.Error) {
                Icon(
                    imageVector = Icons.Default.ImageNotSupported,
                    contentDescription = "error",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .padding(5.dp),
                    tint = componentLighter
                )
            }

            if (imageState is AsyncImagePainter.State.Loading) {
                AnimatedShimmerItem()
            }
        }

        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
        ) {

            Text(
                text = cast.name,
                fontFamily = netflixFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .padding(bottom = 5.dp),
                maxLines = 1
            )

            Text(
                text = "Actor",
                fontFamily = netflixFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = componentLighter
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = "Arrow Icon",
            tint = Color.Red,
            modifier = Modifier.size(25.dp)
        )


    }
}