package com.example.examen2

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

private fun LocationDetailsActivity.showDetailMenu(
    view: View,
    detail: LocationDetail
) {
}

class LocationDetailsActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var locationNameTextView: TextView
    private var locationId: Int = 0
    private var locationName: String = ""
    private lateinit var details: ArrayList<LocationDetail>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_details)

        locationId = intent.getIntExtra("locationId", -1)
        locationName = intent.getStringExtra("locationName") ?: "Ubicación"

        if (locationId == -1) {
            finish()
            return
        }

        dbHelper = DatabaseHelper(this)
        listView = findViewById(R.id.detailsListView)
        locationNameTextView = findViewById(R.id.tvLocationName)
        locationNameTextView.text = locationName

        findViewById<FloatingActionButton>(R.id.fabAddDetail).setOnClickListener {
            showAddDetailDialog()
        }

        loadDetails()

        listView.setOnItemLongClickListener { _, view, position, _ ->
            showDetailMenu(view, details[position])
            true
        }
    }

    private fun loadDetails() {
        details = dbHelper.getDetailsForLocation(locationId)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            details.map { it.title }
        )
        listView.adapter = adapter
    }

    private fun showAddDetailDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_detail, null)

        AlertDialog.Builder(this)
            .setTitle("Agregar Nuevo Detalle")
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val title = dialogView.findViewById<EditText>(R.id.etDetailTitle).text.toString()
                val description =
                    dialogView.findViewById<EditText>(R.id.etDetailDescription).text.toString()

                if (title.isNotEmpty()) {
                    val detail = LocationDetail(
                        locationId = locationId,
                        title = title,
                        description = description
                    )
                    dbHelper.insertDetail(detail)
                    loadDetails()
                    Toast.makeText(this, "Detalle agregado exitosamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Por favor ingrese un título", Toast.LENGTH_SHORT).show()
                }
            }
    }
}