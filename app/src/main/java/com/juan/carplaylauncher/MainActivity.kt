package com.juan.carplaylauncher

// --------------------- IMPORTS ----------------------
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juan.carplaylauncher.ui.theme.CarplayLauncherTheme
import java.time.LocalTime

// --------------------- MODELO ----------------------
data class CarPlayApp(
    val name: String,
    val iconRes: Int,
    val pkg: String? = null
)

// --------------------- ACTIVITY ----------------------
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CarplayLauncherTheme {
                CarPlayHomeScreen()
            }
        }
    }
}

// ====================== HOME SCREEN ============================
@Composable
fun CarPlayHomeScreen() {

    val apps = listOf(
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

    val appsPerPage = 10
    val pages = apps.chunked(appsPerPage)
    val pageCount = pages.size

    var currentPage by remember { mutableStateOf(0) }

    // Lista de apps recientes (para la barra lateral)
    val recentApps = remember { mutableStateListOf<CarPlayApp>() }

    val context = LocalContext.current

    fun registerRecent(app: CarPlayApp) {
        recentApps.removeAll { it.name == app.name }
        recentApps.add(0, app)
        if (recentApps.size > 3) {
            recentApps.removeLast()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(10.dp)
            .clip(RoundedCornerShape(36.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D2238),
                        Color(0xFF07101F)
                    )
                )
            )
    ) {
        Row(modifier = Modifier.fillMaxSize()) {

            // Barra lateral con hora, red, recientes
            CarPlaySidebar(
                modifier = Modifier
                    .width(95.dp)
                    .fillMaxHeight(),
                recentApps = recentApps
            )

            Spacer(modifier = Modifier.width(22.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 28.dp, top = 22.dp, bottom = 24.dp)
            ) {

                // Grid con swipe horizontal para páginas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { change, drag ->
                                change.consume()

                                if (drag > 40 && currentPage > 0) {
                                    currentPage--
                                } else if (drag < -40 && currentPage < pageCount - 1) {
                                    currentPage++
                                }
                            }
                        }
                ) {
                    CarPlayAppsGrid(
                        apps = pages[currentPage],
                        onAppClick = { app ->
                            registerRecent(app)
                            launchApp(context, app)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mini reproductor de música con BT
                MiniMusicPlayer(
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Dots de páginas
                CarPlayPageDots(pageCount, currentPage)
            }
        }
    }
}

// ====================== SIDEBAR ============================
@Composable
fun CarPlaySidebar(
    modifier: Modifier = Modifier,
    recentApps: List<CarPlayApp>
) {
    val hourText = LocalTime.now().toString().substring(0, 5)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0x11000000))
            .padding(vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Hora + estado de red
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(hourText, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(getNetworkStatus(LocalContext.current), color = Color.White, fontSize = 12.sp)
        }

        // Apps recientes (hasta 3)
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val first = recentApps.getOrNull(0)
            val second = recentApps.getOrNull(1)
            val third = recentApps.getOrNull(2)

            SidebarAppIcon(app = first, active = first != null)
            SidebarAppIcon(app = second, active = false)
            SidebarAppIcon(app = third, active = false)
        }

        // Botón "home" inferior
        Box(
            modifier = Modifier
                .size(42.dp, 22.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.9f))
        )
    }
}

@Composable
fun SidebarAppIcon(app: CarPlayApp?, active: Boolean) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                when {
                    app == null -> Color(0x22000000)
                    active -> Color(0xFF1E90FF)
                    else -> Color(0x33000000)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (app != null) {
            Image(
                painter = painterResource(app.iconRes),
                contentDescription = app.name,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

// ====================== GRID DE APPS ============================
@Composable
fun CarPlayAppsGrid(
    apps: List<CarPlayApp>,
    onAppClick: (CarPlayApp) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        horizontalArrangement = Arrangement.spacedBy(22.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        items(apps) { app ->
            CarPlayAppIcon(
                app = app,
                onClick = { onAppClick(app) }
            )
        }
    }
}

// ====================== ICONO INDIVIDUAL ============================
@Composable
fun CarPlayAppIcon(
    app: CarPlayApp,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Box(
            modifier = Modifier
                .size(86.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFFFFA855),
                            Color(0xFFFF4D3D)
                        )
                    )
                )
                .clickable { onClick() }
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(app.iconRes),
                contentDescription = app.name,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xB3000000))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                app.name,
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ====================== MINI MUSIC PLAYER ============================
@Composable
fun MiniMusicPlayer(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xB3000000))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Reproduciendo",
                color = Color(0xFFB0C4DE),
                fontSize = 11.sp
            )
            Text(
                text = "Tu canción aquí",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = getBluetoothLabel(context),
                color = Color(0xFF9BD4FF),
                fontSize = 11.sp
            )
        }

        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { isPlaying = !isPlaying },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isPlaying) "II" else "▶",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

// ====================== DOTS DE PÁGINA ============================
@Composable
fun CarPlayPageDots(pageCount: Int, currentPage: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { idx ->
            val active = idx == currentPage
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(8.dp)
                    .width(if (active) 20.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (active) Color.White else Color(0x55FFFFFF))
            )
        }
    }
}

// ====================== LANZAR APPS ============================
fun launchApp(context: Context, app: CarPlayApp) {
    app.pkg?.let { pkg ->
        val intent = context.packageManager.getLaunchIntentForPackage(pkg)
        if (intent != null) {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } else {
            Toast.makeText(context, "App no instalada", Toast.LENGTH_SHORT).show()
        }
    }
}

// ====================== ESTADO DE RED ============================
fun getNetworkStatus(context: Context): String {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return "Offline"
    val caps = cm.getNetworkCapabilities(network) ?: return "Offline"

    return when {
        caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
        caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "LTE"
        else -> "Sin red"
    }
}

// ====================== ESTADO BLUETOOTH ============================
@SuppressLint("MissingPermission")
fun getBluetoothLabel(context: Context): String {
    val adapter = BluetoothAdapter.getDefaultAdapter() ?: return "Sin Bluetooth"
    return if (adapter.isEnabled) "Bluetooth conectado" else "Bluetooth apagado"
}
