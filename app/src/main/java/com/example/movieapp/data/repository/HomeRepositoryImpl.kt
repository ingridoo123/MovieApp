package com.example.movieapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.movieapp.data.paging.MovieGenrePagingSource
import com.example.movieapp.data.paging.MoviePagingSource
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.data.remote.respond.GenreResponse
import com.example.movieapp.data.remote.respond.MovieDetailsDTO
import com.example.movieapp.data.remote.respond.MovieResponse
import com.example.movieapp.domain.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.random.Random

class HomeRepositoryImpl @Inject constructor(private val apiService: MediaAPI) {

    fun getNowPlayingMovies(): Flow<MovieResponse> = flow {
        val response = apiService.getNowPlayingMovies(1)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getPopularMovies(): Flow<MovieResponse> = flow {
        val response = apiService.getPopularMovies(1)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getTopRaredMovies(): Flow<MovieResponse> = flow {
        val randomPage = Random.nextInt(1,4)
        val response = apiService.getTopRatedMovies(randomPage)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getAllMoviesPagination(tags: String): Pager<Int, Movie> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {MoviePagingSource(apiService,tags)}
        )
    }

    fun getGenresWiseMovie(tags: Int): Pager<Int, Movie> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MovieGenrePagingSource(apiService,tags) }
        )
    }

    fun getDiscoverMovies(): Flow<MovieResponse> = flow {
        val response = apiService.getDiscoverMovies(1)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getTrendingMovies(): Flow<MovieResponse> = flow {
        val response = apiService.getTrendingMovies(1)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getUpcomingMovies(): Flow<MovieResponse> = flow {
        val response = apiService.getUpcomingMovies(1)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getMovieGenres(): Flow<GenreResponse> = flow {
        val response = apiService.getMovieGenres()
        emit(response)
    }.flowOn(Dispatchers.IO)


    fun getMovieDetails(moveId: String): Flow<MovieDetailsDTO> = flow {
        val response = apiService.getMoviesDetails(moveId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)

}