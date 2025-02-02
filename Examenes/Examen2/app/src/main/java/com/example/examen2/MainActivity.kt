package com.example.examen2

// MainActivity.kt
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var locations: ArrayList<Location>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        listView = findViewById(R.id.locationListView)

        // Configurar FAB para agregar nueva ubicación
        findViewById<FloatingActionButton>(R.id.fabAddLocation).setOnClickListener {
            showAddLocationDialog()
        }

        loadLocations()

        listView.setOnItemClickListener { _, view, position, _ ->
            showLocationMenu(view, locations[position])
        }
    }

    private fun loadLocations() {
        locations = dbHelper.getAllLocations()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            locations.map { it.name }
        )
        listView.adapter = adapter
    }

    private fun showLocationMenu(view: View, location: Location) {
        PopupMenu(this, view).apply {
            menu.add("Ver en Mapa")
            menu.add("Editar")
            menu.add("Eliminar")
            menu.add("Ver Detalles")

            setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Ver en Mapa" -> openMap(location)
                    "Editar" -> editLocation(location)
                    "Eliminar" -> deleteLocation(location)
                    "Ver Detalles" -> openDetails(location)
                }
                true
            }
            show()
        }
    }

    private fun openMap(location: Location) {
        val intent = Intent(this, MapsActivity::class.java).apply {
            putExtra("latitude", location.latitude)
            putExtra("longitude", location.longitude)
            putExtra("title", location.name)
        }
        startActivity(intent)
    }

    private fun showAddLocationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_location, null)

        AlertDialog.Builder(this)
            .setTitle("Agregar Nueva Ubicación")
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val name = dialogView.findViewById<EditText>(R.id.etLocationName).text.toString()
                val description = dialogView.findViewById<EditText>(R.id.etLocationDescription).text.toString()
                val latitude = dialogView.findViewById<EditText>(R.id.etLatitude).text.toString().toDoubleOrNull()
                val longitude = dialogView.findViewById<EditText>(R.id.etLongitude).text.toString().toDoubleOrNull()

                if (name.isNotEmpty() && latitude != null && longitude != null) {
                    val location = Location(
                        name = name,
                        description = description,
                        latitude = latitude,
                        longitude = longitude
                    )
                    dbHelper.insertLocation(location)
                    loadLocations()
                    Toast.makeText(this, "Ubicación agregada exitosamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Por favor complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun editLocation(location: Location) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_location, null)

        // Pre-poblar los campos con los datos existentes
        dialogView.findViewById<EditText>(R.id.etLocationName).setText(location.name)
        dialogView.findViewById<EditText>(R.id.etLocationDescription).setText(location.description)
        dialogView.findViewById<EditText>(R.id.etLatitude).setText(location.latitude.toString())
        dialogView.findViewById<EditText>(R.id.etLongitude).setText(location.longitude.toString())

        AlertDialog.Builder(this)
            .setTitle("Editar Ubicación")
            .setView(dialogView)
            .setPositiveButton("Actualizar") { dialog, _ ->
                val name = dialogView.findViewById<EditText>(R.id.etLocationName).text.toString()
                val description = dialogView.findViewById<EditText>(R.id.etLocationDescription).text.toString()
                val latitude = dialogView.findViewById<EditText>(R.id.etLatitude).text.toString().toDoubleOrNull()
                val longitude = dialogView.findViewById<EditText>(R.id.etLongitude).text.toString().toDoubleOrNull()

                if (name.isNotEmpty() && latitude != null && longitude != null) {
                    val updatedLocation = Location(
                        id = location.id,
                        name = name,
                        description = description,
                        latitude = latitude,
                        longitude = longitude
                    )
                    dbHelper.updateLocation(updatedLocation)
                    loadLocations()
                    Toast.makeText(this, "Ubicación actualizada exitosamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Por favor complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteLocation(location: Location) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Ubicación")
            .setMessage("¿Está seguro que desea eliminar ${location.name}?")
            .setPositiveButton("Sí") { dialog, _ ->
                dbHelper.deleteLocation(location.id)
                loadLocations()
                Toast.makeText(this, "Ubicación eliminada exitosamente", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun openDetails(location: Location) {
        val intent = Intent(this, LocationDetailsActivity::class.java).apply {
            putExtra("locationId", location.id)
            putExtra("locationName", location.name)
        }
        startActivity(intent)
    }
}