package com.juan.carplaylauncher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.juan.carplaylauncher.data.AppCatalog
import com.juan.carplaylauncher.model.CarPlayApp
import com.juan.carplaylauncher.system.getNetworkStatus
import com.juan.carplaylauncher.system.launchApp
import androidx.compose.ui.draw.clip


data class HomeUiState(
    val apps: List<CarPlayApp>,
    val currentPage: Int = 0,
    val recentApps: List<CarPlayApp> = emptyList()
)

/**
 * Pantalla principal del launcher CarPlay.
 */
@Composable
fun CarPlayHomeScreen() {
    val context = LocalContext.current

    var uiState by remember {
        mutableStateOf(HomeUiState(apps = AppCatalog.defaultApps))
    }

    val appsPerPage = 10
    val pages = remember(uiState.apps) { uiState.apps.chunked(appsPerPage) }
    val pageCount = pages.size

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
                    .padding(end = 28.dp, top = 22.dp, bottom = 24.dp)
            ) {

                // Grid con swipe horizontal por páginas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(pageCount, uiState.currentPage) {
                            detectHorizontalDragGestures { change, drag ->
                                // change.consume() opcional
                                when {
                                    drag > 40 && uiState.currentPage > 0 -> {
                                        uiState = uiState.copy(currentPage = uiState.currentPage - 1)
                                    }

                                    drag < -40 && uiState.currentPage < pageCount - 1 -> {
                                        uiState = uiState.copy(currentPage = uiState.currentPage + 1)
                                    }
                                }
                            }
                        }
                ) {
                    CarPlayAppsGrid(
                        apps = pages[uiState.currentPage],
                        onAppClick = { app ->
                            uiState = uiState.copy(
                                recentApps = updateRecents(uiState.recentApps, app)
                            )
                            launchApp(context, app)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                MiniMusicPlayer(
                    modifier = Modifier.fillMaxWidth()
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

private fun updateRecents(
    current: List<CarPlayApp>,
    app: CarPlayApp
): List<CarPlayApp> {
    val list = current.toMutableList()
    list.removeAll { it.name == app.name }
    list.add(0, app)
    if (list.size > 3) list.removeLast()
    return list
}
