package com.example.movieapp.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Job(
    @SerializedName("job")
    val job: String,
    @SerializedName("episode_count")
    val episodeCount: Int
) : Parcelable

@Parcelize
data class SeriesCrew(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("profile_path")
    val profilePath: String?,
    @SerializedName("jobs")
    val jobs: List<Job>,
    @SerializedName("department")
    val department: String,
    @SerializedName("total_episode_count")
    val totalEpisodeCount: Int
) : Parcelable