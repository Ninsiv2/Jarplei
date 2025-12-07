package com.juan.carplaylauncher.spotify

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object SpotifyApi {

    private const val CURRENTLY_PLAYING_URL =
        "https://api.spotify.com/v1/me/player/currently-playing"

    private val client = OkHttpClient()

    suspend fun getCurrentlyPlaying(): SpotifyTrack? {
        val token = SpotifyAuth.accessToken ?: return null

        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(CURRENTLY_PLAYING_URL)
                    .addHeader("Authorization", "Bearer $token")
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    Log.e("SPOTIFY", "Error: $body")
                    if (response.code == 401) {
                        SpotifyAuth.accessToken = null
                    }
                    return@withContext null
                }

                if (body.isEmpty()) return@withContext null

                val json = JSONObject(body)

                // Estado de reproducción
                val isPlaying = json.optBoolean("is_playing", false)

                // Objeto de track
                val item = json.getJSONObject("item")

                // Info principal
                val title = item.getString("name")
                val artist = item.getJSONArray("artists")
                    .getJSONObject(0)
                    .getString("name")
                val album = item.getJSONObject("album").getString("name")

                // URL de imagen del álbum (Spotify da varias)
                val images = item.getJSONObject("album").getJSONArray("images")
                val imageUrl = images.getJSONObject(0).getString("url")  // 640px HD

                // Descargar el bitmap
                val bitmap = downloadBitmap(imageUrl)

                // Duraciones
                val durationMs = item.getLong("duration_ms")
                val progressMs = json.optLong("progress_ms", 0L)

                SpotifyTrack(
                    title = title,
                    artist = artist,
                    album = album,
                    coverBitmap = bitmap,
                    coverUrl = imageUrl,
                    isPlaying = isPlaying,
                    durationMs = durationMs,
                    progressMs = progressMs
                )

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun downloadBitmap(url: String): Bitmap? {
        return try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val bytes = response.body?.bytes() ?: return null
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

data class SpotifyTrack(
    val title: String,
    val artist: String,
    val album: String,
    val coverBitmap: Bitmap?,    // FOTO REAL DEL ÁLBUM
    val coverUrl: String?,       // URL si la quieres reutilizar
    val isPlaying: Boolean,      // Spotify dice si está sonando o no
    val durationMs: Long,        // duración total
    val progressMs: Long         // progreso actual
)

