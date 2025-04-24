package com.example.movieapp.data.remote.respond

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class MovieImagesResponse(
    @SerializedName("backdrops")
    val backdrops: List<BackdropImage>
)

@Parcelize
data class BackdropImage(
    @SerializedName("height")
    val height: Int,
    @SerializedName("iso_639_1")
    val language: String?,
    @SerializedName("file_path")
    val filePath: String,
    @SerializedName("width")
    val width: Int
) : Parcelable
