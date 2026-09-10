package com.reynaldo.themoviedb.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.reynaldo.themoviedb.domain.repository.MovieRepository
import com.reynaldo.themoviedb.ui.components.toUserMessage
import com.reynaldo.themoviedb.ui.navigation.MovieDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId =
        savedStateHandle.toRoute<MovieDetailRoute>().movieId

    private val _uiState =
        MutableStateFlow<MovieDetailUiState>(MovieDetailUiState.Loading)

    val uiState = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadDetail()
    }

    fun loadDetail() {
        if (loadJob?.isActive == true) return

        loadJob = viewModelScope.launch {
            if (movieId <= 0) {
                _uiState.value = MovieDetailUiState.Error(
                    "ID film tidak valid."
                )
                return@launch
            }

            _uiState.value = MovieDetailUiState.Loading

            try {
                val movie = repository.getMovieDetail(movieId)

                _uiState.value = MovieDetailUiState.Success(movie)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: HttpException) {
                val message = if (exception.code() == 404) {
                    "Film tidak ditemukan."
                } else {
                    exception.toUserMessage()
                }

                _uiState.value = MovieDetailUiState.Error(message)
            } catch (exception: IOException) {
                _uiState.value = MovieDetailUiState.Error(
                    exception.toUserMessage()
                )
            } catch (exception: SerializationException) {
                _uiState.value = MovieDetailUiState.Error(
                    exception.toUserMessage()
                )
            }
        }
    }
}