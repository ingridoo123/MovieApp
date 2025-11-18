package com.example.movieapp.presentation.components

import android.util.Log
import android.widget.RatingBar
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.rounded.ImageNotSupported
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.movieapp.R
import com.example.movieapp.data.local.media.MediaEntity
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.data.remote.MediaAPI.Companion.BASE_BACKDROP_IMAGE_URL
import com.example.movieapp.data.remote.MediaAPI.Companion.IMAGE_BASE_URL
import com.example.movieapp.data.remote.respond.MovieDetailsDTO
import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.Crew
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.Series
import com.example.movieapp.navigation.Screen
import com.example.movieapp.presentation.screens.favourite.FavouriteViewModel
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.util.Constants.netflixFamily
import com.example.movieapp.util.getAverageColor
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.absoluteValue

@Composable
fun MovieItemLoadingPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(16.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(color = ShimmerDarkGray)
        ) {
            AnimatedShimmerItem()
        }
    }
}

@Composable
fun MovieDataItem(movieInfo: MovieDetailsDTO?, navController: NavController, viewModel: FavouriteViewModel, bookmarkImageUrl: String) {

    val hazeState = remember {
        HazeState()
    }

    viewModel.isFavourite(movieInfo!!.id)
    var isFavourite = viewModel.isFavourite.value

    val date = SimpleDateFormat.getDateInstance().format(Date())
    val context = LocalContext.current

    val myMovieInfo = MediaEntity(
        mediaId = movieInfo.id,
        imagePath = bookmarkImageUrl,
        title = movieInfo.title,
        releaseDate = movieInfo.releaseDate,
        rating = movieInfo.voteAverage,
        addedOn = System.currentTimeMillis(),
        mediaType = "movie"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .haze(
                    state = hazeState,
                    backgroundColor = top_bar_component.copy(alpha = 0.7f),
                    tint = top_bar_component.copy(alpha = 0.2f),
                    blurRadius = 15.dp
                )
        ) {
            SubcomposeAsyncImage(
                model = BASE_BACKDROP_IMAGE_URL + movieInfo!!.backdropPath,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize().background(background.copy(0.5f))
                    )
                },
                modifier = Modifier.fillMaxWidth().height(350.dp) //usunite fillMaxWidth zobaczym jak teraz bedzie
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
            ) {}
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .hazeChild(hazeState, shape = CircleShape)
                        .clip(shape = CircleShape)
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
                        .height(40.dp)
                        .width(40.dp)
                        .clip(shape = RoundedCornerShape(20.dp))
                        .clickable {
                            if (isFavourite != 0) {
                                viewModel.removeFromFavourites(movieInfo.id)
                                Toast
                                    .makeText(
                                        context,
                                        "Remove from your Favourites",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            } else {
                                viewModel.addToFavourites(myMovieInfo)
                                Toast
                                    .makeText(
                                        context,
                                        "Added to your Favourites",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        }
                        .hazeChild(hazeState, shape = RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavourite != 0) Icons.Default.Bookmark else Icons.Default.BookmarkBorder ,
                        contentDescription = "",
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-20).dp, y = (-6).dp)
                .width(50.dp)
                .height(19.dp)
                .background(
                    color = Color.White.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(20.dp)
                )
                .clip(RoundedCornerShape(20.dp))
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(painter = painterResource(id = R.drawable.ic_star), contentDescription = "star", modifier = Modifier.size(18.dp))
            Text(
                text = movieInfo?.voteAverage.toString().take(6),
                fontFamily = netflixFamily,
                color = background,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 1.dp)
            )
        }


    }
}

@Composable
fun MovieDataItemEmpty(navController: NavController) {

    val hazeState = remember {
        HazeState()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .haze(
                    state = hazeState,
                    backgroundColor = top_bar_component.copy(alpha = 0.7f),
                    tint = Color.Transparent,
                    blurRadius = 15.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedShimmerItem()
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
            ) {}
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .hazeChild(hazeState, shape = CircleShape)
                        .clip(shape = CircleShape)
                        .clickable { navController.popBackStack() }
                        .background(Color.Transparent, shape = CircleShape),
                    contentAlignment =  Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew ,
                        contentDescription = "IosBack",
                        tint = Color.White.copy(alpha = 0.8f),
                    )
                }

                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(40.dp)
                        .clip(shape = RoundedCornerShape(20.dp))
                        .clickable { }
                        .hazeChild(hazeState, shape = RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder ,
                        contentDescription = "",
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

        }


    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoSlidingCarousel2(
    images: List<Movie>,
    modifier: Modifier = Modifier,
    slideDuration: Long = 4000L,
    currentIndex: Int,
    onIndexChanged: (Int) -> Unit,
    navController: NavController,
    onPageChanged: ((Int) -> Unit)? = null
) {
    val pagerState = rememberPagerState(
        initialPage = currentIndex,
        pageCount = { images.size }
    )

    var isUserScrolling by remember { mutableStateOf(false) }
    var isAutoScrolling by remember { mutableStateOf(false) }

    // Synchronizacja pagerState z currentIndex - tylko dla automatycznego przesuwania
    LaunchedEffect(currentIndex) {
        if (!isUserScrolling && pagerState.currentPage != currentIndex) {
            isAutoScrolling = true
            pagerState.animateScrollToPage(currentIndex)
            isAutoScrolling = false
        }
    }

    // Natychmiastowa aktualizacja podczas scrollowania przez użytkownika
    LaunchedEffect(pagerState.currentPage, pagerState.currentPageOffsetFraction) {
        if (pagerState.isScrollInProgress && !isAutoScrolling) {
            // Natychmiastowa aktualizacja tylko podczas ręcznego przeciągania
            onPageChanged?.invoke(pagerState.currentPage)
        }
    }

    // Śledzenie zmian strony przez użytkownika
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress && !isAutoScrolling && pagerState.currentPage != currentIndex) {
            onIndexChanged(pagerState.currentPage)
        }
        isUserScrolling = pagerState.isScrollInProgress && !isAutoScrolling
    }

    // Automatyczne przesuwanie
    LaunchedEffect(currentIndex, isUserScrolling) {
        if (images.isNotEmpty() && !isUserScrolling) {
            delay(slideDuration)
            val nextIndex = (currentIndex + 1) % images.size
            onIndexChanged(nextIndex)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = modifier
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val scale = lerp(0.85f, 1f, 1f - pageOffset.absoluteValue.coerceIn(0f, 1f))
            val alpha = lerp(0.6f, 1f, 1f - pageOffset.absoluteValue.coerceIn(0f, 1f))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .clickable {
                        if (page == pagerState.currentPage) {
                            navController.navigate(Screen.Details.route + "/${images[page].id}")
                        } else {
                            onIndexChanged(page)
                        }
                    }
            ) {
                val imagePainter = rememberAsyncImagePainter(
                    model = BASE_BACKDROP_IMAGE_URL + images[page].posterPath
                )

                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = imagePainter,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                )
            }
        }

        // Wskaźniki - używają currentIndex dla automatycznego, pagerState.currentPage dla ręcznego
        if (images.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(top = 5.dp)
            ) {
                images.forEachIndexed { index, _ ->
                    val isActive = if (isUserScrolling || pagerState.isScrollInProgress) {
                        index == pagerState.currentPage
                    } else {
                        index == currentIndex
                    }
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .width(if (isActive) 40.dp else 15.dp)
                            .height(4.dp)
                            .background(if (isActive) Color.White else Color.Gray)
                            .animateContentSize(
                                animationSpec = tween(200)
                            )
                    )
                }
            }
        }
    }
}



@Composable
fun AutoSlidingCarousel(
    images: List<Movie>,
    modifier: Modifier = Modifier,
    slideDuration: Long = 4000L,
    currentIndex: Int,
    onIndexChanged: (Int) -> Unit,
    navController: NavController
) {
    LaunchedEffect(key1 = currentIndex, key2 = images) {
        if (images.isNotEmpty()) {
            delay(slideDuration)
            val nextIndex = (currentIndex + 1) % images.size
            onIndexChanged(nextIndex)

        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = modifier) {
            if(images.isNotEmpty()) {
                val imagePainter = rememberAsyncImagePainter(model = BASE_BACKDROP_IMAGE_URL + images[currentIndex].posterPath)
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { navController.navigate(Screen.Details.route + "/${images[currentIndex].id}") },
                    painter = imagePainter,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,

                )
            }

        }
        if(images.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(top = 5.dp)
            ) {
                images.forEachIndexed { index, movie ->
                    val size by animateDpAsState(targetValue = if (index == currentIndex) 12.dp else 8.dp)
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .width(if (index == currentIndex) 40.dp else 15.dp)
                            .height(4.dp)
                            .background(if (index == currentIndex) Color.White else Color.Gray)
                    )
                }
            }
        }
    }
    

}

@Composable
fun GenreBlock(name: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(color = top_bar_component)
            .clickable { onClick() }
    ) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp),
            text = name,
            fontFamily = netflixFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            maxLines = 1,
            color = Color.White.copy(alpha = 0.8f)
        )

    }
}

@Composable
fun MovieItemAllGenreScreen(
    movie: Movie,
    navController: NavController,
    genre: String
) {
    val title = movie.title
    val imageUrl = "${BASE_BACKDROP_IMAGE_URL}${movie.posterPath}"
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )
    val imageState = imagePainter.state

    Column(
        modifier = Modifier
            .width(171.dp)
            .height(299.dp)
    ) {
        Box(
            modifier = Modifier
                .height(256.dp)
                .width(171.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (imageState is AsyncImagePainter.State.Success) {
                val imageBitmap = imageState.result.drawable.toBitmap()

                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { navController.navigate(Screen.Details.route + "/${movie.id}") }
                )
            }

            if (imageState is AsyncImagePainter.State.Error) {
                Icon(
                    imageVector = Icons.Default.ImageNotSupported,
                    contentDescription = "error",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    tint = component
                )
            }
            if (imageState is AsyncImagePainter.State.Loading) {
                AnimatedShimmerItem()
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                fontFamily = netflixFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 5.dp, bottom = 2.dp),
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 1,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = movie.releaseDate?.take(4) ?: "null",
                    fontFamily = netflixFamily,
                    color = componentLighter,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(end = 4.dp),
                )
                Icon(
                    imageVector = Icons.Default.Circle,
                    modifier = Modifier
                        .size(10.dp)
                        .padding(end = 4.dp),
                    tint = componentLighter,
                    contentDescription = "circle"
                )
                Text(
                    text = genre,
                    fontFamily = netflixFamily,
                    color = componentLighter,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(end = 2.dp)
                )
            }
        }
    }
}

@Composable
fun MovieItemSmallSimilar(movie: Movie, navController: NavController) {

    val title = movie.title
    val imageUrl = "${BASE_BACKDROP_IMAGE_URL}${movie.posterPath}"
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )
    val imageState = imagePainter.state

    val genreMap = mapOf(
        28 to "Action", 12 to "Adventure", 16 to "Animation", 35 to "Comedy", 80 to "Crime",
        99 to "Documentary", 18 to "Drama", 10751 to "Family", 14 to "Fantasy", 36 to "History",
        27 to "Horror", 10402 to "Music", 9648 to "Mystery", 10749 to "Romance", 878 to "Sci-Fi",
        10770 to "TV Movie", 53 to "Thriller", 10752 to "War", 37 to "Western"
    )


    val genre = movie.genreIds?.firstOrNull()?.let { genreMap[it] } ?: "N/A"

    Column(
        modifier = Modifier
            .width(126.dp)
            .height(234.dp)
    ) {
        Box(
            modifier = Modifier
                .height(189.dp)
                .width(126.dp)
                .background(color = component, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (imageState is AsyncImagePainter.State.Success) {
                val imageBitmap = imageState.result.drawable.toBitmap()

                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { navController.navigate(Screen.Details.route + "/${movie.id}") }
                )
            }

            if (imageState is AsyncImagePainter.State.Error) {
                Icon(
                    imageVector = Icons.Default.ImageNotSupported,
                    contentDescription = "error",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    tint = componentLighter
                )
            }

            if (imageState is AsyncImagePainter.State.Loading) {
                AnimatedShimmerItem()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                fontFamily = netflixFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 5.dp, bottom = 2.dp),
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 1,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if(movie.releaseDate?.take(4)?.isNotEmpty() == true) movie.releaseDate.take(4) else "N/A",
                    fontFamily = netflixFamily,
                    color = componentLighter,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(end = 4.dp),
                )
                Icon(
                    imageVector = Icons.Default.Circle,
                    modifier = Modifier
                        .size(10.dp) // bez zmian
                        .padding(end = 4.dp),
                    tint = componentLighter,
                    contentDescription = "circle"
                )
                Text(
                    text = genre ?: "N/A",
                    fontFamily = netflixFamily,
                    color = componentLighter,
                    fontSize = 11.sp, // bez zmian
                    modifier = Modifier.padding(end = 2.dp)
                )
            }
        }
    }

}

@Composable
fun SeriesItemSmallSimilar(series: Series, navController: NavController) {

    val title = series.name
    val imageUrl = "${MediaAPI.BASE_BACKDROP_IMAGE_URL}${series.posterPath}"
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

    Log.d("SimilarSeries","${series.name}, ${series.posterPath}")

    Column(
        modifier = Modifier
            .width(126.dp)
            .height(234.dp)
    ) {
        Box(
            modifier = Modifier
                .height(189.dp)
                .width(126.dp)
                .background(color = component, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (imageState is AsyncImagePainter.State.Success) {
                val imageBitmap = imageState.result.drawable.toBitmap()
                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { navController.navigate(Screen.SeriesDetails.route + "/${series.id}") }
                )
            }

            if (imageState is AsyncImagePainter.State.Error) {
                Icon(
                    imageVector = Icons.Default.ImageNotSupported,
                    contentDescription = "error",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    tint = componentLighter
                )
            }

            if (imageState is AsyncImagePainter.State.Loading) {
                AnimatedShimmerItem()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                fontFamily = netflixFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 5.dp, bottom = 2.dp),
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 1,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if(series.firstAirDate?.take(4)?.isNotEmpty() == true) series.firstAirDate.take(4) else "N/A",
                    fontFamily = netflixFamily,
                    color = componentLighter,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(end = 4.dp),
                )
                Icon(
                    imageVector = Icons.Default.Circle,
                    modifier = Modifier
                        .size(10.dp)
                        .padding(end = 4.dp),
                    tint = componentLighter,
                    contentDescription = "circle"
                )
                Text(
                    text = genre,
                    fontFamily = netflixFamily,
                    color = componentLighter,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(end = 2.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun MovieCastComponent(castList: List<Cast>, navController: NavController, movieId: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 15.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cast & Crew",
                fontFamily = netflixFamily,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
            IconButton(onClick = {
                navController.navigate(Screen.CastAndCrew.route + "/$movieId") {
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "See More",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        val groupedList = castList.chunked(3)
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            items(groupedList) { columnItems ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    columnItems.forEach { cast ->
                        CastItem(cast = cast, navController)
                    }
                }
                
            }
        }
    }
}



@Composable
fun CastItem(cast: Cast, navController: NavController) {

    val imageUrl = "${BASE_BACKDROP_IMAGE_URL}${cast.profilePath}"
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )
    val imageState = imagePainter.state


    Row(
        modifier = Modifier
            .width(210.dp)
            .height(60.dp)
            .clickable { navController.navigate(Screen.Person.route + "/${cast.id}") }
            .background(top_bar_component, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
           modifier = Modifier
               .size(50.dp)
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

        Spacer(modifier = Modifier.width(8.dp))
        Column {

            Text(
                text = cast.name,
                fontFamily = netflixFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .padding(bottom = 2.dp)
                    .fillMaxWidth(),
                maxLines = 1
            )

            Text(
                text = cast.department,
                fontFamily = netflixFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.8f),

            )


        }
    }
}

@Composable
fun CastItemShimmer() {
    Row(
        modifier = Modifier
            .width(210.dp)
            .height(60.dp)
            .background(top_bar_component, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 5.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(component),
            contentAlignment = Alignment.Center
        ) {
            AnimatedShimmerItem()
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(component)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(component)
            )
        }
    }
}
@Composable
fun MovieCastLoading() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 15.dp)
    ) {
        Text(
            text = "Cast & Crew",
            fontFamily = netflixFamily,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(25.dp)) {
            items(5) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    repeat(3) {
                        CastItemShimmer()
                    }
                }
            }
        }
    }
}



