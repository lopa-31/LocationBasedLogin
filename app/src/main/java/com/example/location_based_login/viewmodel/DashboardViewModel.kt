package com.example.location_based_login.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.location_based_login.service.LocationService
import com.example.location_based_login.util.SessionManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)

    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    init {
        viewModelScope.launch {
            LocationService.isWithinPerimeterFlow.collectLatest { isWithinPerimeter ->
                if (!isWithinPerimeter) {
                    logout()
                }
            }
        }
    }

    fun logout() {
        if (sessionManager.isLoggedIn) {
            sessionManager.isLoggedIn = false
            _navigationEvent.value = NavigationEvent.GoToLogin
        }
    }

    sealed class NavigationEvent {
        object GoToLogin : NavigationEvent()
    }
} 