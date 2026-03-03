package com.miapp.duelchoice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.miapp.duelchoice.database.Opcion;

public class OpcionFragment extends Fragment implements View.OnClickListener {

    private ImageView ivImagen;
    private TextView tvNombre;
    private TextView tvNumero;  // Para mostrar 1️⃣ o 2️⃣
    private Opcion opcion;
    private OnOpcionClickListener listener;
    private View rootView;
    private int posicion; // 1 para arriba, 2 para abajo

    public interface OnOpcionClickListener {
        void onOpcionClick(Opcion opcion);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_opcion, container, false);

        ivImagen = rootView.findViewById(R.id.iv_imagen);
        tvNombre = rootView.findViewById(R.id.tv_nombre);
        tvNumero = rootView.findViewById(R.id.tv_numero);

        rootView.setOnClickListener(this);

        // Obtener la posición del fragment (1 o 2)
        Bundle args = getArguments();
        if (args != null) {
            posicion = args.getInt("posicion", 1);
            int imagenResId = args.getInt("imagen_res_id", -1);

            if (imagenResId != -1 && ivImagen != null) {
                ivImagen.setImageResource(imagenResId);
            }

            // Mostrar el número correspondiente
            if (tvNumero != null) {
                tvNumero.setText(posicion == 1 ? "1️⃣" : "2️⃣");
            }
        }

        return rootView;
    }

    public void setOpcion(Opcion opcion, OnOpcionClickListener listener) {
        this.opcion = opcion;
        this.listener = listener;

        if (tvNombre != null && opcion != null) {
            tvNombre.setText(opcion.getTexto());
            // NO mostramos el ELO
        }
    }

    public void setClickable(boolean clickable) {
        if (rootView != null) {
            rootView.setClickable(clickable);
            rootView.setEnabled(clickable);
            rootView.setAlpha(clickable ? 1.0f : 0.5f);
        }
    }

    @Override
    public void onClick(View v) {
        if (listener != null && opcion != null) {
            listener.onOpcionClick(opcion);
        }
    }
}