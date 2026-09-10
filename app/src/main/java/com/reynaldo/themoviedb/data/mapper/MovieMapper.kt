package com.reynaldo.themoviedb.data.mapper

import com.reynaldo.themoviedb.data.remote.dto.MovieDto
import com.reynaldo.themoviedb.domain.model.Movie

fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    title = title,
    posterPath = posterPath,
    releaseDate = releaseDate,
    rating = voteAverage
)