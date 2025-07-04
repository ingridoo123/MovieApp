package com.example.movieapp.presentation.screens.series_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.movieapp.presentation.components.CircleLoader
import com.example.movieapp.presentation.components.SeriesItemBig
import com.example.movieapp.presentation.components.SeriesItemSmallSimilar
import com.example.movieapp.presentation.screens.series_details.SeriesDetailsViewModel
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.util.Constants
import com.example.movieapp.util.MovieState

@Composable
fun SimilarSeriesScreen(navController: NavController, seriesId: String, viewModel: SeriesDetailsViewModel = hiltViewModel()) {

    LaunchedEffect(seriesId) {
        viewModel.fetchSimilarSeries(seriesId)
    }

    val similarSeriesState by viewModel.similarSeriesResponse.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(background),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.Transparent)
        ) {
            Text(
                text = "Similar Series",
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
        Spacer(modifier = Modifier.height(15.dp))

        when (val state = similarSeriesState) {
            is MovieState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(background),
                    contentAlignment = Alignment.Center
                ) {
                    CircleLoader(
                        modifier = Modifier.size(100.dp),
                        color = componentLighter,
                        secondColor = null,
                        tailLength = 250f
                    )
                }

            }

            is MovieState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.message,
                        color = Color.Red,
                    )
                }

            }

            is MovieState.Success -> {
                val similarList = state.data?.results
                val listState = rememberLazyGridState()
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if(similarList != null) {
                        val filteredSimilarList = similarList.filter { it.originalLanguage != "zh" && it.originalLanguage != "kn" && it.originalLanguage != "hi"}
                        items(filteredSimilarList.size) { index ->
                            filteredSimilarList[index].let {
                                SeriesItemBig(series = it, navController = navController)
                            }

                        }
                    }
                }
            }
        }

    }
}