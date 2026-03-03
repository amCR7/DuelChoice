package com.miapp.duelchoice;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoriaViewHolder extends RecyclerView.ViewHolder {

    public ImageView ivIcono;
    public TextView tvNombre;
    public Button btnJugar;  // Solo estos tres campos

    public CategoriaViewHolder(@NonNull View itemView) {
        super(itemView);
        ivIcono = itemView.findViewById(R.id.ivIcono);
        tvNombre = itemView.findViewById(R.id.tvNombre);
        btnJugar = itemView.findViewById(R.id.btnJugar);
        // ELIMINADO: tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
    }
}