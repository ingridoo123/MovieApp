package com.example.movieapp.presentation.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.remote.respond.CastResponse
import com.example.movieapp.data.remote.respond.MovieDetailsDTO
import com.example.movieapp.data.remote.respond.MovieResponse
import com.example.movieapp.data.repository.MovieDetailsRepositoryImpl
import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.Trailer
import com.example.movieapp.util.MovieState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(private val repository: MovieDetailsRepositoryImpl): ViewModel() {

    private val _response1: MutableStateFlow<MovieState<MovieDetailsDTO?>> = MutableStateFlow(MovieState.Loading)
    val detailsMovieResponse: StateFlow<MovieState<MovieDetailsDTO?>> = _response1

    private val _response2: MutableStateFlow<MovieState<MovieResponse?>> = MutableStateFlow(MovieState.Loading)
    val similarMoviesResponse: StateFlow<MovieState<MovieResponse?>> = _response2

    private val _response3: MutableStateFlow<MovieState<List<Cast>?>> = MutableStateFlow(MovieState.Loading)
    val movieCastResponse: StateFlow<MovieState<List<Cast>?>> = _response3

    private val _movieTrailerResponse: MutableStateFlow<MovieState<List<Trailer>?>> = MutableStateFlow(MovieState.Loading)
    val movieTrailerResponse: StateFlow<MovieState<List<Trailer>?>> = _movieTrailerResponse

    fun fetchMovieDetails(movieId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMoviesDetails(movieId).first()
                _response1.emit(MovieState.Success(response))
            } catch (e: Exception) {
                val errorMessage = "movie details error, try again :*"
                _response1.emit(MovieState.Error(errorMessage))
            }
        }
    }

    fun fetchSimilarMovies(movieId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getSimilarMovies(movieId).first()
                _response2.emit(MovieState.Success(response))
            } catch (e: Exception) {
                val errorMessage = "similar movies error, try again :*"
                _response2.emit(MovieState.Error(errorMessage))
            }
        }
    }

    fun fetchCastOfMovie(movieId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMovieCast(movieId).first()
                val castList = response.castList
                _response3.emit(MovieState.Success(castList))
            } catch (e: Exception) {
                val errorMessage = "cast error, try again :*"
                _response2.emit(MovieState.Error(errorMessage))
            }
        }
    }

    fun fetchMovieTrailer(movieId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMovieTrailers(movieId).first()
                _movieTrailerResponse.emit(MovieState.Success(response.results))
            } catch (e: Exception) {
                val errorMessage = "trailers error, try again :*"
                _movieTrailerResponse.emit(MovieState.Error(errorMessage))
            }
        }
    }



}