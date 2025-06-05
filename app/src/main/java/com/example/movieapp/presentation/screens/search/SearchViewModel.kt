package com.example.movieapp.presentation.screens.search

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.movieapp.data.remote.respond.BackdropImage
import com.example.movieapp.data.remote.respond.MovieResponse
import com.example.movieapp.data.repository.SearchRepositoryImpl
import com.example.movieapp.domain.model.Search
import com.example.movieapp.util.MovieState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepositoryImpl
): ViewModel() {

    private val _searchResults = MutableStateFlow<List<Search>>(emptyList())
    val searchResults: StateFlow<List<Search>> = _searchResults

    private val _searchLoading = MutableStateFlow(false)
    val searchLoading: StateFlow<Boolean> = _searchLoading

    private val _response1: MutableStateFlow<MovieState<MovieResponse?>> = MutableStateFlow(MovieState.Loading)
    val popularMovieResponse: StateFlow<MovieState<MovieResponse?>> = _response1

    private val _movieImagesMap = mutableStateMapOf<Int, List<BackdropImage>?>()
    val movieImagesMap: Map<Int, List<BackdropImage>?> = _movieImagesMap

    var searchParam = mutableStateOf("")

    private val _sortBy = mutableStateOf("Popular")
    val sortBy: State<String> = _sortBy

    private val _ratingRange = mutableStateOf(0f..9f)
    val ratingRange: State<ClosedFloatingPointRange<Float>> = _ratingRange

    private val _selectedCountry = mutableStateOf<String?>(null)
    val selectedCountry: State<String?> = _selectedCountry

    private val _selectedYear = mutableStateOf<Int?>(null)
    val selectedYear: State<Int?> = _selectedYear




    fun searchRemoteMedia(includeAdult: Boolean) {
        viewModelScope.launch {
            if(searchParam.value.isNotEmpty()) {
                _searchLoading.value = true
                try {
                    Log.d("SearchScreen","${_selectedYear.value.toString()} VIEWMODEL")
                    searchRepository.multiSearchWithSorting(
                        searchParams = searchParam.value,
                        includeAdult = includeAdult,
                        sortBy = _sortBy.value,
                        ratingRange = _ratingRange.value,
                        originalLanguage = _selectedCountry.value,
                        year = _selectedYear.value
                    ).collect { results ->
                        _searchResults.value = results
                        _searchLoading.value = false
                    }
                } catch (e: Exception) {
                    _searchResults.value = emptyList()
                    _searchLoading.value = false
                }
            }
        }
    }

    fun fetchPopularMovies() {
        viewModelScope.launch {
            try {
                searchRepository.getPopularMoviesWithSorting(
                    sortBy = _sortBy.value,
                    ratingRange = _ratingRange.value
                ).collect {response ->
                    _response1.emit(MovieState.Success(response))
                }
            } catch (e: Exception) {
                val errorMessage = "Error popular"
                _response1.emit(MovieState.Error(errorMessage))
            }
        }
    }

    fun fetchMovieImages(movieId: Int) {
        viewModelScope.launch {
            try {
                val response = searchRepository.getMovieImages(movieId.toString()).first()
                _movieImagesMap[movieId] = response.backdrops
            } catch (e: Exception) {
                _movieImagesMap[movieId] = null
            }
        }
    }

    fun updateSortBy(sortOption: String) {
        _sortBy.value = sortOption

        fetchPopularMovies()

        if(searchParam.value.isNotEmpty()) {
            searchRemoteMedia(false)
        }
    }

    fun updateSelectedCountry(country: String?) {
        _selectedCountry.value = country
        fetchPopularMovies()
        if(searchParam.value.isNotEmpty()) {
            searchRemoteMedia(false)
        }
    }

    fun updateSelectedYear(year: Int?) {
        _selectedYear.value = year
        fetchPopularMovies()
        if(searchParam.value.isNotEmpty()) {
            searchRemoteMedia(false)
        }
    }

    fun updateRatingRange(ratingRange: ClosedFloatingPointRange<Float>) {
        _ratingRange.value = ratingRange
        fetchPopularMovies()
        if(searchParam.value.isNotEmpty()) {
            searchRemoteMedia(false)
        }
    }


    fun resetAllFilters() {
        _sortBy.value = "Popular"
        _ratingRange.value = 0f..9f
        _selectedCountry.value = null
        _selectedYear.value = null
        fetchPopularMovies()
        if(searchParam.value.isNotEmpty()) {
            searchRemoteMedia(false)
        }
    }





//    private fun applySortingToSearchResults(searchResults: List<Search>): List<Search> {
//        return when(_sortBy.value) {
//            "Newest" -> searchResults.sortedByDescending { search ->
//                try {
//                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                        .parse(search.releaseDate ?: "1900-01-01")
//                } catch(E: Exception) {
//                    Date(0)
//                }
//            }
//            "Popular" -> searchResults.sortedByDescending { it.voteAverage ?: 0.0 }
//            "Rating" -> searchResults.sortedByDescending { it.popularity ?: 0.0 }
//            else -> searchResults
//        }
//    }

}