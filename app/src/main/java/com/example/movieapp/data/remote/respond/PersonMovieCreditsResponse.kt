package com.example.movieapp.data.remote.respond

import com.example.movieapp.domain.model.PersonMovieCastCredits
import com.example.movieapp.domain.model.PersonMovieCrewCredits

data class PersonMovieCreditsResponse(
    val cast: List<PersonMovieCastCredits>,
    val crew: List<PersonMovieCrewCredits>,
)