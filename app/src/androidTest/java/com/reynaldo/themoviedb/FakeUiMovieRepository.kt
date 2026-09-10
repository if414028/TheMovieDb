package com.reynaldo.themoviedb

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.reynaldo.themoviedb.domain.model.Genre
import com.reynaldo.themoviedb.domain.model.Movie
import com.reynaldo.themoviedb.domain.model.MovieDetail
import com.reynaldo.themoviedb.domain.model.Review
import com.reynaldo.themoviedb.domain.model.Trailer
import com.reynaldo.themoviedb.domain.repository.MovieRepository
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeUiMovieRepository : MovieRepository {

    var genres: List<Genre> = listOf(Genre(28, "Action"))
    var failFirstGenreRequest: Boolean = false

    val genreCalls = AtomicInteger(0)

    @Volatile
    var requestedGenreId: Int? = null

    @Volatile
    var requestedDetailId: Int? = null

    @Volatile
    var requestedReviewsId: Int? = null

    @Volatile
    var requestedTrailerId: Int? = null

    override suspend fun getMovieGenres(): List<Genre> {
        val call = genreCalls.incrementAndGet()

        if (failFirstGenreRequest && call == 1) {
            throw IOException("Offline")
        }

        return genres
    }

    override fun discoverMovies(
        genreId: Int
    ): Flow<PagingData<Movie>> {
        requestedGenreId = genreId

        return flowOf(
            PagingData.from(
                listOf(
                    Movie(
                        id = 101,
                        title = "Assessment Movie",
                        posterPath = null,
                        releaseDate = "2026-01-01",
                        rating = 8.5
                    )
                )
            )
        )
    }

    override suspend fun getMovieDetail(movieId: Int): MovieDetail {
        requestedDetailId = movieId

        return MovieDetail(
            id = movieId,
            title = "Assessment Movie",
            overview = "This is the assessment movie synopsis.",
            tagline = null,
            posterPath = null,
            backdropPath = null,
            releaseDate = "2026-01-01",
            runtimeMinutes = 120,
            rating = 8.5,
            voteCount = 100,
            genres = listOf(Genre(28, "Action"))
        )
    }

    override fun getMovieReviews(
        movieId: Int
    ): Flow<PagingData<Review>> {
        requestedReviewsId = movieId

        return flowOf(
            PagingData.from(
                data = emptyList<Review>(),
                sourceLoadStates = LoadStates(
                    refresh = LoadState.NotLoading(
                        endOfPaginationReached = false
                    ),
                    prepend = LoadState.NotLoading(
                        endOfPaginationReached = true
                    ),
                    append = LoadState.NotLoading(
                        endOfPaginationReached = true
                    )
                )
            )
        )
    }

    override suspend fun getMovieTrailer(movieId: Int): Trailer? {
        requestedTrailerId = movieId
        return null
    }
}