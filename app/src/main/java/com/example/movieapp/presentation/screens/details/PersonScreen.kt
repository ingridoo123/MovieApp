package com.example.movieapp.presentation.screens.details

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.movieapp.R
import com.example.movieapp.data.remote.MediaAPI.Companion.BASE_BACKDROP_IMAGE_URL
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.util.Constants.netflixFamily
import com.example.movieapp.util.MovieState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

@Composable
fun PersonScreen(personId: String, navController: NavController, viewModel: MovieDetailsViewModel = hiltViewModel()) {


    val personDetailsState by viewModel.personDetailsResponse.collectAsState()

    LaunchedEffect(personId) {
        if(personId.isNotEmpty()) {
            viewModel.fetchPersonDetails(personId)
        }
    }

    val hazeState = remember {
        HazeState()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        when(val state = personDetailsState) {
            is MovieState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is MovieState.Success -> {
                val person = state.data
                if(person != null) {

                    val displayDepartment = when (person.department) {
                        "Acting" -> "Actor"
                        "Directing" -> "Director"
                        "Production" -> "Producer"
                        "Writing" -> "Writer"
                        else -> person.department
                    }

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(540.dp)
                        .haze(
                            state = hazeState,
                            backgroundColor = top_bar_component.copy(alpha = 0.7f),
                            tint = top_bar_component.copy(alpha = 0.2f),
                            blurRadius = 15.dp
                        )
                    ) {
                        AsyncImage(
                            model = BASE_BACKDROP_IMAGE_URL+ person.profilePath,
                            contentDescription = person.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(540.dp) // Wysokość obrazu
                        )
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        background.copy(0.5f),
                                    ), startY = 100f
                                )
                            )
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(540.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .hazeChild(hazeState, shape = CircleShape)
                                    .clip(shape = CircleShape)
                                    .padding(end = 2.dp)
                                    .clickable { navController.popBackStack() }
                                    .background(Color.Transparent, shape = CircleShape),
                                contentAlignment =  Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBackIosNew ,
                                    contentDescription = "IosBack",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .hazeChild(hazeState, shape = CircleShape)
                                    .clip(shape = CircleShape)
                                    .clickable { }
                                    .padding(bottom = 2.dp)
                                    .background(Color.Transparent, shape = CircleShape),
                                contentAlignment =  Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.IosShare,
                                    contentDescription = "IosBack",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(350.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = person.name,
                                fontSize = 25.sp,
                                fontFamily = netflixFamily,
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(modifier = Modifier
                                .height(35.dp)
                                .wrapContentWidth()
                                .hazeChild(hazeState, shape = RoundedCornerShape(15.dp))
                                .background(
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(start = 20.dp, end = 20.dp, bottom = 1.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = displayDepartment,
                                    fontFamily = netflixFamily,
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                )
                            }

                        }
                    }
                    

                }
            }

            is MovieState.Error -> {
                Text(state.message, color = Color.Red, modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp))
            }
        }

    }
}

@Composable
@Preview(showBackground = true)
fun PersonScreenPreview() {
    //PersonScreen(personName =" Jack Black")
}