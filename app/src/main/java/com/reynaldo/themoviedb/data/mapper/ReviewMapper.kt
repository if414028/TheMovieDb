package com.reynaldo.themoviedb.data.mapper

import com.reynaldo.themoviedb.data.remote.dto.ReviewDto
import com.reynaldo.themoviedb.domain.model.Review

fun ReviewDto.toDomain(): Review = Review(
    id = id,
    author = author.ifBlank { "Anonymous" },
    content = content,
    createdAt = createdAt,
    rating = authorDetails?.rating
)