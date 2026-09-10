package com.reynaldo.themoviedb.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.reynaldo.themoviedb.domain.model.MovieDetail
import com.reynaldo.themoviedb.ui.components.ErrorMessage
import java.util.Locale

@Composable
fun MovieDetailRouteScreen(
    onBack: () -> Unit,
    onReviewsClick: (Int) -> Unit,
    onTrailerClick: (Int) -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MovieDetailScreen(
        state = state,
        onBack = onBack,
        onRetry = viewModel::loadDetail,
        onReviewsClick = onReviewsClick,
        onTrailerClick = onTrailerClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    state: MovieDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onReviewsClick: (Int) -> Unit,
    onTrailerClick: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Detail") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                MovieDetailUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is MovieDetailUiState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = onRetry
                    )
                }

                is MovieDetailUiState.Success -> {
                    MovieDetailContent(
                        movie = state.movie,
                        onReviewsClick = onReviewsClick,
                        onTrailerClick = onTrailerClick
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieDetailContent(
    movie: MovieDetail,
    onReviewsClick: (Int) -> Unit,
    onTrailerClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            MovieDetailImage(
                path = movie.backdropPath,
                description = "Backdrop ${movie.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MovieDetailImage(
                    path = movie.posterPath,
                    description = "Poster ${movie.title}",
                    modifier = Modifier
                        .width(140.dp)
                        .aspectRatio(2f / 3f)
                        .clip(MaterialTheme.shapes.medium)
                )

                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineMedium
                )

                movie.tagline
                    ?.takeIf { it.isNotBlank() }
                    ?.let { tagline ->
                        Text(
                            text = tagline,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                val ratingText = if (movie.voteCount > 0) {
                    "${
                        String.format(Locale.US, "%.1f", movie.rating)
                    }/10 • ${movie.voteCount} penilaian"
                } else {
                    "Belum ada penilaian"
                }

                Text(
                    text = ratingText,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Tanggal rilis: ${
                        movie.releaseDate
                            ?.takeIf { it.isNotBlank() }
                            ?: "Belum tersedia"
                    }"
                )

                Text(
                    text = "Durasi: ${
                        movie.runtimeMinutes.toDurationLabel()
                    }"
                )

                Text(
                    text = "Genre: ${
                        movie.genres
                            .joinToString { it.name }
                            .ifBlank { "Belum tersedia" }
                    }"
                )
            }
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Sinopsis",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = movie.overview
                        ?.takeIf { it.isNotBlank() }
                        ?: "Sinopsis belum tersedia.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onTrailerClick(movie.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Watch Trailer")
                }

                OutlinedButton(
                    onClick = { onReviewsClick(movie.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("User Reviews")
                }
            }
        }
    }
}

@Composable
private fun MovieDetailImage(
    path: String?,
    description: String,
    modifier: Modifier = Modifier
) {
    val imageUrl = path
        ?.takeIf { it.isNotBlank() }
        ?.let { "https://image.tmdb.org/t/p/w500$it" }

    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = description,
        contentScale = ContentScale.Crop,
        modifier = modifier,
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
                Text("Gambar tidak tersedia")
            }
        }
    )
}

private fun Int?.toDurationLabel(): String {
    val minutes = this?.takeIf { it > 0 }
        ?: return "Belum tersedia"

    val hours = minutes / 60
    val remainingMinutes = minutes % 60

    return when {
        hours == 0 -> "$remainingMinutes menit"
        remainingMinutes == 0 -> "$hours jam"
        else -> "$hours jam $remainingMinutes menit"
    }
}