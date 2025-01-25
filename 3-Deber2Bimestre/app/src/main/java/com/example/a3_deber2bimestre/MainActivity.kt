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
    private lateinit var txtEmpty: TextView
    private lateinit var btnCrearParcela: Button
    private lateinit var adapter: ParcelaAdapter

    private val dbHelper by lazy { DBHelper(this) } // Inicializa el helper de la base de datos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupRecyclerView()
        setupListeners()
        cargarParcelas() // Cargar parcelas desde SQLite
        actualizarVistaVacia()
    }

    private fun initializeViews() {
        recyclerViewParcelas = findViewById(R.id.recyclerViewParcelas)
        txtEmpty = findViewById(R.id.txtEmpty)
        btnCrearParcela = findViewById(R.id.btnCrearParcela)
    }

    private fun setupRecyclerView() {
        adapter = ParcelaAdapter(mutableListOf()) { parcela, view ->
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
        adapter.updateData(parcelas) // Actualiza el adaptador con los datos de SQLite
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

    private fun actualizarVistaVacia() {
        if (adapter.itemCount == 0) {
            txtEmpty.visibility = View.VISIBLE
            recyclerViewParcelas.visibility = View.GONE
        } else {
            txtEmpty.visibility = View.GONE
            recyclerViewParcelas.visibility = View.VISIBLE
        }
    }
}
