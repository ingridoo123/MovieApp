package com.example.movieapp.data.remote.respond

import com.example.movieapp.domain.model.PersonMovieCastCredits
import com.example.movieapp.domain.model.PersonMovieCrewCredits
import com.example.movieapp.domain.model.PersonSeriesCastCredits
import com.example.movieapp.domain.model.PersonSeriesCrewCredits

data class PersonMovieCreditsResponse(
    val cast: List<PersonMovieCastCredits>,
    val crew: List<PersonMovieCrewCredits>,
)

data class PersonSeriesCreditsResponse(
    val cast: List<PersonSeriesCastCredits>,
    val crew: List<PersonSeriesCrewCredits>
)