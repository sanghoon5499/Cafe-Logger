package com.choi.cafelogger

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.choi.cafelogger.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1) Initialize Location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 2) Initialize Places SDK & client
        Places.initialize(this, retrieveApiKeyFromManifest())
        placesClient = Places.createClient(this)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableLocationAndCenterMap()
            searchNearbyCafes()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }

        searchSpecialtyCoffee()
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationAndCenterMap() {
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val myLatLng = LatLng(it.latitude, it.longitude)
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(myLatLng, 15f),
                        1000,
                        null
                    )
                }
            }
    }

    private fun searchNearbyCafes() {
        // 1) Make sure we have location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) return

        // 2) Specify the fields we want
        val placeFields = listOf(Place.Field.NAME, Place.Field.LAT_LNG)

        // 3) Build the request
        val request = FindCurrentPlaceRequest.newInstance(placeFields)

        // 4) Call it
        placesClient.findCurrentPlace(request)
            .addOnSuccessListener { response ->
                // clear old markers
                mMap.clear()
                // add each place as a marker
                for (likelihood in response.placeLikelihoods) {
                    val place = likelihood.place
                    place.latLng?.let { ll ->
                        mMap.addMarker(MarkerOptions().position(ll).title(place.name))
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("MapsActivity", "Current place request failed", e)
            }
    }


    private fun searchSpecialtyCoffee() {
        // placesClient is now non-null, so this won’t crash
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery("specialty coffee")
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { resp ->
                resp.autocompletePredictions.forEach { pred ->
                    val placeReq = FetchPlaceRequest.builder(
                        pred.placeId,
                        listOf(Place.Field.LAT_LNG, Place.Field.NAME)
                    ).build()

                    placesClient.fetchPlace(placeReq)
                        .addOnSuccessListener { placeResp ->
                            placeResp.place.latLng?.let { ll ->
                                mMap.addMarker(
                                    MarkerOptions().position(ll).title(placeResp.place.name)
                                )
                            }
                        }
                }
            }
    }

    private fun retrieveApiKeyFromManifest(): String {
        val ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        return ai.metaData
            .getString("com.google.android.geo.API_KEY")
            ?: error("Maps API key missing from manifest!")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // user just granted permission — set up location
            enableLocationAndCenterMap()
        } else {
            // permission denied — you could show a message or fallback
            Toast.makeText(this, "Location permission required to show your position", Toast.LENGTH_SHORT).show()
        }
    }
}