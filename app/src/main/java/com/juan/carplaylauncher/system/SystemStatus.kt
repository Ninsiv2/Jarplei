package com.juan.carplaylauncher.system

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.juan.carplaylauncher.model.CarPlayApp

// =================== LANZAR APPS =======================

fun launchApp(context: Context, app: CarPlayApp) {
    val pkg = app.packageName

    if (pkg.isNullOrBlank()) {
        Toast.makeText(context, "App no configurada", Toast.LENGTH_SHORT).show()
        return
    }

    val intent = context.packageManager.getLaunchIntentForPackage(pkg)
    if (intent != null) {
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } else {
        Toast.makeText(context, "App no instalada", Toast.LENGTH_SHORT).show()
    }
}

// =================== ESTADO DE RED =======================

fun getNetworkStatus(context: Context): String {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return "Sin red"
    val caps = cm.getNetworkCapabilities(network) ?: return "Sin red"

    return when {
        caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
        caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "LTE"
        else -> "Sin red"
    }
}

// =================== ESTADO BLUETOOTH (OPCIONAL) =======================

@SuppressLint("MissingPermission")
fun getBluetoothLabel(): String {
    val adapter = BluetoothAdapter.getDefaultAdapter() ?: return "BT apagado"
    return if (adapter.isEnabled) "Bluetooth conectado" else "Bluetooth apagado"
}
