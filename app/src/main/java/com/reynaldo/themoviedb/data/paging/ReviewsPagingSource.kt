package com.reynaldo.themoviedb.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.reynaldo.themoviedb.data.mapper.toDomain
import com.reynaldo.themoviedb.data.remote.TmdbApiService
import com.reynaldo.themoviedb.domain.model.Review
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

class ReviewsPagingSource(
    private val api: TmdbApiService,
    private val movieId: Int
) : PagingSource<Int, Review>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Review> {
        val page = params.key ?: 1

        return try {
            val response = api.getMovieReviews(
                movieId = movieId,
                page = page
            )

            LoadResult.Page(
                data = response.results.map { it.toDomain() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (
                    response.results.isEmpty() ||
                    page >= minOf(response.totalPages, 500)
                ) {
                    null
                } else {
                    page + 1
                }
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } catch (exception: SerializationException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(
        state: PagingState<Int, Review>
    ): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor)

        return page?.prevKey?.plus(1)
            ?: page?.nextKey?.minus(1)
    }
}