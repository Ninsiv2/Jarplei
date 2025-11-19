package com.juan.carplaylauncher.ui.home

import com.juan.carplaylauncher.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juan.carplaylauncher.MapScreen
import com.juan.carplaylauncher.data.AppCatalog
import com.juan.carplaylauncher.model.CarPlayApp
import com.juan.carplaylauncher.system.getNetworkStatus
import com.juan.carplaylauncher.system.launchApp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ==== imports clima / ubicación ====
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.roundToInt

// =================== ESTADO Y SCREENS =======================

data class HomeUiState(
    val apps: List<CarPlayApp>,
    val currentPage: Int = 0,
    val recentApps: List<CarPlayApp> = emptyList()
)

sealed class CarPlayScreen {
    object Launcher : CarPlayScreen()
    object MapDashboard : CarPlayScreen()
    object Music : CarPlayScreen()
}

// =================== ROOT =======================

@Composable
fun CarPlayHomeScreen() {
    var uiState by remember {
        mutableStateOf(HomeUiState(apps = AppCatalog.defaultApps))
    }

    var currentScreen by remember {
        mutableStateOf<CarPlayScreen>(CarPlayScreen.Launcher)
    }

    when (currentScreen) {
        is CarPlayScreen.Launcher -> {
            LauncherScreen(
                uiState = uiState,
                onUiStateChange = { uiState = it },
                onOpenMap = { currentScreen = CarPlayScreen.MapDashboard },
                onOpenMusic = { currentScreen = CarPlayScreen.Music }
            )
        }

        is CarPlayScreen.MapDashboard -> {
            MapDashboardScreen(
                onBackHome = { currentScreen = CarPlayScreen.Launcher }
            )
        }

        is CarPlayScreen.Music -> {
            MusicScreen(
                onBackHome = { currentScreen = CarPlayScreen.Launcher }
            )
        }
    }
}

// =================== BARRA SUPERIOR =======================

@Composable
fun CarPlayStatusBar(
    modifier: Modifier = Modifier
) {
    val timeText = remember {
        SimpleDateFormat("H:mm", Locale.getDefault()).format(Date())
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Hora
            Text(
                text = timeText,
                color = Color.White,
            )

            // Señal + WiFi + Batería
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Wi-Fi",
                    color = Color.White.copy(alpha = 0.9f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    Box(
                        Modifier
                            .size(width = 3.dp, height = 6.dp)
                            .background(Color.White.copy(alpha = 0.7f))
                    )
                    Box(
                        Modifier
                            .size(width = 3.dp, height = 9.dp)
                            .background(Color.White.copy(alpha = 0.85f))
                    )
                    Box(
                        Modifier
                            .size(width = 3.dp, height = 12.dp)
                            .background(Color.White)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(10.dp)
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(3.dp)
                        )
                        .padding(1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.7f)
                            .background(Color.White, shape = RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}

// =================== PANTALLA LAUNCHER =======================

@Composable
private fun LauncherScreen(
    uiState: HomeUiState,
    onUiStateChange: (HomeUiState) -> Unit,
    onOpenMap: () -> Unit,
    onOpenMusic: () -> Unit
) {
    val context = LocalContext.current

    val appsPerPage = 10
    val pages = remember(uiState.apps) { uiState.apps.chunked(appsPerPage) }
    val pageCount = pages.size

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 6.dp, bottom = 8.dp)
        ) {

            CarPlayStatusBar()
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 4.dp)
            ) {

                CarPlaySidebar(
                    modifier = Modifier
                        .width(95.dp)
                        .fillMaxHeight(),
                    recentApps = uiState.recentApps,
                    networkStatus = getNetworkStatus(context)
                )

                Spacer(modifier = Modifier.width(22.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 28.dp, top = 16.dp, bottom = 16.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .pointerInput(pageCount, uiState.currentPage) {
                                detectHorizontalDragGestures { _, drag ->
                                    when {
                                        drag > 40 && uiState.currentPage > 0 ->
                                            onUiStateChange(
                                                uiState.copy(currentPage = uiState.currentPage - 1)
                                            )

                                        drag < -40 && uiState.currentPage < pageCount - 1 ->
                                            onUiStateChange(
                                                uiState.copy(currentPage = uiState.currentPage + 1)
                                            )
                                    }
                                }
                            }
                    ) {
                        CarPlayAppsGrid(
                            apps = pages[uiState.currentPage],
                            onAppClick = { app ->
                                when (app.name) {
                                    "Mapas" -> onOpenMap()
                                    "Spotify", "Música", "YT Music" -> onOpenMusic()
                                    else -> {
                                        val newRecents = updateRecents(uiState.recentApps, app)
                                        onUiStateChange(uiState.copy(recentApps = newRecents))
                                        launchApp(context, app)
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    MiniMusicPlayer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(24.dp),
                                clip = false
                            )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    CarPlayPageDots(
                        pageCount = pageCount,
                        currentPage = uiState.currentPage
                    )
                }
            }
        }
    }
}

// =================== PANTALLA MAPA =======================

@Composable
fun MapDashboardScreen(
    onBackHome: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 6.dp, bottom = 8.dp)
        ) {

            CarPlayStatusBar()
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {

                Box(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    MapScreen(
                        modifier = Modifier.fillMaxSize(),
                        showBackButton = false
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // 🌤 CLIMA
                        GlassTile(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            WeatherWidget(
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // 🎵 REPRODUCTOR NUEVO
                        MiniMusicPlayer(
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                        // 🧭 TEXTO NAVEGACIÓN
                        GlassTile(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SmartNavWidget(
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White.copy(alpha = 0.95f))
                .clickable { onBackHome() }
                .padding(horizontal = 28.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Home",
                color = Color.Black
            )
        }
    }
}

// =================== PANTALLA MÚSICA =======================

@Composable
fun MusicScreen(
    onBackHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF35003A),
                        Color(0xFF070015)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 6.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CarPlayStatusBar()
            Spacer(modifier = Modifier.height(10.dp))

            MiniMusicPlayer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(24.dp),
                        clip = false
                    )
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.9f))
                    .clickable { onBackHome() }
                    .padding(horizontal = 28.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Home",
                    color = Color.Black
                )
            }
        }
    }
}

// =================== WIDGET CLIMA =======================

@Composable
fun WeatherWidget(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var weather by remember { mutableStateOf<WeatherInfo?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        weather = getWeatherForCurrentLocation(context)
        loading = false
    }

    Column(
        modifier = modifier
            .padding(horizontal = 2.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "Clima",
            color = Color.White.copy(alpha = 0.9f)
        )

        if (loading) {
            Text(
                text = "Obteniendo ubicación...",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
            )
            return@Column
        }

        val info = weather

        if (info == null) {
            Text(
                text = "Activa la ubicación para ver el clima",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
            )
            return@Column
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFFFFD65B),
                                Color(0xFFFF8A00)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "☀",
                    color = Color.White,
                    fontSize = 22.sp
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "${info.temperature}°",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${info.city} · ${info.description}",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }
        }

        Text(
            text = "Se siente como ${info.feelsLike}°. Datos por GPS.",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp
        )
    }
}

// =================== TEXTO NAVEGACIÓN =======================

@Composable
fun SmartNavWidget(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 2.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Navegación",
            color = Color.White.copy(alpha = 0.9f)
        )
        Text(
            text = "Toca un punto en el mapa para marcar un destino.",
            color = Color.White
        )
        Text(
            text = "Aquí verás distancia y hora estimada.",
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

// =================== TARJETA GLASS =======================

@Composable
fun GlassTile(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.22f),
                        Color.White.copy(alpha = 0.10f)
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        content()
    }
}

// =================== HELPERS =======================

private fun updateRecents(
    current: List<CarPlayApp>,
    app: CarPlayApp
): List<CarPlayApp> {
    val list = current.toMutableList()
    list.removeAll { it.name == app.name }
    list.add(0, app)
    if (list.size > 3) list.removeAt(list.lastIndex)
    return list
}

// ---- Clima ----

data class WeatherInfo(
    val temperature: Int,
    val feelsLike: Int,
    val description: String,
    val city: String
)

private suspend fun getWeatherForCurrentLocation(context: Context): WeatherInfo? {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val hasLocationPermission =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    if (!hasLocationPermission) return null

    val provider = when {
        lm.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
        else -> return null
    }

    val lastLocation: Location = lm.getLastKnownLocation(provider) ?: return null
    val lat = lastLocation.latitude
    val lon = lastLocation.longitude

    return withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()

            // 1) Clima actual
            val weatherUrl =
                "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true&timezone=auto"
            val weatherReq = Request.Builder().url(weatherUrl).build()
            val weatherRes = client.newCall(weatherReq).execute()
            val weatherBody = weatherRes.body?.string() ?: return@withContext null
            val weatherJson = JSONObject(weatherBody)
            val current = weatherJson.getJSONObject("current_weather")

            val temp = current.getDouble("temperature").roundToInt()
            val feelsLike = temp
            val code = current.getInt("weathercode")

            val description = when (code) {
                0 -> "Despejado"
                1, 2, 3 -> "Parcialmente nublado"
                45, 48 -> "Niebla"
                51, 53, 55 -> "Llovizna"
                61, 63, 65 -> "Lluvia"
                71, 73, 75 -> "Nieve"
                80, 81, 82 -> "Chubascos"
                else -> "Clima variable"
            }

            // 2) Nombre de ciudad aproximado
            val cityUrl =
                "https://geocoding-api.open-meteo.com/v1/reverse?latitude=$lat&longitude=$lon&language=es&count=1"
            val cityReq = Request.Builder().url(cityUrl).build()
            val cityRes = client.newCall(cityReq).execute()
            val cityBody = cityRes.body?.string() ?: ""
            val cityJson = JSONObject(cityBody)
            val results = cityJson.optJSONArray("results")
            val cityName = results?.optJSONObject(0)?.optString("name") ?: "Ubicación actual"

            WeatherInfo(
                temperature = temp,
                feelsLike = feelsLike,
                description = description,
                city = cityName
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
