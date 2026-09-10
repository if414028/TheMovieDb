package com.reynaldo.themoviedb.ui.movies

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.reynaldo.themoviedb.domain.repository.MovieRepository
import com.reynaldo.themoviedb.ui.navigation.MoviesRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<MoviesRoute>()

    val genreName: String = route.genreName

    val movies = repository
        .discoverMovies(route.genreId)
        .cachedIn(viewModelScope)
}