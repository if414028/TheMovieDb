package com.reynaldo.themoviedb.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.io.IOException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

fun Throwable.toUserMessage(): String = when (this) {
    is HttpException -> when (code()) {
        401 -> "Akses ditolak. Periksa konfigurasi token TMDB."
        429 -> "Terlalu banyak permintaan. Coba lagi sebentar."
        in 500..599 -> "Server sedang bermasalah. Silakan coba lagi."
        else -> "Data gagal dimuat. Silakan coba lagi."
    }

    is IOException ->
        "Tidak dapat terhubung. Periksa koneksi internet."

    is SerializationException ->
        "Data dari server tidak dapat dibaca."

    else ->
        "Terjadi kesalahan. Silakan coba lagi."
}

@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = message,
            textAlign = TextAlign.Center
        )

        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}