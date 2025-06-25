package com.example.movieapp.data.remote.respond

import com.example.movieapp.domain.model.Series
import com.google.gson.annotations.SerializedName

data class SeriesResponse (
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<Series>,
    @SerializedName("total_pages")
    val total_pages:Int,
    @SerializedName("total_results")
    val total_results: Int
)