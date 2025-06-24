package com.example.movieapp.presentation.screens.favourite

import android.provider.MediaStore.Audio.Media
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movieapp.data.local.media.MediaEntity
import com.example.movieapp.data.repository.MyMoviesRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

import javax.inject.Inject


@HiltViewModel
class FavouriteViewModel @Inject constructor(private val myMoviesRepository: MyMoviesRepositoryImpl): ViewModel() {

    private var _isFavourite = mutableStateOf(0)
    val isFavourite: State<Int> = _isFavourite

    private val _myMovieData = mutableStateOf<Flow<List<MediaEntity>>>(emptyFlow())
    val myMovieData: State<Flow<List<MediaEntity>>> = _myMovieData

    init {
        allMoviesData()
    }

    private fun allMoviesData() {
        _myMovieData.value = myMoviesRepository.getAllMoviesData()
    }

    fun isFavourite(mediaId: Int) {
        viewModelScope.launch {
            _isFavourite.value = myMoviesRepository.exist(mediaId)
        }
    }

    fun addToFavourites(movie: MediaEntity) {
        viewModelScope.launch {
            val movieWithTimeStamp = movie.copy(addedOn = System.currentTimeMillis())
            myMoviesRepository.insertMovie(movieWithTimeStamp)
        }.invokeOnCompletion {
            isFavourite(movie.mediaId)
        }
    }

    fun removeFromFavourites(mediaId: Int) {
        viewModelScope.launch {
            myMoviesRepository.removeFromList(mediaId)
        }.invokeOnCompletion {
            isFavourite(mediaId)
        }
    }


}