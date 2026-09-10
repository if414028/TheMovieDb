package com.reynaldo.themoviedb

import androidx.paging.PagingSource
import com.reynaldo.themoviedb.data.paging.ReviewsPagingSource
import com.reynaldo.themoviedb.data.remote.TmdbApiService
import com.reynaldo.themoviedb.data.remote.dto.ReviewDto
import com.reynaldo.themoviedb.data.remote.dto.ReviewResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class ReviewsPagingSourceTest {

    private val api = mockk<TmdbApiService>()

    private fun refresh() =
        PagingSource.LoadParams.Refresh<Int>(
            key = null,
            loadSize = 20,
            placeholdersEnabled = false
        )

    @Test
    fun `first page maps review without optional rating`() = runTest {
        coEvery {
            api.getMovieReviews(movieId = 101, page = 1)
        } returns ReviewResponseDto(
            page = 1,
            results = listOf(
                ReviewDto(
                    id = "review-1",
                    author = "Rey",
                    content = "Great movie",
                    authorDetails = null
                )
            ),
            totalPages = 2
        )

        val result = ReviewsPagingSource(api, 101)
            .load(refresh()) as PagingSource.LoadResult.Page

        val review = result.data.single()

        assertEquals("review-1", review.id)
        assertEquals("Rey", review.author)
        assertEquals("Great movie", review.content)
        assertNull(review.rating)
        assertNull(result.prevKey)
        assertEquals(2, result.nextKey)

        coVerify(exactly = 1) {
            api.getMovieReviews(movieId = 101, page = 1)
        }
    }

    @Test
    fun `no reviews returns empty page without next key`() = runTest {
        coEvery {
            api.getMovieReviews(movieId = 101, page = 1)
        } returns ReviewResponseDto(
            page = 1,
            results = emptyList(),
            totalPages = 0
        )

        val result = ReviewsPagingSource(api, 101)
            .load(refresh()) as PagingSource.LoadResult.Page

        assertTrue(result.data.isEmpty())
        assertNull(result.nextKey)
    }

    @Test
    fun `failed append can load the same page successfully afterward`() = runTest {
        val source = ReviewsPagingSource(api, 101)

        val params = PagingSource.LoadParams.Append(
            key = 2,
            loadSize = 20,
            placeholdersEnabled = false
        )

        coEvery {
            api.getMovieReviews(movieId = 101, page = 2)
        } throws IOException("Connection lost")

        val failed = source.load(params)
        assertTrue(failed is PagingSource.LoadResult.Error)

        coEvery {
            api.getMovieReviews(movieId = 101, page = 2)
        } returns ReviewResponseDto(
            page = 2,
            results = listOf(
                ReviewDto(
                    id = "review-2",
                    author = "Herlina",
                    content = "Enjoyed it"
                )
            ),
            totalPages = 2
        )

        val recovered = source.load(params) as PagingSource.LoadResult.Page

        assertEquals("review-2", recovered.data.single().id)
        assertEquals(1, recovered.prevKey)
        assertNull(recovered.nextKey)

        coVerify(exactly = 2) {
            api.getMovieReviews(movieId = 101, page = 2)
        }
    }
}