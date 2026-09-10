package com.reynaldo.themoviedb.ui.reviews

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.reynaldo.themoviedb.domain.repository.MovieRepository
import com.reynaldo.themoviedb.ui.navigation.ReviewsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId =
        savedStateHandle.toRoute<ReviewsRoute>().movieId

    val reviews = repository
        .getMovieReviews(movieId)
        .cachedIn(viewModelScope)
}