package com.reynaldo.themoviedb.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.reynaldo.themoviedb.data.mapper.toDomain
import com.reynaldo.themoviedb.data.remote.TmdbApiService
import com.reynaldo.themoviedb.domain.model.Movie
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

class MoviesPagingSource(
    private val api: TmdbApiService,
    private val genreId: Int
) : PagingSource<Int, Movie>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Movie> {
        val page = params.key ?: 1

        return try {
            val response = api.discoverMovies(
                genreId = genreId,
                page = page
            )

            val lastPage = minOf(response.totalPages, 500)

            LoadResult.Page(
                data = response.results.map { it.toDomain() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (
                    response.results.isEmpty() || page >= lastPage
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
        state: PagingState<Int, Movie>
    ): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val closestPage = state.closestPageToPosition(anchorPosition)

        return closestPage?.prevKey?.plus(1)
            ?: closestPage?.nextKey?.minus(1)
    }
}