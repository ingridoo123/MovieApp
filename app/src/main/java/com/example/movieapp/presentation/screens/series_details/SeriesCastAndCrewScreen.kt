package com.example.movieapp.presentation.screens.series_details

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.Crew
import com.example.movieapp.presentation.screens.details.CastItemBigger
import com.example.movieapp.presentation.screens.details.CrewItemBigger
import com.example.movieapp.presentation.screens.details.FilterChip
import com.example.movieapp.presentation.screens.series_details.SeriesDetailsViewModel
import com.example.movieapp.ui.theme.background
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

    val filteredList = remember(selectedFilter, castList, crewList) {
        when (selectedFilter) {
            "Actors" -> castList.filter { it.department == "Acting" }
            "Directors" -> crewList.filter { it.job == "Director" }
            "Producers" -> crewList.filter { it.job == "Producer" }
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
                        "Actors" -> items(filteredList as List<Cast>) { CastItemBigger(it, navController) }
                        else -> items(filteredList as List<Crew>) { CrewItemBigger(it, navController) }
                    }
                }
            }

            else -> {}
        }
    }
}