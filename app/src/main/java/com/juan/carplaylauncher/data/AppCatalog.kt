package com.juan.carplaylauncher.data

import com.juan.carplaylauncher.R
import com.juan.carplaylauncher.model.CarPlayApp

object AppCatalog {

    val defaultApps: List<CarPlayApp> = listOf(
        CarPlayApp("Teléfono", R.drawable.ic_app_phone, "com.android.dialer"),
        CarPlayApp("Música", R.drawable.ic_app_music, "com.spotify.music"),
        CarPlayApp("Mapas", R.drawable.ic_app_maps, "com.google.android.apps.maps"),
        CarPlayApp("Mensajes", R.drawable.ic_app_messages, "com.google.android.apps.messaging"),
        CarPlayApp("Spotify", R.drawable.ic_app_spotify, "com.spotify.music"),
        CarPlayApp("Podcasts", R.drawable.ic_app_podcasts, "com.google.android.apps.podcasts"),
        CarPlayApp("Audiolibros", R.drawable.ic_app_audiobooks),
        CarPlayApp("Clima", R.drawable.ic_app_weather),
        CarPlayApp("Calendario", R.drawable.ic_app_calendar, "com.google.android.calendar"),
        CarPlayApp("Configuración", R.drawable.ic_app_settings, "com.android.settings"),
        CarPlayApp("Waze", R.drawable.ic_app_waze, "com.waze"),
        CarPlayApp("YT Music", R.drawable.ic_app_ytmusic, "com.google.android.apps.youtube.music"),
    )
}


