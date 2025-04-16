package com.example.movieapp.util

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.movieapp.R

object Constants {
    const val POPULAR = "popular"
    const val UPCOMING = "upcoming"

    const val unavailable = "Unavailable"

    const val nowPlayingAllListScreen = "nowPlayingAllListScreen"
    const val popularAllListScreen = "popularAllListScreen"
    const val discoverListScreen = "DiscoverListScreen"
    const val upcomingListScreen = "upcomingListScreen"
    const val similarListScreen = "similarListing"
    const val genreWiseMovie = "genreWiseMovie"

    val netflixFamily = FontFamily(
        Font(R.font.netflixsans_bold, FontWeight.Bold),
        Font(R.font.netflixsans_regular, FontWeight.Normal),
        Font(R.font.netflixsans_medium, FontWeight.Medium)
    )

}