package com.juan.carplaylauncher.system

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.juan.carplaylauncher.model.CarPlayApp

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

@SuppressLint("MissingPermission")
fun getBluetoothLabel(): String {
    val adapter = BluetoothAdapter.getDefaultAdapter() ?: return "Sin Bluetooth"
    return if (adapter.isEnabled) "Bluetooth conectado" else "Bluetooth apagado"
}
