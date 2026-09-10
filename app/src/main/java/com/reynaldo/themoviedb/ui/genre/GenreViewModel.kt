package com.reynaldo.themoviedb.ui.genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reynaldo.themoviedb.domain.repository.MovieRepository
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
class GenreViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<GenreUiState>(GenreUiState.Loading)

    val uiState = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadGenres()
    }

    fun loadGenres() {
        if (loadJob?.isActive == true) return

        loadJob = viewModelScope.launch {
            _uiState.value = GenreUiState.Loading

            try {
                val genres = repository.getMovieGenres()

                _uiState.value = if (genres.isEmpty()) {
                    GenreUiState.Empty
                } else {
                    GenreUiState.Success(genres)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: HttpException) {
                val message = when (exception.code()) {
                    401 -> "Akses ditolak. Periksa konfigurasi token TMDB."
                    429 -> "Terlalu banyak permintaan. Coba lagi sebentar."
                    in 500..599 -> "Server sedang bermasalah. Coba lagi."
                    else -> "Gagal memuat genre. Silakan coba lagi."
                }

                _uiState.value = GenreUiState.Error(message)
            } catch (exception: IOException) {
                _uiState.value = GenreUiState.Error(
                    "Tidak dapat terhubung. Periksa koneksi internet."
                )
            } catch (exception: SerializationException) {
                _uiState.value = GenreUiState.Error(
                    "Data dari server tidak dapat dibaca."
                )
            }
        }
    }
}