package com.juan.carplaylauncher.media

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.app.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MediaNotificationListener : NotificationListenerService() {

    companion object {
        // Estado global con lo que se está reproduciendo
        private val _nowPlaying = MutableStateFlow<NowPlaying?>(null)
        val nowPlaying: StateFlow<NowPlaying?> = _nowPlaying
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notification = sbn.notification

        // Solo nos interesan notificaciones de media (Spotify, YT Music, etc.)
        if (notification.category != Notification.CATEGORY_TRANSPORT) return

        val extras = notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE) ?: return
        val artist = extras.getString(Notification.EXTRA_TEXT) ?: ""

        _nowPlaying.value = NowPlaying(
            title = title,
            artist = artist
        )
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        val notification = sbn.notification
        if (notification.category == Notification.CATEGORY_TRANSPORT) {
            _nowPlaying.value = null
        }
    }
}


