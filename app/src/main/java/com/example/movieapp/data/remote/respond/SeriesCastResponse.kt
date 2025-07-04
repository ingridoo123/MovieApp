package com.example.movieapp.data.remote.respond

import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.SeriesCrew
import com.google.gson.annotations.SerializedName

data class SeriesCastResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("cast")
    val castList: List<Cast>,
    @SerializedName("crew")
    val crewList: List<SeriesCrew>
)