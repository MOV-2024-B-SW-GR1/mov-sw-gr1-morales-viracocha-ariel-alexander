package com.example.a3_deber2bimestre

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ParcelaAdapter(
    private val parcelas: List<Parcela>,
    private val onItemClick: (Parcela, View) -> Unit
) : RecyclerView.Adapter<ParcelaAdapter.ParcelaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParcelaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parcela, parent, false)
        return ParcelaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParcelaViewHolder, position: Int) {
        val parcela = parcelas[position]
        holder.bind(parcela)
        holder.itemView.setOnClickListener { view ->
            onItemClick(parcela, view)
        }
    }

    override fun getItemCount() = parcelas.size

    class ParcelaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtNombreParcela: TextView = itemView.findViewById(R.id.txtNombreParcela)

        fun bind(parcela: Parcela) {
            txtNombreParcela.text = parcela.nombre
        }
    }
}