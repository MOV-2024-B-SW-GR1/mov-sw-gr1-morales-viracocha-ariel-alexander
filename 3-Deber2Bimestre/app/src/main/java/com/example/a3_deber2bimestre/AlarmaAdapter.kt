package com.example.a3_deber2bimestre

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlarmaAdapter(
    private var alarmas: MutableList<Alarma>,
    private val onItemClick: (Alarma, View) -> Unit
) : RecyclerView.Adapter<AlarmaAdapter.AlarmaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alarma, parent, false)
        return AlarmaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmaViewHolder, position: Int) {
        val alarma = alarmas[position]
        holder.bind(alarma)
        holder.itemView.setOnClickListener { onItemClick(alarma, it) }
    }

    override fun getItemCount(): Int = alarmas.size

    fun updateData(newAlarmas: List<Alarma>) {
        alarmas.clear()
        alarmas.addAll(newAlarmas)
        notifyDataSetChanged()
    }

    fun addAlarma(alarma: Alarma) {
        alarmas.add(alarma)
        notifyItemInserted(alarmas.size - 1)
    }

    fun removeAlarma(alarma: Alarma) {
        val position = alarmas.indexOfFirst { it.id == alarma.id } // Buscar por ID
        if (position != -1) {
            alarmas.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    class AlarmaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtHora: TextView = itemView.findViewById(R.id.txtHora)
        private val txtMotivo: TextView = itemView.findViewById(R.id.txtMotivo)
        private val txtUbicacion: TextView = itemView.findViewById(R.id.txtUbicacion)

        fun bind(alarma: Alarma) {
            txtHora.text = alarma.hora
            txtMotivo.text = alarma.motivo
            txtUbicacion.text = alarma.ubicacion
        }
    }
}
