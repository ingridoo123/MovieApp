package com.example.movieapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.movieapp.data.paging.SearchMovieSource
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.data.remote.respond.MovieImagesResponse
import com.example.movieapp.data.remote.respond.MovieResponse
import com.example.movieapp.domain.model.Search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: MediaAPI
) {

    fun multiSearch(searchParams: String, includeAdult: Boolean): Flow<PagingData<Search>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                SearchMovieSource(apiService = apiService, searchParams = searchParams, includeAdult)
            }
        ).flow
    }

    fun getPopularMovies(): Flow<MovieResponse> = flow {
        val response = apiService.getPopularMovies(1)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getMovieImages(movieId: String): Flow<MovieImagesResponse> = flow {
        val response = apiService.getMovieImages(movieId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)
}