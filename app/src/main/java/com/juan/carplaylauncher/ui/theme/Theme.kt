package com.juan.carplaylauncher.ui.ui

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 🔥 IMPORTAMOS TUS NUEVOS COLORES

// ==== MODO OSCURO EXCLUSIVO (CarPlay UI by Juan) ====
private val CarPlayDarkColors = darkColorScheme(
    primary = CarPlayAccentGreen,
    secondary = CarPlayAccentBlue,
    background = CarPlayBackground,
    surface = CarPlaySurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = CarPlayTextPrimary,
    onSurface = CarPlayTextPrimary,
    error = CarPlayError
)

// ==== MODO CLARO (opcionales, pero aquí mantenemos estilo oscuro bonito) ====
private val CarPlayLightColors = lightColorScheme(
    primary = CarPlayAccentGreen,
    secondary = CarPlayAccentBlue,
    background = Color(0xFFF2F2F7),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    error = CarPlayError
)

@Composable
fun CarplayLauncherTheme(
    darkTheme: Boolean = true,     // 🔥 siempre modo oscuro (igual que CarPlay)
    dynamicColor: Boolean = false, // 🔥 desactivado para evitar tonos raros
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (darkTheme) CarPlayDarkColors else CarPlayLightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        window.statusBarColor = Color.Transparent.value.toInt()
        window.navigationBarColor = Color.Transparent.value.toInt()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
