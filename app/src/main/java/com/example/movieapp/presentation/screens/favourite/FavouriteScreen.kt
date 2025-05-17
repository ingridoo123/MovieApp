package com.example.movieapp.presentation.screens.favourite

import android.text.BoringLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.deleteRed
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.util.Constants
import com.example.movieapp.util.Constants.netflixFamily

@Composable
fun FavouriteScreen(navController: NavController, viewModel: FavouriteViewModel = hiltViewModel(), viewModel2: MovieDetailsViewModel = hiltViewModel()) {

    val myMoviesDataFlow = viewModel.myMovieData.value
    val myMoviesDataOriginal by myMoviesDataFlow.collectAsState(initial = null)

    var isEditMode by remember { mutableStateOf(false) }

    val sortOptions = listOf("Date added to the list", "Title (A-Z)", "Release date")
    var showSortDialog by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf(sortOptions[0]) }


    val sortedMovies = remember(myMoviesDataOriginal, selectedSort) {
        myMoviesDataOriginal?.let { movies ->
            when (selectedSort) {
                "Date added to the list" -> movies
                "Title (A-Z)" -> movies.sortedBy { it.title?.lowercase() ?: "" }
                "Release date" -> movies.sortedByDescending { it.releaseDate ?: "" }
                else -> movies
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(color = top_bar_component),
        ) {
            Text(
                text = if (isEditMode) "Edit" else "My List",
                fontSize = 19.sp,
                fontFamily = netflixFamily,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center),
                color = Color.White.copy(alpha = 0.8f)
            )


            if (isEditMode) {
                Text(
                    text = "Finished",
                    fontSize = 14.sp,
                    fontFamily = netflixFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { isEditMode = !isEditMode }
                        .padding(end = 17.dp, top = 2.dp)
                )
            } else {
                IconButton(
                    onClick = { isEditMode = !isEditMode },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White.copy(0.8f)
                    )
                }
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
            Box(
                modifier = Modifier
                    .padding(top = 1.dp)
                    .clickable { showSortDialog = true }
                    .background(Color.Transparent)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedSort,
                        fontSize = 15.sp,
                        fontFamily = netflixFamily,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }

            }
        }
        Spacer(modifier = Modifier.height(5.dp))

        if (myMoviesDataOriginal == null) {
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

                items(
                    items = sortedMovies ?: emptyList(),
                    key = { it.mediaId }
                ) { movie ->


                    var visible by remember { mutableStateOf(true) }

                    AnimatedVisibility(
                        visible = visible,
                        exit = slideOutVertically(
                            animationSpec = tween(durationMillis = 4000),
                            targetOffsetY = { -it }
                        ) + fadeOut(animationSpec = tween(4000))
                    ) {
                        FavouriteItem(
                            navController = navController,
                            imageUrl = BASE_BACKDROP_IMAGE_URL + (movie.imagePath ?: ""),
                            isEditMode = isEditMode,
                            title = movie.title,
                            onClick = {
                                if (!isEditMode) {
                                    navController.navigate(Screen.Details.route + "/${movie.mediaId}")
                                }
                            },
                            onDeleteClick = {
                                visible = false
                                viewModel.removeFromFavourites(movie.mediaId)
                            }
                        )
                    }
                }
            }
        }
    }
        if (showSortDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(background.copy(alpha = 0.8f))
                    .clickable(enabled = true, onClick = { showSortDialog = false }),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .width(300.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(top_bar_component)
                        .padding(vertical = 5.dp, horizontal = 10.dp)
                        .clickable(enabled = false, onClick = {}),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sort by",
                            fontSize = 18.sp,
                            fontFamily = netflixFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        IconButton(onClick = { showSortDialog = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    sortOptions.forEach { option ->

                        var isPressed by remember { mutableStateOf(false) }

                        val alphaAnim by animateFloatAsState(
                            targetValue = if (isPressed) 0.5f else 0.8f,
                            animationSpec = tween(durationMillis = 200)
                        )

                        val scaleAnim by animateFloatAsState(
                            targetValue = if (isPressed) 0.95f else 1f,
                            animationSpec = tween(durationMillis = 200)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            isPressed = true
                                            try {
                                                awaitRelease()
                                                selectedSort = option
                                                showSortDialog = false
                                            } finally {
                                                isPressed = false
                                            }
                                        }
                                    )
                                }
                                .padding(vertical = 8.dp)
                                .graphicsLayer(
                                    scaleX = scaleAnim,
                                    scaleY = scaleAnim,
                                    alpha = alphaAnim
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selectedSort == option) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Zaznaczone",
                                    tint = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Spacer(modifier = Modifier.width(20.dp))
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = option,
                                fontSize = 15.sp,
                                fontFamily = netflixFamily,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FavouriteItem(navController: NavController, imageUrl: String, title: String, isEditMode: Boolean, onClick: () -> Unit, onDeleteClick:() -> Unit) {



    val dismissState = rememberDismissState(
        confirmStateChange = {
            if(it == DismissValue.DismissedToStart) {
                onDeleteClick()
            }
            false
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),

        background = {
            val color = when (dismissState.targetValue) {
                DismissValue.Default -> Color.Transparent
                else -> deleteRed
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, shape = RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = "Delete Icon",
                    tint = top_bar_component
                )
            }
        },
        dismissContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(77.dp)
                    .clickable(enabled = !isEditMode) { onClick() }
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = top_bar_component, shape = RoundedCornerShape(10.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(5.dp))
                        .width(150.dp),
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = imageUrl)
                            .size(Size.ORIGINAL)
                            .build()
                    ),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = title,
                    fontFamily = netflixFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                if (isEditMode) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(durationMillis = 800)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 800))
                    ) {
                        IconButton(onClick = { onDeleteClick() }) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = "Delete",
                                tint = deleteRed
                            )
                        }
                    }
                }
            }
        }
    )

}