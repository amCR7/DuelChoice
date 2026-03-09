package com.miapp.duelchoice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class PreguntaFragment extends Fragment {

    private TextView tvPregunta;
    private TextView tvCategoria;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pregunta, container, false);
        tvPregunta = view.findViewById(R.id.tv_pregunta);
        tvCategoria = view.findViewById(R.id.tv_categoria);
        return view;
    }

    public void actualizarPregunta(String texto) {
        if (tvPregunta != null) {
            tvPregunta.setText(texto);
        }
    }
    public void actualizarCategoria(String nombreCategoria) {
        if (tvCategoria != null) {
            tvCategoria.setText(getString(R.string.categoria) + " " + nombreCategoria);
        }
    }
}