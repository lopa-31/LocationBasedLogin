package com.example.location_based_login.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.location_based_login.R
import com.example.location_based_login.viewmodel.SplashViewModel

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.splashFragment, true)
                .build()
            when (event) {
                is SplashViewModel.NavigationEvent.GoToDashboard -> {
                    findNavController().navigate(R.id.action_splashFragment_to_dashboardFragment, null, navOptions)
                }
                is SplashViewModel.NavigationEvent.GoToLogin -> {
                    findNavController().navigate(R.id.action_splashFragment_to_loginFragment, null, navOptions)
                }
            }
        }
    }
} 