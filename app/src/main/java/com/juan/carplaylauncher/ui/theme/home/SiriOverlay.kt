package com.juan.carplaylauncher.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SiriOverlay(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onOpenMaps: () -> Unit,
    onOpenMusic: () -> Unit,
    onOpenSettings: () -> Unit = {}
) {
    // Animación del “orbe” de Siri
    val infinite = rememberInfiniteTransition(label = "SiriWave")
    val scale by infinite.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SiriScale"
    )
    val alpha by infinite.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SiriAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xB0000000))
            // Tap fuera = cerrar Siri
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {

        // Tarjeta central de Siri
        Column(
            modifier = Modifier
                .padding(horizontal = 48.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF05060A).copy(alpha = 0.96f))
                .padding(horizontal = 24.dp, vertical = 22.dp)
                .clickable(enabled = false) { }, // para que el click solo lo manejen los hijos
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Orbe animado de Siri
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF4F5BFE),
                                Color(0xFF8A2BE2),
                                Color(0xFF00E5FF)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp * scale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color.White.copy(alpha = alpha),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF4F5BFE),
                                    Color(0xFF8A2BE2),
                                    Color(0xFF00E5FF)
                                )
                            )
                        )
                )
            }

            Text(
                text = "¿En qué puedo ayudarte?",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Mantén pulsado el botón Home para hablar con Siri.\n" +
                        "Prueba con: “Abrir Mapas”, “Reproducir música” o “Abrir configuración”.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Sugerencias rápidas (acciones reales)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SiriSuggestionChip(text = "Abrir Mapas") {
                    onOpenMaps()
                    onClose()
                }
                SiriSuggestionChip(text = "Reproducir música") {
                    onOpenMusic()
                    onClose()
                }
                SiriSuggestionChip(text = "Configuración") {
                    onOpenSettings()
                    onClose()
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Toque fuera para cerrar",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun SiriSuggestionChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}
