package com.reynaldo.themoviedb.ui.movies

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.reynaldo.themoviedb.domain.model.Movie
import java.util.Locale

@Composable
fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val posterUrl = movie.posterPath
        ?.takeIf { it.isNotBlank() }
        ?.let { "https://image.tmdb.org/t/p/w500$it" }

    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        SubcomposeAsyncImage(
            model = posterUrl,
            contentDescription = "Poster ${movie.title}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f),
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            },
            error = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Poster tidak tersedia")
                }
            }
        )

        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(
                start = 12.dp,
                end = 12.dp,
                top = 12.dp
            )
        )

        val year = movie.releaseDate
            ?.takeIf { it.length >= 4 }
            ?.take(4)
            ?: "—"

        Text(
            text = "$year • ${
                String.format(Locale.US, "%.1f", movie.rating)
            }/10",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(12.dp)
        )
    }
}