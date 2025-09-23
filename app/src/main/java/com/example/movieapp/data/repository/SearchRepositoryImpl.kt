package com.example.movieapp.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.movieapp.data.paging.SearchMovieSource
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.data.remote.respond.MovieImagesResponse
import com.example.movieapp.data.remote.respond.MovieResponse
import com.example.movieapp.data.remote.respond.SeriesImagesResponse
import com.example.movieapp.domain.model.Search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: MediaAPI
) {

    suspend fun multiSearchWithSorting(
        searchParams: String,
        includeAdult: Boolean,
        sortBy: String = "Popular",
        ratingRange: ClosedFloatingPointRange<Float> = 0f..9f,
        originalLanguage: String? = null,
        year: Int? = null,
        mediaType: Int? = 0,
        maxPages: Int = 5
    ): Flow<List<Search>> = flow {
        try {
            val allResults = mutableListOf<Search>()

            for (page in 1..maxPages) {
                val response = apiService.search(
                    searchParams = searchParams,
                    page = page,
                    includeAdult = includeAdult
                )

                if (response.results.isNullOrEmpty()) break

                allResults.addAll(response.results)


                if (page >= (response.totalPages ?: 1)) break
            }
            val calendar = Calendar.getInstance()


            val filteredResults = allResults.filter {

                val mediaTypeFilter = when(mediaType) {
                    1 -> it.mediaType == "movie"
                    2 -> it.mediaType == "tv"
                    else -> it.mediaType == "movie" || it.mediaType == "tv"
                }

                val baseFilter = when (it.mediaType) {
                    "movie" -> {
                        it.title != null &&
                                !it.releaseDate.isNullOrBlank() &&
                                it.voteAverage != 0.0 &&
                                it.backdropPath != null
                    }
                    "tv" -> {
                        it.title != null &&
                                it.voteAverage != null && it.voteAverage != 0.0 &&
                                it.backdropPath != null
                    }
                    else -> false
                }

                val ratingRange = (it.voteAverage ?: 0.0) >= ratingRange.start && (it.voteAverage ?: 0.0) <= ratingRange.endInclusive

                val languageFilter = originalLanguage == null || it.originalLanguage == originalLanguage

                val yearFilter = year == null || it.releaseDate?.let {yearParam ->
                    try {
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(yearParam)
                        if (date != null) {
                            calendar.time = date
                            calendar.get(Calendar.YEAR) == year
                        } else false
                    } catch (e: Exception) {
                        false
                    }
                } ?: false


                baseFilter && languageFilter && yearFilter && ratingRange && mediaTypeFilter

            }

            val sortedResults = when (sortBy) {
                "Newest" -> filteredResults.sortedByDescending { search ->
                    try {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .parse(search.releaseDate ?: "1900-01-01")
                    } catch (e: Exception) {
                        Date(0)
                    }
                }
                "Rating" -> filteredResults.sortedByDescending { it.voteAverage ?: 0.0 }
                "Popular" -> filteredResults.sortedByDescending { it.popularity ?: 0.0 }
                else -> filteredResults
            }

            emit(sortedResults)

        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    fun multiSearch(searchParams: String, includeAdult: Boolean): Flow<PagingData<Search>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                SearchMovieSource(apiService = apiService, searchParams = searchParams, includeAdult)
            }
        ).flow
    }

    suspend fun getPopularMoviesWithSorting(
        sortBy: String = "Popular",
        ratingRange: ClosedFloatingPointRange<Float> = 0f..9f,
        originalLanguage: String? = null,
        year: Int? = null
    ): Flow<MovieResponse> = flow {
        val response = apiService.getPopularMovies(1)

        // Sort and filter the popular movies
        val processedMovies = response.results.let { movies ->
            // First apply rating filter
            val filteredMovies = movies.filter { movie ->
                (movie.voteAverage ?: 0.0) >= ratingRange.start &&
                        (movie.voteAverage ?: 0.0) <= ratingRange.endInclusive
            }

            // Then apply sorting
            when (sortBy) {
                "Newest" -> filteredMovies.sortedByDescending { movie ->
                    try {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .parse(movie.releaseDate ?: "1900-01-01")
                    } catch (e: Exception) {
                        Date(0)
                    }
                }
                "Rating" -> filteredMovies.sortedByDescending { it.voteAverage ?: 0.0 }
                "Popular" -> filteredMovies.sortedByDescending { it.popularity ?: 0.0 }
                else -> filteredMovies
            }
        }

        emit(response.copy(results = processedMovies))
    }.flowOn(Dispatchers.IO)

    fun getPopularMovies(): Flow<MovieResponse> = flow {
        val response = apiService.getPopularMovies(1)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getMovieImages(movieId: String): Flow<MovieImagesResponse> = flow {
        val response = apiService.getMovieImages(movieId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getSeriesImages(seriesId: String): Flow<SeriesImagesResponse> = flow {
        val response = apiService.getSeriesImages(seriesId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)
}