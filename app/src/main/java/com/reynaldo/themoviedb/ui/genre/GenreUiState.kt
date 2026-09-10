package com.reynaldo.themoviedb.ui.genre

import com.reynaldo.themoviedb.domain.model.Genre

sealed interface GenreUiState {
    data object Loading : GenreUiState
    data object Empty : GenreUiState
    data class Success(val genres: List<Genre>) : GenreUiState
    data class Error(val message: String) : GenreUiState
}