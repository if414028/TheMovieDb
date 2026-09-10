package com.reynaldo.themoviedb

import androidx.paging.PagingSource
import com.reynaldo.themoviedb.data.paging.MoviesPagingSource
import com.reynaldo.themoviedb.data.remote.TmdbApiService
import com.reynaldo.themoviedb.data.remote.dto.MovieDto
import com.reynaldo.themoviedb.data.remote.dto.MovieResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class MoviesPagingSourceTest {

    private val api = mockk<TmdbApiService>()

    private fun refresh(page: Int? = null) =
        PagingSource.LoadParams.Refresh<Int>(
            key = page,
            loadSize = 20,
            placeholdersEnabled = false
        )

    @Test
    fun `first page uses selected genre and provides next page`() = runTest {
        coEvery {
            api.discoverMovies(genreId = 28, page = 1)
        } returns MovieResponseDto(
            page = 1,
            results = listOf(
                MovieDto(
                    id = 101,
                    title = "Action Movie",
                    posterPath = "/poster.jpg",
                    releaseDate = "2026-01-01",
                    voteAverage = 8.2
                )
            ),
            totalPages = 3
        )

        val source = MoviesPagingSource(api, genreId = 28)
        val result = source.load(refresh())

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page

        assertEquals(101, page.data.single().id)
        assertEquals("Action Movie", page.data.single().title)
        assertEquals(8.2, page.data.single().rating, 0.001)
        assertNull(page.prevKey)
        assertEquals(2, page.nextKey)

        coVerify(exactly = 1) {
            api.discoverMovies(genreId = 28, page = 1)
        }
    }

    @Test
    fun `append requests the supplied page`() = runTest {
        coEvery {
            api.discoverMovies(genreId = 35, page = 2)
        } returns MovieResponseDto(
            page = 2,
            results = listOf(MovieDto(202, "Comedy Movie")),
            totalPages = 3
        )

        val source = MoviesPagingSource(api, genreId = 35)

        val result = source.load(
            PagingSource.LoadParams.Append(
                key = 2,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(202, result.data.single().id)
        assertEquals(1, result.prevKey)
        assertEquals(3, result.nextKey)

        coVerify(exactly = 1) {
            api.discoverMovies(genreId = 35, page = 2)
        }
    }

    @Test
    fun `last page has no next key`() = runTest {
        coEvery {
            api.discoverMovies(genreId = 28, page = 3)
        } returns MovieResponseDto(
            page = 3,
            results = listOf(MovieDto(303, "Last Movie")),
            totalPages = 3
        )

        val result = MoviesPagingSource(api, 28)
            .load(refresh(3)) as PagingSource.LoadResult.Page

        assertNull(result.nextKey)
    }

    @Test
    fun `empty page stops pagination`() = runTest {
        coEvery {
            api.discoverMovies(genreId = 28, page = 1)
        } returns MovieResponseDto(
            page = 1,
            results = emptyList(),
            totalPages = 10
        )

        val result = MoviesPagingSource(api, 28)
            .load(refresh()) as PagingSource.LoadResult.Page

        assertTrue(result.data.isEmpty())
        assertNull(result.nextKey)
    }

    @Test
    fun `page 500 stops even if total pages is larger`() = runTest {
        coEvery {
            api.discoverMovies(genreId = 28, page = 500)
        } returns MovieResponseDto(
            page = 500,
            results = listOf(MovieDto(500, "Movie")),
            totalPages = 900
        )

        val result = MoviesPagingSource(api, 28)
            .load(refresh(500)) as PagingSource.LoadResult.Page

        assertNull(result.nextKey)
    }

    @Test
    fun `network failure returns load error`() = runTest {
        val failure = IOException("Offline")

        coEvery {
            api.discoverMovies(genreId = 28, page = 1)
        } throws failure

        val result = MoviesPagingSource(api, 28).load(refresh())

        assertTrue(result is PagingSource.LoadResult.Error)
        assertSame(
            failure,
            (result as PagingSource.LoadResult.Error).throwable
        )
    }
}