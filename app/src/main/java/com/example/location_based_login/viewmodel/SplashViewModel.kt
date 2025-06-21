package com.example.location_based_login.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.location_based_login.util.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)

    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    init {
        decideNextScreen()
    }

    private fun decideNextScreen() {
        viewModelScope.launch {
            delay(2000) // Simulate a short delay for the splash screen

            if (sessionManager.isLoggedIn) {
                _navigationEvent.value = NavigationEvent.GoToDashboard
            } else {
                _navigationEvent.value = NavigationEvent.GoToLogin
            }
        }
    }

    sealed class NavigationEvent {
        object GoToDashboard : NavigationEvent()
        object GoToLogin : NavigationEvent()
    }
} 