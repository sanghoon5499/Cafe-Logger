package com.choi.cafelogger

import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

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
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.Gson

import java.io.IOException

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
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
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
                    searchSpecialtyCoffeeNearby(myLatLng)
                }
            }
    }

    private fun searchSpecialtyCoffeeNearby(myLL: LatLng) {
        // 1) Build the URL
        val apiKey = retrieveApiKeyFromManifest()
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("maps.googleapis.com")
            .addPathSegments("maps/api/place/nearbysearch/json")
            .addQueryParameter("location", "${myLL.latitude},${myLL.longitude}")
            .addQueryParameter("radius", "5000")
            .addQueryParameter("keyword", "specialty coffee")
            .addQueryParameter("key", apiKey)
            .build()

        // 2) Create request + client
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()

        // 3) Fire asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MapsActivity", "Nearby search failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { bodyStr ->
                    val resp = Gson().fromJson(bodyStr, NearbySearchResponse::class.java)
                    if (resp.status == "OK") {
                        runOnUiThread {
                            // clear old pins
                            mMap.clear()
                            for (r in resp.results) {
                                val ll = LatLng(r.geometry.location.lat, r.geometry.location.lng)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(ll)
                                        .title(r.name)
                                )
                            }
                        }
                    } else {
                        Log.w("MapsActivity", "Places API returned ${resp.status}")
                    }
                }
            }
        })
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