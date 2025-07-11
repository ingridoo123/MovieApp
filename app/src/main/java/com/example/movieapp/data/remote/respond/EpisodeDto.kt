package com.example.movieapp.data.remote.respond

import com.google.gson.annotations.SerializedName

data class EpisodeDto(
    @SerializedName("air_date")
    val airDate: String?,
    @SerializedName("episode_number")
    val episodeNumber: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("season_number")
    val seasonNumber: Int,
    @SerializedName("still_path")
    val stillPath: String?,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int
)