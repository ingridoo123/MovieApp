package com.example.movieapp.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.movieapp.data.paging.MovieGenrePagingSource
import com.example.movieapp.data.paging.MoviePagingSource
import com.example.movieapp.data.paging.SeriesGenrePagingSource
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.data.remote.respond.GenreResponse
import com.example.movieapp.data.remote.respond.MovieDetailsDTO
import com.example.movieapp.data.remote.respond.MovieResponse
import com.example.movieapp.data.remote.respond.SeriesResponse
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.Series
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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

    fun getTopRatedSeries(): Flow<SeriesResponse> = flow {
        coroutineScope {
            val page1 = async { apiService.getTopRatedSeries(1) }
            val page2 = async { apiService.getTopRatedSeries(2) }
            val page3 = async { apiService.getTopRatedSeries(3) }
            val page4 = async { apiService.getTopRatedSeries(4) }

            val responsePage1 = page1.await()
            val responsePage2 = page2.await()
            val responsePage3 = page3.await()
            val responsePage4 = page4.await()

            val combinedResults = responsePage1.results + responsePage2.results + responsePage3.results + responsePage4.results
            val shuffledResult = combinedResults.sortedByDescending { it.voteAverage }
            val finalResult: MutableList<Series> = mutableListOf()

            val respectedAnimations = listOf("INVINCIBLE", "Solo Leveling", "Arcane", "Attack on Titan", "Batman: The Animated Series", "Death Note", "Hazbin Hotel", "JoJo's Bizarre Adventure", "Chainsaw Man", "Rick and Morty", "Regular Show", "Hunter x Hunter", "JUJUTSU KAISEN")
            val animationId = 16

            shuffledResult.forEach { series ->
                val isAnimated = series.genreIds?.contains(animationId) == true
                if(isAnimated) {
                    if(series.name in respectedAnimations) {
                        finalResult.add(series)
                    }
                } else {
                    finalResult.add(series)
                }
            }

            finalResult.forEach {series ->
                Log.d("Top Rated Series", "${series.name}, VA: ${series.voteAverage}, VC: ${series.voteCount}, POP: ${series.popularity}, genre: ${series.genreIds} ")
            }

            val combinedResponse = responsePage1.copy(results = finalResult)
            emit(combinedResponse)
        }
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

    fun getSeriesByGenre(tags: Int): Pager<Int, Movie> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SeriesGenrePagingSource(apiService, tags) }
        )
    }

    fun getDiscoverMovies(): Flow<MovieResponse> = flow {
        val response = apiService.getDiscoverMovies(1)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getRecommendedMovies(): Flow<MovieResponse> = flow {
        coroutineScope {
            val page1 = async { apiService.getRecommendedMovies(1) }
            val page2 = async { apiService.getRecommendedMovies(2) }

            val responsePage1 = page1.await()
            val responsePage2 = page2.await()

            val combinedResults = responsePage1.results + responsePage2.results
            val sortedResults = combinedResults.distinctBy { it.id }.sortedByDescending { it.popularity }
            val combinedResponse = responsePage1.copy(results = sortedResults)

            emit(combinedResponse)
        }
    }.flowOn(Dispatchers.IO)

    fun getRecommendedSeries(): Flow<SeriesResponse> = flow {
        coroutineScope {
            val page1 = async { apiService.getRecommendedSeries(1) }
            val page2 = async { apiService.getRecommendedSeries(2) }

            val responsePage1 = page1.await()
            val responsePage2 = page2.await()

            val combinedResults = responsePage1.results + responsePage2.results
            val sortedResults = combinedResults.distinctBy { it.id }.sortedByDescending { it.popularity }
            val combinedResponse = responsePage1.copy(results = sortedResults)

            emit(combinedResponse)
        }
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