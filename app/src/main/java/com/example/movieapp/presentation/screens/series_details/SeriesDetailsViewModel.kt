package com.example.movieapp.presentation.screens.series_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.remote.respond.SeriesDetailsDTO
import com.example.movieapp.data.repository.HomeRepositoryImpl
import com.example.movieapp.data.repository.MovieDetailsRepositoryImpl
import com.example.movieapp.domain.model.Trailer
import com.example.movieapp.util.MovieState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesDetailsViewModel @Inject constructor(private val repository: MovieDetailsRepositoryImpl): ViewModel() {
    private val _detailsSeriesResponse: MutableStateFlow<MovieState<SeriesDetailsDTO?>> = MutableStateFlow(MovieState.Loading)
    val detailsSeriesResponse: StateFlow<MovieState<SeriesDetailsDTO?>> = _detailsSeriesResponse

    private val _seriesTrailerResponse: MutableStateFlow<MovieState<List<Trailer>?>> = MutableStateFlow(MovieState.Loading)
    val seriesTrailerResponse: StateFlow<MovieState<List<Trailer>?>> = _seriesTrailerResponse

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
}