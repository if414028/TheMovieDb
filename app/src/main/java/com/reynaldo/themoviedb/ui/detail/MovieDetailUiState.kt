package com.reynaldo.themoviedb.ui.detail

import com.reynaldo.themoviedb.domain.model.MovieDetail

sealed interface MovieDetailUiState {
    data object Loading : MovieDetailUiState

    data class Success(
        val movie: MovieDetail
    ) : MovieDetailUiState

    data class Error(
        val message: String
    ) : MovieDetailUiState
}