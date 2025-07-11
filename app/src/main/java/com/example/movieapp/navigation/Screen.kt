package com.example.movieapp.navigation

sealed class Screen(val route: String) {

    object Main : Screen("main_screen")

    object Launch: Screen("launch_screen")

    object Welcome: Screen("welcome_screen")

    object Home: Screen("home_screen")

    object Details: Screen("details_screen")

    object SeriesDetails: Screen("series_details_screen")

    object CastAndCrew: Screen("cast_and_crew_screen")

    object SeriesCastAndCrew: Screen("series_cast_and_crew_screen")

    object SimilarMovies: Screen("similar_movies_screen")

    object SimilarSeries: Screen("similar_series_screen")

    object Person: Screen("person_screen")
    object AllMovies: Screen("all_movies_screen")

    object GenreWise: Screen("genre_wise_screen")

    object Search: Screen("search_screen")

    object About: Screen("about_screen")



    object Favourite: Screen("favourite_screen")


}