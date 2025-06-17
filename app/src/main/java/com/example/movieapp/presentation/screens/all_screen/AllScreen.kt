package com.example.movieapp.presentation.screens.all_screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.movieapp.R
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.presentation.components.MovieItemAllGenreScreen
import com.example.movieapp.presentation.screens.home.HomeViewModel
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.util.Constants.netflixFamily

@Composable
fun AllGenresScreen(navController: NavController, genId:String, genName:String, viewModel: HomeViewModel = hiltViewModel()) {

    BackHandler(
        enabled = true
    ) {
        navController.popBackStack()
    }


    var title by remember {
        mutableStateOf(genName)
    }
    val genresAllMovies: LazyPagingItems<Movie>? = viewModel.genresWiseMovieListState?.collectAsLazyPagingItems()
    val seriesAllMovies: LazyPagingItems<Movie>? = viewModel.seriesWiseListState?.collectAsLazyPagingItems()

    LaunchedEffect(key1 = genId) {
        viewModel.setGenreData(genId.toInt())
    }

    val tabs = listOf("All", "Movies", "Series")
    var selectedTab by remember {
        mutableStateOf("All")
    }


    val genreImages = mapOf(
        "Action" to R.drawable.action_bc_c,
        "Adventure" to R.drawable.adventure_bc_a,
        "Animation" to R.drawable.animation_bc_c,
        "Comedy" to R.drawable.comedy_bc_a,
        "Documentary" to R.drawable.doc_bc_a,
        "Horror" to R.drawable.horror_bc_a,
        "Drama" to R.drawable.drama_bc_a,
        "Romance" to R.drawable.romance_bc_a,
        "Sci-Fi" to R.drawable.sci_fi_bc_b,
        "Thriller" to R.drawable.thriller_bc_b
    )

    val backgroundImage = genreImages[genName] ?: R.drawable.thriller_bc_b

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Blue)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = backgroundImage),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            background.copy(0.85f)
                        )
                    )
                ))

            Text(
                text = title,
                fontSize = 24.sp,
                fontFamily = netflixFamily,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 10.dp, bottom = 10.dp),
                color = Color.White.copy(alpha = 0.8f),
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 8.dp, top = 8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { navController.popBackStack() }
                    .background(top_bar_component.copy(0.6f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "IosBack",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .padding(end = 3.dp)
                        .size(21.dp)
                )
            }

        }
        Spacer(modifier = Modifier.height(20.dp))


        val selectedIndex = tabs.indexOf(selectedTab)
        val animatedIndex by animateFloatAsState(
            targetValue = selectedIndex.toFloat(),
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
            label = "tab_animation"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(30.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(top_bar_component, shape = RoundedCornerShape(12.dp))
        ) {

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val tabWidth = size.width / tabs.size
                val indicatorX = animatedIndex * tabWidth

                drawRoundRect(
                    color = component,
                    topLeft = Offset(indicatorX, 0f),
                    size = Size(tabWidth, size.height),
                    cornerRadius = CornerRadius(12.dp.toPx())
                )
            }

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tab ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { selectedTab = tab },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            color = if(selectedTab == tab) Color.White.copy(0.8f) else componentLighter,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = netflixFamily
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .padding(top = 10.dp),
        ) {
            val itemsToDisplay = when (selectedTab) {
                "Series" -> seriesAllMovies
                else -> genresAllMovies
            }

            if (itemsToDisplay == null || itemsToDisplay.loadState.refresh is LoadState.Loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = component,
                        modifier = Modifier
                            .size(100.dp)
                            .scale(0.7f)
                    )
                }
            } else {
                val listState = rememberLazyGridState()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = listState,
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(itemsToDisplay.itemCount) { index ->
                        itemsToDisplay[index]?.let {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally))  {
                                MovieItemAllGenreScreen(
                                    movie = it,
                                    navController = navController,
                                    genre = genName
                                )
                            }

                        }
                    }
                }
            }
        }


    }

}