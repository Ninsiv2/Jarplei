package com.juan.carplaylauncher.ui.theme.music

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juan.carplaylauncher.R

// =======================================================
// ESTADO GLOBAL DEL REPRODUCTOR
// =======================================================

data class MusicState(
    val connected: Boolean = false,
    val playing: Boolean = false,
    val title: String = "Conectar Spotify",
    val artist: String = "",
    val cover: Int? = null,
    val progress: Float = 0f
)

// =======================================================
// REPRODUCTOR ESTILO CARPLAY / APPLE MUSIC
// =======================================================

@Composable
fun MusicPlayerApple(
    state: MusicState,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onConnect: () -> Unit = {},
    onPlayPause: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrev: () -> Unit = {}
) {

    val coverSize by animateDpAsState(
        targetValue = if (expanded) 210.dp else 90.dp,
        animationSpec = tween(300)
    )

    val titleSize = if (expanded) 32.sp else 18.sp
    val artistSize = if (expanded) 20.sp else 14.sp

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.16f),
                        Color.White.copy(alpha = 0.06f)
                    )
                )
            )
            .shadow(22.dp, RoundedCornerShape(26.dp), clip = false)
            .padding(22.dp)
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = if (state.connected) "En reproducción" else "Spotify",
                fontSize = if (expanded) 22.sp else 16.sp,
                color = Color.White.copy(alpha = 0.85f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(coverSize)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.cover != null) {
                        Image(
                            painter = painterResource(state.cover),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("🎵", fontSize = if (expanded) 55.sp else 35.sp)
                    }
                }

                Column {
                    Text(state.title, color = Color.White, fontSize = titleSize)
                    Text(
                        state.artist,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = artistSize
                    )
                }
            }

            if (state.connected) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (expanded) 10.dp else 6.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(state.progress)
                            .background(Color.White)
                    )
                }
            }

            AnimatedVisibility(visible = state.connected) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ControlButton(icon = R.drawable.ic_prev, onClick = onPrev)
                    PlayPauseButtonApple(
                        playing = state.playing,
                        onClick = onPlayPause
                    )
                    ControlButton(icon = R.drawable.ic_next, onClick = onNext)
                }
            }

            AnimatedVisibility(visible = !state.connected) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFF1ED760))
                        .clickable { onConnect() }
                        .padding(horizontal = 26.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Conectar Spotify", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

// =======================================================
// BOTONES
// =======================================================

@Composable
fun ControlButton(icon: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
fun PlayPauseButtonApple(playing: Boolean, onClick: () -> Unit) {

    val size by animateDpAsState(
        targetValue = if (playing) 90.dp else 92.dp,
        animationSpec = tween(260, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(if (playing) "⏸" else "▶️", fontSize = 34.sp, color = Color.Black)
    }
}
