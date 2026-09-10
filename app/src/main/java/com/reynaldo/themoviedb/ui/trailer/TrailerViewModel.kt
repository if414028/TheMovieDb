package com.reynaldo.themoviedb.ui.trailer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.reynaldo.themoviedb.domain.repository.MovieRepository
import com.reynaldo.themoviedb.ui.components.toUserMessage
import com.reynaldo.themoviedb.ui.navigation.TrailerRoute
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
class TrailerViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId =
        savedStateHandle.toRoute<TrailerRoute>().movieId

    private val _uiState =
        MutableStateFlow<TrailerUiState>(TrailerUiState.Loading)

    val uiState = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadTrailer()
    }

    fun loadTrailer() {
        if (loadJob?.isActive == true) return

        loadJob = viewModelScope.launch {
            _uiState.value = TrailerUiState.Loading

            try {
                val trailer = repository.getMovieTrailer(movieId)

                _uiState.value = if (trailer == null) {
                    TrailerUiState.Empty
                } else {
                    TrailerUiState.Success(trailer)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: HttpException) {
                _uiState.value = TrailerUiState.Error(
                    exception.toUserMessage()
                )
            } catch (exception: IOException) {
                _uiState.value = TrailerUiState.Error(
                    exception.toUserMessage()
                )
            } catch (exception: SerializationException) {
                _uiState.value = TrailerUiState.Error(
                    exception.toUserMessage()
                )
            }
        }
    }
}