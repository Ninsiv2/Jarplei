package com.juan.carplaylauncher.spotify

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Base64

object SpotifyAuth {

    private const val CLIENT_ID = "62c2a7a012904a9086f85a57c45884bb"
    private const val CLIENT_SECRET = "4ae30d4f312042428358d38cd0db2da3"
    private const val REDIRECT_URI = "carplaylauncher://callback"

    private const val AUTH_URL = "https://accounts.spotify.com/authorize"
    private const val TOKEN_URL = "https://accounts.spotify.com/api/token"

    private const val SCOPES = "user-read-currently-playing user-read-playback-state"

    var accessToken: String? = null
    var refreshToken: String? = null

    private val client = OkHttpClient()

    fun startLogin(context: Context) {
        val url = Uri.parse(AUTH_URL)
            .buildUpon()
            .appendQueryParameter("client_id", CLIENT_ID)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("scope", SCOPES)
            .build()

        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(context, url)
    }

    suspend fun handleRedirect(context: Context, code: String) {
        withContext(Dispatchers.IO) {
            val requestBody = FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", REDIRECT_URI)
                .build()

            val credentials = "$CLIENT_ID:$CLIENT_SECRET"
            val basic = Base64.getEncoder().encodeToString(credentials.toByteArray())

            val request = Request.Builder()
                .url(TOKEN_URL)
                .addHeader("Authorization", "Basic $basic")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            Log.d("SPOTIFY_AUTH", "Response: $body")

            val json = JSONObject(body)

            accessToken = json.getString("access_token")
            refreshToken = json.optString("refresh_token", refreshToken)

            saveTokens(context)
        }
    }

    // Refresca el token cuando el actual esté vencido
    suspend fun refreshAccessToken(context: Context): Boolean {
        val currentRefresh = refreshToken ?: return false

        return withContext(Dispatchers.IO) {
            try {
                val requestBody = FormBody.Builder()
                    .add("grant_type", "refresh_token")
                    .add("refresh_token", currentRefresh)
                    .build()

                val credentials = "$CLIENT_ID:$CLIENT_SECRET"
                val basic = Base64.getEncoder().encodeToString(credentials.toByteArray())

                val request = Request.Builder()
                    .url(TOKEN_URL)
                    .addHeader("Authorization", "Basic $basic")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: ""

                Log.d("SPOTIFY_AUTH", "Refresh response: $body")

                if (!response.isSuccessful) return@withContext false

                val json = JSONObject(body)
                accessToken = json.getString("access_token")
                // A veces Spotify devuelve nuevo refresh; si no, dejamos el que ya teníamos
                refreshToken = json.optString("refresh_token", refreshToken)

                saveTokens(context)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    fun saveTokens(context: Context) {
        val prefs = context.getSharedPreferences("spotify", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("access", accessToken)
            .putString("refresh", refreshToken)
            .apply()
    }

    fun loadTokens(context: Context) {
        val prefs = context.getSharedPreferences("spotify", Context.MODE_PRIVATE)
        accessToken = prefs.getString("access", null)
        refreshToken = prefs.getString("refresh", null)
    }
}
