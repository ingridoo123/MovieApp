package com.example.movieapp.presentation.screens.series_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.Crew
import com.example.movieapp.domain.model.SeriesCrew
import com.example.movieapp.navigation.Screen
import com.example.movieapp.presentation.components.AnimatedShimmerItem
import com.example.movieapp.presentation.screens.details.CastItemBigger
import com.example.movieapp.presentation.screens.details.CrewItemBigger
import com.example.movieapp.presentation.screens.details.FilterChip
import com.example.movieapp.presentation.screens.series_details.SeriesDetailsViewModel
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.util.Constants
import com.example.movieapp.util.MovieState

@Composable
fun SeriesCastAndCrewScreen(navController: NavController, viewModel: SeriesDetailsViewModel = hiltViewModel(), seriesId: String) {

    LaunchedEffect(seriesId) {
        viewModel.fetchSeriesCastAndCrew(seriesId)
    }

    val castCrewState by viewModel.seriesCrewCastResponse.collectAsState()

    val castList = (castCrewState as? MovieState.Success)?.data?.first ?: emptyList()
    val crewList = (castCrewState as? MovieState.Success)?.data?.second ?: emptyList()

    var selectedFilter by remember { mutableStateOf("Actors") }

    val filteredCrew = remember(selectedFilter, crewList) {
        when (selectedFilter) {
            "Directors" -> crewList.filter { it.jobs.any { job -> job.job == "Director" }}
            "Producers" -> crewList.filter { it.jobs.any { job -> job.job == "Producer" || job.job == "Executive Producer" } }
            else -> emptyList()
        }
    }

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
                fontFamily = Constants.netflixFamily,
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
                        "Actors" -> items(castList) { CastItemBigger(it, navController) }
                        "Directors" -> items(filteredCrew) { crew ->
                            SeriesCrewItemBigger(crew = crew, jobTitle = "Director", navController = navController)
                        }
                        "Producers" -> items(filteredCrew) { crew ->
                            val job = crew.jobs.first { it.job == "Producer" || it.job == "Executive Producer" }
                            SeriesCrewItemBigger(crew = crew, jobTitle = job.job, navController = navController)
                        }
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
fun SeriesCrewItemBigger(crew: SeriesCrew, jobTitle: String, navController: NavController) {

        val imageUrl = "${MediaAPI.BASE_BACKDROP_IMAGE_URL}${crew.profilePath}"
        val imagePainter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .size(coil.size.Size.ORIGINAL)
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
                               fontFamily = Constants.netflixFamily,
                               fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier
                                            .padding(bottom = 5.dp),
                                maxLines = 1
                                    )

                        Text(
                                text = jobTitle,
                                fontFamily = Constants.netflixFamily,
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
                               navController.navigate(Screen.Person.route + "/${crew.id}") {
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