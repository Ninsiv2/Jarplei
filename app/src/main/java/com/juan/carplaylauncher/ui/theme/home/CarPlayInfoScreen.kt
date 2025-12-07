package com.juan.carplaylauncher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun CarPlayInfoScreen(onBackHome: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        InfoTitle("Llaves del coche")
        InfoTile("Desbloquea y arranca tu coche con tu teléfono. Compatible con modelos desde 2021. Funciona incluso 5 horas sin batería.")

        Spacer(Modifier.height(18.dp))

        InfoTitle("CarPlay")
        InfoTile("Indicaciones, llamadas, mensajes y música directamente en la pantalla del coche.")

        Spacer(Modifier.height(18.dp))

        InfoTitle("Mapas")
        InfoTile("Predicción de destinos, vistas 3D, puntos de interés y navegación rápida.")

        Spacer(Modifier.height(18.dp))

        InfoTitle("Teléfono")
        InfoTile("Llama, responde y escucha tu buzón sin soltar el volante.")

        Spacer(Modifier.height(18.dp))

        InfoTitle("Mensajes")
        InfoTile("Dicta mensajes y escucha respuestas sin mirar el teléfono.")

        Spacer(Modifier.height(18.dp))

        InfoTitle("Música")
        InfoTile("Toda tu música organizada. Dolby Atmos y Audio Espacial compatibles.")

        Spacer(Modifier.height(18.dp))

        InfoTitle("Calendario")
        InfoTile("Consulta eventos y obtén direcciones a tus reuniones.")

        Spacer(Modifier.height(18.dp))

        InfoTitle("CarPlay Ultra")
        InfoTile("Control del panel, clima, radio, widgets y diseño personalizable.")

        Spacer(Modifier.height(22.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.9f))
                .clickable { onBackHome() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Home", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun InfoTitle(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun InfoTile(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.18f),
                        Color.White.copy(alpha = 0.10f)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Text(text, color = Color.White.copy(alpha = 0.85f), fontSize = 16.sp)
    }
}
