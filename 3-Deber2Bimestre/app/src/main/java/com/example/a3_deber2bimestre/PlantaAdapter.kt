package com.example.a3_deber2bimestre

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlantaAdapter(
    private val plantas: List<Planta>,
    private val onItemClick: (Planta, View) -> Unit
) : RecyclerView.Adapter<PlantaAdapter.PlantaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_planta, parent, false)
        return PlantaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantaViewHolder, position: Int) {
        val planta = plantas[position]
        holder.bind(planta)
        holder.itemView.setOnClickListener { view ->
            onItemClick(planta, view)
        }
    }

    override fun getItemCount() = plantas.size

    class PlantaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtEspecie: TextView = itemView.findViewById(R.id.txtEspecie)
        private val txtDetalles: TextView = itemView.findViewById(R.id.txtDetalles)

        fun bind(planta: Planta) {
            txtEspecie.text = planta.especie
            txtDetalles.text = "Edad: ${planta.edad} años • Color: ${planta.color} • Altura: ${planta.altura}m"
        }
    }
}