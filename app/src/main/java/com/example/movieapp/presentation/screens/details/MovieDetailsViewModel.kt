package com.example.movieapp.presentation.screens.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.remote.respond.BackdropImage
import com.example.movieapp.data.remote.respond.CastResponse
import com.example.movieapp.data.remote.respond.MovieDetailsDTO
import com.example.movieapp.data.remote.respond.MovieResponse
import com.example.movieapp.data.remote.respond.PersonMovieCreditsResponse
import com.example.movieapp.data.repository.MovieDetailsRepositoryImpl
import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.Crew
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.Person
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

    private val _crewCastResponse: MutableStateFlow<MovieState<Pair<List<Cast>, List<Crew>>>?> = MutableStateFlow(null)
    val crewCastResponse: StateFlow<MovieState<Pair<List<Cast>, List<Crew>>>?> = _crewCastResponse

    private val _movieTrailerResponse: MutableStateFlow<MovieState<List<Trailer>?>> = MutableStateFlow(MovieState.Loading)
    val movieTrailerResponse: StateFlow<MovieState<List<Trailer>?>> = _movieTrailerResponse

    private val _movieImagesResponse: MutableStateFlow<MovieState<List<BackdropImage>?>> = MutableStateFlow(MovieState.Loading)
    val movieImagesResponse: StateFlow<MovieState<List<BackdropImage>?>> = _movieImagesResponse

    private val _personDetailsResponse: MutableStateFlow<MovieState<Person?>> = MutableStateFlow(MovieState.Loading)
    val personDetailsResponse: StateFlow<MovieState<Person?>> = _personDetailsResponse

    private val _personMovieCreditsResponse: MutableStateFlow<MovieState<PersonMovieCreditsResponse?>> = MutableStateFlow(MovieState.Loading)
    val personMovieCreditsResponse: StateFlow<MovieState<PersonMovieCreditsResponse?>> = _personMovieCreditsResponse



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

    fun fetchCastAndCrewOfMovie(movieId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMovieCast(movieId).first()
                _response3.emit(MovieState.Success(response.castList))
            } catch (e: Exception) {
                val errorMessage = "cast error, try again :*"
                _response3.emit(MovieState.Error(errorMessage))
            }
        }
    }

    fun fetchCastOfMovie(movieId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMovieCast(movieId).first()
                _crewCastResponse.emit(MovieState.Success(Pair(response.castList, response.crewList)))
            } catch (e: Exception) {
                _crewCastResponse.emit(MovieState.Error("cast error, try again :*"))
            }
        }
    }

//    fun getCurrentCastAndCrew(): Pair<List<Cast>, List<Crew>>? {
//        val data = crewCastResponse.value
//        return if (data is MovieState.Success) data.data else null
//    }

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

    fun fetchMovieImages(movieId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMovieImages(movieId).first()
                _movieImagesResponse.emit(MovieState.Success(response.backdrops))
            } catch (e: Exception) {
                val errorMessage = "images error, try again :*"
                _movieImagesResponse.emit(MovieState.Error(errorMessage))
            }
        }
    }

    fun fetchPersonDetails(personId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getPersonDetails(personId).first()
                _personDetailsResponse.emit(MovieState.Success(response))
                fetchPersonMovieCredits(personId,response.department)
            } catch (e: Exception) {
                _personDetailsResponse.emit(MovieState.Error("Error fetching person details: $e"))
            }
        }
    }

    fun fetchPersonMovieCredits(personId: String, department: String?) {
        viewModelScope.launch {
            _personMovieCreditsResponse.emit(MovieState.Loading)
            try {
                val response = repository.getPersonMovieCredits(personId).first()
                _personMovieCreditsResponse.emit(MovieState.Success(response))
                ///processAndLogMovieCredits(response, department, personId)

            } catch (e: Exception) {
                _personMovieCreditsResponse.emit(MovieState.Error("Error fetching person movie credits: ${e.localizedMessage}"))
            }
        }
    }





}