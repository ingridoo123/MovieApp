package com.example.movieapp.presentation.screens.details

import android.content.Intent
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.movieapp.R
import com.example.movieapp.data.remote.MediaAPI.Companion.BASE_BACKDROP_IMAGE_URL
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.presentation.components.CircleLoader
import com.example.movieapp.presentation.components.PersonMoviesComponent
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.util.Constants.netflixFamily
import com.example.movieapp.util.MovieState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PersonScreen(personId: String, navController: NavController, viewModel: MovieDetailsViewModel = hiltViewModel()) {


    val personDetailsState by viewModel.personDetailsResponse.collectAsState()
    val personMovieCreditsResponse by viewModel.personMovieCreditsResponse.collectAsState()

    val contextCurrent = LocalContext.current

    LaunchedEffect(personId) {
        if(personId.isNotEmpty()) {
            viewModel.fetchPersonDetails(personId)
        }
    }

    val hazeState = remember {
        HazeState()
    }
    Log.d("Personnn", personId)

    var personDepartment by remember { mutableStateOf("") }


    when (val state = personDetailsState) {
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

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(background)
                    .verticalScroll(rememberScrollState())
            ) {
                when (state) {
                    is MovieState.Success -> {
                        val person = state.data
                        Log.d("PersonScreen","${person?.id}")
                        if (person != null) {

                            personDepartment = person.department
                            val displayDepartment = when (person.department) {
                                "Acting" -> "Actor"
                                "Directing" -> "Director"
                                "Production" -> "Producer"
                                "Writing" -> "Writer"
                                else -> person.department
                            }


                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(540.dp)
                                    .background(background)
                            ) {
                                Box(
                                    modifier = Modifier
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
                                        model = BASE_BACKDROP_IMAGE_URL + person.profilePath,
                                        contentDescription = person.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(540.dp)
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
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowBackIosNew,
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
                                                .clickable {

                                                    val sendIntent = Intent().apply {
                                                        action = Intent.ACTION_SEND
                                                        val shareMessage = """
                                                        🎬 Check out ${person.name} on MovieApp!
                                                        
                                                        ${
                                                            if (!person.biography.isNullOrBlank()) person.biography.take(
                                                                180
                                                            ) + "..." else "Discover the works and career of this amazing talent."
                                                        }
                                                        
                                                        Explore their full filmography and more in the MovieApp now!
                                                        👉 Download the app or visit: https://yourmovieapp.link/person/${person.id}
                                                        """.trimIndent()
                                                        putExtra(Intent.EXTRA_TEXT, shareMessage)
                                                        type = "text/plain"
                                                    }
                                                    val shareIntent = Intent.createChooser(sendIntent, null)
                                                    contextCurrent.startActivity(shareIntent)
                                                }
                                                .padding(bottom = 2.dp)
                                                .background(Color.Transparent, shape = CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.IosShare,
                                                contentDescription = "IosBack",
                                                tint = Color.White.copy(alpha = 0.8f),
                                                modifier = Modifier.size(25.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(370.dp))
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
                                        Box(
                                            modifier = Modifier
                                                .height(32.dp)
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
                            person.let {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp)

                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Date of birth:",
                                            fontSize = 15.sp,
                                            fontFamily = netflixFamily,
                                            color = componentLighter
                                        )
                                        Text(
                                            text = formatDateWithAge(it.birthday, it.deathDay),
                                            fontSize = 15.sp,
                                            fontFamily = netflixFamily,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                    Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = top_bar_component)

                                    it.deathDay?.let { deathDay ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Date of death",
                                                fontSize = 15.sp,
                                                fontFamily = netflixFamily,
                                                color = componentLighter
                                            )
                                            Text(
                                                text = formatDateWithAge2(it.deathDay),
                                                fontSize = 15.sp,
                                                fontFamily = netflixFamily,
                                                color = Color.White.copy(alpha = 0.8f)
                                            )
                                        }
                                        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = top_bar_component)
                                    }


                                    it.placeOfBirth?.let { place ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Place of birth",
                                                fontSize = 15.sp,
                                                fontFamily = netflixFamily,
                                                color = componentLighter,
                                                modifier = Modifier.padding(end = 5.dp)
                                            )
                                            Text(
                                                text = it.placeOfBirth,
                                                fontSize = 15.sp,
                                                fontFamily = netflixFamily,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                color = Color.White.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                    Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = top_bar_component)

                                    val genderText = when (it.gender) {
                                        1 -> "Female"
                                        2 -> "Male"
                                        else -> "Other/Unknown"
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Gender",
                                            fontSize = 15.sp,
                                            fontFamily = netflixFamily,
                                            color = componentLighter
                                        )
                                        Text(
                                            text = genderText,
                                            fontSize = 15.sp,
                                            fontFamily = netflixFamily,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )

                                    }
                                    Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = top_bar_component)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Biography",
                                        fontSize = 18.sp,
                                        fontFamily = netflixFamily,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))

                                    Text(
                                        text = it.biography ?: "No description, we are sorry :c",
                                        fontSize = 12.sp,
                                        fontFamily = netflixFamily,
                                        color = componentLighter,
                                        lineHeight = 17.sp
                                    )
                                }
                            }

                        }
                    }

                    is MovieState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(background),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                state.message, color = Color.Red, modifier = Modifier
                                    .padding(16.dp)
                            )
                        }

                    }

                    else -> {}
                }

                Spacer(modifier = Modifier.height(20.dp))
                when(val creditsState = personMovieCreditsResponse) {
                    is MovieState.Loading -> {

                    }
                    is MovieState.Success -> {

                        PersonMoviesComponent(
                            navController = navController,
                            movieCreditsResponse = creditsState.data,
                            personDepartment = personDepartment
                        )
                    }
                    is MovieState.Error -> {
                        Text(
                            creditsState.message, color = Color.Red, modifier = Modifier
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}


fun formatDateWithAge(dateStr: String?, endDateStr: String? = null): String {
    if (dateStr.isNullOrEmpty()) return "Unknown"

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)

    return try {
        val birthDate = LocalDate.parse(dateStr, formatter)
        val endDate = endDateStr?.let { LocalDate.parse(it, formatter) } ?: LocalDate.now()
        val age = Period.between(birthDate, endDate).years

        "${birthDate.format(displayFormatter)} (age $age)"
    } catch (e: Exception) {
        "Unknown"
    }
}

fun formatDateWithAge2(deathDateStr: String?): String {
    if (deathDateStr.isNullOrEmpty()) return "Unknown"

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)

    return try {
        val date = LocalDate.parse(deathDateStr, formatter)
        date.format(displayFormatter)
    } catch (e: Exception) {
        "Unknown"
    }
}


@Composable
@Preview(showBackground = true)
fun PersonScreenPreview() {
    //PersonScreen(personName =" Jack Black")
}