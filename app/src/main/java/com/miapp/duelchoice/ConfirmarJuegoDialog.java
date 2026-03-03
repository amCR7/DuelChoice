package com.miapp.duelchoice;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.miapp.duelchoice.database.Categoria;

public class ConfirmarJuegoDialog extends DialogFragment {

    private Categoria categoria;
    private OnConfirmacionListener listener;

    public interface OnConfirmacionListener {
        void onConfirmar();
        void onCancelar();
    }

    public ConfirmarJuegoDialog(Categoria categoria, OnConfirmacionListener listener) {
        this.categoria = categoria;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_confirmar_juego, null);
        builder.setView(view);

        TextView tvMensaje = view.findViewById(R.id.tvMensaje);
        Button btnSi = view.findViewById(R.id.btnSi);
        Button btnNo = view.findViewById(R.id.btnNo);

        tvMensaje.setText("¿Seguro que quieres jugar a la categoría\n\"" + categoria.getNombre() + "\"?");

        AlertDialog dialog = builder.create();

        btnSi.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmar();
            }
            dialog.dismiss();
        });

        btnNo.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelar();
            }
            dialog.dismiss();
        });

        return dialog;
    }
}