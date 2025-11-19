package com.juan.carplaylauncher

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager

@SuppressLint("SetJavaScriptEnabled", "MissingPermission")
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    // ⭐ LISTENER GPS estable
    DisposableEffect(Unit) {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val hasFine = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarse = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            // Sin permisos: no registramos listener, pero devolvemos igualmente onDispose
            return@DisposableEffect onDispose { }
        }

        val listener = object : LocationListener {
            override fun onLocationChanged(loc: Location) {
                // Enviamos SOLO lat/lon para evitar giros locos
                val js = "updatePosition(${loc.latitude}, ${loc.longitude})"
                webViewRef?.post { webViewRef?.evaluateJavascript(js, null) }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        try {
            // Solo GPS, cada 1s y mínimo 3m para reducir ruido
            lm.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L,
                3f,
                listener
            )
        } catch (_: SecurityException) { }

        // Esto es lo que devuelve DisposableEffect
        onDispose {
            lm.removeUpdates(listener)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true

                    webViewClient = WebViewClient()

                    loadUrl("file:///android_asset/map.html")

                    webViewRef = this
                }
            }
        )

        if (showBackButton) {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp)
                    .height(40.dp)
            ) {
                Text("Volver")
            }
        }
    }
}
