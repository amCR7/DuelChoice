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

public class NuevaCategoriaDialog extends DialogFragment {

    private AppDatabase db;
    private OnCategoriaCreadaListener listener;

    public interface OnCategoriaCreadaListener {
        void onCategoriaCreada(Categoria categoria);
    }

    public NuevaCategoriaDialog(OnCategoriaCreadaListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        db = AppDatabase.getInstance(requireContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_nueva_categoria, null);
        builder.setView(view);

        EditText etNombre = view.findViewById(R.id.etNombre);
        EditText etDescripcion = view.findViewById(R.id.etDescripcion);
        Button btnGuardar = view.findViewById(R.id.btnGuardar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        builder.setTitle("Nueva Categoría");

        AlertDialog dialog = builder.create();

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();

            if (nombre.isEmpty()) {
                Toast.makeText(getContext(), "Escribe un nombre", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear nueva categoría (icono por defecto)
            Categoria nuevaCategoria = new Categoria(nombre, descripcion, R.drawable.ic_launcher_foreground);
            long id = db.categoriaDao().insert(nuevaCategoria);

            // Obtener la categoría con su ID asignado
            nuevaCategoria.setId((int) id);

            Toast.makeText(getContext(), "Categoría creada", Toast.LENGTH_SHORT).show();

            if (listener != null) {
                listener.onCategoriaCreada(nuevaCategoria);
            }

            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}