package com.example.movieapp.data.remote.respond

import com.example.movieapp.domain.model.Trailer
import com.google.gson.annotations.SerializedName

data class VideoResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("results")
    val results: List<Trailer>
)
