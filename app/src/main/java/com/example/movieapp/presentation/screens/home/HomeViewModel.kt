package com.example.movieapp.presentation.screens.home

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.movieapp.data.remote.respond.GenreResponse
import com.example.movieapp.data.remote.respond.MovieDetailsDTO
import com.example.movieapp.data.remote.respond.MovieResponse
import com.example.movieapp.data.remote.respond.SeriesResponse
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

    private val _recommendedMovies: MutableStateFlow<MovieState<MovieResponse?>> = MutableStateFlow(MovieState.Loading)
    val recommendedMovies: StateFlow<MovieState<MovieResponse?>> = _recommendedMovies

    private val _recommendedSeries: MutableStateFlow<MovieState<SeriesResponse?>> = MutableStateFlow(MovieState.Loading)
    val recommendedSeries: StateFlow<MovieState<SeriesResponse?>> = _recommendedSeries

    private val _topRatedSeries: MutableStateFlow<MovieState<SeriesResponse?>> = MutableStateFlow(MovieState.Loading)
    val topRatedSeries: StateFlow<MovieState<SeriesResponse?>> = _topRatedSeries

    private val _detailsMovieResponse: MutableStateFlow<MovieState<MovieDetailsDTO?>> = MutableStateFlow(MovieState.Loading)
    val detailsMovieResponse: StateFlow<MovieState<MovieDetailsDTO?>> = _detailsMovieResponse

    private val _movieDetailsMap = MutableStateFlow<Map<Int, MovieDetailsDTO>>(emptyMap())
    val movieDetailsMap: StateFlow<Map<Int, MovieDetailsDTO>> = _movieDetailsMap

    private val _allDetailsLoaded = MutableStateFlow(false)
    val allDetailsLoaded: StateFlow<Boolean> = _allDetailsLoaded

    var genresWiseMovieListState: Flow<PagingData<Movie>>? = null
    var seriesWiseListState: Flow<PagingData<Movie>>? = null


    private val _refreshTrigger = MutableStateFlow(0)
    val refreshTrigger: StateFlow<Int> = _refreshTrigger

    private val _cachedFilteredMovies = MutableStateFlow<List<Movie>>(emptyList())
    val cachedFilteredMovies: StateFlow<List<Movie>> = _cachedFilteredMovies

    init {
        fetchDiscoverMovies()
        fetchRecommendedMovies()
        fetchRecommendedSeries()
        fetchTopRatedMovies()
        fetchTopRatedSeries()
    }


    val popularAllListState = repository.getAllMoviesPagination("popularAllListScreen").flow.cachedIn(viewModelScope)
    val discoverListState = repository.getAllMoviesPagination("discoverListScreen").flow.cachedIn(viewModelScope)
    val nowPlayingAllListState = repository.getAllMoviesPagination("nowPlayingAllListScreen").flow.cachedIn(viewModelScope)
    val upcomingListState = repository.getAllMoviesPagination("upcomingListScreen").flow.cachedIn(viewModelScope)

    fun refreshAllData() {
        viewModelScope.launch {
            try {
                // Increment refresh trigger to force paging data refresh
                _refreshTrigger.value++


                fetchPopularMovies()
                fetchDiscoverMovies()

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

        val seriesGenreId: Int? = when (genreSelected) {
            28 -> 10759  // Action -> Action & Adventure
            12 -> 10759  // Adventure -> Action & Adventure
            53 -> 80     // Thriller -> Crime
            878 -> 10765 // Sci-Fi -> Sci-Fi & Fantasy
            27 -> null   // Horror -> Brak seriali
            else -> genreSelected
        }

        if (seriesGenreId != null) {

            seriesWiseListState = repository.getSeriesByGenre(seriesGenreId).flow.cachedIn(viewModelScope)
        } else {
            seriesWiseListState = null
        }
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

    fun fetchRecommendedMovies() {
        viewModelScope.launch {
            try {
                val response = repository.getRecommendedMovies().first()
                _recommendedMovies.emit(MovieState.Success(response))
            } catch (e: Exception) {
                val errorMessage = "Error recommended movies"
                _recommendedMovies.emit(MovieState.Error(errorMessage))
            }
        }
    }

    fun fetchRecommendedSeries() {
        viewModelScope.launch {
            try {
                val response = repository.getRecommendedSeries().first()
                _recommendedSeries.emit(MovieState.Success(response))
            } catch (e: Exception) {
                _recommendedSeries.emit(MovieState.Error("Error recommended series"))
            }
        }
    }

    fun fetchTopRatedMovies() {
        viewModelScope.launch {
            try {
                val response = repository.getTopRaredMovies().first()
                Log.d("SplashScreen", "Top rated movies fetched: ${response?.results?.size}")
                _response7.emit(MovieState.Success(response))
            } catch (e: Exception) {
                val errorMessage = "Error top rated"
                _response7.emit(MovieState.Error(errorMessage))
            }

        }
    }

    fun fetchTopRatedSeries() {
        viewModelScope.launch {
            try {
                val response = repository.getTopRatedSeries().first()
                _topRatedSeries.emit(MovieState.Success(response))
            } catch (e: Exception) {
                _topRatedSeries.emit(MovieState.Error("Error TR series"))
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
                Log.d("SplashScreen", "Genres fetched: ${response?.genres?.size}")
                _response6.emit(MovieState.Success(response))
            } catch (e: Exception) {
                val errorMessage = "Error genre response"
                _response6.emit(MovieState.Error(errorMessage))
            }
        }
    }
}