package com.example.movieapp.data.remote.respond

import com.example.movieapp.domain.model.Cast
import com.google.gson.annotations.SerializedName


data class CastResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("cast")
    val castList: List<Cast>
)