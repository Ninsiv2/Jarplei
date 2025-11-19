package com.juan.carplaylauncher.spotify

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

                    // Si el token está vencido, dejamos que arriba lo refresquen
                    if (response.code == 401) {
                        SpotifyAuth.accessToken = null
                    }
                    return@withContext null
                }

                if (body.isEmpty()) {
                    // No se está reproduciendo nada
                    return@withContext null
                }

                val json = JSONObject(body)
                val item = json.getJSONObject("item")

                SpotifyTrack(
                    title = item.getString("name"),
                    artist = item.getJSONArray("artists")
                        .getJSONObject(0)
                        .getString("name"),
                    album = item.getJSONObject("album").getString("name"),
                )

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

data class SpotifyTrack(
    val title: String,
    val artist: String,
    val album: String
)
