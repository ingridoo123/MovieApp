package com.example.movieapp.data.remote.respond

import com.google.gson.annotations.SerializedName

data class SeriesImagesResponse (
    @SerializedName("backdrops")
    val backdrops: List<BackdropImage>
)