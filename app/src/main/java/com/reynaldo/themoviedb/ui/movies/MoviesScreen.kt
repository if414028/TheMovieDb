package com.reynaldo.themoviedb.ui.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.reynaldo.themoviedb.domain.model.Movie
import com.reynaldo.themoviedb.ui.components.ErrorMessage
import com.reynaldo.themoviedb.ui.components.toUserMessage

@Composable
fun MoviesRouteScreen(
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit,
    viewModel: MoviesViewModel = hiltViewModel()
) {
    val movies = viewModel.movies.collectAsLazyPagingItems()

    MoviesScreen(
        genreName = viewModel.genreName,
        movies = movies,
        onBack = onBack,
        onMovieClick = onMovieClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    genreName: String,
    movies: LazyPagingItems<Movie>,
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit
) {
    val gridState = rememberLazyGridState()
    val refreshState = movies.loadState.refresh

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(genreName) },
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
            when {
                refreshState is LoadState.Loading &&
                        movies.itemCount == 0 -> {
                    CircularProgressIndicator()
                }

                refreshState is LoadState.Error &&
                        movies.itemCount == 0 -> {
                    ErrorMessage(
                        message = refreshState.error.toUserMessage(),
                        onRetry = { movies.retry() }
                    )
                }

                refreshState is LoadState.NotLoading &&
                        movies.itemCount == 0 -> {
                    Text("Belum ada film untuk genre ini.")
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = gridState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (refreshState is LoadState.Error) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                ErrorMessage(
                                    message = refreshState.error.toUserMessage(),
                                    onRetry = { movies.retry() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        items(count = movies.itemCount) { index ->
                            val movie = movies[index]

                            if (movie != null) {
                                MovieCard(
                                    movie = movie,
                                    onClick = { onMovieClick(movie.id) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        when (val appendState = movies.loadState.append) {
                            is LoadState.Loading -> {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            is LoadState.Error -> {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    ErrorMessage(
                                        message = appendState.error.toUserMessage(),
                                        onRetry = { movies.retry() },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            is LoadState.NotLoading -> Unit
                        }
                    }
                }
            }
        }
    }
}