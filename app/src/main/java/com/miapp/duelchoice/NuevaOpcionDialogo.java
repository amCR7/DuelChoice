package com.miapp.duelchoice;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.miapp.duelchoice.database.AppDatabase;
import com.miapp.duelchoice.database.Categoria;
import com.miapp.duelchoice.database.Opcion;

public class NuevaOpcionDialogo extends DialogFragment {

    private AppDatabase db;
    private Categoria categoria;
    private OnOpcionCreadaListener listener;

    public interface OnOpcionCreadaListener {
        void onOpcionCreada();
    }

    public NuevaOpcionDialogo(Categoria categoria, OnOpcionCreadaListener listener) {
        this.categoria = categoria;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        db = AppDatabase.getInstance(requireContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Diapositiva 8: Diseño personalizado
        View view = inflater.inflate(R.layout.dialog_nueva_opcion, null);
        builder.setView(view);

        EditText etOpcion = view.findViewById(R.id.etOpcion);
        Button btnGuardar = view.findViewById(R.id.btnGuardar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        // Configurar título
        builder.setTitle("Nueva opción para: " + categoria.getNombre());

        // Crear el diálogo
        AlertDialog dialog = builder.create();

        // Listeners para los botones
        btnGuardar.setOnClickListener(v -> {
            String textoOpcion = etOpcion.getText().toString().trim();

            if (textoOpcion.isEmpty()) {
                Toast.makeText(getContext(), "Escribe una opción", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear nueva opción con ELO inicial = 1500
            Opcion nuevaOpcion = new Opcion(textoOpcion, R.drawable.imagen_opcion_a, categoria.getId());
            db.opcionDao().insert(nuevaOpcion);

            Toast.makeText(getContext(), "Opción añadida", Toast.LENGTH_SHORT).show();

            if (listener != null) {
                listener.onOpcionCreada();
            }

            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}