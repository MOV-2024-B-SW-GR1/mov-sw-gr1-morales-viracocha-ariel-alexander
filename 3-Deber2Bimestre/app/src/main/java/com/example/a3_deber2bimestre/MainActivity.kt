package com.example.a3_deber2bimestre

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var parcelaAdapter: ParcelaAdapter
    private val parcelasList = mutableListOf<String>() // Lista de parcelas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Ajustes de ventana
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewParcelas)
        parcelaAdapter = ParcelaAdapter(parcelasList, this::onParcelaClicked)
        recyclerView.adapter = parcelaAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar botón Crear Parcela
        val buttonCrear: FloatingActionButton = findViewById(R.id.buttonCrear)
        buttonCrear.setOnClickListener {
            crearNuevaParcela()
        }
    }

    // Método para crear una nueva parcela
    private fun crearNuevaParcela() {
        val nuevaParcela = "Parcela ${parcelasList.size + 1}"
        parcelasList.add(nuevaParcela)
        parcelaAdapter.notifyItemInserted(parcelasList.size - 1)
        Toast.makeText(this, "Parcela $nuevaParcela creada", Toast.LENGTH_SHORT).show()
    }

    // Método cuando se da clic en una parcela
    private fun onParcelaClicked(parcela: String) {
        Toast.makeText(this, "Clic en $parcela", Toast.LENGTH_SHORT).show()
        // Aquí puedes mostrar el menú con opciones de Editar, Eliminar o Ver plantas
        mostrarMenuOpciones(parcela)
    }

    // Método para mostrar el menú de opciones
    private fun mostrarMenuOpciones(parcela: String) {
        // Implementa aquí tu lógica para mostrar el menú de opciones para Editar/Eliminar/Ver Plantas
        Toast.makeText(this, "Mostrar opciones para $parcela", Toast.LENGTH_SHORT).show()
    }
}
