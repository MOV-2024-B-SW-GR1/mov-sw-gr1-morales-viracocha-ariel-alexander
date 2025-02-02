package com.example.examen2

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class LocationDetailsActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var tvLocationName: TextView
    private lateinit var tvNoDetails: TextView
    private var locationId: Int = 0
    private var locationName: String = ""
    private lateinit var details: ArrayList<LocationDetail>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_details)

        // Obtener datos de la ubicación
        locationId = intent.getIntExtra("locationId", -1)
        locationName = intent.getStringExtra("locationName") ?: "Ubicación"

        if (locationId == -1) {
            Toast.makeText(this, "Error al cargar la ubicación", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inicializar vistas
        dbHelper = DatabaseHelper(this)
        listView = findViewById(R.id.detailsListView)
        tvLocationName = findViewById(R.id.tvLocationName)
        tvNoDetails = findViewById(R.id.tvNoDetails)

        // Configurar título
        tvLocationName.text = locationName
        supportActionBar?.title = "Detalles de $locationName"

        // Configurar botón de agregar
        findViewById<MaterialButton>(R.id.btnAddDetail).setOnClickListener {
            showAddDetailDialog()
        }

        // Cargar detalles
        loadDetails()

        // Configurar click largo para opciones de edición/eliminación
        listView.setOnItemLongClickListener { _, view, position, _ ->
            showDetailOptions(view, details[position])
            true
        }
    }

    private fun loadDetails() {
        details = dbHelper.getDetailsForLocation(locationId)

        if (details.isEmpty()) {
            listView.visibility = View.GONE
            tvNoDetails.visibility = View.VISIBLE
        } else {
            listView.visibility = View.VISIBLE
            tvNoDetails.visibility = View.GONE
            listView.adapter = DetailAdapter(this, details)
        }
    }

    private fun showAddDetailDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_detail, null)

        AlertDialog.Builder(this)
            .setTitle("Agregar Nuevo Observación")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val title = dialogView.findViewById<EditText>(R.id.etDetailTitle).text.toString()
                val description = dialogView.findViewById<EditText>(R.id.etDetailDescription).text.toString()

                if (title.isNotEmpty()) {
                    val detail = LocationDetail(
                        locationId = locationId,
                        title = title,
                        description = description
                    )
                    dbHelper.insertDetail(detail)
                    loadDetails()
                } else {
                    Toast.makeText(this, "El título es requerido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDetailOptions(view: View, detail: LocationDetail) {
        PopupMenu(this, view).apply {
            menu.add("Editar")
            menu.add("Eliminar")

            setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Editar" -> editDetail(detail)
                    "Eliminar" -> deleteDetail(detail)
                }
                true
            }
            show()
        }
    }

    private fun editDetail(detail: LocationDetail) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_detail, null)

        // Pre-poblar campos
        dialogView.findViewById<EditText>(R.id.etDetailTitle).setText(detail.title)
        dialogView.findViewById<EditText>(R.id.etDetailDescription).setText(detail.description)

        AlertDialog.Builder(this)
            .setTitle("Editar Detalle")
            .setView(dialogView)
            .setPositiveButton("Actualizar") { _, _ ->
                val newTitle = dialogView.findViewById<EditText>(R.id.etDetailTitle).text.toString()
                val newDescription = dialogView.findViewById<EditText>(R.id.etDetailDescription).text.toString()

                if (newTitle.isNotEmpty()) {
                    val updatedDetail = LocationDetail(
                        id = detail.id,
                        locationId = locationId,
                        title = newTitle,
                        description = newDescription
                    )
                    dbHelper.updateDetail(updatedDetail)
                    loadDetails()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteDetail(detail: LocationDetail) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Observación")
            .setMessage("¿Está seguro que desea eliminar esta observación?")
            .setPositiveButton("Sí") { _, _ ->
                dbHelper.deleteDetail(detail.id)
                loadDetails()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private class DetailAdapter(
        private val context: Context,
        private val details: List<LocationDetail>
    ) : BaseAdapter() {

        override fun getCount(): Int = details.size
        override fun getItem(position: Int): LocationDetail = details[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.detail_item, parent, false)

            val detail = getItem(position)
            view.findViewById<TextView>(R.id.tvDetailTitle).text = detail.title
            view.findViewById<TextView>(R.id.tvDetailDescription).text =
                detail.description ?: "Sin descripción"

            return view
        }
    }
}