package com.reynaldo.themoviedb

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.reynaldo.themoviedb.ui.navigation.AppNavHost
import com.reynaldo.themoviedb.ui.theme.TheMovieDbTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        setContent {
            TheMovieDbTheme {
                AppNavHost()
            }
        }
    }
}