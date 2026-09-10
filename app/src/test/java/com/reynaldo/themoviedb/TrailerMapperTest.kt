package com.reynaldo.themoviedb

import com.reynaldo.themoviedb.data.mapper.selectYoutubeTrailer
import com.reynaldo.themoviedb.data.remote.dto.VideoDto
import org.junit.Assert.*
import org.junit.Test

class TrailerMapperTest {

    private fun video(
        key: String = "abcdefghijk",
        official: Boolean = false,
        site: String = "YouTube",
        type: String = "Trailer"
    ) = VideoDto(
        key = key,
        name = "Trailer",
        site = site,
        type = type,
        official = official
    )

    @Test
    fun `official trailer is selected even when listed second`() {
        val result = listOf(
            video(key = "abcdefghijk"),
            video(key = "lmnopqrstuv", official = true)
        ).selectYoutubeTrailer()

        assertEquals("lmnopqrstuv", result?.videoId)
    }

    @Test
    fun `non official trailer is used when official is unavailable`() {
        val result = listOf(
            video(key = "abcdefghijk")
        ).selectYoutubeTrailer()

        assertEquals("abcdefghijk", result?.videoId)
    }

    @Test
    fun `non YouTube video is ignored`() {
        val result = listOf(
            video(site = "Vimeo", official = true)
        ).selectYoutubeTrailer()

        assertNull(result)
    }

    @Test
    fun `teaser is not selected as trailer`() {
        val result = listOf(
            video(type = "Teaser", official = true)
        ).selectYoutubeTrailer()

        assertNull(result)
    }

    @Test
    fun `invalid video id is ignored`() {
        val result = listOf(
            video(key = "invalid", official = true)
        ).selectYoutubeTrailer()

        assertNull(result)
    }

    @Test
    fun `empty response returns no trailer`() {
        assertNull(emptyList<VideoDto>().selectYoutubeTrailer())
    }
}