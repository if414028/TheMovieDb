package com.reynaldo.themoviedb.ui.trailer

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reynaldo.themoviedb.ui.components.ErrorMessage

@Composable
fun TrailerRouteScreen(
    onBack: () -> Unit,
    viewModel: TrailerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    TrailerScreen(
        state = state,
        onBack = onBack,
        onRetry = viewModel::loadTrailer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailerScreen(
    state: TrailerUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("YouTube Trailer") },
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
                TrailerUiState.Loading -> {
                    CircularProgressIndicator()
                }

                TrailerUiState.Empty -> {
                    Text("Trailer YouTube belum tersedia.")
                }

                is TrailerUiState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = onRetry
                    )
                }

                is TrailerUiState.Success -> {
                    var reloadKey by remember(state.trailer.videoId) {
                        mutableIntStateOf(0)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = state.trailer.name,
                            style = MaterialTheme.typography.titleLarge
                        )

                        YoutubePlayer(
                            videoId = state.trailer.videoId,
                            reloadKey = reloadKey,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        )

                        Text(
                            "Jika video tidak dapat diputar, " +
                                    "coba muat ulang atau buka di YouTube."
                        )

                        OutlinedButton(
                            onClick = { reloadKey++ }
                        ) {
                            Text("Reload player")
                        }

                        Button(
                            onClick = {
                                val uri = Uri.parse(
                                    "https://www.youtube.com/watch?v=" +
                                            state.trailer.videoId
                                )

                                try {
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW, uri)
                                    )
                                } catch (
                                    exception: ActivityNotFoundException
                                ) {
                                    Toast.makeText(
                                        context,
                                        "Tidak ada aplikasi untuk membuka video.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        ) {
                            Text("Buka di YouTube")
                        }
                    }
                }
            }
        }
    }
}