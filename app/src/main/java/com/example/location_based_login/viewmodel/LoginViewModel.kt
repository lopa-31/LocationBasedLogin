package com.example.location_based_login.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.location_based_login.util.LocationUtils
import com.example.location_based_login.util.SessionManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch

class LoginViewModel(private val app: Application) : AndroidViewModel(app) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(app)
    private val sessionManager = SessionManager(app)

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    @SuppressLint("MissingPermission")
    fun onPermissionsGranted() {
        _loginState.value = LoginState.Loading
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    if (LocationUtils.isWithinOfficePerimeter(location.latitude, location.longitude, sessionManager)) {
                        sessionManager.isLoggedIn = true
                        _loginState.postValue(LoginState.Success)
                    } else {
                        _loginState.postValue(LoginState.Error("You are not within the office perimeter."))
                    }
                } else {
                    _loginState.postValue(LoginState.Error("Could not determine location. Please try again."))
                }
            }
            .addOnFailureListener { e ->
                _loginState.postValue(LoginState.Error("An error occurred while fetching location: ${e.message}"))
            }
    }

    sealed class LoginState {
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }
} 