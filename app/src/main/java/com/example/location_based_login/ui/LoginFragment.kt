package com.example.location_based_login.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.location_based_login.R
import com.example.location_based_login.service.LocationService
import com.example.location_based_login.util.SessionManager
import com.example.location_based_login.viewmodel.LoginViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var loginButton: Button
    private lateinit var setLocationButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var sessionManager: SessionManager

    private val requestForegroundPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.any { !it.value }) {
                Toast.makeText(
                    requireContext(),
                    "Foreground permissions are required to use this app.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val requestBackgroundPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.onPermissionsGranted()
            } else {
                progressBar.visibility = View.GONE
                loginButton.isEnabled = true
                Toast.makeText(
                    requireContext(),
                    "Background location permission is required to log in.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        loginButton = view.findViewById(R.id.login_button)
        setLocationButton = view.findViewById(R.id.set_location_button)
        progressBar = view.findViewById(R.id.progress_bar)

        requestForegroundPermissions()

        loginButton.setOnClickListener {
            handleLoginClick()
        }

        setLocationButton.setOnClickListener {
            showEditLocationDialog()
        }

        observeViewModel()
    }

    private fun showEditLocationDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_location, null)
        val latitudeEditText = dialogView.findViewById<EditText>(R.id.latitude_edit_text)
        val longitudeEditText = dialogView.findViewById<EditText>(R.id.longitude_edit_text)

        latitudeEditText.setText(sessionManager.officeLatitude.toString())
        longitudeEditText.setText(sessionManager.officeLongitude.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("Set Office Location")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val lat = latitudeEditText.text.toString().toFloatOrNull()
                val lon = longitudeEditText.text.toString().toFloatOrNull()

                if (lat != null && lon != null) {
                    sessionManager.officeLatitude = lat
                    sessionManager.officeLongitude = lon
                    Toast.makeText(requireContext(), "Location saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginViewModel.LoginState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    loginButton.isEnabled = false
                }
                is LoginViewModel.LoginState.Success -> {
                    progressBar.visibility = View.GONE
                    startLocationService()
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.loginFragment, true)
                        .build()
                    findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment, null, navOptions)
                }
                is LoginViewModel.LoginState.Error -> {
                    progressBar.visibility = View.GONE
                    loginButton.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun startLocationService() {
        val intent = Intent(requireActivity(), LocationService::class.java).apply {
            action = LocationService.ACTION_START
        }
        requireActivity().startService(intent)
    }

    private fun requestForegroundPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val neededPermissions = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (neededPermissions.isNotEmpty()) {
            requestForegroundPermissionsLauncher.launch(neededPermissions)
        }
    }

    private fun handleLoginClick() {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true

        if (!fineLocationGranted || !notificationGranted) {
            Toast.makeText(requireContext(), "Please grant foreground permissions first.", Toast.LENGTH_SHORT)
                .show()
            requestForegroundPermissions()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                viewModel.onPermissionsGranted()
            } else {
                Toast.makeText(requireContext(), "Please select 'Allow all the time' for location access.", Toast.LENGTH_LONG).show()
                requestBackgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        } else {
            viewModel.onPermissionsGranted()
        }
    }
} 