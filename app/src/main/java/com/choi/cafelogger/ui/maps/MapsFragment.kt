package com.choi.cafelogger.ui.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.core.content.ContextCompat

import androidx.core.graphics.scale
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.choi.cafelogger.NearbySearchResponse
import com.choi.cafelogger.R
import com.choi.cafelogger.SuggestionAdapter
import com.choi.cafelogger.databinding.FragmentMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class MapsFragment : Fragment(), OnMapReadyCallback {

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var suggestionAdapter: SuggestionAdapter
    private var userCenter: LatLng? = null
    private lateinit var searchWatcher: TextWatcher

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = com.google.android.material.transition.platform.MaterialSharedAxis(
            com.google.android.material.transition.platform.MaterialSharedAxis.X, /* forward = */ true
        )

        returnTransition = com.google.android.material.transition.platform.MaterialSharedAxis(
            com.google.android.material.transition.platform.MaterialSharedAxis.X, /* forward = */ false
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Initialize Location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // 2) Initialize Places SDK & client
        Places.initialize(requireContext(), retrieveApiKeyFromManifest())
        placesClient = Places.createClient(requireContext())

        // 3) Initialize Adapter for suggestions + recyclerview
        suggestionAdapter = SuggestionAdapter(::onSuggestionClicked)
        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = suggestionAdapter
        }

        // 4) Create divider for search results
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
            setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
        }
        binding.rvSearchResults.addItemDecoration(divider)

        // 5) Start watching text inputs
        searchWatcher = binding.etSearch.addTextChangedListener { text ->
            val query = text?.toString().orEmpty()
            if (query.length < 2) {
                binding.rvSearchResults.isGone = true
            } else {
                binding.rvSearchResults.isVisible = true
                performAutocomplete(query)
            }
        }

        // 6) Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.supportMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableLocationAndCenterMap()
        } else {
            requestPermissions(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationAndCenterMap() {
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    userCenter = LatLng(it.latitude, it.longitude)
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(userCenter!!, 15f),
                        1000,
                        null
                    )
                    searchSpecialtyCoffeeNearby(userCenter!!)
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
                        activity?.runOnUiThread {
                            // clear old pins
                            mMap.clear()
                            for (r in resp.results) {
                                val original = BitmapFactory.decodeResource(resources, R.drawable.coffee_pin)
                                val scaled = original.scale(80, 80, false)

                                val ll = LatLng(r.geometry.location.lat, r.geometry.location.lng)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(ll)
                                        .title(r.name)
                                        .icon(BitmapDescriptorFactory.fromBitmap(scaled))
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

    private fun performAutocomplete(query: String) {
        val center = userCenter ?: return
        val delta = 0.01
        val bounds = RectangularBounds.newInstance(
            LatLng(center.latitude - delta, center.longitude - delta),
            LatLng(center.latitude + delta, center.longitude + delta)
        )

        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setTypeFilter(TypeFilter.ESTABLISHMENT)
            .setQuery(query)
            .setLocationBias(bounds)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { resp ->
                // pass both the raw predictions and the session token
                suggestionAdapter.submitList(resp.autocompletePredictions, token)
                binding.rvSearchResults.isVisible = resp.autocompletePredictions.isNotEmpty()
            }
            .addOnFailureListener { e ->
                Log.e("MapsActivity", "Autocomplete failed", e)
            }
    }

    fun onSuggestionClicked(prediction: AutocompletePrediction, token: AutocompleteSessionToken) {
        // 1) Temporarily stop reacting to text changes
        binding.etSearch.removeTextChangedListener(searchWatcher)

        // 2) Hide the suggestions list immediately
        binding.rvSearchResults.isGone = true

        // 3) Populate the field with just the name & clear focus
        binding.etSearch.setText(prediction.getPrimaryText(null))
        binding.etSearch.clearFocus()

        // 4) Hide the keyboard
        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)

        // 5) Re-attach your text watcher
        binding.etSearch.addTextChangedListener(searchWatcher)

        // 6) Now fetch the place’s full details and drop a marker
        val placeReq = FetchPlaceRequest.builder(
            prediction.placeId,
            listOf(Place.Field.LAT_LNG, Place.Field.NAME)
        )
            .setSessionToken(token)
            .build()

        placesClient.fetchPlace(placeReq)
            .addOnSuccessListener { resp ->
                val ll = resp.place.latLng!!
                mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(ll.latitude, ll.longitude))
                        .title(resp.place.name)
                )
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(ll.latitude, ll.longitude), 15f)
                )
            }
    }

    // ------------------------------- Backend: APIs, Permissions ------------------------------- //
    private fun retrieveApiKeyFromManifest(): String {
        val ai = requireActivity().packageManager
            .getApplicationInfo(
                requireActivity().packageName,
                PackageManager.GET_META_DATA)
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
            Toast.makeText(requireActivity(), "Location permission required to show your position", Toast.LENGTH_SHORT).show()
        }
    }
}