package com.juan.carplaylauncher.data

import com.juan.carplaylauncher.R
import com.juan.carplaylauncher.model.CarPlayApp

object AppCatalog {

    val defaultApps: List<CarPlayApp> = listOf(

        // Teléfono
        CarPlayApp(
            name = "Teléfono",
            iconRes = R.drawable.ic_app_facetime,
            packageName = "com.android.dialer"
        ),

        // Música (usa Spotify como reproductor principal)
        CarPlayApp(
            name = "Música",
            iconRes = R.drawable.ic_app_music,
            packageName = "com.spotify.music"
        ),

        // Mapas (dejamos sólo Google Maps, como pediste)
        CarPlayApp(
            name = "Mapas",
            iconRes = R.drawable.ic_app_maps,
            packageName = "com.google.android.apps.maps"
        ),

        // Mensajes
        CarPlayApp(
            name = "Mensajes",
            iconRes = R.drawable.ic_app_mensaje,
            packageName = "com.google.android.apps.messaging"
        ),

        // Spotify directo
        CarPlayApp(
            name = "Spotify",
            iconRes = R.drawable.ic_app_spotify,
            packageName = "com.spotify.music"
        ),

        // Play Store (reemplaza Podcasts)
        CarPlayApp(
            name = "Play Store",
            iconRes = R.drawable.ic_app_appstore,
            packageName = "com.android.vending"
        ),

        // Car DVR (reemplaza Audiolibros)
        CarPlayApp(
            name = "Car DVR",
            iconRes = R.drawable.ic_app_camera,
            packageName = "com.syu.dvr"
        ),

        // Sound Effects / Ecualizador (reemplaza Clima)
        CarPlayApp(
            name = "Sound Effects",
            iconRes = R.drawable.ic_app_voice,
            packageName = "com.ts.soundeffect"
        ),

        // Calendario
        CarPlayApp(
            name = "Calendario",
            iconRes = R.drawable.ic_app_calendar,
            packageName = "com.google.android.calendar"
        ),

        // Configuración del coche / sistema
        CarPlayApp(
            name = "Configuración",
            iconRes = R.drawable.ic_app_settings,
            packageName = "com.android.settings"
        ),

        // Waze
        CarPlayApp(
            name = "Waze",
            iconRes = R.drawable.ic_app_waze,
            packageName = "com.waze"
        ),

        // YouTube Music
        CarPlayApp(
            name = "YT Music",
            iconRes = R.drawable.ic_app_ytmusic,
            packageName = "com.google.android.apps.youtube.music"
        ),
    )
}
