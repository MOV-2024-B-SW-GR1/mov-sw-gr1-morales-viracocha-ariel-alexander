package com.example.examen2

// MapsActivity.kt
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var locationName: String = ""
    private val DEFAULT_ZOOM = 15f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtener datos de la ubicación
        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)
        locationName = intent.getStringExtra("title") ?: "Ubicación"

        // Configurar el mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Configurar el tipo de mapa
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        // Crear la posición con la latitud y longitud
        val position = LatLng(latitude, longitude)

        // Añadir el marcador
        mMap.addMarker(MarkerOptions()
            .position(position)
            .title(locationName)
        )?.showInfoWindow()

        // Mover la cámara a la posición con zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM))

        // Habilitar controles del mapa
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }
    }
}