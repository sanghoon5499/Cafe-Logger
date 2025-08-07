package com.choi.cafelogger.ui.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.choi.cafelogger.NearbySearchResponse
import com.choi.cafelogger.R
import com.choi.cafelogger.SuggestionAdapter
import com.choi.cafelogger.databinding.ActivityMapsBinding
import com.choi.cafelogger.databinding.FragmentMapsBinding
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
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class MapsFragment : Fragment(), OnMapReadyCallback {

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var suggestionAdapter: SuggestionAdapter
    private var userCenter: LatLng? = null
    private lateinit var searchWatcher: TextWatcher
    private lateinit var bindingMaps: ActivityMapsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return bindingMaps.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("CafeLoggerDEBUG", "MapsFragment.kt")

        // 1) Location SDK
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // 2) Places SDK
        Places.initialize(requireContext(), retrieveApiKey())
        placesClient = Places.createClient(requireContext())

        // 3) RecyclerView + adapter
        suggestionAdapter = SuggestionAdapter(::onSuggestionClicked)
        bindingMaps.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = suggestionAdapter
            val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                .apply {
                    setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
                }
            addItemDecoration(divider)
        }

        // 4) Text watcher
        searchWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s?.toString().orEmpty()
                if (q.length < 2) bindingMaps.etSearch.isGone = true
                else {
                    bindingMaps.etSearch.isVisible = true
                    performAutocomplete(q)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        bindingMaps.etSearch.addTextChangedListener(searchWatcher)

        // 5) Map fragment
        val mapFrag = childFragmentManager.findFragmentById(R.id.supportMapFragment)
                as SupportMapFragment
        mapFrag.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableLocationAndSearch()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationAndSearch() {
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
            loc?.let {
                userCenter = LatLng(it.latitude, it.longitude)
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(userCenter!!, 15f),
                    1000, null
                )
                searchSpecialtyCoffeeNearby(userCenter!!)
            }
        }
    }

    private fun searchSpecialtyCoffeeNearby(center: LatLng) {
        val apiKey = retrieveApiKey()
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("maps.googleapis.com")
            .addPathSegments("maps/api/place/nearbysearch/json")
            .addQueryParameter("location", "${center.latitude},${center.longitude}")
            .addQueryParameter("radius", "5000")
            .addQueryParameter("keyword", "specialty coffee")
            .addQueryParameter("key", apiKey)
            .build()

        val req = Request.Builder().url(url).build()
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()

        client.newCall(req).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("MapsFragment", "Nearby search failed", e)
            }
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let { body ->
                    val resp = Gson().fromJson(body, NearbySearchResponse::class.java)
                    if (resp.status == "OK") {
                        activity?.runOnUiThread {
                            mMap.clear()
                            resp.results.forEach { r ->
                                val bmp = BitmapFactory.decodeResource(
                                    resources, R.drawable.coffee_pin
                                ).scale(80, 80, false)
                                val ll = LatLng(r.geometry.location.lat, r.geometry.location.lng)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(ll)
                                        .title(r.name)
                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                )
                            }
                        }
                    } else {
                        Log.w("MapsFragment", "Places returned ${resp.status}")
                    }
                }
            }
        })
    }

    private fun performAutocomplete(query: String) {
        val center = userCenter ?: return
        val delta = 0.01
        val bias = RectangularBounds.newInstance(
            LatLng(center.latitude - delta, center.longitude - delta),
            LatLng(center.latitude + delta, center.longitude + delta)
        )
        val token = AutocompleteSessionToken.newInstance()
        val req = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setTypeFilter(TypeFilter.ESTABLISHMENT)
            .setLocationBias(bias)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(req)
            .addOnSuccessListener { resp ->
                suggestionAdapter.submitList(resp.autocompletePredictions, token)
                bindingMaps.rvSearchResults.isVisible = resp.autocompletePredictions.isNotEmpty()
            }
            .addOnFailureListener { e -> Log.e("MapsFragment", "Autocomplete failed", e) }
    }

    private fun onSuggestionClicked(pred: AutocompletePrediction, token: AutocompleteSessionToken) {
        // stop watcher & hide list
        bindingMaps.etSearch.removeTextChangedListener(searchWatcher)
        bindingMaps.rvSearchResults.isGone = true

        // set text & hide keyboard
        bindingMaps.etSearch.setText(pred.getPrimaryText(null))
        bindingMaps.etSearch.clearFocus()
        (requireContext().getSystemService(InputMethodManager::class.java))
            .hideSoftInputFromWindow(bindingMaps.etSearch.windowToken, 0)

        // re-attach watcher
        bindingMaps.etSearch.addTextChangedListener(searchWatcher)

        // fetch details + drop red pin
        val req = FetchPlaceRequest.builder(
            pred.placeId, listOf(Place.Field.LAT_LNG, Place.Field.NAME)
        ).setSessionToken(token).build()

        placesClient.fetchPlace(req)
            .addOnSuccessListener { resp ->
                val ll = resp.place.latLng!!
                mMap.addMarker(MarkerOptions().position(ll).title(resp.place.name))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15f))
            }
    }

    private fun retrieveApiKey(): String {
        val ai = requireActivity().packageManager
            .getApplicationInfo(requireActivity().packageName, PackageManager.GET_META_DATA)
        return ai.metaData.getString("com.google.android.geo.API_KEY")
            ?: error("Missing Maps API key")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, results: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, results)
        if (requestCode == REQUEST_LOCATION_PERMISSION
            && results.firstOrNull() == PackageManager.PERMISSION_GRANTED
        ) {
            enableLocationAndSearch()
        } else {
            Toast.makeText(requireContext(),
                "Location permission required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}