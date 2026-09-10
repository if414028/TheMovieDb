package com.reynaldo.themoviedb.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object GenresRoute

@Serializable
data class MoviesRoute(
    val genreId: Int,
    val genreName: String
)

@Serializable
data class MovieDetailRoute(
    val movieId: Int
)

@Serializable
data class ReviewsRoute(
    val movieId: Int
)

@Serializable
data class TrailerRoute(
    val movieId: Int
)