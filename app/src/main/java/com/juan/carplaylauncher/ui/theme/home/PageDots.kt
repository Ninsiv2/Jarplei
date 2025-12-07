package com.juan.carplaylauncher.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CarPlayPageDots(
    pageCount: Int,
    currentPage: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val size = animateDpAsState(if (index == currentPage) 10.dp else 6.dp).value
            val color = animateColorAsState(
                if (index == currentPage) Color.White else Color.White.copy(alpha = 0.35f)
            ).value

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(size)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
