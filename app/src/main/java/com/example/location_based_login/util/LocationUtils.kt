package com.example.location_based_login.util

import android.location.Location

object LocationUtils {

    fun isWithinOfficePerimeter(latitude: Double, longitude: Double, sessionManager: SessionManager): Boolean {
        val userLocation = Location("").apply {
            this.latitude = latitude
            this.longitude = longitude
        }

        val officeLocation = Location("").apply {
            this.latitude = sessionManager.officeLatitude.toDouble()
            this.longitude = sessionManager.officeLongitude.toDouble()
        }

        val distance = userLocation.distanceTo(officeLocation)
        return distance <= sessionManager.officeRadiusMeters
    }
} 