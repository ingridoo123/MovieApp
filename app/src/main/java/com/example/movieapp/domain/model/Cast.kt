package com.example.movieapp.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cast(
    @SerializedName("id")
    val id: Int,
    @SerializedName("known_for_department")
    val department: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("profile_path")
    val profilePath: String?,
    @SerializedName("job")
    val job: String
): Parcelable
