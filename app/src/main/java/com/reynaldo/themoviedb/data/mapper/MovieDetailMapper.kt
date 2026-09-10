package com.reynaldo.themoviedb.data.mapper

import com.reynaldo.themoviedb.data.remote.dto.MovieDetailDto
import com.reynaldo.themoviedb.domain.model.Genre
import com.reynaldo.themoviedb.domain.model.MovieDetail

fun MovieDetailDto.toDomain(): MovieDetail = MovieDetail(
    id = id,
    title = title,
    overview = overview,
    tagline = tagline,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    runtimeMinutes = runtime,
    rating = voteAverage,
    voteCount = voteCount,
    genres = genres.map { Genre(id = it.id, name = it.name) }
)