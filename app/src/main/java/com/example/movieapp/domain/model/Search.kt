package com.example.movieapp.domain.model

import com.google.gson.annotations.SerializedName

data class Search(
    @SerializedName("adult")
    val adult: Boolean?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("genre_ids")
    val genreIds: List<Int>?,
    @SerializedName("genres")
    val genres: List<Genre>?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("imdb_id")
    val imdbId: String?,
    @SerializedName("media_type")
    val mediaType: String?,
    @SerializedName("origin_country")
    val originCountry: List<String>?,
    @SerializedName("original_language")
    val originalLanguage: String?,
    @SerializedName("original_name")
    val originalName: String?,
    @SerializedName("original_title")
    val originalTitle: String?,
    @SerializedName("overview")
    val overview: String?,
    @SerializedName("popularity")
    val popularity: Double?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date", alternate = ["first_air_date"])
    val releaseDate: String?,
    @SerializedName("title", alternate = ["name"])
    val title: String?,
    @SerializedName("video")
    val video: Boolean?,
    @SerializedName("runtime")
    val runtime: Int?,
    @SerializedName("vote_average")
    val voteAverage: Double?,
    @SerializedName("vote_count")
    val voteCount: Int?
)

fun Search.toMovie(): Movie? {
    // If id or title is missing, return null (cannot display as Movie)
    if (id == null || title == null) return null
    return Movie(
        adult = adult ?: false,
        backdropPath = backdropPath,
        posterPath = posterPath,
        genreIds = genreIds,
        genres = genres,
        mediaType = mediaType,
        id = id,
        imdbId = imdbId,
        originalLanguage = originalLanguage ?: "",
        originalTitle = originalTitle ?: title,
        overview = overview ?: "",
        popularity = popularity ?: 0.0,
        releaseDate = releaseDate ?: "N/A",
        runtime = runtime,
        title = title,
        video = video ?: false,
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0
    )
}