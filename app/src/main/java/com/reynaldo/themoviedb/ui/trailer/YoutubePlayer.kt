package com.reynaldo.themoviedb.ui.trailer

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YoutubePlayer(
    videoId: String,
    reloadKey: Int,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateAsState()

    if (!lifecycleState.isAtLeast(Lifecycle.State.RESUMED)) {
        return
    }

    key(videoId, reloadKey) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = true
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false

                    webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()

                    val baseUrl = "https://${context.packageName}/"

                    val html = """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta name="viewport"
                                  content="width=device-width, initial-scale=1">
                            <meta name="referrer"
                                  content="strict-origin-when-cross-origin">
                            <style>
                                html, body {
                                    margin: 0;
                                    padding: 0;
                                    width: 100%;
                                    background: #ffffff;
                                }
                    
                                #player {
                                    display: block;
                                    width: 100%;
                                    height: 240px;
                                    border: 0;
                                }
                    
                                #status {
                                    margin: 0;
                                    padding: 8px;
                                    font: 12px sans-serif;
                                    color: #333333;
                                }
                                
                                #status {
                                    display: none !important;
                                }
                            </style>
                        </head>
                        <body>
                            <div id="player"></div>
                            <p id="status" hidden></p>
                            <p id="status">Memuat YouTube player...</p>
                    
                            <script>
                                var player;
                                var ready = false;
                    
                                function showStatus(message) {
                                    var statusElement = document.getElementById("status");
                                
                                    if (statusElement) {
                                        statusElement.textContent = message;
                                    }
                                
                                    console.log("[TrailerPlayer] " + message);
                                }
                    
                                function resizePlayer() {
                                    if (player && typeof player.setSize === "function") {
                                        var width = document.documentElement.clientWidth;
                                        player.setSize(width, 240);
                                    }
                                }
                    
                                window.onYouTubeIframeAPIReady = function() {
                                    showStatus("API siap. Menyiapkan video...");
                    
                                    player = new YT.Player("player", {
                                        width: document.documentElement.clientWidth,
                                        height: 240,
                                        videoId: "$videoId",
                                        playerVars: {
                                            playsinline: 1,
                                            autoplay: 0,
                                            controls: 1,
                                            fs: 0,
                                            origin: "https://${context.packageName}"
                                        },
                                        events: {
                                            onReady: function(event) {
                                                ready = true;
                                                resizePlayer();
                                                showStatus("Player siap. Tekan Play.");
                                            },
                                            onStateChange: function(event) {
                                                if (event.data === 1) {
                                                    showStatus("Video sedang diputar.");
                                                } else if (event.data === 2) {
                                                    showStatus("Video dijeda.");
                                                }
                                            },
                                            onError: function(event) {
                                                showStatus(
                                                    "YouTube error: " + event.data +
                                                    ". Gunakan tombol Buka di YouTube."
                                                );
                                            }
                                        }
                                    });
                                };
                    
                                window.addEventListener("resize", resizePlayer);
                    
                                var script = document.createElement("script");
                                script.src = "https://www.youtube.com/iframe_api";
                                script.onerror = function() {
                                    showStatus("Script YouTube gagal dimuat.");
                                };
                                document.head.appendChild(script);
                    
                                setTimeout(function() {
                                    if (!ready) {
                                        showStatus(
                                            "Player belum siap setelah 15 detik. " +
                                            "Coba Reload player."
                                        );
                                    }
                                }, 15000);
                            </script>
                        </body>
                        </html>
                    """.trimIndent()

                    loadDataWithBaseURL(
                        baseUrl,
                        html,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            },
            onRelease = { webView ->
                webView.stopLoading()
                webView.loadUrl("about:blank")
                webView.onPause()
                webView.removeAllViews()
                webView.destroy()
            }
        )
    }
}