package com.example.a3_deber2bimestre;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ParcelaAdapter extends RecyclerView.Adapter<ParcelaAdapter.ParcelaViewHolder> {

    private List<String> parcelas;
    private OnParcelaClickListener listener;

    public ParcelaAdapter(List<String> parcelas, OnParcelaClickListener listener) {
        this.parcelas = parcelas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ParcelaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parcela, parent, false);
        return new ParcelaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParcelaViewHolder holder, int position) {
        String parcela = parcelas.get(position);
        holder.bind(parcela);
    }

    @Override
    public int getItemCount() {
        return parcelas.size();
    }

    public class ParcelaViewHolder extends RecyclerView.ViewHolder {

        private TextView parcelaName;
        private ImageButton optionsButton;

        public ParcelaViewHolder(@NonNull View itemView) {
            super(itemView);
            parcelaName = itemView.findViewById(R.id.txtParcela);
            optionsButton = itemView.findViewById(R.id.btnOptions);

            // Listener para el botón de opciones
            optionsButton.setOnClickListener(v -> listener.onOptionsClick(getAdapterPosition(), itemView));
        }

        public void bind(String parcela) {
            parcelaName.setText(parcela);
        }
    }

    // Interface para gestionar los clics en opciones
    public interface OnParcelaClickListener {
        void onOptionsClick(int position, View view);
    }
}
