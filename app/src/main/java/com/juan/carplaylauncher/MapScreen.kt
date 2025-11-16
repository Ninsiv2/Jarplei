package com.juan.carplaylauncher

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {

        // Botón de volver estilo CarPlay
        Button(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .height(45.dp)
        ) {
            Text("Volver")
        }

        // WebView con Google Maps
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            factory = { context ->
                WebView(context).apply {

                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.cacheMode = WebSettings.LOAD_DEFAULT

                    webViewClient = WebViewClient()

                    loadUrl(
                        "https://www.google.com/maps/@?api=1&map_action=map"
                    )
                }
            }
        )
    }
}

