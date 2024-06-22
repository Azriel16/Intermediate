package com.dicoding.intermediate.ui.maps

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.intermediate.R
import com.dicoding.intermediate.data.remote.response.ListStoryItem
import com.dicoding.intermediate.databinding.ActivityMapsBinding
import com.dicoding.intermediate.ui.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        viewModel.getStoriesWithLocation().observe(this) { listStory ->
            listStory?.let { stories ->
                for (story in stories) {
                    story.lat?.let { lat ->
                        story.lon?.let { lon ->
                            val latLng = LatLng(lat, lon)
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(story.name)
                                    .snippet(story.description)
                            )
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                        }
                    }
                }
            }
        }

        addManyMarker()
    }

    data class Data(
        val name: String,
        val latitude: Double,
        val longitude: Double,
        val description: String
    )

    private val boundsBuilder = LatLngBounds.Builder()

    private fun addManyMarker() {
        val data = listOf(
            Data("Floating Market Lembang", -6.8168954, 107.6151046, "Deskripsi Floating Market Lembang"),
            Data("The Great Asia Africa", -6.8331128, 107.6048483, "Deskripsi The Great Asia Africa"),
            Data("Rabbit Town", -6.8668408, 107.608081, "Deskripsi Rabbit Town"),
            Data("Alun-Alun Kota Bandung", -6.9218518, 107.6025294, "Deskripsi Alun-Alun Kota Bandung"),
            Data("Orchid Forest Cikole", -6.780725, 107.637409, "Deskripsi Orchid Forest Cikole")
        )

        data.forEach { dataItem ->
            val latLng = LatLng(dataItem.latitude, dataItem.longitude)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(dataItem.name)
                    .snippet(dataItem.description)
            )
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }
}
