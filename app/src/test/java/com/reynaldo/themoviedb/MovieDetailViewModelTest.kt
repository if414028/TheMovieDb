package com.reynaldo.themoviedb

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.reynaldo.themoviedb.domain.model.Genre
import com.reynaldo.themoviedb.domain.model.MovieDetail
import com.reynaldo.themoviedb.domain.repository.MovieRepository
import com.reynaldo.themoviedb.ui.detail.MovieDetailUiState
import com.reynaldo.themoviedb.ui.detail.MovieDetailViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [28],
    manifest = Config.NONE,
    application = Application::class
)
class MovieDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<MovieRepository>()

    private fun createViewModel(movieId: Int = 101) =
        MovieDetailViewModel(
            repository = repository,
            savedStateHandle = SavedStateHandle(
                mapOf("movieId" to movieId)
            )
        )

    private fun movie() = MovieDetail(
        id = 101,
        title = "Test Movie",
        overview = "A test synopsis",
        tagline = null,
        posterPath = null,
        backdropPath = null,
        releaseDate = "2026-01-01",
        runtimeMinutes = 120,
        rating = 8.5,
        voteCount = 100,
        genres = listOf(Genre(28, "Action"))
    )

    @Test
    fun `loads detail using movie id from navigation`() = runTest {
        val expected = movie()
        coEvery { repository.getMovieDetail(101) } returns expected

        val viewModel = createViewModel()

        assertEquals(
            MovieDetailUiState.Loading,
            viewModel.uiState.value
        )

        advanceUntilIdle()

        val state =
            viewModel.uiState.value as MovieDetailUiState.Success

        assertEquals(expected, state.movie)

        coVerify(exactly = 1) {
            repository.getMovieDetail(101)
        }
    }

    @Test
    fun `404 produces movie not found message`() = runTest {
        val error = HttpException(
            Response.error<Any>(
                404,
                """{"status_message":"Not found"}""".toResponseBody()
            )
        )

        coEvery { repository.getMovieDetail(101) } throws error

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state =
            viewModel.uiState.value as MovieDetailUiState.Error

        assertEquals("Film tidak ditemukan.", state.message)
    }

    @Test
    fun `retry succeeds after network failure`() = runTest {
        coEvery {
            repository.getMovieDetail(101)
        } throws IOException("Offline")

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(
            viewModel.uiState.value is MovieDetailUiState.Error
        )

        coEvery {
            repository.getMovieDetail(101)
        } returns movie()

        viewModel.loadDetail()
        advanceUntilIdle()

        assertTrue(
            viewModel.uiState.value is MovieDetailUiState.Success
        )

        coVerify(exactly = 2) {
            repository.getMovieDetail(101)
        }
    }

    @Test
    fun `invalid movie id produces error without calling repository`() = runTest {
        val viewModel = createViewModel(movieId = -1)
        advanceUntilIdle()

        val state =
            viewModel.uiState.value as MovieDetailUiState.Error

        assertEquals("ID film tidak valid.", state.message)

        coVerify(exactly = 0) {
            repository.getMovieDetail(any())
        }
    }
}