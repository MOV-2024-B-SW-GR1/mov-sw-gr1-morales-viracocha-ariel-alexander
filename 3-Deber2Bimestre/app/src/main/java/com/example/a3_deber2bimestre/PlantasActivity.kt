package com.example.a3_deber2bimestre

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.PopupMenu

class PlantasActivity : AppCompatActivity() {
    private lateinit var recyclerViewPlantas: RecyclerView
    private lateinit var txtEmptyPlantas: TextView
    private lateinit var btnCrearPlanta: Button
    private val plantas = mutableListOf<Planta>()
    private lateinit var adapter: PlantaAdapter
    private var parcelaId: Int = -1
    private var nombreParcela: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plantas)

        // Recuperar datos de la parcela
        parcelaId = intent.getIntExtra("PARCELA_ID", -1)
        nombreParcela = intent.getStringExtra("PARCELA_NOMBRE") ?: ""

        title = "Plantas de $nombreParcela"

        initializeViews()
        setupRecyclerView()
        setupListeners()
        actualizarVistaVacia()
    }

    private fun initializeViews() {
        recyclerViewPlantas = findViewById(R.id.recyclerViewPlantas)
        txtEmptyPlantas = findViewById(R.id.txtEmptyPlantas)
        btnCrearPlanta = findViewById(R.id.btnCrearPlanta)
    }

    private fun setupRecyclerView() {
        adapter = PlantaAdapter(plantas) { planta, view ->
            mostrarMenuPlanta(planta, view)
        }
        recyclerViewPlantas.apply {
            layoutManager = LinearLayoutManager(this@PlantasActivity)
            adapter = this@PlantasActivity.adapter
        }
    }

    private fun setupListeners() {
        btnCrearPlanta.setOnClickListener {
            mostrarDialogoCrearPlanta()
        }
    }

    private fun actualizarVistaVacia() {
        if (plantas.isEmpty()) {
            txtEmptyPlantas.visibility = View.VISIBLE
            recyclerViewPlantas.visibility = View.GONE
        } else {
            txtEmptyPlantas.visibility = View.GONE
            recyclerViewPlantas.visibility = View.VISIBLE
        }
    }

    private fun mostrarDialogoCrearPlanta() {
        val dialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_crear_planta, null)

        dialog.setView(view)
            .setTitle("Nueva Planta")
            .setPositiveButton("Crear") { _, _ ->
                val especie = view.findViewById<EditText>(R.id.etEspecie).text.toString()
                val edad = view.findViewById<EditText>(R.id.etEdad).text.toString().toIntOrNull() ?: 0
                val color = view.findViewById<EditText>(R.id.etColor).text.toString()
                val altura = view.findViewById<EditText>(R.id.etAltura).text.toString().toDoubleOrNull() ?: 0.0

                if (especie.isNotEmpty() && color.isNotEmpty()) {
                    val planta = Planta(
                        id = plantas.size + 1,
                        especie = especie,
                        edad = edad,
                        color = color,
                        altura = altura,
                        parcelaId = parcelaId
                    )
                    plantas.add(planta)
                    adapter.notifyDataSetChanged()
                    actualizarVistaVacia()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarMenuPlanta(planta: Planta, view: View) {
        PopupMenu(this, view).apply {
            menuInflater.inflate(R.menu.menu_planta, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menuEditarPlanta -> {
                        editarPlanta(planta)
                        true
                    }
                    R.id.menuEliminarPlanta -> {
                        eliminarPlanta(planta)
                        true
                    }
                    R.id.menuVerDetalles -> {
                        mostrarDetallesPlanta(planta)
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    private fun editarPlanta(planta: Planta) {
        val view = layoutInflater.inflate(R.layout.dialog_crear_planta, null)

        // Pre-llenar los campos
        view.findViewById<EditText>(R.id.etEspecie).setText(planta.especie)
        view.findViewById<EditText>(R.id.etEdad).setText(planta.edad.toString())
        view.findViewById<EditText>(R.id.etColor).setText(planta.color)
        view.findViewById<EditText>(R.id.etAltura).setText(planta.altura.toString())

        AlertDialog.Builder(this)
            .setTitle("Editar Planta")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                // Actualizar datos de la planta
                planta.especie = view.findViewById<EditText>(R.id.etEspecie).text.toString()
                planta.edad = view.findViewById<EditText>(R.id.etEdad).text.toString().toIntOrNull() ?: planta.edad
                planta.color = view.findViewById<EditText>(R.id.etColor).text.toString()
                planta.altura = view.findViewById<EditText>(R.id.etAltura).text.toString().toDoubleOrNull() ?: planta.altura
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarPlanta(planta: Planta) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Planta")
            .setMessage("¿Estás seguro de que quieres eliminar esta planta?")
            .setPositiveButton("Sí") { _, _ ->
                plantas.remove(planta)
                adapter.notifyDataSetChanged()
                actualizarVistaVacia()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun mostrarDetallesPlanta(planta: Planta) {
        AlertDialog.Builder(this)
            .setTitle(planta.especie)
            .setMessage("""
                Especie: ${planta.especie}
                Edad: ${planta.edad} años
                Color: ${planta.color}
                Altura: ${planta.altura} metros
            """.trimIndent())
            .setPositiveButton("Cerrar", null)
            .show()
    }
}