package com.example.movieapp.presentation.screens.series_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.remote.respond.SeasonDetailsDto
import com.example.movieapp.data.remote.respond.SeriesDetailsDTO
import com.example.movieapp.data.remote.respond.SeriesResponse
import com.example.movieapp.data.repository.HomeRepositoryImpl
import com.example.movieapp.data.repository.MovieDetailsRepositoryImpl
import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.Crew
import com.example.movieapp.domain.model.Trailer
import com.example.movieapp.util.MovieState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesDetailsViewModel @Inject constructor(private val repository: MovieDetailsRepositoryImpl): ViewModel() {
    private val _detailsSeriesResponse: MutableStateFlow<MovieState<SeriesDetailsDTO?>> = MutableStateFlow(MovieState.Loading)
    val detailsSeriesResponse: StateFlow<MovieState<SeriesDetailsDTO?>> = _detailsSeriesResponse

    private val _seriesTrailerResponse: MutableStateFlow<MovieState<List<Trailer>?>> = MutableStateFlow(MovieState.Loading)
    val seriesTrailerResponse: StateFlow<MovieState<List<Trailer>?>> = _seriesTrailerResponse

    private val _seasonDetailsResponse: MutableStateFlow<MovieState<SeasonDetailsDto?>> = MutableStateFlow(MovieState.Loading)
    val seasonDetailsResponse: StateFlow<MovieState<SeasonDetailsDto?>> = _seasonDetailsResponse

    private val _seriesCastResponse: MutableStateFlow<MovieState<List<Cast>?>> = MutableStateFlow(MovieState.Loading)
    val seriesCastResponse: StateFlow<MovieState<List<Cast>?>> = _seriesCastResponse

    private val _similarSeriesResponse: MutableStateFlow<MovieState<SeriesResponse?>> = MutableStateFlow(MovieState.Loading)
    val similarSeriesResponse: StateFlow<MovieState<SeriesResponse?>> = _similarSeriesResponse

    private val _seriesCrewCastResponse: MutableStateFlow<MovieState<Pair<List<Cast>, List<Crew>>>?> = MutableStateFlow(null)
    val seriesCrewCastResponse: StateFlow<MovieState<Pair<List<Cast>, List<Crew>>>?> = _seriesCrewCastResponse



    fun fetchSeriesDetails(seriesId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getSeriesDetails(seriesId).first()
                _detailsSeriesResponse.emit(MovieState.Success(response))
            } catch (e: Exception) {
                _detailsSeriesResponse.emit(MovieState.Error("Error series details"))
            }
        }
    }

    fun fetchSeriesTrailer(seriesId: String) {
        viewModelScope.launch{
            try {
                val response = repository.getSeriesTrailers(seriesId).first()
                _seriesTrailerResponse.emit(MovieState.Success(response.results))
            } catch (e: Exception) {
                _seriesTrailerResponse.emit(MovieState.Error("Error series trailer"))
            }
        }
    }

    fun fetchSeasonDetails(seriesId: String, seasonNumber: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getSeasonDetails(seriesId, seasonNumber).first()
                _seasonDetailsResponse.emit(MovieState.Success(response))
            } catch (e: Exception) {
                _seasonDetailsResponse.emit(MovieState.Error("Error season details"))
            }
        }
    }

    fun fetchSeriesCast(seriesId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getSeriesCast(seriesId).first()
                _seriesCastResponse.emit(MovieState.Success(response.castList))
            } catch (e: Exception) {
                _seriesCastResponse.emit(MovieState.Error("Error fetching series cast"))
            }
        }
    }

    fun fetchSimilarSeries(seriesId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getSimilarSeries(seriesId).first()
                _similarSeriesResponse.emit(MovieState.Success(response))
            } catch (e: Exception) {
                _similarSeriesResponse.emit(MovieState.Error("Error fetching similar series"))
            }
        }
    }

    fun fetchSeriesCastAndCrew(seriesId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getSeriesCast(seriesId).first()
                _seriesCrewCastResponse.emit(MovieState.Success(Pair(response.castList, response.crewList)))
            } catch (e: Exception) {
                _seriesCrewCastResponse.emit(MovieState.Error("cast error, try again :*"))
            }
        }
    }


}