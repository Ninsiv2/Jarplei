package com.juan.carplaylauncher.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juan.carplaylauncher.model.CarPlayApp
import java.time.LocalTime

@Composable
fun CarPlaySidebar(
    modifier: Modifier = Modifier,
    recentApps: List<CarPlayApp>,
    networkStatus: String
) {
    val hourText = LocalTime.now().toString().substring(0, 5)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0x9905060D),
                        Color(0xCC05060D)
                    )
                )
            )
            .padding(vertical = 20.dp, horizontal = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Hora + estado de red
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                hourText,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                networkStatus,
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 12.sp
            )
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
                .size(width = 44.dp, height = 22.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White.copy(alpha = 0.9f))
        )
    }
}

@Composable
fun SidebarAppIcon(
    app: CarPlayApp?,
    active: Boolean
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                when {
                    app == null -> Color(0x22000000)
                    active -> Color(0xFF1D7CFB)
                    else -> Color(0x33000000)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (app != null) {
            Image(
                painter = painterResource(app.iconRes),
                contentDescription = app.name,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
