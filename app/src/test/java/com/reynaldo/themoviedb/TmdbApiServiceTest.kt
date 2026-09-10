package com.reynaldo.themoviedb

import com.reynaldo.themoviedb.data.remote.TmdbApiService
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class TmdbApiServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var api: TmdbApiService

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        val json = Json {
            ignoreUnknownKeys = true
        }

        api = Retrofit.Builder()
            .baseUrl(server.url("/3/"))
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()
            .create(TmdbApiService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    private fun enqueueJson(body: String, status: Int = 200) {
        server.enqueue(
            MockResponse()
                .setResponseCode(status)
                .setHeader("Content-Type", "application/json")
                .setBody(body)
        )
    }

    @Test
    fun `detail response parses nullable fields and ignores extra fields`() = runTest {
        enqueueJson(
            """
            {
              "id": 101,
              "title": "Test Movie",
              "overview": null,
              "runtime": null,
              "poster_path": null,
              "backdrop_path": null,
              "release_date": "",
              "vote_average": 8.5,
              "vote_count": 100,
              "genres": [{"id": 28, "name": "Action"}],
              "unexpected_field": "ignored"
            }
            """.trimIndent()
        )

        val result = api.getMovieDetail(movieId = 101)

        assertEquals(101, result.id)
        assertEquals("Test Movie", result.title)
        assertNull(result.overview)
        assertNull(result.runtime)
        assertNull(result.posterPath)
        assertEquals(8.5, result.voteAverage, 0.001)
        assertEquals("Action", result.genres.single().name)

        val request = requireNotNull(
            server.takeRequest(2, TimeUnit.SECONDS)
        )

        assertEquals("GET", request.method)
        assertEquals("/3/movie/101", request.requestUrl?.encodedPath)
        assertEquals(
            "en-US",
            request.requestUrl?.queryParameter("language")
        )
    }

    @Test
    fun `discover sends genre and page query parameters`() = runTest {
        enqueueJson(
            """
            {
              "page": 2,
              "total_pages": 5,
              "results": [
                {
                  "id": 202,
                  "title": "Next Movie",
                  "poster_path": null,
                  "vote_average": 7.2
                }
              ]
            }
            """.trimIndent()
        )

        val result = api.discoverMovies(
            genreId = 35,
            page = 2
        )

        assertEquals(2, result.page)
        assertEquals(5, result.totalPages)
        assertEquals(202, result.results.single().id)

        val request = requireNotNull(
            server.takeRequest(2, TimeUnit.SECONDS)
        )

        assertEquals("/3/discover/movie", request.requestUrl?.encodedPath)
        assertEquals("35", request.requestUrl?.queryParameter("with_genres"))
        assertEquals("2", request.requestUrl?.queryParameter("page"))
        assertEquals("false", request.requestUrl?.queryParameter("include_adult"))
    }

    @Test
    fun `reviews response parses null reviewer rating`() = runTest {
        enqueueJson(
            """
            {
              "page": 1,
              "total_pages": 1,
              "results": [
                {
                  "id": "review-1",
                  "author": "Rey",
                  "content": "Great movie",
                  "created_at": "2026-01-01T10:00:00Z",
                  "author_details": {
                    "rating": null
                  }
                }
              ]
            }
            """.trimIndent()
        )

        val result = api.getMovieReviews(
            movieId = 101,
            page = 1
        )

        val review = result.results.single()

        assertEquals("review-1", review.id)
        assertEquals("Great movie", review.content)
        assertNull(review.authorDetails?.rating)
    }

    @Test
    fun `401 response throws HttpException`() = runTest {
        enqueueJson(
            body = """{"status_message":"Invalid API key"}""",
            status = 401
        )

        try {
            api.getMovieDetail(101)
            fail("Expected HttpException")
        } catch (exception: HttpException) {
            assertEquals(401, exception.code())
        }
    }

    @Test
    fun `invalid response shape throws SerializationException`() = runTest {
        enqueueJson("""{"id":101}""")

        try {
            api.getMovieDetail(101)
            fail("Expected SerializationException")
        } catch (exception: SerializationException) {
            assertNotNull(exception.message)
        }
    }
}