package com.reynaldo.themoviedb.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.reynaldo.themoviedb.data.mapper.selectYoutubeTrailer
import com.reynaldo.themoviedb.data.mapper.toDomain
import com.reynaldo.themoviedb.data.paging.MoviesPagingSource
import com.reynaldo.themoviedb.data.paging.ReviewsPagingSource
import com.reynaldo.themoviedb.data.remote.TmdbApiService
import com.reynaldo.themoviedb.domain.model.Genre
import com.reynaldo.themoviedb.domain.model.Movie
import com.reynaldo.themoviedb.domain.model.MovieDetail
import com.reynaldo.themoviedb.domain.model.Review
import com.reynaldo.themoviedb.domain.model.Trailer
import com.reynaldo.themoviedb.domain.repository.MovieRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApiService
) : MovieRepository {

    override suspend fun getMovieGenres(): List<Genre> {
        return api.getMovieGenres().genres.map { dto ->
            Genre(
                id = dto.id,
                name = dto.name
            )
        }
    }

    override fun discoverMovies(
        genreId: Int
    ): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MoviesPagingSource(
                    api = api,
                    genreId = genreId
                )
            }
        ).flow
    }

    override suspend fun getMovieDetail(movieId: Int): MovieDetail {
        return api.getMovieDetail(movieId).toDomain()
    }

    override fun getMovieReviews(
        movieId: Int
    ): Flow<PagingData<Review>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 3,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ReviewsPagingSource(
                    api = api,
                    movieId = movieId
                )
            }
        ).flow
    }

    override suspend fun getMovieTrailer(movieId: Int): Trailer? {
        return api.getMovieVideos(movieId)
            .results
            .selectYoutubeTrailer()
    }
}