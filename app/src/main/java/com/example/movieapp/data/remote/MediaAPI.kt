package com.example.movieapp.data.remote


import com.example.movieapp.data.remote.respond.CastResponse
import com.example.movieapp.data.remote.respond.GenreResponse
import com.example.movieapp.data.remote.respond.MovieDetailsDTO
import com.example.movieapp.data.remote.respond.MovieImagesResponse
import com.example.movieapp.data.remote.respond.MovieResponse
import com.example.movieapp.data.remote.respond.PersonMovieCreditsResponse
import com.example.movieapp.data.remote.respond.PersonSeriesCreditsResponse
import com.example.movieapp.data.remote.respond.SearchResponse
import com.example.movieapp.data.remote.respond.SeasonDetailsDto
import com.example.movieapp.data.remote.respond.SeriesDetailsDTO
import com.example.movieapp.data.remote.respond.SeriesResponse
import com.example.movieapp.data.remote.respond.VideoResponse
import com.example.movieapp.domain.model.Person
import com.example.movieapp.domain.model.Series
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MediaAPI {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en"
    ): MovieResponse

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en"
    ): MovieResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en"
    ): MovieResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en"
    ): MovieResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en"
    ): MovieResponse


    @GET("discover/movie?")
    suspend fun getDiscoverMovies(
        @Query("page") page: Int = 0,
        @Query("primary_release_date.gte") gteReleaseDate: String = "1940-01-01",
        @Query("primary_release_date.lte") lteReleaseDate: String = "1981-01-01",
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en",
        @Query("sort_by") sortBy: String = "popularity.desc",
    ): MovieResponse

    @GET("discover/movie")
    suspend fun getGenreWiseMovieList(
        @Query("with_genres") genresId: Int,
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en",
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("vote_average.gte") voteAverageGte: Float = 2.0f,
        @Query("popularity.gte") popularityGte: Float = 34.0f

    ): MovieResponse

    @GET("discover/movie")
    suspend fun getRecommendedMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("popularity.gte") popularityGte: Float = 35.0f,
        @Query("vote_average.gte") voteAverageGte: Float = 5.5f,
        @Query("vote_count.gte") voteCountGte: Int = 3000
    ): MovieResponse

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Path("movie_id") filmId: Int,
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"
    ): MovieResponse

    @GET("movie/{movie_id}")
    suspend fun getMoviesDetails(
        @Path("movie_id") movieId: Int,
        @Query("append_to_response") appendToResponse: String = "videos,credits",
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en"
    ): MovieDetailsDTO

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCast(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = API_KEY,
    ): CastResponse

    @GET("person/{person_id}")
    suspend fun getPersonDetails(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"
    ): Person

    @GET("person/{person_id}/movie_credits")
    suspend fun getPersonMovieCredits(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"
    ): PersonMovieCreditsResponse

    @GET("movie/{movie_id}/images")
    suspend fun getMovieImages(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): MovieImagesResponse

    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String ="en"
    ): GenreResponse

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieTrailer(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = API_KEY
    ):  VideoResponse

    @GET("search/multi")
    suspend fun search(
        @Query("query") searchParams: String,
        @Query("page") page: Int = 0,
        @Query("include_adult") includeAdult: Boolean = true,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en"
    ): SearchResponse

    //TV SERIES!!!!!!!!!!!!!

    @GET("discover/tv")
    suspend fun getSeriesByGenre(
        @Query("with_genres") genresId: Int,
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en",
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("vote_average.gte") voteAverageGte: Float = 2.0f,
        @Query("popularity.gte") popularityGte: Float = 34.0f,
        @Query("sort_by") sortBy: String ="popularity.desc",
    ): MovieResponse

    @GET("discover/tv")
    suspend fun getRecommendedSeries(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("popularity.gte") popularityGte: Float = 35.0f,
        @Query("vote_average.gte") voteAverageGte: Float = 5.5f,
        @Query("vote_count.gte") voteCountGte: Int = 3000
    ): SeriesResponse

    @GET("discover/tv")
    suspend fun getTopRatedSeries(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("sort_by") sortBy: String = "vote_average.desc",
        @Query("vote_count.gte") voteCountGte: Int = 1200
    ): SeriesResponse

    @GET("tv/{series_id}")
    suspend fun getSeriesDetails(
        @Path("series_id") seriesId: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"
    ): SeriesDetailsDTO

    @GET("tv/{series_id}/videos")
    suspend fun getSeriesTrailer(
        @Path("series_id") seriesId: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): VideoResponse

    @GET("tv/{series_id}/season/{season_number}")
    suspend fun getSeasonDetails(
        @Path("series_id") seriesId: Int,
        @Path("season_number") seasonNumber: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"
    ): SeasonDetailsDto

    @GET("person/{person_id}/tv_credits")
    suspend fun getPersonSeriesCredits(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"
    ): PersonSeriesCreditsResponse

    @GET("tv/{series_id}/credits")
    suspend fun getSeriesCast(
        @Path("series_id") seriesId: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): CastResponse

    @GET("tv/{series_id}/similar")
    suspend fun getSimilarSeries(
        @Path("series_id") seriesId: Int,
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"
    ): SeriesResponse




    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL = "https://image.tmbd.org/t/p/w500"
        const val BASE_BACKDROP_IMAGE_URL = "https://image.tmdb.org/t/p/w780/"
        const val API_KEY = "fdc96eaf6179fd6207094d6658be9dae"


    }
}