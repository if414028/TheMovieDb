package com.reynaldo.themoviedb

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.reynaldo.themoviedb.di.RepositoryModule
import com.reynaldo.themoviedb.domain.repository.MovieRepository
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(RepositoryModule::class)
@RunWith(AndroidJUnit4::class)
class AppNavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createEmptyComposeRule()

    private val fake = FakeUiMovieRepository()

    @BindValue
    @JvmField
    val repository: MovieRepository = fake

    private var scenario: ActivityScenario<MainActivity>? = null

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        scenario?.close()
    }

    private fun launchApp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    private fun waitForText(text: String) {
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule
                .onAllNodesWithText(text)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    private fun openMovieDetail() {
        waitForText("Action")
        composeRule.onNodeWithText("Action").performClick()

        waitForText("Assessment Movie")
        composeRule
            .onNodeWithText("Assessment Movie")
            .performClick()

        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule
                .onAllNodesWithTag("movie_detail_list")
                .fetchSemanticsNodes()
                .size == 1
        }
    }

    @Test
    fun genreToMovieToDetailAndBack() {
        launchApp()
        openMovieDetail()

        composeRule
            .onNodeWithText("Movie Detail")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Assessment Movie")
            .assertIsDisplayed()

        assertEquals(28, fake.requestedGenreId)
        assertEquals(101, fake.requestedDetailId)

        composeRule.onNodeWithText("Back").performClick()

        waitForText("Action")

        composeRule
            .onNodeWithText("Assessment Movie")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Movie Detail")
            .assertDoesNotExist()
    }

    @Test
    fun genreEmptyStateIsDisplayed() {
        fake.genres = emptyList()
        launchApp()

        waitForText("Belum ada genre tersedia.")

        composeRule
            .onNodeWithText("Belum ada genre tersedia.")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Retry")
            .assertDoesNotExist()
    }

    @Test
    fun genreRetryRecoversFromError() {
        fake.failFirstGenreRequest = true
        launchApp()

        waitForText("Retry")

        composeRule
            .onNodeWithText(
                "Tidak dapat terhubung. Periksa koneksi internet."
            )
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Retry")
            .performClick()

        waitForText("Action")

        composeRule
            .onNodeWithText("Action")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Retry")
            .assertDoesNotExist()

        assertEquals(2, fake.genreCalls.get())
    }

    @Test
    fun reviewsNavigationDisplaysEmptyStateForSelectedMovie() {
        launchApp()
        openMovieDetail()

        composeRule
            .onNodeWithTag("movie_detail_list")
            .performScrollToNode(hasText("User Reviews"))

        composeRule
            .onNodeWithText("User Reviews")
            .assertIsDisplayed()
            .performClick()

        waitForText("Belum ada review untuk film ini.")

        composeRule
            .onNodeWithText("Belum ada review untuk film ini.")
            .assertIsDisplayed()

        assertEquals(101, fake.requestedReviewsId)
    }

    @Test
    fun trailerNavigationDisplaysEmptyStateForSelectedMovie() {
        launchApp()
        openMovieDetail()

        composeRule
            .onNodeWithTag("movie_detail_list")
            .performScrollToNode(hasText("Watch Trailer"))

        composeRule
            .onNodeWithText("Watch Trailer")
            .assertIsDisplayed()
            .performClick()

        waitForText("Trailer YouTube belum tersedia.")

        composeRule
            .onNodeWithText("Trailer YouTube belum tersedia.")
            .assertIsDisplayed()

        assertEquals(101, fake.requestedTrailerId)
    }
}