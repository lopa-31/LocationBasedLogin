package com.example.location_based_login.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    companion object {
        const val IS_LOGGED_IN = "is_logged_in"
        const val OFFICE_LATITUDE = "office_latitude"
        const val OFFICE_LONGITUDE = "office_longitude"
        const val OFFICE_RADIUS_METERS = "office_radius_meters"

        // Default values
        private const val DEFAULT_OFFICE_LATITUDE = 37.7749f
        private const val DEFAULT_OFFICE_LONGITUDE = -122.4194f
        private const val DEFAULT_OFFICE_RADIUS_METERS = 80.0f
    }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(IS_LOGGED_IN, value).apply()

    var officeLatitude: Float
        get() = prefs.getFloat(OFFICE_LATITUDE, DEFAULT_OFFICE_LATITUDE)
        set(value) = prefs.edit().putFloat(OFFICE_LATITUDE, value).apply()

    var officeLongitude: Float
        get() = prefs.getFloat(OFFICE_LONGITUDE, DEFAULT_OFFICE_LONGITUDE)
        set(value) = prefs.edit().putFloat(OFFICE_LONGITUDE, value).apply()

    var officeRadiusMeters: Float
        get() = prefs.getFloat(OFFICE_RADIUS_METERS, DEFAULT_OFFICE_RADIUS_METERS)
        set(value) = prefs.edit().putFloat(OFFICE_RADIUS_METERS, value).apply()
} 