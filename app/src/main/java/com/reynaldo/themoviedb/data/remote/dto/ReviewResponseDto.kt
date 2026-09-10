package com.reynaldo.themoviedb.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewResponseDto(
    val page: Int,
    val results: List<ReviewDto>,
    @SerialName("total_pages")
    val totalPages: Int
)

@Serializable
data class ReviewDto(
    val id: String,
    val author: String,
    val content: String,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("author_details")
    val authorDetails: ReviewAuthorDto? = null
)

@Serializable
data class ReviewAuthorDto(
    val rating: Double? = null
)