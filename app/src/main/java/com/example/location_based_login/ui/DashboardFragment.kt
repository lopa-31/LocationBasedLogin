package com.example.location_based_login.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.location_based_login.R
import com.example.location_based_login.service.LocationService
import com.example.location_based_login.util.LocationProviderChangedReceiver
import com.example.location_based_login.viewmodel.DashboardViewModel

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var logoutButton: Button

    private val locationDisabledReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            showLocationDisabledDialog()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logoutButton = view.findViewById(R.id.logout_button)

        logoutButton.setOnClickListener {
            viewModel.logout()
        }

        viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.dashboardFragment, true)
                .build()
            if (event is DashboardViewModel.NavigationEvent.GoToLogin) {
                stopLocationService()
                findNavController().navigate(R.id.action_dashboardFragment_to_loginFragment, null, navOptions)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(LocationProviderChangedReceiver.ACTION_LOCATION_DISABLED)
        requireActivity().registerReceiver(locationDisabledReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(locationDisabledReceiver)
    }

    private fun showLocationDisabledDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Location Services Disabled")
            .setMessage("Location services are required for this app to function. You will be logged out.")
            .setPositiveButton("OK") { _, _ ->
                viewModel.logout()
            }
            .setCancelable(false)
            .show()
    }

    private fun stopLocationService() {
        val intent = Intent(requireActivity(), LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
        }
        requireActivity().startService(intent)
    }
} 