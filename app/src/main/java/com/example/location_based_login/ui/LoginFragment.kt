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

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                viewModel.onPermissionsGranted()
            } else {
                progressBar.visibility = View.GONE
                loginButton.isEnabled = true
                Toast.makeText(requireContext(), "All permissions are required to log in.\n${permissions.all { it.value }}", Toast.LENGTH_LONG).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        loginButton = view.findViewById(R.id.login_button)
        setLocationButton = view.findViewById(R.id.set_location_button)
        progressBar = view.findViewById(R.id.progress_bar)

        loginButton.setOnClickListener {
            requestLocationPermissions()
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
                    findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
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

    private fun requestLocationPermissions() {
        val requiredPermissions = mutableListOf<String>()
        requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requiredPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest)
        } else {
            viewModel.onPermissionsGranted()
        }
    }
} 