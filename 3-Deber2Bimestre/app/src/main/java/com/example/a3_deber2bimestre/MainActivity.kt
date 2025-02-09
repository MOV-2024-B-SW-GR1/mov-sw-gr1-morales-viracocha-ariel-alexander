package com.example.a3_deber2bimestre

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerViewParcelas: RecyclerView
    private lateinit var txtEmptyParcelas: TextView
    private lateinit var txtEmptyAlarmas: TextView
    private lateinit var btnCrearParcela: Button
    private lateinit var adapter: ParcelaAdapter
    private lateinit var recyclerViewAlarmas: RecyclerView
    private lateinit var btnCrearAlarma: Button
    private lateinit var adapterAlarmas: AlarmaAdapter

    private val dbHelper by lazy { DBHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupRecyclerView()
        setupListeners()
        cargarParcelas()
        cargarAlarmas()
        actualizarVistaVacia()
    }

    private fun initializeViews() {
        recyclerViewParcelas = findViewById(R.id.recyclerViewParcelas)
        recyclerViewAlarmas = findViewById(R.id.recyclerViewAlarmas)
        btnCrearAlarma = findViewById(R.id.btnCrearAlarma)
        btnCrearParcela = findViewById(R.id.btnCrearParcela)

        // Nuevas referencias para los estados vacíos
        txtEmptyParcelas = findViewById(R.id.txtEmptyParcelas)
        txtEmptyAlarmas = findViewById(R.id.txtEmptyAlarmas)
    }


    private fun setupRecyclerView() {
        adapter = ParcelaAdapter(mutableListOf()) { parcela, view ->
            mostrarMenuParcela(parcela, view)
        }
        adapterAlarmas = AlarmaAdapter(mutableListOf()) { alarma, view ->
            mostrarMenuAlarma(alarma, view)
        }

        recyclerViewAlarmas.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapterAlarmas
        }

        recyclerViewParcelas.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        // Verificar estado vacío después de inicializar los adapters
        actualizarVistaVacia()
    }

    private fun actualizarVistaVacia() {
        // Para Parcelas
        if (adapter.itemCount == 0) {
            txtEmptyParcelas.visibility = View.VISIBLE
            recyclerViewParcelas.visibility = View.GONE
        } else {
            txtEmptyParcelas.visibility = View.GONE
            recyclerViewParcelas.visibility = View.VISIBLE
        }

        // Para Alarmas
        if (adapterAlarmas.itemCount == 0) {
            txtEmptyAlarmas.visibility = View.VISIBLE
            recyclerViewAlarmas.visibility = View.GONE
        } else {
            txtEmptyAlarmas.visibility = View.GONE
            recyclerViewAlarmas.visibility = View.VISIBLE
        }
    }


    private fun setupListeners() {
        btnCrearParcela.setOnClickListener {
            mostrarDialogoCrearParcela()
        }
        btnCrearAlarma.setOnClickListener {
            mostrarDialogoCrearAlarma()
        }
    }

    private fun cargarAlarmas() {
        val alarmas = mutableListOf<Alarma>()
        val cursor = dbHelper.obtenerAlarmas()

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(DBHelper.COLUMN_ALARMA_ID))  // Agregar id
                val hora = it.getString(it.getColumnIndexOrThrow(DBHelper.COLUMN_ALARMA_HORA))
                val motivo = it.getString(it.getColumnIndexOrThrow(DBHelper.COLUMN_ALARMA_MOTIVO))
                val ubicacion = it.getString(it.getColumnIndexOrThrow(DBHelper.COLUMN_ALARMA_UBICACION))
                alarmas.add(Alarma(id, hora, motivo, ubicacion))  // Incluir id
            }
        }
        adapterAlarmas.updateData(alarmas)
        actualizarVistaVacia()
    }


    private fun mostrarDialogoCrearAlarma() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 30, 50, 30)
        }

        val inputHora = EditText(this).apply { hint = "Hora" }
        val inputMotivo = EditText(this).apply { hint = "Motivo" }
        val inputUbicacion = EditText(this).apply { hint = "Ubicación" }

        layout.addView(inputHora)
        layout.addView(inputMotivo)
        layout.addView(inputUbicacion)

        AlertDialog.Builder(this)
            .setTitle("Nueva Alarma")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val hora = inputHora.text.toString()
                val motivo = inputMotivo.text.toString()
                val ubicacion = inputUbicacion.text.toString()

                if (hora.isNotEmpty() && motivo.isNotEmpty() && ubicacion.isNotEmpty()) {
                    val id = dbHelper.insertarAlarma(hora, motivo, ubicacion).toInt() // Convertimos el ID a Int
                    if (id != -1) { // Si la inserción fue exitosa
                        val nuevaAlarma = Alarma(id, hora, motivo, ubicacion) // Ahora pasamos el ID correctamente
                        adapterAlarmas.addAlarma(nuevaAlarma)
                        actualizarVistaVacia()
                    } else {
                        Toast.makeText(this, "Error al crear alarma", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    private fun mostrarMenuAlarma(alarma: Alarma, view: View) {
        PopupMenu(this, view).apply {
            menuInflater.inflate(R.menu.menu_alarma, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menuEditar -> {
                        editarAlarma(alarma)
                        true
                    }
                    R.id.menuEliminar -> {
                        eliminarAlarma(alarma)
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    private fun editarAlarma(alarma: Alarma) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 30, 50, 30)
        }

        val inputHora = EditText(this).apply { setText(alarma.hora) }
        val inputMotivo = EditText(this).apply { setText(alarma.motivo) }
        val inputUbicacion = EditText(this).apply { setText(alarma.ubicacion) }

        layout.addView(inputHora)
        layout.addView(inputMotivo)
        layout.addView(inputUbicacion)

        AlertDialog.Builder(this)
            .setTitle("Editar Alarma")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevaHora = inputHora.text.toString()
                val nuevoMotivo = inputMotivo.text.toString()
                val nuevaUbicacion = inputUbicacion.text.toString()

                dbHelper.actualizarAlarma(alarma.id.toString(), nuevaHora, nuevoMotivo, nuevaUbicacion) // Usar ID
                cargarAlarmas()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarAlarma(alarma: Alarma) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Alarma")
            .setMessage("¿Estás seguro de que quieres eliminar esta alarma?")
            .setPositiveButton("Sí") { _, _ ->
                dbHelper.eliminarAlarma(alarma.id.toString())  // Usar ID en lugar de hora
                cargarAlarmas()
                actualizarVistaVacia()
            }
            .setNegativeButton("No", null)
            .show()
    }


    // Métodos para Parcelas
    private fun cargarParcelas() {
        val parcelas = mutableListOf<Parcela>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(DBHelper.TABLE_PARCELA, null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PARCELA_ID))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PARCELA_NOMBRE))
            parcelas.add(Parcela(id, nombre))
        }
        cursor.close()
        adapter.updateData(parcelas)
        actualizarVistaVacia()
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
                    val id = insertarParcela(nombre)
                    if (id != -1L) {
                        val nuevaParcela = Parcela(id.toInt(), nombre)
                        adapter.addParcela(nuevaParcela)
                        actualizarVistaVacia()
                    } else {
                        Toast.makeText(this, "Error al crear parcela", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun insertarParcela(nombre: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COLUMN_PARCELA_NOMBRE, nombre)
        }
        return db.insert(DBHelper.TABLE_PARCELA, null, values)
        actualizarVistaVacia()
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
                    val actualizado = actualizarParcela(parcela.id, nuevoNombre)
                    if (actualizado) {
                        parcela.nombre = nuevoNombre
                        adapter.notifyDataSetChanged()
                        actualizarVistaVacia()
                    } else {
                        Toast.makeText(this, "Error al actualizar parcela", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarParcela(id: Int, nuevoNombre: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COLUMN_PARCELA_NOMBRE, nuevoNombre)
        }
        val rowsAffected = db.update(DBHelper.TABLE_PARCELA, values, "${DBHelper.COLUMN_PARCELA_ID} = ?", arrayOf(id.toString()))
        return rowsAffected > 0
        actualizarVistaVacia()
    }

    private fun eliminarParcela(parcela: Parcela) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Parcela")
            .setMessage("¿Estás seguro de que quieres eliminar esta parcela?")
            .setPositiveButton("Sí") { _, _ ->
                val eliminada = eliminarParcelaDeSQLite(parcela.id)
                if (eliminada) {
                    adapter.removeParcela(parcela)
                    actualizarVistaVacia()
                } else {
                    Toast.makeText(this, "Error al eliminar parcela", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun eliminarParcelaDeSQLite(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val rowsAffected = db.delete(DBHelper.TABLE_PARCELA, "${DBHelper.COLUMN_PARCELA_ID} = ?", arrayOf(id.toString()))
        return rowsAffected > 0
    }

    private fun verPlantas(parcela: Parcela) {
        val intent = Intent(this, PlantasActivity::class.java).apply {
            putExtra("PARCELA_ID", parcela.id)
            putExtra("PARCELA_NOMBRE", parcela.nombre)
        }
        startActivity(intent)
    }
}