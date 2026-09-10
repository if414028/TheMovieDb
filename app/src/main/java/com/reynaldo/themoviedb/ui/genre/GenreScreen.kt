package com.reynaldo.themoviedb.ui.genre

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reynaldo.themoviedb.domain.model.Genre

@Composable
fun GenreRoute(
    onGenreClick: (Genre) -> Unit,
    viewModel: GenreViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    GenreScreen(
        state = state,
        onRetry = viewModel::loadGenres,
        onGenreClick = onGenreClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreScreen(
    state: GenreUiState,
    onRetry: () -> Unit,
    onGenreClick: (Genre) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Genres") }
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
                GenreUiState.Loading -> {
                    CircularProgressIndicator()
                }

                GenreUiState.Empty -> {
                    Text("Belum ada genre tersedia.")
                }

                is GenreUiState.Error -> {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = state.message,
                            textAlign = TextAlign.Center
                        )

                        Button(onClick = onRetry) {
                            Text("Retry")
                        }
                    }
                }

                is GenreUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.genres,
                            key = { it.id }
                        ) { genre ->
                            Card(
                                onClick = { onGenreClick(genre) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = genre.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}