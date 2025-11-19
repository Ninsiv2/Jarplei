package com.juan.carplaylauncher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juan.carplaylauncher.spotify.SpotifyApi
import com.juan.carplaylauncher.spotify.SpotifyAuth
import com.juan.carplaylauncher.spotify.SpotifyTrack
import kotlinx.coroutines.delay

@Composable
fun MiniMusicPlayer(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    var loggedIn by remember { mutableStateOf(false) }
    var track by remember { mutableStateOf<SpotifyTrack?>(null) }

    LaunchedEffect(Unit) {
        // Cargar tokens guardados (si existen)
        SpotifyAuth.loadTokens(context)
        loggedIn = SpotifyAuth.accessToken != null

        if (loggedIn) {
            while (true) {
                var currentTrack = SpotifyApi.getCurrentlyPlaying()

                // Si el token está vencido, accessToken se pone a null en SpotifyApi
                if (currentTrack == null &&
                    SpotifyAuth.accessToken == null &&
                    SpotifyAuth.refreshToken != null
                ) {
                    // Intentar refrescar el token una vez
                    val refreshed = SpotifyAuth.refreshAccessToken(context)
                    if (refreshed) {
                        loggedIn = true
                        currentTrack = SpotifyApi.getCurrentlyPlaying()
                    } else {
                        loggedIn = false
                    }
                }

                track = currentTrack
                delay(2000)
            }
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.6f),
                        Color.Black.copy(alpha = 0.85f)
                    )
                )
            )
            .padding(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Reproductor", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)

            when {
                !loggedIn -> {
                    Button(
                        onClick = { SpotifyAuth.startLogin(context) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1DB954)
                        )
                    ) {
                        Text("Conectar Spotify", color = Color.White)
                    }
                }

                loggedIn && track == null -> {
                    Text(
                        "Abre Spotify en tu iPhone y reproduce música",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                track != null -> {
                    Text(
                        track!!.title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        track!!.artist,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 16.sp
                    )
                    Text(
                        track!!.album,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
