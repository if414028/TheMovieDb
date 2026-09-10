package com.reynaldo.themoviedb.domain.repository

import androidx.paging.PagingData
import com.reynaldo.themoviedb.domain.model.Genre
import com.reynaldo.themoviedb.domain.model.Movie
import com.reynaldo.themoviedb.domain.model.MovieDetail
import com.reynaldo.themoviedb.domain.model.Review
import com.reynaldo.themoviedb.domain.model.Trailer
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getMovieGenres(): List<Genre>

    fun discoverMovies(genreId: Int): Flow<PagingData<Movie>>

    suspend fun getMovieDetail(movieId: Int): MovieDetail

    fun getMovieReviews(movieId: Int): Flow<PagingData<Review>>

    suspend fun getMovieTrailer(movieId: Int): Trailer?
}