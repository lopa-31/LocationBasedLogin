package com.example.location_based_login.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager

class LocationProviderChangedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isLocationEnabled) {
                // Location has been disabled. Send a broadcast to the app.
                val localIntent = Intent(ACTION_LOCATION_DISABLED)
                context.sendBroadcast(localIntent)
            }
        }
    }

    companion object {
        const val ACTION_LOCATION_DISABLED = "com.example.location_based_login.ACTION_LOCATION_DISABLED"
    }
} 