package com.example.movieapp.data.repository

import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.data.remote.respond.CastResponse
import com.example.movieapp.data.remote.respond.MovieDetailsDTO
import com.example.movieapp.data.remote.respond.MovieImagesResponse
import com.example.movieapp.data.remote.respond.MovieResponse
import com.example.movieapp.data.remote.respond.PersonMovieCreditsResponse
import com.example.movieapp.data.remote.respond.PersonSeriesCreditsResponse
import com.example.movieapp.data.remote.respond.SeasonDetailsDto
import com.example.movieapp.data.remote.respond.SeriesCastResponse
import com.example.movieapp.data.remote.respond.SeriesDetailsDTO
import com.example.movieapp.data.remote.respond.SeriesResponse
import com.example.movieapp.data.remote.respond.VideoResponse
import com.example.movieapp.domain.model.Person
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

    fun getMovieImages(movieId: String): Flow<MovieImagesResponse> = flow {
        val response = apiService.getMovieImages(movieId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getPersonDetails(personId: String): Flow<Person> = flow {
        val response = apiService.getPersonDetails(personId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getPersonMovieCredits(personId: String): Flow<PersonMovieCreditsResponse> = flow {
        val response = apiService.getPersonMovieCredits(personId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getSeriesDetails(seriesId: String): Flow<SeriesDetailsDTO> = flow {
        val response = apiService.getSeriesDetails(seriesId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getSeriesTrailers(seriesId: String): Flow<VideoResponse> = flow {
        val response = apiService.getSeriesTrailer(seriesId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getSeasonDetails(seriesId: String, seasonNumber: Int): Flow<SeasonDetailsDto> = flow {
        val response = apiService.getSeasonDetails(seriesId.toInt(), seasonNumber)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getPersonSeriesCredits(personId: String): Flow<PersonSeriesCreditsResponse> = flow {
        val response = apiService.getPersonSeriesCredits(personId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getSeriesCast(seriesId: String): Flow<SeriesCastResponse> = flow {
        val response = apiService.getSeriesCast(seriesId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getSimilarSeries(seriesId: String): Flow<SeriesResponse> = flow {
        val response = apiService.getSimilarSeries(seriesId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)





}