package com.example.movieapp.navigation

sealed class Screen(val route: String) {

    object Launch: Screen("launch_screen")

    object Welcome: Screen("welcome_screen")

    object Home: Screen("home_screen")

    object Details: Screen("details_screen")

    object AllMovies: Screen("all_movies_screen")

    object GenreWise: Screen("genre_wise_screen")

    object Search: Screen("search_screen")

    object About: Screen("about_screen")

    object Favourite: Screen("favourite_screen")


}