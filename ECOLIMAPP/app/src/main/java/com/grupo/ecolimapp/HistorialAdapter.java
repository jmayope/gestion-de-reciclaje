package com.grupo.ecolimapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(RegistroItem registro);
    }

    private final List<RegistroItem> lista;
    private final OnItemClickListener listener;

    public HistorialAdapter(List<RegistroItem> lista, OnItemClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial_registro, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistroItem item = lista.get(position);

        holder.tvTituloItem.setText("Registro #" + item.getIdRegistro());
        holder.tvCantidadItem.setText("Cantidad: " + item.getCantidad() + " " + item.getUnidad());
        holder.tvResiduoItem.setText("Residuo ID: " + item.getIdResiduo());
        holder.tvFechaItem.setText("Fecha: " + item.getFecha());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTituloItem, tvCantidadItem, tvResiduoItem, tvFechaItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTituloItem = itemView.findViewById(R.id.tvTituloItem);
            tvCantidadItem = itemView.findViewById(R.id.tvCantidadItem);
            tvResiduoItem = itemView.findViewById(R.id.tvResiduoItem);
            tvFechaItem = itemView.findViewById(R.id.tvFechaItem);
        }
    }
}
