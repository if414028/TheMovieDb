package com.reynaldo.themoviedb.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VideoResponseDto(
    val results: List<VideoDto>
)

@Serializable
data class VideoDto(
    val key: String,
    val name: String,
    val site: String,
    val type: String,
    val official: Boolean = false
)