package com.reynaldo.themoviedb

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.reynaldo.themoviedb.domain.model.Trailer
import com.reynaldo.themoviedb.domain.repository.MovieRepository
import com.reynaldo.themoviedb.ui.trailer.TrailerUiState
import com.reynaldo.themoviedb.ui.trailer.TrailerViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [28],
    manifest = Config.NONE,
    application = Application::class
)
class TrailerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<MovieRepository>()

    private fun createViewModel() = TrailerViewModel(
        repository = repository,
        savedStateHandle = SavedStateHandle(
            mapOf("movieId" to 101)
        )
    )

    @Test
    fun `available trailer produces success`() = runTest {
        val expected = Trailer(
            videoId = "abcdefghijk",
            name = "Official Trailer"
        )

        coEvery {
            repository.getMovieTrailer(101)
        } returns expected

        val viewModel = createViewModel()

        assertEquals(TrailerUiState.Loading, viewModel.uiState.value)

        advanceUntilIdle()

        val state =
            viewModel.uiState.value as TrailerUiState.Success

        assertEquals(expected, state.trailer)

        coVerify(exactly = 1) {
            repository.getMovieTrailer(101)
        }
    }

    @Test
    fun `missing trailer produces empty state`() = runTest {
        coEvery {
            repository.getMovieTrailer(101)
        } returns null

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(TrailerUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `network failure produces error`() = runTest {
        coEvery {
            repository.getMovieTrailer(101)
        } throws IOException("Offline")

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is TrailerUiState.Error)
    }

    @Test
    fun `retry loads trailer after failure`() = runTest {
        coEvery {
            repository.getMovieTrailer(101)
        } throws IOException("Offline")

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is TrailerUiState.Error)

        val expected = Trailer("abcdefghijk", "Official Trailer")

        coEvery {
            repository.getMovieTrailer(101)
        } returns expected

        viewModel.loadTrailer()
        advanceUntilIdle()

        val state =
            viewModel.uiState.value as TrailerUiState.Success

        assertEquals(expected, state.trailer)

        coVerify(exactly = 2) {
            repository.getMovieTrailer(101)
        }
    }
}