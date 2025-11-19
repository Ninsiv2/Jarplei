package com.juan.carplaylauncher

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.juan.carplaylauncher.spotify.SpotifyAuth
import com.juan.carplaylauncher.ui.home.CarPlayHomeScreen
import com.juan.carplaylauncher.ui.theme.CarplayLauncherTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleSpotifyRedirect(intent)
        requestLocationPermissionIfNeeded()

        setContent {
            CarplayLauncherTheme {
                CarPlayHomeScreen()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            handleSpotifyRedirect(intent)
        }
    }

    private fun handleSpotifyRedirect(intent: Intent) {
        val data = intent.data ?: return

        if (data.scheme == "carplaylauncher" && data.host == "callback") {
            val code = data.getQueryParameter("code")
            if (!code.isNullOrEmpty()) {
                lifecycleScope.launch {
                    SpotifyAuth.handleRedirect(this@MainActivity, code)
                }
            }
        }
    }

    private fun requestLocationPermissionIfNeeded() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val needsRequest = permissions.any { perm ->
            ActivityCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED
        }

        if (needsRequest) {
            ActivityCompat.requestPermissions(this, permissions, 100)
        }
    }
}
