package com.reynaldo.themoviedb.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String?,
    val tagline: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val runtimeMinutes: Int?,
    val rating: Double,
    val voteCount: Int,
    val genres: List<Genre>
)