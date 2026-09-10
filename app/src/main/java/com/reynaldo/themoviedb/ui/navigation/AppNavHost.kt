package com.reynaldo.themoviedb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reynaldo.themoviedb.ui.detail.MovieDetailRouteScreen
import com.reynaldo.themoviedb.ui.genre.GenreRoute
import com.reynaldo.themoviedb.ui.movies.MoviesRouteScreen
import com.reynaldo.themoviedb.ui.reviews.ReviewsRouteScreen
import com.reynaldo.themoviedb.ui.trailer.TrailerRouteScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = GenresRoute
    ) {
        composable<GenresRoute> {
            GenreRoute(
                onGenreClick = { genre ->
                    navController.navigate(
                        MoviesRoute(
                            genreId = genre.id,
                            genreName = genre.name
                        )
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<MoviesRoute> {
            MoviesRouteScreen(
                onBack = { navController.popBackStack() },
                onMovieClick = { movieId ->
                    navController.navigate(
                        MovieDetailRoute(movieId)
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<MovieDetailRoute> {
            MovieDetailRouteScreen(
                onBack = { navController.popBackStack() },
                onReviewsClick = { movieId ->
                    navController.navigate(ReviewsRoute(movieId)) {
                        launchSingleTop = true
                    }
                },
                onTrailerClick = { movieId ->
                    navController.navigate(TrailerRoute(movieId)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<ReviewsRoute> {
            ReviewsRouteScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<TrailerRoute> {
            TrailerRouteScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}