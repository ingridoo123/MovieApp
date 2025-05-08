package com.example.movieapp.presentation.screens.home

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.movieapp.data.remote.respond.GenreResponse
import com.example.movieapp.data.remote.respond.MovieDetailsDTO
import com.example.movieapp.data.remote.respond.MovieResponse
import com.example.movieapp.data.repository.HomeRepositoryImpl
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.util.MovieState
import com.example.movieapp.util.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepositoryImpl): ViewModel() {

    private val _response1: MutableStateFlow<MovieState<MovieResponse?>> = MutableStateFlow(MovieState.Loading)
    val popularMovieResponse: StateFlow<MovieState<MovieResponse?>> = _response1

    private val _response2: MutableStateFlow<MovieState<MovieResponse?>> = MutableStateFlow(MovieState.Loading)
    val discoverMovieResponse: StateFlow<MovieState<MovieResponse?>> = _response2

    private val _response3: MutableStateFlow<MovieState<MovieResponse?>> = MutableStateFlow(MovieState.Loading)
    val trendingMovieResponse: StateFlow<MovieState<MovieResponse?>> = _response3

    private val _response4: MutableStateFlow<MovieState<MovieResponse?>> = MutableStateFlow(MovieState.Loading)
    val nowPlayingMovieResponse: StateFlow<MovieState<MovieResponse?>> = _response4

    private val _response5: MutableStateFlow<MovieState<MovieResponse?>> = MutableStateFlow(MovieState.Loading)
    val upcomingMovieResponse: StateFlow<MovieState<MovieResponse?>> = _response5

    private val _response6: MutableStateFlow<MovieState<GenreResponse?>> = MutableStateFlow(MovieState.Loading)
    val genresMovieResponse: StateFlow<MovieState<GenreResponse?>> = _response6

    private val _response7: MutableStateFlow<MovieState<MovieResponse?>> = MutableStateFlow(MovieState.Loading)
    val topRatedMovieResponse: StateFlow<MovieState<MovieResponse?>> = _response7

    private val _detailsMovieResponse: MutableStateFlow<MovieState<MovieDetailsDTO?>> = MutableStateFlow(MovieState.Loading)
    val detailsMovieResponse: StateFlow<MovieState<MovieDetailsDTO?>> = _detailsMovieResponse

    private val _movieDetailsMap = MutableStateFlow<Map<Int, MovieDetailsDTO>>(emptyMap())
    val movieDetailsMap: StateFlow<Map<Int, MovieDetailsDTO>> = _movieDetailsMap

    private val _allDetailsLoaded = MutableStateFlow(false)
    val allDetailsLoaded: StateFlow<Boolean> = _allDetailsLoaded

    var genresWiseMovieListState: Flow<PagingData<Movie>>? = null

    private val networkUtils = NetworkUtils()
    val networkType = networkUtils.networkType

    private val _refreshTrigger = MutableStateFlow(0)
    val refreshTrigger: StateFlow<Int> = _refreshTrigger

    private val _cachedFilteredMovies = MutableStateFlow<List<Movie>>(emptyList())
    val cachedFilteredMovies: StateFlow<List<Movie>> = _cachedFilteredMovies

    init {
        fetchPopularMovies()
        fetchDiscoverMovies()
        fetchTrendingMovies()
        fetchNowPlayingMovies()
        fetchUpcomingMovies()
        fetchGenreResponse()
        fetchTopRatedMovies()
    }

//    @RequiresApi(Build.VERSION_CODES.S)
//    fun registerNetwork(context: Context) {
//        networkUtils.registerNetworkCallback(context)
//    }

    val popularAllListState = repository.getAllMoviesPagination("popularAllListScreen").flow.cachedIn(viewModelScope)
    val discoverListState = repository.getAllMoviesPagination("discoverListScreen").flow.cachedIn(viewModelScope)
    val nowPlayingAllListState = repository.getAllMoviesPagination("nowPlayingAllListScreen").flow.cachedIn(viewModelScope)
    val upcomingListState = repository.getAllMoviesPagination("upcomingListScreen").flow.cachedIn(viewModelScope)

    fun refreshAllData() {
        viewModelScope.launch {
            try {
                // Increment refresh trigger to force paging data refresh
                _refreshTrigger.value++

                // Refresh all non-paging data
                fetchPopularMovies()
                fetchDiscoverMovies()
                fetchTrendingMovies()
                fetchTopRatedMovies()
                fetchNowPlayingMovies()
                fetchUpcomingMovies()
                fetchGenreResponse()
            } catch (e: Exception) {
                // Handle any errors if needed
            }
        }
    }
    fun cacheFilteredMovies(movies: List<Movie>) {
        _cachedFilteredMovies.value = movies
    }

    fun setGenreData(genreSelected: Int) {
        genresWiseMovieListState = repository.getGenresWiseMovie(genreSelected).flow.cachedIn(viewModelScope)
    }

    fun resetDetailsLoaded() {
        _allDetailsLoaded.value = false
    }

    fun fetchAllMovieDetails(movieIds: List<String>) {
        viewModelScope.launch {
            try {
                _allDetailsLoaded.value = false

                val missingIds = movieIds.filter { id ->
                    !_movieDetailsMap.value.containsKey(id.toInt())
                }

                if(missingIds.isEmpty()) {
                    _allDetailsLoaded.value = true
                    return@launch
                }

                val deferredDetails = missingIds.map { movieId ->
                    async {
                        try {
                            val movieDetail = repository.getMovieDetails(movieId).first()
                            movieDetail?.let { Pair(movieId.toInt(),it) }
                        } catch (e:Exception) {
                            null
                        }
                    }
                }

                val newDetails = deferredDetails.awaitAll().filterNotNull().toMap()

                _movieDetailsMap.value = _movieDetailsMap.value + newDetails

                _allDetailsLoaded.value = true
            } catch (e: Exception) {
                _allDetailsLoaded.value = true
            }
        }
    }


    fun fetchMovieDetails(movieId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMovieDetails(movieId).first()
                _detailsMovieResponse.emit(MovieState.Success(response))
                // Update the map with the new details
                response?.let { details ->
                    _movieDetailsMap.value = _movieDetailsMap.value + (movieId.toInt() to details)
                }
            } catch (e:Exception) {
                val errorMessage = "An error occurred."
                _detailsMovieResponse.emit(MovieState.Error(errorMessage))
            }
        }
    }

    fun fetchPopularMovies() {
        viewModelScope.launch {
            try {
                val response = repository.getPopularMovies().first()
                _response1.emit(MovieState.Success(response))
            } catch (e: Exception) {
                val errorMessage = "Error popular"
                _response1.emit(MovieState.Error(errorMessage))
            }
        }
    }

    fun fetchTopRatedMovies() {
        viewModelScope.launch {
            try {
                val response = repository.getTopRaredMovies().first()
                _response7.emit(MovieState.Success(response))
            } catch (e: Exception) {
                val errorMessage = "Error top rated"
                _response7.emit(MovieState.Error(errorMessage))
            }

        }
    }

    fun fetchDiscoverMovies() {
        viewModelScope.launch {
            try {
                val response = repository.getDiscoverMovies().first()
                _response2.emit(MovieState.Success(response))
            } catch (e: Exception) {
                val errorMessage = "Error discover"
                _response2.emit(MovieState.Error(errorMessage))
            }
        }
    }

    fun fetchTrendingMovies() {
        viewModelScope.launch {
            try {
                val response = repository.getTrendingMovies().first()
                _response3.emit(MovieState.Success(response))
            } catch (e: Exception) {
                val errorMessage = "Error trending"
                _response3.emit(MovieState.Error(errorMessage))
            }
        }
    }

    fun fetchNowPlayingMovies() {
        viewModelScope.launch {
            try {
                val response = repository.getNowPlayingMovies().first()
                _response4.emit(MovieState.Success(response))
            } catch (e: Exception) {
                val errorMessage = "Error now playing"
                _response4.emit(MovieState.Error(errorMessage))
            }
        }
    }

    fun fetchUpcomingMovies() {
        viewModelScope.launch {
            try {
                val response = repository.getUpcomingMovies().first()
                _response5.emit(MovieState.Success(response))
            } catch (e: Exception) {
                val errorMessage = "Error upcoming"
                _response5.emit(MovieState.Error(errorMessage))
            }
        }
    }

    fun fetchGenreResponse() {
        viewModelScope.launch {
            try {
                val response = repository.getMovieGenres().first()
                _response6.emit(MovieState.Success(response))
            } catch (e: Exception) {
                val errorMessage = "Error genre response"
                _response6.emit(MovieState.Error(errorMessage))
            }
        }
    }
}