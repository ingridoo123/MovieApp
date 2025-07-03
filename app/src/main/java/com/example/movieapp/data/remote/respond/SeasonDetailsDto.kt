package com.example.movieapp.data.remote.respond

import com.google.gson.annotations.SerializedName

data class SeasonDetailsDto(
    @SerializedName("air_date")
    val airDate: String?,
    @SerializedName("episodes")
    val episodes: List<EpisodeDto>,
    @SerializedName("name")
    val name: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("season_number")
    val seasonNumber: Int
)