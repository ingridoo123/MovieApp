package com.example.movieapp.data.remote.respond

import com.example.movieapp.domain.model.Genre
import com.google.gson.annotations.SerializedName

data class GenreResponse(
    @SerializedName("genres")
    val genres: List<Genre>
)
