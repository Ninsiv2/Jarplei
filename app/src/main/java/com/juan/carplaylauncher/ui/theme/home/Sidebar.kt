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
            .background(Color(0x11000000))
            .padding(vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Hora + estado de red
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(hourText, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(networkStatus, color = Color.White, fontSize = 12.sp)
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

        // Botón "home" inferior (decorativo)
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
