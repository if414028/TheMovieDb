package com.reynaldo.themoviedb.ui.trailer

import com.reynaldo.themoviedb.domain.model.Trailer

sealed interface TrailerUiState {
    data object Loading : TrailerUiState
    data object Empty : TrailerUiState

    data class Success(
        val trailer: Trailer
    ) : TrailerUiState

    data class Error(
        val message: String
    ) : TrailerUiState
}