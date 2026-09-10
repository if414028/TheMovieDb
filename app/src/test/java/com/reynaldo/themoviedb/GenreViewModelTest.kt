package com.reynaldo.themoviedb

import com.reynaldo.themoviedb.domain.model.Genre
import com.reynaldo.themoviedb.domain.repository.MovieRepository
import com.reynaldo.themoviedb.ui.genre.GenreUiState
import com.reynaldo.themoviedb.ui.genre.GenreViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GenreViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<MovieRepository>()

    @Test
    fun `loading changes to success when repository returns genres`() = runTest {
        val expected = listOf(
            Genre(28, "Action"),
            Genre(35, "Comedy")
        )

        coEvery { repository.getMovieGenres() } returns expected

        val viewModel = GenreViewModel(repository)

        assertEquals(GenreUiState.Loading, viewModel.uiState.value)

        advanceUntilIdle()

        val state = viewModel.uiState.value as GenreUiState.Success
        assertEquals(expected, state.genres)

        coVerify(exactly = 1) {
            repository.getMovieGenres()
        }
    }

    @Test
    fun `empty result produces empty state`() = runTest {
        coEvery { repository.getMovieGenres() } returns emptyList()

        val viewModel = GenreViewModel(repository)
        advanceUntilIdle()

        assertEquals(GenreUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `network failure produces error state`() = runTest {
        coEvery {
            repository.getMovieGenres()
        } throws IOException("Offline")

        val viewModel = GenreViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value as GenreUiState.Error
        assertTrue(state.message.isNotBlank())
    }

    @Test
    fun `retry recovers after network failure`() = runTest {
        coEvery {
            repository.getMovieGenres()
        } throws IOException("Offline")

        val viewModel = GenreViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is GenreUiState.Error)

        val expected = listOf(Genre(28, "Action"))
        coEvery { repository.getMovieGenres() } returns expected

        viewModel.loadGenres()
        advanceUntilIdle()

        val state = viewModel.uiState.value as GenreUiState.Success
        assertEquals(expected, state.genres)

        coVerify(exactly = 2) {
            repository.getMovieGenres()
        }
    }

    @Test
    fun `repeated load calls do not duplicate an active request`() = runTest {
        val response = CompletableDeferred<List<Genre>>()

        coEvery {
            repository.getMovieGenres()
        } coAnswers {
            response.await()
        }

        val viewModel = GenreViewModel(repository)
        runCurrent()

        viewModel.loadGenres()
        viewModel.loadGenres()
        runCurrent()

        assertEquals(GenreUiState.Loading, viewModel.uiState.value)

        coVerify(exactly = 1) {
            repository.getMovieGenres()
        }

        response.complete(listOf(Genre(28, "Action")))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is GenreUiState.Success)
    }
}