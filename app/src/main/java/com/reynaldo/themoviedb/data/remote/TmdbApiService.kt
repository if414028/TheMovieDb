package com.reynaldo.themoviedb.data.remote

import com.reynaldo.themoviedb.data.remote.dto.GenreResponseDto
import com.reynaldo.themoviedb.data.remote.dto.MovieDetailDto
import com.reynaldo.themoviedb.data.remote.dto.MovieResponseDto
import com.reynaldo.themoviedb.data.remote.dto.ReviewResponseDto
import com.reynaldo.themoviedb.data.remote.dto.VideoResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("language") language: String = "en-US"
    ): GenreResponseDto

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int,
        @Query("language") language: String = "en-US",
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("include_adult") includeAdult: Boolean = false
    ): MovieResponseDto

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US"
    ): MovieDetailDto

    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): ReviewResponseDto

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US"
    ): VideoResponseDto
}