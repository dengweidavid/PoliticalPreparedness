package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception
import java.util.Locale

class RepresentativeFragment : Fragment() {

    companion object {
        private const val TAG = "RepresentativeFragment"
        private const val REQUEST_ACCESS_FINE_LOCATION = 1001
        const val MOTION_LAYOUT_STATE_KEY = "MOTION_LAYOUT_STATE_KEY"
    }

    private val viewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(this).get(RepresentativeViewModel::class.java)
    }

    lateinit var binding: FragmentRepresentativeBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentRepresentativeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = RepresentativeListAdapter()
        binding.recyclerRepresentatives.adapter = adapter
        viewModel.representatives.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.motionLayoutState.observe(viewLifecycleOwner) {
            binding.motionLayout.transitionToState(it)
        }

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            clearEditFocus()
            viewModel.getRepresentativesByAddress()
        }

        binding.buttonLocation.setOnClickListener {
            hideKeyboard()
            clearEditFocus()
            binding.buttonLocation.requestFocus()
            if (checkLocationPermissions()) {
                getLocation()
                viewModel.getRepresentativesByAddress()
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity as Activity)

        binding.executePendingBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated is called")

        if (savedInstanceState != null) {
            val motionLayoutState: Int = savedInstanceState.getInt(MOTION_LAYOUT_STATE_KEY)
            Log.d(TAG, "Motion Layout State: $motionLayoutState")
            viewModel.setMotionLayoutState(motionLayoutState)
            viewModel.getState()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState is called")

        outState.putInt(MOTION_LAYOUT_STATE_KEY, binding.motionLayout.currentState)
        viewModel.saveState()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (REQUEST_ACCESS_FINE_LOCATION == requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Snackbar.make(requireView(), R.string.error_require_location_permission, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_ACCESS_FINE_LOCATION)
            false
        }
    }

    private fun isPermissionGranted() : Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            try {
                val address = geoCodeLocation(location)
                viewModel.setAddress(address)
            } catch (e: Exception) {
                e.localizedMessage?.let { Log.e("location error - representative", it) }
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun clearEditFocus() {
        binding.addressLine1.clearFocus()
        binding.addressLine2.clearFocus()
        binding.city.clearFocus()
        binding.zip.clearFocus()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}