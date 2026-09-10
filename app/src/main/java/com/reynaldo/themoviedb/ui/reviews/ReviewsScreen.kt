package com.reynaldo.themoviedb.ui.reviews

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.reynaldo.themoviedb.domain.model.Review
import com.reynaldo.themoviedb.ui.components.ErrorMessage
import com.reynaldo.themoviedb.ui.components.toUserMessage
import java.util.Locale

@Composable
fun ReviewsRouteScreen(
    onBack: () -> Unit,
    viewModel: ReviewsViewModel = hiltViewModel()
) {
    val reviews = viewModel.reviews.collectAsLazyPagingItems()

    ReviewsScreen(
        reviews = reviews,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    reviews: LazyPagingItems<Review>,
    onBack: () -> Unit
) {
    val listState = rememberLazyListState()
    val refresh = reviews.loadState.refresh

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Reviews") },
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
                refresh is LoadState.Loading &&
                        reviews.itemCount == 0 -> {
                    CircularProgressIndicator()
                }

                refresh is LoadState.Error &&
                        reviews.itemCount == 0 -> {
                    ErrorMessage(
                        message = refresh.error.toUserMessage(),
                        onRetry = { reviews.retry() }
                    )
                }

                refresh is LoadState.NotLoading &&
                        reviews.itemCount == 0 -> {
                    Text("Belum ada review untuk film ini.")
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (refresh is LoadState.Error) {
                            item {
                                ErrorMessage(
                                    message = refresh.error.toUserMessage(),
                                    onRetry = { reviews.retry() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        items(
                            count = reviews.itemCount,
                            key = reviews.itemKey { it.id }
                        ) { index ->
                            reviews[index]?.let { review ->
                                ReviewCard(review)
                            }
                        }

                        when (val append = reviews.loadState.append) {
                            is LoadState.Loading -> {
                                item {
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
                                item {
                                    ErrorMessage(
                                        message = append.error.toUserMessage(),
                                        onRetry = { reviews.retry() },
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

@Composable
private fun ReviewCard(review: Review) {
    var expanded by rememberSaveable(review.id) {
        mutableStateOf(false)
    }

    var canExpand by remember(review.id) {
        mutableStateOf(false)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = review.author,
                style = MaterialTheme.typography.titleMedium
            )

            review.rating?.let { rating ->
                Text(
                    text = "${
                        String.format(Locale.US, "%.1f", rating)
                    }/10",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            review.createdAt
                ?.takeIf { it.isNotBlank() }
                ?.let { date ->
                    Text(
                        text = date.take(10),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            Text(
                text = review.content.ifBlank {
                    "Review tidak memiliki isi."
                },
                maxLines = if (expanded) Int.MAX_VALUE else 5,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { result ->
                    if (!expanded) {
                        canExpand = result.hasVisualOverflow
                    }
                }
            )

            if (canExpand || expanded) {
                TextButton(onClick = { expanded = !expanded }) {
                    Text(
                        if (expanded) "Show less" else "Read more"
                    )
                }
            }
        }
    }
}