package com.example.movieapp.presentation.screens.search

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
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepositoryImpl
): ViewModel() {
    private var _multiSearchState = mutableStateOf<Flow<PagingData<Search>>>(emptyFlow())
    val multiSearchState: State<Flow<PagingData<Search>>> = _multiSearchState

    private val _response1: MutableStateFlow<MovieState<MovieResponse?>> = MutableStateFlow(MovieState.Loading)
    val popularMovieResponse: StateFlow<MovieState<MovieResponse?>> = _response1

    private val _movieImagesMap = mutableStateMapOf<Int, List<BackdropImage>?>()
    val movieImagesMap: Map<Int, List<BackdropImage>?> = _movieImagesMap

    var searchParam = mutableStateOf("")

    init {
        fetchPopularMovies()
    }

    fun searchRemoteMedia(includeAdult: Boolean) {
        viewModelScope.launch {
            if(searchParam.value.isNotEmpty()) {
                _multiSearchState.value = searchRepository.multiSearch(
                    searchParams = searchParam.value,
                    includeAdult
                ).map { result ->
                    result.filter { (it.title != null || it.originalName != null || it.originalTitle != null) && it.mediaType == "movie" }
                }.cachedIn(viewModelScope)
            }
        }
    }
    fun fetchPopularMovies() {
        viewModelScope.launch {
            try {
                val response = searchRepository.getPopularMovies().first()
                _response1.emit(MovieState.Success(response))
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

}