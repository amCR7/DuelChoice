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
    private Opcion opcion;
    private OnOpcionClickListener listener;
    private View rootView;

    public interface OnOpcionClickListener {
        void onOpcionClick(Opcion opcion);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_opcion, container, false);

        ivImagen = rootView.findViewById(R.id.iv_imagen);
        tvNombre = rootView.findViewById(R.id.tv_nombre);
        rootView.setOnClickListener(this);

        Bundle args = getArguments();
        if (args != null) {
            int imagenResId = args.getInt("imagen_res_id", -1);
            if (imagenResId != -1 && ivImagen != null) {
                ivImagen.setImageResource(imagenResId);
            }

            int color = args.getInt("color", -1);
            if (color != -1 && rootView != null) {
                rootView.setBackgroundColor(color);
            }
        }

        return rootView;
    }

    public void setOpcion(Opcion opcion, OnOpcionClickListener listener) {
        this.opcion = opcion;
        this.listener = listener;

        if (tvNombre != null && opcion != null) {
            tvNombre.setText(opcion.getTexto());
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
        v.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                    if (listener != null && opcion != null) {
                        listener.onOpcionClick(opcion);
                    }
                });
    }
}