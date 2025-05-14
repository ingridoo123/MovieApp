package com.example.movieapp.domain.model

import com.google.gson.annotations.SerializedName

data class Person (
    @SerializedName("biography")
    val biography: String,
    @SerializedName("birthday")
    val birthday: String, // Format YYYY-MM-DD lub null
    @SerializedName("deathday")
    val deathDay: String?, // Format YYYY-MM-DD lub null
    @SerializedName("gender")
    val gender: Int?, // 0: Not set, 1: Female, 2: Male, 3: Non-binary
    @SerializedName("id")
    val id: Int,
    @SerializedName("known_for_department")
    val department: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("place_of_birth")
    val placeOfBirth: String?,
    @SerializedName("profile_path")
    val profilePath: String?
)
