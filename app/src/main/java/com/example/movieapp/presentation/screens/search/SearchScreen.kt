package com.example.movieapp.presentation.screens.search


import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.movieapp.R
import com.example.movieapp.data.local.media.MediaEntity
import com.example.movieapp.data.remote.MediaAPI.Companion.BASE_BACKDROP_IMAGE_URL
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.toMovie
import com.example.movieapp.navigation.Screen
import com.example.movieapp.presentation.components.AnimatedShimmerItem
import com.example.movieapp.presentation.components.CircleLoader
import com.example.movieapp.presentation.components.CountryPickerDialog
import com.example.movieapp.presentation.components.YearPicker
import com.example.movieapp.presentation.screens.favourite.FavouriteViewModel
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.ui.theme.whiteCopy
import com.example.movieapp.util.Constants.netflixFamily
import com.example.movieapp.util.MovieState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

val tmdbLanguageToCountryMap = mapOf(
    "en" to "English (USA/UK)",
    "es" to "Spanish (Spain/Latin America)",
    "fr" to "French (France)",
    "de" to "German (Germany)",
    "ja" to "Japanese (Japan)",
    "ko" to "Korean (Korea)",
    "hi" to "Hindi (India)",
    "zh" to "Chinese (China/HK/SG/TW)",
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel = hiltViewModel(), viewModel2: FavouriteViewModel = hiltViewModel()) {

    //val searchResults = viewModel.multiSearchState.value.collectAsLazyPagingItems()
    val popularState by viewModel.popularMovieResponse.collectAsState()
    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }
    val movieImagesMap = viewModel.movieImagesMap

    var searchSubmitted by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current


    var isGenresExpanded by rememberSaveable { mutableStateOf(false) }

    val genreMap = mapOf(
        28 to "Action", 12 to "Adventure", 16 to "Animation", 35 to "Comedy", 80 to "Crime",
        99 to "Documentary", 18 to "Drama", 10751 to "Family", 14 to "Fantasy", 36 to "History",
        27 to "Horror", 10402 to "Music", 9648 to "Mystery", 10749 to "Romance", 878 to "Sci-Fi",
        10770 to "TV Movie", 53 to "Thriller", 10752 to "War", 37 to "Western"
    )

    val initialGenres = listOf("Action", "Adventure", "Comedy", "Thriller", "Horror", "Documentary")
    val expandedGenres = listOf("Animation", "Sci-Fi", "Romance", "Music")
    val displayedGenres = if (isGenresExpanded) initialGenres + expandedGenres else initialGenres



    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true, animationSpec = tween(
        durationMillis = 550,
        easing = FastOutSlowInEasing
    )
    )

    var selectedMovie by remember { mutableStateOf<Movie?>(null) }
    val isFavourite = remember { mutableStateOf(0) }

    LaunchedEffect(selectedMovie) {
        if (selectedMovie != null) {
            viewModel2.isFavourite(selectedMovie!!.id)
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    val filterSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true, animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing))
    var showFilterSheet by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(filterSheetState.currentValue) {
        if(filterSheetState.currentValue == ModalBottomSheetValue.Hidden && showFilterSheet)  {
            showFilterSheet = false
        }
    }

    LaunchedEffect(showFilterSheet) {
        if (showFilterSheet && !filterSheetState.isVisible) {
            filterSheetState.show()
        }

    }

    LaunchedEffect(viewModel2.isFavourite.value) {
        isFavourite.value = viewModel2.isFavourite.value
    }

    fun resetFilters() {
        viewModel.resetAllFilters()
    }

    ModalBottomSheetLayout(
        sheetState = filterSheetState,
        sheetContent = {
            FilterBottomSheet(
                sheetState = filterSheetState,
                onDismissRequest = { showFilterSheet = false },
                currentSortBy = viewModel.sortBy.value,
                onSortChanged = { sortOption ->
                    viewModel.updateSortBy(sortOption)
                },
                currentRatingRange = viewModel.ratingRange.value,
                onRatingChanged = { ratingRange ->
                    viewModel.updateRatingRange(ratingRange)
                },
                currentYear = viewModel.selectedYear.value,
                onYearChanged = { selectedYear ->
                    viewModel.updateSelectedYear(selectedYear)
                },
                currentCountry = viewModel.selectedCountry.value,
                onCountryChanged = { selectedCountry ->
                    viewModel.updateSelectedCountry(selectedCountry)
                }
            ) {
                resetFilters()
            }
        },
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetBackgroundColor = background
    ) {
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = top_bar_component,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(background)
                    .padding(20.dp)
                ) {
                    selectedMovie?.let { movie ->
                        Column {
                            Text(
                                text = movie.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = movie.overview,
                                fontSize = 14.sp,
                                color = componentLighter,
                                maxLines = 5,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(bottom = 20.dp)
                            )

                            Button(
                                onClick = {
                                    val date = SimpleDateFormat.getDateInstance().format(Date())
                                    val imageUrl = movieImagesMap[movie.id]
                                        ?.firstOrNull { it.height == 1080 && it.width == 1920 && it.language == "en" }
                                        ?.filePath
                                    if(isFavourite.value != 0 ) {
                                        viewModel2.removeFromFavourites(movie.id)
                                        Toast.makeText(context, "Removed from your Favourites", Toast.LENGTH_SHORT).show()
                                        isFavourite.value = 0
                                    } else {
                                        val entity = MediaEntity(
                                            mediaId = movie.id,
                                            imagePath = imageUrl ?: "",
                                            title = movie.title,
                                            releaseDate = movie.releaseDate ?: "N/A",
                                            rating = movie.voteAverage ?: 0.0,
                                            addedOn = date
                                        )
                                        viewModel2.addToFavourites(entity)
                                        Toast.makeText(context, "Added to your Favourites", Toast.LENGTH_SHORT).show()
                                        isFavourite.value = 1
                                    }


                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = top_bar_component)
                            ) {
                                Icon(
                                    imageVector = if (isFavourite.value != 0) Icons.Default.RemoveCircleOutline else Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isFavourite.value != 0) "Remove from Favourites" else "Add to Favourites",
                                    color = Color.White,
                                    fontFamily = netflixFamily
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    // TODO: Share logic
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.IosShare, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share")
                            }

                            Spacer(modifier = Modifier.height(60.dp))
                        }
                    } ?: Box(modifier = Modifier.height(1.dp))
                }
            },
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetBackgroundColor = background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                CatalogTopBar(
                    searchQuery = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        searchSubmitted = false
                    },
                    onSearch = {
                        searchSubmitted = true
                        viewModel.searchParam.value = searchQuery
                        viewModel.searchRemoteMedia(false)
                    },
                    onFilterClick = {
                        showFilterSheet = true
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
                if (searchQuery.isBlank()) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Genres",
                            fontFamily = netflixFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(0.8f),
                            modifier = Modifier.padding(bottom = 15.dp)
                        )
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(initialGenres.size) { index ->
                                val genre = initialGenres[index]
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(85.dp)
                                        .clickable {

                                        }
                                        .background(
                                            color = top_bar_component,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    Text(
                                        text = genre,
                                        fontFamily = netflixFamily,
                                        color = Color.White.copy(0.8f),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(start = 10.dp, top = 10.dp)
                                    )

                                    Box(
                                        modifier = Modifier.size(65.dp)
                                            .align(Alignment.BottomEnd)
                                            .offset(x = 6.dp, y = 6.dp)
                                            .background(
                                                color = Color.Blue,
                                                shape = CircleShape
                                            )
                                            .clip(CircleShape)
                                    )
                                }
                            }
                            items(expandedGenres.size) { index ->
                                val genre = expandedGenres[index]
                                AnimatedVisibility(
                                    visible = isGenresExpanded,
                                    enter = fadeIn(
                                        animationSpec = tween(durationMillis = 300, delayMillis = index* 100)
                                    ) + slideInVertically(
                                        animationSpec = tween(
                                            durationMillis = 300,
                                            delayMillis = index * 100
                                        ),
                                        initialOffsetY = { it / 2 }
                                    ) + expandVertically(
                                        animationSpec = tween(durationMillis = 300, delayMillis = index * 100)
                                    ),
                                    exit = fadeOut(
                                        animationSpec = tween(durationMillis = 200, delayMillis = (expandedGenres.size - 1 - index) * 50)
                                    ) + slideOutVertically(
                                        animationSpec = tween(durationMillis = 200, delayMillis = (expandedGenres.size - 1 - index) * 50),
                                        targetOffsetY = { it / 2 }
                                    ) + shrinkVertically(
                                        animationSpec = tween(
                                            durationMillis = 200,
                                            delayMillis = (expandedGenres.size - 1 - index) * 50
                                        )
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(85.dp)
                                            .clickable {

                                            }
                                            .background(
                                                color = top_bar_component,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clip(RoundedCornerShape(12.dp))
                                    ) {
                                        Text(
                                            text = genre,
                                            fontFamily = netflixFamily,
                                            color = Color.White.copy(0.8f),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(start = 10.dp, top = 10.dp)
                                        )
                                        Box(
                                            modifier = Modifier.size(65.dp)
                                                .align(Alignment.BottomEnd)
                                                .offset(x = 6.dp, y = 6.dp)
                                                .background(
                                                    color = Color.Blue,
                                                    shape = CircleShape
                                                )
                                                .clip(CircleShape)
                                        )
                                    }
                                }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .clickable {
                                    isGenresExpanded = !isGenresExpanded
                                }
                                .align(Alignment.CenterHorizontally),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (!isGenresExpanded) "See more" else "Show less",
                                fontSize = 15.sp,
                                color = Color.White.copy(0.8f),
                                fontFamily = netflixFamily,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            AnimatedContent(
                                targetState = isGenresExpanded,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(300)) with
                                            fadeOut(animationSpec = tween(300))
                                }
                            ) { expanded ->
                                Icon(
                                    imageVector = if (!expanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                                    contentDescription = if (!expanded) "Expand" else "Collapse",
                                    tint = Color.White.copy(0.8f) ,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                } else if (searchSubmitted) {
                    //var isLoading by remember { mutableStateOf(true)}
                    val searchResultsList by viewModel.searchResults.collectAsState()
                    val isSearchLoading by viewModel.searchLoading.collectAsState()
                    if (isSearchLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircleLoader(
                                modifier = Modifier.size(100.dp),
                                isVisible = isSearchLoading,
                                color = componentLighter,
                                secondColor = null,
                                tailLength = 250f
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(15.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(searchResultsList) { search ->
                                val movie = search.toMovie()
                                if (movie != null) {
                                    if (movie.id !in movieImagesMap) {
                                        LaunchedEffect(movie.id) {
                                            viewModel.fetchMovieImages(movie.id)
                                        }
                                    }
                                    val images = movieImagesMap[movie.id]
                                    val imageUrl = movieImagesMap[movie.id]
                                        ?.firstOrNull { it.height == 1080 && it.width == 1920 && it.language == "en" }?.filePath

                                    when {
                                        images == null -> {
                                            LaunchedEffect(movie.id) {
                                                viewModel.fetchMovieImages(movie.id)
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(100.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(
                                                        top_bar_component,
                                                        shape = RoundedCornerShape(10.dp)
                                                    ),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .width(175.dp)
                                                        .clip(RoundedCornerShape(5.dp)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    AnimatedShimmerItem()
                                                }
                                            }
                                        }

                                        imageUrl != null -> {
                                            MovieItemSearchScreen(
                                                movie = movie,
                                                navController = navController,
                                                genre = movie.genreIds?.firstOrNull()
                                                    ?.let { genreMap[it] }
                                                    ?: "N/A",
                                                imageUrl = imageUrl,
                                                onMoreClick = { movie ->
                                                    selectedMovie = movie
                                                }
                                            )
                                        }

                                        else -> {
                                            MovieItemSearchScreen(
                                                movie = movie,
                                                navController = navController,
                                                genre = movie.genreIds?.firstOrNull()
                                                    ?.let { genreMap[it] }
                                                    ?: "N/A",
                                                imageUrl = movie.backdropPath,
                                                onMoreClick = { movie ->
                                                    selectedMovie = movie
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Type your query and press Enter or tap the search icon.",
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }


    LaunchedEffect(sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.Hidden) {
            selectedMovie = null
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogTopBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch:() -> Unit,
    onFilterClick:() -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .padding(top = 10.dp, bottom = 8.dp)
    ) {
        Text(
            text = "Catalog",
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(background),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp)),
                placeholder = {
                    Text(text = "Search", fontSize = 13.sp, fontFamily = netflixFamily, color = componentLighter)
                },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 13.sp,
                    fontFamily = netflixFamily,
                    color = Color.White.copy(alpha = 0.8f)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearch()
                        keyboardController?.hide()
                    }
                ),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White.copy(alpha = 0.8f),
                    cursorColor = Color.White.copy(alpha = 0.8f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    containerColor = top_bar_component,
                    focusedLabelColor = Color.Transparent,
                    focusedSupportingTextColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        onQueryChange("")
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(25.dp))
                    }
                },
                leadingIcon = {
                    IconButton(onClick ={
                        onSearch()
                        keyboardController?.hide()
                    }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = componentLighter, modifier = Modifier.size(25.dp) )
                    }
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier
                .fillMaxHeight()
                .width(45.dp)
                .clickable { onFilterClick() }
                .background(top_bar_component, shape = RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(id = R.drawable.filter_icon), tint = Color.White.copy(alpha = 0.8f), contentDescription = null, modifier = Modifier.size(25.dp))
            }

        }
    }


}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun FilterBottomSheet(
    sheetState: ModalBottomSheetState,
    onDismissRequest: suspend () -> Unit,
    currentSortBy: String = "Popular",
    onSortChanged: (String) -> Unit,
    currentRatingRange: ClosedFloatingPointRange<Float> = 0f..9f,
    onRatingChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    currentYear: Int? = null,
    onYearChanged: (Int?) -> Unit,
    currentCountry: String? = null,
    onCountryChanged:(String?) -> Unit,
    onResetClickableNode: () -> Unit

) {
    var selectedTab by rememberSaveable { mutableStateOf(0)}
    val sortOptions = listOf("Newest", "Popular", "Rating")
    var selectedSort by rememberSaveable { mutableStateOf(currentSortBy) }
    var ratingRange by remember { mutableStateOf(currentRatingRange) }
    var selectedYear by remember {mutableStateOf(currentYear)}
    var selectedCountry by remember { mutableStateOf(currentCountry)}
    val coroutineScope = rememberCoroutineScope()

    var showCountryPicker by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }

    Log.d("SearchScreen",selectedYear.toString())


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .background(background)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(33.dp)
                    .clip(CircleShape)
                    .clickable {
                        coroutineScope.launch {
                            sheetState.hide()
                            onDismissRequest()
                        }
                    }
                    .background(top_bar_component, shape = CircleShape)
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.ArrowBackIos, contentDescription = null, tint = Color.White.copy(0.8f), modifier = Modifier
                    .size(25.dp)
                    .padding(start = 7.dp))
            }
            Text(text = "Filters", fontSize = 22.sp, fontFamily = netflixFamily, color = Color.White.copy(0.8f), fontWeight = FontWeight.Medium, modifier = Modifier.align(
                Alignment.Center))
            
        }
        Spacer(modifier = Modifier.height(15.dp))

        val tabs = listOf("All", "Movies", "Series")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            tabs.forEachIndexed { index, title ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .height(36.dp)
                        .wrapContentWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selectedTab == index) component else top_bar_component,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedTab = index }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontFamily = netflixFamily,
                        fontSize = 14.sp,
                        color = if (selectedTab == index) Color.White.copy(alpha = 0.8f) else componentLighter
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Filters",
            fontFamily = netflixFamily,
            fontSize = 12.sp,
            color = componentLighter,
            modifier = Modifier.padding(start = 2.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))

        FilterRow("Genre", "Drama, Thriller, +1") {}
        Spacer(modifier = Modifier.height(8.dp))
        FilterRow("Country", getDisplayNameForCountryCode(selectedCountry)) { showCountryPicker = true}
        Spacer(modifier = Modifier.height(8.dp))
        FilterRow("Year", value = selectedYear?.toString() ?: "All") { showYearPicker = true}

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(top_bar_component, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rating",
                    modifier = Modifier.padding(top = 14.dp),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 15.sp,
                    fontFamily = netflixFamily,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "From ${ratingRange.start.toInt()} to ${ratingRange.endInclusive.toInt()}",
                    modifier = Modifier.padding(top = 12.dp),
                    color = componentLighter,
                    fontFamily = netflixFamily,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            RangeSlider(
                value = ratingRange,
                onValueChange = {
                    ratingRange = it
                    onRatingChanged(it)
                                },
                valueRange = 0f..9f,
                steps = 9,
                colors = androidx.compose.material3.SliderDefaults.colors(
                    thumbColor = whiteCopy,
                    activeTrackColor = Color.White.copy(alpha = 0.8f),
                    activeTickColor = whiteCopy,
                    inactiveTickColor = componentLighter
                )
            )
        }
        Spacer(modifier = Modifier.height(24.dp))


        Text(
            text = "Sort By",
            color = componentLighter,
            fontSize = 12.sp,
            fontFamily = netflixFamily,
            modifier = Modifier.padding(start = 2.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .background(top_bar_component, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            verticalArrangement = Arrangement.Center
        ) {
            sortOptions.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedSort = option
                            onSortChanged(option)
                        }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = option, color = if(selectedSort == option) Color.White.copy(alpha = 0.8f) else componentLighter, fontSize = 15.sp, fontFamily = netflixFamily, fontWeight = if(selectedSort == option) FontWeight.Medium else FontWeight.Normal)
                    RadioButton(
                        selected = selectedSort == option,
                        onClick = {
                            selectedSort = option
                            onSortChanged(option)
                                  },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.White.copy(alpha = 0.8f),
                            unselectedColor = componentLighter
                        )
                    )

                }
            }
        }
        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    onResetClickableNode()
                    selectedSort = "Popular"
                    ratingRange = 0f..9f
                    selectedTab = 0
                    selectedCountry = null
                    selectedYear = null

                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = whiteCopy,
                    containerColor = top_bar_component
                )
            ) {
                Text(text = "Reset", fontSize = 15.sp, fontFamily = netflixFamily, color = componentLighter)
            }
            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        sheetState.hide()
                        onDismissRequest()
                    }
                },
                modifier = Modifier
                    .weight(2f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = background
                )
            ) {
                Text(text = "Apply", fontSize = 15.sp, fontFamily = netflixFamily, color = background)
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
    if(showCountryPicker) {
        CountryPickerDialog(
            currentSelectedCountry = selectedCountry,
            onCountrySelected = {
                selectedCountry = it
                onCountryChanged(selectedCountry)
                showCountryPicker = false
            },
            onDismiss = {showCountryPicker = false}
        )
    }

    if(showYearPicker) {
        YearPicker(
            currentSelectedYear = selectedYear,
            onYearSelected = {
                selectedYear = it
                onYearChanged(selectedYear)
                showYearPicker = false
            },
            onDismiss = {showYearPicker = false}
        )
    }
}

@Composable
fun FilterRow(title: String, value: String, onClick: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(top_bar_component, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp, horizontal = 10.dp)
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = Color.White.copy(alpha = 0.8f), fontFamily = netflixFamily, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Text(value, color = componentLighter, fontSize = 13.sp, fontFamily = netflixFamily)

    }
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MovieItemSearchScreen(
    movie: Movie,
    navController: NavController,
    genre: String,
    imageUrl: String?,
    onMoreClick: (Movie) -> Unit
) {
    val title = movie.title
    val releaseDate = movie.releaseDate?.take(4) ?: "N/A"
    val imageUrlFinal = BASE_BACKDROP_IMAGE_URL + (imageUrl ?: "")
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrlFinal)
            .size(Size.ORIGINAL)
            .build()
    )
    val imageState = imagePainter.state





    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { navController.navigate(Screen.Details.route + "/${movie.id}") }
            .clip(RoundedCornerShape(10.dp))
            .background(top_bar_component, shape = RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(175.dp)
                .clip(RoundedCornerShape(5.dp)),
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
                        .clip(RoundedCornerShape(5.dp))
                )
            }

            if (imageState is AsyncImagePainter.State.Error) {

                    Icon(
                        imageVector = Icons.Default.ImageNotSupported,
                        contentDescription = "error",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(5.dp))
                            .padding(10.dp),
                        tint = component
                    )

        }
            if (imageState is AsyncImagePainter.State.Loading) {
                AnimatedShimmerItem()
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                modifier = Modifier
                    .width(150.dp)
                    .padding(bottom = 2.dp),
                text = title,
                lineHeight = 14.sp,
                fontFamily = netflixFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = releaseDate,
                fontFamily = netflixFamily,
                fontSize = 11.sp,
                color = componentLighter
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        IconButton(onClick = {onMoreClick(movie) }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = componentLighter
            )

        }
        
    }

}

fun getDisplayNameForCountryCode(code: String?): String {
    return tmdbLanguageToCountryMap[code] ?: code ?: "All"
}


/*@Composable
fun SearchBottomSheet(
    movie: Movie,
    onAddToFavourites: (MediaEntity) -> Unit,
    onShare: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet
}*/



