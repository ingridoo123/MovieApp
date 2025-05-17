package com.example.movieapp.presentation.screens.search

import android.inputmethodservice.Keyboard
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.movieapp.data.local.media.MediaEntity
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.data.remote.MediaAPI.Companion.BASE_BACKDROP_IMAGE_URL
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.Search
import com.example.movieapp.domain.model.toMovie
import com.example.movieapp.navigation.Screen
import com.example.movieapp.presentation.components.AnimatedShimmerItem
import com.example.movieapp.presentation.components.MovieItemAllGenreScreen
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.util.Constants.netflixFamily
import com.example.movieapp.util.MovieState
import org.jetbrains.annotations.Async

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel = hiltViewModel()) {

    val searchResults = viewModel.multiSearchState.value.collectAsLazyPagingItems()
    val popularState by viewModel.popularMovieResponse.collectAsState()
    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }
    val movieImagesMap = viewModel.movieImagesMap

    var searchSubmitted by rememberSaveable { mutableStateOf(false) }




    val genreMap = mapOf(
        28 to "Action", 12 to "Adventure", 16 to "Animation", 35 to "Comedy", 80 to "Crime",
        99 to "Documentary", 18 to "Drama", 10751 to "Family", 14 to "Fantasy", 36 to "History",
        27 to "Horror", 10402 to "Music", 9648 to "Mystery", 10749 to "Romance", 878 to "Sci-Fi",
        10770 to "TV Movie", 53 to "Thriller", 10752 to "War", 37 to "Western"
    )

    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)

    var selectedMovie by remember { mutableStateOf<Movie?>(null) }
    var selectedGenre by remember { mutableStateOf<String>("N/A") }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedMovie) {
        if (selectedMovie != null) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = top_bar_component, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
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

                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = top_bar_component)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to Favourites", color = Color.White)
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
                    /* TODO */
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (searchQuery.isBlank()) {
                when (val state = popularState) {
                    is MovieState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is MovieState.Success -> {
                        val movies = state.data?.results ?: emptyList()

                        movies.forEach { movie ->

                        }
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(15.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(movies) { movie ->
                                if (movie.id !in movieImagesMap) {
                                    LaunchedEffect(movie.id) {
                                        viewModel.fetchMovieImages(movie.id)

                                    }
                                }
                                val images = movieImagesMap[movie.id]
                                val imageUrl = movieImagesMap[movie.id]
                                    ?.firstOrNull { it.height == 1080 && it.width == 1920 && it.language == "en" }
                                    ?.filePath

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
                                            onMoreClick = {movie ->
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
                                            onMoreClick = {movie ->
                                                selectedMovie = movie

                                            }
                                        )
                                    }
                                }

                            }
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(100.dp))
                    }

                    is MovieState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Error: ${state.message}", color = Color.Red)
                        }
                    }
                }
            } else if (searchSubmitted) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Log.d(
                        "SearchScreen",
                        "${searchResults.itemSnapshotList.filter { it?.title?.contains("Transcendence") == true }}"
                    )
                    items(searchResults.itemCount) { index ->
                        val item = searchResults[index]
                        if (item is Search && item.mediaType == "movie") {
                            val movie = item.toMovie()
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
                                            onMoreClick = {movie ->
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
                                            onMoreClick = {movie ->
                                                selectedMovie = movie

                                            }
                                        )
                                    }
                                }
                            }

                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
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
                    Text(text = "Search", fontSize = 14.sp, fontFamily = netflixFamily, color = componentLighter)
                },
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

                ),
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
                Icon(imageVector = Icons.Default.FilterList, contentDescription = null, tint = Color.White.copy(0.8f), modifier = Modifier.size(25.dp))
            }

        }
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



