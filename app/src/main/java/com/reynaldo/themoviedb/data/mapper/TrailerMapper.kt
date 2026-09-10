package com.reynaldo.themoviedb.data.mapper

import com.reynaldo.themoviedb.data.remote.dto.VideoDto
import com.reynaldo.themoviedb.domain.model.Trailer

fun List<VideoDto>.selectYoutubeTrailer(): Trailer? {
    val candidates = filter {
        it.site.equals("YouTube", ignoreCase = true) &&
                it.type.equals("Trailer", ignoreCase = true) &&
                it.key.matches(Regex("[A-Za-z0-9_-]{11}"))
    }

    val selected = candidates.firstOrNull { it.official }
        ?: candidates.firstOrNull()
        ?: return null

    return Trailer(
        videoId = selected.key,
        name = selected.name
    )
}