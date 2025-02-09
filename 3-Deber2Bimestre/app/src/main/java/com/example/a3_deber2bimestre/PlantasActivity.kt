package com.example.a3_deber2bimestre

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PlantasActivity : AppCompatActivity() {
    private lateinit var recyclerViewPlantas: RecyclerView
    private lateinit var txtEmptyPlantas: TextView
    private lateinit var btnCrearPlanta: Button
    private lateinit var adapter: PlantaAdapter

    private val dbHelper by lazy { DBHelper(this) }
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
        cargarPlantas() // Cargar plantas desde SQLite
        actualizarVistaVacia()
    }

    private fun initializeViews() {
        recyclerViewPlantas = findViewById(R.id.recyclerViewPlantas)
        txtEmptyPlantas = findViewById(R.id.txtEmptyPlantas)
        btnCrearPlanta = findViewById(R.id.btnCrearPlanta)
    }

    private fun setupRecyclerView() {
        adapter = PlantaAdapter(mutableListOf()) { planta, view ->
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

    private fun cargarPlantas() {
        val plantas = mutableListOf<Planta>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DBHelper.TABLE_PLANTA,
            null,
            "${DBHelper.COLUMN_PLANTA_PARCELA_ID} = ?",
            arrayOf(parcelaId.toString()),
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLANTA_ID))
            val especie = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLANTA_ESPECIE))
            val edad = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLANTA_EDAD))
            val color = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLANTA_COLOR))
            val altura = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLANTA_ALTURA))
            val plantaParcelaId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLANTA_PARCELA_ID))

            plantas.add(Planta(id, especie, edad, color, altura, plantaParcelaId))
        }
        cursor.close()
        adapter.updateData(plantas) // Actualiza el adaptador con los datos de SQLite
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
                    val id = insertarPlanta(especie, edad, color, altura)
                    if (id != -1L) {
                        val nuevaPlanta = Planta(id.toInt(), especie, edad, color, altura, parcelaId)
                        adapter.addPlanta(nuevaPlanta)
                        actualizarVistaVacia()
                    } else {
                        Toast.makeText(this, "Error al crear planta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun insertarPlanta(especie: String, edad: Int, color: String, altura: Double): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COLUMN_PLANTA_ESPECIE, especie)
            put(DBHelper.COLUMN_PLANTA_EDAD, edad)
            put(DBHelper.COLUMN_PLANTA_COLOR, color)
            put(DBHelper.COLUMN_PLANTA_ALTURA, altura)
            put(DBHelper.COLUMN_PLANTA_PARCELA_ID, parcelaId)
        }
        return db.insert(DBHelper.TABLE_PLANTA, null, values)
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
                    else -> false
                }
            }
            show()
        }
    }

    private fun editarPlanta(planta: Planta) {
        val view = layoutInflater.inflate(R.layout.dialog_crear_planta, null)

        view.findViewById<EditText>(R.id.etEspecie).setText(planta.especie)
        view.findViewById<EditText>(R.id.etEdad).setText(planta.edad.toString())
        view.findViewById<EditText>(R.id.etColor).setText(planta.color)
        view.findViewById<EditText>(R.id.etAltura).setText(planta.altura.toString())

        AlertDialog.Builder(this)
            .setTitle("Editar Planta")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevaEspecie = view.findViewById<EditText>(R.id.etEspecie).text.toString()
                val nuevaEdad = view.findViewById<EditText>(R.id.etEdad).text.toString().toIntOrNull() ?: planta.edad
                val nuevoColor = view.findViewById<EditText>(R.id.etColor).text.toString()
                val nuevaAltura = view.findViewById<EditText>(R.id.etAltura).text.toString().toDoubleOrNull() ?: planta.altura

                val actualizado = actualizarPlanta(planta.id, nuevaEspecie, nuevaEdad, nuevoColor, nuevaAltura)
                if (actualizado) {
                    planta.especie = nuevaEspecie
                    planta.edad = nuevaEdad
                    planta.color = nuevoColor
                    planta.altura = nuevaAltura
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Error al actualizar planta", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarPlanta(id: Int, especie: String, edad: Int, color: String, altura: Double): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COLUMN_PLANTA_ESPECIE, especie)
            put(DBHelper.COLUMN_PLANTA_EDAD, edad)
            put(DBHelper.COLUMN_PLANTA_COLOR, color)
            put(DBHelper.COLUMN_PLANTA_ALTURA, altura)
        }
        val rowsAffected = db.update(DBHelper.TABLE_PLANTA, values, "${DBHelper.COLUMN_PLANTA_ID} = ?", arrayOf(id.toString()))
        return rowsAffected > 0
    }

    private fun eliminarPlanta(planta: Planta) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Planta")
            .setMessage("¿Estás seguro de que quieres eliminar esta planta?")
            .setPositiveButton("Sí") { _, _ ->
                val eliminada = eliminarPlantaDeSQLite(planta.id)
                if (eliminada) {
                    adapter.removePlanta(planta)
                    actualizarVistaVacia()
                } else {
                    Toast.makeText(this, "Error al eliminar planta", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun eliminarPlantaDeSQLite(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val rowsAffected = db.delete(DBHelper.TABLE_PLANTA, "${DBHelper.COLUMN_PLANTA_ID} = ?", arrayOf(id.toString()))
        return rowsAffected > 0
    }

    private fun actualizarVistaVacia() {
        if (adapter.itemCount == 0) {
            txtEmptyPlantas.visibility = View.VISIBLE
            recyclerViewPlantas.visibility = View.GONE
        } else {
            txtEmptyPlantas.visibility = View.GONE
            recyclerViewPlantas.visibility = View.VISIBLE
        }
    }
}
