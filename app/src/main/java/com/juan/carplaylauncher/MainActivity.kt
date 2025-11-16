package com.juan.carplaylauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.juan.carplaylauncher.ui.home.CarPlayHomeScreen
import com.juan.carplaylauncher.ui.theme.CarplayLauncherTheme

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
