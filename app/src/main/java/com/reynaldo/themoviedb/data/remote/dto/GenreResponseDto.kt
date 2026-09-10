package com.reynaldo.themoviedb.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GenreResponseDto(
    val genres: List<GenreDto>
)

@Serializable
data class GenreDto(
    val id: Int,
    val name: String
)