package com.example.movieapp.data.repository

import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.data.remote.respond.CastResponse
import com.example.movieapp.data.remote.respond.MovieDetailsDTO
import com.example.movieapp.data.remote.respond.MovieResponse
import com.example.movieapp.data.remote.respond.VideoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MovieDetailsRepositoryImpl @Inject constructor(private val apiService: MediaAPI) {


    fun getMoviesDetails(movieId: String): Flow<MovieDetailsDTO> = flow {
        val response = apiService.getMoviesDetails(movieId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getSimilarMovies(movieId: String): Flow<MovieResponse> = flow {
        val response = apiService.getSimilarMovies(movieId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getMovieCast(movieId: String): Flow<CastResponse> = flow {
        val response = apiService.getMovieCast(movieId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getMovieTrailers(movieId: String): Flow<VideoResponse> = flow {
        val response = apiService.getMovieTrailer(movieId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)

}