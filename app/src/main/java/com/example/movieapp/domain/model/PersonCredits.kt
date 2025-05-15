package com.example.movieapp.domain.model

import com.google.gson.annotations.SerializedName

data class PersonMovieCastCredits(
    val id: Int,
    val title: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("genre_ids")
    val genreIds: List<Int>?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String
)

data class PersonMovieCrewCredits(
    val id: Int,
    val title: String,
    val popularity: Double,
    @SerializedName("job")
    val job: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("genre_ids")
    val genreIds: List<Int>?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String
)

data class DisplayableMovieCredit(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val genreIds: List<Int>?,
)


