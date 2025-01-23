package com.example.a3_deber2bimestre

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.PopupMenu

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerViewParcelas: RecyclerView
    private lateinit var txtEmpty: TextView
    private lateinit var btnCrearParcela: Button
    private val parcelas = mutableListOf<Parcela>()
    private lateinit var adapter: ParcelaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupRecyclerView()
        setupListeners()
        actualizarVistaVacia()
    }

    private fun initializeViews() {
        recyclerViewParcelas = findViewById(R.id.recyclerViewParcelas)
        txtEmpty = findViewById(R.id.txtEmpty)
        btnCrearParcela = findViewById(R.id.btnCrearParcela)
    }

    private fun setupRecyclerView() {
        adapter = ParcelaAdapter(parcelas) { parcela, view ->
            mostrarMenuParcela(parcela, view)
        }
        recyclerViewParcelas.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupListeners() {
        btnCrearParcela.setOnClickListener {
            mostrarDialogoCrearParcela()
        }
    }

    private fun actualizarVistaVacia() {
        if (parcelas.isEmpty()) {
            txtEmpty.visibility = View.VISIBLE
            recyclerViewParcelas.visibility = View.GONE
        } else {
            txtEmpty.visibility = View.GONE
            recyclerViewParcelas.visibility = View.VISIBLE
        }
    }

    private fun mostrarMenuParcela(parcela: Parcela, view: View) {
        PopupMenu(this, view).apply {
            menuInflater.inflate(R.menu.manu_parcela, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menuEditar -> {
                        editarParcela(parcela)
                        true
                    }
                    R.id.menuEliminar -> {
                        eliminarParcela(parcela)
                        true
                    }
                    R.id.menuVerPlantas -> {
                        verPlantas(parcela)
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    private fun mostrarDialogoCrearParcela() {
        val input = EditText(this).apply {
            setPadding(50, 30, 50, 30)
        }

        AlertDialog.Builder(this)
            .setTitle("Nueva Parcela")
            .setView(input)
            .setPositiveButton("Crear") { _, _ ->
                val nombre = input.text.toString()
                if (nombre.isNotEmpty()) {
                    parcelas.add(Parcela(parcelas.size + 1, nombre))
                    adapter.notifyDataSetChanged()
                    actualizarVistaVacia()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun editarParcela(parcela: Parcela) {
        val input = EditText(this).apply {
            setText(parcela.nombre)
            setPadding(50, 30, 50, 30)
        }

        AlertDialog.Builder(this)
            .setTitle("Editar Parcela")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoNombre = input.text.toString()
                if (nuevoNombre.isNotEmpty()) {
                    parcela.nombre = nuevoNombre
                    adapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarParcela(parcela: Parcela) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Parcela")
            .setMessage("¿Estás seguro de que quieres eliminar esta parcela?")
            .setPositiveButton("Sí") { _, _ ->
                parcelas.remove(parcela)
                adapter.notifyDataSetChanged()
                actualizarVistaVacia()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun verPlantas(parcela: Parcela) {
        val intent = Intent(this, PlantasActivity::class.java).apply {
            putExtra("PARCELA_ID", parcela.id)
            putExtra("PARCELA_NOMBRE", parcela.nombre)
        }
        startActivity(intent)
    }
}